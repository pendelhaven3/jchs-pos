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

import com.pj.magic.dao.PromoType4RuleDao;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType4Rule;

@Repository
public class PromoType4RuleDaoImpl extends MagicDao implements PromoType4RuleDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PROMO_ID, TARGET_AMOUNT from PROMO_TYPE_4_RULE a";
	
	private PromoType4RuleRowMapper ruleRowMapper = new PromoType4RuleRowMapper();
	
	@Override
	public void save(PromoType4Rule rule) {
		if (rule.isNew()) {
			insert(rule);
		} else {
			update(rule);
		}
	}

	private static final String INSERT_SQL =
			"insert into PROMO_TYPE_4_RULE (PROMO_ID, TARGET_AMOUNT) values (?, ?)";
	
	private void insert(final PromoType4Rule rule) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, rule.getParent().getId());
				ps.setBigDecimal(2, rule.getTargetAmount());
				return ps;
			}

		}, holder);
		
		PromoType4Rule updated = get(holder.getKey().longValue());
		rule.setId(updated.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	private PromoType4Rule get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, ruleRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String UPDATE_SQL =
			"update PROMO_TYPE_4_RULE set TARGET_AMOUNT = ? where ID = ?";
	
	private void update(PromoType4Rule rule) {
		getJdbcTemplate().update(UPDATE_SQL,
				rule.getTargetAmount(),
				rule.getId());
	}

	private class PromoType4RuleRowMapper implements RowMapper<PromoType4Rule> {

		@Override
		public PromoType4Rule mapRow(ResultSet rs, int rowNum) throws SQLException {
			PromoType4Rule rule = new PromoType4Rule();
			rule.setId(rs.getLong("ID"));
			rule.setParent(new Promo(rs.getLong("PROMO_ID")));
			rule.setTargetAmount(rs.getBigDecimal("TARGET_AMOUNT"));
			return rule;
		}
		
	}

	private static final String FIND_BY_PROMO_SQL = BASE_SELECT_SQL
			+ " where a.PROMO_ID = ?";
	
	@Override
	public PromoType4Rule findByPromo(Promo promo) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_PROMO_SQL, ruleRowMapper, promo.getId());
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String ADD_ALL_PRODUCTS_TO_PROMO_TYPE_4 = 
			"insert into PROMO_TYPE_4_RULE_PROMO_PRODUCT"
			+ " (PROMO_TYPE_4_RULE_ID, PRODUCT_ID)"
			+ " select ?, ID from PRODUCT where ACTIVE_IND = 'Y'";
	
	@Override
	public void addAllPromoProducts(PromoType4Rule rule) {
		getJdbcTemplate().update(ADD_ALL_PRODUCTS_TO_PROMO_TYPE_4, rule.getId());
	}

	private static final String ADD_ALL_PRODUCTS_BY_MANUFACTURER_TO_PROMO_TYPE_4 = 
			"insert into PROMO_TYPE_4_RULE_PROMO_PRODUCT"
			+ " (PROMO_TYPE_4_RULE_ID, PRODUCT_ID)"
			+ " select ?, ID from PRODUCT where ACTIVE_IND = 'Y' and MANUFACTURER_ID = ?";
	
	@Override
	public void addAllPromoProductsByManufacturer(PromoType4Rule rule, Manufacturer manufacturer) {
		getJdbcTemplate().update(ADD_ALL_PRODUCTS_BY_MANUFACTURER_TO_PROMO_TYPE_4, rule.getId(), manufacturer.getId());
	}
	
}