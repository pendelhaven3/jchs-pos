package com.pj.magic.model.search;

import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Supplier;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryReportCriteria {

	private String codeOrDescriptionLike;
	private Supplier supplier;
	private Manufacturer manufacturer;

}
