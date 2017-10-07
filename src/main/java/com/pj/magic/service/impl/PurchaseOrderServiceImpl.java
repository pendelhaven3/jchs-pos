package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.PurchaseOrderDao;
import com.pj.magic.dao.PurchaseOrderItemDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.exception.NoActualQuantityException;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchaseOrderItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.PurchaseOrderSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PurchaseOrderService;
import com.pj.magic.service.ReceivingReceiptService;
import com.pj.magic.service.SystemService;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

	@Autowired private PurchaseOrderDao purchaseOrderDao;
	@Autowired private PurchaseOrderItemDao purchaseOrderItemDao;
	@Autowired private ProductDao productDao;
	@Autowired private SystemDao systemDao;
	@Autowired private ReceivingReceiptService receivingReceiptService;
	@Autowired private LoginService loginService;
	@Autowired private SystemService systemService;
	
	@Transactional
	@Override
	public void save(PurchaseOrder purchaseOrder) {
		boolean inserting = (purchaseOrder.getId() == null);
		if (inserting) {
			purchaseOrder.setCreatedBy(loginService.getLoggedInUser());
		}
		purchaseOrderDao.save(purchaseOrder);
	}

	@Override
	public PurchaseOrder getPurchaseOrder(long id) {
		PurchaseOrder purchaseOrder = purchaseOrderDao.get(id);
		loadPurchaseOrderDetails(purchaseOrder);
		return purchaseOrder;
	}
	
	private void loadPurchaseOrderDetails(PurchaseOrder purchaseOrder) {
		purchaseOrder.setItems(purchaseOrderItemDao.findAllByPurchaseOrder(purchaseOrder));
		for (PurchaseOrderItem item : purchaseOrder.getItems()) {
			item.setProduct(productDao.get(item.getProduct().getId()));
		}
		purchaseOrder.setVatRate(systemService.getVatRate());
	}

	@Transactional
	@Override
	public void save(PurchaseOrderItem item) {
		purchaseOrderItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(PurchaseOrderItem item) {
		purchaseOrderItemDao.delete(item);
	}

	@Transactional
	@Override
	public void delete(PurchaseOrder purchaseOrder) {
		purchaseOrderItemDao.deleteAllByPurchaseOrder(purchaseOrder);
		purchaseOrderDao.delete(purchaseOrder);
	}

	@Transactional
	@Override
	public ReceivingReceipt post(PurchaseOrder purchaseOrder) throws NoActualQuantityException {
		PurchaseOrder updated = getPurchaseOrder(purchaseOrder.getId());
		for (PurchaseOrderItem item : updated.getItems()) {
			if (item.getActualQuantity() == null) {
				throw new NoActualQuantityException(item);
			}
		}
		
		updated.setPosted(true);
		updated.setPostDate(systemDao.getCurrentDateTime());
		purchaseOrderDao.save(updated);

		ReceivingReceipt receivingReceipt = updated.createReceivingReceipt();
		receivingReceipt.setReceivedDate(systemDao.getCurrentDateTime());
		receivingReceipt.setReceivedBy(loginService.getLoggedInUser());
		receivingReceiptService.save(receivingReceipt);
		return receivingReceipt;
	}

	@Override
	public List<PurchaseOrder> getAllNonPostedPurchaseOrders() {
		PurchaseOrderSearchCriteria criteria = new PurchaseOrderSearchCriteria();
		criteria.setPosted(false);
		
		return search(criteria);
	}

	@Transactional
	@Override
	public void markAsDelivered(PurchaseOrder purchaseOrder) {
		purchaseOrder.setDelivered(true);
		purchaseOrderDao.save(purchaseOrder);
		purchaseOrderItemDao.updateAllByPurchaseOrderAsOrdered(purchaseOrder);
	}

	@Override
	public List<PurchaseOrder> getAllPurchaseOrdersBySupplier(Supplier supplier) {
		return purchaseOrderDao.findAllBySupplier(supplier);
	}

	@Override
	public List<PurchaseOrder> search(PurchaseOrderSearchCriteria criteria) {
		return purchaseOrderDao.search(criteria);
	}

	@Override
	public PurchaseOrder newPurchaseOrder() {
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setVatRate(systemService.getVatRate());
		return purchaseOrder;
	}
	
}
