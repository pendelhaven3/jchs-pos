package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
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

	void updateCosts(Product product);

	Product findByIdAndPricingScheme(long id, PricingScheme pricingScheme);
	
	Product findByCodeAndPricingScheme(String code, PricingScheme pricingScheme);

	void delete(Product product);
	
	void updateMaximumStockLevel(List<Product> products);
	
}
