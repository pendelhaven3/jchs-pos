package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.Area;

public interface AreaService {

	void save(Area area);
	
	Area getArea(long id);
	
	List<Area> getAllAreas();

	Area findAreaByName(String name);
	
}
