package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.Product;
import com.pj.magic.model.Product2;
import com.pj.magic.model.UnitSku;
import com.pj.magic.model.search.Product2SearchCriteria;

public interface Product2Repository {

	Product2 findByDescription(String description);

	Long createFromTrisys(Product product);
	
	void updateFromTrisys(Product2 product);

	Product2 get(Long id);

	void update(Product2 product);
	
	List<UnitSku> getUnitSkus(Product2 product);

	void updateCosts(Product2 product);

	void addAvailableQuantity(Long id, String unit, Integer quantity);

	void subtractAvailableQuantity(Long id, String unit, int quantity);

	List<Product2> search(Product2SearchCriteria criteria);

}
