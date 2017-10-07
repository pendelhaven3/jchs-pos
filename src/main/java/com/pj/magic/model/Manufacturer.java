package com.pj.magic.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Manufacturer implements Comparable<Manufacturer>, Serializable {

    private static final long serialVersionUID = 1937601133886009684L;
    
    private Long id;
	private String name;

	public Manufacturer() {
		// default constructor
	}
	
	public Manufacturer(long id) {
		this.id = id;
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
        if (!(obj instanceof Manufacturer)) {
            return false;
        }
        Manufacturer other = (Manufacturer)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(Manufacturer o) {
		return name.compareTo(o.getName());
	}
	
}