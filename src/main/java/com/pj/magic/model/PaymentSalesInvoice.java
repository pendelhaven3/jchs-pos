package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.Constants;

public class PaymentSalesInvoice {

	private Long id;
	private Payment parent;
	private SalesInvoice salesInvoice;
	private BigDecimal adjustedAmount;
	private List<SalesReturn> salesReturns = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Payment getParent() {
		return parent;
	}

	public void setParent(Payment parent) {
		this.parent = parent;
	}

	public SalesInvoice getSalesInvoice() {
		return salesInvoice;
	}

	public void setSalesInvoice(SalesInvoice salesInvoice) {
		this.salesInvoice = salesInvoice;
	}

	public BigDecimal getAdjustedAmount() {
		if (adjustedAmount != null) {
			return adjustedAmount;
		}
		
		BigDecimal amount = Constants.ZERO;
		for (SalesReturn salesReturn : salesReturns) {
			amount = amount.add(salesReturn.getTotalAmount());
		}
		return amount;
	}

	public BigDecimal getAmountDue() {
		return getSalesInvoice().getTotalNetAmount().subtract(getAdjustedAmount());
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
        if (!(obj instanceof PaymentSalesInvoice)) {
            return false;
        }
        PaymentSalesInvoice other = (PaymentSalesInvoice)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}

	public List<SalesReturn> getSalesReturns() {
		return salesReturns;
	}

	public void setSalesReturns(List<SalesReturn> salesReturns) {
		this.salesReturns = salesReturns;
	}
	
	public void setAdjustedAmount(BigDecimal adjustedAmount) {
		this.adjustedAmount = adjustedAmount;
	}
	
}