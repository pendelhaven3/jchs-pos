package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.model.AdjustmentOutItem;
import com.pj.magic.model.Product2;

import lombok.Getter;
import lombok.Setter;

/*
 * Wrapper class to separate table gui concerns of inputting adjustment out items
 * from the business logic of adjustment out item model.
 */
@Getter
@Setter
public class AdjustmentOutItemRowItem {

	private AdjustmentOutItem item;
	private Product2 product;
	private String unit;
	private Integer quantity;

	public AdjustmentOutItemRowItem(AdjustmentOutItem item) {
		this.item = item;
		if (item.getProduct() != null) {
			product = item.getProduct();
		}
		unit = item.getUnit();
		if (item.getQuantity() != null) {
			quantity = item.getQuantity();
		}
	}
	
	public String getProductCode() {
		return item.getCode();
	}

	public boolean isValid() {
		return product != null && !StringUtils.isEmpty(unit) && quantity != null;  
	}

	public BigDecimal getCost() {
		BigDecimal cost = item.getCost();
		if (cost == null) {
			if (product != null && !StringUtils.isEmpty(unit)) {
				cost = product.getFinalCost(unit);
			}
		}
		return cost;
	}

	public BigDecimal getAmount() {
		return isValid() ? item.getAmount() : null;
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
        if (!(obj instanceof AdjustmentOutItemRowItem)) {
            return false;
        }
        AdjustmentOutItemRowItem other = (AdjustmentOutItemRowItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(unit, other.getUnit())
			.isEquals();
	}

	public boolean isUpdating() {
		return item.getId() != null;
	}

	public void reset() {
		if (item.getId() != null) {
			product = item.getProduct();
			unit = item.getUnit();
			quantity = item.getQuantity();
		}
	}

	public boolean hasValidProduct() {
		return product != null;
	}
	
	public boolean hasValidUnit() {
		return !StringUtils.isEmpty(unit);
	}

}
