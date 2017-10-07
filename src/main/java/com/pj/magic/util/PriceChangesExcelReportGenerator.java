package com.pj.magic.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.ProductPriceHistory;
import com.pj.magic.model.Unit;
import com.pj.magic.model.report.PriceChangesReport;
import com.pj.magic.model.report.PriceChangesReportItemGroup;
import com.pj.magic.model.util.CellStyleBuilder;

public class PriceChangesExcelReportGenerator {

	private static String BLANK = " ";
	
	public static Workbook generate(PriceChangesReport report) throws IOException {
		Workbook workbook = new XSSFWorkbook(
				PriceChangesExcelReportGenerator.class.getResourceAsStream("/excel/priceChangesReport.xlsx"));
	
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short)10);

		Font smallFont = workbook.createFont();
		smallFont.setFontHeightInPoints((short)9);

		CellStyle defaultStyle = CellStyleBuilder.createStyle(workbook).setFont(font).build();
		CellStyle borderedStyle = CellStyleBuilder.createStyle(workbook).setFont(font).setFullBorderThin(true).build();
		CellStyle centeredStyle = CellStyleBuilder.createStyle(workbook)
				.setAlignment(CellStyle.ALIGN_CENTER).setFont(font).setFullBorderThin(true).build();
		CellStyle amountStyle = CellStyleBuilder.createStyle(workbook)
				.setAlignment(CellStyle.ALIGN_RIGHT).setAmountFormat(true).setFont(font).setFullBorderThin(true).build();
		CellStyle timeStyle = CellStyleBuilder.createStyle(workbook).setFont(font).setFullBorderThin(true)
				.setAlignment(CellStyle.ALIGN_RIGHT).build();
		CellStyle percentIncreaseStyle = CellStyleBuilder.createStyle(workbook).setFont(smallFont).setFullBorderThin(true)
				.build();
		
		Sheet sheet = workbook.getSheetAt(0);
		
		Header header = sheet.getHeader();
		header.setCenter(getHeader(report));
		
		int currentRow = 0;
		for (PriceChangesReportItemGroup group : report.getItemsGroupedByDate()) {
			Row row = sheet.createRow(currentRow);
			
			Cell cell = row.createCell(0, Cell.CELL_TYPE_STRING);
			cell.setCellValue("Effective Date: " + FormatterUtil.formatDate(group.getDate()));
			cell.setCellStyle(defaultStyle);
			
			currentRow++;
			
			row = sheet.createRow(currentRow);
			CellRangeAddress cellRange = new CellRangeAddress(currentRow, currentRow, 2, 6);
			sheet.addMergedRegion(cellRange);
			cell = row.createCell(2, Cell.CELL_TYPE_STRING);
			cell.setCellValue("NEW PRICE");
			cell.setCellStyle(centeredStyle);
			setFullBorderThin(cellRange, sheet, workbook);
			
			cellRange = new CellRangeAddress(currentRow, currentRow, 7, 11);
			sheet.addMergedRegion(cellRange);
			cell = row.createCell(7, Cell.CELL_TYPE_STRING);
			cell.setCellValue("OLD PRICE");
			cell.setCellStyle(centeredStyle);
			setFullBorderThin(cellRange, sheet, workbook);
			
			currentRow++;
			
			row = sheet.createRow(currentRow);
			cell = row.createCell(0, Cell.CELL_TYPE_STRING);
			cell.setCellValue("Time");
			cell.setCellStyle(centeredStyle);
			
			cell = row.createCell(1, Cell.CELL_TYPE_STRING);
			cell.setCellValue("Product Description");
			cell.setCellStyle(centeredStyle);
			
			cell = row.createCell(2, Cell.CELL_TYPE_STRING);
			cell.setCellValue("PCS");
			cell.setCellStyle(centeredStyle);
			
			cell = row.createCell(3, Cell.CELL_TYPE_STRING);
			cell.setCellValue("DOZ");
			cell.setCellStyle(centeredStyle);
			
			cell = row.createCell(4, Cell.CELL_TYPE_STRING);
			cell.setCellValue("CTN");
			cell.setCellStyle(centeredStyle);
			
			cell = row.createCell(5, Cell.CELL_TYPE_STRING);
			cell.setCellValue("TIE");
			cell.setCellStyle(centeredStyle);
			
			cell = row.createCell(6, Cell.CELL_TYPE_STRING);
			cell.setCellValue("CSE");
			cell.setCellStyle(centeredStyle);
			
			cell = row.createCell(7, Cell.CELL_TYPE_STRING);
			cell.setCellValue("PCS");
			cell.setCellStyle(centeredStyle);
			
			cell = row.createCell(8, Cell.CELL_TYPE_STRING);
			cell.setCellValue("DOZ");
			cell.setCellStyle(centeredStyle);
			
			cell = row.createCell(9, Cell.CELL_TYPE_STRING);
			cell.setCellValue("CTN");
			cell.setCellStyle(centeredStyle);
			
			cell = row.createCell(10, Cell.CELL_TYPE_STRING);
			cell.setCellValue("TIE");
			cell.setCellStyle(centeredStyle);
			
			cell = row.createCell(11, Cell.CELL_TYPE_STRING);
			cell.setCellValue("CSE");
			cell.setCellStyle(centeredStyle);
			
			cell = row.createCell(12, Cell.CELL_TYPE_STRING);
			cell.setCellValue("% Increase");
			cell.setCellStyle(percentIncreaseStyle);
			
			currentRow++;
			
			for (ProductPriceHistory item : group.getItems()) {
				row = sheet.createRow(currentRow);

				cell = row.createCell(0, Cell.CELL_TYPE_STRING);
				cell.setCellValue(FormatterUtil.formatTime(item.getUpdateDate()) + " ");
				cell.setCellStyle(timeStyle);
				
				cell = row.createCell(1, Cell.CELL_TYPE_STRING);
				cell.setCellValue(item.getProduct().getDescription());
				cell.setCellStyle(borderedStyle);
				
				createAmountCell(row, 2, item.getActiveUnitPrice(Unit.PIECES), amountStyle, borderedStyle);
				createAmountCell(row, 3, item.getActiveUnitPrice(Unit.DOZEN), amountStyle, borderedStyle);
				createAmountCell(row, 4, item.getActiveUnitPrice(Unit.CARTON), amountStyle, borderedStyle);
				createAmountCell(row, 5, item.getActiveUnitPrice(Unit.TIE), amountStyle, borderedStyle);
				createAmountCell(row, 6, item.getUnitPrice(Unit.CASE), amountStyle, borderedStyle);
				
				createAmountCell(row, 7, item.getPreviousActiveUnitPrice(Unit.PIECES), amountStyle, borderedStyle);
				createAmountCell(row, 8, item.getPreviousActiveUnitPrice(Unit.DOZEN), amountStyle, borderedStyle);
				createAmountCell(row, 9, item.getPreviousActiveUnitPrice(Unit.CARTON), amountStyle, borderedStyle);
				createAmountCell(row, 10, item.getPreviousActiveUnitPrice(Unit.TIE), amountStyle, borderedStyle);
				createAmountCell(row, 11, item.getPreviousUnitPrice(Unit.CASE), amountStyle, borderedStyle);
				
				BigDecimal percentIncrease = item.getPercentIncrease();
				if (percentIncrease != null) {
					createAmountCell(row, 12, percentIncrease, amountStyle, borderedStyle);
				} else {
					cell = row.createCell(12, Cell.CELL_TYPE_STRING);
					cell.setCellValue("-");
					cell.setCellStyle(borderedStyle);
				}
				
				currentRow++;
			}
			
			currentRow = currentRow + 2;
		}

		return workbook;
	}

	private static String getHeader(PriceChangesReport report) {
		if (report.getFromDate().equals(report.getToDate())) {
			String header = "JC HARMONY SELLING\n\nPRICE CHANGES REPORT\nas of {0}";
			return MessageFormat.format(header, 
					FormatterUtil.formatDate(report.getFromDate()));
		} else {
			String header = "JC HARMONY SELLING\n\nPRICE CHANGES REPORT\nas of {0} - {1}";
			return MessageFormat.format(header, 
					FormatterUtil.formatDate(report.getFromDate()),
					FormatterUtil.formatDate(report.getToDate()));
		}
	}

	private static void setFullBorderThin(CellRangeAddress cellRange, Sheet sheet, Workbook workbook) {
		int border = (int)CellStyle.BORDER_THIN;
		RegionUtil.setBorderLeft(border, cellRange, sheet, workbook);
		RegionUtil.setBorderTop(border, cellRange, sheet, workbook);
		RegionUtil.setBorderRight(border, cellRange, sheet, workbook);
		RegionUtil.setBorderBottom(border, cellRange, sheet, workbook);
	}

	private static void createAmountCell(Row row, int columnIndex, BigDecimal value, CellStyle amountStyle,
			CellStyle borderedStyle) {
		if (value != null) {
			Cell cell = row.createCell(columnIndex, Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue(value.doubleValue());
			cell.setCellStyle(amountStyle);
		} else {
			Cell cell = row.createCell(columnIndex, Cell.CELL_TYPE_STRING);
			cell.setCellValue(BLANK);
			cell.setCellStyle(borderedStyle);
		}
	}
	
}
