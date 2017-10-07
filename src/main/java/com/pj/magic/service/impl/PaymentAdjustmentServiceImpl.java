package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.PaymentAdjustmentDao;
import com.pj.magic.dao.PaymentDao;
import com.pj.magic.dao.PaymentTerminalAssignmentDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.exception.PaymentAdjustmentAlreadyUsedException;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.User;
import com.pj.magic.model.search.PaymentAdjustmentSearchCriteria;
import com.pj.magic.model.search.PaymentSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PaymentAdjustmentService;

@Service
public class PaymentAdjustmentServiceImpl implements PaymentAdjustmentService {

	@Autowired private PaymentAdjustmentDao paymentAdjustmentDao;
	@Autowired private LoginService loginService;
	@Autowired private PaymentTerminalAssignmentDao paymentTerminalAssignmentDao;
	@Autowired private SystemDao systemDao;
	@Autowired private PaymentDao paymentDao;
	
	@Transactional
	@Override
	public void save(PaymentAdjustment paymentAdjustment) {
		paymentAdjustmentDao.save(paymentAdjustment);
	}

	@Override
	public PaymentAdjustment getPaymentAdjustment(long id) {
		return paymentAdjustmentDao.get(id);
	}

	@Override
	public List<PaymentAdjustment> getAllPaymentAdjustments() {
		return paymentAdjustmentDao.getAll();
	}

	@Transactional
	@Override
	public void post(PaymentAdjustment paymentAdjustment) {
		PaymentAdjustment updated = paymentAdjustmentDao.get(paymentAdjustment.getId());
		updated.setPosted(true);
		updated.setPostDate(systemDao.getCurrentDateTime());
		updated.setPostedBy(loginService.getLoggedInUser());
		paymentAdjustmentDao.save(updated);
	}

	@Transactional
	@Override
	public void markAsPaid(PaymentAdjustment paymentAdjustment) {
		User user = loginService.getLoggedInUser();
		PaymentTerminalAssignment paymentTerminalAssignment = paymentTerminalAssignmentDao.findByUser(user);
		if (paymentTerminalAssignment == null) {
			throw new RuntimeException("User " + user.getUsername() + " is not assigned to payment terminal");
		}
		
		PaymentAdjustment updated = paymentAdjustmentDao.get(paymentAdjustment.getId());
		updated.setPaid(true);
		updated.setPaidDate(systemDao.getCurrentDateTime());
		updated.setPaidBy(loginService.getLoggedInUser());
		updated.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
		paymentAdjustmentDao.save(updated);
	}

	@Override
	public PaymentAdjustment findPaymentAdjustmentByPaymentAdjustmentNumber(long paymentAdjustmentNumber) {
		return paymentAdjustmentDao.findByPaymentAdjustmentNumber(paymentAdjustmentNumber);
	}

	@Override
	public List<PaymentAdjustment> search(PaymentAdjustmentSearchCriteria criteria) {
		return paymentAdjustmentDao.search(criteria);
	}

	@Override
	public List<PaymentAdjustment> getUnpaidPaymentAdjustments() {
		PaymentAdjustmentSearchCriteria criteria = new PaymentAdjustmentSearchCriteria();
		criteria.setPaid(false);
		
		return search(criteria);
	}

	@Transactional
	@Override
	public void unpost(PaymentAdjustment paymentAdjustment) throws PaymentAdjustmentAlreadyUsedException {
		PaymentSearchCriteria criteria = new PaymentSearchCriteria();
		criteria.setPaymentAdjustmentNumber(paymentAdjustment.getPaymentAdjustmentNumber());
		
		List<Payment> payments = paymentDao.search(criteria);
		if (!payments.isEmpty()) {
			throw new PaymentAdjustmentAlreadyUsedException(paymentAdjustment, payments.get(0));
		}
		
		PaymentAdjustment updated = paymentAdjustmentDao.get(paymentAdjustment.getId());
		updated.setPosted(false);
		updated.setPostDate(null);
		updated.setPostedBy(null);
		paymentAdjustmentDao.save(updated);
	}

}