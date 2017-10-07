package com.pj.magic.dao;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.report.CustomerSalesSummaryReportItem;
import com.pj.magic.model.report.InventoryReportItem;
import com.pj.magic.model.report.PilferageReportItem;
import com.pj.magic.model.report.ProductQuantityDiscrepancyReport;
import com.pj.magic.model.report.SalesByManufacturerReportItem;
import com.pj.magic.model.report.StockOfftakeReportItem;
import com.pj.magic.model.search.InventoryReportCriteria;
import com.pj.magic.model.search.PilferageReportCriteria;
import com.pj.magic.model.search.SalesByManufacturerReportCriteria;
import com.pj.magic.model.search.StockCardInventoryReportCriteria;
import com.pj.magic.model.search.StockOfftakeReportCriteria;

public interface ReportDao {

	List<StockCardInventoryReportItem> getStockCardInventoryReport(StockCardInventoryReportCriteria criteria);

	List<InventoryReportItem> getInventoryReportItems(InventoryReportCriteria criteria);

	List<CustomerSalesSummaryReportItem> searchCustomerSalesSummaryReportItems(Date fromDate, Date toDate);

	List<SalesByManufacturerReportItem> searchSalesByManufacturerReportItems(
			SalesByManufacturerReportCriteria criteria);

	List<StockOfftakeReportItem> searchStockOfftakeReportItems(StockOfftakeReportCriteria criteria);

	List<StockCardInventoryReportItem> getStockCardInventoryReportItem(StockCardInventoryReportCriteria criteria);

	List<ProductQuantityDiscrepancyReport> getProductQuantityDiscrepancyReports();

	void createProductQuantityDiscrepancyReportForToday();

	ProductQuantityDiscrepancyReport getProductQuantityDiscrepancyReportByDate(Date date);

	List<PilferageReportItem> searchPilferageReportItems(PilferageReportCriteria criteria);
	
}