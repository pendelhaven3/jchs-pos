package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.pj.magic.model.PaymentCheckPayment;

public class PaymentCheckPaymentRowItem {

	private PaymentCheckPayment check;
	private String bank;
	private Date checkDate;
	private String checkNumber;
	private BigDecimal amount;

	public PaymentCheckPaymentRowItem(PaymentCheckPayment check) {
		this.check = check;
		bank = check.getBank();
		checkDate = check.getCheckDate();
		checkNumber = check.getCheckNumber();
		amount = check.getAmount();
	}
	
	public PaymentCheckPayment getCheck() {
		return check;
	}

	public void setCheck(PaymentCheckPayment check) {
		this.check = check;
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
		bank = check.getBank();
		checkNumber = check.getCheckNumber();
		amount = check.getAmount();
	}

	public boolean isValid() {
		return !StringUtils.isEmpty(bank) && !StringUtils.isEmpty(checkNumber) && amount != null;
	}

	public boolean isUpdating() {
		return check.getId() != null;
	}

	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}
	
}
