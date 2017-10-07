package com.pj.magic.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.CreditCardDao;
import com.pj.magic.model.CreditCard;

@Repository
public class CreditCardDaoImpl implements CreditCardDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public void save(CreditCard creditCard) {
		if (creditCard.getId() == null) {
			entityManager.persist(creditCard);
		} else {
			entityManager.merge(creditCard);
		}
	}

	@Override
	public CreditCard get(long id) {
		return entityManager.find(CreditCard.class, id);
	}

	@Override
	public List<CreditCard> getAll() {
        return entityManager.createQuery("SELECT c FROM CreditCard c order by c.user, c.bank, c.cardNumber", 
        		CreditCard.class).getResultList();
	}

}