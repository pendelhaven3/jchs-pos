package com.pj.magic.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
@Table(name = "PURCHASE_PAYMENT_ADJ_TYPE")
public class PurchasePaymentAdjustmentType {

	public static final String PURCHASE_RETURN_GOOD_STOCK_CODE = "RETURN - GS";
	public static final String PURCHASE_RETURN_BAD_STOCK_CODE = "RETURN - BS";
    public static final String EWT_CODE = "EWT";
    public static final String DISCOUNT_CODE = "DISC";
	
	@Id
	@GeneratedValue
	private Long id;

	private String code;
	private String description;

	public PurchasePaymentAdjustmentType() {
		// default constructor
	}
	
	public PurchasePaymentAdjustmentType(long id) {
		this.id = id;
	}
	
	public PurchasePaymentAdjustmentType(long id, String code) {
		this.id = id;
		this.code = code;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return code;
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
        if (!(obj instanceof PurchasePaymentAdjustmentType)) {
            return false;
        }
        PurchasePaymentAdjustmentType other = (PurchasePaymentAdjustmentType)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
}