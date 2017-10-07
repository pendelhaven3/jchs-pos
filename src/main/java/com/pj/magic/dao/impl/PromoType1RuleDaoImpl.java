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

import com.pj.magic.dao.PromoType1RuleDao;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Product;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType1Rule;

@Repository
public class PromoType1RuleDaoImpl extends MagicDao implements PromoType1RuleDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PROMO_ID, TARGET_AMOUNT, a.MANUFACTURER_ID, PRODUCT_ID, UNIT, QUANTITY,"
			+ " a.DAILY_REDEEM_LIMIT_PER_CUSTOMER,"
			+ " b.NAME as MANUFACTURER_NAME,"
			+ " c.CODE as PRODUCT_CODE, c.DESCRIPTION as PRODUCT_DESCRIPTION"
			+ " from PROMO_TYPE_1_RULE a"
			+ " join MANUFACTURER b"
			+ "   on b.ID = a.MANUFACTURER_ID"
			+ " join PRODUCT c"
			+ "   on c.ID = a.PRODUCT_ID";
	
	private PromoType1RuleRowMapper ruleRowMapper = new PromoType1RuleRowMapper();
	
	private static final String FIND_BY_PROMO_SQL = BASE_SELECT_SQL
			+ " where a.PROMO_ID = ?";
	
	@Override
	public PromoType1Rule findByPromo(Promo promo) {
		try {
			PromoType1Rule rule =
					getJdbcTemplate().queryForObject(FIND_BY_PROMO_SQL, ruleRowMapper, promo.getId());
			rule.setParent(promo);
			return rule;
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(PromoType1Rule rule) {
		if (rule.isNew()) {
			insert(rule);
		} else {
			update(rule);
		}
	}

	private static final String INSERT_SQL =
			"insert into PROMO_TYPE_1_RULE"
			+ " (PROMO_ID, TARGET_AMOUNT, MANUFACTURER_ID, PRODUCT_ID, UNIT, QUANTITY, DAILY_REDEEM_LIMIT_PER_CUSTOMER) values"
			+ " (?, ?, ?, ?, ?, ?, ?)";
	
	private void insert(final PromoType1Rule rule) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, rule.getParent().getId());
				ps.setBigDecimal(2, rule.getTargetAmount());
				ps.setLong(3, rule.getManufacturer().getId());
				ps.setLong(4, rule.getProduct().getId());
				ps.setString(5, rule.getUnit());
				ps.setInt(6, rule.getQuantity());
				ps.setInt(7, rule.getDailyRedeemLimitPerCustomer());
				return ps;
			}

		}, holder);
		
		PromoType1Rule updated = get(holder.getKey().longValue());
		rule.setId(updated.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	private PromoType1Rule get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, ruleRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String UPDATE_SQL = 
			"update PROMO_TYPE_1_RULE set TARGET_AMOUNT = ?, MANUFACTURER_ID = ?,"
			+ " PRODUCT_ID = ?, UNIT = ?, QUANTITY = ?, DAILY_REDEEM_LIMIT_PER_CUSTOMER = ? where ID = ?";
	
	private void update(PromoType1Rule rule) {
		getJdbcTemplate().update(UPDATE_SQL,
				rule.getTargetAmount(),
				rule.getManufacturer().getId(),
				rule.getProduct().getId(),
				rule.getUnit(),
				rule.getQuantity(),
				rule.getDailyRedeemLimitPerCustomer(),
				rule.getId());
	}

	private class PromoType1RuleRowMapper implements RowMapper<PromoType1Rule> {

		@Override
		public PromoType1Rule mapRow(ResultSet rs, int rowNum) throws SQLException {
			PromoType1Rule rule = new PromoType1Rule();
			rule.setId(rs.getLong("ID"));
			rule.setParent(new Promo(rs.getLong("PROMO_ID")));
			rule.setTargetAmount(rs.getBigDecimal("TARGET_AMOUNT"));
			rule.setUnit(rs.getString("UNIT"));
			rule.setQuantity(rs.getInt("QUANTITY"));
			
			Manufacturer manufacturer = new Manufacturer();
			manufacturer.setId(rs.getLong("MANUFACTURER_ID"));
			manufacturer.setName(rs.getString("MANUFACTURER_NAME"));
			rule.setManufacturer(manufacturer);
			
			Product product = new Product();
			product.setId(rs.getLong("PRODUCT_ID"));
			product.setCode(rs.getString("PRODUCT_CODE"));
			product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
			rule.setProduct(product);
			
			rule.setDailyRedeemLimitPerCustomer(rs.getInt("DAILY_REDEEM_LIMIT_PER_CUSTOMER"));
			
			return rule;
		}
		
	}
	
}