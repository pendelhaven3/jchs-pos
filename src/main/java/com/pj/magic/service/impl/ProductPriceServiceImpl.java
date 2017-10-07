package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.ProductPriceHistoryDao;
import com.pj.magic.model.ProductPriceHistory;
import com.pj.magic.model.search.ProductPriceHistorySearchCriteria;
import com.pj.magic.service.ProductPriceService;

@Service
public class ProductPriceServiceImpl implements ProductPriceService {

	@Autowired private ProductPriceHistoryDao productPriceHistoryDao;
	
	@Override
	public List<ProductPriceHistory> searchProductPriceHistories(
			ProductPriceHistorySearchCriteria criteria) {
		return productPriceHistoryDao.search(criteria);
	}

}