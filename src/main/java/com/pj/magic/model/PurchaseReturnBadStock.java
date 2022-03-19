package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseReturnBadStock {

	private Long id;
	private Long purchaseReturnBadStockNumber;
	private Supplier supplier;
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private String remarks;
	private Date pickupDate;
	
	private List<PurchaseReturnBadStockItem> items = new ArrayList<>();

	public PurchaseReturnBadStock() {
		// default constructor
	}
	
	public PurchaseReturnBadStock(Long id) {
		this.id = id;
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

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (PurchaseReturnBadStockItem item : items) {
			total = total.add(item.getAmount());
		}
		return total;
	}

}