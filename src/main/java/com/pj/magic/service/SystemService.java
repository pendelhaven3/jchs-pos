package com.pj.magic.service;

import java.math.BigDecimal;
import java.util.Date;


public interface SystemService {

	String getDatabaseVersion();
	
	BigDecimal getVatRate();

	Date getCurrentDateTime(); 
	
}
