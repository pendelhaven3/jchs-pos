package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.pj.magic.model.CreditCardStatementPayment;

public class CreditCardStatementPaymentRowItem {

	private CreditCardStatementPayment payment;
	private Date paymentDate;
	private BigDecimal amount;
	private String paymentType;
	private String remarks;

	public CreditCardStatementPaymentRowItem(CreditCardStatementPayment payment) {
		this.payment = payment;
		reset();
	}

	public void reset() {
		paymentDate = payment.getPaymentDate();
		amount = payment.getAmount();
		paymentType = payment.getPaymentType();
		remarks = payment.getRemarks();
	}

	public boolean isValid() {
		return paymentDate != null && amount != null && !StringUtils.isEmpty(paymentType);
	}

	public boolean isUpdating() {
		return payment.getId() != null;
	}

	public CreditCardStatementPayment getPayment() {
		return payment;
	}

	public void setPayment(CreditCardStatementPayment payment) {
		this.payment = payment;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
