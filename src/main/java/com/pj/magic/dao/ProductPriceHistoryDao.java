package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductPriceHistory;
import com.pj.magic.model.search.ProductPriceHistorySearchCriteria;

public interface ProductPriceHistoryDao {

	void save(ProductPriceHistory history);
	
	List<ProductPriceHistory> findAllByProductAndPricingScheme(Product product, PricingScheme pricingScheme);
	
	List<ProductPriceHistory> search(ProductPriceHistorySearchCriteria criteria);

}