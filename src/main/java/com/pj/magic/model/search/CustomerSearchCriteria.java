package com.pj.magic.model.search;

public class CustomerSearchCriteria {

	private String nameLike;
	private Boolean active;

	public String getNameLike() {
		return nameLike;
	}

	public void setNameLike(String name) {
		this.nameLike = name;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
}