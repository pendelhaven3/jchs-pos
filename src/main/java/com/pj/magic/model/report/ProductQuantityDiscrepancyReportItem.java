package com.pj.magic.model.report;

import com.pj.magic.model.Product;

public class ProductQuantityDiscrepancyReportItem {

	private Product product;
	private String unit;
	private int previousQuantity;
	private int quantityMoved;
	private int newQuantity;

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

	public int getPreviousQuantity() {
		return previousQuantity;
	}

	public void setPreviousQuantity(int previousQuantity) {
		this.previousQuantity = previousQuantity;
	}

	public int getQuantityMoved() {
		return quantityMoved;
	}

	public void setQuantityMoved(int quantityMoved) {
		this.quantityMoved = quantityMoved;
	}

	public int getNewQuantity() {
		return newQuantity;
	}

	public void setNewQuantity(int newQuantity) {
		this.newQuantity = newQuantity;
	}

	public int getDiscrepancy() {
		return newQuantity - (previousQuantity - quantityMoved);
	}

}
