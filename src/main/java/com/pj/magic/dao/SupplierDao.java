package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Product2;
import com.pj.magic.model.Supplier;

public interface SupplierDao {

	void save(Supplier supplier);
	
	Supplier get(long id);
	
	List<Supplier> getAll();

	List<Supplier> findAllByProduct(Product2 product);

	void saveSupplierProduct(Supplier supplier, Product2 product);
	
	List<Supplier> findAllNotHavingProduct(Product2 product);

	void deleteSupplierProduct(Supplier supplier, Product2 product);

	Supplier findByCode(String code);

	void delete(Supplier supplier);
	
	void removeAllProductsFromSupplier(Supplier supplier);

	void deleteAllByProduct(Product2 product);
	
}
