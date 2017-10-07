package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentBankTransfer;
import com.pj.magic.model.search.PurchasePaymentBankTransferSearchCriteria;

public interface PurchasePaymentBankTransferDao {

	void save(PurchasePaymentBankTransfer bankTransfer);

	List<PurchasePaymentBankTransfer> findAllByPurchasePayment(PurchasePayment purchasePayment);

	void delete(PurchasePaymentBankTransfer bankTransfer);

	List<PurchasePaymentBankTransfer> search(PurchasePaymentBankTransferSearchCriteria criteria);

}