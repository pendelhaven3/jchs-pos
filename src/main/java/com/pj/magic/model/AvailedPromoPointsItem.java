package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author PJ Miranda
 *
 */
public class AvailedPromoPointsItem {

	private Promo promo;
	private Long salesInvoiceNumber;
	private Date transactionDate;
	private BigDecimal qualifyingAmount;
	private BigDecimal adjustedAmount;
	private int points;
	
	public Promo getPromo() {
		return promo;
	}

	public void setPromo(Promo promo) {
		this.promo = promo;
	}

	public Long getSalesInvoiceNumber() {
		return salesInvoiceNumber;
	}

	public void setSalesInvoiceNumber(Long salesInvoiceNumber) {
		this.salesInvoiceNumber = salesInvoiceNumber;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public BigDecimal getQualifyingAmount() {
		return qualifyingAmount;
	}

	public void setQualifyingAmount(BigDecimal qualifyingAmount) {
		this.qualifyingAmount = qualifyingAmount;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public boolean hasPoints() {
		return points > 0;
	}

	public BigDecimal getAdjustedAmount() {
		return adjustedAmount;
	}

	public void setAdjustedAmount(BigDecimal adjustedAmount) {
		this.adjustedAmount = adjustedAmount;
	}

}