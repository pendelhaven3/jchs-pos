package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.Date;

public class ProductCanvassItem {

	private Date receivedDate;
	private Long receivingReceiptNumber;
	private Supplier supplier;
	private String unit;
	private BigDecimal finalCost;
	private BigDecimal grossCost;
	private String referenceNumber;

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
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

	public BigDecimal getFinalCost() {
		return finalCost;
	}

	public void setFinalCost(BigDecimal finalCost) {
		this.finalCost = finalCost;
	}

	public BigDecimal getGrossCost() {
		return grossCost;
	}

	public void setGrossCost(BigDecimal grossCost) {
		this.grossCost = grossCost;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}
