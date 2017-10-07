package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.Date;

import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.Customer;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.SalesReturn;

public class CashFlowReportItem {

	private Date time;
	private String transactionType;
	private Long referenceNumber;
	private Customer customer;
	private Date transactionDate;
	private BigDecimal amount;
	private PaymentTerminal paymentTerminal;

	public CashFlowReportItem(PaymentSalesInvoice paymentSalesInvoice) {
		time = paymentSalesInvoice.getParent().getPostDate();
		transactionType = "SALES INVOICE";
		referenceNumber = paymentSalesInvoice.getSalesInvoice().getSalesInvoiceNumber();
		customer = paymentSalesInvoice.getSalesInvoice().getCustomer();
		transactionDate = paymentSalesInvoice.getSalesInvoice().getTransactionDate();
		amount = paymentSalesInvoice.getSalesInvoice().getTotalNetAmount();
		paymentTerminal = paymentSalesInvoice.getParent().getPaymentTerminal();
	}
	
	public CashFlowReportItem(SalesReturn salesReturn) {
		time = salesReturn.getPaidDate();
		transactionType = "SALES RETURN";
		referenceNumber = salesReturn.getSalesReturnNumber();
		customer = salesReturn.getSalesInvoice().getCustomer();
		transactionDate = salesReturn.getPostDate();
		amount = salesReturn.getTotalAmount().negate();
		paymentTerminal = salesReturn.getPaymentTerminal();
	}
	
	public CashFlowReportItem(BadStockReturn badStockReturn) {
		time = badStockReturn.getPaidDate();
		transactionType = "BAD STOCK RETURN";
		referenceNumber = badStockReturn.getBadStockReturnNumber();
		customer = badStockReturn.getCustomer();
		transactionDate = badStockReturn.getPaidDate();
		amount = badStockReturn.getTotalAmount().negate();
		paymentTerminal = badStockReturn.getPaymentTerminal();
	}
	
	public CashFlowReportItem(NoMoreStockAdjustment noMoreStockAdjustment) {
		time = noMoreStockAdjustment.getPaidDate();
		transactionType = "NO MORE STOCK";
		referenceNumber = noMoreStockAdjustment.getNoMoreStockAdjustmentNumber();
		customer = noMoreStockAdjustment.getSalesInvoice().getCustomer();
		transactionDate = noMoreStockAdjustment.getPostDate();
		amount = noMoreStockAdjustment.getTotalAmount().negate();
		paymentTerminal = noMoreStockAdjustment.getPaymentTerminal();
	}
	
	public CashFlowReportItem(PaymentAdjustment paymentAdjustment) {
		time = paymentAdjustment.getPaidDate();
		transactionType = paymentAdjustment.getAdjustmentType().getCode();
		referenceNumber = paymentAdjustment.getPaymentAdjustmentNumber();
		customer = paymentAdjustment.getCustomer();
		transactionDate = paymentAdjustment.getPostDate();
		amount = paymentAdjustment.getAmount().negate();
		paymentTerminal = paymentAdjustment.getPaymentTerminal();
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public Long getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(Long referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public PaymentTerminal getPaymentTerminal() {
		return paymentTerminal;
	}

	public void setPaymentTerminal(PaymentTerminal paymentTerminal) {
		this.paymentTerminal = paymentTerminal;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

}