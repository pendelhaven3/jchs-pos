package com.pj.magic.service;

import java.util.List;

import com.pj.magic.exception.SalesRequisitionPostException;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.model.SalesRequisitionSeparateItemsList;
import com.pj.magic.model.User;

public interface SalesRequisitionService {

	void save(SalesRequisition salesRequisition);
	
	SalesRequisition getSalesRequisition(long id);

	void save(SalesRequisitionItem item);

	void delete(SalesRequisitionItem item);

	void delete(SalesRequisition currentlySelectedSalesRequisition);

	SalesInvoice post (SalesRequisition salesRequisition) throws SalesRequisitionPostException;
	
    SalesInvoice post (SalesRequisition salesRequisition, User postedBy) throws SalesRequisitionPostException;
    
	List<SalesRequisition> getAllNonPostedSalesRequisitions();

	SalesRequisition separatePerCaseItems(SalesRequisition salesRequisition);

	SalesRequisitionSeparateItemsList getSalesRequisitionSeparateItemsList();

	void addSalesRequisitionSeparateItem(Product product);

	void removeSalesRequisitionSeparateItem(Product product);
	
}
