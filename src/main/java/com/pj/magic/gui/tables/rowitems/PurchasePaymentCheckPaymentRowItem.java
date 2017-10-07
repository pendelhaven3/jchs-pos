package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.pj.magic.model.PurchasePaymentCheckPayment;

public class PurchasePaymentCheckPaymentRowItem {

	private PurchasePaymentCheckPayment checkPayment;
	private String bank;
	private Date checkDate;
	private String checkNumber;
	private BigDecimal amount;

	public PurchasePaymentCheckPaymentRowItem(PurchasePaymentCheckPayment checkPayment) {
		this.checkPayment = checkPayment;
		bank = checkPayment.getBank();
		checkDate = checkPayment.getCheckDate();
		checkNumber = checkPayment.getCheckNumber();
		amount = checkPayment.getAmount();
	}
	
	public PurchasePaymentCheckPayment getCheckPayment() {
		return checkPayment;
	}

	public void setCheck(PurchasePaymentCheckPayment checkPayment) {
		this.checkPayment = checkPayment;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getCheckNumber() {
		return checkNumber;
	}

	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void reset() {
		bank = checkPayment.getBank();
		checkNumber = checkPayment.getCheckNumber();
		amount = checkPayment.getAmount();
	}

	public boolean isValid() {
		return !StringUtils.isEmpty(bank) && !StringUtils.isEmpty(checkNumber) && amount != null;
	}

	public boolean isUpdating() {
		return checkPayment.getId() != null;
	}

	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}
	
}