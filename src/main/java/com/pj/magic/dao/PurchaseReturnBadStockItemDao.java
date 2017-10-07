package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchaseReturnBadStock;
import com.pj.magic.model.PurchaseReturnBadStockItem;

public interface PurchaseReturnBadStockItemDao {

	void save(PurchaseReturnBadStockItem item);
	
	List<PurchaseReturnBadStockItem> findAllByPurchaseReturnBadStock(PurchaseReturnBadStock purchaseReturnBadStock);

	void delete(PurchaseReturnBadStockItem item);

}