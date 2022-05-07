package com.pj.magic.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.AdjustmentTypeDao;
import com.pj.magic.model.AdjustmentType;

@Repository
public class AdjustmentTypeDaoImpl extends MagicDao implements AdjustmentTypeDao {

	@Override
	public void save(AdjustmentType type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<AdjustmentType> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdjustmentType get(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdjustmentType findByCode(String code) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
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
	*/

}