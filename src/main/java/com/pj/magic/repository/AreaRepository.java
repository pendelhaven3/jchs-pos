package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.Area;

public interface AreaRepository {

	void save(Area area);
	
	Area get(long id);
	
	List<Area> getAll();
	
	Area findByName(String name);
	
}
