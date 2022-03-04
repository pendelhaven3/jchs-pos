package com.pj.magic.gui.tables.rowitems;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.model.AreaInventoryReportItem;
import com.pj.magic.model.Product2;

import lombok.Getter;
import lombok.Setter;

/*
 * Wrapper class to separate table gui concerns of inputting adjustment in items
 * from the business logic of adjustment in item model.
 */
@Getter
@Setter
public class AreaInventoryReportItemRowItem {

	private AreaInventoryReportItem item;
	private Product2 product;
	private String unit;
	private Integer quantity;

	public AreaInventoryReportItemRowItem(AreaInventoryReportItem item) {
		this.item = item;
		if (item.getProduct() != null) {
			product = item.getProduct();
		}
		unit = item.getUnit();
		if (item.getQuantity() != null) {
			quantity = item.getQuantity();
		}
	}
	
	public boolean isValid() {
		return product != null && !StringUtils.isEmpty(unit) && quantity != null;
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
        if (!(obj instanceof AreaInventoryReportItemRowItem)) {
            return false;
        }
        AreaInventoryReportItemRowItem other = (AreaInventoryReportItemRowItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(unit, other.getUnit())
			.isEquals();
	}

	public String getProductDescription() {
		return (product != null) ? product.getDescription() : null;
	}

	public String getProductCode() {
		return item.getCode();
	}

	public boolean isUpdating() {
		return item.getId() != null;
	}

	public boolean hasValidProduct() {
		return product != null;
	}

	public boolean hasValidUnit() {
		return !StringUtils.isEmpty(unit);
	}

	public void reset() {
		if (item.getId() != null) {
			product = item.getProduct();
			unit = item.getUnit();
			quantity = item.getQuantity();
		}
	}
	
}
