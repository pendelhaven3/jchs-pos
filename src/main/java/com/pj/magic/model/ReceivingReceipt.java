package com.pj.magic.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

public class ReceivingReceipt {

	private Long id;
	private Long receivingReceiptNumber;
	private Supplier supplier;
	private Date receivedDate;
	private User receivedBy;
	private PaymentTerm paymentTerm;
	private String referenceNumber;
	private List<ReceivingReceiptItem> items = new ArrayList<>();
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private boolean cancelled;
	private Date cancelDate;
	private User cancelledBy;
	private String remarks;
	private Long relatedPurchaseOrderNumber;
	private boolean vatInclusive;
	private BigDecimal vatRate;

	public ReceivingReceipt() {
		// default constructor
	}
	
	public ReceivingReceipt(long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getReceivingReceiptNumber() {
		return receivingReceiptNumber;
	}

	public void setReceivingReceiptNumber(Long receivingReceiptNumber) {
		this.receivingReceiptNumber = receivingReceiptNumber;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public User getReceivedBy() {
		return receivedBy;
	}

	public void setReceivedBy(User receivedBy) {
		this.receivedBy = receivedBy;
	}

	public PaymentTerm getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(PaymentTerm paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public void setItems(List<ReceivingReceiptItem> items) {
		this.items = items;
	}
	
	public List<ReceivingReceiptItem> getItems() {
		return items;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getStatus() {
		if (posted) {
			return "Posted";
		} else if (cancelled) {
			return "Cancelled";
		} else {
			return "New";
		}
	}

	public int getTotalNumberOfItems() {
		return items.size();
	}

	public BigDecimal getSubTotalAmount() {
		BigDecimal subTotal = Constants.ZERO;
		for (ReceivingReceiptItem item : items) {
			subTotal = subTotal.add(item.getAmount());
		}
		return subTotal;
	}

	public BigDecimal getTotalDiscountedAmount() {
		BigDecimal totalDiscountedAmount = Constants.ZERO;
		for (ReceivingReceiptItem item : items) {
			totalDiscountedAmount = totalDiscountedAmount.add(item.getDiscountedAmount());
		}
		return totalDiscountedAmount;
	}

	public BigDecimal getTotalNetAmount() {
		BigDecimal totalNetAmount = Constants.ZERO;
		for (ReceivingReceiptItem item : items) {
			totalNetAmount = totalNetAmount.add(item.getNetAmount());
		}
		return totalNetAmount;
	}

	public Long getRelatedPurchaseOrderNumber() {
		return relatedPurchaseOrderNumber;
	}

	public void setRelatedPurchaseOrderNumber(Long relatedPurchaseOrderNumber) {
		this.relatedPurchaseOrderNumber = relatedPurchaseOrderNumber;
	}
	
	public int getTotalQuantity() {
		int totalQuantity = 0;
		for (ReceivingReceiptItem item : items) {
			totalQuantity += item.getQuantity();
		}
		return totalQuantity;
	}

	public boolean isVatInclusive() {
		return vatInclusive;
	}

	public void setVatInclusive(boolean vatInclusive) {
		this.vatInclusive = vatInclusive;
	}

	public BigDecimal getVatRate() {
		return vatRate;
	}

	public void setVatRate(BigDecimal vatRate) {
		this.vatRate = vatRate;
	}
	
	// TODO: CHeck if used (reports?)
	public BigDecimal getTotalAmount() {
		return getTotalNetAmount().add(getVatAmount());
	}
	
	public BigDecimal getVatAmount() {
		if (vatInclusive) {
			return Constants.ZERO;
		} else {
			return (getTotalNetAmount().multiply(vatRate)).setScale(2, RoundingMode.HALF_UP);
		}
	}
	
	public BigDecimal getVatMultiplier() {
		if (vatInclusive) {
			return Constants.ONE;
		} else {
			return Constants.ONE.add(vatRate);
		}
	}

	public BigDecimal getTotalNetAmountWithVat() {
		return getTotalNetAmount().add(getVatAmount());
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

	public boolean isNew() {
		return !posted && !cancelled;
	}

	public ReceivingReceiptItem findItemByProductAndUnit(Product product, String unit) {
		for (ReceivingReceiptItem item : items) {
			if (product.equals(item.getProduct()) && unit.equals(item.getUnit())) {
				return item;
			}
		}
		return null;
	}

	public boolean hasProduct(Product product) {
		for (ReceivingReceiptItem item : items) {
			if (product.equals(item.getProduct())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasProductAndUnit(Product product, String unit) {
		for (ReceivingReceiptItem item : items) {
			if (product.equals(item.getProduct()) && unit.equals(item.getUnit())) {
				return true;
			}
		}
		return false;
	}

}