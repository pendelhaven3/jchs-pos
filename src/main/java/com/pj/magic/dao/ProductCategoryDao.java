package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.ProductCategory;

public interface ProductCategoryDao {

	void save(ProductCategory category);
	
	ProductCategory get(long id);
	
	List<ProductCategory> getAll();
	
}
