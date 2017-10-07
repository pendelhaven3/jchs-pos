package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.search.PaymentSalesInvoiceSearchCriteria;

public interface PaymentSalesInvoiceDao {

	void save(PaymentSalesInvoice paymentSalesInvoice);

	List<PaymentSalesInvoice> findAllByPayment(Payment payment);

	void deleteAllByPayment(Payment payment);

	void delete(PaymentSalesInvoice paymentSalesInvoice);

	List<PaymentSalesInvoice> search(PaymentSalesInvoiceSearchCriteria criteria);
	
}
