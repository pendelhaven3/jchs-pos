package com.pj.magic.report.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.PurchasePaymentEcashPayment;
import com.pj.magic.model.report.EcashPurchasePaymentsReport;
import com.pj.magic.model.util.CellStyleBuilder;
import com.pj.magic.util.FormatterUtil;

public class EcashPurchasePaymentsReportExcelGenerator {

    public Workbook generate(EcashPurchasePaymentsReport report) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        CellStyle centered = CellStyleBuilder.createStyle(workbook)
                .setAlignment(CellStyle.ALIGN_CENTER).build();
        CellStyle rightAligned = CellStyleBuilder.createStyle(workbook)
                .setAlignment(CellStyle.ALIGN_RIGHT).build();
        CellStyle amountFormat = CellStyleBuilder.createStyle(workbook)
                .setAmountFormat(true).build();
        CellStyle dateStyle = CellStyleBuilder.createStyle(workbook)
        		.setDateFormat(true).build();
        
        int currentRow = 0;
        
        Row row = sheet.createRow(currentRow);
        Cell cell = row.createCell(0);
        cell.setCellValue("E-CASH PURCHASE PAYMENTS REPORT");
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 5));
        cell.setCellStyle(centered);
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("REFERENCE NO.");
        
        cell = row.createCell(1);
        cell.setCellValue("AMOUNT");
        cell.setCellStyle(rightAligned);
        
        cell = row.createCell(2);
        cell.setCellValue("E-CASH RECEIVER");
        
        cell = row.createCell(3);
        cell.setCellValue("RECEIVED DATE");
        
        cell = row.createCell(4);
        cell.setCellValue("RECEIVED BY");
        
        cell = row.createCell(5);
        cell.setCellValue("PAYMENT NO.");
        
        currentRow++;
        
        for (PurchasePaymentEcashPayment payment : report.getPayments()) {
            currentRow++;
            row = sheet.createRow(currentRow);
            row.createCell(0).setCellValue(payment.getReferenceNumber());
            
            cell = row.createCell(1);
            cell.setCellValue(payment.getAmount().doubleValue());
            cell.setCellStyle(amountFormat);
            
            row.createCell(2).setCellValue(payment.getEcashReceiver().getName());
            
            cell = row.createCell(3);
            cell.setCellValue(FormatterUtil.formatDate(payment.getPaidDate()));
            cell.setCellStyle(dateStyle);
            
            row.createCell(4).setCellValue(payment.getPaidBy().getUsername());
            row.createCell(5).setCellValue(String.valueOf(payment.getParent().getPurchasePaymentNumber()));
        }
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        
        cell = row.createCell(4);
        cell.setCellValue("TOTAL AMOUNT:");
        cell.setCellStyle(rightAligned);
        
        cell = row.createCell(5);
        cell.setCellValue(report.getTotalAmount().doubleValue());
        cell.setCellStyle(amountFormat);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        
        return workbook;
    }

}
