package com.pj.magic.dao.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ProductPriceHistoryDao;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductPriceHistory;
import com.pj.magic.model.Unit;
import com.pj.magic.model.UnitPrice;
import com.pj.magic.model.User;
import com.pj.magic.model.search.ProductPriceHistorySearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class ProductPriceHistoryDaoImpl extends MagicDao implements ProductPriceHistoryDao {

	private static final String BASE_SELECT_SQL =
			"select PRICING_SCHEME_ID, PRODUCT_ID, UPDATE_DT, UPDATE_BY,"
			+ " UNIT_PRICE_CSE, UNIT_PRICE_TIE, UNIT_PRICE_CTN, UNIT_PRICE_DOZ, UNIT_PRICE_PCS,"
			+ " PREV_UNIT_PRICE_CSE, PREV_UNIT_PRICE_TIE, PREV_UNIT_PRICE_CTN, PREV_UNIT_PRICE_DOZ, PREV_UNIT_PRICE_PCS,"
			+ " c.ACTIVE_UNIT_IND_PCS, c.ACTIVE_UNIT_IND_DOZ, c.ACTIVE_UNIT_IND_CTN, c.ACTIVE_UNIT_IND_TIE,"
			+ " b.USERNAME as UPDATE_BY_USERNAME,"
			+ " c.DESCRIPTION as PRODUCT_DESCRIPTION"
			+ " from PRODUCT_PRICE_HISTORY a"
			+ " join USER b"
			+ "   on b.ID = a.UPDATE_BY"
			+ " join PRODUCT c"
			+ "   on c.ID = a.PRODUCT_ID";
	
	private static final String INSERT_SQL =
			"insert into PRODUCT_PRICE_HISTORY"
			+ " (PRICING_SCHEME_ID, PRODUCT_ID, UPDATE_DT, UPDATE_BY,"
			+ " UNIT_PRICE_CSE, UNIT_PRICE_TIE, UNIT_PRICE_CTN, UNIT_PRICE_DOZ, UNIT_PRICE_PCS,"
			+ " PREV_UNIT_PRICE_CSE, PREV_UNIT_PRICE_TIE, PREV_UNIT_PRICE_CTN, PREV_UNIT_PRICE_DOZ, PREV_UNIT_PRICE_PCS)"
			+ " values (?, ?, now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private ProductPriceHistoryRowMapper rowMapper = new ProductPriceHistoryRowMapper();
	
	@Override
	public void save(ProductPriceHistory history) {
		getJdbcTemplate().update(INSERT_SQL,
				history.getPricingScheme().getId(),
				history.getProduct().getId(),
				history.getUpdatedBy().getId(),
				history.getUnitPrice(Unit.CASE),
				history.getUnitPrice(Unit.TIE),
				history.getUnitPrice(Unit.CARTON),
				history.getUnitPrice(Unit.DOZEN),
				history.getUnitPrice(Unit.PIECES),
				history.getPreviousUnitPrice(Unit.CASE),
				history.getPreviousUnitPrice(Unit.TIE),
				history.getPreviousUnitPrice(Unit.CARTON),
				history.getPreviousUnitPrice(Unit.DOZEN),
				history.getPreviousUnitPrice(Unit.PIECES)
		);
	}

	private static final String FIND_ALL_BY_PRODUCT_AND_PRICING_SCHEME_SQL = BASE_SELECT_SQL
			+ " where PRICING_SCHEME_ID = ?"
			+ " and PRODUCT_ID = ?"
			+ " order by UPDATE_DT desc"; 
	
	@Override
	public List<ProductPriceHistory> findAllByProductAndPricingScheme(
			Product product, PricingScheme pricingScheme) {
		return getJdbcTemplate().query(FIND_ALL_BY_PRODUCT_AND_PRICING_SCHEME_SQL, rowMapper, pricingScheme.getId(), product.getId());
	}

	private class ProductPriceHistoryRowMapper implements RowMapper<ProductPriceHistory> {

		@Override
		public ProductPriceHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
			ProductPriceHistory history = new ProductPriceHistory();
			history.setPricingScheme(new PricingScheme(rs.getLong("PRICING_SCHEME_ID")));
			
			Product product = new Product(rs.getLong("PRODUCT_ID"));
			product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
			history.setProduct(product);
			
			history.setUpdateDate(rs.getTimestamp("UPDATE_DT"));
			history.setUpdatedBy(new User(rs.getLong("UPDATE_BY"), rs.getString("UPDATE_BY_USERNAME")));
			
			BigDecimal unitPriceCase = rs.getBigDecimal("UNIT_PRICE_CSE");
			if (unitPriceCase != null) {
				history.getUnitPrices().add(new UnitPrice(Unit.CASE, unitPriceCase));
			}
			
			BigDecimal unitPriceTie = rs.getBigDecimal("UNIT_PRICE_TIE");
			if (unitPriceTie != null) {
				history.getUnitPrices().add(new UnitPrice(Unit.TIE, unitPriceTie));
			}
			
			BigDecimal unitPriceCarton = rs.getBigDecimal("UNIT_PRICE_CTN");
			if (unitPriceCarton != null) {
				history.getUnitPrices().add(new UnitPrice(Unit.CARTON, unitPriceCarton));
			}

			BigDecimal unitPriceDozen = rs.getBigDecimal("UNIT_PRICE_DOZ");
			if (unitPriceDozen != null) {
				history.getUnitPrices().add(new UnitPrice(Unit.DOZEN, unitPriceDozen));
			}

			BigDecimal unitPricePieces = rs.getBigDecimal("UNIT_PRICE_PCS");
			if (unitPricePieces != null) {
				history.getUnitPrices().add(new UnitPrice(Unit.PIECES, unitPricePieces));
			}
			
			history.setPreviousUnitPrices(getPreviousUnitPrices(rs));
			
			if ("Y".equals(rs.getString("ACTIVE_UNIT_IND_TIE"))) {
				history.getActiveUnits().add(Unit.TIE);
			}
			
			if ("Y".equals(rs.getString("ACTIVE_UNIT_IND_CTN"))) {
				history.getActiveUnits().add(Unit.CARTON);
			}
			
			if ("Y".equals(rs.getString("ACTIVE_UNIT_IND_DOZ"))) {
				history.getActiveUnits().add(Unit.DOZEN);
			}
			
			if ("Y".equals(rs.getString("ACTIVE_UNIT_IND_PCS"))) {
				history.getActiveUnits().add(Unit.PIECES);
			}
			
			return history;
		}

		private List<UnitPrice> getPreviousUnitPrices(ResultSet rs) throws SQLException {
			List<UnitPrice> unitPrices = new ArrayList<>();
			
			BigDecimal unitPriceCase = rs.getBigDecimal("PREV_UNIT_PRICE_CSE");
			if (unitPriceCase != null) {
				unitPrices.add(new UnitPrice(Unit.CASE, unitPriceCase));
			}
			
			BigDecimal unitPriceTie = rs.getBigDecimal("PREV_UNIT_PRICE_TIE");
			if (unitPriceTie != null) {
				unitPrices.add(new UnitPrice(Unit.TIE, unitPriceTie));
			}
			
			BigDecimal unitPriceCarton = rs.getBigDecimal("PREV_UNIT_PRICE_CTN");
			if (unitPriceCarton != null) {
				unitPrices.add(new UnitPrice(Unit.CARTON, unitPriceCarton));
			}

			BigDecimal unitPriceDozen = rs.getBigDecimal("PREV_UNIT_PRICE_DOZ");
			if (unitPriceDozen != null) {
				unitPrices.add(new UnitPrice(Unit.DOZEN, unitPriceDozen));
			}

			BigDecimal unitPricePieces = rs.getBigDecimal("PREV_UNIT_PRICE_PCS");
			if (unitPricePieces != null) {
				unitPrices.add(new UnitPrice(Unit.PIECES, unitPricePieces));
			}
			
			return unitPrices;
		}
		
	}

	@Override
	public List<ProductPriceHistory> search(ProductPriceHistorySearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		List<Object> params = new ArrayList<>();
		
		if (criteria.getFromDate() != null) {
			sql.append(" and a.UPDATE_DT >= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getFromDate()));
		}
		
		if (criteria.getToDate() != null) {
			sql.append(" and a.UPDATE_DT < date_add(?, interval 1 day)");
			params.add(DbUtil.toMySqlDateString(criteria.getToDate()));
		}
		
		if (criteria.getPricingScheme() != null) {
			sql.append(" and a.PRICING_SCHEME_ID = ?");
			params.add(criteria.getPricingScheme().getId());
		}
		
		sql.append(" order by c.DESCRIPTION");
		
		return getJdbcTemplate().query(sql.toString(), rowMapper, params.toArray());
	}
	
}