package com.pj.magic.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcashReceiver {

	private Long id;
	private String name;
	private EcashType ecashType;
	
	public EcashReceiver() { }
	
	public EcashReceiver(Long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
