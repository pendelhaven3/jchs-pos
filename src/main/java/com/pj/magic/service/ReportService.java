package com.pj.magic.service;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.BirForm2307Report;
import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.report.CustomerSalesSummaryReport;
import com.pj.magic.model.report.EwtReport;
import com.pj.magic.model.report.InventoryReport;
import com.pj.magic.model.report.PilferageReport;
import com.pj.magic.model.report.SalesByManufacturerReport;
import com.pj.magic.model.report.StockOfftakeReport;
import com.pj.magic.model.search.EwtReportCriteria;
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

	PilferageReport getPilferageReport(PilferageReportCriteria criteria);

    EwtReport generateEwtReport(EwtReportCriteria criteria);
	
    BirForm2307Report generateBirForm2307Report(EwtReportCriteria criteria);
    
    BirForm2307Report getBirForm2307Report(Long id);

    BirForm2307Report regenerateBirForm2307Report(BirForm2307Report report, EwtReportCriteria criteria);
    
}