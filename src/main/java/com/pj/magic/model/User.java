package com.pj.magic.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class User implements Serializable {

    private static final long serialVersionUID = -8624069925842088821L;
    
    private Long id;
	private String username;
	private String password;
	private boolean supervisor;
	private String plainPassword; // not stored in database
	private boolean modifyPricing;

	public User() {
		// default constructor
	}
	
	public User(long id) {
		this.id = id;
	}
	
	public User(Long id, String username) {
		this.id = id;
		this.username = username;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
        if (!(obj instanceof User)) {
            return false;
        }
        User other = (User)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}

	public boolean isSupervisor() {
		return supervisor;
	}

	public void setSupervisor(boolean supervisor) {
		this.supervisor = supervisor;
	}

	public String getPlainPassword() {
		return plainPassword;
	}

	public void setPlainPassword(String plainPassword) {
		this.plainPassword = plainPassword;
	}

	@Override
	public String toString() {
		return username;
	}

	public boolean isModifyPricing() {
		return modifyPricing;
	}

	public void setModifyPricing(boolean modifyPricing) {
		this.modifyPricing = modifyPricing;
	}

	public boolean canModifyPricing() {
		return supervisor || modifyPricing;
	}
	
}
