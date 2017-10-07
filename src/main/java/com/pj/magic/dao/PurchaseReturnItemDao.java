package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchaseReturn;
import com.pj.magic.model.PurchaseReturnItem;

public interface PurchaseReturnItemDao {

	List<PurchaseReturnItem> findAllByPurchaseReturn(PurchaseReturn purchaseReturn);

	void save(PurchaseReturnItem item);

	void delete(PurchaseReturnItem item);

	void deleteAllByPurchaseReturn(PurchaseReturn purchaseReturn);
	
}