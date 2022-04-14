package com.pj.magic.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class PaymentTerminal {

	private Long id;
	
	private String name;

	public PaymentTerminal() {
		// default constructor
	}
	
	public PaymentTerminal(Long id, String name) {
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
        if (!(obj instanceof PaymentTerminal)) {
            return false;
        }
        PaymentTerminal other = (PaymentTerminal)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}