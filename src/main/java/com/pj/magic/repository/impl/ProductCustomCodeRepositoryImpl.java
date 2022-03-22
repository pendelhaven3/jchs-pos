package com.pj.magic.repository.impl;

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

import com.pj.magic.dao.impl.MagicDao;
import com.pj.magic.model.Product2;
import com.pj.magic.model.ProductCustomCode;
import com.pj.magic.repository.ProductCustomCodeRepository;

@Repository
public class ProductCustomCodeRepositoryImpl extends MagicDao implements ProductCustomCodeRepository {

	private static final String BASE_SELECT_SQL =
			"select a.ID, a.PRODUCT_ID, a.CODE, a.REMARKS"
			+ " from PRODUCT_CUSTOM_CODE a"
			+ " where 1 = 1";
	
	private RowMapper<ProductCustomCode> rowMapper = new RowMapper<ProductCustomCode>() {

		@Override
		public ProductCustomCode mapRow(ResultSet rs, int rowNum) throws SQLException {
			ProductCustomCode customCode = new ProductCustomCode();
			customCode.setId(rs.getLong("ID"));
			customCode.setProduct(new Product2(rs.getLong("PRODUCT_ID")));
			customCode.setCode(rs.getString("CODE"));
			customCode.setRemarks(rs.getString("REMARKS"));
			return customCode;
		}
		
	};
	
	private static final String FIND_ALL_BY_PRODUCT_ID_SQL = BASE_SELECT_SQL + " and a.PRODUCT_ID = ? order by a.CODE";
	
	@Override
	public List<ProductCustomCode> findAllByProductId(Long id) {
		return getJdbcTemplate().query(FIND_ALL_BY_PRODUCT_ID_SQL, rowMapper, id);
	}

	private static final String FIND_BY_PRODUCT_ID_AND_CODE_SQL = BASE_SELECT_SQL + " and a.PRODUCT_ID = ? and a.CODE = ?";
	
	@Override
	public ProductCustomCode findByProductIdAndCode(Long productId, String code) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_PRODUCT_ID_AND_CODE_SQL, rowMapper, productId, code);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(ProductCustomCode customCode) {
		if (customCode.getId() == null) {
			insert(customCode);
		} else {
			update(customCode);
		}
	}
	
	private static final String INSERT_SQL = "insert into PRODUCT_CUSTOM_CODE (PRODUCT_ID, CODE, REMARKS) values (?, ?, ?)";
	
	private void insert(final ProductCustomCode customCode) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, customCode.getProduct().getId());
				ps.setString(2, customCode.getCode());
				ps.setString(3, customCode.getRemarks());
				return ps;
			}
		}, holder);

		customCode.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = "update PRODUCT_CUSTOM_CODE set CODE = ?, REMARKS = ? where ID = ?";
	
	private void update(ProductCustomCode customCode) {
		getJdbcTemplate().update(UPDATE_SQL,
				customCode.getCode(),
				customCode.getRemarks(),
				customCode.getId());
	}

	@Override
	public void delete(ProductCustomCode customCode) {
		getJdbcTemplate().update("delete from PRODUCT_CUSTOM_CODE where ID = ?", customCode.getId());
	}
	
}
