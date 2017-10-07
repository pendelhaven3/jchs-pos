package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Product;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.StockQuantityConversionItem;

public interface StockQuantityConversionItemDao {

	StockQuantityConversionItem get(Long id);
	
	void save(StockQuantityConversionItem item);
	
	List<StockQuantityConversionItem> findAllByStockQuantityConversion(StockQuantityConversion stockQuantityConversion);

	void delete(StockQuantityConversionItem item);

	void deleteAllByStockQuantityConversion(StockQuantityConversion stockQuantityConversion);

	StockQuantityConversionItem findFirstByProduct(Product product);
	
	void updateConvertedQuantity(StockQuantityConversionItem item);
	
}
