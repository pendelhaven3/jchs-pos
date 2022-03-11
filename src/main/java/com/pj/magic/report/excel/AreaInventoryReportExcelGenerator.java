package com.pj.magic.report.excel;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.AreaInventoryReportItem;
import com.pj.magic.model.util.CellStyleBuilder;

public class AreaInventoryReportExcelGenerator {

    public Workbook generateSpreadsheet(AreaInventoryReport areaInventoryReport) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(getClass().getResourceAsStream("/excel/areaInventoryReport.xlsx"));
        
        CellStyle centered = CellStyleBuilder.createStyle(workbook)
                .setAlignment(CellStyle.ALIGN_CENTER).build();
        
        Sheet sheet = workbook.getSheetAt(0);
        int currentRow = 0;
        
        Row row = sheet.getRow(currentRow);
        Cell cell = row.getCell(0);
        cell.setCellValue("AREA INVENTORY REPORT - " + new SimpleDateFormat("MM-dd-yyyy").format(areaInventoryReport.getParent().getInventoryDate()));
        
        currentRow++;
        currentRow++;
        
        row = sheet.getRow(currentRow);
        cell = row.createCell(1);
        cell.setCellValue(areaInventoryReport.getReportNumber().toString());
        
        currentRow++;
        
        row = sheet.getRow(currentRow);
        cell = row.createCell(1);
        cell.setCellValue(areaInventoryReport.getCreatedBy().getUsername());
        
        currentRow++;
        
        row = sheet.getRow(currentRow);
        cell = row.createCell(1);
        cell.setCellValue(areaInventoryReport.getArea().getName());
        
        currentRow++;
        
        row = sheet.getRow(currentRow);
        cell = row.createCell(1);
        cell.setCellValue(areaInventoryReport.getChecker());
        
        currentRow++;
        
        row = sheet.getRow(currentRow);
        cell = row.createCell(1);
        cell.setCellValue(areaInventoryReport.getDoubleChecker());
        
        currentRow++;
        currentRow++;
        
        for (AreaInventoryReportItem item : areaInventoryReport.getItems()) {
            currentRow++;
            
            row = sheet.createRow(currentRow);
            
            row.createCell(0).setCellValue(item.getCode());
            row.createCell(1).setCellValue(item.getProduct().getDescription());
            row.createCell(2).setCellValue(item.getUnit());
            row.createCell(3).setCellValue(item.getQuantity());
            
            cell = row.createCell(4);
            cell.setCellValue("[        ]");
            cell.setCellStyle(centered);
        }
        
        return workbook;
    }
	
}
