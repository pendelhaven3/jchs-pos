package com.pj.magic.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PurchasePaymentAdjustmentTypeDao;
import com.pj.magic.model.PurchasePaymentAdjustmentType;

@Repository
public class PurchasePaymentAdjustmentTypeDaoImpl extends MagicDao implements PurchasePaymentAdjustmentTypeDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public void save(PurchasePaymentAdjustmentType type) {
		if (type.getId() == null) {
			entityManager.persist(type);
		} else {
			entityManager.merge(type);
		}
	}

	@Override
	public List<PurchasePaymentAdjustmentType> getAll() {
        return entityManager.createQuery("SELECT a FROM PurchasePaymentAdjustmentType a order by a.code", 
        		PurchasePaymentAdjustmentType.class).getResultList();
	}
	
	@Override
	public PurchasePaymentAdjustmentType get(long id) {
		return entityManager.find(PurchasePaymentAdjustmentType.class, id);
	}

	@Override
	public PurchasePaymentAdjustmentType findByCode(String code) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PurchasePaymentAdjustmentType> criteria = 
				builder.createQuery(PurchasePaymentAdjustmentType.class);
		Root<PurchasePaymentAdjustmentType> adjustmentType = criteria.from(PurchasePaymentAdjustmentType.class);
		criteria.where(adjustmentType.get("code").in(code));
		
		try {
			return entityManager.createQuery(criteria).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}