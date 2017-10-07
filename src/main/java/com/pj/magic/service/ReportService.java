package com.pj.magic.service;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.report.CustomerSalesSummaryReport;
import com.pj.magic.model.report.InventoryReport;
import com.pj.magic.model.report.PilferageReport;
import com.pj.magic.model.report.ProductQuantityDiscrepancyReport;
import com.pj.magic.model.report.SalesByManufacturerReport;
import com.pj.magic.model.report.StockOfftakeReport;
import com.pj.magic.model.search.InventoryReportCriteria;
import com.pj.magic.model.search.PilferageReportCriteria;
import com.pj.magic.model.search.SalesByManufacturerReportCriteria;
import com.pj.magic.model.search.StockCardInventoryReportCriteria;
import com.pj.magic.model.search.StockOfftakeReportCriteria;

public interface ReportService {

	List<StockCardInventoryReportItem> getStockCardInventoryReport(
			StockCardInventoryReportCriteria criteria);

	InventoryReport getInventoryReport(InventoryReportCriteria criteria);

	CustomerSalesSummaryReport getCustomerSalesSummaryReport(Date fromDate, Date toDate);

	SalesByManufacturerReport getManufacturerSalesReport(SalesByManufacturerReportCriteria criteria);
	
	StockOfftakeReport getStockOfftakeReport(StockOfftakeReportCriteria criteria);

	List<ProductQuantityDiscrepancyReport> getProductQuantityDiscrepancyReports();

	void generateDailyProductQuantityDiscrepancyReport();

	ProductQuantityDiscrepancyReport getProductQuantityDiscrepancyReport(Date date);

	PilferageReport getPilferageReport(PilferageReportCriteria criteria);
	
}