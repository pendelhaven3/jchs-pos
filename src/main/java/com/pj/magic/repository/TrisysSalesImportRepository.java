package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.TrisysSalesImport;

public interface TrisysSalesImportRepository {

	List<TrisysSalesImport> getAll();
	TrisysSalesImport get(long id);
	void save(TrisysSalesImport salesImport);
	TrisysSalesImport findByFile(String file);
	void delete(long id);
	
}
