package com.pj.magic.excel;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.BirForm2307Report;
import com.pj.magic.model.util.CellStyleBuilder;

public class BirForm2307ReportExcelGenerator {

    private final int SHAPE_INDEX_PERIOD_FROM_MONTH = 39;
    private final int SHAPE_INDEX_PERIOD_FROM_DATE = 36;
    private final int SHAPE_INDEX_PERIOD_FROM_YEAR = 10;
    private final int SHAPE_INDEX_PERIOD_TO_MONTH = 41;
    private final int SHAPE_INDEX_PERIOD_TO_DATE = 42;
    private final int SHAPE_INDEX_PERIOD_TO_YEAR = 9;

    private final int SHAPE_INDEX_PAYEE_TIN_1 = 8;
    private final int SHAPE_INDEX_PAYEE_TIN_2 = 0;
    private final int SHAPE_INDEX_PAYEE_TIN_3 = 7;
    private final int SHAPE_INDEX_PAYEE_TIN_4 = 6;
    private final int SHAPE_INDEX_PAYEE_NAME = 18;
    private final int SHAPE_INDEX_PAYEE_ADDRESS = 19;
    private final int SHAPE_INDEX_PAYEE_ZIP_CODE = 1;
    
    private final int SHAPE_INDEX_PAYOR_TIN_1 = 5;
    private final int SHAPE_INDEX_PAYOR_TIN_2 = 4;
    private final int SHAPE_INDEX_PAYOR_TIN_3 = 3;
    private final int SHAPE_INDEX_PAYOR_TIN_4 = 2;
    private final int SHAPE_INDEX_PAYOR_NAME = 22;
    private final int SHAPE_INDEX_PAYOR_ADDRESS = 23;
    private final int SHAPE_INDEX_PAYOR_ZIP_CODE = 45;
    
    private final Pattern TIN_PATTERN = Pattern.compile("^(\\d{3}-\\d{3}-\\d{3}-(\\d{3}|\\d{5}))$");
    
    private List<XSSFShape> shapes;
    
    public Workbook generate(BirForm2307Report report) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(getClass().getResourceAsStream("/excel/form2307b.xlsx"));
        
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFDrawing patriarch = sheet.createDrawingPatriarch();
        shapes = patriarch.getShapes();
        
        Calendar fromDate = DateUtils.toCalendar(report.getForm2307FromDate());
        Calendar toDate = DateUtils.toCalendar(report.getForm2307ToDate());
        
        setText(SHAPE_INDEX_PERIOD_FROM_MONTH, StringUtils.leftPad(String.valueOf(fromDate.get(Calendar.MONTH) + 1), 2, '0'), 7.6);
        setText(SHAPE_INDEX_PERIOD_FROM_DATE, StringUtils.leftPad(String.valueOf(fromDate.get(Calendar.DATE)), 2, '0'), 7.6);
        setText(SHAPE_INDEX_PERIOD_FROM_YEAR, String.valueOf(fromDate.get(Calendar.YEAR)), 14.4);
        
        setText(SHAPE_INDEX_PERIOD_TO_MONTH, StringUtils.leftPad(String.valueOf(toDate.get(Calendar.MONTH) + 1), 2, '0'), 7.6);
        setText(SHAPE_INDEX_PERIOD_TO_DATE, StringUtils.leftPad(String.valueOf(toDate.get(Calendar.DATE)), 2, '0'), 7.6);
        setText(SHAPE_INDEX_PERIOD_TO_YEAR, String.valueOf(toDate.get(Calendar.YEAR)), 14.4);
        
        if (isValidTin(report.getSupplier().getTin())) {
            String[] tinParts = report.getSupplier().getTin().split("-");
            
            setText(SHAPE_INDEX_PAYEE_TIN_1, tinParts[0], 10.8);
            setText(SHAPE_INDEX_PAYEE_TIN_2, tinParts[1], 10.8);
            setText(SHAPE_INDEX_PAYEE_TIN_3, tinParts[2], 10.8);
            setText(SHAPE_INDEX_PAYEE_TIN_4, tinParts[3], 10.8);
        }
        
        setNameText(SHAPE_INDEX_PAYEE_NAME, report.getSupplier().getName().toUpperCase());
        setAddressText(SHAPE_INDEX_PAYEE_ADDRESS, report.getSupplier().getAddress().toUpperCase(), workbook);
        
        XSSFFont amountFont = workbook.createFont();
        amountFont.setFontName("Arial");
        amountFont.setFontHeight(8);
        
        CellStyle amountStyle = CellStyleBuilder.createStyle(workbook)
        		.setFont(amountFont).setAmountFormat(true).setFullBorderThin(true)
        		.setAlignment(CellStyle.ALIGN_CENTER)
        		.setVerticalAlignment(CellStyle.VERTICAL_CENTER)
        		.build();
        
        Row row = sheet.getRow(37);
        Cell cell;
        
        if (!report.getMonth1NetAmount().equals(BigDecimal.ZERO)) {
            cell = row.getCell(14);
            cell.setCellValue(report.getMonth1NetAmount().doubleValue());
            cell.setCellStyle(amountStyle);
        }
        
        if (!report.getMonth2NetAmount().equals(BigDecimal.ZERO)) {
            cell = row.getCell(19);
            cell.setCellValue(report.getMonth2NetAmount().doubleValue());
            cell.setCellStyle(amountStyle);
        }
        
        if (!report.getMonth3NetAmount().equals(BigDecimal.ZERO)) {
            cell = row.getCell(24);
            cell.setCellValue(report.getMonth3NetAmount().doubleValue());
            cell.setCellStyle(amountStyle);
        }
        
//        Font font = workbook.createFont();
//        font.setFontHeightInPoints((short)6);
//        CellStyle reportNumberStyle = CellStyleBuilder.createStyle(workbook).setFont(font).build();
        
//        row = sheet.getRow(79);
//        cell = row.createCell(43);
//        cell.setCellValue(report.getReportNumber());
//        cell.setCellStyle(reportNumberStyle);

        HSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
        
        return workbook;
    }

    private void setText(int index, String text, double leftInset) {
        XSSFRichTextString string = new XSSFRichTextString(text);
        XSSFSimpleShape shape = (XSSFSimpleShape)shapes.get(index);
        
        shape.setTopInset(1.5d);
        shape.setLeftInset(leftInset);
        shape.setText(string);
    }
    
    private void setNameText(int index, String text) {
        XSSFRichTextString string = new XSSFRichTextString("  " + text);
        XSSFSimpleShape shape = (XSSFSimpleShape)shapes.get(index);
        
        shape.setTopInset(1.5d);
        shape.setText(string);
    }
    
    private void setAddressText(int index, String text, Workbook workbook) {
        XSSFRichTextString string = new XSSFRichTextString("  " + text);
        
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short)9);
        string.applyFont(font);
        
        XSSFSimpleShape shape = (XSSFSimpleShape)shapes.get(index);
        shape.setText(string);
    }
    
    private boolean isValidTin(String tin) {
        if (!StringUtils.isEmpty(tin)) {
            return TIN_PATTERN.matcher(tin).matches();
        }
        return false;
    }
    
}
