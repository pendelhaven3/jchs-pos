package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.ManufacturerDao;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.service.ManufacturerService;

@Service
public class ManufacturerServiceImpl implements ManufacturerService {

	@Autowired private ManufacturerDao manufacturerDao;
	
	@Transactional
	@Override
	public void save(Manufacturer manufacturer) {
		manufacturerDao.save(manufacturer);
	}

	@Override
	public Manufacturer getManufacturer(long id) {
		return manufacturerDao.get(id);
	}

	@Override
	public List<Manufacturer> getAllManufacturers() {
		return manufacturerDao.getAll();
	}

}
