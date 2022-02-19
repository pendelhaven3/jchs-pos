package com.pj.magic.service;

import com.pj.magic.model.Product;

public interface Product2Service {

	Long saveFromTrisys(Product product);

	Product getProduct(Long id);
	
}
