package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.util.StringUtils;

import com.pj.magic.model.Product;
import com.pj.magic.model.PurchaseOrderItem;

/*
 * Wrapper class to separate table gui concerns of inputting purchase order items
 * from the business logic of purchase order item model.
 */
public class PurchaseOrderItemRowItem {

	private PurchaseOrderItem item;
	private String unit;
	private Integer quantity;
	private BigDecimal cost;
	private Product product;
	private Integer actualQuantity;

	public PurchaseOrderItemRowItem(PurchaseOrderItem item) {
		this.item = item;
		if (item.getProduct() != null) {
			product = item.getProduct();
		}
		unit = item.getUnit();
		if (item.getQuantity() != null) {
			quantity = item.getQuantity();
		}
		if (item.getActualQuantity() != null) {
			actualQuantity = item.getActualQuantity();
		}
		if (item.getCost() != null) {
			cost = item.getCost();
		}
	}
	
	public PurchaseOrderItem getItem() {
		return item;
	}

	public void setItem(PurchaseOrderItem item) {
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

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public boolean isValid() {
		return product != null && product.hasUnit(unit) && quantity != null;
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
        if (!(obj instanceof PurchaseOrderItemRowItem)) {
            return false;
        }
        PurchaseOrderItemRowItem other = (PurchaseOrderItemRowItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(unit, other.getUnit())
			.isEquals();
	}

	public Integer getActualQuantity() {
		return actualQuantity;
	}

	public void setActualQuantity(Integer actualQuantity) {
		this.actualQuantity = actualQuantity;
	}

	public BigDecimal getAmount() {
		return item.getAmount();
	}

	public boolean isUpdating() {
		return item.getId() != null;
	}

	public void reset() {
		if (item.getId() != null) {
			product = item.getProduct();
			unit = item.getUnit();
			quantity = item.getQuantity();
			cost = item.getCost();
			actualQuantity = item.getActualQuantity();
		}
	}

	public String getProductCode() {
		return product != null ? product.getCode() : null;
	}

	public String getProductDescription() {
		return product != null ? product.getDescription() : null;
	}

	public String getSuggestedOrder() {
		if (product != null && !StringUtils.isEmpty(unit)) {
			return String.valueOf(product.getSuggestedOrder(unit));
		} else {
			return null;
		}
	}
	
}
