package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.PurchaseReturnBadStockDao;
import com.pj.magic.dao.PurchaseReturnBadStockItemDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.model.PurchaseReturnBadStock;
import com.pj.magic.model.PurchaseReturnBadStockItem;
import com.pj.magic.model.search.PurchaseReturnBadStockSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PurchaseReturnBadStockService;

@Service
public class PurchaseReturnBadStockServiceImpl implements PurchaseReturnBadStockService {

	@Autowired private PurchaseReturnBadStockDao purchaseReturnBadStockDao;
	@Autowired private PurchaseReturnBadStockItemDao purchaseReturnBadStockItemDao;
	@Autowired private LoginService loginService;
	@Autowired private ProductDao productDao;
	@Autowired private SystemDao systemDao;
	
	@Transactional
	@Override
	public void save(PurchaseReturnBadStock purchaseReturnBadStock) {
		purchaseReturnBadStockDao.save(purchaseReturnBadStock);
	}

	@Override
	public PurchaseReturnBadStock getPurchaseReturnBadStock(long id) {
		PurchaseReturnBadStock purchaseReturnBadStock = purchaseReturnBadStockDao.get(id);
		if (purchaseReturnBadStock != null) {
			purchaseReturnBadStock.setItems(purchaseReturnBadStockItemDao.findAllByPurchaseReturnBadStock(purchaseReturnBadStock));
			for (PurchaseReturnBadStockItem item : purchaseReturnBadStock.getItems()) {
				item.setProduct(productDao.get(item.getProduct().getId()));
			}
		}
		return purchaseReturnBadStock;
	}

	@Override
	public List<PurchaseReturnBadStock> getAllNewPurchaseReturnBadStocks() {
		PurchaseReturnBadStockSearchCriteria criteria = new PurchaseReturnBadStockSearchCriteria();
		criteria.setPosted(false);
		
		return search(criteria);
	}

	@Override
	public List<PurchaseReturnBadStock> search(PurchaseReturnBadStockSearchCriteria criteria) {
		List<PurchaseReturnBadStock> purchaseReturnBadStocks = purchaseReturnBadStockDao.search(criteria);
		for (PurchaseReturnBadStock purchaseReturnBadStock : purchaseReturnBadStocks) {
			purchaseReturnBadStock.setItems(purchaseReturnBadStockItemDao.findAllByPurchaseReturnBadStock(purchaseReturnBadStock));
		}
		return purchaseReturnBadStocks;
	}

	@Transactional
	@Override
	public void save(PurchaseReturnBadStockItem item) {
		purchaseReturnBadStockItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(PurchaseReturnBadStockItem item) {
		purchaseReturnBadStockItemDao.delete(item);
	}

	@Transactional
	@Override
	public void post(PurchaseReturnBadStock purchaseReturnBadStock) {
		PurchaseReturnBadStock updated = getPurchaseReturnBadStock(purchaseReturnBadStock.getId());
		updated.setPosted(true);
		updated.setPostDate(systemDao.getCurrentDateTime());
		updated.setPostedBy(loginService.getLoggedInUser());
		purchaseReturnBadStockDao.save(updated);
	}

	@Override
	public PurchaseReturnBadStock findPurchaseReturnBadStockByPurchaseReturnBadStockNumber(
			long purchaseReturnBadStockNumber) {
		PurchaseReturnBadStock purchaseReturnBadStock = 
				purchaseReturnBadStockDao.findByPurchaseReturnBadStockNumber(purchaseReturnBadStockNumber);
		if (purchaseReturnBadStock != null) {
			purchaseReturnBadStock.setItems(purchaseReturnBadStockItemDao.findAllByPurchaseReturnBadStock(
					purchaseReturnBadStock));
		}
		return purchaseReturnBadStock;
	}

}