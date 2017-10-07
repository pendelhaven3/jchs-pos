package com.pj.magic.service;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.CreditCard;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.model.CreditCardStatementItem;
import com.pj.magic.model.CreditCardStatementPayment;

public interface CreditCardService {

	void save(CreditCard creditCard);
	
	CreditCard getCreditCard(long id);
	
	List<CreditCard> getAllCreditCards();
	
	List<CreditCardStatement> getAllCreditCardStatements();

	CreditCardStatement getCreditCardStatement(long id);

	void save(CreditCardStatement statement);

	void delete(CreditCardStatementItem item);

	void save(CreditCardStatementItem item);

	List<CreditCardStatement> findAllStatementsByCreditCard(CreditCard creditCard);

	CreditCardStatement findStatementByCreditCardAndStatementDate(CreditCard creditCard, Date statementDate);
	
	void save(CreditCardStatementPayment payment);
	
	List<CreditCardStatementPayment> findAllPaymentsByCreditCardStatement(CreditCardStatement statement);
	
	void delete(CreditCardStatementPayment payment);
	
	List<String> getAllCustomerNumbers();

	void delete(CreditCardStatement statement);
	
}