package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.Manufacturer;

public interface ManufacturerService {

	void save(Manufacturer manufacturer);
	
	Manufacturer getManufacturer(long id);
	
	List<Manufacturer> getAllManufacturers();
	
}
