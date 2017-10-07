package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Manufacturer;

public interface ManufacturerDao {

	void save(Manufacturer manufacturer);
	
	Manufacturer get(long id);
	
	List<Manufacturer> getAll();
	
}
