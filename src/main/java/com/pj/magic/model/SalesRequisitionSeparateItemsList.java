package com.pj.magic.model;

import java.util.ArrayList;
import java.util.List;

public class SalesRequisitionSeparateItemsList {

	private List<Product> products = new ArrayList<>();

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public boolean isIncluded(SalesRequisitionItem item) {
		return item.getUnit().equals(Unit.CASE) || products.contains(item.getProduct());
	}
	
}
