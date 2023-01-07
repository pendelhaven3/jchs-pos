package com.pj.magic.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.Constants;
import com.pj.magic.dao.ReceivingReceiptDao;
import com.pj.magic.dao.ReceivingReceiptItemDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.exception.AlreadyCancelledException;
import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.model.Product2;
import com.pj.magic.model.ProductCanvassItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.ProductCanvassSearchCriteria;
import com.pj.magic.model.search.ReceivingReceiptSearchCriteria;
import com.pj.magic.repository.Product2Repository;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.ReceivingReceiptService;

@Service
public class ReceivingReceiptServiceImpl implements ReceivingReceiptService {

	@Autowired private ReceivingReceiptDao receivingReceiptDao;
	@Autowired private ReceivingReceiptItemDao receivingReceiptItemDao;
	@Autowired private SystemDao systemDao;
	@Autowired private LoginService loginService;
	@Autowired private Product2Repository product2Repository;
	
	@Transactional
	@Override
	public void save(ReceivingReceipt receivingReceipt) {
		boolean inserting = (receivingReceipt.getId() == null);
		receivingReceiptDao.save(receivingReceipt);
		if (inserting) {
			for (ReceivingReceiptItem item : receivingReceipt.getItems()) {
				receivingReceiptItemDao.save(item);
			}
		}
	}

	@Override
	public ReceivingReceipt getReceivingReceipt(long id) {
		ReceivingReceipt receivingReceipt = receivingReceiptDao.get(id);
		loadReceivingReceiptDetails(receivingReceipt);
		return receivingReceipt;
	}
	
	private void loadReceivingReceiptDetails(ReceivingReceipt receivingReceipt) {
		receivingReceipt.setItems(receivingReceiptItemDao.findAllByReceivingReceipt(receivingReceipt));
		for (ReceivingReceiptItem item : receivingReceipt.getItems()) {
			item.setProduct(product2Repository.get(item.getProduct().getId()));
		}
	}

	@Transactional
	@Override
	public void save(ReceivingReceiptItem item) {
		receivingReceiptItemDao.save(item);
	}

	@Override
	public List<ReceivingReceipt> getNewReceivingReceipts() {
		ReceivingReceiptSearchCriteria criteria = new ReceivingReceiptSearchCriteria();
		criteria.setPosted(false);
		criteria.setCancelled(false);
		
		return search(criteria);
	}

	@Transactional
	@Override
	public void post(ReceivingReceipt receivingReceipt) {
		ReceivingReceipt updated = getReceivingReceipt(receivingReceipt.getId());
		
		if (updated.isPosted()) {
			throw new AlreadyPostedException();
		}
		
		if (updated.isCancelled()) {
			throw new AlreadyCancelledException();
		}
		
		BigDecimal costMultipler = Constants.ONE;
		if (!updated.isVatInclusive()) {
			costMultipler = Constants.ONE.add(updated.getVatRate());
		}
		
		for (ReceivingReceiptItem item : updated.getItems()) {
			Product2 product = product2Repository.get(item.getProduct().getId());
			BigDecimal currentCost = product.getFinalCost(item.getUnit());
			
			try {
				System.out.println(item.getProduct().getDescription());
				product.setGrossCost(item.getUnit(), 
						item.getCost().multiply(costMultipler).setScale(2, RoundingMode.HALF_UP));
				product.setFinalCost(item.getUnit(), 
						item.getFinalCost().multiply(costMultipler).setScale(2, RoundingMode.HALF_UP));
				if (item.getProduct().getUnits().size() > 1) {
					product.autoCalculateCostsOfSmallerUnits(item.getUnit());
				}
			} catch (Exception e) {
				throw e;
			}
			product2Repository.updateCosts(product);
			
			product2Repository.addAvailableQuantity(product.getId(), item.getUnit(), item.getQuantity());
			
			item.setCurrentCost(currentCost);
			receivingReceiptItemDao.save(item);
		}
		
		updated.setPosted(true);
		updated.setPostDate(systemDao.getCurrentDateTime());
		updated.setPostedBy(loginService.getLoggedInUser());
		receivingReceiptDao.save(updated);
	}

	public void setReceivingReceiptItemDao(ReceivingReceiptItemDao receivingReceiptItemDao) {
		this.receivingReceiptItemDao = receivingReceiptItemDao;
	}

	public void setReceivingReceiptDao(ReceivingReceiptDao receivingReceiptDao) {
		this.receivingReceiptDao = receivingReceiptDao;
	}

	@Override
	public List<ReceivingReceipt> search(ReceivingReceiptSearchCriteria criteria) {
		List<ReceivingReceipt> receivingReceipts = receivingReceiptDao.search(criteria);
		for (ReceivingReceipt receivingReceipt : receivingReceipts) {
			receivingReceipt.setItems(receivingReceiptItemDao.findAllByReceivingReceipt(receivingReceipt));
		}
		return receivingReceipts;
	}

	@Transactional
	@Override
	public void cancel(ReceivingReceipt receivingReceipt) {
		ReceivingReceipt updated = receivingReceiptDao.get(receivingReceipt.getId());
		updated.setCancelled(true);
		updated.setCancelDate(systemDao.getCurrentDateTime());
		updated.setCancelledBy(loginService.getLoggedInUser());
		receivingReceiptDao.save(updated);
	}

	@Override
	public List<ReceivingReceipt> findAllReceivingReceiptsForPaymentBySupplier(Supplier supplier) {
		List<ReceivingReceipt> receivingReceipts = receivingReceiptDao.findAllForPaymentBySupplier(supplier);
		for (ReceivingReceipt receivingReceipt : receivingReceipts) {
			receivingReceipt.setItems(receivingReceiptItemDao.findAllByReceivingReceipt(receivingReceipt));
		}
		return receivingReceipts;
	}

	@Override
	public ReceivingReceipt findReceivingReceiptByReceivingReceiptNumber(long receivingReceiptNumber) {
		ReceivingReceipt receivingReceipt = 
				receivingReceiptDao.findByReceivingReceiptNumber(receivingReceiptNumber);
		return receivingReceipt;
	}

	@Override
	public ReceivingReceiptItem findMostRecentReceivingReceiptItem(Supplier supplier, Product2 product) {
		ReceivingReceiptItem item = receivingReceiptItemDao
				.findMostRecentBySupplierAndProduct(supplier, product);
		if (item == null) {
			return null;
		}
		
		item.setProduct(product2Repository.get(item.getProduct().getId()));
		item.setParent(receivingReceiptDao.get(item.getParent().getId()));
		
		BigDecimal costMultipler = Constants.ONE;
		if (!item.getParent().isVatInclusive()) {
			costMultipler = Constants.ONE.add(item.getParent().getVatRate());
		}
		
		item.getProduct().setFinalCost(item.getUnit(), 
				item.getFinalCost().multiply(costMultipler).setScale(2, RoundingMode.HALF_UP));
		if (item.getProduct().getUnits().size() > 1) {
			item.getProduct().autoCalculateCostsOfSmallerUnits(item.getUnit());
		}
		
		return item;
	}

	@Override
	public List<ProductCanvassItem> getProductCanvass(ProductCanvassSearchCriteria criteria) {
		return receivingReceiptDao.getProductCanvassItems(criteria);
	}

	@Transactional
	@Override
	public void setAllItemDiscounts(ReceivingReceipt receivingReceipt, 
			BigDecimal discount1, BigDecimal discount2, BigDecimal discount3) {
		for (ReceivingReceiptItem item : receivingReceipt.getItems()) {
			item.setDiscount1(discount1);
			item.setDiscount2(discount2);
			item.setDiscount3(discount3);
			receivingReceiptItemDao.save(item);
		}
	}
	
}