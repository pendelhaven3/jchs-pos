package com.pj.magic.service.impl;

import java.util.Date;
import java.util.List;

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
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductPriceHistory;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.ProductSearchCriteria;
import com.pj.magic.repository.DailyProductStartingQuantityRepository;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

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

	@Transactional
	@Override
	public void addProductSupplier(Product product, Supplier supplier) {
		supplierDao.saveSupplierProduct(supplier, product);
	}
	
	@Override
	public List<Supplier> getProductSuppliers(Product product) {
		return supplierDao.findAllByProduct(product);
	}

	@Override
	public List<Supplier> getAvailableSuppliers(Product product) {
		return supplierDao.findAllNotHavingProduct(product);
	}

	@Transactional
	@Override
	public void deleteProductSupplier(Product product, Supplier supplier) {
		supplierDao.deleteSupplierProduct(supplier, product);
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

	@Transactional
	@Override
	public void deleteProduct(Product product) {
		supplierDao.deleteAllByProduct(product);
		productPriceDao.deleteAllByProduct(product);
		productDao.delete(product);
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
	        productDao.save(product);
	    } else if (!existing.areFieldsEqual(product)) {
            product.setId(existing.getId());
            product.setMinimumStockLevel(existing.getMinimumStockLevel());
            product.setMaximumStockLevel(existing.getMaximumStockLevel());
            productDao.save(product);
	    }
    }

}