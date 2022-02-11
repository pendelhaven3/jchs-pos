package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrisysSales {

	private Long id;
	private TrisysSalesImport salesImport;
	private String saleNumber;
	private String terminal;
	private Date salesDate;
	private List<TrisysSalesItem> items;
	
	public TrisysSales() { }
	
	public TrisysSales(Long id) {
		this.id = id;
	}

	public BigDecimal getTotalAmount() {
		return items.stream().map(item -> item.getTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
}
