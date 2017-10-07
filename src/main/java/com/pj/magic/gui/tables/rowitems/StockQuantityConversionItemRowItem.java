package com.pj.magic.gui.tables.rowitems;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.model.Product;
import com.pj.magic.model.StockQuantityConversionItem;

/*
 * Wrapper class to separate table gui concerns of inputting stock quantity conversion items
 * from the business logic of stock quantity conversion item model.
 */
public class StockQuantityConversionItemRowItem {

	private StockQuantityConversionItem item;
	private Product product;
	private String fromUnit;
	private String toUnit;
	private Integer quantity;

	public StockQuantityConversionItemRowItem(StockQuantityConversionItem item) {
		this.item = item;
		if (item.getProduct() != null) {
			product = item.getProduct();
		}
		fromUnit = item.getFromUnit();
		toUnit = item.getToUnit();
		quantity = item.getQuantity();
	}
	
	public StockQuantityConversionItem getItem() {
		return item;
	}

	public void setItem(StockQuantityConversionItem item) {
		this.item = item;
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
		return product != null && !StringUtils.isEmpty(fromUnit) && !StringUtils.isEmpty(toUnit)
				&& quantity != null;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(product)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof StockQuantityConversionItemRowItem)) {
            return false;
        }
        StockQuantityConversionItemRowItem other = (StockQuantityConversionItemRowItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.isEquals();
	}

	public String getProductDescription() {
		return (product != null) ? product.getDescription() : "";
	}

	public String getProductCode() {
		return (product != null) ? product.getCode() : "";
	}

	public String getFromUnit() {
		return fromUnit;
	}

	public void setFromUnit(String fromUnit) {
		this.fromUnit = fromUnit;
	}

	public String getToUnit() {
		return toUnit;
	}

	public void setToUnit(String toUnit) {
		this.toUnit = toUnit;
	}

	public boolean isUpdating() {
		return item.getId() != null;
	}

	public void reset() {
		if (item.getId() != null) {
			product = item.getProduct();
			fromUnit = item.getFromUnit();
			quantity = item.getQuantity();
			toUnit = item.getToUnit();
		}
	}
	
}
