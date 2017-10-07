package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.AdjustmentType;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.util.TimePeriod;

public class PaymentAdjustmentSearchCriteria {

	private Long paymentAdjustmentNumber;
	private Customer customer;
	private AdjustmentType adjustmentType;
	private Boolean posted;
	private Date postDate;
	private Date postDateFrom;
	private Date postDateTo;
	private Boolean paid;
	private Date paidDate;
	private TimePeriod timePeriod;
	private PaymentTerminal paymentTerminal;

	public Long getPaymentAdjustmentNumber() {
		return paymentAdjustmentNumber;
	}

	public void setPaymentAdjustmentNumber(Long paymentAdjustmentNumber) {
		this.paymentAdjustmentNumber = paymentAdjustmentNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public AdjustmentType getAdjustmentType() {
		return adjustmentType;
	}

	public void setAdjustmentType(AdjustmentType adjustmentType) {
		this.adjustmentType = adjustmentType;
	}

	public Boolean getPosted() {
		return posted;
	}

	public void setPosted(Boolean posted) {
		this.posted = posted;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}

	public Date getPostDateFrom() {
		return postDateFrom;
	}

	public void setPostDateFrom(Date postDateFrom) {
		this.postDateFrom = postDateFrom;
	}

	public Date getPostDateTo() {
		return postDateTo;
	}

	public void setPostDateTo(Date postDateTo) {
		this.postDateTo = postDateTo;
	}

	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(TimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}

	public PaymentTerminal getPaymentTerminal() {
		return paymentTerminal;
	}

	public void setPaymentTerminal(PaymentTerminal paymentTerminal) {
		this.paymentTerminal = paymentTerminal;
	}

}