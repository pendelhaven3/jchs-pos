package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PromoType3RuleDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType3Rule;

@Repository
public class PromoType3RuleDaoImpl extends MagicDao implements PromoType3RuleDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PROMO_ID, TARGET_AMOUNT, FREE_PRODUCT_ID, FREE_UNIT, FREE_QUANTITY,"
			+ " a.DAILY_REDEEM_LIMIT_PER_CUSTOMER,"
			+ " b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION"
			+ " from PROMO_TYPE_3_RULE a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.FREE_PRODUCT_ID";
	
	private PromoType3RuleRowMapper ruleRowMapper = new PromoType3RuleRowMapper();
	
	@Override
	public void save(PromoType3Rule rule) {
		if (rule.isNew()) {
			insert(rule);
		} else {
			update(rule);
		}
	}

	private static final String INSERT_SQL =
			"insert into PROMO_TYPE_3_RULE (PROMO_ID, TARGET_AMOUNT, FREE_PRODUCT_ID, FREE_UNIT,"
			+ " FREE_QUANTITY, DAILY_REDEEM_LIMIT_PER_CUSTOMER) values (?, ?, ?, ?, ?, ?)";
	
	private void insert(final PromoType3Rule rule) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, rule.getParent().getId());
				ps.setBigDecimal(2, rule.getTargetAmount());
				ps.setLong(3, rule.getFreeProduct().getId());
				ps.setString(4, rule.getFreeUnit());
				ps.setInt(5, rule.getFreeQuantity());
				ps.setInt(6, rule.getDailyRedeemLimitPerCustomer());
				return ps;
			}

		}, holder);
		
		PromoType3Rule updated = get(holder.getKey().longValue());
		rule.setId(updated.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	private PromoType3Rule get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, ruleRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String UPDATE_SQL =
			"update PROMO_TYPE_3_RULE set TARGET_AMOUNT = ?, FREE_PRODUCT_ID = ?, FREE_UNIT = ?,"
			+ " FREE_QUANTITY = ?, DAILY_REDEEM_LIMIT_PER_CUSTOMER = ? where ID = ?";
	
	private void update(PromoType3Rule rule) {
		getJdbcTemplate().update(UPDATE_SQL,
				rule.getTargetAmount(),
				rule.getFreeProduct().getId(),
				rule.getFreeUnit(),
				rule.getFreeQuantity(),
				rule.getDailyRedeemLimitPerCustomer(),
				rule.getId());
	}

	private class PromoType3RuleRowMapper implements RowMapper<PromoType3Rule> {

		@Override
		public PromoType3Rule mapRow(ResultSet rs, int rowNum) throws SQLException {
			PromoType3Rule rule = new PromoType3Rule();
			rule.setId(rs.getLong("ID"));
			rule.setParent(new Promo(rs.getLong("PROMO_ID")));
			rule.setTargetAmount(rs.getBigDecimal("TARGET_AMOUNT"));
			
			Product product = new Product();
			product.setId(rs.getLong("FREE_PRODUCT_ID"));
			product.setCode(rs.getString("PRODUCT_CODE"));
			product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
			rule.setFreeProduct(product);
			
			rule.setFreeUnit(rs.getString("FREE_UNIT"));
			rule.setFreeQuantity(rs.getInt("FREE_QUANTITY"));
			rule.setDailyRedeemLimitPerCustomer(rs.getInt("DAILY_REDEEM_LIMIT_PER_CUSTOMER"));
			
			return rule;
		}
		
	}

	private static final String FIND_BY_PROMO_SQL = BASE_SELECT_SQL
			+ " where a.PROMO_ID = ?";
	
	@Override
	public PromoType3Rule findByPromo(Promo promo) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_PROMO_SQL, ruleRowMapper, promo.getId());
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String ADD_ALL_PRODUCTS_TO_PROMO_TYPE_3 = 
			"insert into PROMO_TYPE_3_RULE_PROMO_PRODUCT"
			+ " (PROMO_TYPE_3_RULE_ID, PRODUCT_ID)"
			+ " select ?, ID from PRODUCT where ACTIVE_IND = 'Y'";
	
	@Override
	public void addAllPromoProducts(PromoType3Rule rule) {
		getJdbcTemplate().update(ADD_ALL_PRODUCTS_TO_PROMO_TYPE_3, rule.getId());
	}
	
}