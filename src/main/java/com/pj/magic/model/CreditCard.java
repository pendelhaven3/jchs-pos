package com.pj.magic.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class CreditCard {

	private Long id;
	
	private String user;
	private String bank;
	
	private String cardNumber;

	private String customerNumber;
	
	public CreditCard() {
		// default constructor
	}
	
	public CreditCard(long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof CreditCard)) {
            return false;
        }
        CreditCard other = (CreditCard)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
	@Override
	public String toString() {
		return user + " - " + bank;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

}