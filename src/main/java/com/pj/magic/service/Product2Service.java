package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.Product;
import com.pj.magic.model.Supplier;

public interface Product2Service {

	Long saveFromTrisys(Product product);

	Product getProduct(Long id);

	void save(Product product);

	void deleteProductSupplier(Product product, Supplier supplier);

	void addProductSupplier(Product product, Supplier supplier);

	List<Supplier> getProductSuppliers(Product product);
	
}
