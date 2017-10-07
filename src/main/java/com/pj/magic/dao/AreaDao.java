package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Area;

public interface AreaDao {

	void save(Area area);
	
	Area get(long id);
	
	List<Area> getAll();
	
	Area findByName(String name);
	
}
