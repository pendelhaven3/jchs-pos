package com.pj.magic.model;

import java.math.BigDecimal;


public class PurchaseReturnItem {

	private Long id;
	private PurchaseReturn parent;
	private ReceivingReceiptItem receivingReceiptItem;
	private Integer quantity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public PurchaseReturn getParent() {
		return parent;
	}

	public void setParent(PurchaseReturn parent) {
		this.parent = parent;
	}

	public ReceivingReceiptItem getReceivingReceiptItem() {
		return receivingReceiptItem;
	}

	public void setReceivingReceiptItem(ReceivingReceiptItem receivingReceiptItem) {
		this.receivingReceiptItem = receivingReceiptItem;
	}

	public BigDecimal getAmount() {
		if (receivingReceiptItem == null || quantity == null) {
			return null;
		} else {
			return getUnitCost().multiply(new BigDecimal(quantity));
		}
	}

	public BigDecimal getUnitCost() {
		if (receivingReceiptItem == null) {
			return null;
		} else {
			return receivingReceiptItem.getFinalCostWithVat();
		}
	}
	
}