package com.pj.magic.model;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseOrderItem implements Comparable<PurchaseOrderItem> {

	private Long id;
	private PurchaseOrder parent;
	private Product2 product;
	private String unit;
	private Integer quantity;
	private BigDecimal cost;
	private BigDecimal vatInclusiveCost;
	private Integer actualQuantity;
	private boolean ordered;
	
	private String code; // derived field
	private String customCode; // derived field

	public BigDecimal getAmount() {
		if (actualQuantity != null) {
			return cost.multiply(new BigDecimal(actualQuantity.intValue()));
		} else {
			return cost.multiply(new BigDecimal(quantity.intValue()));
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(product)
			.append(unit)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof PurchaseOrderItem)) {
            return false;
        }
        PurchaseOrderItem other = (PurchaseOrderItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(unit, other.getUnit())
			.isEquals();
	}

	@Override
	public int compareTo(PurchaseOrderItem o) {
		int result = product.compareTo(o.getProduct());
		if (result == 0) {
			return Unit.compare(unit, o.getUnit());
		} else {
			return result;
		}
	}

}
