package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

/**
 * Model class for Purchase Return Good Stock
 * 
 * @author PJ Miranda
 *
 */
public class PurchaseReturn {

	private Long id;
	private Long purchaseReturnNumber;
	private ReceivingReceipt receivingReceipt;
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private String remarks;
	private List<PurchaseReturnItem> items = new ArrayList<>();
	private boolean paid;
	private Date paidDate;
	private User paidBy;

	public PurchaseReturn() {
		// default constructor
	}
	
	public PurchaseReturn(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public Long getPurchaseReturnNumber() {
		return purchaseReturnNumber;
	}

	public void setPurchaseReturnNumber(Long purchaseReturnNumber) {
		this.purchaseReturnNumber = purchaseReturnNumber;
	}

	public ReceivingReceipt getReceivingReceipt() {
		return receivingReceipt;
	}

	public void setReceivingReceipt(ReceivingReceipt receivingReceipt) {
		this.receivingReceipt = receivingReceipt;
	}

	public List<PurchaseReturnItem> getItems() {
		return items;
	}

	public void setItems(List<PurchaseReturnItem> items) {
		this.items = items;
	}

	public void setId(Long id) {
		this.id = id;
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
		} else {
			return (posted) ? "Posted/Unpaid" : "New";
		}
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (PurchaseReturnItem item : items) {
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
	
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
	
}