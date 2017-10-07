package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Product;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchaseOrderItem;

public interface PurchaseOrderItemDao {

	void save(PurchaseOrderItem item);
	
	List<PurchaseOrderItem> findAllByPurchaseOrder(PurchaseOrder purchaseOrder);

	void delete(PurchaseOrderItem item);

	void deleteAllByPurchaseOrder(PurchaseOrder purchaseOrder);
	
	void updateAllByPurchaseOrderAsOrdered(PurchaseOrder purchaseOrder);

	PurchaseOrderItem findFirstByProduct(Product product);
	
}
