package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.PurchaseReturnDao;
import com.pj.magic.dao.PurchaseReturnItemDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.PurchaseReturn;
import com.pj.magic.model.PurchaseReturnItem;
import com.pj.magic.model.search.PurchaseReturnSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PurchaseReturnService;
import com.pj.magic.service.ReceivingReceiptService;

@Service
public class PurchaseReturnServiceImpl implements PurchaseReturnService {

	@Autowired private PurchaseReturnDao purchaseReturnDao;
	@Autowired private PurchaseReturnItemDao purchaseReturnItemDao;
	@Autowired private ReceivingReceiptService receivingReceiptService;
	@Autowired private ProductDao productDao;
	@Autowired private SystemDao systemDao;
	@Autowired private LoginService loginService;
	
	@Transactional
	@Override
	public void save(PurchaseReturn purchaseReturn) {
		boolean newPurchaseReturn = (purchaseReturn.getId() == null);
		purchaseReturnDao.save(purchaseReturn);
		if (!newPurchaseReturn) {
			purchaseReturnItemDao.deleteAllByPurchaseReturn(purchaseReturn);
		}
	}

	@Override
	public PurchaseReturn getPurchaseReturn(long id) {
		PurchaseReturn purchaseReturn = purchaseReturnDao.get(id);
		if (purchaseReturn != null) {
			purchaseReturn.setItems(purchaseReturnItemDao.findAllByPurchaseReturn(purchaseReturn));
			purchaseReturn.setReceivingReceipt(
					receivingReceiptService.getReceivingReceipt(purchaseReturn.getReceivingReceipt().getId()));
		}
		return purchaseReturn;
	}

	@Transactional
	@Override
	public void save(PurchaseReturnItem item) {
		purchaseReturnItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(PurchaseReturnItem item) {
		purchaseReturnItemDao.delete(item);
	}

	@Transactional
	@Override
	public void post(PurchaseReturn purchaseReturn) {
		PurchaseReturn updated = purchaseReturnDao.get(purchaseReturn.getId());
		updated.setItems(purchaseReturnItemDao.findAllByPurchaseReturn(purchaseReturn));
		
		for (PurchaseReturnItem item : updated.getItems()) {
			Product product = productDao.get(item.getReceivingReceiptItem().getProduct().getId());
			product.addUnitQuantity(item.getReceivingReceiptItem().getUnit(), -1 * item.getQuantity());
			productDao.updateAvailableQuantities(product);
		}
		updated.setPosted(true);
		updated.setPostDate(systemDao.getCurrentDateTime());
		updated.setPostedBy(loginService.getLoggedInUser());
		purchaseReturnDao.save(updated);
	}

	@Override
	public List<PurchaseReturn> search(PurchaseReturnSearchCriteria criteria) {
		List<PurchaseReturn> purchaseReturns = purchaseReturnDao.search(criteria);
		for (PurchaseReturn purchaseReturn : purchaseReturns) {
			purchaseReturn.setItems(purchaseReturnItemDao.findAllByPurchaseReturn(purchaseReturn));
		}
		return purchaseReturns;
 	}

//	@Override
//	public List<PurchaseReturn> findPostedPurchaseReturnsByPurchaseInvoice(ReceivingReceipt receivingReceipt) {
//		PurchaseReturnSearchCriteria criteria = new PurchaseReturnSearchCriteria();
//		criteria.setReceivingReceipt(receivingReceipt);
//		criteria.setPosted(true);
//		
//		return search(criteria);
//	}

//	@Override
//	public List<PurchaseReturn> findAllPaymentPurchaseReturns(Payment payment, PurchaseInvoice purchaseInvoice) {
//		PurchaseReturnSearchCriteria criteria = new PurchaseReturnSearchCriteria();
//		criteria.setPayment(payment);
//		criteria.setPurchaseInvoice(purchaseInvoice);
//		
//		return search(criteria);
//	}

	@Override
	public PurchaseReturn findPurchaseReturnByPurchaseReturnNumber(long purchaseReturnNumber) {
		PurchaseReturn purchaseReturn = purchaseReturnDao.findByPurchaseReturnNumber(purchaseReturnNumber);
		if (purchaseReturn != null) {
			purchaseReturn.setItems(purchaseReturnItemDao.findAllByPurchaseReturn(purchaseReturn));
			purchaseReturn.setReceivingReceipt(
					receivingReceiptService.getReceivingReceipt(purchaseReturn.getReceivingReceipt().getId()));
		}
		return purchaseReturn;
	}

	@Override
	public void markAsPaid(PurchaseReturn purchaseReturn) {
		PurchaseReturn updated = purchaseReturnDao.get(purchaseReturn.getId());
		updated.setPaid(true);
		updated.setPaidDate(systemDao.getCurrentDateTime());
		updated.setPaidBy(loginService.getLoggedInUser());
		purchaseReturnDao.save(updated);
	}

	@Override
	public List<PurchaseReturn> getUnpaidPurchaseReturns() {
		PurchaseReturnSearchCriteria criteria = new PurchaseReturnSearchCriteria();
		criteria.setPaid(false);
		
		List<PurchaseReturn> purchaseReturns = purchaseReturnDao.search(criteria);
		for (PurchaseReturn purchaseReturn : purchaseReturns) {
			purchaseReturn.setItems(purchaseReturnItemDao.findAllByPurchaseReturn(purchaseReturn));
		}
		return purchaseReturns;
	}

}