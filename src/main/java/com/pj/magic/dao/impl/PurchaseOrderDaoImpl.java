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

import com.pj.magic.dao.PurchaseOrderDao;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.User;
import com.pj.magic.model.search.PurchaseOrderSearchCriteria;

@Repository
public class PurchaseOrderDaoImpl extends MagicDao implements PurchaseOrderDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PURCHASE_ORDER_NO, SUPPLIER_ID, POST_IND, DELIVERY_IND,"
			+ " a.PAYMENT_TERM_ID, a.REMARKS, REFERENCE_NO, POST_DT, a.VAT_INCLUSIVE,"
			+ " b.CODE as SUPPLIER_CODE, b.NAME as SUPPLIER_NAME,"
			+ " a.CREATED_BY, c.USERNAME as CREATED_BY_USERNAME"
			+ " from PURCHASE_ORDER a, SUPPLIER b, USER c"
			+ " where a.SUPPLIER_ID = b.ID"
			+ " and a.CREATED_BY = c.ID";
	
	private static final String PURCHASE_ORDER_NUMBER_SEQUENCE = "PURCHASE_ORDER_NO_SEQ";
	
	private PurchaseOrderRowMapper purchaseOrderRowMapper = new PurchaseOrderRowMapper();
	
	private static final String GET_SQL = BASE_SELECT_SQL + " and a.ID = ?";
	
	@Override
	public PurchaseOrder get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, purchaseOrderRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(PurchaseOrder purchaseOrder) {
		if (purchaseOrder.getId() == null) {
			insert(purchaseOrder);
		} else {
			update(purchaseOrder);
		}
	}
	
	private static final String INSERT_SQL =
			"insert into PURCHASE_ORDER"
			+ " (PURCHASE_ORDER_NO, SUPPLIER_ID, PAYMENT_TERM_ID, CREATED_BY, VAT_INCLUSIVE)"
			+ " values (?, ?, ?, ?, ?)";
	
	private void insert(final PurchaseOrder purchaseOrder) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextPurchaseOrderNumber());
				ps.setLong(2, purchaseOrder.getSupplier().getId());
				if (purchaseOrder.getPaymentTerm() != null) {
					ps.setLong(3, purchaseOrder.getPaymentTerm().getId());
				} else {
					ps.setNull(3, Types.INTEGER);
				}
				ps.setLong(4, purchaseOrder.getCreatedBy().getId());
				ps.setString(5, purchaseOrder.isVatInclusive() ? "Y" : "N");
				return ps;
			}
		}, holder);
		
		PurchaseOrder updated = get(holder.getKey().longValue());
		purchaseOrder.setId(updated.getId());
		purchaseOrder.setPurchaseOrderNumber(updated.getPurchaseOrderNumber());
	}
	
	private long getNextPurchaseOrderNumber() {
		return getNextSequenceValue(PURCHASE_ORDER_NUMBER_SEQUENCE);
	}

	private static final String UPDATE_SQL =
			"update PURCHASE_ORDER set SUPPLIER_ID = ?, POST_IND = ?, DELIVERY_IND = ?,"
			+ " PAYMENT_TERM_ID = ?, REMARKS = ?, REFERENCE_NO = ?, POST_DT = ?, VAT_INCLUSIVE = ?"
			+ " where ID = ?";
	
	private void update(PurchaseOrder purchaseOrder) {
		getJdbcTemplate().update(UPDATE_SQL, 
				purchaseOrder.getSupplier().getId(),
				purchaseOrder.isPosted() ? "Y" : "N",
				purchaseOrder.isDelivered() ? "Y" : "N",
				purchaseOrder.getPaymentTerm() != null ? purchaseOrder.getPaymentTerm().getId() : null,
				purchaseOrder.getRemarks(),
				purchaseOrder.getReferenceNumber(),
				purchaseOrder.getPostDate(),
				purchaseOrder.isVatInclusive() ? "Y" : "N",
				purchaseOrder.getId());
	}
	
	private class PurchaseOrderRowMapper implements RowMapper<PurchaseOrder> {

		@Override
		public PurchaseOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
			PurchaseOrder purchaseOrder = new PurchaseOrder();
			purchaseOrder.setId(rs.getLong("ID"));
			purchaseOrder.setPurchaseOrderNumber(rs.getLong("PURCHASE_ORDER_NO"));
			
			Supplier supplier = new Supplier();
			supplier.setId(rs.getLong("SUPPLIER_ID"));
			supplier.setCode(rs.getString("SUPPLIER_CODE"));
			supplier.setName(rs.getString("SUPPLIER_NAME"));
			purchaseOrder.setSupplier(supplier);
			
			purchaseOrder.setPosted("Y".equals(rs.getString("POST_IND")));
			purchaseOrder.setDelivered("Y".equals(rs.getString("DELIVERY_IND")));
			if (rs.getLong("PAYMENT_TERM_ID") != 0) {
				purchaseOrder.setPaymentTerm(new PaymentTerm(rs.getLong("PAYMENT_TERM_ID")));
			}
			purchaseOrder.setRemarks(rs.getString("REMARKS"));
			purchaseOrder.setReferenceNumber(rs.getString("REFERENCE_NO"));
			purchaseOrder.setPostDate(rs.getDate("POST_DT"));
			purchaseOrder.setCreatedBy(new User(rs.getLong("CREATED_BY"), rs.getString("CREATED_BY_USERNAME")));
			purchaseOrder.setVatInclusive("Y".equals(rs.getString("VAT_INCLUSIVE")));
			return purchaseOrder;
		}
	}

	private static final String DELETE_SQL = "delete from PURCHASE_ORDER where ID = ?";
	
	@Override
	public void delete(PurchaseOrder purchaseOrder) {
		getJdbcTemplate().update(DELETE_SQL, purchaseOrder.getId());
	}

	@Override
	public List<PurchaseOrder> search(PurchaseOrderSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		List<Object> params = new ArrayList<>();
		
		if (criteria.getPurchaseOrderNumber() != null) {
			sql.append(" and a.PURCHASE_ORDER_NO = ?");
			params.add(criteria.getPurchaseOrderNumber());
		}
		
		if (criteria.getSupplier() != null) {
			sql.append(" and b.ID = ?");
			params.add(criteria.getSupplier().getId());
		}
		
		if (criteria.getPosted() != null) {
			sql.append(" and a.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}
		
		sql.append(" order by a.ID desc");
		
		return getJdbcTemplate().query(sql.toString(), purchaseOrderRowMapper, params.toArray());
	}

	private static final String FIND_ALL_BY_SUPPLIER_SQL =
			BASE_SELECT_SQL + " and b.ID = ?";
	
	@Override
	public List<PurchaseOrder> findAllBySupplier(Supplier supplier) {
		return getJdbcTemplate().query(FIND_ALL_BY_SUPPLIER_SQL, purchaseOrderRowMapper, supplier.getId());
	}

}
