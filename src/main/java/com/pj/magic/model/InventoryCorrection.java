package com.pj.magic.model;

import java.util.Date;

public class InventoryCorrection {

	private Long id;
	private Long inventoryCorrectionNumber;
	private Date postDate;
	private Product product;
	private String unit;
	private Integer quantity;
	private Date updateDate;
	private User updatedBy;
	private String remarks;
	private boolean deleted;

	public boolean isNew() {
		return id == null;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getInventoryCorrectionNumber() {
		return inventoryCorrectionNumber;
	}

	public void setInventoryCorrectionNumber(Long inventoryCorrectionNumber) {
		this.inventoryCorrectionNumber = inventoryCorrectionNumber;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
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

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
