package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.model.EcashType;
import com.pj.magic.repository.EcashTypeRepository;
import com.pj.magic.service.EcashTypeService;

@Service
public class EcashTypeServiceImpl implements EcashTypeService {

	@Autowired
	private EcashTypeRepository ecashTypeRepository;
	
	@Override
	public List<EcashType> getAllEcashTypes() {
		return ecashTypeRepository.getAll();
	}

}
