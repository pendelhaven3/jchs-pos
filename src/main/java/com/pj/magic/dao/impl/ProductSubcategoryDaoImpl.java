package com.pj.magic.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ProductSubcategoryDao;
import com.pj.magic.model.ProductSubcategory;

@Repository
public class ProductSubcategoryDaoImpl implements ProductSubcategoryDao {

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

}
