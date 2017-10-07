package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.model.AdjustmentOutItem;
import com.pj.magic.model.Product;

/*
 * Wrapper class to separate table gui concerns of inputting adjustment out items
 * from the business logic of adjustment out item model.
 */
public class AdjustmentOutItemRowItem {

	private AdjustmentOutItem item;
	private Product product;
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
	
	public AdjustmentOutItem getItem() {
		return item;
	}

	public void setItem(AdjustmentOutItem item) {
		this.item = item;
	}

	public String getProductCode() {
		return (product != null) ? product.getCode() : null;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public boolean isValid() {
		return product != null && !StringUtils.isEmpty(unit) && quantity != null;  
	}

	public BigDecimal getUnitPrice() {
		BigDecimal unitPrice = item.getUnitPrice();
		if (unitPrice == null) {
			if (product != null && !StringUtils.isEmpty(unit)) {
				unitPrice = product.getUnitPrice(unit);
			}
		}
		return unitPrice;
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
