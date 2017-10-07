package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.ProductPriceHistory;
import com.pj.magic.model.search.ProductPriceHistorySearchCriteria;

public interface ProductPriceService {

	List<ProductPriceHistory> searchProductPriceHistories(ProductPriceHistorySearchCriteria criteria);
	
}