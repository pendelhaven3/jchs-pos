package com.pj.magic.model.search;

import org.apache.commons.lang.StringUtils;


public class InventoryCheckSearchCriteria {
	
	private String codeOrDescriptionLike;
	private Boolean withDiscrepancy;

	public String getCodeOrDescriptionLike() {
		return codeOrDescriptionLike;
	}

	public void setCodeOrDescriptionLike(String codeOrDescriptionLike) {
		this.codeOrDescriptionLike = codeOrDescriptionLike;
	}

	public Boolean getWithDiscrepancy() {
		return withDiscrepancy;
	}

	public void setWithDiscrepancy(Boolean withDiscrepancy) {
		this.withDiscrepancy = withDiscrepancy;
	}

	public boolean isEmpty() {
		return StringUtils.isEmpty(codeOrDescriptionLike) && withDiscrepancy == null;
	}
	
}
