package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Product;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.SalesRequisitionItem;

public interface SalesRequisitionItemDao {

	void save(SalesRequisitionItem item);
	
	List<SalesRequisitionItem> findAllBySalesRequisition(SalesRequisition salesRequisition);

	void delete(SalesRequisitionItem item);

	void deleteAllBySalesRequisition(SalesRequisition salesRequisition);

	SalesRequisitionItem findFirstByProduct(Product product);
	
}
