package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.TrisysSalesImport;
import com.pj.magic.model.TrisysSales;

public interface TrisysSalesRepository {

	List<TrisysSales> findAllBySalesImport(TrisysSalesImport salesImport);
	void save(TrisysSales sales);
	TrisysSales get(long id);
	
}
