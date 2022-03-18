package com.pj.magic.model;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseReturnBadStockItem {

	private Long id;
	private PurchaseReturnBadStock parent;
	private Product2 product;
	private String unit;
	private Integer quantity;
	private BigDecimal unitCost;
	
	private String code; // derived field

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
        if (!(obj instanceof PurchaseReturnBadStockItem)) {
            return false;
        }
        PurchaseReturnBadStockItem other = (PurchaseReturnBadStockItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.isEquals();
	}

	public BigDecimal getAmount() {
		if (quantity != null && unitCost != null) {
			return unitCost.multiply(new BigDecimal(quantity));
		}
		return null;
	}

}