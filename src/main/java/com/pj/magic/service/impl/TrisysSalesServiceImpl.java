package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.model.TrisysSalesImport;
import com.pj.magic.model.TrisysSalesItem;
import com.pj.magic.model.TrisysSales;
import com.pj.magic.repository.TrisysSalesImportRepository;
import com.pj.magic.repository.TrisysSalesItemRepository;
import com.pj.magic.repository.TrisysSalesRepository;
import com.pj.magic.service.TrisysSalesService;

@Service
public class TrisysSalesServiceImpl implements TrisysSalesService {

	@Autowired private TrisysSalesImportRepository trisysSalesImportRepository;
	@Autowired private TrisysSalesRepository trisysSalesRepository;
	@Autowired private TrisysSalesItemRepository trisysSalesItemRepository;
	
	@Override
	public List<TrisysSalesImport> getAllTrisysSalesImports() {
		return trisysSalesImportRepository.getAll();
	}

	@Override
	public TrisysSalesImport getTrisysSalesImport(Long id) {
		TrisysSalesImport salesImport = trisysSalesImportRepository.get(id);
		salesImport.setSales(trisysSalesRepository.findAllBySalesImport(salesImport));
		return salesImport;
	}

	@Override
	public void saveTrisysSalesImport(TrisysSalesImport salesImport) {
		trisysSalesImportRepository.save(salesImport);
	}

	@Override
	public void saveTrisysSales(TrisysSales sales) {
		trisysSalesRepository.save(sales);
	}

	@Override
	public TrisysSalesImport findByFile(String file) {
		return trisysSalesImportRepository.findByFile(file);
	}

	@Override
	public TrisysSales getTrisysSales(Long id) {
		TrisysSales sales = trisysSalesRepository.get(id);
		sales.setItems(trisysSalesItemRepository.findAllByTrisysSales(sales));
		return sales;
	}

	@Override
	public void saveSalesItem(TrisysSalesItem item) {
		trisysSalesItemRepository.save(item);
	}

}
