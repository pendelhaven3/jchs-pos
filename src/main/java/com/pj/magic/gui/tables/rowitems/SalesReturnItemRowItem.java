package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.model.Product;
import com.pj.magic.model.SalesReturnItem;

/*
 * Wrapper class to separate table gui concerns of inputting sales return items
 * from the business logic of sales return item model.
 */
public class SalesReturnItemRowItem {

	private SalesReturnItem item;
	private String unit;
	private Integer quantity;
	private Product product;

	public SalesReturnItemRowItem(SalesReturnItem item) {
		this.item = item;
		reset();
	}
	
	public SalesReturnItem getItem() {
		return item;
	}

	public void setItem(SalesReturnItem item) {
		this.item = item;
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
        if (!(obj instanceof SalesReturnItemRowItem)) {
            return false;
        }
        SalesReturnItemRowItem other = (SalesReturnItemRowItem)obj;		
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
			product = item.getSalesInvoiceItem().getProduct();
			unit = item.getSalesInvoiceItem().getUnit();
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
		return (product != null) ? product.getCode() : "";
	}

	public boolean isUpdating() {
		return item.getId() != null;
	}
	
	public BigDecimal getUnitPrice() {
		return item.getUnitPrice();
	}
	
}