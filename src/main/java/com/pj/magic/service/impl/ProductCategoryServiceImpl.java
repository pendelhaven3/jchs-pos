package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.ProductCategoryDao;
import com.pj.magic.dao.ProductSubcategoryDao;
import com.pj.magic.model.ProductCategory;
import com.pj.magic.model.ProductSubcategory;
import com.pj.magic.service.ProductCategoryService;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

	@Autowired private ProductCategoryDao productCategoryDao;
	@Autowired private ProductSubcategoryDao productSubcategoryDao;
	
	@Transactional
	@Override
	public void save(ProductCategory supplier) {
		productCategoryDao.save(supplier);
	}

	@Override
	public ProductCategory getProductCategory(long id) {
		return productCategoryDao.get(id);
	}

	@Override
	public List<ProductCategory> getAllProductCategories() {
		return productCategoryDao.getAll();
	}

	@Transactional
	@Override
	public void save(ProductSubcategory subcategory) {
		productSubcategoryDao.save(subcategory);
	}

	@Transactional
	@Override
	public void delete(ProductSubcategory subcategory) {
		productSubcategoryDao.delete(subcategory);
	}

}
