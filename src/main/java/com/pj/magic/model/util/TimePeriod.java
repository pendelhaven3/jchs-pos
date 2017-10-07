package com.pj.magic.model.util;

public enum TimePeriod {

	MORNING_ONLY("MORNING ONLY"), AFTERNOON_ONLY("AFTERNOON ONLY");

	private String description;
	
	private TimePeriod(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
}