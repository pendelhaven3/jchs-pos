package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchaseReturnBadStock;
import com.pj.magic.model.search.PurchaseReturnBadStockSearchCriteria;

public interface PurchaseReturnBadStockDao {

	PurchaseReturnBadStock get(long id);

	void save(PurchaseReturnBadStock purchaseReturnBadStock);

	List<PurchaseReturnBadStock> search(PurchaseReturnBadStockSearchCriteria criteria);

	PurchaseReturnBadStock findByPurchaseReturnBadStockNumber(long purchaseReturnBadStockNumber);

}