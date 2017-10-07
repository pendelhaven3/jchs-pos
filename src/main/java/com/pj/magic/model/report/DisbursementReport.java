package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;
import com.pj.magic.model.PurchasePaymentBankTransfer;
import com.pj.magic.model.PurchasePaymentCashPayment;
import com.pj.magic.model.PurchasePaymentCheckPayment;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;

public class DisbursementReport {

	private Date fromDate;
	private Date toDate;
	private List<PurchasePaymentBankTransfer> bankTransfers;
	private List<PurchasePaymentCreditCardPayment> creditCardPayments;
	private List<PurchasePaymentCashPayment> cashPayments;
	private List<PurchasePaymentCheckPayment> checkPayments;

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

	public List<PurchasePaymentCreditCardPayment> getCreditCardPayments() {
		return creditCardPayments;
	}

	public void setCreditCardPayments(
			List<PurchasePaymentCreditCardPayment> creditCardPayments) {
		this.creditCardPayments = creditCardPayments;
	}

	public List<PurchasePaymentCashPayment> getCashPayments() {
		return cashPayments;
	}

	public void setCashPayments(List<PurchasePaymentCashPayment> cashPayments) {
		this.cashPayments = cashPayments;
	}

	public List<PurchasePaymentCheckPayment> getCheckPayments() {
		return checkPayments;
	}

	public void setCheckPayments(List<PurchasePaymentCheckPayment> checkPayments) {
		this.checkPayments = checkPayments;
	}

	public BigDecimal getTotalCashPaymentsAmount() {
		BigDecimal total = Constants.ZERO;
		for (PurchasePaymentCashPayment cashPayment : cashPayments) {
			total = total.add(cashPayment.getAmount());
		}
		return total;
	}

	public BigDecimal getTotalCheckPaymentsAmount() {
		BigDecimal total = Constants.ZERO;
		for (PurchasePaymentCheckPayment checkPayment : checkPayments) {
			total = total.add(checkPayment.getAmount());
		}
		return total;
	}
	
	public BigDecimal getTotalCreditCardPaymentsAmount() {
		BigDecimal total = Constants.ZERO;
		for (PurchasePaymentCreditCardPayment creditCardPayment : creditCardPayments) {
			total = total.add(creditCardPayment.getAmount());
		}
		return total;
	}

	public BigDecimal getTotalBankTransfersAmount() {
		BigDecimal total = Constants.ZERO;
		for (PurchasePaymentBankTransfer bankTransfer : bankTransfers) {
			total = total.add(bankTransfer.getAmount());
		}
		return total;
	}

	public BigDecimal getTotalAmount() {
		return getTotalCashPaymentsAmount().add(getTotalCheckPaymentsAmount())
				.add(getTotalCreditCardPaymentsAmount())
				.add(getTotalBankTransfersAmount());
	}
	
}