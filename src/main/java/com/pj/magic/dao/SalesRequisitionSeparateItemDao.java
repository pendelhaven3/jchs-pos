package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Product;

public interface SalesRequisitionSeparateItemDao {

	List<Product> getAll();
	
	void add(Product product);
	
	void remove(Product product);
	
}
