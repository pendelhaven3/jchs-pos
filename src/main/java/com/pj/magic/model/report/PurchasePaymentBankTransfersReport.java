package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;
import com.pj.magic.model.PurchasePaymentBankTransfer;
import com.pj.magic.model.Supplier;

public class PurchasePaymentBankTransfersReport {

	private Date fromDate;
	private Date toDate;
	private Supplier supplier;
	private List<PurchasePaymentBankTransfer> bankTransfers = new ArrayList<>();

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

	public List<PurchasePaymentBankTransfer> getBankTransfers() {
		return bankTransfers;
	}

	public void setBankTransfers(List<PurchasePaymentBankTransfer> bankTransfers) {
		this.bankTransfers = bankTransfers;
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (PurchasePaymentBankTransfer bankTransfer : bankTransfers) {
			total = total.add(bankTransfer.getAmount());
		}
		return total;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	
}