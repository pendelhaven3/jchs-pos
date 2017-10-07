package com.pj.magic.service;

import java.math.BigDecimal;


public interface SystemService {

	String getDatabaseVersion();
	
	BigDecimal getVatRate(); 
	
}
