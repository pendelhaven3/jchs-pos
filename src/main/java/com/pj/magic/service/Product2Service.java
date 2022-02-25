package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.Product;
import com.pj.magic.model.Product2;
import com.pj.magic.model.Supplier;

public interface Product2Service {

	Long saveFromTrisys(Product product);

	Product2 getProduct(Long id);

	void save(Product2 product);

	void deleteProductSupplier(Product2 product, Supplier supplier);

	void addProductSupplier(Product2 product, Supplier supplier);

	List<Supplier> getProductSuppliers(Product2 product);

}
