package com.pj.magic.report.excel;

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.pj.magic.model.PurchaseReturnBadStock;
import com.pj.magic.model.PurchaseReturnBadStockItem;
import com.pj.magic.model.util.CellStyleBuilder;
import com.pj.magic.util.FormatterUtil;

@Component
public class PurchaseReturnBadStockExcelGenerator {

    public Workbook generate(PurchaseReturnBadStock purchaseReturnBadStock) {
//        receivingReceipt.setSupplier(supplierDao.get(receivingReceipt.getSupplier().getId()));
        
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
        cell.setCellValue("PURCHASE RETURN - BAD STOCK");
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 8));
        cell.setCellStyle(centered);
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("PRBS No.:");
        row.createCell(1).setCellValue(purchaseReturnBadStock.getPurchaseReturnBadStockNumber());
        row.createCell(4).setCellValue("Date:");
        row.createCell(5).setCellValue(FormatterUtil.formatDate(new Date()));
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("Supplier:");
        row.createCell(1).setCellValue(purchaseReturnBadStock.getSupplier().getName());
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("Product Code");
        cell.setCellStyle(centered);
        
        cell = row.createCell(1);
        cell.setCellValue("Description");
        cell.setCellStyle(centered);
        
        cell = row.createCell(5);
        cell.setCellValue("UNIT");
        cell.setCellStyle(centered);
        
        cell = row.createCell(6);
        cell.setCellValue("QTY");
        cell.setCellStyle(centered);
        
        cell = row.createCell(7);
        cell.setCellValue("COST");
        cell.setCellStyle(centered);
        
        cell = row.createCell(8);
        cell.setCellValue("AMOUNT");
        cell.setCellStyle(centered);
        
        currentRow++;
        
        for (PurchaseReturnBadStockItem item : purchaseReturnBadStock.getItems()) {
            currentRow++;
            row = sheet.createRow(currentRow);
            row.createCell(0).setCellValue(item.getCode());
            row.createCell(1).setCellValue(item.getProduct().getDescription());
            row.createCell(5).setCellValue(item.getUnit());
            row.createCell(6).setCellValue(item.getQuantity());
            
            cell = row.createCell(7);
            cell.setCellValue(item.getUnitCost().doubleValue());
            cell.setCellStyle(amountFormat);
            
            cell = row.createCell(8);
            cell.setCellValue(item.getAmount().doubleValue());
            cell.setCellStyle(amountFormat);
        }
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("TOTAL ITEMS: " + purchaseReturnBadStock.getTotalItems());
        row.createCell(7).setCellValue("TOTAL: ");
        
        cell = row.createCell(8);
        cell.setCellValue(purchaseReturnBadStock.getTotalAmount().doubleValue());
        cell.setCellStyle(amountFormat);

        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("REMARKS: " + purchaseReturnBadStock.getRemarks());
        
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        
        return workbook;
    }
    
}
