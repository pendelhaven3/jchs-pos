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

import com.pj.magic.dao.PricingSchemeDao;
import com.pj.magic.model.PricingScheme;

@Repository
public class PricingSchemeDaoImpl extends MagicDao implements PricingSchemeDao {

	private static final String BASE_SELECT_SQL = "select ID, NAME from PRICING_SCHEME";
	
	private PricingSchemeRowMapper pricingSchemeRowMapper = new PricingSchemeRowMapper();
	
	@Override
	public void save(PricingScheme pricingScheme) {
		if (pricingScheme.getId() == null) {
			insert(pricingScheme);
		} else {
			update(pricingScheme);
		}
	}

	private static final String INSERT_SQL = "insert into PRICING_SCHEME (NAME) values (?)";
	
	private void insert(final PricingScheme pricingScheme) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, pricingScheme.getName());
				return ps;
			}
		}, holder);
		
		pricingScheme.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = "update PRICING_SCHEME set NAME = ? where ID = ?";
	
	private void update(PricingScheme pricingScheme) {
		getJdbcTemplate().update(UPDATE_SQL, pricingScheme.getName(), pricingScheme.getId());
	}

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by NAME";
	
	@Override
	public List<PricingScheme> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, pricingSchemeRowMapper);
	}
	
	private class PricingSchemeRowMapper implements RowMapper<PricingScheme> {

		@Override
		public PricingScheme mapRow(ResultSet rs, int rowNum) throws SQLException {
			PricingScheme pricingScheme = new PricingScheme();
			pricingScheme.setId(rs.getLong("ID"));
			pricingScheme.setName(rs.getString("NAME"));
			return pricingScheme;
		}
		
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where ID = ?";
	
	@Override
	public PricingScheme get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, pricingSchemeRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String CREATE_PRODUCT_PRICES_SQL =
			"insert into PRODUCT_PRICE (PRICING_SCHEME_ID, PRODUCT_ID) select ?, ID from PRODUCT";
	
	@Override
	public void createProductPrices(PricingScheme pricingScheme) {
		getJdbcTemplate().update(CREATE_PRODUCT_PRICES_SQL, pricingScheme.getId());
	}

}