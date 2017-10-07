package com.pj.magic.model;

import java.util.Date;

public class PromoPointsClaim {

	private Long id;
	private Promo promo;
	private Long claimNumber;
	private Customer customer;
	private int points;
	private String remarks;
	private Date claimDate;
	private User claimBy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Promo getPromo() {
		return promo;
	}

	public void setPromo(Promo promo) {
		this.promo = promo;
	}

	public Long getClaimNumber() {
		return claimNumber;
	}

	public void setClaimNumber(Long claimNumber) {
		this.claimNumber = claimNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getClaimDate() {
		return claimDate;
	}

	public void setClaimDate(Date claimDate) {
		this.claimDate = claimDate;
	}

	public User getClaimBy() {
		return claimBy;
	}

	public void setClaimBy(User claimBy) {
		this.claimBy = claimBy;
	}

	public boolean isNew() {
		return id == null;
	}

}