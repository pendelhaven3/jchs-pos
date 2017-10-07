package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

public class PurchaseReturnBadStock {

	private Long id;
	private Long purchaseReturnBadStockNumber;
	private Supplier supplier;
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private String remarks;
	
	private List<PurchaseReturnBadStockItem> items = new ArrayList<>();

	public PurchaseReturnBadStock() {
		// default constructor
	}
	
	public PurchaseReturnBadStock(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPurchaseReturnBadStockNumber() {
		return purchaseReturnBadStockNumber;
	}

	public void setPurchaseReturnBadStockNumber(Long purchaseReturnBadStockNumber) {
		this.purchaseReturnBadStockNumber = purchaseReturnBadStockNumber;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public List<PurchaseReturnBadStockItem> getItems() {
		return items;
	}

	public void setItems(List<PurchaseReturnBadStockItem> items) {
		this.items = items;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public String getStatus() {
		return (posted) ? "Posted" : "New";
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
		for (PurchaseReturnBadStockItem item : items) {
			total = total.add(item.getAmount());
		}
		return total;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}