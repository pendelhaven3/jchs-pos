package com.pj.magic.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
@Table(name = "ADJUSTMENT_TYPE")
public class AdjustmentType {

	public static final String SALES_RETURN_CODE = "SR";
	public static final String BAD_STOCK_RETURN_CODE = "BSR";
	public static final String NO_MORE_STOCK_ADJUSTMENT_CODE = "NMS";
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String code;
	private String description;

	public AdjustmentType() {
		// default constructor
	}
	
	public AdjustmentType(Long id) {
		this.id = id;
	}

	public AdjustmentType(Long id, String code) {
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
        if (!(obj instanceof AdjustmentType)) {
            return false;
        }
        AdjustmentType other = (AdjustmentType)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
	@Override
	public String toString() {
		return code;
	}
	
}