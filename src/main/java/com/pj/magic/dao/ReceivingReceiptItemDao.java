package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Product;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.Supplier;

public interface ReceivingReceiptItemDao {

	void save(ReceivingReceiptItem item);
	
	List<ReceivingReceiptItem> findAllByReceivingReceipt(ReceivingReceipt receivingReceipt);

	ReceivingReceiptItem findMostRecentBySupplierAndProduct(Supplier supplier, Product product);

}