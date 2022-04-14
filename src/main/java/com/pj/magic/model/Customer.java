package com.pj.magic.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Customer implements Serializable {

    private static final long serialVersionUID = 4994363059129213047L;
    
	private Long id;
	private String code;
	private String name;

	private String businessAddress;

	private String deliveryAddress;

	private String contactPerson;

	private String contactNumber;

	private String tin;

	private BigDecimal approvedCreditLine;

	private PaymentTerm paymentTerm;

	private String businessType;

	private String owners;

	private String bankReferences;

	private Boolean hold = false;
	
	private Boolean active = false;

	private String remarks;

	public Customer() {
	}

	public Customer(Long id) {
		this.id = id;
	}

	public Customer(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBusinessAddress() {
		return businessAddress;
	}

	public void setBusinessAddress(String businessAddress) {
		this.businessAddress = businessAddress;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(code).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Customer)) {
			return false;
		}
		Customer other = (Customer) obj;
		return new EqualsBuilder().append(code, other.getCode()).isEquals();
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public PaymentTerm getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(PaymentTerm paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public String getTin() {
		return tin;
	}

	public void setTin(String tin) {
		this.tin = tin;
	}

	public BigDecimal getApprovedCreditLine() {
		return approvedCreditLine;
	}

	public void setApprovedCreditLine(BigDecimal approvedCreditLine) {
		this.approvedCreditLine = approvedCreditLine;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getOwners() {
		return owners;
	}

	public void setOwners(String owners) {
		this.owners = owners;
	}

	public String getBankReferences() {
		return bankReferences;
	}

	public void setBankReferences(String bankReferences) {
		this.bankReferences = bankReferences;
	}

	public Boolean getHold() {
		return hold;
	}

	public void setHold(Boolean hold) {
		this.hold = hold;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public boolean isOnHold() {
		return hold;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public boolean isActive() {
		return active;
	}
	
}