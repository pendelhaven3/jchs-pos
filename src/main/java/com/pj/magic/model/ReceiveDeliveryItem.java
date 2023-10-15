package com.pj.magic.model;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiveDeliveryItem implements Comparable<ReceiveDeliveryItem> {

	private Long id;
	private ReceiveDelivery parent;
	private String code;
	private String unit;
	private Integer quantity;
	private BigDecimal cost;
	private Product product;
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof ReceiveDeliveryItem)) {
            return false;
        }
        ReceiveDeliveryItem other = (ReceiveDeliveryItem)obj;		
		return new EqualsBuilder()
			.append(code, other.getCode())
			.append(unit, other.getUnit())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(code)
				.append(unit)
				.toHashCode();
	}
	
	@Override
	public int compareTo(ReceiveDeliveryItem o) {
		int result = code.compareTo(o.getCode());
		if (result == 0) {
			return Unit.compare(unit, o.getUnit());
		} else {
			return result;
		}
	}
	
}
