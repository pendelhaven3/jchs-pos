package com.pj.magic.dao.impl;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ProductSubcategoryDao;
import com.pj.magic.model.ProductSubcategory;

@Repository
public class ProductSubcategoryDaoImpl implements ProductSubcategoryDao {

	@Override
	public void save(ProductSubcategory subcategory) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ProductSubcategory get(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(ProductSubcategory subcategory) {
		// TODO Auto-generated method stub
		
	}

	/*
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public void save(ProductSubcategory subcategory) {
		if (subcategory.getId() == null) {
			entityManager.persist(subcategory);
		} else {
			entityManager.merge(subcategory);
		}
	}

	@Override
	public ProductSubcategory get(long id) {
		return entityManager.find(ProductSubcategory.class, id);
	}

	@Override
	public void delete(ProductSubcategory subcategory) {
		entityManager.remove(get(subcategory.getId()));
	}
	*/

}
