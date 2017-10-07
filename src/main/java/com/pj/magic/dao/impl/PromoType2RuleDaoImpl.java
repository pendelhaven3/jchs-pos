package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PromoType2RuleDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType2Rule;

@Repository
public class PromoType2RuleDaoImpl extends MagicDao implements PromoType2RuleDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PROMO_ID, PROMO_PRODUCT_ID, PROMO_UNIT, PROMO_QUANTITY,"
			+ " FREE_PRODUCT_ID, FREE_UNIT, FREE_QUANTITY"
			+ " from PROMO_TYPE_2_RULE a";
	
	private PromoType2RuleRowMapper ruleRowMapper = new PromoType2RuleRowMapper();
	
	@Override
	public void save(PromoType2Rule rule) {
		if (rule.getId() == null) {
			insert(rule);
		} else {
			update(rule);
		}
	}

	private static final String INSERT_SQL =
			"insert into PROMO_TYPE_2_RULE (PROMO_ID, PROMO_PRODUCT_ID, PROMO_UNIT, PROMO_QUANTITY,"
			+ " FREE_PRODUCT_ID, FREE_UNIT, FREE_QUANTITY) values (?, ?, ?, ?, ?, ?, ?)";
	
	private void insert(final PromoType2Rule rule) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, rule.getParent().getId());
				ps.setLong(2, rule.getPromoProduct().getId());
				ps.setString(3, rule.getPromoUnit());
				ps.setInt(4, rule.getPromoQuantity());
				ps.setLong(5, rule.getFreeProduct().getId());
				ps.setString(6, rule.getFreeUnit());
				ps.setInt(7, rule.getFreeQuantity());
				return ps;
			}

		}, holder);
		
		PromoType2Rule updated = get(holder.getKey().longValue());
		rule.setId(updated.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	private PromoType2Rule get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, ruleRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String UPDATE_SQL =
			"update PROMO_TYPE_2_RULE set PROMO_PRODUCT_ID = ?, PROMO_UNIT = ?, PROMO_QUANTITY = ?,"
			+ " FREE_PRODUCT_ID = ?, FREE_UNIT = ?, FREE_QUANTITY = ? where ID = ?";
	
	private void update(PromoType2Rule rule) {
		getJdbcTemplate().update(UPDATE_SQL,
				rule.getPromoProduct().getId(),
				rule.getPromoUnit(),
				rule.getPromoQuantity(),
				rule.getFreeProduct().getId(),
				rule.getFreeUnit(),
				rule.getFreeQuantity(),
				rule.getId());
	}

	private class PromoType2RuleRowMapper implements RowMapper<PromoType2Rule> {

		@Override
		public PromoType2Rule mapRow(ResultSet rs, int rowNum) throws SQLException {
			PromoType2Rule rule = new PromoType2Rule();
			rule.setId(rs.getLong("ID"));
			rule.setParent(new Promo(rs.getLong("PROMO_ID")));
			rule.setPromoProduct(new Product(rs.getLong("PROMO_PRODUCT_ID")));
			rule.setPromoUnit(rs.getString("PROMO_UNIT"));
			rule.setPromoQuantity(rs.getInt("PROMO_QUANTITY"));
			rule.setFreeProduct(new Product(rs.getLong("FREE_PRODUCT_ID")));
			rule.setFreeUnit(rs.getString("FREE_UNIT"));
			rule.setFreeQuantity(rs.getInt("FREE_QUANTITY"));
			return rule;
		}
		
	}

	private static final String FIND_ALL_BY_PROMO_SQL = BASE_SELECT_SQL
			+ " where a.PROMO_ID = ?";
	
	@Override
	public List<PromoType2Rule> findAllByPromo(Promo promo) {
		List<PromoType2Rule> rules = 
				getJdbcTemplate().query(FIND_ALL_BY_PROMO_SQL, ruleRowMapper, promo.getId());
		for (PromoType2Rule rule : rules) {
			rule.setParent(promo);
		}
		return rules;
	}

	private static final String DELETE_SQL = "delete from PROMO_TYPE_2_RULE where ID = ?";
	
	@Override
	public void delete(PromoType2Rule rule) {
		getJdbcTemplate().update(DELETE_SQL, rule.getId());
	}
	
}