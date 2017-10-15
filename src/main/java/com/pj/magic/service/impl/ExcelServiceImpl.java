package com.pj.magic.service.impl;

import java.io.IOException;
import java.math.RoundingMode;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.SupplierDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchaseOrderItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.Unit;
import com.pj.magic.model.report.CustomerSalesSummaryReport;
import com.pj.magic.model.report.CustomerSalesSummaryReportItem;
import com.pj.magic.model.report.StockOfftakeReport;
import com.pj.magic.model.report.StockOfftakeReportItem;
import com.pj.magic.model.util.CellStyleBuilder;
import com.pj.magic.service.ExcelService;
import com.pj.magic.util.FormatterUtil;

@Service
public class ExcelServiceImpl implements ExcelService {

	@Autowired private SupplierDao supplierDao;
	@Autowired private SystemDao systemDao;
	
	@Override
	public Workbook generateSpreadsheet(SalesInvoice salesInvoice) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		int currentRow = 0;
		CellStyle centered = CellStyleBuilder.createStyle(workbook)
				.setAlignment(CellStyle.ALIGN_CENTER).build();
		CellStyle amountFormat = CellStyleBuilder.createStyle(workbook)
				.setAmountFormat(true).build();
		
		Row row = sheet.createRow(currentRow);
		Cell cell = row.createCell(0);
		cell.setCellValue("JC HARMONY SELLING INC");
		sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 8));
		cell.setCellStyle(centered);
		
		currentRow++;
		
		row = sheet.createRow(currentRow);
		cell = row.createCell(0);
		cell.setCellValue("SALES INVOICE");
		sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 8));
		cell.setCellStyle(centered);
		
		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("CUSTOMER:  " + salesInvoice.getCustomer().getName());
		row.createCell(7).setCellValue("SI#");
		row.createCell(8).setCellValue(salesInvoice.getSalesInvoiceNumber());

		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("ADDRESS:  " + salesInvoice.getCustomer().getBusinessAddress());
		row.createCell(7).setCellValue("DATE:");
		
		cell = row.createCell(8);
		cell.setCellValue(FormatterUtil.formatDate(salesInvoice.getTransactionDate()));
		cell.setCellStyle(CellStyleBuilder.createStyle(workbook)
				.setAlignment(CellStyle.ALIGN_RIGHT).build());
		
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("MODE:  " + salesInvoice.getMode());
		row.createCell(7).setCellValue("PS:");
		row.createCell(8).setCellValue(salesInvoice.getPricingScheme().getId());
		
		currentRow++;
		currentRow++;
		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		cell = row.createCell(0);
		cell.setCellValue("PRODUCT DETAILS");
		sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 4));
		cell.setCellStyle(centered);
		
		cell = row.createCell(5);
		cell.setCellValue("UNIT");
		cell.setCellStyle(centered);
		
		cell = row.createCell(6);
		cell.setCellValue("QTY");
		cell.setCellStyle(centered);
		
		cell = row.createCell(7);
		cell.setCellValue("PRICE");
		cell.setCellStyle(centered);
		
		cell = row.createCell(8);
		cell.setCellValue("AMT");
		cell.setCellStyle(centered);
		
		currentRow++;
		
		for (SalesInvoiceItem item : salesInvoice.getItems()) {
			currentRow++;
			row = sheet.createRow(currentRow);
			row.createCell(0).setCellValue(item.getProduct().getDescription());
			row.createCell(5).setCellValue(item.getUnit());
			row.createCell(6).setCellValue(item.getQuantity());
			cell = row.createCell(7);
			cell.setCellValue(item.getUnitPrice().doubleValue());
			cell.setCellStyle(amountFormat);
			cell = row.createCell(8);
			cell.setCellValue(item.getAmount().doubleValue());
			cell.setCellStyle(amountFormat);
		}
		cell.setCellStyle(CellStyleBuilder.createStyle(workbook)
				.setBorderBottom(CellStyle.BORDER_THIN).setAmountFormat(true).build());
		
		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);	
		row.createCell(0).setCellValue("TOTAL ITEMS:  " + salesInvoice.getItems().size());
		row.createCell(2).setCellValue("TOTAL QTY:  " + salesInvoice.getTotalQuantity());
		cell = row.createCell(8);
		cell.setCellValue(salesInvoice.getTotalAmount().doubleValue());
		cell.setCellStyle(amountFormat);
		
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(7).setCellValue("DISC");
		cell = row.createCell(8);
		cell.setCellValue(salesInvoice.getTotalDiscounts().doubleValue());
		cell.setCellStyle(CellStyleBuilder.createStyle(workbook)
				.setBorderBottom(CellStyle.BORDER_THIN).setAmountFormat(true).build());
		
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(7).setCellValue("NET AMT");
		cell = row.createCell(8);
		cell.setCellValue(salesInvoice.getTotalNetAmount().doubleValue());
		cell.setCellStyle(CellStyleBuilder.createStyle(workbook)
				.setBorderBottom(CellStyle.BORDER_DOUBLE).setAmountFormat(true).build());
		
		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("ENCODER:  " + salesInvoice.getEncoder());

		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("REMARKS:  " + salesInvoice.getRemarks());
		
		sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8);
		
		return workbook;
	}

	@Override
	public Workbook generateSpreadsheet(PurchaseOrder purchaseOrder) {
		purchaseOrder.setSupplier(supplierDao.get(purchaseOrder.getSupplier().getId()));
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		int currentRow = 0;
		CellStyle centered = CellStyleBuilder.createStyle(workbook)
				.setAlignment(CellStyle.ALIGN_CENTER).build();
		CellStyle amountFormat = CellStyleBuilder.createStyle(workbook)
				.setAmountFormat(true).build();
		
		Row row = sheet.createRow(currentRow);
		Cell cell = row.createCell(0);
		cell.setCellValue("JC HARMONY SELLING INC");
		sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 8));
		cell.setCellStyle(centered);
		
		currentRow++;
		
		row = sheet.createRow(currentRow);
		cell = row.createCell(0);
		cell.setCellValue("PURCHASE ORDER");
		sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 8));
		cell.setCellStyle(centered);
		
		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("SUPPLIER: " + purchaseOrder.getSupplier().getName());
		row.createCell(7).setCellValue("PO#");
		row.createCell(8).setCellValue(purchaseOrder.getPurchaseOrderNumber());
		
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("ADDRESS: " + purchaseOrder.getSupplier().getAddress());
		row.createCell(7).setCellValue("DATE:");
		cell = row.createCell(8);
		cell.setCellValue(FormatterUtil.formatDate(systemDao.getCurrentDateTime()));
		cell.setCellStyle(CellStyleBuilder.createStyle(workbook)
				.setAlignment(CellStyle.ALIGN_RIGHT).build());

		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("FAX: " + purchaseOrder.getSupplier().getFaxNumber());
		
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("CONTACT: " + purchaseOrder.getSupplier().getContactNumber());

		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		cell = row.createCell(0);
		cell.setCellValue("PRODUCT DETAILS");
		sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 4));
		cell.setCellStyle(centered);
		
		cell = row.createCell(5);
		cell.setCellValue("UNIT");
		cell.setCellStyle(centered);
		
		cell = row.createCell(6);
		cell.setCellValue("QTY");
		cell.setCellStyle(centered);
		
		cell = row.createCell(7);
		cell.setCellValue("PRICE/U");
		cell.setCellStyle(centered);
		
		cell = row.createCell(8);
		cell.setCellValue("AMT");
		cell.setCellStyle(centered);
		
		currentRow++;
		
		for (PurchaseOrderItem item : purchaseOrder.getItems()) {
			currentRow++;
			row = sheet.createRow(currentRow);
			row.createCell(0).setCellValue(item.getProduct().getDescription());
			row.createCell(5).setCellValue(item.getUnit());
			row.createCell(6).setCellValue(item.getQuantity());
			cell = row.createCell(7);
			cell.setCellValue(item.getCost().doubleValue());
			cell.setCellStyle(amountFormat);
			cell = row.createCell(8);
			cell.setCellValue(item.getAmount().doubleValue());
			cell.setCellStyle(amountFormat);
		}
		
		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		cell = row.createCell(8);
		cell.setCellValue(purchaseOrder.getTotalAmount().doubleValue());
		cell.setCellStyle(amountFormat);

		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("TOTAL ITEMS: " + purchaseOrder.getTotalNumberOfItems());
		row.createCell(2).setCellValue("TOTAL QTY: " + purchaseOrder.getTotalQuantity());

		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
//		row.createCell(0).setCellValue("PREPARED BY: " + purchaseOrder.getCreatedBy().getUsername());
        row.createCell(0).setCellValue("PREPARED BY: ");
		
		currentRow++;
		currentRow++;

		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("REMARKS: " + purchaseOrder.getRemarks());
		
		sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8);
		
		return workbook;
	}

	@Override
	public Workbook generateSpreadsheet(PricingScheme pricingScheme) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(
					getClass().getResourceAsStream("/excel/sellingPriceAndCost.xlsx"));
		Sheet sheet = workbook.getSheetAt(0);
		
		CellStyle amountFormat = CellStyleBuilder.createStyle(workbook).setAmountFormat(true).build();
		CellStyle amountFormatWithRightBorder = 
				CellStyleBuilder.createStyle(workbook).setAmountFormat(true)
				.setBorderRight(CellStyle.BORDER_THIN).build();
		CellStyle rightBorder = CellStyleBuilder.createStyle(workbook)
				.setBorderRight(CellStyle.BORDER_THIN).build();
		
		int currentRow = 2;
		for (Product product : pricingScheme.getProducts()) {
			Row row = sheet.createRow(currentRow);
			
			row.createCell(0).setCellValue(product.getCode());
			
			Cell cell = row.createCell(1);
			cell.setCellValue(product.getDescription());
			cell.setCellStyle(rightBorder);
			
			if (product.hasActiveUnit(Unit.CASE)) {
				cell = row.createCell(2);
				cell.setCellValue(product.getUnitPrice(Unit.CASE).doubleValue());
				cell.setCellStyle(amountFormat);
				
				cell = row.createCell(7);
				cell.setCellValue(product.getFinalCost(Unit.CASE).doubleValue());
				cell.setCellStyle(amountFormat);
				
				cell = row.createCell(12);
				cell.setCellValue(product.getFlatProfit(Unit.CASE).doubleValue());
				cell.setCellStyle(amountFormat);
				
				cell = row.createCell(17);
				cell.setCellValue(product.getPercentProfit(Unit.CASE).doubleValue());
				cell.setCellStyle(amountFormat);
			}
			
			if (product.hasActiveUnit(Unit.TIE)) {
				cell = row.createCell(3);
				cell.setCellValue(product.getUnitPrice(Unit.TIE).doubleValue());
				cell.setCellStyle(amountFormat);
				
				cell = row.createCell(8);
				cell.setCellValue(product.getFinalCost(Unit.TIE).doubleValue());
				cell.setCellStyle(amountFormat);
				
				cell = row.createCell(13);
				cell.setCellValue(product.getFlatProfit(Unit.TIE).doubleValue());
				cell.setCellStyle(amountFormat);
				
				cell = row.createCell(18);
				cell.setCellValue(product.getPercentProfit(Unit.TIE).doubleValue());
				cell.setCellStyle(amountFormat);
			}
			
			if (product.hasActiveUnit(Unit.CARTON)) {
				cell = row.createCell(4);
				cell.setCellValue(product.getUnitPrice(Unit.CARTON).doubleValue());
				cell.setCellStyle(amountFormat);
				
				cell = row.createCell(9);
				cell.setCellValue(product.getFinalCost(Unit.CARTON).doubleValue());
				cell.setCellStyle(amountFormat);
				
				cell = row.createCell(14);
				cell.setCellValue(product.getFlatProfit(Unit.CARTON).doubleValue());
				cell.setCellStyle(amountFormat);
				
				cell = row.createCell(19);
				cell.setCellValue(product.getPercentProfit(Unit.CARTON).doubleValue());
				cell.setCellStyle(amountFormat);
			}
			
			if (product.hasActiveUnit(Unit.DOZEN)) {
				cell = row.createCell(5);
				cell.setCellValue(product.getUnitPrice(Unit.DOZEN).doubleValue());
				cell.setCellStyle(amountFormat);
				
				cell = row.createCell(10);
				cell.setCellValue(product.getFinalCost(Unit.DOZEN).doubleValue());
				cell.setCellStyle(amountFormat);
				
				cell = row.createCell(15);
				cell.setCellValue(product.getFlatProfit(Unit.DOZEN).doubleValue());
				cell.setCellStyle(amountFormat);
				
				cell = row.createCell(20);
				cell.setCellValue(product.getPercentProfit(Unit.DOZEN).doubleValue());
				cell.setCellStyle(amountFormat);
			}
		
			if (product.hasActiveUnit(Unit.PIECES)) {
				cell = row.createCell(6);
				cell.setCellValue(product.getUnitPrice(Unit.PIECES).doubleValue());
				cell.setCellStyle(amountFormat);
				
				cell = row.createCell(11);
				cell.setCellValue(product.getFinalCost(Unit.PIECES).doubleValue());
				cell.setCellStyle(amountFormat);
				
				cell = row.createCell(16);
				cell.setCellValue(product.getFlatProfit(Unit.PIECES).doubleValue());
				cell.setCellStyle(amountFormat);
				
				cell = row.createCell(21);
				cell.setCellValue(product.getPercentProfit(Unit.PIECES).doubleValue());
				cell.setCellStyle(amountFormat);
			}
			
			cell = row.getCell(6);
			if (cell == null) {
				cell = row.createCell(6);
			}
			cell.setCellStyle(amountFormatWithRightBorder);
			
			cell = row.getCell(11);
			if (cell == null) {
				cell = row.createCell(11);
			}
			cell.setCellStyle(amountFormatWithRightBorder);
			
			cell = row.getCell(16);
			if (cell == null) {
				cell = row.createCell(16);
			}
			cell.setCellStyle(amountFormatWithRightBorder);
			
			cell = row.getCell(21);
			if (cell == null) {
				cell = row.createCell(21);
			}
			cell.setCellStyle(amountFormatWithRightBorder);
			
			currentRow++;
		}
		
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		
		return workbook;
	}

	@Override
	public Workbook generateSpreadsheet(CustomerSalesSummaryReport report) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(
				getClass().getResourceAsStream("/excel/customerSalesSummaryReport.xlsx"));
		Sheet sheet = workbook.getSheetAt(0);

		CellStyle amountFormat = CellStyleBuilder.createStyle(workbook).setAmountFormat(true).build();
		
		int currentRow = 1;
		for (CustomerSalesSummaryReportItem item : report.getItems()) {
			Row row = sheet.createRow(currentRow);
			row.createCell(0).setCellValue(item.getCustomer().getCode());
			row.createCell(1).setCellValue(item.getCustomer().getName());
			
			Cell cell = row.createCell(2);
			cell.setCellValue(item.getTotalAmount().doubleValue());
			cell.setCellStyle(amountFormat);
			
			cell = row.createCell(3);
			cell.setCellValue(item.getTotalCost().doubleValue());
			cell.setCellStyle(amountFormat);
			
			cell = row.createCell(4);
			cell.setCellValue(item.getTotalProfit().doubleValue());
			cell.setCellStyle(amountFormat);
			
			currentRow++;
		}

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		
		return workbook;
	}

	@Override
	public Workbook generateSpreadsheet(StockOfftakeReport report) throws IOException {
		Workbook workbook = new XSSFWorkbook(
				getClass().getResourceAsStream("/excel/stockOfftakeReport.xlsx"));
		Sheet sheet = workbook.getSheetAt(0);

		int currentRow = 1;
		for (StockOfftakeReportItem item : report.getItems()) {
			Row row = sheet.createRow(currentRow);
			row.createCell(0).setCellValue(item.getProduct().getCode());
			row.createCell(1).setCellValue(item.getProduct().getDescription());
			row.createCell(2).setCellValue(item.getUnit());
			row.createCell(3).setCellValue(item.getQuantity());
			
			currentRow++;
		}

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		
		return workbook;
	}

    @Override
    public Workbook generateSpreadsheet(ReceivingReceipt receivingReceipt) {
        receivingReceipt.setSupplier(supplierDao.get(receivingReceipt.getSupplier().getId()));
        
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        int currentRow = 0;
        CellStyle centered = CellStyleBuilder.createStyle(workbook)
                .setAlignment(CellStyle.ALIGN_CENTER).build();
        CellStyle amountFormat = CellStyleBuilder.createStyle(workbook)
                .setAmountFormat(true).build();
        
        Row row = sheet.createRow(currentRow);
        Cell cell = row.createCell(0);
        cell.setCellValue("JOSELLE CHRISTIAN GENERAL MERCHANDISE");
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 8));
        cell.setCellStyle(centered);
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("RECEIVING RECEIPT");
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 8));
        cell.setCellStyle(centered);
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("SUPPLIER: " + receivingReceipt.getSupplier().getName());
        row.createCell(7).setCellValue("RR#");
        row.createCell(8).setCellValue(receivingReceipt.getReceivingReceiptNumber());
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("ADDRESS: " + receivingReceipt.getSupplier().getAddress());
        row.createCell(7).setCellValue("DATE:");
        cell = row.createCell(8);
        cell.setCellValue(FormatterUtil.formatDate(systemDao.getCurrentDateTime()));
        cell.setCellStyle(CellStyleBuilder.createStyle(workbook)
                .setAlignment(CellStyle.ALIGN_RIGHT).build());

        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("FAX: " + receivingReceipt.getSupplier().getFaxNumber());
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("CONTACT: " + receivingReceipt.getSupplier().getContactNumber());

        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("PRODUCT DETAILS");
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 4));
        cell.setCellStyle(centered);
        
        cell = row.createCell(5);
        cell.setCellValue("UNIT");
        cell.setCellStyle(centered);
        
        cell = row.createCell(6);
        cell.setCellValue("QTY");
        cell.setCellStyle(centered);
        
        cell = row.createCell(7);
        cell.setCellValue("NET COST");
        cell.setCellStyle(centered);
        
        cell = row.createCell(8);
        cell.setCellValue("WITH VAT");
        cell.setCellStyle(centered);
        
        currentRow++;
        
        for (ReceivingReceiptItem item : receivingReceipt.getItems()) {
            currentRow++;
            row = sheet.createRow(currentRow);
            row.createCell(0).setCellValue(item.getProduct().getCode());
            row.createCell(1).setCellValue(item.getProduct().getDescription());
            row.createCell(5).setCellValue(item.getUnit());
            row.createCell(6).setCellValue(item.getQuantity());
            cell = row.createCell(7);
            cell.setCellValue(item.getFinalCost().doubleValue());
            cell.setCellStyle(amountFormat);
            cell = row.createCell(8);
            if (receivingReceipt.isVatInclusive()) {
                cell.setCellValue(item.getFinalCost().doubleValue());
            } else {
                cell.setCellValue(item.getFinalCostWithVat().setScale(2, RoundingMode.HALF_UP).doubleValue());
            }
            cell.setCellStyle(amountFormat);
        }
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("TOTAL ITEMS: " + receivingReceipt.getTotalNumberOfItems());
        row.createCell(2).setCellValue("TOTAL QTY: " + receivingReceipt.getTotalQuantity());

        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
//      row.createCell(0).setCellValue("PREPARED BY: " + purchaseOrder.getCreatedBy().getUsername());
        row.createCell(0).setCellValue("PREPARED BY: ");
        
        currentRow++;
        currentRow++;

        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("REMARKS: " + receivingReceipt.getRemarks());
        
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        
        return workbook;
    }
		
}