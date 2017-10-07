package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.pj.magic.dao.PromoDao;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType;
import com.pj.magic.model.search.PromoSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class PromoDaoImpl extends MagicDao implements PromoDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PROMO_NO, a.NAME, PROMO_TYPE_ID, a.ACTIVE_IND, a.START_DT, a.END_DT, a.PRICING_SCHEME_ID,"
			+ " b.NAME as PRICING_SCHEME_NAME"
			+ " from PROMO a"
			+ " left join PRICING_SCHEME b"
			+ "   on b.ID = a.PRICING_SCHEME_ID";
	
	private static final String PROMO_NUMBER_SEQUENCE = "PROMO_NO_SEQ";
	
	private PromoRowMapper promoRowMapper = new PromoRowMapper();
	
	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by a.PROMO_NO desc";
	
	@Override
	public List<Promo> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, promoRowMapper);
	}

	private class PromoRowMapper implements RowMapper<Promo> {

		@Override
		public Promo mapRow(ResultSet rs, int rowNum) throws SQLException {
			Promo promo = new Promo();
			promo.setId(rs.getLong("ID"));
			promo.setPromoNumber(rs.getLong("PROMO_NO"));
			promo.setName(rs.getString("NAME"));
			promo.setPromoType(PromoType.getPromoType(rs.getLong("PROMO_TYPE_ID")));
			promo.setActive("Y".equals(rs.getString("ACTIVE_IND")));
			promo.setStartDate(rs.getDate("START_DT"));
			promo.setEndDate(rs.getDate("END_DT"));
			
			long pricingSchemeId = rs.getLong("PRICING_SCHEME_ID");
			if (pricingSchemeId != 0) {
				promo.setPricingScheme(new PricingScheme(pricingSchemeId, rs.getString("PRICING_SCHEME_NAME")));
			}
			
			return promo;
		}
		
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public Promo get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, promoRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(Promo promo) {
		if (promo.getId() == null) {
			insert(promo);
		} else {
			update(promo);
		}
	}

	private static final String INSERT_SQL =
			"insert into PROMO"
			+ " (PROMO_NO, NAME, PROMO_TYPE_ID, ACTIVE_IND, START_DT, END_DT, PRICING_SCHEME_ID)"
			+ " values (?, ?, ?, ?, ?, ?, ?)";
	
	private void insert(final Promo promo) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextPromoNumber());
				ps.setString(2, promo.getName());
				ps.setLong(3, promo.getPromoType().getId());
				ps.setString(4, promo.isActive() ? "Y" : "N");
				ps.setDate(5, new java.sql.Date(promo.getStartDate().getTime()));
				if (promo.getEndDate() != null) {
					ps.setDate(6, new java.sql.Date(promo.getEndDate().getTime()));
				} else {
					ps.setNull(6, Types.DATE);
				}
				if (promo.getPricingScheme() != null) {
					ps.setLong(7, promo.getPricingScheme().getId());
				} else {
					ps.setNull(7, Types.BIGINT);
				}
				return ps;
			}

		}, holder);
		
		Promo updated = get(holder.getKey().longValue());
		promo.setId(updated.getId());
		promo.setPromoNumber(updated.getPromoNumber());
	}
	
	private long getNextPromoNumber() {
		return getNextSequenceValue(PROMO_NUMBER_SEQUENCE);
	}

	private static final String UPDATE_SQL = 
			"update PROMO set NAME = ?, ACTIVE_IND = ?, START_DT = ?, END_DT = ?, PRICING_SCHEME_ID = ? where ID = ?";
	
	private void update(Promo promo) {
		getJdbcTemplate().update(UPDATE_SQL,
				promo.getName(),
				promo.isActive() ? "Y" : "N",
				promo.getStartDate(),
				promo.getEndDate(),
				promo.getPricingScheme() != null ? promo.getPricingScheme().getId() : null,
				promo.getId());
	}

	private static final String FIND_ALL_BY_ACTIVE_SQL = BASE_SELECT_SQL
			+ " where a.ACTIVE_IND = ? order by PROMO_NO desc";
	
	@Override
	public List<Promo> findAllByActive(boolean active) {
		return getJdbcTemplate().query(FIND_ALL_BY_ACTIVE_SQL, promoRowMapper, 
				active ? "Y" : "N");
	}

	@Override
	public List<Promo> search(PromoSearchCriteria criteria) {
		List<Object> params = new ArrayList<>();
		
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		if (criteria.getPromoType() != null) {
			sql.append(" and PROMO_TYPE_ID = ?");
			params.add(criteria.getPromoType().getId());
		}
		
		if (criteria.getPromoTypes() != null) {
			List<String> promoTypeIds = new ArrayList<>(
					Collections2.transform(criteria.getPromoTypes(), new Function<PromoType, String>() {

				@Override
				public String apply(PromoType input) {
					return input.getId().toString();
				}
			}));
			sql.append(" and PROMO_TYPE_ID in (")
				.append(DbUtil.toSqlInValues(promoTypeIds))
				.append(")");
		}
		
		if (criteria.getPromoDate() != null) {
			sql.append(" and a.START_DT <= ? and ? <= a.END_DT");
			
			String promoDateString = DbUtil.toMySqlDateString(criteria.getPromoDate());
			params.add(promoDateString);
			params.add(promoDateString);
		}
		
		if (criteria.getAcceptedPricingScheme() != null) {
			sql.append(" and (a.PRICING_SCHEME_ID = ? or a.PRICING_SCHEME_ID is null)");
			params.add(criteria.getAcceptedPricingScheme().getId());
		}
		
		if (criteria.getActive() != null) {
			sql.append(" and a.ACTIVE_IND = ?");
			params.add(criteria.getActive() ? "Y" : "N");
		}
		
		if (criteria.getStartDate() != null) {
			sql.append(" and a.START_DT <= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getStartDate()));
		}
		
		if (criteria.getEndDate() != null) {
			sql.append(" and ? <= a.END_DT");
			params.add(DbUtil.toMySqlDateString(criteria.getEndDate()));
		}
		
		sql.append(" order by PROMO_NO desc");
		
		return getJdbcTemplate().query(sql.toString(), promoRowMapper, params.toArray());
	}

}