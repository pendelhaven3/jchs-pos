package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Customer;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;

public interface SalesInvoiceDao {

	void save(SalesInvoice salesInvoice);
	
	SalesInvoice get(long id);

	List<SalesInvoice> search(SalesInvoiceSearchCriteria criteria);
	
	SalesInvoice findBySalesInvoiceNumber(long salesInvoiceNumber);

	List<SalesInvoice> findAllForPaymentByCustomer(Customer customer);

	SalesInvoice findMostRecentByCustomerAndProduct(Customer customer, Product product);

}