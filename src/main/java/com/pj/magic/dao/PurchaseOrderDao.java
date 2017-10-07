package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.PurchaseOrderSearchCriteria;

public interface PurchaseOrderDao {

	PurchaseOrder get(long id);

	void save(PurchaseOrder purchaseOrder);

	void delete(PurchaseOrder purchaseOrder);

	List<PurchaseOrder> search(PurchaseOrderSearchCriteria criteria);
	
	List<PurchaseOrder> findAllBySupplier(Supplier supplier);
	
}
