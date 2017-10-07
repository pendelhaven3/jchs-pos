package com.pj.magic.service;

import java.math.BigDecimal;
import java.util.List;

import com.pj.magic.model.Customer;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;

public interface SalesInvoiceService {

	void save(SalesInvoice salesInvoice);

	SalesInvoice get(long id);
	
	SalesRequisition createSalesRequisitionFromSalesInvoice(SalesInvoice salesInvoice);
	
	void mark(SalesInvoice salesInvoice);
	
	void cancel(SalesInvoice salesInvoice);

	List<SalesInvoice> getNewSalesInvoices();

	void markSalesInvoices(List<SalesInvoice> salesInvoices);

	void save(SalesInvoiceItem item);

	List<SalesInvoice> search(SalesInvoiceSearchCriteria criteria);

	List<SalesInvoice> getAllNewSalesInvoices();

	SalesInvoice findBySalesInvoiceNumber(long salesInvoiceNumber);
	
	List<SalesInvoice> findAllSalesInvoicesForPaymentByCustomer(Customer customer);

	SalesInvoice getMostRecentSalesInvoice(Customer customer, Product product);

	void setAllItemDiscounts(SalesInvoice salesInvoice, BigDecimal discount1, BigDecimal discount2,
			BigDecimal discount3);

	void markAsPrinted(SalesInvoice salesInvoice);

}