package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.PaymentTerminalAssignmentDao;
import com.pj.magic.dao.PaymentTerminalDao;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.User;
import com.pj.magic.service.PaymentTerminalService;

@Service
public class PaymentTerminalServiceImpl implements PaymentTerminalService {

	@Autowired private PaymentTerminalAssignmentDao paymentTerminalAssignmentDao;
	@Autowired private PaymentTerminalDao paymentTerminalDao;
	
	@Override
	public List<PaymentTerminalAssignment> getAllPaymentTerminalAssignments() {
		return paymentTerminalAssignmentDao.getAll();
	}

	@Override
	public List<PaymentTerminal> getAllPaymentTerminals() {
		return paymentTerminalDao.getAll();
	}

	@Transactional
	@Override
	public void save(PaymentTerminalAssignment paymentTerminalAssignment) {
		paymentTerminalAssignmentDao.save(paymentTerminalAssignment);
	}

	@Transactional
	@Override
	public void delete(PaymentTerminalAssignment paymentTerminalAssignment) {
		paymentTerminalAssignmentDao.delete(paymentTerminalAssignment);
	}

	@Override
	public PaymentTerminalAssignment findPaymentTerminalAssignment(User user) {
		return paymentTerminalAssignmentDao.findByUser(user);
	}

}
