package com.pj.magic.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
@Table(name="PAYMENT_TERM")
public class PaymentTerm implements Serializable {

    private static final long serialVersionUID = -1020994733634274429L;
    
    @Id
	@GeneratedValue
	private Long id;
	private String name;
	
	@Column(name="NUMBER_OF_DAYS")
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
