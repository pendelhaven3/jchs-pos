package com.pj.magic.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.SystemDao;
import com.pj.magic.exception.InvalidProductCodeException;
import com.pj.magic.model.Product;
import com.pj.magic.model.Product2;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchaseOrderItem;
import com.pj.magic.model.ReceiveDelivery;
import com.pj.magic.model.ReceiveDeliveryItem;
import com.pj.magic.repository.ReceiveDeliveryItemRepository;
import com.pj.magic.repository.ReceiveDeliveryRepository;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.Product2Service;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.PurchaseOrderService;
import com.pj.magic.service.ReceiveDeliveryService;

@Service
public class ReceiveDeliveryServiceImpl implements ReceiveDeliveryService {

	@Autowired private ReceiveDeliveryRepository receiveDeliveryRepository;
	@Autowired private ReceiveDeliveryItemRepository receiveDeliveryItemRepository;
	@Autowired private SystemDao systemDao;
	@Autowired private LoginService loginService;
	@Autowired private ProductService productService;
	@Autowired private Product2Service product2Service;
	@Autowired private PurchaseOrderService purchaseOrderService;
	
	@Override
	public List<ReceiveDelivery> getAllUnpostedReceiveDeliveries() {
		return receiveDeliveryRepository.findAllByPosted(false);
	}

	@Transactional
	@Override
	public void save(ReceiveDelivery receiveDelivery) {
		if (receiveDelivery.getId() == null) {
			receiveDelivery.setReceiveDate(systemDao.getCurrentDateTime());
			receiveDelivery.setReceivedBy(loginService.getLoggedInUser());
		}
		receiveDeliveryRepository.save(receiveDelivery);
	}

	@Override
	public ReceiveDelivery getDeliveryService(Long id) {
		ReceiveDelivery receiveDelivery = receiveDeliveryRepository.get(id);
		receiveDelivery.setItems(receiveDeliveryItemRepository.findAllByReceiveDelivery(receiveDelivery));
		return receiveDelivery;
	}

	@Override
	public void save(ReceiveDeliveryItem item) {
		receiveDeliveryItemRepository.save(item);
	}

	@Override
	public void delete(ReceiveDeliveryItem item) {
		receiveDeliveryItemRepository.delete(item);
	}

	@Override
	public PurchaseOrder post(ReceiveDelivery receiveDelivery) {
		receiveDelivery = getDeliveryService(receiveDelivery.getId());
		
		for (ReceiveDeliveryItem item : receiveDelivery.getItems()) {
			Product product = productService.findProductByCode(item.getCode());
			if (product == null) {
				throw new InvalidProductCodeException(item.getCode());
			} else {
				item.setProduct(product);
			}
		}
		
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setSupplier(receiveDelivery.getSupplier());
		purchaseOrder.setVatInclusive(receiveDelivery.getSupplier().isVatInclusive());
		purchaseOrderService.save(purchaseOrder);
		purchaseOrderService.markAsDelivered(purchaseOrder);
		
		for (ReceiveDeliveryItem item : receiveDelivery.getItems()) {
			PurchaseOrderItem purchaseOrderItem = new PurchaseOrderItem();
			purchaseOrderItem.setParent(purchaseOrder);
			purchaseOrderItem.setProduct(new Product2(item.getProduct().getProduct2Id()));
			purchaseOrderItem.setUnit(item.getUnit()); 
			purchaseOrderItem.setQuantity(item.getQuantity());
			purchaseOrderItem.setActualQuantity(item.getQuantity());
			
			Product2 product2 = product2Service.getProduct(item.getProduct().getProduct2Id());
			BigDecimal originalCost = product2.getGrossCost(item.getUnit());
			if (!purchaseOrder.isVatInclusive()) {
				originalCost = originalCost.divide(purchaseOrder.getVatMultiplier(), 2,
						RoundingMode.HALF_UP);
			}
			purchaseOrderItem.setCost(originalCost);
			
			purchaseOrderService.save(purchaseOrderItem);
		}
		
		receiveDelivery.setPosted(true);
		receiveDelivery.setPostDate(systemDao.getCurrentDateTime());
		receiveDelivery.setPostedBy(loginService.getLoggedInUser());
		receiveDeliveryRepository.save(receiveDelivery);
		
		return purchaseOrder;
	}

	@Transactional
	@Override
	public void delete(ReceiveDelivery receiveDelivery) {
		receiveDeliveryItemRepository.deleteAllByReceiveDelivery(receiveDelivery);
		receiveDeliveryRepository.delete(receiveDelivery);
	}

}
