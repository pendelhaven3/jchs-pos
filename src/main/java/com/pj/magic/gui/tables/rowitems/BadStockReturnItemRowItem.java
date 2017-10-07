package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.model.BadStockReturnItem;
import com.pj.magic.model.Product;

/*
 * Wrapper class to separate table gui concerns of inputting Bad Stock Return items
 * from the business logic of Bad Stock Return item model.
 */
public class BadStockReturnItemRowItem {

	private BadStockReturnItem item;
	private Product product;
	private String unit;
	private Integer quantity;
	private BigDecimal unitPrice;
	private Long salesInvoiceNumber;

	public BadStockReturnItemRowItem(BadStockReturnItem item) {
		this.item = item;
		reset();
	}
	
	public BadStockReturnItem getItem() {
		return item;
	}

	public void setItem(BadStockReturnItem item) {
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
	
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	public BigDecimal getUnitPrice() {
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
        if (!(obj instanceof BadStockReturnItemRowItem)) {
            return false;
        }
        BadStockReturnItemRowItem other = (BadStockReturnItemRowItem)obj;		
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
			unitPrice = item.getUnitPrice();
			salesInvoiceNumber = item.getSalesInvoiceNumber();
		}
	}

	public Long getSalesInvoiceNumber() {
		return salesInvoiceNumber;
	}

	public void setSalesInvoiceNumber(Long salesInvoiceNumber) {
		this.salesInvoiceNumber = salesInvoiceNumber;
	}
	
}