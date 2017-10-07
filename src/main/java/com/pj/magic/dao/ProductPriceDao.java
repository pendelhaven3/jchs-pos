package com.pj.magic.dao;

import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;

public interface ProductPriceDao {

	void updateUnitPrices(Product product, PricingScheme pricingScheme);

	void createUnitPrices(Product product);

	void deleteAllByProduct(Product product);

}
