package com.pj.magic.model;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdjustmentOutItem implements Comparable<AdjustmentOutItem> {

	private Long id;
	private AdjustmentOut parent;
	private Product2 product;
	private String unit;
	private Integer quantity;
	private BigDecimal cost;
	
	private String code; // derived field

	public BigDecimal getEffectiveCost() {
		BigDecimal cost = this.cost;
		if (cost == null) {
			cost = product.getFinalCost(unit);
		}
		return cost;
	}
	
	public BigDecimal getAmount() {
		if (product == null || quantity == null) {
			return null;
		}
		return getEffectiveCost().multiply(new BigDecimal(quantity.intValue()));
	}
	
	public boolean isQuantityValid() {
		return product.hasAvailableUnitQuantity(unit, quantity);
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
        if (!(obj instanceof AdjustmentOutItem)) {
            return false;
        }
        AdjustmentOutItem other = (AdjustmentOutItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(unit, other.getUnit())
			.isEquals();
	}

	@Override
	public int compareTo(AdjustmentOutItem o) {
		int result = product.compareTo(o.getProduct());
		if (result == 0) {
			return Unit.compare(unit, o.getUnit());
		} else {
			return result;
		}
	}

}
