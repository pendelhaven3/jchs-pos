package com.pj.magic.dao.impl;

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

import com.pj.magic.dao.PurchaseReturnItemDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.PurchaseReturn;
import com.pj.magic.model.PurchaseReturnItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;

@Repository
public class PurchaseReturnItemDaoImpl extends MagicDao implements PurchaseReturnItemDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PURCHASE_RETURN_ID, a.QUANTITY, b.PRODUCT_ID, "
			+ " c.CODE as PRODUCT_CODE, c.DESCRIPTION as PRODUCT_DESCRIPTION,"
			+ " a.RECEIVING_RECEIPT_ITEM_ID, b.UNIT, b.QUANTITY as RECEIVING_RECEIPT_ITEM_QUANTITY,"
			+ " b.COST as UNIT_COST, b.DISCOUNT_1, b.DISCOUNT_2, b.DISCOUNT_3, b.FLAT_RATE_DISCOUNT,"
			+ " d.VAT_INCLUSIVE, d.VAT_RATE"
			+ " from PURCHASE_RETURN_ITEM a"
			+ " join RECEIVING_RECEIPT_ITEM b"
			+ "   on b.ID = a.RECEIVING_RECEIPT_ITEM_ID"
			+ " join PRODUCT c"
			+ "   on c.ID = b.PRODUCT_ID"
			+ " join RECEIVING_RECEIPT d"
			+ "   on d.ID = b.RECEIVING_RECEIPT_ID";
	
	private PurchaseReturnItemRowMapper purchaseReturnItemRowMapper = new PurchaseReturnItemRowMapper();
	
	private static final String FIND_ALL_BY_PURCHASE_RETURN_SQL = BASE_SELECT_SQL
			+ " where a.PURCHASE_RETURN_ID = ?";
	
	@Override
	public List<PurchaseReturnItem> findAllByPurchaseReturn(PurchaseReturn purchaseReturn) {
		return getJdbcTemplate().query(FIND_ALL_BY_PURCHASE_RETURN_SQL, purchaseReturnItemRowMapper,
				purchaseReturn.getId());
	}

	private class PurchaseReturnItemRowMapper implements RowMapper<PurchaseReturnItem> {

		@Override
		public PurchaseReturnItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			PurchaseReturnItem item = new PurchaseReturnItem();
			item.setId(rs.getLong("ID"));
			item.setParent(new PurchaseReturn(rs.getLong("PURCHASE_RETURN_ID")));
			item.setQuantity(rs.getInt("QUANTITY"));
			
			Product product = new Product();
			product.setId(rs.getLong("PRODUCT_ID"));
			product.setCode(rs.getString("PRODUCT_CODE"));
			product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
			
			ReceivingReceiptItem receivingReceiptItem = new ReceivingReceiptItem();
			receivingReceiptItem.setId(rs.getLong("RECEIVING_RECEIPT_ITEM_ID"));
			receivingReceiptItem.setProduct(product);
			receivingReceiptItem.setUnit(rs.getString("UNIT"));
			receivingReceiptItem.setQuantity(rs.getInt("RECEIVING_RECEIPT_ITEM_QUANTITY"));
			receivingReceiptItem.setCost(rs.getBigDecimal("UNIT_COST"));
			receivingReceiptItem.setDiscount1(rs.getBigDecimal("DISCOUNT_1"));
			receivingReceiptItem.setDiscount2(rs.getBigDecimal("DISCOUNT_2"));
			receivingReceiptItem.setDiscount3(rs.getBigDecimal("DISCOUNT_3"));
			receivingReceiptItem.setFlatRateDiscount(rs.getBigDecimal("FLAT_RATE_DISCOUNT"));
			
			ReceivingReceipt receivingReceipt = new ReceivingReceipt();
			receivingReceipt.setVatRate(rs.getBigDecimal("VAT_RATE"));
			receivingReceipt.setVatInclusive("Y".equals(rs.getString("VAT_INCLUSIVE")));
			receivingReceiptItem.setParent(receivingReceipt);
			
			item.setReceivingReceiptItem(receivingReceiptItem);
			
			return item;
		}
		
	}

	@Override
	public void save(PurchaseReturnItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL = "insert into PURCHASE_RETURN_ITEM"
			+ " (PURCHASE_RETURN_ID, RECEIVING_RECEIPT_ITEM_ID, QUANTITY) values (?, ?, ?)";
	
	private void insert(final PurchaseReturnItem item) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, item.getParent().getId());
				ps.setLong(2, item.getReceivingReceiptItem().getId());
				ps.setInt(3, item.getQuantity());
				return ps;
			}
		}, holder);
		
		item.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = "update PURCHASE_RETURN_ITEM"
			+ " set RECEIVING_RECEIPT_ITEM_ID = ?, QUANTITY = ? where ID = ?";
	
	private void update(PurchaseReturnItem item) {
		getJdbcTemplate().update(UPDATE_SQL, 
				item.getReceivingReceiptItem().getId(),
				item.getQuantity(),
				item.getId());
	}

	private static final String DELETE_SQL = "delete from PURCHASE_RETURN_ITEM where ID = ?";
	
	@Override
	public void delete(PurchaseReturnItem item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}

	private static final String DELETE_ALL_BY_PURCHASE_RETURN_SQL =
			"delete from PURCHASE_RETURN_ITEM where PURCHASE_RETURN_ID = ?";
	
	@Override
	public void deleteAllByPurchaseReturn(PurchaseReturn purchaseReturn) {
		getJdbcTemplate().update(DELETE_ALL_BY_PURCHASE_RETURN_SQL, purchaseReturn.getId());
	}
	
}