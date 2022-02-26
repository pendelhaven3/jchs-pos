package com.pj.magic.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrisysSalesItem {

	private Long id;
	private TrisysSales sales;
	private String productCode;
	private String unit;
	private int quantity;
	private BigDecimal unitCost;
	private BigDecimal sellPrice;
	
	// derived fields
	private String productDescription;
	
	public BigDecimal getTotal() {
		return sellPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_EVEN);
	}
	
}
