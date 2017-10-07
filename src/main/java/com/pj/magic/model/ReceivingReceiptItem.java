package com.pj.magic.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.pj.magic.Constants;
import com.pj.magic.model.util.Percentage;

public class ReceivingReceiptItem implements Comparable<ReceivingReceiptItem> {

	private Long id;
	private ReceivingReceipt parent;
	private Product product;
	private String unit;
	private Integer quantity;
	private BigDecimal cost;
	private BigDecimal discount1 = BigDecimal.ZERO;
	private BigDecimal discount2 = BigDecimal.ZERO; 
	private BigDecimal discount3 = BigDecimal.ZERO;
	private BigDecimal flatRateDiscount = BigDecimal.ZERO;
	private BigDecimal currentCost;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ReceivingReceipt getParent() {
		return parent;
	}

	public void setParent(ReceivingReceipt parent) {
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

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getAmount() {
		return cost.multiply(new BigDecimal(quantity.intValue()));
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

	public BigDecimal getFinalCost() {
		return getNetAmount().divide(new BigDecimal(quantity), 2, RoundingMode.HALF_UP);
	}

	@Override
	public int compareTo(ReceivingReceiptItem o) {
		int result = product.compareTo(o.getProduct());
		if (result == 0) {
			return Unit.compare(unit, o.getUnit());
		} else {
			return result;
		}
	}

	public BigDecimal getCurrentCost() {
		return currentCost;
	}

	public void setCurrentCost(BigDecimal currentCost) {
		this.currentCost = currentCost;
	}
	
	public BigDecimal getFinalCostWithVat() {
		return getFinalCost().multiply(parent.getVatMultiplier());
	}
	
}