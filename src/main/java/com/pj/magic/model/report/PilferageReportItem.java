package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import com.pj.magic.model.Product;

public class PilferageReportItem {

	private Date date;
	private String transactionType;
	private Long transactionNumber;
	private Product product;
	private String unit;
	private Integer quantity;
	private BigDecimal cost;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public Long getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(Long transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getAmount() {
		return cost.multiply(new BigDecimal(quantity)).setScale(2,  RoundingMode.HALF_UP);
	}

}
