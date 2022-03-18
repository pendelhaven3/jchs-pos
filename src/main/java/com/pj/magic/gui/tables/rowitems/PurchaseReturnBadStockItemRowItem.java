package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.model.PurchaseReturnBadStockItem;

import lombok.Getter;
import lombok.Setter;

import com.pj.magic.model.Product2;

/*
 * Wrapper class to separate table gui concerns of inputting Bad Stock Return items
 * from the business logic of Bad Stock Return item model.
 */
@Getter
@Setter
public class PurchaseReturnBadStockItemRowItem {

	private PurchaseReturnBadStockItem item;
	private Product2 product;
	private String unit;
	private Integer quantity;
	private BigDecimal unitCost;
	
	public PurchaseReturnBadStockItemRowItem(PurchaseReturnBadStockItem item) {
		this.item = item;
		reset();
	}
	
	public String getProductCode() {
		return item.getCode();
	}

	public boolean isValid() {
		return product != null && !StringUtils.isEmpty(unit) && quantity != null;  
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
        if (!(obj instanceof PurchaseReturnBadStockItemRowItem)) {
            return false;
        }
        PurchaseReturnBadStockItemRowItem other = (PurchaseReturnBadStockItemRowItem)obj;		
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
			unitCost = item.getUnitCost();
		}
	}
	
}