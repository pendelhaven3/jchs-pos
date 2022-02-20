package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.Product;
import com.pj.magic.model.UnitSku;

public interface Product2Repository {

	Product findByDescription(String description);

	Long createFromTrisys(Product product);
	
	void updateFromTrisys(Product product);

	Product get(Long id);

	void update(Product product);
	
	List<UnitSku> getUnitSkus(Product product);
	
}
