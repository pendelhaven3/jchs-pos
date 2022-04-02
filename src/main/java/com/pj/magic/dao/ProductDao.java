package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductBySpecialCode;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.ProductSearchCriteria;

public interface ProductDao {

	List<Product> getAll();

	Product findByCode(String code);

	Product get(long id);
	
	void updateAvailableQuantities(Product product);
	
	void save(Product product);
	
	List<Product> search(ProductSearchCriteria criteria);
	
	List<Product> findAllByPricingScheme(PricingScheme pricingScheme);
	
	List<Product> findAllActiveBySupplier(Supplier supplier);

	Product findByIdAndPricingScheme(long id, PricingScheme pricingScheme);
	
	Product findByCodeAndPricingScheme(String code, PricingScheme pricingScheme);

	void updateMaximumStockLevel(List<Product> products);

	List<String> getAllActiveProductCodes();

	void updateActiveIndicator(String productCode, boolean active);

	void subtractAvailableQuantity(Product product, int quantity);

	void addAvailableQuantity(Product product, int quantity);

	void removeProduct2Id(Long id);

	List<ProductBySpecialCode> searchProductsBySpecialCode(String customCode, Supplier supplier);

	ProductBySpecialCode findProductBySpecialCode(String productCode, Supplier supplier);
	
}
