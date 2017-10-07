package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

public class BadStockReturn {

	private Long id;
	private Long badStockReturnNumber;
	private Customer customer;
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private boolean paid;
	private Date paidDate;
	private User paidBy;
	private boolean cancelled;
	private Date cancelDate;
	private User cancelledBy;
	private PaymentTerminal paymentTerminal;
	private String remarks;
	private Long paymentNumber;
	
	private List<BadStockReturnItem> items = new ArrayList<>();

	public BadStockReturn() {
		// default constructor
	}
	
	public BadStockReturn(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBadStockReturnNumber() {
		return badStockReturnNumber;
	}

	public void setBadStockReturnNumber(Long badStockReturnNumber) {
		this.badStockReturnNumber = badStockReturnNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<BadStockReturnItem> getItems() {
		return items;
	}

	public void setItems(List<BadStockReturnItem> items) {
		this.items = items;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public String getStatus() {
		if (cancelled) {
			return "Cancelled";
		} else if (paid) {
			return "Paid";
		} else if (posted) {
			return "Posted/Unpaid";
		} else {
			return "New";
		}
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

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (BadStockReturnItem item : items) {
			total = total.add(item.getAmount());
		}
		return total;
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
		for (BadStockReturnItem item : items) {
			total = total.add(item.getTotalCost());
		}
		return total;
	}

	public BigDecimal getTotalLoss() {
		BigDecimal total = Constants.ZERO;
		for (BadStockReturnItem item : items) {
			total = total.add(item.getTotalLoss());
		}
		return total;
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

	public Long getPaymentNumber() {
		return paymentNumber;
	}

	public void setPaymentNumber(Long paymentNumber) {
		this.paymentNumber = paymentNumber;
	}
	
}