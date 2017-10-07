package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.Payment;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.NoMoreStockAdjustmentItem;
import com.pj.magic.model.search.NoMoreStockAdjustmentSearchCriteria;

public interface NoMoreStockAdjustmentService {

	List<NoMoreStockAdjustment> getNewNoMoreStockAdjustments();

	void save(NoMoreStockAdjustment noMoreStockAdjustment);

	NoMoreStockAdjustment getNoMoreStockAdjustment(long id);

	void save(NoMoreStockAdjustmentItem item);

	void delete(NoMoreStockAdjustmentItem item);
	
	void post(NoMoreStockAdjustment noMoreStockAdjustment);

	List<NoMoreStockAdjustment> findPostedNoMoreStockAdjustmentsBySalesInvoice(SalesInvoice salesInvoice);

	List<NoMoreStockAdjustment> findAllPaymentNoMoreStockAdjustments(Payment payment, SalesInvoice salesInvoice);

	void markAsPaid(NoMoreStockAdjustment noMoreStockAdjustment);

	NoMoreStockAdjustment findNoMoreStockAdjustmentByNoMoreStockAdjustmentNumber(long noMoreStockAdjustmentNumber);

	List<NoMoreStockAdjustment> getUnpaidNoMoreStockAdjustments();

	List<NoMoreStockAdjustment> search(NoMoreStockAdjustmentSearchCriteria criteria);
	
}