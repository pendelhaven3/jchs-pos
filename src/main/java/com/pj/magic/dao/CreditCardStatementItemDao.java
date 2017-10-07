package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.model.CreditCardStatementItem;

public interface CreditCardStatementItemDao {

	List<CreditCardStatementItem> findAllByCreditCardStatement(CreditCardStatement statement);

	void save(CreditCardStatementItem item);

	void delete(CreditCardStatementItem item);

	void deleteAllByCreditCardStatement(CreditCardStatement statement);
	
}