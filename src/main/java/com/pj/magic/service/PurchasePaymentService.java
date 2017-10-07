package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentBankTransfer;
import com.pj.magic.model.PurchasePaymentCashPayment;
import com.pj.magic.model.PurchasePaymentCheckPayment;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.model.PurchasePaymentPaymentAdjustment;
import com.pj.magic.model.PurchasePaymentReceivingReceipt;
import com.pj.magic.model.search.PurchasePaymentBankTransferSearchCriteria;
import com.pj.magic.model.search.PurchasePaymentCashPaymentSearchCriteria;
import com.pj.magic.model.search.PurchasePaymentCheckPaymentSearchCriteria;
import com.pj.magic.model.search.PurchasePaymentCreditCardPaymentSearchCriteria;
import com.pj.magic.model.search.PurchasePaymentSearchCriteria;

public interface PurchasePaymentService {

	void save(PurchasePayment purchasePayment);

	PurchasePayment getPurchasePayment(Long id);

	void post(PurchasePayment purchasePayment);

	List<PurchasePayment> getAllNewPurchasePayments();

	void save(PurchasePaymentReceivingReceipt paymentReceivingReceipt);
	
	List<PurchasePaymentReceivingReceipt> findAllPaymentReceivingReceiptsByPurchasePayment(
			PurchasePayment purchasePayment);

	void save(PurchasePaymentCheckPayment checkPayment);

	void delete(PurchasePaymentReceivingReceipt paymentReceivingReceipt);

	void save(PurchasePaymentCashPayment cashPayment);

	void delete(PurchasePaymentCheckPayment checkPayment);

	void delete(PurchasePaymentCashPayment cashPayment);
	
	void save(PurchasePaymentCreditCardPayment creditCardPayment);

	void delete(PurchasePaymentCreditCardPayment creditCardPayment);

	void delete(PurchasePaymentPaymentAdjustment adjustment);

	void save(PurchasePaymentPaymentAdjustment adjustment);

	List<PurchasePayment> searchPurchasePayments(PurchasePaymentSearchCriteria criteria);

	void save(PurchasePaymentBankTransfer bankTransfer);

	void delete(PurchasePaymentBankTransfer bankTransfer);

	List<PurchasePaymentBankTransfer> searchBankTransfers(PurchasePaymentBankTransferSearchCriteria criteria);

	List<PurchasePaymentCheckPayment> searchCheckPayments(PurchasePaymentCheckPaymentSearchCriteria criteria);

	List<PurchasePaymentCreditCardPayment> searchCreditCardPayments(
			PurchasePaymentCreditCardPaymentSearchCriteria criteria);

	List<PurchasePaymentCashPayment> searchCashPayments(PurchasePaymentCashPaymentSearchCriteria criteria);

	void unpost(PurchasePayment purchasePayment);

}