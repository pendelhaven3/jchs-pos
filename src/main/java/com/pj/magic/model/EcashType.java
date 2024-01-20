package com.pj.magic.model;

import org.apache.commons.lang.builder.EqualsBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EcashType {
	
	private Long id;
	private String code;
	
	@Override
	public String toString() {
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		EcashType other = (EcashType)obj;
		return new EqualsBuilder().append(id, other.getId()).isEquals();
	}
	
}
