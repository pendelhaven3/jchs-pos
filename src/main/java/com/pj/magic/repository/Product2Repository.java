package com.pj.magic.repository;

import com.pj.magic.model.Product;

public interface Product2Repository {

	Long createFromTrisys(Product product);
	
	Product findByDescription(String description);

	void updateFromTrisys(Product product);

	Product get(Long id);
	
}
