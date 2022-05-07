package com.pj.magic.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.PurchasePaymentAdjustmentDao;
import com.pj.magic.dao.PurchasePaymentAdjustmentTypeDao;
import com.pj.magic.dao.PurchasePaymentBankTransferDao;
import com.pj.magic.dao.PurchasePaymentCashPaymentDao;
import com.pj.magic.dao.PurchasePaymentCheckPaymentDao;
import com.pj.magic.dao.PurchasePaymentCreditCardPaymentDao;
import com.pj.magic.dao.PurchasePaymentDao;
import com.pj.magic.dao.PurchasePaymentPaymentAdjustmentDao;
import com.pj.magic.dao.PurchasePaymentReceivingReceiptDao;
import com.pj.magic.dao.PurchaseReturnBadStockDao;
import com.pj.magic.dao.PurchaseReturnDao;
import com.pj.magic.dao.ReceivingReceiptItemDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentAdjustment;
import com.pj.magic.model.PurchasePaymentAdjustmentType;
import com.pj.magic.model.PurchasePaymentBankTransfer;
import com.pj.magic.model.PurchasePaymentCashPayment;
import com.pj.magic.model.PurchasePaymentCheckPayment;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.model.PurchasePaymentPaymentAdjustment;
import com.pj.magic.model.PurchasePaymentReceivingReceipt;
import com.pj.magic.model.PurchaseReturn;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.search.PurchasePaymentBankTransferSearchCriteria;
import com.pj.magic.model.search.PurchasePaymentCashPaymentSearchCriteria;
import com.pj.magic.model.search.PurchasePaymentCheckPaymentSearchCriteria;
import com.pj.magic.model.search.PurchasePaymentCreditCardPaymentSearchCriteria;
import com.pj.magic.model.search.PurchasePaymentSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PurchasePaymentAdjustmentService;
import com.pj.magic.service.PurchasePaymentService;
import com.pj.magic.service.PurchaseReturnBadStockService;
import com.pj.magic.service.PurchaseReturnService;

@Service
public class PurchasePaymentServiceImpl implements PurchasePaymentService {

	@Autowired private PurchasePaymentDao purchasePaymentDao;
	@Autowired private ReceivingReceiptItemDao receivingReceiptItemDao;
	@Autowired private LoginService loginService;
	@Autowired private PurchasePaymentReceivingReceiptDao purchasePaymentReceivingReceiptDao;
	@Autowired private PurchasePaymentCashPaymentDao purchasePaymentCashPaymentDao;
	@Autowired private PurchasePaymentCreditCardPaymentDao purchasePaymentCreditCardPaymentDao;
	@Autowired private PurchasePaymentCheckPaymentDao purchasePaymentCheckPaymentDao;
	@Autowired private PurchasePaymentBankTransferDao purchasePaymentBankTransferDao;
	@Autowired private PurchasePaymentAdjustmentDao purchasePaymentAdjustmentDao;
	@Autowired private PurchasePaymentPaymentAdjustmentDao purchasePaymentPaymentAdjustmentDao;
	@Autowired private PurchaseReturnService purchaseReturnService;
	@Autowired private PurchaseReturnDao purchaseReturnDao;
	@Autowired private PurchasePaymentAdjustmentService purchasePaymentAdjustmentService;
	@Autowired private PurchaseReturnBadStockService purchaseReturnBadStockService;
	@Autowired private PurchaseReturnBadStockDao purchaseReturnBadStockDao;
	@Autowired private SystemDao systemDao;
	@Autowired private PurchasePaymentAdjustmentTypeDao purchasePaymentAdjustmentTypeDao;
	
	@Transactional
	@Override
	public void save(PurchasePayment purchasePayment) {
		boolean newPayment = (purchasePayment.getId() == null);
		if (newPayment) {
			purchasePayment.setEncoder(loginService.getLoggedInUser());
		}
		purchasePaymentDao.save(purchasePayment);
		if (!newPayment) {
			purchasePaymentReceivingReceiptDao.deleteAllByPurchasePayment(purchasePayment);
			purchasePaymentPaymentAdjustmentDao.deleteAllByPurchasePayment(purchasePayment);
		}
	}

	@Override
	public PurchasePayment getPurchasePayment(Long id) {
		PurchasePayment purchasePayment = purchasePaymentDao.get(id);
		loadPaymentDetails(purchasePayment);
		return purchasePayment;
	}

	private void loadPaymentDetails(PurchasePayment purchasePayment) {
		purchasePayment.setReceivingReceipts(
				purchasePaymentReceivingReceiptDao.findAllByPurchasePayment(purchasePayment));
		for (PurchasePaymentReceivingReceipt receivingReceipt : purchasePayment.getReceivingReceipts()) {
			receivingReceipt.getReceivingReceipt().setItems(
					receivingReceiptItemDao.findAllByReceivingReceipt(receivingReceipt.getReceivingReceipt()));
		}
		purchasePayment.setCashPayments(purchasePaymentCashPaymentDao.findAllByPurchasePayment(purchasePayment));
		purchasePayment.setCreditCardPayments(
				purchasePaymentCreditCardPaymentDao.findAllByPurchasePayment(purchasePayment));
		purchasePayment.setCheckPayments(purchasePaymentCheckPaymentDao.findAllByPurchasePayment(purchasePayment));
		purchasePayment.setBankTransfers(
				purchasePaymentBankTransferDao.findAllByPurchasePayment(purchasePayment));
		purchasePayment.setPaymentAdjustments(
				purchasePaymentPaymentAdjustmentDao.findAllByPurchasePayment(purchasePayment));
	}
	
	@Transactional
	@Override
	public void post(PurchasePayment purchasePayment) {
		PurchasePayment updated = getPurchasePayment(purchasePayment.getId());
		updated.setPosted(true);
		updated.setPostDate(systemDao.getCurrentDateTime());
		updated.setPostedBy(loginService.getLoggedInUser());
		purchasePaymentDao.save(updated);
		
		for (PurchasePaymentPaymentAdjustment paymentAdjustment : updated.getPaymentAdjustments()) {
			long referenceNumber = Long.parseLong(paymentAdjustment.getReferenceNumber());
			switch (paymentAdjustment.getAdjustmentType().getCode()) {
			case PurchasePaymentAdjustmentType.PURCHASE_RETURN_GOOD_STOCK_CODE:
				purchaseReturnService.markAsPaid(
						purchaseReturnDao.findByPurchaseReturnNumber(referenceNumber));
				break;
			case PurchasePaymentAdjustmentType.PURCHASE_RETURN_BAD_STOCK_CODE:
				purchaseReturnBadStockService.post(
						purchaseReturnBadStockDao.findByPurchaseReturnBadStockNumber(referenceNumber));
				break;
			default:
				purchasePaymentAdjustmentService.post(
						purchasePaymentAdjustmentDao.findByPurchasePaymentAdjustmentNumber(referenceNumber));
				break;
			}
		}
	}

	@Override
	public List<PurchasePayment> getAllNewPurchasePayments() {
		PurchasePaymentSearchCriteria criteria = new PurchasePaymentSearchCriteria();
		criteria.setPosted(false);
		return search(criteria);
	}

	private List<PurchasePayment> search(PurchasePaymentSearchCriteria criteria) {
		return purchasePaymentDao.search(criteria);
	}

	@Transactional
	@Override
	public void save(PurchasePaymentReceivingReceipt paymentReceivingReceipt) {
		purchasePaymentReceivingReceiptDao.insert(paymentReceivingReceipt);
	}

	@Override
	public List<PurchasePaymentReceivingReceipt> findAllPaymentReceivingReceiptsByPurchasePayment(
			PurchasePayment purchasePayment) {
		return purchasePaymentReceivingReceiptDao.findAllByPurchasePayment(purchasePayment);
	}

	@Transactional
	@Override
	public void save(PurchasePaymentCheckPayment checkPayment) {
		purchasePaymentCheckPaymentDao.save(checkPayment);
	}

	@Transactional
	@Override
	public void delete(PurchasePaymentReceivingReceipt paymentReceivingReceipt) {
		purchasePaymentReceivingReceiptDao.delete(paymentReceivingReceipt);
	}

	@Transactional
	@Override
	public void save(PurchasePaymentCashPayment cashPayment) {
		purchasePaymentCashPaymentDao.save(cashPayment);
	}

	@Transactional
	@Override
	public void delete(PurchasePaymentCheckPayment checkPayment) {
		purchasePaymentCheckPaymentDao.delete(checkPayment);
	}

	@Transactional
	@Override
	public void delete(PurchasePaymentCashPayment cashPayment) {
		purchasePaymentCashPaymentDao.delete(cashPayment);
	}

	@Transactional
	@Override
	public void save(PurchasePaymentCreditCardPayment creditCardPayment) {
		purchasePaymentCreditCardPaymentDao.save(creditCardPayment);
	}

	@Transactional
	@Override
	public void delete(PurchasePaymentCreditCardPayment creditCardPayment) {
		purchasePaymentCreditCardPaymentDao.delete(creditCardPayment);
	}

	@Transactional
	@Override
	public void delete(PurchasePaymentPaymentAdjustment paymentAdjustment) {
		purchasePaymentPaymentAdjustmentDao.delete(paymentAdjustment);
	}

	@Override
	public void save(PurchasePaymentPaymentAdjustment paymentAdjustment) {
		purchasePaymentPaymentAdjustmentDao.save(paymentAdjustment);
	}

	@Override
	public List<PurchasePayment> searchPurchasePayments(PurchasePaymentSearchCriteria criteria) {
		return purchasePaymentDao.search(criteria);
	}

	@Transactional
	@Override
	public void save(PurchasePaymentBankTransfer bankTransfer) {
		purchasePaymentBankTransferDao.save(bankTransfer);
	}

	@Transactional
	@Override
	public void delete(PurchasePaymentBankTransfer bankTransfer) {
		purchasePaymentBankTransferDao.delete(bankTransfer);
	}

	@Override
	public List<PurchasePaymentBankTransfer> searchBankTransfers(
			PurchasePaymentBankTransferSearchCriteria criteria) {
		return purchasePaymentBankTransferDao.search(criteria);
	}

	@Override
	public List<PurchasePaymentCheckPayment> searchCheckPayments(
			PurchasePaymentCheckPaymentSearchCriteria criteria) {
		return purchasePaymentCheckPaymentDao.search(criteria);
	}

	@Override
	public List<PurchasePaymentCreditCardPayment> searchCreditCardPayments(
			PurchasePaymentCreditCardPaymentSearchCriteria criteria) {
		return purchasePaymentCreditCardPaymentDao.search(criteria);
	}

	@Override
	public List<PurchasePaymentCashPayment> searchCashPayments(
			PurchasePaymentCashPaymentSearchCriteria criteria) {
		return purchasePaymentCashPaymentDao.search(criteria);
	}

	@Transactional
	@Override
	public void unpost(PurchasePayment purchasePayment) {
		PurchasePayment updated = getPurchasePayment(purchasePayment.getId());
		updated.setPosted(false);
		updated.setPostDate(null);
		updated.setPostedBy(null);
		purchasePaymentDao.save(updated);
	}
	
    @Transactional
    @Override
    public void generateEwtAdjustment(PurchasePayment purchasePayment) {
        purchasePayment = getPurchasePayment(purchasePayment.getId());
        Map<ReceivingReceipt, BigDecimal> cancelledItemsAmountMap = generateCancelledItemsAmountMap(purchasePayment);
        
        for (int i = 0; i < purchasePayment.getReceivingReceipts().size(); i++) {
            ReceivingReceipt receivingReceipt = purchasePayment.getReceivingReceipts().get(i).getReceivingReceipt();
            BigDecimal invoiceAmount = receivingReceipt.getTotalNetAmountWithVat();
            BigDecimal grossAmount = invoiceAmount.subtract(cancelledItemsAmountMap.get(receivingReceipt));
            if (i == 0) {
                grossAmount = grossAmount.subtract(purchasePayment.getBadStockAdjustmentsTotalAmount())
                        .subtract(purchasePayment.getDiscountAdjustmentsTotalAmount());
            }
            BigDecimal netOfVatAmount = grossAmount.divide(new BigDecimal("1.12"), RoundingMode.HALF_UP);
            BigDecimal ewt = netOfVatAmount.multiply(new BigDecimal("0.01")).setScale(2, RoundingMode.HALF_UP);
            
            PurchasePaymentAdjustment ewtAdjustment = new PurchasePaymentAdjustment();
            ewtAdjustment.setSupplier(purchasePayment.getSupplier());
            ewtAdjustment.setAdjustmentType(purchasePaymentAdjustmentTypeDao.findByCode(PurchasePaymentAdjustmentType.EWT_CODE));
            ewtAdjustment.setAmount(ewt);
            ewtAdjustment.setRemarks(MessageFormat.format("PP: {0}, RR: {1}", 
                    purchasePayment.getPurchasePaymentNumber().toString(), receivingReceipt.getReceivingReceiptNumber().toString()));
            purchasePaymentAdjustmentDao.save(ewtAdjustment);
            
            if (!purchasePayment.isPosted()) {
                PurchasePaymentPaymentAdjustment paymentAdjustment = new PurchasePaymentPaymentAdjustment();
                paymentAdjustment.setParent(purchasePayment);
                paymentAdjustment.setAdjustmentType(ewtAdjustment.getAdjustmentType());
                paymentAdjustment.setReferenceNumber(ewtAdjustment.getPurchasePaymentAdjustmentNumber().toString());
                paymentAdjustment.setAmount(ewtAdjustment.getAmount());
                purchasePaymentPaymentAdjustmentDao.save(paymentAdjustment);
            }
        }
    }

    private Map<ReceivingReceipt, BigDecimal> generateCancelledItemsAmountMap(PurchasePayment purchasePayment) {
        Map<ReceivingReceipt, BigDecimal> map = purchasePayment.getReceivingReceipts().stream()
                .map(r -> r.getReceivingReceipt())
                .collect(Collectors.toMap(r -> r, r -> BigDecimal.ZERO));
        
        for (PurchasePaymentPaymentAdjustment paymentAdjustment : purchasePayment.getPaymentAdjustments()) {
            if (paymentAdjustment.isCancelledItemsAdjustment()) {
                PurchaseReturn purchaseReturn = purchaseReturnDao.findByPurchaseReturnNumber(Long.valueOf(paymentAdjustment.getReferenceNumber()));
                ReceivingReceipt receivingReceipt = purchaseReturn.getReceivingReceipt();
                map.put(receivingReceipt, purchaseReturn.getTotalAmount());
            }
        }
        
        return map;
    }

}