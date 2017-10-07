package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

public class NoMoreStockAdjustment {

	private Long id;
	private Long noMoreStockAdjustmentNumber;
	private SalesInvoice salesInvoice;
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private boolean paid;
	private Date paidDate;
	private User paidBy;
	private PaymentTerminal paymentTerminal;
	private String remarks;
	private List<NoMoreStockAdjustmentItem> items = new ArrayList<>();
	private Long paymentNumber;
	private boolean pilferageFlag;

	public NoMoreStockAdjustment() {
		// default constructor
	}
	
	public NoMoreStockAdjustment(Long id) {
		this.id = id;
	}

	
	public boolean isNew() {
		return id == null;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getNoMoreStockAdjustmentNumber() {
		return noMoreStockAdjustmentNumber;
	}

	public void setNoMoreStockAdjustmentNumber(Long noMoreStockAdjustmentNumber) {
		this.noMoreStockAdjustmentNumber = noMoreStockAdjustmentNumber;
	}

	public SalesInvoice getSalesInvoice() {
		return salesInvoice;
	}

	public void setSalesInvoice(SalesInvoice salesInvoice) {
		this.salesInvoice = salesInvoice;
	}

	public List<NoMoreStockAdjustmentItem> getItems() {
		return items;
	}

	public void setItems(List<NoMoreStockAdjustmentItem> items) {
		this.items = items;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public String getStatus() {
		if (paid) {
			return "Paid";
		} else if (posted) {
			return "Posted/Unpaid";
		} else {
			return "New";
		}
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (NoMoreStockAdjustmentItem item : items) {
			total = total.add(item.getAmount());
		}
		return total;
	}

	public int getTotalItems() {
		return items.size();
	}

	public boolean hasItems() {
		return !items.isEmpty();
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
	
	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public User getPaidBy() {
		return paidBy;
	}

	public void setPaidBy(User paidBy) {
		this.paidBy = paidBy;
	}

	public PaymentTerminal getPaymentTerminal() {
		return paymentTerminal;
	}

	public void setPaymentTerminal(PaymentTerminal paymentTerminal) {
		this.paymentTerminal = paymentTerminal;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public BigDecimal getTotalCost() {
		BigDecimal total = Constants.ZERO;
		for (NoMoreStockAdjustmentItem item : items) {
			total = total.add(item.getTotalCost());
		}
		return total;
	}

	public Long getPaymentNumber() {
		return paymentNumber;
	}

	public void setPaymentNumber(Long paymentNumber) {
		this.paymentNumber = paymentNumber;
	}

	public boolean getPilferageFlag() {
		return pilferageFlag;
	}

	public void setPilferageFlag(boolean pilferageFlag) {
		this.pilferageFlag = pilferageFlag;
	}
	
}