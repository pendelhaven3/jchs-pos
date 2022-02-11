package com.pj.magic.service;

import java.io.File;
import java.util.List;

import com.pj.magic.model.TrisysSalesImport;
import com.pj.magic.model.TrisysSalesItem;
import com.pj.magic.model.TrisysSales;

public interface TrisysSalesService {

	List<TrisysSalesImport> getAllTrisysSalesImports();
	TrisysSalesImport getTrisysSalesImport(Long id);
	void saveTrisysSalesImport(TrisysSalesImport salesImport);
	void saveTrisysSales(TrisysSales sales);
	TrisysSalesImport findByFile(String file);
	TrisysSales getTrisysSales(Long id);
	void saveSalesItem(TrisysSalesItem item);
	void importTrisysSales(File file) throws Exception;
	
}
