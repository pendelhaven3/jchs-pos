package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

public class PurchasePayment {

	private Long id;
	private Long purchasePaymentNumber;
	private Supplier supplier;
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private Date createDate;
	private User encoder;
	private boolean cancelled;
	private Date cancelDate;
	private User cancelledBy;
	private List<PurchasePaymentReceivingReceipt> receivingReceipts = new ArrayList<>();
	private List<PurchasePaymentCashPayment> cashPayments = new ArrayList<>();
	private List<PurchasePaymentCreditCardPayment> creditCardPayments = new ArrayList<>();
	private List<PurchasePaymentCheckPayment> checkPayments = new ArrayList<>();
	private List<PurchasePaymentBankTransfer> bankTransfers = new ArrayList<>();
	private List<PurchasePaymentPaymentAdjustment> paymentAdjustments = new ArrayList<>();

	public PurchasePayment() {
		// default constructor
	}
	
	public PurchasePayment(long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPurchasePaymentNumber() {
		return purchasePaymentNumber;
	}

	public void setPurchasePaymentNumber(Long purchasePaymentNumber) {
		this.purchasePaymentNumber = purchasePaymentNumber;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public User getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(User postedBy) {
		this.postedBy = postedBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public User getEncoder() {
		return encoder;
	}

	public void setEncoder(User encoder) {
		this.encoder = encoder;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public User getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(User cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	public List<PurchasePaymentReceivingReceipt> getReceivingReceipts() {
		return receivingReceipts;
	}

	public void setReceivingReceipts(
			List<PurchasePaymentReceivingReceipt> receivingReceipts) {
		this.receivingReceipts = receivingReceipts;
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (PurchasePaymentReceivingReceipt receivingReceipt : receivingReceipts) {
			total = total.add(receivingReceipt.getReceivingReceipt().getTotalNetAmountWithVat());
		}
		return total;
	}

	public String getStatus() {
		return (posted) ? "Posted" : "New";
	}

	public List<PurchasePaymentCashPayment> getCashPayments() {
		return cashPayments;
	}

	public void setCashPayments(List<PurchasePaymentCashPayment> cashPayments) {
		this.cashPayments = cashPayments;
	}

	public boolean isNew() {
		return !posted && !cancelled;
	}

	public BigDecimal getTotalCashPayments() {
		BigDecimal total = Constants.ZERO;
		for (PurchasePaymentCashPayment cashPayment : cashPayments) {
			total = total.add(cashPayment.getAmount());
		}
		return total;
	}
	
	public BigDecimal getTotalCreditCardPayments() {
		BigDecimal total = Constants.ZERO;
		for (PurchasePaymentCreditCardPayment creditCardPayment : creditCardPayments) {
			total = total.add(creditCardPayment.getAmount());
		}
		return total;
	}
	
	public BigDecimal getTotalPayments() {
		return getTotalCashPayments().add(getTotalCreditCardPayments()).add(getTotalCheckPayments())
				.add(getTotalBankTransfers());
	}
	
	public BigDecimal getTotalBankTransfers() {
		BigDecimal total = Constants.ZERO;
		for (PurchasePaymentBankTransfer bankTransfer : bankTransfers) {
			total = total.add(bankTransfer.getAmount());
		}
		return total;
	}

	public BigDecimal getTotalCheckPayments() {
		BigDecimal total = Constants.ZERO;
		for (PurchasePaymentCheckPayment check : checkPayments) {
			total = total.add(check.getAmount());
		}
		return total;
	}
	
	public BigDecimal getOverOrShort() {
		return getTotalPayments().subtract(getTotalAmount()).add(getTotalAdjustments());
	}

	public BigDecimal getTotalAdjustments() {
		BigDecimal total = Constants.ZERO;
		for (PurchasePaymentPaymentAdjustment adjustment : paymentAdjustments) {
			total = total.add(adjustment.getAmount());
		}
		return total;
	}

	public List<PurchasePaymentCreditCardPayment> getCreditCardPayments() {
		return creditCardPayments;
	}

	public void setCreditCardPayments(List<PurchasePaymentCreditCardPayment> creditCardPayments) {
		this.creditCardPayments = creditCardPayments;
	}

	public List<PurchasePaymentCheckPayment> getCheckPayments() {
		return checkPayments;
	}

	public void setCheckPayments(List<PurchasePaymentCheckPayment> checkPayments) {
		this.checkPayments = checkPayments;
	}

	public List<PurchasePaymentPaymentAdjustment> getPaymentAdjustments() {
		return paymentAdjustments;
	}

	public void setPaymentAdjustments(List<PurchasePaymentPaymentAdjustment> paymentAdjustments) {
		this.paymentAdjustments = paymentAdjustments;
	}

	public List<PurchasePaymentBankTransfer> getBankTransfers() {
		return bankTransfers;
	}

	public void setBankTransfers(List<PurchasePaymentBankTransfer> bankTransfers) {
		this.bankTransfers = bankTransfers;
	}

	public BigDecimal getTotalAmountDue() {
		return getTotalAmount().subtract(getTotalAdjustments());
	}
	
}