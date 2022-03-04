package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.model.Area;
import com.pj.magic.repository.AreaRepository;
import com.pj.magic.service.AreaService;

@Service
public class AreaServiceImpl implements AreaService {

	@Autowired
	private AreaRepository areaRepository;
	
	@Transactional
	@Override
	public void save(Area supplier) {
		areaRepository.save(supplier);
	}

	@Override
	public Area getArea(long id) {
		return areaRepository.get(id);
	}

	@Override
	public List<Area> getAllAreas() {
		return areaRepository.getAll();
	}

	@Override
	public Area findAreaByName(String name) {
		return areaRepository.findByName(name);
	}

}
