package com.pj.magic.report.excel;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.InventoryCheckSummaryItem;
import com.pj.magic.model.util.CellStyleBuilder;

public class InventoryCheckExcelGenerator {

    public Workbook generateSpreadsheet(InventoryCheck inventoryCheck) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(getClass().getResourceAsStream("/excel/inventoryCheck.xlsx"));
        
        CellStyle centered = CellStyleBuilder.createStyle(workbook)
                .setAlignment(CellStyle.ALIGN_CENTER).build();
        CellStyle amountFormat = CellStyleBuilder.createStyle(workbook)
                .setAmountFormat(true).build();
        CellStyle rightAligned = CellStyleBuilder.createStyle(workbook)
                .setAlignment(CellStyle.ALIGN_RIGHT).build();
        
        Sheet sheet = workbook.getSheetAt(0);
        int currentRow = 3;
        Row row = sheet.getRow(currentRow);
        
        Cell cell = row.getCell(0);
        cell.setCellValue("ACTUAL COUNT AS OF - " + new SimpleDateFormat("MM-dd-yyyy").format(inventoryCheck.getInventoryDate()));
        cell.setCellStyle(centered);
        
        currentRow++;
        currentRow++;
        
        List<InventoryCheckSummaryItem> items = inventoryCheck.getSummaryItems().stream()
        		.filter(item -> item.getQuantity() > 0)
        		.collect(Collectors.toList());
        
        for (InventoryCheckSummaryItem item : items) {
            currentRow++;
            
            row = sheet.createRow(currentRow);
            
            row.createCell(0).setCellValue(item.getCode());
            row.createCell(1).setCellValue(item.getProduct().getDescription());
            row.createCell(2).setCellValue(item.getUnit());
            row.createCell(3).setCellValue(item.getQuantity());
            
            cell = row.createCell(4);
            cell.setCellValue(item.getCost().doubleValue());
            cell.setCellStyle(amountFormat);
            
            cell = row.createCell(5);
            cell.setCellValue(item.getActualValue().doubleValue());
            cell.setCellStyle(amountFormat);
        }
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        
        cell = row.createCell(4);
        cell.setCellValue("Inventory Value:");
        cell.setCellStyle(rightAligned);
        
        cell = row.createCell(5);
        cell.setCellFormula(MessageFormat.format("SUM(F7:F{0})", String.valueOf(currentRow - 1)));
        cell.setCellStyle(amountFormat);
        
        XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
        
        return workbook;
    }
	
}
