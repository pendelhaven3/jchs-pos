package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.ProductCanvassItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.ProductCanvassSearchCriteria;
import com.pj.magic.model.search.ReceivingReceiptSearchCriteria;

/**
 * 
 * @author PJ Miranda
 *
 */
public interface ReceivingReceiptDao {

	ReceivingReceipt get(long id);

	void save(ReceivingReceipt receivingReceipt);

	List<ReceivingReceipt> getAll();

	List<ProductCanvassItem> getProductCanvassItems(ProductCanvassSearchCriteria criteria);

	List<ReceivingReceipt> search(ReceivingReceiptSearchCriteria criteria);

	List<ReceivingReceipt> findAllForPaymentBySupplier(Supplier supplier);

	ReceivingReceipt findByReceivingReceiptNumber(long receivingReceiptNumber);

}