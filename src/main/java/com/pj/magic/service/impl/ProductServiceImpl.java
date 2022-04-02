package com.pj.magic.service.impl;

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
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.Product2;
import com.pj.magic.model.ProductBySpecialCode;
import com.pj.magic.model.ProductPriceHistory;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.ProductSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.Product2Service;
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
	public List<Supplier> getAvailableSuppliers(Product2 product) {
		return supplierDao.findAllNotHavingProduct(product);
	}

	@Transactional
	@Override
	public void saveUnitCostsAndPrices(Product product, PricingScheme pricingScheme) {
		Product productBeforeUpdate = productDao.findByIdAndPricingScheme(product.getId(), pricingScheme);
		
		productPriceDao.updateUnitPrices(product, pricingScheme);
//		product2Repository.updateCosts(product);
//		productPriceHistoryDao.save(createProductPriceHistory(product, pricingScheme, productBeforeUpdate));
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
    public void updateProduct(Product product) {
	    Product existing = productDao.findByCode(product.getCode());
	    if (existing == null) {
	    	product.setProduct2Id(product2Service.saveFromTrisys(product));
	        productDao.save(product);
	    } else {
	    	if (!existing.isActive()) {
	    		return;
	    	}
	    	
	    	boolean shouldUpdate = !existing.areFieldsEqual(product) 
	    			|| existing.getProduct2Id() == null
	    			|| !existing.hasActiveUnit(product.getUnits().get(0));
	    		
			if (shouldUpdate) {
	            product.setId(existing.getId());
	            product.setActiveUnits(product.getUnits());
	            product.setProduct2Id(existing.getProduct2Id());
		    	product.setProduct2Id(product2Service.saveFromTrisys(product));
	            productDao.save(product);
			} else {
				Product2 product2 = product2Service.getProduct(existing.getProduct2Id());
				if (!product2.hasActiveUnit(product.getUnits().get(0))) {
					productDao.updateActiveIndicator(product.getCode(), true);
				}
			}
	    }
    }

	@Override
	public void markAsActive(String productCode, Boolean active) {
		productDao.updateActiveIndicator(productCode, active);
	}

	@Override
	public void unlinkCodeFromProduct(Product product) {
		productDao.removeProduct2Id(product.getId());
	}

	@Override
	public List<ProductBySpecialCode> searchProductsBySpecialCode(String customCode, Supplier supplier) {
		return productDao.searchProductsBySpecialCode(customCode, supplier);
	}

	@Override
	public ProductBySpecialCode findProductBySpecialCode(String productCode, Supplier supplier) {
		return productDao.findProductBySpecialCode(productCode, supplier);
	}

}