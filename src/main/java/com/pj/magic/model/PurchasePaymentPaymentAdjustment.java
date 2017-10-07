package com.pj.magic.model;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class PurchasePaymentPaymentAdjustment {

	private Long id;
	private PurchasePayment parent;
	private PurchasePaymentAdjustmentType adjustmentType;
	private BigDecimal amount;
	private String referenceNumber;

	public PurchasePaymentPaymentAdjustment() {
		// default constructor
	}
	
	public PurchasePaymentPaymentAdjustment(long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PurchasePayment getParent() {
		return parent;
	}

	public void setParent(PurchasePayment parent) {
		this.parent = parent;
	}

	public PurchasePaymentAdjustmentType getAdjustmentType() {
		return adjustmentType;
	}

	public void setAdjustmentType(PurchasePaymentAdjustmentType adjustmentType) {
		this.adjustmentType = adjustmentType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof PurchasePaymentPaymentAdjustment)) {
            return false;
        }
        PurchasePaymentPaymentAdjustment other = (PurchasePaymentPaymentAdjustment)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
}