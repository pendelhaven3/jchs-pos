package com.pj.magic.service;

import java.math.BigDecimal;
import java.util.List;

import com.pj.magic.model.Product;
import com.pj.magic.model.ProductCanvassItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.ProductCanvassSearchCriteria;
import com.pj.magic.model.search.ReceivingReceiptSearchCriteria;

/**
 * 
 * @author PJ Miranda
 *
 */
public interface ReceivingReceiptService {

	void save(ReceivingReceipt receivingReceipt);
	
	void save(ReceivingReceiptItem receivingReceiptItem);
	
	ReceivingReceipt getReceivingReceipt(long id);

	List<ReceivingReceipt> getNewReceivingReceipts();
	
	void post(ReceivingReceipt receivingReceipt);

	List<ReceivingReceipt> search(ReceivingReceiptSearchCriteria criteria);
	
	void cancel(ReceivingReceipt receivingReceipt);

	List<ReceivingReceipt> findAllReceivingReceiptsForPaymentBySupplier(Supplier supplier);
	
	ReceivingReceipt findReceivingReceiptByReceivingReceiptNumber(long receivingReceiptNumber);
	
	ReceivingReceiptItem findMostRecentReceivingReceiptItem(Supplier supplier, Product product);

	List<ProductCanvassItem> getProductCanvass(ProductCanvassSearchCriteria criteria);

	void setAllItemDiscounts(ReceivingReceipt receivingReceipt, BigDecimal discount1, BigDecimal discount2,
			BigDecimal discount3);
	
}