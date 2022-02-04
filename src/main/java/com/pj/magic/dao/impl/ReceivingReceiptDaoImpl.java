package com.pj.magic.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ReceivingReceiptDao;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.ProductCanvassItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.User;
import com.pj.magic.model.search.ProductCanvassSearchCriteria;
import com.pj.magic.model.search.ReceivingReceiptSearchCriteria;
import com.pj.magic.util.DbUtil;

/**
 * 
 * @author PJ Miranda
 *
 */
@Repository
public class ReceivingReceiptDaoImpl extends MagicDao implements ReceivingReceiptDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, RECEIVING_RECEIPT_NO, SUPPLIER_ID, POST_IND, a.VAT_INCLUSIVE, VAT_RATE,"
			+ " a.PAYMENT_TERM_ID, c.NAME as PAYMENT_TERM_NAME, a.REMARKS, REFERENCE_NO, RECEIVED_DT,"
			+ " POST_DT, POST_BY, CANCEL_IND, CANCEL_DT, CANCEL_BY,"
			+ " RELATED_PURCHASE_ORDER_NO, RECEIVED_BY, b.CODE as SUPPLIER_CODE, b.NAME as SUPPLIER_NAME,"
			+ " d.USERNAME as POST_BY_USERNAME, e.USERNAME as CANCEL_BY_USERNAME"
			+ " from RECEIVING_RECEIPT a"
			+ " join SUPPLIER b"
			+ "   on b.ID = a.SUPPLIER_ID"
			+ " join PAYMENT_TERM c"
			+ "   on c.ID = a.PAYMENT_TERM_ID"
			+ " left join USER d"
			+ "   on d.ID = a.POST_BY"
			+ " left join USER e"
			+ "   on e.ID = a.CANCEL_BY"
			+ " where 1 = 1";

	private static final String RECEIVING_RECEIPT_NUMBER_SEQUENCE = "RECEIVING_RECEIPT_NO_SEQ";
	
	private ReceivingReceiptRowMapper receivingReceiptRowMapper = new ReceivingReceiptRowMapper();
	
	private static final String GET_SQL = BASE_SELECT_SQL + " and a.ID = ?";
	
	@Override
	public ReceivingReceipt get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, receivingReceiptRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(ReceivingReceipt receivingReceipt) {
		if (receivingReceipt.getId() == null) {
			insert(receivingReceipt);
		} else {
			update(receivingReceipt);
		}
	}
	
	private static final String INSERT_SQL =
			"insert into RECEIVING_RECEIPT"
			+ " (RECEIVING_RECEIPT_NO, SUPPLIER_ID, PAYMENT_TERM_ID, REFERENCE_NO, REMARKS, RECEIVED_DT, "
			+ "  RELATED_PURCHASE_ORDER_NO, RECEIVED_BY, VAT_INCLUSIVE, VAT_RATE)"
			+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private void insert(final ReceivingReceipt receivingReceipt) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextReceivingReceiptNumber());
				ps.setLong(2, receivingReceipt.getSupplier().getId());
				ps.setLong(3, receivingReceipt.getPaymentTerm().getId());
				ps.setString(4, receivingReceipt.getReferenceNumber());
				ps.setString(5, receivingReceipt.getRemarks());
				ps.setDate(6, new Date(receivingReceipt.getReceivedDate().getTime()));
				ps.setLong(7, receivingReceipt.getRelatedPurchaseOrderNumber());
				ps.setLong(8, receivingReceipt.getReceivedBy().getId());
				ps.setString(9,  receivingReceipt.isVatInclusive() ? "Y" : "N");
				ps.setBigDecimal(10, receivingReceipt.getVatRate());
				return ps;
			}
		}, holder);
		
		ReceivingReceipt updated = get(holder.getKey().longValue());
		receivingReceipt.setId(updated.getId());
		receivingReceipt.setReceivingReceiptNumber(updated.getReceivingReceiptNumber());
	}
	
	private long getNextReceivingReceiptNumber() {
		return getNextSequenceValue(RECEIVING_RECEIPT_NUMBER_SEQUENCE);
	}

	private static final String UPDATE_SQL =
			"update RECEIVING_RECEIPT set SUPPLIER_ID = ?, POST_IND = ?, POST_DT = ?, POST_BY = ?,"
			+ " PAYMENT_TERM_ID = ?, REMARKS = ?, REFERENCE_NO = ?, RECEIVED_DT = ?,"
			+ " CANCEL_IND = ?, CANCEL_DT = ?, CANCEL_BY = ?"
			+ " where ID = ?";
	
	private void update(ReceivingReceipt receivingReceipt) {
		getJdbcTemplate().update(UPDATE_SQL, 
				receivingReceipt.getSupplier().getId(),
				receivingReceipt.isPosted() ? "Y" : "N",
				receivingReceipt.isPosted() ? receivingReceipt.getPostDate() : null,
				receivingReceipt.isPosted() ? receivingReceipt.getPostedBy().getId() : null,
				receivingReceipt.getPaymentTerm().getId(),
				receivingReceipt.getRemarks(),
				receivingReceipt.getReferenceNumber(),
				receivingReceipt.getReceivedDate(),
				receivingReceipt.isCancelled() ? "Y" : "N",
				receivingReceipt.isCancelled() ? receivingReceipt.getCancelDate() : null,
				receivingReceipt.isCancelled() ? receivingReceipt.getCancelledBy().getId() : null,
				receivingReceipt.getId());
	}
	
	private class ReceivingReceiptRowMapper implements RowMapper<ReceivingReceipt> {

		@Override
		public ReceivingReceipt mapRow(ResultSet rs, int rowNum) throws SQLException {
			ReceivingReceipt receivingReceipt = new ReceivingReceipt();
			receivingReceipt.setId(rs.getLong("ID"));
			receivingReceipt.setReceivingReceiptNumber(rs.getLong("RECEIVING_RECEIPT_NO"));
			
			Supplier supplier = new Supplier();
			supplier.setId(rs.getLong("SUPPLIER_ID"));
			supplier.setCode(rs.getString("SUPPLIER_CODE"));
			supplier.setName(rs.getString("SUPPLIER_NAME"));
			receivingReceipt.setSupplier(supplier);
			
			receivingReceipt.setPosted("Y".equals(rs.getString("POST_IND")));
			if (receivingReceipt.isPosted()) {
				receivingReceipt.setPostDate(rs.getTimestamp("POST_DT"));
				receivingReceipt.setPostedBy(
						new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			}
			receivingReceipt.setCancelled("Y".equals(rs.getString("CANCEL_IND")));
			if (receivingReceipt.isCancelled()) {
				receivingReceipt.setCancelDate(rs.getDate("CANCEL_DT"));
				receivingReceipt.setCancelledBy(
						new User(rs.getLong("CANCEL_BY"), rs.getString("CANCEL_BY_USERNAME")));
			}
			receivingReceipt.setPaymentTerm(
					new PaymentTerm(rs.getLong("PAYMENT_TERM_ID"), rs.getString("PAYMENT_TERM_NAME")));
			receivingReceipt.setRemarks(rs.getString("REMARKS"));
			receivingReceipt.setReferenceNumber(rs.getString("REFERENCE_NO"));
			receivingReceipt.setReceivedDate(rs.getDate("RECEIVED_DT"));
			receivingReceipt.setRelatedPurchaseOrderNumber(rs.getLong("RELATED_PURCHASE_ORDER_NO"));
			receivingReceipt.setReceivedBy(new User(rs.getLong("RECEIVED_BY")));
			receivingReceipt.setVatInclusive("Y".equals(rs.getString("VAT_INCLUSIVE")));
			receivingReceipt.setVatRate(rs.getBigDecimal("VAT_RATE"));
			return receivingReceipt;
		}
	}

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by a.ID desc";
	
	@Override
	public List<ReceivingReceipt> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, receivingReceiptRowMapper);
	}

	private static final String GET_PRODUCT_CANVASS_ITEMS_SQL =
			"select b.RECEIVED_DT, b.RECEIVING_RECEIPT_NO, c.NAME as SUPPLIER_NAME, a.QUANTITY, a.COST,"
			+ " b.REFERENCE_NO, a.DISCOUNT_1, a.DISCOUNT_2, a.DISCOUNT_3, a.FLAT_RATE_DISCOUNT,"
			+ " a.UNIT, b.VAT_INCLUSIVE, b.VAT_RATE"
			+ " from RECEIVING_RECEIPT_ITEM a"
			+ " join RECEIVING_RECEIPT b"
			+ "   on b.ID = a.RECEIVING_RECEIPT_ID"
			+ " join SUPPLIER c"
			+ "   on c.ID = b.SUPPLIER_ID"
			+ " where a.PRODUCT_ID = ?"
			+ " and b.POST_IND = 'Y'";
	
	@Override
	public List<ProductCanvassItem> getProductCanvassItems(ProductCanvassSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(GET_PRODUCT_CANVASS_ITEMS_SQL);
		List<Object> params = new ArrayList<>();
	
		params.add(criteria.getProduct().getId());
		
		if (criteria.getDateFrom() != null) {
			sql.append(" and b.RECEIVED_DT >= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getDateFrom()));
		}
		
		if (criteria.getDateTo() != null) {
			sql.append(" and b.RECEIVED_DT <= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getDateTo()));
		}
		
		sql.append(" order by RECEIVED_DT desc, RECEIVING_RECEIPT_NO desc");
		
		return getJdbcTemplate().query(sql.toString(), new RowMapper<ProductCanvassItem>() {

			@Override
			public ProductCanvassItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				ReceivingReceiptItem item = new ReceivingReceiptItem();
				item.setQuantity(rs.getInt("QUANTITY"));
				item.setCost(rs.getBigDecimal("COST"));
				item.setDiscount1(rs.getBigDecimal("DISCOUNT_1"));
				item.setDiscount2(rs.getBigDecimal("DISCOUNT_2"));
				item.setDiscount3(rs.getBigDecimal("DISCOUNT_3"));
				item.setFlatRateDiscount(rs.getBigDecimal("FLAT_RATE_DISCOUNT"));
				
				ReceivingReceipt receivingReceipt = new ReceivingReceipt();
				receivingReceipt.setVatInclusive("Y".equals(rs.getString("VAT_INCLUSIVE")));
				receivingReceipt.setVatRate(rs.getBigDecimal("VAT_RATE"));
				BigDecimal costMultipler = receivingReceipt.getVatMultiplier();
				
				ProductCanvassItem canvassItem = new ProductCanvassItem();
				canvassItem.setReceivedDate(rs.getDate("RECEIVED_DT"));
				canvassItem.setReceivingReceiptNumber(rs.getLong("RECEIVING_RECEIPT_NO"));
				canvassItem.setSupplier(new Supplier(rs.getString("SUPPLIER_NAME")));
				canvassItem.setUnit(rs.getString("UNIT"));
				canvassItem.setFinalCost(
						item.getFinalCost().multiply(costMultipler).setScale(2, RoundingMode.HALF_UP));
				canvassItem.setGrossCost(
						rs.getBigDecimal("COST").multiply(costMultipler).setScale(2, RoundingMode.HALF_UP));
				canvassItem.setReferenceNumber(rs.getString("REFERENCE_NO"));
				return canvassItem;
			}
			
		}, params.toArray());
	}

	private static final String UNPAID_WHERE_CLAUSE_SQL = 
			" and a.CANCEL_IND = 'N'"
			+ " and not exists("
			+ "   select 1"
			+ "   from PURCHASE_PAYMENT_RECEIVING_RECEIPT pprr"
			+ "   join PURCHASE_PAYMENT pp"
			+ "     on pp.ID = pprr.PURCHASE_PAYMENT_ID"
			+ "   where pprr.RECEIVING_RECEIPT_ID = a.ID"
			+ "   and pp.POST_IND = 'Y'"
			+ " )"
			+ " and a.RECEIVED_DT >= '2015-01-01'";
	
	@Override
	public List<ReceivingReceipt> search(ReceivingReceiptSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		List<Object> params = new ArrayList<>();
		
		if (criteria.getReceivingReceiptNumber() != null) {
			sql.append(" and a.RECEIVING_RECEIPT_NO = ?");
			params.add(criteria.getReceivingReceiptNumber());
		}
		
		if (criteria.getSupplier() != null) {
			sql.append(" and b.ID = ?");
			params.add(criteria.getSupplier().getId());
		}
		
		if (criteria.getPosted() != null) {
			sql.append(" and a.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}

		if (criteria.getCancelled() != null) {
			sql.append(" and a.CANCEL_IND = ?");
			params.add(criteria.getCancelled() ? "Y" : "N");
		}

		if (criteria.getReceivedDate() != null) {
			sql.append(" and a.RECEIVED_DT = ?");
			params.add(DbUtil.toMySqlDateString(criteria.getReceivedDate()));
		}
		
		if (criteria.getReceivedDateFrom() != null) {
			sql.append(" and a.RECEIVED_DT >= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getReceivedDateFrom()));
		}
		
		if (criteria.getReceivedDateTo() != null) {
			sql.append(" and a.RECEIVED_DT <= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getReceivedDateTo()));
		}
		
		if (criteria.getPaid() != null) {
			if (!criteria.getPaid()) {
				sql.append(UNPAID_WHERE_CLAUSE_SQL);
			}
		}
		
		if (!StringUtils.isEmpty(criteria.getOrderBy())) {
			sql.append(" order by ").append(criteria.getOrderBy());
		} else {
			sql.append(" order by a.ID desc");
		}
		
		return getJdbcTemplate().query(sql.toString(), receivingReceiptRowMapper, params.toArray());
	}

	private static final String FIND_ALL_FOR_PAYMENT_BY_SUPPLIER_SQL = BASE_SELECT_SQL
			+ " and a.SUPPLIER_ID = ?"
			+ " and a.CANCEL_IND = 'N'"
			+ " and not exists ("
			+ "   select 1"
			+ "   from PURCHASE_PAYMENT_RECEIVING_RECEIPT pprr"
			+ "   join PURCHASE_PAYMENT pp"
			+ "     on pp.ID = pprr.PURCHASE_PAYMENT_ID"
			+ "   where pprr.RECEIVING_RECEIPT_ID = a.ID"
			+ "   and pp.CANCEL_IND = 'N'"
			+ " )"
			+ " and a.RECEIVED_DT >= '2015-01-01'";
	
	@Override
	public List<ReceivingReceipt> findAllForPaymentBySupplier(Supplier supplier) {
		return getJdbcTemplate().query(FIND_ALL_FOR_PAYMENT_BY_SUPPLIER_SQL, receivingReceiptRowMapper, 
				supplier.getId());
	}

	private static final String FIND_BY_RECEIVING_RECEIPT_NUMBER_SQL = BASE_SELECT_SQL
			+ " and RECEIVING_RECEIPT_NO = ?";
	
	@Override
	public ReceivingReceipt findByReceivingReceiptNumber(long receivingReceiptNumber) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_RECEIVING_RECEIPT_NUMBER_SQL,
					receivingReceiptRowMapper, receivingReceiptNumber);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

}