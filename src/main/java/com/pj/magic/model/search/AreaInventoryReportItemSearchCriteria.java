package com.pj.magic.model.search;

import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.Product2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AreaInventoryReportItemSearchCriteria {

	private InventoryCheck inventoryCheck;
	private Product2 product;
	private String unit;

}
