package com.pj.magic.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.Constants;
import com.pj.magic.model.util.Percentage;

public class SalesInvoiceItem implements Comparable<SalesInvoiceItem>, Serializable {

    private static final long serialVersionUID = -5385600090647746687L;
    
    private Long id;
	private SalesInvoice parent;
	private Product product;
	private String unit;
	private Integer quantity;
	private BigDecimal unitPrice;
	private BigDecimal discount1 = BigDecimal.ZERO;
	private BigDecimal discount2 = BigDecimal.ZERO; 
	private BigDecimal discount3 = BigDecimal.ZERO;
	private BigDecimal flatRateDiscount = BigDecimal.ZERO;
	private BigDecimal cost;

	public SalesInvoiceItem() {
		// default constructor
	}
	
	public SalesInvoiceItem(Long id) {
		this.id = id;
	}
	
	public SalesInvoiceItem(SalesInvoiceItem item) {
		product = item.getProduct();
		unit = item.getUnit();
		quantity = item.getQuantity();
		unitPrice = item.getUnitPrice();
		discount1 = item.getDiscount1();
		discount2 = item.getDiscount2();
		discount3 = item.getDiscount3();
		flatRateDiscount = item.getFlatRateDiscount();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SalesInvoice getParent() {
		return parent;
	}

	public void setParent(SalesInvoice parent) {
		this.parent = parent;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
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

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getAmount() {
		return unitPrice.multiply(new BigDecimal(quantity)).setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public int compareTo(SalesInvoiceItem o) {
		int result = product.compareTo(o.getProduct());
		if (result == 0) {
			return Unit.compare(unit, o.getUnit());
		} else {
			return result;
		}
	}

	public BigDecimal getDiscount1() {
		return discount1;
	}

	public void setDiscount1(BigDecimal discount1) {
		this.discount1 = discount1;
	}

	public BigDecimal getDiscount2() {
		return discount2;
	}

	public void setDiscount2(BigDecimal discount2) {
		this.discount2 = discount2;
	}

	public BigDecimal getDiscount3() {
		return discount3;
	}

	public void setDiscount3(BigDecimal discount3) {
		this.discount3 = discount3;
	}

	public BigDecimal getFlatRateDiscount() {
		return flatRateDiscount;
	}

	public void setFlatRateDiscount(BigDecimal flatRateDiscount) {
		this.flatRateDiscount = flatRateDiscount;
	}

	// TODO: Rename to discountAmount
	public BigDecimal getDiscountedAmount() {
		return getAmount().subtract(getNetAmount());
	}

	public BigDecimal getNetAmount() {
		BigDecimal netAmount = getAmount();
		if (discount1.compareTo(Constants.ZERO) != 0) {
			netAmount = netAmount.subtract(netAmount.multiply(new Percentage(discount1).toDecimal()));
		}
		if (discount2.compareTo(Constants.ZERO) != 0) {
			netAmount = netAmount.subtract(netAmount.multiply(new Percentage(discount2).toDecimal()));
		}
		if (discount3.compareTo(Constants.ZERO) != 0) {
			netAmount = netAmount.subtract(netAmount.multiply(new Percentage(discount3).toDecimal()));
		}
		if (flatRateDiscount.compareTo(Constants.ZERO) != 0) {
			netAmount = netAmount.subtract(flatRateDiscount);
		}
		return netAmount;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof SalesInvoiceItem)) {
            return false;
        }
        SalesInvoiceItem other = (SalesInvoiceItem)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getNetCost() {
		return cost.multiply(new BigDecimal(quantity));
	}
	
	public BigDecimal getDiscountedUnitPrice() {
		BigDecimal price = unitPrice;
		if (discount1.compareTo(Constants.ZERO) != 0) {
			price = price.subtract(price.multiply(new Percentage(discount1).toDecimal()));
		}
		if (discount2.compareTo(Constants.ZERO) != 0) {
			price = price.subtract(price.multiply(new Percentage(discount2).toDecimal()));
		}
		if (discount3.compareTo(Constants.ZERO) != 0) {
			price = price.subtract(price.multiply(new Percentage(discount3).toDecimal()));
		}
		if (flatRateDiscount.compareTo(Constants.ZERO) != 0) {
			price = price.subtract(flatRateDiscount.divide(new BigDecimal(quantity), 2, RoundingMode.HALF_UP));
		}
		return price;
	}

	public BigDecimal getNetPrice() {
		return getDiscountedUnitPrice();
	}
	
	public boolean hasNetPriceLessThanCost() {
		return getNetPrice().compareTo(cost) < 0;
	}
	
}