package com.pj.magic.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCustomCode {

	private Long id;
	private Product2 product;
	private String code;
	private String remarks;
	
}
