package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.ProductCategory;
import com.pj.magic.model.ProductSubcategory;

public interface ProductCategoryService {

	void save(ProductCategory category);
	
	ProductCategory getProductCategory(long id);
	
	List<ProductCategory> getAllProductCategories();
	
	void save(ProductSubcategory subcategory);

	void delete(ProductSubcategory subcategory);
	
}
