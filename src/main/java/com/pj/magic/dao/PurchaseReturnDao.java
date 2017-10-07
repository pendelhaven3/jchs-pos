package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchaseReturn;
import com.pj.magic.model.search.PurchaseReturnSearchCriteria;

public interface PurchaseReturnDao {

	void save(PurchaseReturn purchaseReturn);
	
	PurchaseReturn get(long id);

	List<PurchaseReturn> search(PurchaseReturnSearchCriteria criteria);
	
	PurchaseReturn findByPurchaseReturnNumber(long purchaseReturnNumber);
	
}