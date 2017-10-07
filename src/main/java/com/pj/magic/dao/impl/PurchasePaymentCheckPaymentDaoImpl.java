package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PurchasePaymentCheckPaymentDao;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCheckPayment;
import com.pj.magic.model.search.PurchasePaymentCheckPaymentSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class PurchasePaymentCheckPaymentDaoImpl extends MagicDao implements PurchasePaymentCheckPaymentDao {

	private static final String BASE_SELECT_SQL = 
			"select a.ID, PURCHASE_PAYMENT_ID, BANK, CHECK_DT, CHECK_NO, AMOUNT,"
			+ " b.SUPPLIER_ID, c.NAME as SUPPLIER_NAME,"
			+ " b.PURCHASE_PAYMENT_NO"
			+ " from PURCHASE_PAYMENT_CHECK_PAYMENT a"
			+ " join PURCHASE_PAYMENT b"
			+ "   on b.ID = a.PURCHASE_PAYMENT_ID"
			+ " join SUPPLIER c"
			+ "   on c.ID = b.SUPPLIER_ID";
	
	private PurchasePaymentCheckPaymentRowMapper checkPaymentRowMapper = 
			new PurchasePaymentCheckPaymentRowMapper();
	
	@Override
	public void save(PurchasePaymentCheckPayment checkPayment) {
		if (checkPayment.getId() == null) {
			insert(checkPayment);
		} else {
			update(checkPayment);
		}
	}

	private static final String INSERT_SQL = 
			"insert into PURCHASE_PAYMENT_CHECK_PAYMENT"
			+ " (PURCHASE_PAYMENT_ID, BANK, CHECK_DT, CHECK_NO, AMOUNT) values (?, ?, ?, ?, ?)";
	
	private void insert(final PurchasePaymentCheckPayment checkPayment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, checkPayment.getParent().getId());
				ps.setString(2, checkPayment.getBank());
				ps.setDate(3, new Date(checkPayment.getCheckDate().getTime()));
				ps.setString(4, checkPayment.getCheckNumber());
				ps.setBigDecimal(5, checkPayment.getAmount());
				return ps;
			}
		}, holder);
		
		checkPayment.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = 
			"update PURCHASE_PAYMENT_CHECK_PAYMENT"
			+ " set BANK = ?, CHECK_DT = ?, CHECK_NO = ?, AMOUNT = ?"
			+ " where ID = ?";
	
	private void update(PurchasePaymentCheckPayment checkPayment) {
		getJdbcTemplate().update(UPDATE_SQL, 
				checkPayment.getBank(), 
				checkPayment.getCheckDate(),
				checkPayment.getCheckNumber(),
				checkPayment.getAmount(),
				checkPayment.getId());
	}

	private static final String FIND_ALL_BY_PURCHASE_PAYMENT_SQL = BASE_SELECT_SQL
			+ " where a.PURCHASE_PAYMENT_ID = ?";
	
	@Override
	public List<PurchasePaymentCheckPayment> findAllByPurchasePayment(PurchasePayment purchasePayment) {
		return getJdbcTemplate().query(FIND_ALL_BY_PURCHASE_PAYMENT_SQL, checkPaymentRowMapper, 
				purchasePayment.getId());
	}

	private class PurchasePaymentCheckPaymentRowMapper implements RowMapper<PurchasePaymentCheckPayment> {

		@Override
		public PurchasePaymentCheckPayment mapRow(ResultSet rs, int rowNum) throws SQLException {
			PurchasePaymentCheckPayment check = new PurchasePaymentCheckPayment();
			check.setId(rs.getLong("ID"));
			
			PurchasePayment payment = new PurchasePayment();
			payment.setId(rs.getLong("PURCHASE_PAYMENT_ID"));
			payment.setPurchasePaymentNumber(rs.getLong("PURCHASE_PAYMENT_NO"));
			check.setParent(payment);
			
			check.setBank(rs.getString("BANK"));
			check.setCheckDate(rs.getDate("CHECK_DT"));
			check.setCheckNumber(rs.getString("CHECK_NO"));
			check.setAmount(rs.getBigDecimal("AMOUNT"));
			
			check.getParent().setSupplier(
					new Supplier(rs.getLong("SUPPLIER_ID"), rs.getString("SUPPLIER_NAME")));
			
			return check;
		}
		
	}
	
	private static final String DELETE_SQL = "delete from PURCHASE_PAYMENT_CHECK_PAYMENT where ID = ?";
	
	@Override
	public void delete(PurchasePaymentCheckPayment checkPayment) {
		getJdbcTemplate().update(DELETE_SQL, checkPayment.getId());
	}

	@Override
	public List<PurchasePaymentCheckPayment> search(PurchasePaymentCheckPaymentSearchCriteria criteria) {
		List<Object> params = new ArrayList<>();
		
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		if (criteria.getSupplier() != null) {
			sql.append(" and c.ID = ?");
			params.add(criteria.getSupplier().getId());
		}
		
		if (criteria.getPosted() != null) {
			sql.append(" and b.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}

		if (criteria.getFromDate() != null) {
			sql.append(" and a.CHECK_DT >= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getFromDate()));
		}
		
		if (criteria.getToDate() != null) {
			sql.append(" and a.CHECK_DT <= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getToDate()));
		}
		
        if (criteria.getAmount() != null) {
            sql.append(" and a.AMOUNT = ?");
            params.add(criteria.getAmount());
        }
        
		sql.append(" order by a.CHECK_DT, c.NAME, b.PURCHASE_PAYMENT_NO, a.BANK");
		
		return getJdbcTemplate().query(sql.toString(), checkPaymentRowMapper, params.toArray());
	}

}