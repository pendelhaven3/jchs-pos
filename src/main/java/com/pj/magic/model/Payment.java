package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

public class Payment {

	private Long id;
	private Long paymentNumber;
	private Customer customer;
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private Date createDate;
	private PaymentTerminal paymentTerminal;
	private User encoder;
	private boolean cancelled;
	private Date cancelDate;
	private User cancelledBy;
	private BigDecimal cashAmountGiven;
	private List<PaymentSalesInvoice> salesInvoices = new ArrayList<>();
	private List<PaymentCheckPayment> checkPayments = new ArrayList<>();
	private List<PaymentCashPayment> cashPayments = new ArrayList<>();
	private List<PaymentPaymentAdjustment> adjustments = new ArrayList<>();

	public Payment() {
		// default constructor
	}
	
	public Payment(long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Long getPaymentNumber() {
		return paymentNumber;
	}

	public void setPaymentNumber(Long paymentNumber) {
		this.paymentNumber = paymentNumber;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public List<PaymentSalesInvoice> getSalesInvoices() {
		return salesInvoices;
	}

	public void setSalesInvoices(List<PaymentSalesInvoice> salesInvoices) {
		this.salesInvoices = salesInvoices;
	}

	public List<PaymentCheckPayment> getCheckPayments() {
		return checkPayments;
	}

	public void setCheckPayments(List<PaymentCheckPayment> checkPayments) {
		this.checkPayments = checkPayments;
	}

	public BigDecimal getTotalAmountDue() {
		BigDecimal total = Constants.ZERO;
		for (PaymentSalesInvoice salesInvoice : salesInvoices) {
			total = total.add(salesInvoice.getAmountDue());
		}
		return total;
	}

	public BigDecimal getTotalCheckPayments() {
		BigDecimal total = Constants.ZERO;
		for (PaymentCheckPayment check : checkPayments) {
			total = total.add(check.getAmount());
		}
		return total;
	}
	
	public BigDecimal getTotalPayments() {
		return getTotalCashPayments().add(getTotalCheckPayments());
	}
	
	public BigDecimal getOverOrShort() {
		return getTotalPayments().subtract(getTotalAmountDue()).add(getTotalAdjustments());
	}

	public BigDecimal getTotalCashPayments() {
		BigDecimal total = Constants.ZERO;
		for (PaymentCashPayment cashPayment : cashPayments) {
			total = total.add(cashPayment.getAmount());
		}
		return total;
	}

	public BigDecimal getTotalAdjustments() {
		BigDecimal total = Constants.ZERO;
		for (PaymentPaymentAdjustment adjustment : adjustments) {
			total = total.add(adjustment.getAmount());
		}
		return total;
	}

	public List<PaymentCashPayment> getCashPayments() {
		return cashPayments;
	}

	public void setCashPayments(List<PaymentCashPayment> cashPayments) {
		this.cashPayments = cashPayments;
	}

	public List<PaymentPaymentAdjustment> getAdjustments() {
		return adjustments;
	}

	public void setAdjustments(List<PaymentPaymentAdjustment> adjustments) {
		this.adjustments = adjustments;
	}

	public BigDecimal getTotalNetAmount() {
		BigDecimal total = Constants.ZERO;
		for (PaymentSalesInvoice paymentSalesInvoice : salesInvoices) {
			total = total.add(paymentSalesInvoice.getSalesInvoice().getTotalNetAmount());
		}
		return total;
	}
	
	public BigDecimal getTotalAdjustedAmount() {
		BigDecimal total = Constants.ZERO;
		for (PaymentSalesInvoice paymentSalesInvoice : salesInvoices) {
			total = total.add(paymentSalesInvoice.getAdjustedAmount());
		}
		return total;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public User getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(User postedBy) {
		this.postedBy = postedBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public PaymentTerminal getPaymentTerminal() {
		return paymentTerminal;
	}

	public void setPaymentTerminal(PaymentTerminal paymentTerminal) {
		this.paymentTerminal = paymentTerminal;
	}

	public User getEncoder() {
		return encoder;
	}

	public void setEncoder(User encoder) {
		this.encoder = encoder;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public User getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(User cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	public boolean isNew() {
		return !posted && !cancelled;
	}

	public String getStatus() {
		if (posted) {
			return "Posted";
		} else if (cancelled) {
			return "Cancelled";
		} else {
			return "New";
		}
	}

	public BigDecimal getCashAmountGiven() {
		return cashAmountGiven;
	}

	public void setCashAmountGiven(BigDecimal cashAmountGiven) {
		this.cashAmountGiven = cashAmountGiven;
	}

	public BigDecimal getCashChange() {
		if (cashAmountGiven != null) {
			return cashAmountGiven.subtract(getTotalAmountDueMinusNonCashPaymentsAndAdjustments());
		} else {
			return null;
		}
	}
	
	public BigDecimal getTotalAmountDueMinusNonCashPaymentsAndAdjustments() {
		return getTotalAmountDue().subtract(getTotalCheckPayments().add(getTotalAdjustments()));
	}

	public boolean hasCashPayment() {
		return !cashPayments.isEmpty();
	}
	
}