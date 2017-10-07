package com.pj.magic.model;

public class PurchasePaymentReceivingReceipt {

	private Long id;
	private PurchasePayment parent;
	private ReceivingReceipt receivingReceipt;

	public PurchasePaymentReceivingReceipt() {
		// default constructor
	}
	
	public PurchasePaymentReceivingReceipt(long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PurchasePayment getParent() {
		return parent;
	}

	public void setParent(PurchasePayment parent) {
		this.parent = parent;
	}

	public ReceivingReceipt getReceivingReceipt() {
		return receivingReceipt;
	}

	public void setReceivingReceipt(ReceivingReceipt receivingReceipt) {
		this.receivingReceipt = receivingReceipt;
	}

}