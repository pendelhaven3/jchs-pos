package com.pj.magic.dao;

import com.pj.magic.model.ProductSubcategory;

public interface ProductSubcategoryDao {

	void save(ProductSubcategory subcategory);
	
	ProductSubcategory get(long id);

	void delete(ProductSubcategory subcategory);
	
}
