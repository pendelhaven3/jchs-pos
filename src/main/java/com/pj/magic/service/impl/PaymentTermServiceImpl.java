package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.PaymentTermDao;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.service.PaymentTermService;

@Service
public class PaymentTermServiceImpl implements PaymentTermService {

	@Autowired private PaymentTermDao paymentTermDao;
	
	@Transactional
	@Override
	public void save(PaymentTerm supplier) {
		paymentTermDao.save(supplier);
	}

	@Override
	public PaymentTerm getPaymentTerm(long id) {
		return paymentTermDao.get(id);
	}

	@Override
	public List<PaymentTerm> getAllPaymentTerms() {
		return paymentTermDao.getAll();
	}

}
