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

import com.pj.magic.dao.PromoType5RuleDao;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType5Rule;

@Repository
public class PromoType5RuleDaoImpl extends MagicDao implements PromoType5RuleDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PROMO_ID, TARGET_AMOUNT, REBATE from PROMO_TYPE_5_RULE a";
	
	private PromoType5RuleRowMapper ruleRowMapper = new PromoType5RuleRowMapper();
	
	@Override
	public void save(PromoType5Rule rule) {
		if (rule.isNew()) {
			insert(rule);
		} else {
			update(rule);
		}
	}

	private static final String INSERT_SQL =
			"insert into PROMO_TYPE_5_RULE (PROMO_ID, TARGET_AMOUNT, REBATE) values (?, ?, ?)";
	
	private void insert(final PromoType5Rule rule) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, rule.getParent().getId());
				ps.setBigDecimal(2, rule.getTargetAmount());
				ps.setBigDecimal(3, rule.getRebate());
				return ps;
			}

		}, holder);
		
		PromoType5Rule updated = get(holder.getKey().longValue());
		rule.setId(updated.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	private PromoType5Rule get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, ruleRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String UPDATE_SQL =
			"update PROMO_TYPE_5_RULE set TARGET_AMOUNT = ?, REBATE = ? where ID = ?";
	
	private void update(PromoType5Rule rule) {
		getJdbcTemplate().update(UPDATE_SQL,
				rule.getTargetAmount(),
				rule.getRebate(),
				rule.getId());
	}

	private class PromoType5RuleRowMapper implements RowMapper<PromoType5Rule> {

		@Override
		public PromoType5Rule mapRow(ResultSet rs, int rowNum) throws SQLException {
			PromoType5Rule rule = new PromoType5Rule();
			rule.setId(rs.getLong("ID"));
			rule.setParent(new Promo(rs.getLong("PROMO_ID")));
			rule.setTargetAmount(rs.getBigDecimal("TARGET_AMOUNT"));
			rule.setRebate(rs.getBigDecimal("REBATE"));
			return rule;
		}
		
	}

	private static final String FIND_BY_PROMO_SQL = BASE_SELECT_SQL
			+ " where a.PROMO_ID = ?";
	
	@Override
	public PromoType5Rule findByPromo(Promo promo) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_PROMO_SQL, ruleRowMapper, promo.getId());
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String ADD_ALL_PRODUCTS_TO_PROMO_TYPE_5 = 
			"insert into PROMO_TYPE_5_RULE_PROMO_PRODUCT"
			+ " (PROMO_TYPE_5_RULE_ID, PRODUCT_ID)"
			+ " select ?, ID from PRODUCT where ACTIVE_IND = 'Y'";
	
	@Override
	public void addAllPromoProducts(PromoType5Rule rule) {
		getJdbcTemplate().update(ADD_ALL_PRODUCTS_TO_PROMO_TYPE_5, rule.getId());
	}
	
}