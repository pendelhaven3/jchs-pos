package com.pj.magic.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ProductCategoryDao;
import com.pj.magic.model.ProductCategory;

@Repository
public class ProductCategoryDaoImpl implements ProductCategoryDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public void save(ProductCategory category) {
		if (category.getId() == null) {
			entityManager.persist(category);
		} else {
			entityManager.merge(category);
		}
	}

	@Override
	public ProductCategory get(long id) {
		ProductCategory category = entityManager.find(ProductCategory.class, id);
		category.getSubcategories(); // force load child elements
		return category;
	}

	@Override
	public List<ProductCategory> getAll() {
        return entityManager.createQuery("SELECT p FROM ProductCategory p ORDER BY p.name", ProductCategory.class).getResultList();
	}

}
