package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.pj.magic.dao.ReportDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Product;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.report.CustomerSalesSummaryReportItem;
import com.pj.magic.model.report.EwtReportItem;
import com.pj.magic.model.report.InventoryReportItem;
import com.pj.magic.model.report.PilferageReportItem;
import com.pj.magic.model.report.SalesByManufacturerReportItem;
import com.pj.magic.model.report.StockOfftakeReportItem;
import com.pj.magic.model.search.EwtReportCriteria;
import com.pj.magic.model.search.InventoryReportCriteria;
import com.pj.magic.model.search.PilferageReportCriteria;
import com.pj.magic.model.search.SalesByManufacturerReportCriteria;
import com.pj.magic.model.search.StockCardInventoryReportCriteria;
import com.pj.magic.model.search.StockOfftakeReportCriteria;
import com.pj.magic.util.DbUtil;
import com.pj.magic.util.QueriesUtil;

@Repository
public class ReportDaoImpl extends MagicDao implements ReportDao {

	private StockCardInventoryReportItemRowMapper rowMapper = new StockCardInventoryReportItemRowMapper();
	
	@Override
	public List<StockCardInventoryReportItem> getStockCardInventoryReport(
			StockCardInventoryReportCriteria criteria) {
		StringBuilder sql = new StringBuilder(QueriesUtil.getSql("stockCardInventoryReport"));
		sql.append(" where 1 = 1");
		
		Map<String, Object> params = new HashMap<>();
		params.put("product_code", criteria.getProduct().getCode());
		
		if (criteria.getFromDateTime() != null) {
			sql.append(" and POST_DT >= :fromDate");
			params.put("fromDate", DbUtil.toMySqlDateTimeString(criteria.getFromDateTime()));
		} else if (criteria.getFromDate() != null) {
			sql.append(" and POST_DT >= :fromDate");
			params.put("fromDate", DbUtil.toMySqlDateString(criteria.getFromDate()));
		}
		
		if (criteria.getToDate() != null) {
			sql.append(" and POST_DT < date_add(:toDate, interval 1 day)");
			params.put("toDate", DbUtil.toMySqlDateString(criteria.getToDate()));
		}
		
		if (criteria.getUnit() != null) {
			sql.append(" and UNIT = :unit");
			params.put("unit", criteria.getUnit());
		}
		
		if (!criteria.getTransactionTypes().isEmpty()) {
			sql.append(" and TRANSACTION_TYPE in (")
				.append(DbUtil.toSqlInValues(criteria.getTransactionTypes())).append(")");
		}
		
		sql.append(" order by POST_DT desc, TRANSACTION_TYPE, TRANSACTION_NO, TRANSACTION_TYPE_KEY");
		
		return getNamedParameterJdbcTemplate().query(sql.toString(), params, rowMapper);
	}
	
	private class StockCardInventoryReportItemRowMapper implements RowMapper<StockCardInventoryReportItem> {

		@Override
		public StockCardInventoryReportItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			StockCardInventoryReportItem item = new StockCardInventoryReportItem();
			item.setPostDate(rs.getTimestamp("POST_DT"));
			if (rs.getLong("TRANSACTION_NO") != 0) {
				item.setTransactionNumber(rs.getLong("TRANSACTION_NO"));
			}
			item.setSupplierOrCustomerName(rs.getString("CUSTOMER_SUPPLIER_NAME"));
			item.setTransactionType(rs.getString("TRANSACTION_TYPE"));
			item.setCurrentCostOrSellingPrice(rs.getBigDecimal("UNIT_COST_OR_PRICE"));
			item.setReferenceNumber(rs.getString("REFERENCE_NO"));
			item.setUnit(rs.getString("UNIT"));
			
			switch (rs.getString("TRANSACTION_TYPE_KEY")) {
			case "SALES INVOICE - CANCEL":
				item.setTransactionType("CANCELLED SALES INVOICE");
				
				SalesInvoiceItem salesInvoiceItem = new SalesInvoiceItem();
				salesInvoiceItem.setQuantity(rs.getInt("QUANTITY"));
				salesInvoiceItem.setUnitPrice(rs.getBigDecimal("UNIT_COST_OR_PRICE"));
				item.setAmount(salesInvoiceItem.getAmount());
				item.setAddQuantity(rs.getInt("QUANTITY"));
				break;
			case "SALES":
				salesInvoiceItem = new SalesInvoiceItem();
				salesInvoiceItem.setQuantity(rs.getInt("QUANTITY"));
				salesInvoiceItem.setUnitPrice(rs.getBigDecimal("UNIT_COST_OR_PRICE"));
				item.setAmount(salesInvoiceItem.getAmount());
				item.setLessQuantity(rs.getInt("QUANTITY"));
				break;
			case "RECEIVING RECEIPT":
				ReceivingReceiptItem receivingReceiptItem = new ReceivingReceiptItem();
				receivingReceiptItem.setQuantity(rs.getInt("QUANTITY"));
				receivingReceiptItem.setCost(rs.getBigDecimal("UNIT_COST_OR_PRICE"));
				item.setAmount(receivingReceiptItem.getAmount());
				item.setAddQuantity(rs.getInt("QUANTITY"));
				break;
			case "ADJUSTMENT OUT":
				item.setLessQuantity(rs.getInt("QUANTITY"));
				break;
			case "ADJUSTMENT IN":
				item.setAddQuantity(rs.getInt("QUANTITY"));
				break;
			case "STOCK QTY CONVERSION FROM":
				item.setLessQuantity(rs.getInt("QUANTITY"));
				break;
			case "STOCK QTY CONVERSION TO":
				item.setAddQuantity(rs.getInt("QUANTITY"));
				break;
			case "SALES RETURN":
				item.setAddQuantity(rs.getInt("QUANTITY"));
				break;
			case "SALES RETURN - CANCEL":
				item.setTransactionType("CANCELLED SALES RETURN");
				item.setLessQuantity(rs.getInt("QUANTITY"));
				break;
			case "INVENTORY CHECK":
				item.setAddQuantity(rs.getInt("QUANTITY"));
				break;
			case "INVENTORY CHECK BEFORE":
				item.setLessQuantity(rs.getInt("QUANTITY"));
				break;
			case "INVENTORY CHECK AFTER":
				item.setAddQuantity(rs.getInt("QUANTITY"));
				break;
			case "PROMO REDEMPTION":
				item.setLessQuantity(rs.getInt("QUANTITY"));
				break;
			case "PROMO REDEMPTION - CANCEL":
				item.setTransactionType("CANCELLED PROMO REDEMPTION");
				item.setAddQuantity(rs.getInt("QUANTITY"));
				break;
			case "PURCHASE RETURN":
				item.setLessQuantity(rs.getInt("QUANTITY"));
				break;
			case "INVENTORY CORRECTION":
				int quantity = rs.getInt("QUANTITY");
				if (quantity > 0) {
					item.setAddQuantity(quantity);
				} else {
					item.setLessQuantity(-quantity);
				}
				break;
			}
			
			return item;
		}
		
	}

	private static final String GET_ALL_INVENTORY_REPORT_ITEMS_SQL =
			" select CODE, DESCRIPTION, UNIT, QUANTITY, COST, PRODUCT_ID"
			+ " from ("
			+ "   select b.CODE, a.DESCRIPTION, 'CASE' as UNIT, a.AVAIL_QTY_CASE as QUANTITY, a.FINAL_COST_CASE as COST, a.ID as PRODUCT_ID"
			+ "   from PRODUCT2 a"
			+ "   join PRODUCT b"
			+ "     on b.PRODUCT2_ID = a.ID"
			+ "     and b.UOM_CODE = 'CASE'"
			+ "   where a.UNIT_IND_CASE = 'Y'"
			+ "   and a.AVAIL_QTY_CASE > 0"
			+ "   union all"
			+ "   select b.CODE, a.DESCRIPTION, 'TIES' as UNIT, a.AVAIL_QTY_TIES as QUANTITY, a.FINAL_COST_TIES as COST, a.ID as PRODUCT_ID"
			+ "   from PRODUCT2 a"
			+ "   join PRODUCT b"
			+ "     on b.PRODUCT2_ID = a.ID"
			+ "     and b.UOM_CODE = 'TIES'"
			+ "   where a.UNIT_IND_TIES = 'Y'"
			+ "   and a.AVAIL_QTY_TIES > 0"
			+ "   union all"
			+ "   select b.CODE, a.DESCRIPTION, 'PACK' as UNIT, a.AVAIL_QTY_PACK as QUANTITY, a.FINAL_COST_PACK as COST, a.ID as PRODUCT_ID"
			+ "   from PRODUCT2 a"
			+ "   join PRODUCT b"
			+ "     on b.PRODUCT2_ID = a.ID"
			+ "     and b.UOM_CODE = 'PACK'"
			+ "   where a.UNIT_IND_PACK = 'Y'"
			+ "   and a.AVAIL_QTY_PACK > 0"
			+ "   union all"
			+ "   select b.CODE, a.DESCRIPTION, 'HDZN' as UNIT, a.AVAIL_QTY_HDZN as QUANTITY, a.FINAL_COST_HDZN as COST, a.ID as PRODUCT_ID"
			+ "   from PRODUCT2 a"
			+ "   join PRODUCT b"
			+ "     on b.PRODUCT2_ID = a.ID"
			+ "     and b.UOM_CODE = 'HDZN'"
			+ "   where a.UNIT_IND_HDZN = 'Y'"
			+ "   and a.AVAIL_QTY_HDZN > 0"
			+ "   union all"
			+ "   select b.CODE, a.DESCRIPTION, 'PCS' as UNIT, a.AVAIL_QTY_PCS as QUANTITY, a.FINAL_COST_PCS as COST, a.ID as PRODUCT_ID"
			+ "   from PRODUCT2 a"
			+ "   join PRODUCT b"
			+ "     on b.PRODUCT2_ID = a.ID"
			+ "     and b.UOM_CODE = 'PCS'"
			+ "   where a.UNIT_IND_PCS = 'Y'"
			+ "   and a.AVAIL_QTY_PCS > 0"
			+ " ) a"
			+ " where 1 = 1";
	
	@Override
	public List<InventoryReportItem> getInventoryReportItems(InventoryReportCriteria criteria) {
		List<Object> params = new ArrayList<>();
		
		StringBuilder sql = new StringBuilder(GET_ALL_INVENTORY_REPORT_ITEMS_SQL);
		if (!StringUtils.isEmpty(criteria.getCodeOrDescriptionLike())) {
			sql.append(" and (a.CODE like ? or a.DESCRIPTION like ?)");
			params.add(criteria.getCodeOrDescriptionLike() + "%");
			params.add("%" + criteria.getCodeOrDescriptionLike() + "%");
		}
		if (criteria.getSupplier() != null) {
			sql.append(" and exists(select 1 from SUPPLIER_PRODUCT sp where sp.PRODUCT_ID = a.PRODUCT_ID and sp.SUPPLIER_ID = ?)");
			params.add(criteria.getSupplier().getId());
		}
		if (criteria.getManufacturer() != null) {
			sql.append(" and a.MANUFACTURER_ID = ?");
			params.add(criteria.getManufacturer().getId());
		}
		sql.append(" order by DESCRIPTION");
		
		return getJdbcTemplate().query(sql.toString(), params.toArray(), new RowMapper<InventoryReportItem>() {

			@Override
			public InventoryReportItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				InventoryReportItem item = new InventoryReportItem();
				
				Product product = new Product();
				product.setCode(rs.getString("CODE"));
				product.setDescription(rs.getString("DESCRIPTION"));
				item.setProduct(product);
				
				item.setUnit(rs.getString("UNIT"));
				item.setQuantity(rs.getInt("QUANTITY"));
				item.setUnitCost(rs.getBigDecimal("COST"));
				
				return item;
			}
			
		});
	}

	@Override
	public List<CustomerSalesSummaryReportItem> searchCustomerSalesSummaryReportItems(
			Date fromDate, Date toDate) {
		String sql = QueriesUtil.getSql("customerSalesSummaryReport");
		
		Map<String, Object> params = new HashMap<>();
		params.put("fromDate", DbUtil.toMySqlDateString(fromDate));
		params.put("toDate", DbUtil.toMySqlDateString(toDate));
		
		return getNamedParameterJdbcTemplate().query(sql,
				params,
				new RowMapper<CustomerSalesSummaryReportItem>() {

					@Override
					public CustomerSalesSummaryReportItem mapRow(ResultSet rs, int rowNum) throws SQLException {
						CustomerSalesSummaryReportItem item = new CustomerSalesSummaryReportItem();
						
						Customer customer = new Customer();
						customer.setCode(rs.getString("CUSTOMER_CODE"));
						customer.setName(rs.getString("CUSTOMER_NAME"));
						item.setCustomer(customer);
						
						item.setTotalAmount(rs.getBigDecimal("TOTAL_NET_AMOUNT"));
						item.setTotalCost(rs.getBigDecimal("TOTAL_COST"));
						item.setTotalProfit(rs.getBigDecimal("TOTAL_PROFIT"));
						
						return item;
					}
			
		});
	}

	@Override
	public List<SalesByManufacturerReportItem> searchSalesByManufacturerReportItems(
			SalesByManufacturerReportCriteria criteria) {
		String sql = QueriesUtil.getSql("salesByManufacturerReport");
		
		Map<String, Object> params = new HashMap<>();
		params.put("fromDate", DbUtil.toMySqlDateString(criteria.getFromDate()));
		params.put("toDate", DbUtil.toMySqlDateString(criteria.getToDate()));
		if (criteria.getCustomer() != null) {
			params.put("customer", criteria.getCustomer().getId());
		} else {
			params.put("customer", "");
		}
		
		return getNamedParameterJdbcTemplate().query(sql,
				params,
				new RowMapper<SalesByManufacturerReportItem>() {

					@Override
					public SalesByManufacturerReportItem mapRow(ResultSet rs, int rowNum) throws SQLException {
						SalesByManufacturerReportItem item = new SalesByManufacturerReportItem();
						
						Manufacturer manufacturer = new Manufacturer();
						manufacturer.setName(rs.getString("MANUFACTURER_NAME"));
						item.setManufacturer(manufacturer);
						
						item.setAmount(rs.getBigDecimal("TOTAL_AMOUNT"));
						return item;
					}
			
		});
	}

	@Override
	public List<StockOfftakeReportItem> searchStockOfftakeReportItems(StockOfftakeReportCriteria criteria) {
		String sql = QueriesUtil.getSql("stockOfftakeReport");
		
		Map<String, Object> params = new HashMap<>();
		params.put("manufacturer", criteria.getManufacturer().getId());
		params.put("fromDate", DbUtil.toMySqlDateString(criteria.getFromDate()));
		params.put("toDate", DbUtil.toMySqlDateString(criteria.getToDate()));
		
		return getNamedParameterJdbcTemplate().query(sql,
				params,
				new RowMapper<StockOfftakeReportItem>() {

					@Override
					public StockOfftakeReportItem mapRow(ResultSet rs, int rowNum) throws SQLException {
						StockOfftakeReportItem item = new StockOfftakeReportItem();
						item.setProduct(mapProduct(rs));
						item.setUnit(rs.getString("UNIT"));
						item.setQuantity(rs.getInt("QUANTITY"));
						return item;
					}

					private Product mapProduct(ResultSet rs) throws SQLException {
						Product product = new Product();
						product.setCode(rs.getString("CODE"));
						product.setDescription(rs.getString("DESCRIPTION"));
						return product;
					}
		});
	}

	@Override
	public List<StockCardInventoryReportItem> getStockCardInventoryReportItem(StockCardInventoryReportCriteria criteria) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("product", criteria.getProduct().getId());
		paramMap.put("inventoryCheck", criteria.getInventoryCheck().getId());
		
		StringBuilder sql = new StringBuilder(QueriesUtil.getSql("inventoryCheckStockCardInventoryReportItem"));
		if (!StringUtils.isEmpty(criteria.getUnit())) {
			sql.append(" and a.UNIT = :unit");
			paramMap.put("unit", criteria.getUnit());
		}
		
		return getNamedParameterJdbcTemplate().query(sql.toString(), paramMap, rowMapper);
	}

	@Override
	public List<PilferageReportItem> searchPilferageReportItems(PilferageReportCriteria criteria) {
		Map<String, Object> paramMap = new HashMap<>();
		
		StringBuilder sql = new StringBuilder(QueriesUtil.getSql("pilferageReportItems"));
		if (criteria.getFrom() != null) {
			sql.append(" and POST_DT >= :from");
			paramMap.put("from", DbUtil.toMySqlDateString(criteria.getFrom()));
		}
		if (criteria.getTo() != null) {
			sql.append(" and POST_DT < date_add(:to, interval 1 day)");
			paramMap.put("to", DbUtil.toMySqlDateString(criteria.getTo()));
		}
		if (criteria.getProduct() != null) {
			sql.append(" and PRODUCT_ID = :product");
			paramMap.put("product", criteria.getProduct().getId());
		}
		
		sql.append(" order by POST_DT, DESCRIPTION");
		
		return getNamedParameterJdbcTemplate().query(sql.toString(), paramMap, (rs, rowNum) -> {
			PilferageReportItem item = new PilferageReportItem();
			item.setDate(rs.getDate("POST_DT"));
			item.setTransactionType(rs.getString("TRANSACTION_TYPE"));
			if (rs.getLong("TRANSACTION_NO") != 0) {
				item.setTransactionNumber(rs.getLong("TRANSACTION_NO"));
			}
			item.setProduct(new Product());
			item.getProduct().setCode(rs.getString("CODE"));
			item.getProduct().setDescription(rs.getString("DESCRIPTION"));
			item.setUnit(rs.getString("UNIT"));
			item.setQuantity(rs.getInt("QUANTITY"));
			item.setCost(rs.getBigDecimal("COST"));
			return item;
		});
	}

    @Override
    public List<EwtReportItem> searchEwtReportItems(EwtReportCriteria criteria) {
        getJdbcTemplate().update("set @rank := 0, @paymentId := 0");
        
        String sql = QueriesUtil.getSql("ewtReportItems");
        
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("supplierId", criteria.getSupplier().getId());
        paramMap.put("receivedDateFrom", DateUtils.truncate(criteria.getFromDate(), Calendar.DATE));
        paramMap.put("receivedDateTo", DateUtils.truncate(criteria.getToDate(), Calendar.DATE));
        
        return getNamedParameterJdbcTemplate().query(sql.toString(), paramMap, (rs, rowNum) -> {
            EwtReportItem item = new EwtReportItem();
            
            ReceivingReceipt receivingReceipt = new ReceivingReceipt(rs.getLong("RECEIVING_RECEIPT_ID"));
            receivingReceipt.setReceivingReceiptNumber(rs.getLong("RECEIVING_RECEIPT_NO"));
            receivingReceipt.setReceivedDate(rs.getDate("RECEIVED_DT"));
            receivingReceipt.setReferenceNumber(rs.getString("REFERENCE_NO"));
            receivingReceipt.setVatRate(rs.getBigDecimal("VAT_RATE"));
            receivingReceipt.setVatInclusive("Y".equals(rs.getString("VAT_INCLUSIVE")));
            item.setReceivingReceipt(receivingReceipt);
            
            PurchasePayment purchasePayment = new PurchasePayment(rs.getLong("PURCHASE_PAYMENT_ID"));
            purchasePayment.setPurchasePaymentNumber(rs.getLong("PURCHASE_PAYMENT_NO"));
            item.setPurchasePayment(purchasePayment);
            
            item.setBadStockAndDiscountTotalAmount(rs.getBigDecimal("BAD_STOCK_ADJ_TOTAL"));
            item.setCancelledItemsTotalAmount(rs.getBigDecimal("CANCEL_ITEM_ADJ_TOTAL"));
            
            return item;
        });
    }
	
}