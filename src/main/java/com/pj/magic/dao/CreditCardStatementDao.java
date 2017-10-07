package com.pj.magic.dao;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.CreditCard;
import com.pj.magic.model.CreditCardStatement;

public interface CreditCardStatementDao {

	List<CreditCardStatement> getAll();

	CreditCardStatement get(long id);

	void save(CreditCardStatement statement);

	List<CreditCardStatement> findAllByCreditCard(CreditCard creditCard);

	CreditCardStatement findByCreditCardAndStatementDate(CreditCard creditCard, Date statementDate);

	void delete(CreditCardStatement statement);
	
}
