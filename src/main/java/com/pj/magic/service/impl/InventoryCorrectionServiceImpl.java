package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.model.InventoryCorrection;
import com.pj.magic.repository.InventoryCorrectionRepository;
import com.pj.magic.service.InventoryCorrectionService;
import com.pj.magic.service.LoginService;

@Service
public class InventoryCorrectionServiceImpl implements InventoryCorrectionService {

	@Autowired private InventoryCorrectionRepository inventoryCorrectionRepository;
	@Autowired private LoginService loginService;
	
	@Override
	public List<InventoryCorrection> getAllInventoryCorrections() {
		return inventoryCorrectionRepository.getAll();
	}

	@Override
	public InventoryCorrection getInventoryCorrection(long id) {
		return inventoryCorrectionRepository.get(id);
	}

	@Transactional
	@Override
	public void save(InventoryCorrection inventoryCorrection) {
		inventoryCorrection.setUpdatedBy(loginService.getLoggedInUser());
		inventoryCorrectionRepository.save(inventoryCorrection);
	}

}
