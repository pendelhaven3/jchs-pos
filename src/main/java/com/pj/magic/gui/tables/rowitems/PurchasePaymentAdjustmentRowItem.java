package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;

import org.springframework.util.StringUtils;

import com.pj.magic.model.PurchasePaymentAdjustmentType;
import com.pj.magic.model.PurchasePaymentPaymentAdjustment;

public class PurchasePaymentAdjustmentRowItem {

	private PurchasePaymentPaymentAdjustment adjustment;
	private PurchasePaymentAdjustmentType adjustmentType;
	private String referenceNumber;
	private BigDecimal amount;

	public PurchasePaymentAdjustmentRowItem(PurchasePaymentPaymentAdjustment adjustment) {
		this.adjustment = adjustment;
		
		adjustmentType = adjustment.getAdjustmentType();
		referenceNumber = adjustment.getReferenceNumber();
		amount = adjustment.getAmount();
	}
	
	public PurchasePaymentPaymentAdjustment getPaymentAdjustment() {
		return adjustment;
	}

	public void setAdjustment(PurchasePaymentPaymentAdjustment adjustment) {
		this.adjustment = adjustment;
	}

	public PurchasePaymentAdjustmentType getAdjustmentType() {
		return adjustmentType;
	}

	public void setAdjustmentType(PurchasePaymentAdjustmentType adjustmentType) {
		this.adjustmentType = adjustmentType;
	}

	public PurchasePaymentPaymentAdjustment getAdjustment() {
		return adjustment;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void reset() {
		adjustmentType = adjustment.getAdjustmentType();
		referenceNumber = adjustment.getReferenceNumber();
		amount = adjustment.getAmount();
	}

	public boolean isValid() {
		return !StringUtils.isEmpty(adjustmentType) && amount != null;
	}

	public boolean isUpdating() {
		return adjustment.getId() != null;
	}

}