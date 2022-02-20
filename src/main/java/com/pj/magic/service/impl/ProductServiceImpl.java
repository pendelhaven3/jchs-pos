package com.pj.magic.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.AdjustmentInItemDao;
import com.pj.magic.dao.AdjustmentOutItemDao;
import com.pj.magic.dao.AreaInventoryReportItemDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.ProductPriceDao;
import com.pj.magic.dao.ProductPriceHistoryDao;
import com.pj.magic.dao.PurchaseOrderItemDao;
import com.pj.magic.dao.SalesRequisitionItemDao;
import com.pj.magic.dao.StockQuantityConversionItemDao;
import com.pj.magic.dao.SupplierDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductPriceHistory;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.ProductSearchCriteria;
import com.pj.magic.repository.DailyProductStartingQuantityRepository;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.Product2Service;
import com.pj.magic.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);
    
	@Autowired private ProductDao productDao;
	@Autowired private ProductPriceDao productPriceDao;
	@Autowired private SupplierDao supplierDao;
	@Autowired private SalesRequisitionItemDao salesRequisitionItemDao;
	@Autowired private PurchaseOrderItemDao purchaseOrderItemDao;
	@Autowired private StockQuantityConversionItemDao stockQuantityConversionItemDao;
	@Autowired private AdjustmentInItemDao adjustmentInItemDao;
	@Autowired private AdjustmentOutItemDao adjustmentOutItemDao;
	@Autowired private AreaInventoryReportItemDao areaInventoryReportItemDao;
	@Autowired private ProductPriceHistoryDao productPriceHistoryDao;
	@Autowired private LoginService loginService;
	@Autowired private DailyProductStartingQuantityRepository dailyProductStartingQuantityRepository;
	@Autowired private SystemDao systemDao;
	@Autowired private Product2Service product2Service;
	
	@Override
	public List<Product> getAllProducts() {
		return productDao.getAll();
	}

	@Override
	public Product findProductByCode(String code) {
		return productDao.findByCode(code);
	}

	@Override
	public Product findProductByCodeAndPricingScheme(String code, PricingScheme pricingScheme) {
		return productDao.findByCodeAndPricingScheme(code, pricingScheme);
	}

	@Override
	public Product getProduct(long id) {
		return productDao.get(id);
	}

	@Transactional
	@Override
	public void save(Product product) {
	    productDao.save(product);
	}

	@Override
	public List<Product> getAllActiveProducts() {
		ProductSearchCriteria criteria = new ProductSearchCriteria();
		criteria.setActive(true);

		return productDao.search(criteria);
	}

	@Override
	public List<Supplier> getAvailableSuppliers(Product product) {
		return supplierDao.findAllNotHavingProduct(product);
	}

	@Transactional
	@Override
	public void saveUnitCostsAndPrices(Product product, PricingScheme pricingScheme) {
		Product productBeforeUpdate = productDao.findByIdAndPricingScheme(product.getId(), pricingScheme);
		
		productPriceDao.updateUnitPrices(product, pricingScheme);
		productDao.updateCosts(product);
		productPriceHistoryDao.save(createProductPriceHistory(product, pricingScheme, productBeforeUpdate));
	}

	private ProductPriceHistory createProductPriceHistory(Product product, PricingScheme pricingScheme,
			Product productBeforeUpdate) {
		ProductPriceHistory history = new ProductPriceHistory();
		history.setPricingScheme(pricingScheme);
		history.setProduct(product);
		history.setUpdatedBy(loginService.getLoggedInUser());
		history.setUnitPrices(product.getUnitPrices());
		history.setPreviousUnitPrices(productBeforeUpdate.getUnitPrices());
		return history;
	}

	@Override
	public List<Product> getAllActiveProductsBySupplier(Supplier supplier) {
		return productDao.findAllActiveBySupplier(supplier);
	}

	@Override
	public Product getProduct(long id, PricingScheme pricingScheme) {
		return productDao.findByIdAndPricingScheme(id, pricingScheme);
	}

	@Override
	public List<Product> getAllActiveProducts(PricingScheme pricingScheme) {
		ProductSearchCriteria criteria = new ProductSearchCriteria();
		criteria.setActive(true);
		criteria.setPricingScheme(pricingScheme);
		
		return productDao.search(criteria);
	}

	@Override
	public List<Product> searchProducts(ProductSearchCriteria criteria) {
		return productDao.search(criteria);
	}

	@Override
	public boolean canDeleteProduct(Product product) {
		return salesRequisitionItemDao.findFirstByProduct(product) == null &&
				purchaseOrderItemDao.findFirstByProduct(product) == null &&
				stockQuantityConversionItemDao.findFirstByProduct(product) == null &&
				adjustmentInItemDao.findFirstByProduct(product) == null &&
				adjustmentOutItemDao.findFirstByProduct(product) == null &&
				areaInventoryReportItemDao.findFirstByProduct(product) == null;
	}

	@Override
	public List<ProductPriceHistory> getProductPriceHistory(Product product, PricingScheme pricingScheme) {
		return productPriceHistoryDao.findAllByProductAndPricingScheme(product, pricingScheme);
	}

	@Transactional
	@Override
	public void updateMaximumStockLevel(List<Product> products) {
		productDao.updateMaximumStockLevel(products);
	}

	@Transactional
	@Override
	public boolean saveDailyProductStartingQuantities() {
		Date today = systemDao.getCurrentDateTime();
		if (dailyProductStartingQuantityRepository.getCountByDate(today) == 0) {
			dailyProductStartingQuantityRepository.saveQuantities(today);
			return true;
		}
		return false;
	}

	@Transactional
    @Override
    public void updateProduct(Product product) {
	    Product existing = productDao.findByCode(product.getCode());
	    if (existing == null) {
	    	product.setProduct2Id(product2Service.saveFromTrisys(product));
	        productDao.save(product);
	    } else {
	    	boolean shouldUpdate = !existing.areFieldsEqual(product) 
	    			|| existing.getProduct2Id() == null
	    			|| !existing.hasActiveUnit(product.getUnits().get(0));
	    		
			if (shouldUpdate) {
	            product.setId(existing.getId());
	            product.setActiveUnits(product.getUnits());
		    	product.setProduct2Id(product2Service.saveFromTrisys(product));
	            productDao.save(product);
			} else {
				Product product2 = product2Service.getProduct(existing.getProduct2Id());
				if (!product2.hasActiveUnit(product.getUnits().get(0))) {
					productDao.updateActiveIndicator(product.getCode(), true);
				}
			}
	    }
    }

	@Override
	public List<String> getAllActiveProductCodes() {
		return productDao.getAllActiveProductCodes();
	}

	@Override
	public void updateProductsAsInactive(List<String> activeProductCodes) {
		for (String productCode: activeProductCodes) {
        	LOGGER.info("Update product as inactive: {}", productCode);
			productDao.updateActiveIndicator(productCode, false);
		}
	}

	@Transactional(noRollbackFor = NotEnoughStocksException.class)
	@Override
	public void subtractAvailableQuantity(Product product, int quantity) {
		subtractAvailableQuantity(product, quantity, true);
	}

	@Transactional
	@Override
	public void addAvailableQuantity(Product product, int quantity) {
		productDao.addAvailableQuantity(product, quantity);
	}

	@Override
	public void subtractAvailableQuantity(Product product, int quantity, boolean validateAvailability) {
		if (validateAvailability) {
			Product fromDb = productDao.get(product.getId());
			if (fromDb.getAvailableQuantity() < quantity) {
				throw new NotEnoughStocksException(product.getCode());
			}
		}
		productDao.subtractAvailableQuantity(product, quantity);
	}

}