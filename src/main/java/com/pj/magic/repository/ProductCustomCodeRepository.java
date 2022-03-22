package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.ProductCustomCode;

public interface ProductCustomCodeRepository {
	
	List<ProductCustomCode> findAllByProductId(Long id);

	ProductCustomCode findByProductIdAndCode(Long productId, String code);

	void save(ProductCustomCode customCode);

	void delete(ProductCustomCode customCode);

}
