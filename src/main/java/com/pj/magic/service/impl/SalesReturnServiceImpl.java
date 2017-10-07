package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.PaymentTerminalAssignmentDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.SalesReturnDao;
import com.pj.magic.dao.SalesReturnItemDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.exception.NoItemException;
import com.pj.magic.exception.SalesReturnItemQuantityExceededException;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.SalesReturnItem;
import com.pj.magic.model.User;
import com.pj.magic.model.search.SalesReturnItemSearchCriteria;
import com.pj.magic.model.search.SalesReturnSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.service.SalesReturnService;

@Service
public class SalesReturnServiceImpl implements SalesReturnService {

	@Autowired private SalesReturnDao salesReturnDao;
	@Autowired private SalesReturnItemDao salesReturnItemDao;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private ProductDao productDao;
	@Autowired private SystemDao systemDao;
	@Autowired private LoginService loginService;
	@Autowired private PaymentTerminalAssignmentDao paymentTerminalAssignmentDao;
	
	@Transactional
	@Override
	public void save(SalesReturn salesReturn) {
		boolean newSalesReturn = (salesReturn.getId() == null);
		salesReturnDao.save(salesReturn);
		if (!newSalesReturn) {
			salesReturnItemDao.deleteAllBySalesReturn(salesReturn);
		}
	}

	@Override
	public SalesReturn getSalesReturn(long id) {
		SalesReturn salesReturn = salesReturnDao.get(id);
		if (salesReturn != null) {
			salesReturn.setItems(salesReturnItemDao.findAllBySalesReturn(salesReturn));
			salesReturn.setSalesInvoice(salesInvoiceService.get(salesReturn.getSalesInvoice().getId()));
		}
		return salesReturn;
	}

	@Transactional
	@Override
	public void save(SalesReturnItem item) {
		salesReturnItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(SalesReturnItem item) {
		salesReturnItemDao.delete(item);
	}

	@Transactional
	@Override
	public void post(SalesReturn salesReturn) {
		SalesReturn updated = salesReturnDao.get(salesReturn.getId());
		updated.setItems(salesReturnItemDao.findAllBySalesReturn(salesReturn));
		updated.setSalesInvoice(salesInvoiceService.get(updated.getSalesInvoice().getId()));
		
		if (updated.getItems().isEmpty()) {
			throw new NoItemException();
		}
		
		for (SalesReturnItem item : updated.getItems()) {
			int totalQuantity = findTotalQuantityFromAllSalesReturnsOfSalesInvoice(item);
			if (totalQuantity > item.getSalesInvoiceItem().getQuantity()) {
				throw new SalesReturnItemQuantityExceededException(item);
			}
			
			Product product = productDao.get(item.getSalesInvoiceItem().getProduct().getId());
			product.addUnitQuantity(item.getSalesInvoiceItem().getUnit(), item.getQuantity());
			productDao.updateAvailableQuantities(product);
		}
		updated.setPosted(true);
		updated.setPostDate(systemDao.getCurrentDateTime());
		updated.setPostedBy(loginService.getLoggedInUser());
		salesReturnDao.save(updated);
	}

	private int findTotalQuantityFromAllSalesReturnsOfSalesInvoice(SalesReturnItem item) {
		SalesReturnItemSearchCriteria criteria = new SalesReturnItemSearchCriteria();
		criteria.setProduct(item.getSalesInvoiceItem().getProduct());
		criteria.setUnit(item.getSalesInvoiceItem().getUnit());
		criteria.setSalesInvoice(item.getParent().getSalesInvoice());
		
		int total = 0;
		for (SalesReturnItem salesReturnItem : salesReturnItemDao.search(criteria)) {
			total += salesReturnItem.getQuantity();
		}
		return total;
	}

	@Override
	public List<SalesReturn> search(SalesReturnSearchCriteria criteria) {
		List<SalesReturn> salesReturns = salesReturnDao.search(criteria);
		for (SalesReturn salesReturn : salesReturns) {
			salesReturn.setItems(salesReturnItemDao.findAllBySalesReturn(salesReturn));
		}
		return salesReturns;
 	}

	@Override
	public List<SalesReturn> findPostedSalesReturnsBySalesInvoice(SalesInvoice salesInvoice) {
		SalesReturnSearchCriteria criteria = new SalesReturnSearchCriteria();
		criteria.setSalesInvoice(salesInvoice);
		criteria.setPosted(true);
		criteria.setCancelled(false);
		
		return search(criteria);
	}

	@Override
	public List<SalesReturn> findAllPaymentSalesReturns(Payment payment, SalesInvoice salesInvoice) {
		SalesReturnSearchCriteria criteria = new SalesReturnSearchCriteria();
		criteria.setPayment(payment);
		criteria.setSalesInvoice(salesInvoice);
		
		return search(criteria);
	}

	@Transactional
	@Override
	public void markAsPaid(SalesReturn salesReturn) {
		User user = loginService.getLoggedInUser();
		PaymentTerminalAssignment paymentTerminalAssignment = paymentTerminalAssignmentDao.findByUser(user);
		if (paymentTerminalAssignment == null) {
			throw new RuntimeException("User " + user.getUsername() + " is not assigned to payment terminal");
		}
		
		SalesReturn updated = salesReturnDao.get(salesReturn.getId());
		updated.setPaid(true);
		updated.setPaidDate(systemDao.getCurrentDateTime());
		updated.setPaidBy(loginService.getLoggedInUser());
		updated.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
		salesReturnDao.save(updated);
	}

	@Override
	public SalesReturn findSalesReturnBySalesReturnNumber(long salesReturnNumber) {
		SalesReturn salesReturn = salesReturnDao.findBySalesReturnNumber(salesReturnNumber);
		if (salesReturn != null) {
			salesReturn.setItems(salesReturnItemDao.findAllBySalesReturn(salesReturn));
			salesReturn.setSalesInvoice(salesInvoiceService.get(salesReturn.getSalesInvoice().getId()));
		}
		return salesReturn;
	}

	@Override
	public List<SalesReturn> getUnpaidSalesReturns() {
		SalesReturnSearchCriteria criteria = new SalesReturnSearchCriteria();
		criteria.setPaid(false);
		criteria.setCancelled(false);
		
		List<SalesReturn> salesReturns = salesReturnDao.search(criteria);
		for (SalesReturn salesReturn : salesReturns) {
			salesReturn.setItems(salesReturnItemDao.findAllBySalesReturn(salesReturn));
		}
		return salesReturns;
	}

}