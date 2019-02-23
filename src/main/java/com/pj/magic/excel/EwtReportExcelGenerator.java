package com.pj.magic.excel;

import java.text.MessageFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.report.EwtReport;
import com.pj.magic.model.report.EwtReportItem;
import com.pj.magic.model.util.CellStyleBuilder;
import com.pj.magic.util.FormatterUtil;

public class EwtReportExcelGenerator {

    public Workbook generate(EwtReport report) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        
        CellStyle centerUnderline = CellStyleBuilder.createStyle(workbook).setAlignment(CellStyle.ALIGN_CENTER).setUnderline(true).build();
        CellStyle amountStyle = CellStyleBuilder.createStyle(workbook).setAlignment(CellStyle.ALIGN_RIGHT).setAmountFormat(true).build();
        CellStyle rightStyle = CellStyleBuilder.createStyle(workbook).setAlignment(CellStyle.ALIGN_RIGHT).build();
        CellStyle dateRightStyle = CellStyleBuilder.createStyle(workbook).setAlignment(CellStyle.ALIGN_RIGHT).setDateFormat(true).build();
        CellStyle totalStyle = CellStyleBuilder.createStyle(workbook)
                .setAmountFormat(true).setBorderTop(CellStyle.BORDER_THIN).setBorderBottom(CellStyle.BORDER_DOUBLE).setBold(true).build();
        CellStyle boldStyle = CellStyleBuilder.createStyle(workbook).setBold(true).build();
        
        Sheet sheet = workbook.createSheet();
        int currentRow = 0;

        Row row = sheet.createRow(currentRow);
        Cell cell = row.createCell(0);
        cell.setCellValue("BIR EWT REPORT SCHEDULE");
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("JC HARMONY SELLING INC");
        
        cell = row.createCell(7);
        cell.setCellValue("DATE: " + FormatterUtil.formatDate(new Date()));
        
        currentRow = currentRow + 2;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("COMPANY:");
        
        cell = row.createCell(2);
        cell.setCellValue(report.getSupplier().getName());
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("TIN#");
        
        cell = row.createCell(2);
        cell.setCellValue(report.getSupplier().getTin());
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        
        cell = row.createCell(0);
        cell.setCellValue("INVOICE #");
        cell.setCellStyle(centerUnderline);
        
        cell = row.createCell(1);
        cell.setCellValue("RR#");
        cell.setCellStyle(centerUnderline);
        
        cell = row.createCell(2);
        cell.setCellValue("DATE");
        cell.setCellStyle(centerUnderline);
        
        cell = row.createCell(3);
        cell.setCellValue("INV AMT");
        cell.setCellStyle(centerUnderline);
        
        cell = row.createCell(4);
        cell.setCellValue("GROSS AMT");
        cell.setCellStyle(centerUnderline);
        
        cell = row.createCell(5);
        cell.setCellValue("VAT");
        cell.setCellStyle(centerUnderline);
        
        cell = row.createCell(6);
        cell.setCellValue("NET OF VAT");
        cell.setCellStyle(centerUnderline);
        
        cell = row.createCell(7);
        cell.setCellValue("TAX WITHHELD");
        cell.setCellStyle(centerUnderline);
        
        cell = row.createCell(8);
        cell.setCellValue("NET OF EWT");
        cell.setCellStyle(centerUnderline);
        
        for (int i = 0; i < report.getItems().size(); i++) {
            EwtReportItem item = report.getItems().get(i);
            
            currentRow++;
            row = sheet.createRow(currentRow);
            
            cell = row.createCell(0);
            cell.setCellValue(item.getReceivingReceipt().getReferenceNumber());
            cell.setCellStyle(rightStyle);
            
            cell = row.createCell(1);
            cell.setCellValue(item.getReceivingReceipt().getReceivingReceiptNumber());
            
            cell = row.createCell(2);
            cell.setCellValue(FormatterUtil.formatDate(item.getReceivingReceipt().getReceivedDate()));
            cell.setCellStyle(dateRightStyle);
            
            cell = row.createCell(3);
            cell.setCellValue(item.getInvoiceAmount().doubleValue());
            cell.setCellStyle(amountStyle);
            
            cell = row.createCell(4);
            cell.setCellValue(item.getGrossAmount().doubleValue());
            cell.setCellStyle(amountStyle);
            
            cell = row.createCell(5, Cell.CELL_TYPE_FORMULA);
            cell.setCellFormula(MessageFormat.format("E{0}-G{0}", String.valueOf(8 + i)));
            cell.setCellStyle(amountStyle);
            
            cell = row.createCell(6, Cell.CELL_TYPE_FORMULA);
            cell.setCellFormula(MessageFormat.format("E{0}/1.12", String.valueOf(8 + i)));
            cell.setCellStyle(amountStyle);
            
            cell = row.createCell(7, Cell.CELL_TYPE_FORMULA);
            cell.setCellFormula(MessageFormat.format("G{0}*0.01", String.valueOf(8 + i)));
            cell.setCellStyle(amountStyle);
            
            cell = row.createCell(8, Cell.CELL_TYPE_FORMULA);
            cell.setCellFormula(MessageFormat.format("E{0}-H{0}", String.valueOf(8 + i)));
            cell.setCellStyle(amountStyle);
        }
        
        currentRow = currentRow + 5;
        
        row = sheet.createRow(currentRow);
        
        cell = row.createCell(2);
        cell.setCellValue("TOTAL");
        cell.setCellStyle(boldStyle);
        
        cell = row.createCell(3);
        cell.setCellStyle(totalStyle);
        
        cell = row.createCell(4, Cell.CELL_TYPE_FORMULA);
        cell.setCellFormula(MessageFormat.format("SUM(E8:E{0})", String.valueOf(7 + report.getItems().size())));
        cell.setCellStyle(totalStyle);
        
        cell = row.createCell(5, Cell.CELL_TYPE_FORMULA);
        cell.setCellFormula(MessageFormat.format("SUM(F8:F{0})", String.valueOf(7 + report.getItems().size())));
        cell.setCellStyle(totalStyle);
        
        cell = row.createCell(6, Cell.CELL_TYPE_FORMULA);
        cell.setCellFormula(MessageFormat.format("SUM(G8:G{0})", String.valueOf(7 + report.getItems().size())));
        cell.setCellStyle(totalStyle);
        
        cell = row.createCell(7, Cell.CELL_TYPE_FORMULA);
        cell.setCellFormula(MessageFormat.format("SUM(H8:H{0})", String.valueOf(7 + report.getItems().size())));
        cell.setCellStyle(totalStyle);
        
        cell = row.createCell(8, Cell.CELL_TYPE_FORMULA);
        cell.setCellFormula(MessageFormat.format("SUM(I8:I{0})", String.valueOf(7 + report.getItems().size())));
        cell.setCellStyle(totalStyle);
        
        sheet.setColumnWidth(0, 5387);
        sheet.setColumnWidth(1, 2196);
        sheet.setColumnWidth(2, 3190);
        sheet.setColumnWidth(3, 3521);
        sheet.setColumnWidth(4, 3521);
        sheet.setColumnWidth(5, 3521);
        sheet.setColumnWidth(6, 3521);
        sheet.setColumnWidth(7, 3521);
        sheet.setColumnWidth(8, 3521);
        
        XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
        
        return workbook;
    }

}
