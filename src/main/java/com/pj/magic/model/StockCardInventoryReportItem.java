package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.Date;

public class StockCardInventoryReportItem {

	private Date postDate;
	private Long transactionNumber;
	private String supplierOrCustomerName;
	private String transactionType;
	private Integer addQuantity;
	private Integer lessQuantity;
	private BigDecimal currentCostOrSellingPrice;
	private BigDecimal amount;
	private String referenceNumber;
	private String unit;

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public Long getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(Long transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public String getSupplierOrCustomerName() {
		return supplierOrCustomerName;
	}

	public void setSupplierOrCustomerName(String supplierOrCustomerName) {
		this.supplierOrCustomerName = supplierOrCustomerName;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public Integer getAddQuantity() {
		return addQuantity;
	}

	public void setAddQuantity(Integer addQuantity) {
		this.addQuantity = addQuantity;
	}

	public Integer getLessQuantity() {
		return lessQuantity;
	}

	public void setLessQuantity(Integer lessQuantity) {
		this.lessQuantity = lessQuantity;
	}

	public BigDecimal getCurrentCostOrSellingPrice() {
		return currentCostOrSellingPrice;
	}

	public void setCurrentCostOrSellingPrice(
			BigDecimal currentCostOrSellingPrice) {
		this.currentCostOrSellingPrice = currentCostOrSellingPrice;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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