package com.pj.magic.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UnitSku implements Serializable {

    private String unit;
	private String sku;
	
}
