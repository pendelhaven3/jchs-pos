package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.pj.magic.dao.ReportDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Product;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.report.CustomerSalesSummaryReportItem;
import com.pj.magic.model.report.InventoryReportItem;
import com.pj.magic.model.report.PilferageReportItem;
import com.pj.magic.model.report.ProductQuantityDiscrepancyReport;
import com.pj.magic.model.report.ProductQuantityDiscrepancyReportItem;
import com.pj.magic.model.report.SalesByManufacturerReportItem;
import com.pj.magic.model.report.StockOfftakeReportItem;
import com.pj.magic.model.search.InventoryReportCriteria;
import com.pj.magic.model.search.PilferageReportCriteria;
import com.pj.magic.model.search.SalesByManufacturerReportCriteria;
import com.pj.magic.model.search.StockCardInventoryReportCriteria;
import com.pj.magic.model.search.StockOfftakeReportCriteria;
import com.pj.magic.util.DateUtil;
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
		params.put("product", criteria.getProduct().getId());
		
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
			case "SALES INVOICE":
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
			" select ID, CODE, DESCRIPTION, UNIT, QUANTITY, COST"
			+ " from ("
			+ "   select ID, CODE, DESCRIPTION, 'CSE' as UNIT, AVAIL_QTY_CSE as QUANTITY, FINAL_COST_CSE as COST, MANUFACTURER_ID"
			+ "   from PRODUCT"
			+ "   where UNIT_IND_CSE = 'Y'"
			+ "   and AVAIL_QTY_CSE > 0"
			+ "   union all"
			+ "   select ID, CODE, DESCRIPTION, 'TIE' as UNIT, AVAIL_QTY_TIE as QUANTITY, FINAL_COST_TIE as COST, MANUFACTURER_ID"
			+ "   from PRODUCT"
			+ "   where UNIT_IND_TIE = 'Y'"
			+ "   and AVAIL_QTY_TIE > 0"
			+ "   union all"
			+ "   select ID, CODE, DESCRIPTION, 'CTN' as UNIT, AVAIL_QTY_CTN as QUANTITY, FINAL_COST_CTN as COST, MANUFACTURER_ID"
			+ "   from PRODUCT"
			+ "   where UNIT_IND_CTN = 'Y'"
			+ "   and AVAIL_QTY_CTN > 0"
			+ "   union all"
			+ "   select ID, CODE, DESCRIPTION, 'DOZ' as UNIT, AVAIL_QTY_DOZ as QUANTITY, FINAL_COST_DOZ as COST, MANUFACTURER_ID"
			+ "   from PRODUCT"
			+ "   where UNIT_IND_DOZ = 'Y'"
			+ "   and AVAIL_QTY_DOZ > 0"
			+ "   union all"
			+ "   select ID, CODE, DESCRIPTION, 'PCS' as UNIT, AVAIL_QTY_PCS as QUANTITY, FINAL_COST_PCS as COST, MANUFACTURER_ID"
			+ "   from PRODUCT"
			+ "   where UNIT_IND_PCS = 'Y'"
			+ "   and AVAIL_QTY_PCS > 0"
			+ " ) a";
	
	@Override
	public List<InventoryReportItem> getInventoryReportItems(InventoryReportCriteria criteria) {
		List<Object> params = new ArrayList<>();
		
		StringBuilder sql = new StringBuilder(GET_ALL_INVENTORY_REPORT_ITEMS_SQL);
		if (criteria.getManufacturer() != null) {
			sql.append(" where a.MANUFACTURER_ID = ?");
			params.add(criteria.getManufacturer().getId());
		}
		sql.append(" order by CODE");
		
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

	private static final String GET_PRODUCT_QUANTITY_DISCREPANCY_REPORTS_SQL =
			"select a.DATE, case when b.DATE is null then 'N' else 'Y' end AS HAS_DISCREPANCY"
			+ " from ("
			+ "   select distinct DATE"
			+ "   from DAILY_PRODUCT_STARTING_QUANTITY"
			+ " ) a"
			+ " left join ("
			+ "	  select distinct DATE"
			+ "   from PRODUCT_QUANTITY_DISCREPANCY_REPORT"
			+ " ) b"
			+ "   on b.DATE = a.DATE"
			+ " order by date desc";
	
	@Override
	public List<ProductQuantityDiscrepancyReport> getProductQuantityDiscrepancyReports() {
		return getJdbcTemplate().query(GET_PRODUCT_QUANTITY_DISCREPANCY_REPORTS_SQL, 
				new RowMapper<ProductQuantityDiscrepancyReport>() {

					@Override
					public ProductQuantityDiscrepancyReport mapRow(ResultSet rs, int rowNum) throws SQLException {
						ProductQuantityDiscrepancyReport report = new ProductQuantityDiscrepancyReport();
						report.setDate(rs.getDate("DATE"));
						if ("Y".equals(rs.getString("HAS_DISCREPANCY"))) {
							report.getItems().add(new ProductQuantityDiscrepancyReportItem());
						}
						return report;
					}
			
		});
	}

	@Override
	public void createProductQuantityDiscrepancyReportForToday() {
		recreateDailyProductMovementView();
		doCreateProductQuantityDiscrepancyReportForToday();
		deleteProductQuantityDiscrepancyReportItemsTodayWithCorrections();
	}

	private void recreateDailyProductMovementView() {
		int previousDayModifier = DateUtil.isMonday(new Date()) ? -2 : -1;
		String sql = MessageFormat.format(QueriesUtil.getSql("recreateDailyProductMovementView"), previousDayModifier);
		getJdbcTemplate().update(sql);
	}
	
	private void doCreateProductQuantityDiscrepancyReportForToday() {
		Map<String, Object> params = new HashMap<>();
		params.put("yesterday", DateUtil.isMonday(new Date()) ? -2 : -1);
		getNamedParameterJdbcTemplate().update(QueriesUtil.getSql("productQuantityDiscrepancyReport"), params);
	}

	private static final String DELETE_PRODUCT_QUANTITY_DISCREPANCY_REPORT_ITEMS_WITH_CORRECTION_SQL =
			"delete a from PRODUCT_QUANTITY_DISCREPANCY_REPORT a"
			+ " where a.DATE = :date"
			+ " and exists ("
			+ "   select 1"
			+ "   from INVENTORY_CORRECTION b"
			+ "   where b.POST_DT >= date_add(:date, interval -1 day)"
			+ "   and b.PRODUCT_ID = a.PRODUCT_ID"
			+ " )";
	
	private void deleteProductQuantityDiscrepancyReportItemsTodayWithCorrections() {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("date", DbUtil.toMySqlDateString(new Date()));
		
		getNamedParameterJdbcTemplate().update(DELETE_PRODUCT_QUANTITY_DISCREPANCY_REPORT_ITEMS_WITH_CORRECTION_SQL, paramMap);
	}

	private static final String GET_PRODUCT_QUANTITY_DISCREPANCY_REPORT_BY_DATE_SQL =
			"select DATE, PRODUCT_ID, UNIT, PREVIOUS_QTY, QTY_MOVED, NEW_QTY"
			+ " , b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION"
			+ " from PRODUCT_QUANTITY_DISCREPANCY_REPORT a"
			+ " join PRODUCT b"
			+ "	  on b.ID = a.PRODUCT_ID"
			+ " where DATE = ?"
			+ " order by b.CODE";

	@Override
	public ProductQuantityDiscrepancyReport getProductQuantityDiscrepancyReportByDate(Date date) {
		return getJdbcTemplate().query(GET_PRODUCT_QUANTITY_DISCREPANCY_REPORT_BY_DATE_SQL,
				new Object[] {DbUtil.toMySqlDateString(date)},
				new ResultSetExtractor<ProductQuantityDiscrepancyReport>() {

					@Override
					public ProductQuantityDiscrepancyReport extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						ProductQuantityDiscrepancyReport report = new ProductQuantityDiscrepancyReport();
						report.setDate(date);
						while (rs.next()) {
							ProductQuantityDiscrepancyReportItem item = new ProductQuantityDiscrepancyReportItem();
							item.setProduct(mapProduct(rs));
							item.setUnit(rs.getString("UNIT"));
							item.setPreviousQuantity(rs.getInt("PREVIOUS_QTY"));
							item.setQuantityMoved(rs.getInt("QTY_MOVED"));
							item.setNewQuantity(rs.getInt("NEW_QTY"));
							report.getItems().add(item);
						}
						return report;
					}

					private Product mapProduct(ResultSet rs) throws SQLException {
						Product product = new Product(rs.getLong("PRODUCT_ID"));
						product.setCode(rs.getString("PRODUCT_CODE"));
						product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
						return product;
					}
			
		});
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

}