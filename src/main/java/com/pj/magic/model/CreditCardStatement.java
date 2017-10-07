package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

public class CreditCardStatement {

	private Long id;
	private String customerNumber;
	private CreditCard creditCard;
	private Date statementDate;
	private List<CreditCardStatementItem> items = new ArrayList<>();
	private boolean posted;
	private List<CreditCardStatementPayment> payments = new ArrayList<>();

	public CreditCardStatement() {
		// default constructor
	}
	
	public CreditCardStatement(long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public Date getStatementDate() {
		return statementDate;
	}

	public void setStatementDate(Date statementDate) {
		this.statementDate = statementDate;
	}

	public List<CreditCardStatementItem> getItems() {
		return items;
	}

	public void setItems(List<CreditCardStatementItem> items) {
		this.items = items;
	}

	public boolean isNew() {
		return id == null;
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (CreditCardStatementItem item : items) {
			total = total.add(item.getCreditCardPayment().getAmount());
		}
		return total;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public String getStatus() {
		return posted ? "Posted" : "New";
	}

	public List<CreditCardStatementPayment> getPayments() {
		return payments;
	}

	public void setPayments(List<CreditCardStatementPayment> payments) {
		this.payments = payments;
	}

	public int getTotalPurchases() {
		return items.size();
	}

	public BigDecimal getTotalPurchaseAmount() {
		BigDecimal total = Constants.ZERO;
		for (CreditCardStatementItem item : items) {
			total = total.add(item.getCreditCardPayment().getAmount());
		}
		return total;
	}

	public int getTotalPayments() {
		return payments.size();
	}

	public BigDecimal getTotalPaymentAmount() {
		BigDecimal total = Constants.ZERO;
		for (CreditCardStatementPayment payment : payments) {
			total = total.add(payment.getAmount());
		}
		return total;
	}

	public BigDecimal getBalance() {
		return getTotalPaymentAmount().subtract(getTotalPurchaseAmount());
	}
	
}
