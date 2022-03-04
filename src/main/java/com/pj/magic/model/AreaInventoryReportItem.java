package com.pj.magic.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AreaInventoryReportItem implements Comparable<AreaInventoryReportItem> {

	private Long id;
	private AreaInventoryReport parent;
	private Product2 product;
	private String unit;
	private Integer quantity;

	private String code; // derived field
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(product)
			.append(unit)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof AreaInventoryReportItem)) {
            return false;
        }
        AreaInventoryReportItem other = (AreaInventoryReportItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(unit, other.getUnit())
			.isEquals();
	}

	@Override
	public int compareTo(AreaInventoryReportItem o) {
		int result = product.compareTo(o.getProduct());
		if (result == 0) {
			return Unit.compare(unit, o.getUnit());
		} else {
			return result;
		}
	}
	
}
