package com.pj.magic.model;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.pj.magic.Constants;
import com.pj.magic.util.FormatterUtil;

public class PromoType1Rule implements Cloneable {

	private Long id;
	private Promo parent;
	private BigDecimal targetAmount;
	private Manufacturer manufacturer;
	private Product product;
	private String unit;
	private Integer quantity;
	private int dailyRedeemLimitPerCustomer;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Promo getParent() {
		return parent;
	}

	public void setParent(Promo parent) {
		this.parent = parent;
	}

	public BigDecimal getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(BigDecimal targetAmount) {
		this.targetAmount = targetAmount;
	}

	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
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

	public boolean isNew() {
		return id == null;
	}

	public PromoRedemptionReward evaluate(List<SalesInvoice> salesInvoices) {
		BigDecimal total = Constants.ZERO;
		for (SalesInvoice salesInvoice : salesInvoices) {
			total = total.add(salesInvoice.getSalesByManufacturer(manufacturer));
		}
		
		if (total.compareTo(targetAmount) != -1) {
			PromoRedemptionReward reward = new PromoRedemptionReward();
			reward.setProduct(product);
			reward.setUnit(unit);
			reward.setQuantity(total.divideToIntegralValue(targetAmount).intValue() * quantity);
			
			if (dailyRedeemLimitPerCustomer > 0 && reward.getQuantity() > dailyRedeemLimitPerCustomer) {
				reward.setQuantity(dailyRedeemLimitPerCustomer);
			}
			
			return reward;
		} else {
			return null;
		}
	}

	public BigDecimal getQualifyingAmount(SalesRequisition salesRequisition) {
		return salesRequisition.getSalesByManufacturer(manufacturer);
	}

	public BigDecimal getQualifyingAmount(SalesInvoice salesInvoice) {
		return salesInvoice.getSalesByManufacturer(manufacturer);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		PromoType1Rule clone = new PromoType1Rule();
		BeanUtils.copyProperties(this, clone);
		return clone;
	}
	
	public String getMechanicsDescription() {
		String mechanics = "Buy {0} worth of participating products, get {1} {2} {3} free {4}";
		return MessageFormat.format(mechanics, 
				FormatterUtil.formatAmount(targetAmount),
				String.valueOf(quantity), 
				unit, 
				product.getDescription(),
				(dailyRedeemLimitPerCustomer > 0 ? "(up to " + dailyRedeemLimitPerCustomer + " times only per day per customer)" : "")
		);
	}

	public int getDailyRedeemLimitPerCustomer() {
		return dailyRedeemLimitPerCustomer;
	}

	public void setDailyRedeemLimitPerCustomer(int dailyRedeemLimitPerCustomer) {
		this.dailyRedeemLimitPerCustomer = dailyRedeemLimitPerCustomer;
	}

}