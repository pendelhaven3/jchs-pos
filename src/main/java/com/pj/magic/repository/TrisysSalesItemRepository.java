package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.TrisysSales;
import com.pj.magic.model.TrisysSalesItem;

public interface TrisysSalesItemRepository {

	List<TrisysSalesItem> findAllByTrisysSales(TrisysSales sales);
	void save(TrisysSalesItem item);
	
}
