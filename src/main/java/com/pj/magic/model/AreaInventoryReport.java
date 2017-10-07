package com.pj.magic.model;

import java.util.ArrayList;
import java.util.List;

public class AreaInventoryReport {

	private Long id;
	private InventoryCheck parent;
	private Integer reportNumber;
	private Area area;
	private String checker;
	private String doubleChecker;
	private User createdBy;
	private boolean reviewed;
	private String reviewer;
	private List<AreaInventoryReportItem> items = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public InventoryCheck getParent() {
		return parent;
	}

	public void setParent(InventoryCheck parent) {
		this.parent = parent;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String getChecker() {
		return checker;
	}

	public void setChecker(String checker) {
		this.checker = checker;
	}

	public String getDoubleChecker() {
		return doubleChecker;
	}

	public void setDoubleChecker(String doubleChecker) {
		this.doubleChecker = doubleChecker;
	}

	public List<AreaInventoryReportItem> getItems() {
		return items;
	}

	public void setItems(List<AreaInventoryReportItem> items) {
		this.items = items;
	}

	public boolean hasItems() {
		return !items.isEmpty();
	}

	public Integer getReportNumber() {
		return reportNumber;
	}

	public void setReportNumber(Integer reportNumber) {
		this.reportNumber = reportNumber;
	}
	
	public int getTotalNumberOfItems() {
		return items.size();
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public boolean isReviewed() {
		return reviewed;
	}

	public void setReviewed(boolean reviewed) {
		this.reviewed = reviewed;
	}
	
	public String getStatus() {
		return (reviewed) ? "Reviewed" : "New";
	}

	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}
	
}