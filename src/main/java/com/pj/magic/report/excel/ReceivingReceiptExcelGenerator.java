package com.pj.magic.report.excel;

import org.apache.commons.lang3.StringUtils;
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

public class ReceivingReceiptExcelGenerator {

	private final SupplierService supplierService;
	
	public ReceivingReceiptExcelGenerator(SupplierService supplierService) {
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
        CellStyle rightAligned = CellStyleBuilder.createStyle(workbook)
                .setAlignment(CellStyle.ALIGN_RIGHT).build();
        
        Row row = sheet.createRow(currentRow);
        Cell cell = row.createCell(0);
        cell.setCellValue("JOSELLE CHRISTIAN GENERAL MERCHANDISE");
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 8));
        cell.setCellStyle(centered);
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("RECEIVING REPORT");
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 8));
        cell.setCellStyle(centered);
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("Supplier: ");
        row.createCell(1).setCellValue(receivingReceipt.getSupplier().getName());
        row.createCell(7).setCellValue("RR #");
        row.createCell(8).setCellValue(String.valueOf(receivingReceipt.getReceivingReceiptNumber()));
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("PO #:");
        row.createCell(1).setCellValue(String.valueOf(receivingReceipt.getRelatedPurchaseOrderNumber()));
        row.createCell(7).setCellValue("Ref #:");
        row.createCell(8).setCellValue(receivingReceipt.getReferenceNumber());
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("Terms:");
        row.createCell(1).setCellValue(receivingReceipt.getPaymentTerm().getName());
        row.createCell(7).setCellValue("Date:");
        row.createCell(8).setCellValue(FormatterUtil.formatDate(receivingReceipt.getReceivedDate()));
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("PRODUCT DETAILS");
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 4));
        cell.setCellStyle(centered);
        
        cell = row.createCell(5);
        cell.setCellValue("Unit");
        cell.setCellStyle(centered);
        
        cell = row.createCell(6);
        cell.setCellValue("Qty");
        cell.setCellStyle(centered);
        
        cell = row.createCell(7);
        cell.setCellValue("Cost");
        cell.setCellStyle(centered);
        
        cell = row.createCell(8);
        cell.setCellValue("Amount");
        cell.setCellStyle(centered);
        
        currentRow++;
        
        for (ReceivingReceiptItem item : receivingReceipt.getItems()) {
            currentRow++;
            row = sheet.createRow(currentRow);
            row.createCell(0).setCellValue(item.getCode());
            row.createCell(1).setCellValue(item.getProduct().getDescription());
            row.createCell(5).setCellValue(item.getUnit());
            row.createCell(6).setCellValue(item.getQuantity());
            
            cell = row.createCell(7);
            cell.setCellValue(item.getCost().doubleValue());
            cell.setCellStyle(amountFormat);
            
            cell = row.createCell(8);
            cell.setCellValue(item.getAmount().doubleValue());
            cell.setCellStyle(amountFormat);
        }
        
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);

        currentRow++;
        
        row = sheet.createRow(currentRow);

        cell = row.createCell(8);
        cell.setCellValue("---------------");
        cell.setCellStyle(rightAligned);
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        
        row.createCell(0).setCellValue("Total Items: " + receivingReceipt.getTotalNumberOfItems());
        row.createCell(2).setCellValue("Encoder: " + receivingReceipt.getReceivedBy().getUsername());
        
        cell = row.createCell(7);
        cell.setCellValue("Sub Total: ");
        cell.setCellStyle(rightAligned);
        
        cell = row.createCell(8);
        cell.setCellValue(receivingReceipt.getSubTotalAmount().doubleValue());
        cell.setCellStyle(amountFormat);
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        
        row.createCell(0).setCellValue("Total Qty Order: " + receivingReceipt.getTotalQuantity());

        cell = row.createCell(7);
        cell.setCellValue("Discount: ");
        cell.setCellStyle(rightAligned);
        
        cell = row.createCell(8);
        cell.setCellValue(receivingReceipt.getTotalDiscountedAmount().doubleValue());
        cell.setCellStyle(amountFormat);
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        
        cell = row.createCell(8);
        cell.setCellValue("---------------");
        cell.setCellStyle(rightAligned);
        
        currentRow++;

        row = sheet.createRow(currentRow);
        
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("REMARKS: " + StringUtils.defaultString(receivingReceipt.getRemarks()));
        
        cell = row.createCell(7);
        cell.setCellValue("Net Amount: ");
        cell.setCellStyle(rightAligned);
        
        cell = row.createCell(8);
        cell.setCellValue(receivingReceipt.getTotalNetAmount().doubleValue());
        cell.setCellStyle(amountFormat);
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        
        cell = row.createCell(7);
        cell.setCellValue("VAT Amount: ");
        cell.setCellStyle(rightAligned);
        
        cell = row.createCell(8);
        cell.setCellValue(receivingReceipt.getVatAmount().doubleValue());
        cell.setCellStyle(amountFormat);
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        
        cell = row.createCell(8);
        cell.setCellValue("---------------");
        cell.setCellStyle(rightAligned);
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        
        cell = row.createCell(7);
        cell.setCellValue("TOTAL Amount: ");
        cell.setCellStyle(rightAligned);
        
        cell = row.createCell(8);
        cell.setCellValue(receivingReceipt.getTotalAmount().doubleValue());
        cell.setCellStyle(amountFormat);
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        
        cell = row.createCell(8);
        cell.setCellValue("==========");
        cell.setCellStyle(rightAligned);
        
        return workbook;
    }
	
}
