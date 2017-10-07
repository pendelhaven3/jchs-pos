package com.pj.magic.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.AdjustmentTypeDao;
import com.pj.magic.model.AdjustmentType;

@Repository
public class AdjustmentTypeDaoImpl extends MagicDao implements AdjustmentTypeDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public void save(AdjustmentType type) {
		if (type.getId() == null) {
			entityManager.persist(type);
		} else {
			entityManager.merge(type);
		}
	}

	@Override
	public List<AdjustmentType> getAll() {
        return entityManager.createQuery("SELECT a FROM AdjustmentType a order by a.code", AdjustmentType.class)
        		.getResultList();
	}
	
	@Override
	public AdjustmentType get(long id) {
		return entityManager.find(AdjustmentType.class, id);
	}

	@Override
	public AdjustmentType findByCode(String code) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<AdjustmentType> criteria = builder.createQuery(AdjustmentType.class);
		Root<AdjustmentType> adjustmentType = criteria.from(AdjustmentType.class);
		criteria.where(adjustmentType.get("code").in(code));
		
		try {
			return entityManager.createQuery(criteria).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}