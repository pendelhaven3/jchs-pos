package com.pj.magic.report.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.util.CellStyleBuilder;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.FormatterUtil;

public class RRCostCheckExcelGenerator {

	private final SupplierService supplierService;
	
	public RRCostCheckExcelGenerator(SupplierService supplierService) {
		this.supplierService = supplierService;
	}
	
    public Workbook generateSpreadsheet(ReceivingReceipt receivingReceipt) {
        receivingReceipt.setSupplier(supplierService.getSupplier(receivingReceipt.getSupplier().getId()));
        
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
        cell.setCellValue("RECEIVING REPORT - COST CHECK");
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 7));
        cell.setCellStyle(centered);
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("Supplier: ");
        row.createCell(1).setCellValue(receivingReceipt.getSupplier().getName());
        row.createCell(6).setCellValue("RR #");
        row.createCell(7).setCellValue(String.valueOf(receivingReceipt.getReceivingReceiptNumber()));
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("PO #:");
        row.createCell(1).setCellValue(String.valueOf(receivingReceipt.getRelatedPurchaseOrderNumber()));
        row.createCell(6).setCellValue("Ref #:");
        row.createCell(7).setCellValue(receivingReceipt.getReferenceNumber());
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("Terms:");
        row.createCell(1).setCellValue(receivingReceipt.getPaymentTerm().getName());
        row.createCell(6).setCellValue("Date:");
        row.createCell(7).setCellValue(FormatterUtil.formatDate(receivingReceipt.getReceivedDate()));
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("PRODUCT DETAILS");
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 1));
        cell.setCellStyle(centered);
        
        cell = row.createCell(2);
        cell.setCellValue("Unit");
        cell.setCellStyle(centered);
        
        cell = row.createCell(3);
        cell.setCellValue("Disc. 1");
        cell.setCellStyle(centered);
        
        cell = row.createCell(4);
        cell.setCellValue("Disc. 2");
        cell.setCellStyle(centered);
        
        cell = row.createCell(5);
        cell.setCellValue("Disc. 3");
        cell.setCellStyle(centered);
        
        cell = row.createCell(6);
        cell.setCellValue("Flat Rate");
        cell.setCellStyle(centered);
        
        cell = row.createCell(7);
        cell.setCellValue("Final Cost");
        cell.setCellStyle(centered);
        
        currentRow++;
        
        for (ReceivingReceiptItem item : receivingReceipt.getItems()) {
            currentRow++;
            row = sheet.createRow(currentRow);
            row.createCell(0).setCellValue(item.getCode());
            row.createCell(1).setCellValue(item.getProduct().getDescription());
            row.createCell(2).setCellValue(item.getUnit());
            
            cell = row.createCell(3);
            cell.setCellValue(item.getDiscount1().doubleValue());
            cell.setCellStyle(amountFormat);
            
            cell = row.createCell(4);
            cell.setCellValue(item.getDiscount2().doubleValue());
            cell.setCellStyle(amountFormat);
            
            cell = row.createCell(5);
            cell.setCellValue(item.getDiscount3().doubleValue());
            cell.setCellStyle(amountFormat);
            
            cell = row.createCell(6);
            cell.setCellValue(item.getFlatRateDiscount().doubleValue());
            cell.setCellStyle(amountFormat);
            
            cell = row.createCell(7);
            cell.setCellValue(item.getFinalCostWithVat().doubleValue());
            cell.setCellStyle(amountFormat);
        }
        
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        
        return workbook;
    }
	
}
