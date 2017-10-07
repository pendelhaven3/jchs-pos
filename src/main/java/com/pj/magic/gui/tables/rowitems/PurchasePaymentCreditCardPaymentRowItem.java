package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.pj.magic.model.CreditCard;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;

public class PurchasePaymentCreditCardPaymentRowItem {

	private PurchasePaymentCreditCardPayment creditCardPayment;
	private BigDecimal amount;
	private CreditCard creditCard;
	private Date transactionDate;
	private String approvalCode;

	public PurchasePaymentCreditCardPaymentRowItem(PurchasePaymentCreditCardPayment creditCardPayment) {
		this.creditCardPayment = creditCardPayment;
		reset();
	}
	
	
	public PurchasePaymentCreditCardPayment getCreditCardPayment() {
		return creditCardPayment;
	}


	public void setCreditCardPayment(PurchasePaymentCreditCardPayment creditCardPayment) {
		this.creditCardPayment = creditCardPayment;
	}


	public BigDecimal getAmount() {
		return amount;
	}


	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void reset() {
		amount = creditCardPayment.getAmount();
		creditCard = creditCardPayment.getCreditCard();
		transactionDate = creditCardPayment.getTransactionDate();
		approvalCode = creditCardPayment.getApprovalCode();
	}

	public boolean isValid() {
		return amount != null && creditCard != null && transactionDate != null 
				&& !StringUtils.isEmpty(approvalCode);
	}

	public boolean isUpdating() {
		return creditCardPayment.getId() != null;
	}


	public CreditCard getCreditCard() {
		return creditCard;
	}


	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}


	public Date getTransactionDate() {
		return transactionDate;
	}


	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}


	public String getApprovalCode() {
		return approvalCode;
	}


	public void setApprovalCode(String approvalCode) {
		this.approvalCode = approvalCode;
	}

}