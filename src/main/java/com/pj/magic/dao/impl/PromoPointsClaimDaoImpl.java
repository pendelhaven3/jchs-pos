package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PromoPointsClaimDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoPointsClaim;
import com.pj.magic.model.User;

@Repository
public class PromoPointsClaimDaoImpl extends MagicDao implements PromoPointsClaimDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PROMO_ID, CLAIM_NO, CUSTOMER_ID, POINTS, a.REMARKS, CLAIM_DT, CLAIM_BY,"
			+ " d.USERNAME as CLAIM_BY_USERNAME"
			+ " from PROMO_POINTS_CLAIM a"
			+ " join PROMO b"
			+ "   on b.ID = a.PROMO_ID"
			+ " join CUSTOMER c"
			+ "   on c.ID = a.CUSTOMER_ID"
			+ " join USER d"
			+ "   on d.ID = a.CLAIM_BY";
	
	private PromoPointsClaimRowMapper rowMapper = new PromoPointsClaimRowMapper();
	
	private static final String FIND_ALL_BY_PROMO_AND_CUSTOMER_SQL = BASE_SELECT_SQL +
			" where a.PROMO_ID = ? and a.CUSTOMER_ID = ?";
	
	@Override
	public List<PromoPointsClaim> findAllByPromoAndCustomer(Promo promo, Customer customer) {
		return getJdbcTemplate().query(FIND_ALL_BY_PROMO_AND_CUSTOMER_SQL, rowMapper, promo.getId(), customer.getId());
	}

	private class PromoPointsClaimRowMapper implements RowMapper<PromoPointsClaim> {

		@Override
		public PromoPointsClaim mapRow(ResultSet rs, int rowNum) throws SQLException {
			PromoPointsClaim claim = new PromoPointsClaim();
			claim.setId(rs.getLong("ID"));
			claim.setPromo(new Promo(rs.getLong("PROMO_ID")));
			claim.setClaimNumber(rs.getLong("CLAIM_NO"));
			claim.setCustomer(new Customer(rs.getLong("CUSTOMER_ID")));
			claim.setPoints(rs.getInt("POINTS"));
			claim.setRemarks(rs.getString("REMARKS"));
			claim.setClaimDate(rs.getTimestamp("CLAIM_DT"));
			claim.setClaimBy(new User(rs.getLong("CLAIM_BY"), rs.getString("CLAIM_BY_USERNAME")));
			return claim;
		}
		
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public PromoPointsClaim get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, rowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(PromoPointsClaim claim) {
		if (claim.isNew()) {
			insert(claim);
		} else {
			update(claim);
		}
	}

	private static final String INSERT_SQL =
			"insert into PROMO_POINTS_CLAIM"
			+ " (PROMO_ID, CLAIM_NO, CUSTOMER_ID, POINTS, REMARKS, CLAIM_DT, CLAIM_BY)"
			+ " values"
			+ " (?, ?, ?, ?, ?, ?, ?)";
	
	private void insert(final PromoPointsClaim claim) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, claim.getPromo().getId());
				ps.setLong(2, getNextPromoClaimNumber(claim.getPromo()));
				ps.setLong(3, claim.getCustomer().getId());
				ps.setInt(4, claim.getPoints());
				ps.setString(5, claim.getRemarks());
				ps.setTimestamp(6, new Timestamp(claim.getClaimDate().getTime()));
				ps.setLong(7, claim.getClaimBy().getId());
				return ps;
			}
		}, holder);
		
		PromoPointsClaim updated = get(holder.getKey().longValue());
		claim.setId(updated.getId());
		claim.setClaimNumber(updated.getClaimNumber());
	}

	private static final String GET_SEQUENCE_NEXT_VALUE_SQL = 
			"select VALUE + 1 from PROMO_REDEMPTION_SEQUENCE where PROMO_ID = ? for update";
	
	private static final String UPDATE_SEQUENCE_VALUE_SQL =
			"update PROMO_REDEMPTION_SEQUENCE set VALUE = ? where PROMO_ID = ?";
	
	private long getNextPromoClaimNumber(Promo promo) {
		Long value = getJdbcTemplate().queryForObject(GET_SEQUENCE_NEXT_VALUE_SQL, Long.class, promo.getId());
		getJdbcTemplate().update(UPDATE_SEQUENCE_VALUE_SQL, value, promo.getId());
		return value;
	}
	
	private static final String UPDATE_SQL =
			"update PROMO_POINTS_CLAIM"
			+ " set POINTS = ?, REMARKS = ?"
			+ " where ID = ?";
	
	private void update(PromoPointsClaim claim) {
		getJdbcTemplate().update(UPDATE_SQL,
				claim.getPoints(),
				claim.getRemarks(),
				claim.getId());
	}

	private static final String DELETE_SQL =
			"delete from PROMO_POINTS_CLAIM where ID = ?";
	
	@Override
	public void delete(PromoPointsClaim claim) {
		getJdbcTemplate().update(DELETE_SQL, claim.getId());
	}
	
}