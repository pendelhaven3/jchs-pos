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

import com.pj.magic.dao.PurchasePaymentAdjustmentTypeDao;
import com.pj.magic.model.PurchasePaymentAdjustmentType;

@Repository
public class PurchasePaymentAdjustmentTypeDaoImpl extends MagicDao implements PurchasePaymentAdjustmentTypeDao {

	private static final String BASE_SELECT_SQL =
			"select ID, CODE, DESCRIPTION"
			+ " from PURCHASE_PAYMENT_ADJ_TYPE"
			+ " where 1 = 1";
	
	private RowMapper<PurchasePaymentAdjustmentType> rowMapper = new RowMapper<PurchasePaymentAdjustmentType>() {

		@Override
		public PurchasePaymentAdjustmentType mapRow(ResultSet rs, int rowNum) throws SQLException {
			PurchasePaymentAdjustmentType adjustmentType = new PurchasePaymentAdjustmentType();
			adjustmentType.setId(rs.getLong("ID"));
			adjustmentType.setCode(rs.getString("CODE"));
			adjustmentType.setDescription(rs.getString("DESCRIPTION"));
			return adjustmentType;
		}
		
	};
	
	@Override
	public void save(PurchasePaymentAdjustmentType adjustmentType) {
		if (adjustmentType.getId() == null) {
			insert(adjustmentType);
		} else {
			update(adjustmentType);
		}
	}

	private static final String INSERT_SQL =
			"insert into PURCHASE_PAYMENT_ADJ_TYPE"
			+ " (CODE, DESCRIPTION)"
			+ " values"
			+ " (?, ?)";
	
	private void insert(final PurchasePaymentAdjustmentType adjustmentType) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, adjustmentType.getCode());
				ps.setString(2, adjustmentType.getDescription());
				return ps;
			}
		}, holder);

		adjustmentType.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL =
			"update PURCHASE_PAYMENT_ADJ_TYPE"
			+ " set CODE= ?, DESCRIPTION = ?"
			+ " where ID = ?";
	
	private void update(PurchasePaymentAdjustmentType adjustmentType) {
		getJdbcTemplate().update(UPDATE_SQL,
				adjustmentType.getCode(),
				adjustmentType.getDescription(),
				adjustmentType.getId());
	}
	
	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by CODE";
	
	@Override
	public List<PurchasePaymentAdjustmentType> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, rowMapper);
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " and ID = ?";
	
	@Override
	public PurchasePaymentAdjustmentType get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, rowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String FIND_BY_CODE_SQL = BASE_SELECT_SQL + " and CODE = ?";
	
	@Override
	public PurchasePaymentAdjustmentType findByCode(String code) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_CODE_SQL, rowMapper, code);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

}