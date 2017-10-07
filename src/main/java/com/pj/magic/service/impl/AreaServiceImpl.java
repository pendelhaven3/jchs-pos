package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.AreaDao;
import com.pj.magic.model.Area;
import com.pj.magic.service.AreaService;

@Service
public class AreaServiceImpl implements AreaService {

	@Autowired private AreaDao areaDao;
	
	@Transactional
	@Override
	public void save(Area supplier) {
		areaDao.save(supplier);
	}

	@Override
	public Area getArea(long id) {
		return areaDao.get(id);
	}

	@Override
	public List<Area> getAllAreas() {
		return areaDao.getAll();
	}

	@Override
	public Area findAreaByName(String name) {
		return areaDao.findByName(name);
	}

}
