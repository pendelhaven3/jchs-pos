package com.pj.magic.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.SystemDao;
import com.pj.magic.dao.SystemParameterDao;
import com.pj.magic.service.SystemService;

@Service
public class SystemServiceImpl implements SystemService {

	private static final BigDecimal VAT_RATE = new BigDecimal("0.12");
	
	@Autowired private SystemParameterDao systemParameterDao;
	@Autowired private SystemDao systemDao;
	
	@Override
	public String getDatabaseVersion() {
		return systemParameterDao.getSystemParameterValue("VERSION");
	}

	@Override
	public BigDecimal getVatRate() {
		return VAT_RATE; // TODO: Make this configurable
	}

	@Override
	public Date getCurrentDateTime() {
		return systemDao.getCurrentDateTime();
	}

}
