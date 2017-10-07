package com.pj.magic.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.pj.magic.dao.CreditCardDao;
import com.pj.magic.dao.CreditCardStatementDao;
import com.pj.magic.dao.CreditCardStatementItemDao;
import com.pj.magic.dao.CreditCardStatementPaymentDao;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.model.CreditCardStatementItem;
import com.pj.magic.model.CreditCardStatementPayment;
import com.pj.magic.service.CreditCardService;

@Service
public class CreditCardServiceImpl implements CreditCardService {

	@Autowired private CreditCardDao creditCardDao;
	@Autowired private CreditCardStatementDao creditCardStatementDao;
	@Autowired private CreditCardStatementItemDao creditCardStatementItemDao;
	@Autowired private CreditCardStatementPaymentDao creditCardStatementPaymentDao;
	
	@Transactional
	@Override
	public void save(CreditCard creditCard) {
		creditCardDao.save(creditCard);
	}

	@Override
	public CreditCard getCreditCard(long id) {
		return creditCardDao.get(id);
	}

	@Override
	public List<CreditCard> getAllCreditCards() {
		return creditCardDao.getAll();
	}

	@Override
	public List<CreditCardStatement> getAllCreditCardStatements() {
		List<CreditCardStatement> statements = creditCardStatementDao.getAll();
		for (CreditCardStatement statement : statements) {
			statement.setItems(creditCardStatementItemDao.findAllByCreditCardStatement(statement));
		}
		return statements;
	}

	@Override
	public CreditCardStatement getCreditCardStatement(long id) {
		CreditCardStatement statement = creditCardStatementDao.get(id);
		statement.setItems(creditCardStatementItemDao.findAllByCreditCardStatement(statement));
		statement.setPayments(creditCardStatementPaymentDao.findAllByCreditCardStatement(statement));
		return statement;
	}

	@Transactional
	@Override
	public void save(CreditCardStatement statement) {
		boolean isNew = statement.isNew();
		creditCardStatementDao.save(statement);
		if (isNew) {
			for (CreditCardStatementItem item : statement.getItems()) {
				creditCardStatementItemDao.save(item);
			}
		}
	}

	@Transactional
	@Override
	public void delete(CreditCardStatementItem item) {
		creditCardStatementItemDao.delete(item);
	}

	@Transactional
	@Override
	public void save(CreditCardStatementItem item) {
		creditCardStatementItemDao.save(item);
	}

	@Override
	public List<CreditCardStatement> findAllStatementsByCreditCard(CreditCard creditCard) {
		List<CreditCardStatement> statements = creditCardStatementDao.findAllByCreditCard(creditCard);
		for (CreditCardStatement statement : statements) {
			statement.setItems(creditCardStatementItemDao.findAllByCreditCardStatement(statement));
		}
		return statements;
	}

	@Override
	public CreditCardStatement findStatementByCreditCardAndStatementDate(
			CreditCard creditCard, Date statementDate) {
		return creditCardStatementDao.findByCreditCardAndStatementDate(creditCard, statementDate);
	}

	@Transactional
	@Override
	public void save(CreditCardStatementPayment payment) {
		creditCardStatementPaymentDao.save(payment);
	}

	@Override
	public List<CreditCardStatementPayment> findAllPaymentsByCreditCardStatement(CreditCardStatement statement) {
		return creditCardStatementPaymentDao.findAllByCreditCardStatement(statement);
	}

	@Transactional
	@Override
	public void delete(CreditCardStatementPayment payment) {
		creditCardStatementPaymentDao.delete(payment);
	}

	@Override
	public List<String> getAllCustomerNumbers() {
		Collection<String> customerNumbers = 
				Collections2.transform(creditCardDao.getAll(), new Function<CreditCard, String>() {

			@Override
			public String apply(CreditCard input) {
				return input.getCustomerNumber();
			}
		});
		
		return new ArrayList<String>(new HashSet<>(customerNumbers));
	}

	@Transactional
	@Override
	public void delete(CreditCardStatement statement) {
		creditCardStatementPaymentDao.deleteAllByCreditCardStatement(statement);
		creditCardStatementItemDao.deleteAllByCreditCardStatement(statement);
		creditCardStatementDao.delete(statement);
	}

}