package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;

public interface SalesInvoiceItemDao {

	void save(SalesInvoiceItem item);

	List<SalesInvoiceItem> findAllBySalesInvoice(SalesInvoice salesInvoice);
	
}
