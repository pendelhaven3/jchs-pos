package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.model.Product2;
import com.pj.magic.model.PurchaseReturnItem;

import lombok.Getter;
import lombok.Setter;

/*
 * Wrapper class to separate table gui concerns of inputting purchase return items
 * from the business logic of purchase return item model.
 */
@Getter
@Setter
public class PurchaseReturnItemRowItem {

	private PurchaseReturnItem item;
	private String unit;
	private Integer quantity;
	private Product2 product;

	public PurchaseReturnItemRowItem(PurchaseReturnItem item) {
		this.item = item;
		reset();
	}
	
	public boolean isValid() {
		return product != null && quantity != null;
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
        if (!(obj instanceof PurchaseReturnItemRowItem)) {
            return false;
        }
        PurchaseReturnItemRowItem other = (PurchaseReturnItemRowItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(unit, other.getUnit())
			.isEquals();
	}

	public BigDecimal getAmount() {
		return item.getAmount();
	}

	public String getProductDescription() {
		return (product != null) ? product.getDescription() : "";
	}

	public void reset() {
		if (item.getId() != null) {
			product = item.getReceivingReceiptItem().getProduct();
			unit = item.getReceivingReceiptItem().getUnit();
			quantity = item.getQuantity();
		}
	}
	
	public boolean hasValidProduct() {
		return product != null;
	}
	
	public boolean hasValidUnit() {
		return hasValidProduct() && product.hasUnit(unit);
	}

	public String getProductCode() {
		return item.getCode();
	}

	public boolean isUpdating() {
		return item.getId() != null;
	}
	
	public BigDecimal getUnitCost() {
		return item.getUnitCost();
	}
	
}