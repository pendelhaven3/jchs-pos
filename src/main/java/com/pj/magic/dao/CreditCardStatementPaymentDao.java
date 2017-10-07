package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.model.CreditCardStatementPayment;

public interface CreditCardStatementPaymentDao {

	void save(CreditCardStatementPayment payment);
	
	List<CreditCardStatementPayment> findAllByCreditCardStatement(CreditCardStatement statement);

	void delete(CreditCardStatementPayment payment);

	void deleteAllByCreditCardStatement(CreditCardStatement statement);
	
}
