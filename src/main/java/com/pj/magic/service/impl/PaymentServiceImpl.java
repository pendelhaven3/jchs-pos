package com.pj.magic.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.BadStockReturnDao;
import com.pj.magic.dao.NoMoreStockAdjustmentDao;
import com.pj.magic.dao.PaymentAdjustmentDao;
import com.pj.magic.dao.PaymentCashPaymentDao;
import com.pj.magic.dao.PaymentCheckPaymentDao;
import com.pj.magic.dao.PaymentDao;
import com.pj.magic.dao.PaymentPaymentAdjustmentDao;
import com.pj.magic.dao.PaymentSalesInvoiceDao;
import com.pj.magic.dao.PaymentTerminalAssignmentDao;
import com.pj.magic.dao.SalesInvoiceDao;
import com.pj.magic.dao.SalesInvoiceItemDao;
import com.pj.magic.dao.SalesReturnDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.dao.UserDao;
import com.pj.magic.exception.AlreadyCancelledException;
import com.pj.magic.exception.AlreadyPaidException;
import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.AmountGivenLessThanRemainingAmountDueException;
import com.pj.magic.exception.UserNotAssignedToPaymentTerminalException;
import com.pj.magic.model.AdjustmentType;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.Customer;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.PaymentCashPayment;
import com.pj.magic.model.PaymentCheckPayment;
import com.pj.magic.model.PaymentPaymentAdjustment;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.User;
import com.pj.magic.model.search.PaymentCashPaymentSearchCriteria;
import com.pj.magic.model.search.PaymentCheckPaymentSearchCriteria;
import com.pj.magic.model.search.PaymentSalesInvoiceSearchCriteria;
import com.pj.magic.model.search.PaymentSearchCriteria;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
import com.pj.magic.model.search.SalesReturnSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PaymentService;
import com.pj.magic.service.SalesReturnService;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired private SalesInvoiceItemDao salesInvoiceItemDao;
	@Autowired private PaymentDao paymentDao;
	@Autowired private PaymentSalesInvoiceDao paymentSalesInvoiceDao;
	@Autowired private PaymentCheckPaymentDao paymentCheckPaymentDao;
	@Autowired private PaymentCashPaymentDao paymentCashPaymentDao;
	@Autowired private PaymentPaymentAdjustmentDao paymentPaymentAdjustmentDao;
	@Autowired private PaymentTerminalAssignmentDao paymentTerminalAssignmentDao;
	@Autowired private SalesReturnDao salesReturnDao;
	@Autowired private LoginService loginService;
	@Autowired private SalesReturnService salesReturnService;
	@Autowired private BadStockReturnDao badStockReturnDao;
	@Autowired private NoMoreStockAdjustmentDao noMoreStockAdjustmentDao;
	@Autowired private PaymentAdjustmentDao paymentAdjustmentDao;
	@Autowired private SalesInvoiceDao salesInvoiceDao;
	@Autowired private SystemDao systemDao;
	@Autowired private UserDao userDao;
	
	@Transactional
	@Override
	public void save(Payment payment) {
		boolean newPayment = (payment.getId() == null);
		if (newPayment) {
			payment.setEncoder(loginService.getLoggedInUser());
		}
		paymentDao.save(payment);
		if (!newPayment) {
			paymentSalesInvoiceDao.deleteAllByPayment(payment);
		}
	}

	@Override
	public Payment getPayment(long id) {
		Payment payment = paymentDao.get(id);
		loadPaymentDetails(payment);
		return payment;
	}
	
	private void loadPaymentDetails(Payment payment) {
		payment.setSalesInvoices(paymentSalesInvoiceDao.findAllByPayment(payment));
		for (PaymentSalesInvoice salesInvoice : payment.getSalesInvoices()) {
			salesInvoice.getSalesInvoice().setItems(
					salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice.getSalesInvoice()));
			if (payment.isNew()) {
				salesInvoice.setSalesReturns(
						findPostedSalesReturnsBySalesInvoice(salesInvoice.getSalesInvoice()));
			} else {
				salesInvoice.setSalesReturns(
						salesReturnService.findAllPaymentSalesReturns(payment, salesInvoice.getSalesInvoice()));
			}
		}
		payment.setCashPayments(paymentCashPaymentDao.findAllByPayment(payment));
		payment.setCheckPayments(paymentCheckPaymentDao.findAllByPayment(payment));
		payment.setAdjustments(paymentPaymentAdjustmentDao.findAllByPayment(payment));
	}

	private List<SalesReturn> findPostedSalesReturnsBySalesInvoice(SalesInvoice salesInvoice) {
		SalesReturnSearchCriteria criteria = new SalesReturnSearchCriteria();
		criteria.setSalesInvoice(salesInvoice);
		criteria.setPosted(true);
		criteria.setPaid(false);
		criteria.setCancelled(false);
		
		return salesReturnService.search(criteria);
	}

	@Override
	public List<Payment> getAllNewPayments() {
		PaymentSearchCriteria criteria = new PaymentSearchCriteria();
		criteria.setPosted(false);
		criteria.setCancelled(false);
		return searchPayments(criteria);
	}

	@Transactional
	@Override
	public void save(PaymentSalesInvoice paymentSalesInvoice) {
		paymentSalesInvoiceDao.save(paymentSalesInvoice);
	}

	@Override
	public List<PaymentSalesInvoice> findAllPaymentSalesInvoicesByPayment(Payment payment) {
		List<PaymentSalesInvoice> paymentSalesInvoices = paymentSalesInvoiceDao.findAllByPayment(payment);
		for (PaymentSalesInvoice salesInvoice : paymentSalesInvoices) {
			salesInvoice.getSalesInvoice().setItems(
					salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice.getSalesInvoice()));
		}
		return paymentSalesInvoices;
	}

	@Transactional
	@Override
	public void save(PaymentCheckPayment check) {
		paymentCheckPaymentDao.save(check);
	}

	@Transactional
	@Override
	public void delete(PaymentSalesInvoice paymentSalesInvoice) {
		paymentSalesInvoiceDao.delete(paymentSalesInvoice);
	}

	@Transactional
	@Override
	public void save(PaymentCashPayment cashPayment) {
		paymentCashPaymentDao.save(cashPayment);
	}

	@Transactional
	@Override
	public void delete(PaymentCheckPayment checkPayment) {
		paymentCheckPaymentDao.delete(checkPayment);
	}

	@Transactional
	@Override
	public void delete(PaymentCashPayment cashPayment) {
		paymentCashPaymentDao.delete(cashPayment);
	}

	@Transactional
	@Override
	public void delete(PaymentPaymentAdjustment adjustment) {
		paymentPaymentAdjustmentDao.delete(adjustment);
	}

	@Transactional
	@Override
	public void save(PaymentPaymentAdjustment adjustment) {
		paymentPaymentAdjustmentDao.save(adjustment);
	}

	@Transactional
	@Override
	public void post(Payment payment) {
		User user = loginService.getLoggedInUser();
		PaymentTerminalAssignment paymentTerminalAssignment = paymentTerminalAssignmentDao.findByUser(user);
		if (paymentTerminalAssignment == null) {
			throw new UserNotAssignedToPaymentTerminalException();
		}
		
		Date currentDateTime = systemDao.getCurrentDateTime();
		
		Payment updated = getPayment(payment.getId());
		updated.setPosted(true);
		updated.setPostDate(currentDateTime);
		updated.setPostedBy(user);
		updated.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
		paymentDao.save(updated);
		
		for (PaymentSalesInvoice salesInvoice : updated.getSalesInvoices()) {
			for (SalesReturn salesReturn : salesInvoice.getSalesReturns()) {
				salesReturnDao.savePaymentSalesReturn(updated, salesReturn);
				
				salesReturn.setPaid(true);
				salesReturn.setPaidDate(currentDateTime);
				salesReturn.setPaidBy(loginService.getLoggedInUser());
				salesReturn.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
				salesReturn.setPaymentNumber(updated.getPaymentNumber());
				salesReturnDao.save(salesReturn);
			}
		}
		
		for (PaymentPaymentAdjustment adjustment : updated.getAdjustments()) {
			if (adjustment.getReferenceNumber() == null) {
				continue;
			}
			
			long referenceNumber = Long.valueOf(adjustment.getReferenceNumber());
			switch (adjustment.getAdjustmentType().getCode()) {
			case AdjustmentType.SALES_RETURN_CODE:
				SalesReturn salesReturn = salesReturnDao.findBySalesReturnNumber(referenceNumber);
				if (salesReturn.isPaid()) {
					throw new RuntimeException("Sales Return " + salesReturn.getSalesReturnNumber() + " is already paid");
				}
				
				salesReturn.setPaid(true);
				salesReturn.setPaidDate(currentDateTime);
				salesReturn.setPaidBy(loginService.getLoggedInUser());
				salesReturn.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
				salesReturn.setPaymentNumber(updated.getPaymentNumber());
				salesReturnDao.save(salesReturn);
				break;
			case AdjustmentType.BAD_STOCK_RETURN_CODE:
				BadStockReturn badStockReturn = badStockReturnDao.findByBadStockReturnNumber(referenceNumber);
				if (badStockReturn.isPaid()) {
					throw new RuntimeException("Bad Stock Return " + badStockReturn.getBadStockReturnNumber() + " is already paid");
				}
				
				badStockReturn.setPaid(true);
				badStockReturn.setPaidDate(currentDateTime);
				badStockReturn.setPaidBy(loginService.getLoggedInUser());
				badStockReturn.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
				badStockReturn.setPaymentNumber(updated.getPaymentNumber());
				badStockReturnDao.save(badStockReturn);
				break;
			case AdjustmentType.NO_MORE_STOCK_ADJUSTMENT_CODE:
				NoMoreStockAdjustment noMoreStockAdjustment = noMoreStockAdjustmentDao.findByNoMoreStockAdjustmentNumber(referenceNumber);
				if (noMoreStockAdjustment.isPaid()) {
					throw new RuntimeException("No More Stock Adjustment " + 
						noMoreStockAdjustment.getNoMoreStockAdjustmentNumber() + " is already paid");
				}
				
				noMoreStockAdjustment.setPaid(true);
				noMoreStockAdjustment.setPaidDate(currentDateTime);
				noMoreStockAdjustment.setPaidBy(loginService.getLoggedInUser());
				noMoreStockAdjustment.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
				noMoreStockAdjustment.setPaymentNumber(updated.getPaymentNumber());
				noMoreStockAdjustmentDao.save(noMoreStockAdjustment);
				break;
			default:
				PaymentAdjustment paymentAdjustment = 
					paymentAdjustmentDao.findByPaymentAdjustmentNumber(referenceNumber);
				if (paymentAdjustment.isPaid()) {
					throw new RuntimeException("Payment Adjustment " + 
							paymentAdjustment.getPaymentAdjustmentNumber() + " is already paid");
				}
				
				paymentAdjustment.setPaid(true);
				paymentAdjustment.setPaidDate(currentDateTime);
				paymentAdjustment.setPaidBy(loginService.getLoggedInUser());
				paymentAdjustment.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
				paymentAdjustment.setPaymentNumber(updated.getPaymentNumber());
				paymentAdjustmentDao.save(paymentAdjustment);
				break;
			}
		}
	}

	@Override
	public List<Payment> searchPayments(PaymentSearchCriteria criteria) {
		return paymentDao.search(criteria);
	}

	@Override
	public List<PaymentSalesInvoice> searchPaymentSalesInvoices(
			PaymentSalesInvoiceSearchCriteria criteria) {
		List<PaymentSalesInvoice> paymentSalesInvoices = paymentSalesInvoiceDao.search(criteria);
		for (PaymentSalesInvoice salesInvoice : paymentSalesInvoices) {
			salesInvoice.getSalesInvoice().setItems(
					salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice.getSalesInvoice()));
		}
		return paymentSalesInvoices;
	}

	@Transactional
	@Override
	public void cancel(Payment payment) {
		payment.setCancelled(true);
		payment.setCancelDate(systemDao.getCurrentDateTime());
		payment.setCancelledBy(loginService.getLoggedInUser());
		paymentDao.save(payment);
	}

	@Override
	public List<PaymentCashPayment> searchPaymentCashPayments(
			PaymentCashPaymentSearchCriteria criteria) {
		return paymentCashPaymentDao.search(criteria);
	}

	@Override
	public List<PaymentCheckPayment> searchPaymentCheckPayments(
			PaymentCheckPaymentSearchCriteria criteria) {
		return paymentCheckPaymentDao.search(criteria);
	}

	@Override
	public List<PaymentSalesInvoice> findAllUnpaidSalesInvoices(Customer customer) {
		SalesInvoiceSearchCriteria criteria = new SalesInvoiceSearchCriteria();
		criteria.setCustomer(customer);
		criteria.setPaid(false);
		criteria.setOrderBy("a.TRANSACTION_DT, a.SALES_INVOICE_NO");
		
		List<SalesInvoice> salesInvoices = salesInvoiceDao.search(criteria);
		for (SalesInvoice salesInvoice : salesInvoices) {
			salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		}
		
		List<PaymentSalesInvoice> paymentSalesInvoices = new ArrayList<>();
		for (SalesInvoice salesInvoice : salesInvoices) {
			PaymentSalesInvoice paymentSalesInvoice = new PaymentSalesInvoice();
			paymentSalesInvoice.setSalesInvoice(salesInvoice);
			paymentSalesInvoice.setSalesReturns(findPostedSalesReturns(salesInvoice));
			paymentSalesInvoices.add(paymentSalesInvoice);
		}
		
		return paymentSalesInvoices;
	}

	private List<SalesReturn> findPostedSalesReturns(SalesInvoice salesInvoice) {
		SalesReturnSearchCriteria criteria = new SalesReturnSearchCriteria();
		criteria.setSalesInvoice(salesInvoice);
		criteria.setPosted(true);
		criteria.setCancelled(false);
		return salesReturnService.search(criteria);
	}

	@Transactional
	@Override
	public void addCashPaymentAndPost(Payment payment) {
		Payment updated = getPayment(payment.getId());
		
		if (updated.isPosted()) {
			throw new AlreadyPostedException();
		} else if (updated.isCancelled()) {
			throw new AlreadyCancelledException();
		}
		
		if (payment.getCashAmountGiven() != null && payment.getCashAmountGiven()
				.compareTo(updated.getTotalAmountDueMinusNonCashPaymentsAndAdjustments()) < 0) {
			throw new AmountGivenLessThanRemainingAmountDueException();
		}
		
		PaymentCashPayment cashPayment = new PaymentCashPayment();
		cashPayment.setParent(payment);
		cashPayment.setAmount(payment.getTotalAmountDueMinusNonCashPaymentsAndAdjustments());
		cashPayment.setReceivedDate(systemDao.getCurrentDateTime());
		cashPayment.setReceivedBy(loginService.getLoggedInUser());
		save(cashPayment);
		
		updated.setCashAmountGiven(payment.getCashAmountGiven());
		paymentDao.save(updated);
		
		post(updated);
	}

	@Transactional
	@Override
	public void markAsPaidByPayroll(List<Long> salesInvoiceNumbers) {
		List<SalesInvoice> salesInvoices = new ArrayList<>();
		for (Long salesInvoiceNumber : salesInvoiceNumbers) {
			SalesInvoice salesInvoice = salesInvoiceDao.findBySalesInvoiceNumber(salesInvoiceNumber);
			if (salesInvoice.isCancelled()) {
				throw new AlreadyCancelledException("Sales Invoice " + salesInvoiceNumber + " is already cancelled");
			}
			if (isSalesInvoiceInExistingPayment(salesInvoice)) {
				throw new AlreadyPaidException("Sales Invoice " + salesInvoiceNumber + " is already paid");
			}
			salesInvoices.add(salesInvoice);
		}
		
		Date now = systemDao.getCurrentDateTime();
		User payrollUser = userDao.findByUsername("PAYROLL");
		PaymentTerminal office = paymentTerminalAssignmentDao.findByUser(payrollUser).getPaymentTerminal();
		
		Payment payment = new Payment();
		payment.setCustomer(salesInvoices.get(0).getCustomer());
		payment.setCreateDate(now);
		payment.setEncoder(payrollUser);
		paymentDao.save(payment);
		
		for (SalesInvoice salesInvoice : salesInvoices) {
			PaymentSalesInvoice paymentSalesInvoice = new PaymentSalesInvoice();
			paymentSalesInvoice.setParent(payment);
			paymentSalesInvoice.setSalesInvoice(salesInvoice);
			paymentSalesInvoiceDao.save(paymentSalesInvoice);
		}
		
		payment = getPayment(payment.getId());
		
		PaymentCashPayment cashPayment = new PaymentCashPayment();
		cashPayment.setParent(payment);
		cashPayment.setAmount(payment.getTotalAmountDue());
		cashPayment.setReceivedDate(now);
		cashPayment.setReceivedBy(payrollUser);
		paymentCashPaymentDao.save(cashPayment);
		
		payment.setPosted(true);
		payment.setPostDate(now);
		payment.setPostedBy(payrollUser);
		payment.setPaymentTerminal(office);
		paymentDao.save(payment);
	}

	private boolean isSalesInvoiceInExistingPayment(SalesInvoice salesInvoice) {
		// TODO: implementation
		return false;
	}

	@Transactional
	@Override
	public void unpost(Payment payment) {
		Payment updated = getPayment(payment.getId());
		updated.setPosted(false);
		updated.setPostDate(null);
		updated.setPostedBy(null);
		updated.setPaymentTerminal(null);
		paymentDao.save(updated);
		
		for (PaymentSalesInvoice salesInvoice : updated.getSalesInvoices()) {
			salesReturnDao.deleteAllPaymentSalesReturnAllByPayment(updated);
			
			for (SalesReturn salesReturn : salesInvoice.getSalesReturns()) {
				salesReturn = salesReturnDao.get(salesReturn.getId());
				salesReturn.setPaid(false);
				salesReturn.setPaidDate(null);
				salesReturn.setPaidBy(null);
				salesReturn.setPaymentTerminal(null);
				salesReturn.setPaymentNumber(null);
				salesReturnDao.save(salesReturn);
			}
		}
		
		for (PaymentPaymentAdjustment adjustment : updated.getAdjustments()) {
			if (adjustment.getReferenceNumber() == null) {
				continue;
			}
			
			long referenceNumber = Long.valueOf(adjustment.getReferenceNumber());
			switch (adjustment.getAdjustmentType().getCode()) {
			case AdjustmentType.SALES_RETURN_CODE:
				SalesReturn salesReturn = salesReturnDao.findBySalesReturnNumber(referenceNumber);
				salesReturn.setPaid(false);
				salesReturn.setPaidDate(null);
				salesReturn.setPaidBy(null);
				salesReturn.setPaymentTerminal(null);
				salesReturn.setPaymentNumber(null);
				salesReturnDao.save(salesReturn);
				break;
			case AdjustmentType.BAD_STOCK_RETURN_CODE:
				BadStockReturn badStockReturn = badStockReturnDao.findByBadStockReturnNumber(referenceNumber);
				badStockReturn.setPaid(false);
				badStockReturn.setPaidDate(null);
				badStockReturn.setPaidBy(null);
				badStockReturn.setPaymentTerminal(null);
				badStockReturn.setPaymentNumber(null);
				badStockReturnDao.save(badStockReturn);
				break;
			case AdjustmentType.NO_MORE_STOCK_ADJUSTMENT_CODE:
				NoMoreStockAdjustment noMoreStockAdjustment = noMoreStockAdjustmentDao.findByNoMoreStockAdjustmentNumber(referenceNumber);
				noMoreStockAdjustment.setPaid(false);
				noMoreStockAdjustment.setPaidDate(null);
				noMoreStockAdjustment.setPaidBy(null);
				noMoreStockAdjustment.setPaymentTerminal(null);
				noMoreStockAdjustment.setPaymentNumber(null);
				noMoreStockAdjustmentDao.save(noMoreStockAdjustment);
				break;
			default:
				PaymentAdjustment paymentAdjustment = 
					paymentAdjustmentDao.findByPaymentAdjustmentNumber(referenceNumber);
				paymentAdjustment.setPaid(false);
				paymentAdjustment.setPaidDate(null);
				paymentAdjustment.setPaidBy(null);
				paymentAdjustment.setPaymentTerminal(null);
				paymentAdjustment.setPaymentNumber(null);
				paymentAdjustmentDao.save(paymentAdjustment);
				break;
			}
		}
	}

}