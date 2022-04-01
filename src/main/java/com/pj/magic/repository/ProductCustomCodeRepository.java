package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.Product2;
import com.pj.magic.model.ProductCustomCode;
import com.pj.magic.model.Supplier;

public interface ProductCustomCodeRepository {
	
	List<ProductCustomCode> findAllByProductId(Long id);

	void save(ProductCustomCode customCode);

	void delete(ProductCustomCode customCode);

	ProductCustomCode findByProductAndSupplier(Product2 product, Supplier supplier);

}
