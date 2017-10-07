package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.PurchaseReturnBadStock;
import com.pj.magic.model.PurchaseReturnBadStockItem;
import com.pj.magic.model.search.PurchaseReturnBadStockSearchCriteria;

public interface PurchaseReturnBadStockService {

	void save(PurchaseReturnBadStock purchaseReturnBadStock);
	
	PurchaseReturnBadStock getPurchaseReturnBadStock(long id);

	List<PurchaseReturnBadStock> getAllNewPurchaseReturnBadStocks();

	void save(PurchaseReturnBadStockItem item);

	void delete(PurchaseReturnBadStockItem item);

	void post(PurchaseReturnBadStock purchaseReturnBadStock);

	PurchaseReturnBadStock findPurchaseReturnBadStockByPurchaseReturnBadStockNumber(
			long purchaseReturnBadStockNumber);

	List<PurchaseReturnBadStock> search(PurchaseReturnBadStockSearchCriteria criteria);
	
}