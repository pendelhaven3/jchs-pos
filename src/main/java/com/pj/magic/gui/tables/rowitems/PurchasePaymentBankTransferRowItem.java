package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.pj.magic.model.PurchasePaymentBankTransfer;

public class PurchasePaymentBankTransferRowItem {

	private PurchasePaymentBankTransfer bankTransfer;
	private String bank;
	private BigDecimal amount;
	private String referenceNumber;
	private Date transferDate;

	public PurchasePaymentBankTransferRowItem(PurchasePaymentBankTransfer bankTransfer) {
		this.bankTransfer = bankTransfer;
		reset();
	}

	public PurchasePaymentBankTransfer getBankTransfer() {
		return bankTransfer;
	}

	public void setCheck(PurchasePaymentBankTransfer bankTransfer) {
		this.bankTransfer = bankTransfer;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void reset() {
		bank = bankTransfer.getBank();
		amount = bankTransfer.getAmount();
		referenceNumber = bankTransfer.getReferenceNumber();
		transferDate = bankTransfer.getTransferDate();
	}

	public boolean isValid() {
		return !StringUtils.isEmpty(bank) && amount != null 
				&& !StringUtils.isEmpty(referenceNumber) && transferDate != null;
	}

	public boolean isUpdating() {
		return bankTransfer.getId() != null;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public Date getTransferDate() {
		return transferDate;
	}

	public void setTransferDate(Date transferDate) {
		this.transferDate = transferDate;
	}

	public void setBankTransfer(PurchasePaymentBankTransfer bankTransfer) {
		this.bankTransfer = bankTransfer;
	}

}