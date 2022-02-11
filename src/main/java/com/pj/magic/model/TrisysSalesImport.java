package com.pj.magic.model;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrisysSalesImport {

	private Long id;
	private String file;
	private Date importDate;
	private User importBy;
	private List<TrisysSales> sales;
	
	public TrisysSalesImport() { }
	
	public TrisysSalesImport(Long id) {
		this.id = id;
	}
	
}
