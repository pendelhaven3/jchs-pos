package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.Product;
import com.pj.magic.model.search.ProductSearchCriteria;

public interface ProductSearchRepository {

	List<Product> search(ProductSearchCriteria criteria);
	
}
