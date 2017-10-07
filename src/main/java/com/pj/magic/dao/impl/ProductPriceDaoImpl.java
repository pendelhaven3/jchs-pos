package com.pj.magic.dao.impl;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ProductPriceDao;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;

@Repository
public class ProductPriceDaoImpl extends MagicDao implements ProductPriceDao {

	private static final String UPDATE_UNIT_PRICES_SQL =
			"update PRODUCT_PRICE"
			+ " set UNIT_PRICE_CSE = ?, UNIT_PRICE_TIE = ?, UNIT_PRICE_CTN = ?,"
			+ " UNIT_PRICE_DOZ = ?, UNIT_PRICE_PCS = ?"
			+ " where PRODUCT_ID = ? and PRICING_SCHEME_ID = ?";

	@Override
	public void updateUnitPrices(Product product, PricingScheme pricingScheme) {
		getJdbcTemplate().update(UPDATE_UNIT_PRICES_SQL,
				product.getUnitPrice(Unit.CASE),
				product.getUnitPrice(Unit.TIE),
				product.getUnitPrice(Unit.CARTON),
				product.getUnitPrice(Unit.DOZEN),
				product.getUnitPrice(Unit.PIECES),
				product.getId(),
				pricingScheme.getId()
		);
	}

	private static final String CREATE_UNIT_PRICES_SQL =
			"insert into PRODUCT_PRICE (PRICING_SCHEME_ID, PRODUCT_ID) select ID, ? from PRICING_SCHEME";
	
	@Override
	public void createUnitPrices(Product product) {
		getJdbcTemplate().update(CREATE_UNIT_PRICES_SQL, product.getId());
	}

	private static final String DELETE_ALL_BY_PRODUCT_SQL =
			"delete from PRODUCT_PRICE where PRODUCT_ID = ?";
	
	@Override
	public void deleteAllByProduct(Product product) {
		getJdbcTemplate().update(DELETE_ALL_BY_PRODUCT_SQL, product.getId());
	}

}