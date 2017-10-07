package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Product;

public class PilferageReportCriteria {

	private Product product;
	private Date from;
	private Date to;

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

}
