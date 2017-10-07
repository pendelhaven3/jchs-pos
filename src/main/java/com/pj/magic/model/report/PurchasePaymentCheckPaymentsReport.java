package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;
import com.pj.magic.model.PurchasePaymentCheckPayment;
import com.pj.magic.model.Supplier;

public class PurchasePaymentCheckPaymentsReport {

	private Date fromDate;
	private Date toDate;
	private Supplier supplier;
	private BigDecimal amount;
	private List<PurchasePaymentCheckPayment> checkPayments = new ArrayList<>();

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public List<PurchasePaymentCheckPayment> getCheckPayments() {
		return checkPayments;
	}

	public void setCheckPayments(List<PurchasePaymentCheckPayment> checkPayments) {
		this.checkPayments = checkPayments;
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (PurchasePaymentCheckPayment checkPayment : checkPayments) {
			total = total.add(checkPayment.getAmount());
		}
		return total;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
	
}