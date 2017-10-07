package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.Date;

public class CreditCardStatementPayment {

	private Long id;
	private CreditCardStatement parent;
	private Date paymentDate;
	private BigDecimal amount;
	private String paymentType;
	private String remarks;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CreditCardStatement getParent() {
		return parent;
	}

	public void setParent(CreditCardStatement parent) {
		this.parent = parent;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public boolean isNew() {
		return id == null;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
