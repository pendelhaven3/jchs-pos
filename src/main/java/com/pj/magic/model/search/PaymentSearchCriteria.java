package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Customer;

public class PaymentSearchCriteria {

	private Boolean posted;
	private Customer customer;
	private Date postDate;
	private Long paymentNumber;
	private Boolean cancelled;
	private Long salesInvoiceNumber;
	private Long paymentAdjustmentNumber;

	public Boolean getPosted() {
		return posted;
	}

	public void setPosted(Boolean posted) {
		this.posted = posted;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public Long getPaymentNumber() {
		return paymentNumber;
	}

	public void setPaymentNumber(Long paymentNumber) {
		this.paymentNumber = paymentNumber;
	}

	public Boolean getCancelled() {
		return cancelled;
	}

	public void setCancelled(Boolean cancelled) {
		this.cancelled = cancelled;
	}

	public Long getSalesInvoiceNumber() {
		return salesInvoiceNumber;
	}

	public void setSalesInvoiceNumber(Long salesInvoiceNumber) {
		this.salesInvoiceNumber = salesInvoiceNumber;
	}

	public Long getPaymentAdjustmentNumber() {
		return paymentAdjustmentNumber;
	}

	public void setPaymentAdjustmentNumber(Long paymentAdjustmentNumber) {
		this.paymentAdjustmentNumber = paymentAdjustmentNumber;
	}
	
}