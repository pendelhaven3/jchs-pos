package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.PricingSchemeDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.search.ProductSearchCriteria;
import com.pj.magic.service.PricingSchemeService;

@Service
public class PricingSchemeServiceImpl implements PricingSchemeService {

	@Autowired private PricingSchemeDao pricingSchemeDao;
	@Autowired private ProductDao productDao;
	
	@Override
	public List<PricingScheme> getAllPricingSchemes() {
		return pricingSchemeDao.getAll();
	}

	@Transactional
	@Override
	public void save(PricingScheme pricingScheme) {
		boolean inserting = (pricingScheme.getId() == null);
		pricingSchemeDao.save(pricingScheme);
		if (inserting) {
			pricingSchemeDao.createProductPrices(pricingScheme);
		}
	}

	@Override
	public PricingScheme get(long id) {
		PricingScheme pricingScheme = pricingSchemeDao.get(id);
		pricingScheme.setProducts(findActiveProductsByPricingScheme(pricingScheme));
		return pricingScheme;
	}

	private List<Product> findActiveProductsByPricingScheme(PricingScheme pricingScheme) {
		ProductSearchCriteria criteria = new ProductSearchCriteria();
		criteria.setPricingScheme(pricingScheme);
		criteria.setActive(true);
		return productDao.search(criteria);
	}

}
