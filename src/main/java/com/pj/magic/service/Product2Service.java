package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.Product;
import com.pj.magic.model.Product2;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.Product2SearchCriteria;

public interface Product2Service {

	Long saveFromTrisys(Product product);

	Product2 getProduct(Long id);

	void save(Product2 product);

	void deleteProductSupplier(Product2 product, Supplier supplier);

	void addProductSupplier(Product2 product, Supplier supplier);

	List<Supplier> getProductSuppliers(Product2 product);

	void subtractAvailableQuantity(Long id, String unit, int quantity);

	void addAvailableQuantity(Long id, String unit, int quantity);

	List<Product2> getAllActiveProducts();

	List<Product2> searchProducts(Product2SearchCriteria criteria);

	void updateCosts(Product2 product);

}
