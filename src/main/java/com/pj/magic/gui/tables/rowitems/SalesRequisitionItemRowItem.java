package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.model.Product;
import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.util.FormatterUtil;

/*
 * Wrapper class to separate table gui concerns of inputting sales requisition items
 * from the business logic of sales requisition item model.
 */
public class SalesRequisitionItemRowItem {

	private SalesRequisitionItem item;
	private String unit;
	private String quantity; // TODO: change to integer
	private Product product;

	public SalesRequisitionItemRowItem(SalesRequisitionItem item) {
		this.item = item;
		if (item.getProduct() != null) {
			product = item.getProduct();
		}
		unit = item.getUnit();
		if (item.getQuantity() != null) {
			quantity = item.getQuantity().toString();
		}
	}
	
	public SalesRequisitionItem getItem() {
		return item;
	}

	public void setItem(SalesRequisitionItem item) {
		this.item = item;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public boolean isValid() {
		return product != null && product.hasUnit(unit) 
				&& !StringUtils.isEmpty(quantity) && StringUtils.isNumeric(quantity);
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
        if (!(obj instanceof SalesRequisitionItemRowItem)) {
            return false;
        }
        SalesRequisitionItemRowItem other = (SalesRequisitionItemRowItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(unit, other.getUnit())
			.isEquals();
	}

	public int getQuantityAsInt() {
		return Integer.parseInt(quantity);
	}
	
	public BigDecimal getAmount() {
		return item.getAmount();
	}

	public String getProductDescription() {
		return (product != null) ? product.getDescription() : "";
	}

	public String getUnitPrice() {
		return (product != null && product.hasUnit(unit)) ? 
				FormatterUtil.formatAmount(product.getUnitPrice(unit)) : "";
	}

	public void reset() {
		if (item.getId() != null) {
			product = item.getProduct();
			unit = item.getUnit();
			quantity = item.getQuantity().toString();
		}
	}
	
	public boolean hasValidProduct() {
		return product != null;
	}
	
	public boolean hasValidUnit() {
		return hasValidProduct() && product.hasUnit(unit) && !product.hasNoSellingPrice(unit);
	}

	public String getProductCode() {
		return (product != null) ? product.getCode() : "";
	}

	public boolean isUpdating() {
		return item.getId() != null;
	}
	
}
