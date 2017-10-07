package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.NoMoreStockAdjustmentDao;
import com.pj.magic.dao.NoMoreStockAdjustmentItemDao;
import com.pj.magic.dao.PaymentTerminalAssignmentDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.exception.AlreadyPaidException;
import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.NoMoreStockAdjustmentItemQuantityExceededException;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.NoMoreStockAdjustmentItem;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.User;
import com.pj.magic.model.search.NoMoreStockAdjustmentItemSearchCriteria;
import com.pj.magic.model.search.NoMoreStockAdjustmentSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.NoMoreStockAdjustmentService;
import com.pj.magic.service.SalesInvoiceService;

@Service
public class NoMoreStockAdjustmentServiceImpl implements NoMoreStockAdjustmentService {

	@Autowired private NoMoreStockAdjustmentDao noMoreStockAdjustmentDao;
	@Autowired private NoMoreStockAdjustmentItemDao noMoreStockAdjustmentItemDao;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private LoginService loginService;
	@Autowired private PaymentTerminalAssignmentDao paymentTerminalAssignmentDao;
	@Autowired private SystemDao systemDao;
	
	@Override
	public List<NoMoreStockAdjustment> getNewNoMoreStockAdjustments() {
		NoMoreStockAdjustmentSearchCriteria criteria = new NoMoreStockAdjustmentSearchCriteria();
		criteria.setPosted(false);
		
		List<NoMoreStockAdjustment> noMoreStockAdjustments = noMoreStockAdjustmentDao.search(criteria);
		for (NoMoreStockAdjustment noMoreStockAdjustment : noMoreStockAdjustments) {
			noMoreStockAdjustment.setItems(noMoreStockAdjustmentItemDao.findAllByNoMoreStockAdjustment(noMoreStockAdjustment));
		}
		return noMoreStockAdjustments;
	}

	@Transactional
	@Override
	public void save(NoMoreStockAdjustment noMoreStockAdjustment) {
		if (!noMoreStockAdjustment.isNew()) {
			NoMoreStockAdjustment existing = noMoreStockAdjustmentDao.get(noMoreStockAdjustment.getId());
			if (!existing.getSalesInvoice().getSalesInvoiceNumber()
					.equals(noMoreStockAdjustment.getSalesInvoice().getSalesInvoiceNumber())) {
				noMoreStockAdjustmentItemDao.deleteAllByNoMoreStockAdjustment(noMoreStockAdjustment);
			}
		}
		noMoreStockAdjustmentDao.save(noMoreStockAdjustment);
	}

	@Override
	public NoMoreStockAdjustment getNoMoreStockAdjustment(long id) {
		NoMoreStockAdjustment noMoreStockAdjustment = noMoreStockAdjustmentDao.get(id);
		if (noMoreStockAdjustment != null) {
			noMoreStockAdjustment.setItems(noMoreStockAdjustmentItemDao.findAllByNoMoreStockAdjustment(noMoreStockAdjustment));
			noMoreStockAdjustment.setSalesInvoice(salesInvoiceService.get(noMoreStockAdjustment.getSalesInvoice().getId()));
		}
		return noMoreStockAdjustment;
	}

	@Transactional
	@Override
	public void save(NoMoreStockAdjustmentItem item) {
		noMoreStockAdjustmentItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(NoMoreStockAdjustmentItem item) {
		noMoreStockAdjustmentItemDao.delete(item);
	}

	@Transactional
	@Override
	public void post(NoMoreStockAdjustment noMoreStockAdjustment) {
		NoMoreStockAdjustment updated = noMoreStockAdjustmentDao.get(noMoreStockAdjustment.getId());
		
		if (updated.isPosted()) {
			throw new AlreadyPostedException("No More Stock Adjustment already posted. NMS No.: " + 
					noMoreStockAdjustment.getNoMoreStockAdjustmentNumber());
		}
		
		updated.setItems(noMoreStockAdjustmentItemDao.findAllByNoMoreStockAdjustment(updated));
		updated.setSalesInvoice(salesInvoiceService.get(updated.getSalesInvoice().getId()));
		
		for (NoMoreStockAdjustmentItem item : updated.getItems()) {
			int totalQuantity = findTotalQuantityFromAllNoMoreStockAdjustmentsOfSalesInvoice(item);
			if (totalQuantity > item.getSalesInvoiceItem().getQuantity()) {
				throw new NoMoreStockAdjustmentItemQuantityExceededException(item);
			}
		}
		
		updated.setPosted(true);
		updated.setPostDate(systemDao.getCurrentDateTime());
		updated.setPostedBy(loginService.getLoggedInUser());
		noMoreStockAdjustmentDao.save(updated);
	}

	private int findTotalQuantityFromAllNoMoreStockAdjustmentsOfSalesInvoice(NoMoreStockAdjustmentItem item) {
		NoMoreStockAdjustmentItemSearchCriteria criteria = new NoMoreStockAdjustmentItemSearchCriteria();
		criteria.setProduct(item.getSalesInvoiceItem().getProduct());
		criteria.setUnit(item.getSalesInvoiceItem().getUnit());
		criteria.setSalesInvoice(item.getParent().getSalesInvoice());
		
		int total = 0;
		for (NoMoreStockAdjustmentItem noMoreStockAdjustmentItem : 
				noMoreStockAdjustmentItemDao.search(criteria)) {
			total += noMoreStockAdjustmentItem.getQuantity();
		}
		return total;
	}

	@Override
	public List<NoMoreStockAdjustment> search(NoMoreStockAdjustmentSearchCriteria criteria) {
		List<NoMoreStockAdjustment> noMoreStockAdjustments = noMoreStockAdjustmentDao.search(criteria);
		for (NoMoreStockAdjustment noMoreStockAdjustment : noMoreStockAdjustments) {
			noMoreStockAdjustment.setItems(noMoreStockAdjustmentItemDao.findAllByNoMoreStockAdjustment(noMoreStockAdjustment));
		}
		return noMoreStockAdjustments;
 	}

	@Override
	public List<NoMoreStockAdjustment> findPostedNoMoreStockAdjustmentsBySalesInvoice(SalesInvoice salesInvoice) {
		NoMoreStockAdjustmentSearchCriteria criteria = new NoMoreStockAdjustmentSearchCriteria();
		criteria.setSalesInvoice(salesInvoice);
		criteria.setPosted(true);
		
		return search(criteria);
	}

	@Override
	public List<NoMoreStockAdjustment> findAllPaymentNoMoreStockAdjustments(Payment payment, SalesInvoice salesInvoice) {
		NoMoreStockAdjustmentSearchCriteria criteria = new NoMoreStockAdjustmentSearchCriteria();
		criteria.setPayment(payment);
		criteria.setSalesInvoice(salesInvoice);
		
		return search(criteria);
	}

	@Transactional
	@Override
	public void markAsPaid(NoMoreStockAdjustment noMoreStockAdjustment) {
		NoMoreStockAdjustment updated = noMoreStockAdjustmentDao.get(noMoreStockAdjustment.getId());
		if (updated.isPaid()) {
			throw new AlreadyPaidException("No More Stock Adjustment already paid. NMS No.: " + 
					noMoreStockAdjustment.getNoMoreStockAdjustmentNumber());
		}
		
		User user = loginService.getLoggedInUser();
		PaymentTerminalAssignment paymentTerminalAssignment = paymentTerminalAssignmentDao.findByUser(user);
		if (paymentTerminalAssignment == null) {
			throw new RuntimeException("User " + user.getUsername() + " is not assigned to payment terminal");
		}
		
		updated.setPaid(true);
		updated.setPaidDate(systemDao.getCurrentDateTime());
		updated.setPaidBy(loginService.getLoggedInUser());
		updated.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
		noMoreStockAdjustmentDao.save(updated);
	}

	@Override
	public NoMoreStockAdjustment findNoMoreStockAdjustmentByNoMoreStockAdjustmentNumber(long noMoreStockAdjustmentNumber) {
		NoMoreStockAdjustment noMoreStockAdjustment = noMoreStockAdjustmentDao.findByNoMoreStockAdjustmentNumber(noMoreStockAdjustmentNumber);
		if (noMoreStockAdjustment != null) {
			noMoreStockAdjustment.setItems(noMoreStockAdjustmentItemDao.findAllByNoMoreStockAdjustment(noMoreStockAdjustment));
			noMoreStockAdjustment.setSalesInvoice(salesInvoiceService.get(noMoreStockAdjustment.getSalesInvoice().getId()));
		}
		return noMoreStockAdjustment;
	}

	@Override
	public List<NoMoreStockAdjustment> getUnpaidNoMoreStockAdjustments() {
		NoMoreStockAdjustmentSearchCriteria criteria = new NoMoreStockAdjustmentSearchCriteria();
		criteria.setPaid(false);
		
		List<NoMoreStockAdjustment> noMoreStockAdjustments = noMoreStockAdjustmentDao.search(criteria);
		for (NoMoreStockAdjustment noMoreStockAdjustment : noMoreStockAdjustments) {
			noMoreStockAdjustment.setItems(noMoreStockAdjustmentItemDao.findAllByNoMoreStockAdjustment(noMoreStockAdjustment));
		}
		return noMoreStockAdjustments;
	}

}