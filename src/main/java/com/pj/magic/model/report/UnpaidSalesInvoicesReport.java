package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.List;

import com.pj.magic.Constants;
import com.pj.magic.model.PaymentSalesInvoice;

public class UnpaidSalesInvoicesReport {

	private List<PaymentSalesInvoice> salesInvoices;

	public List<PaymentSalesInvoice> getSalesInvoices() {
		return salesInvoices;
	}

	public void setSalesInvoices(List<PaymentSalesInvoice> salesInvoices) {
		this.salesInvoices = salesInvoices;
	}
	
	public BigDecimal getTotalNetAmount() {
		BigDecimal total = Constants.ZERO;
		for (PaymentSalesInvoice salesInvoice : salesInvoices) {
			total = total.add(salesInvoice.getSalesInvoice().getTotalNetAmount());
		}
		return total;
	}
	
	public BigDecimal getTotalAdjustedAmount() {
		BigDecimal total = Constants.ZERO;
		for (PaymentSalesInvoice salesInvoice : salesInvoices) {
			total = total.add(salesInvoice.getAdjustedAmount());
		}
		return total;
	}
	
	public BigDecimal getTotalAmountDue() {
		BigDecimal total = Constants.ZERO;
		for (PaymentSalesInvoice salesInvoice : salesInvoices) {
			total = total.add(salesInvoice.getAmountDue());
		}
		return total;
	}
	
}