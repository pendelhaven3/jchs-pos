package com.pj.magic.service;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;

import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.report.CustomerSalesSummaryReport;
import com.pj.magic.model.report.StockOfftakeReport;

public interface ExcelService {

	Workbook generateSpreadsheet(SalesInvoice salesInvoice);

	Workbook generateSpreadsheet(PurchaseOrder purchaseOrder);

	Workbook generateSpreadsheet(PricingScheme pricingScheme) throws IOException;

	Workbook generateSpreadsheet(CustomerSalesSummaryReport report) throws IOException;

	Workbook generateSpreadsheet(StockOfftakeReport report) throws IOException;
	
}