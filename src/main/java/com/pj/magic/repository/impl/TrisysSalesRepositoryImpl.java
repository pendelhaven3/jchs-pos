package com.pj.magic.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.impl.MagicDao;
import com.pj.magic.model.TrisysSalesImport;
import com.pj.magic.model.TrisysSales;
import com.pj.magic.repository.TrisysSalesRepository;
import com.pj.magic.util.DbUtil;

@Repository
public class TrisysSalesRepositoryImpl extends MagicDao implements TrisysSalesRepository {

	private static final String BASE_SELECT_SQL =
			"select a.ID, a.TRISYS_SALES_IMPORT_ID, a.SALE_NO, a.TERMINAL, a.SALE_DT"
			+ " from TRISYS_SALES a";
	
	private RowMapper<TrisysSales> rowMapper = new RowMapper<TrisysSales>() {

		@Override
		public TrisysSales mapRow(ResultSet rs, int rowNum) throws SQLException {
			TrisysSales sales = new TrisysSales();
			sales.setId(rs.getLong("ID"));
			sales.setSalesImport(new TrisysSalesImport(rs.getLong("TRISYS_SALES_IMPORT_ID")));
			sales.setSaleNumber(rs.getString("SALE_NO"));
			sales.setTerminal(rs.getString("TERMINAL"));
			sales.setSalesDate(rs.getDate("SALE_DT"));
			return sales;
		}
	};
	
	private static final String FIND_ALL_BY_SALES_IMPORT_SQL = BASE_SELECT_SQL + " where a.TRISYS_SALES_IMPORT_ID = ? order by a.SALE_NO";

	@Override
	public List<TrisysSales> findAllBySalesImport(TrisysSalesImport salesImport) {
		return getJdbcTemplate().query(FIND_ALL_BY_SALES_IMPORT_SQL, rowMapper, salesImport.getId());
	}
	
	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public TrisysSales get(long id) {
		return getJdbcTemplate().queryForObject(GET_SQL, new Object[] {id}, rowMapper);
	}

	private static final String INSERT_SQL =
			"insert into TRISYS_SALES"
			+ " (TRISYS_SALES_IMPORT_ID, SALE_NO, TERMINAL, SALE_DT)"
			+ " values"
			+ " (?, ?, ?, ?)";

	@Override
	public void save(TrisysSales sales) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, sales.getSalesImport().getId());
				ps.setString(2, sales.getSaleNumber());
				ps.setString(3, sales.getTerminal());
				ps.setDate(4, DbUtil.toSqlDate(sales.getSalesDate()));
				return ps;
			}
		}, holder);
		
		sales.setId(holder.getKey().longValue());
	}
	
}
