package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.search.PaymentAdjustmentSearchCriteria;

public interface PaymentAdjustmentDao {

	PaymentAdjustment get(long id);

	void save(PaymentAdjustment paymentAdjustment);

	List<PaymentAdjustment> getAll();

	PaymentAdjustment findByPaymentAdjustmentNumber(long paymentAdjustmentNumber);

	List<PaymentAdjustment> search(PaymentAdjustmentSearchCriteria criteria);

}