package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.util.TimePeriod;

public class CashFlowReport {

	private List<CashFlowReportItem> items = new ArrayList<>();
	private Date paymentDate;
	private PaymentTerminal paymentTerminal;
	private TimePeriod timePeriod;

	public List<CashFlowReportItem> getItems() {
		return items;
	}

	public void setItems(List<CashFlowReportItem> items) {
		this.items = items;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public PaymentTerminal getPaymentTerminal() {
		return paymentTerminal;
	}

	public void setPaymentTerminal(PaymentTerminal paymentTerminal) {
		this.paymentTerminal = paymentTerminal;
	}

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(TimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (CashFlowReportItem item : items) {
			total = total.add(item.getAmount());
		}
		return total;
	}

}