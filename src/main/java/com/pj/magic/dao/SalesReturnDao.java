package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Payment;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.search.SalesReturnSearchCriteria;

public interface SalesReturnDao {

	void save(SalesReturn salesReturn);
	
	SalesReturn get(long id);

	List<SalesReturn> search(SalesReturnSearchCriteria criteria);
	
	void savePaymentSalesReturn(Payment payment, SalesReturn salesReturn);

	SalesReturn findBySalesReturnNumber(long salesReturnNumber);

	List<SalesReturn> findAllBySalesInvoice(SalesInvoice salesInvoice);

	void deleteAllPaymentSalesReturnAllByPayment(Payment payment);
	
}