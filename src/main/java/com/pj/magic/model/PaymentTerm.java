package com.pj.magic.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class PaymentTerm implements Serializable {

    private static final long serialVersionUID = -1020994733634274429L;
    
	private Long id;
	private String name;
	
	private int numberOfDays;

	public PaymentTerm() {
		// default constructor
	}
	
	public PaymentTerm(long id) {
		this.id = id;
	}
	
	public PaymentTerm(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumberOfDays() {
		return numberOfDays;
	}

	public void setNumberOfDays(int numberOfDays) {
		this.numberOfDays = numberOfDays;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof PaymentTerm)) {
            return false;
        }
        PaymentTerm other = (PaymentTerm)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
