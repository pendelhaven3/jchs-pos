package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Product;

/**
 * 
 * @author PJ Miranda
 *
 */
public class ProductCanvassSearchCriteria {

	private Product product;
	private Date dateFrom;
	private Date dateTo;

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

}