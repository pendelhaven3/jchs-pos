package com.pj.magic.report.pdf;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

public class PdfUtil {

	public static Cell headerCell(String text) throws IOException {
	    PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
	    
		return new Cell()
				.add(new Paragraph(text).setFont(font).setFontSize(8))
		.setTextAlignment(TextAlignment.CENTER);
	}
	
	public static Cell stringCell(String text) throws IOException {
	    PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
	    
		return new Cell()
				.add(new Paragraph(StringUtils.defaultString(text)).setFont(font).setFontSize(8))
		.setBorder(Border.NO_BORDER);
	}
	
	public static Cell stringTableCell(String text) throws IOException {
	    PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
	    
		return new Cell()
				.add(new Paragraph(text).setFont(font).setFontSize(8));
	}
	
	public static Cell stringRightCell(String text) throws IOException {
	    PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
	    
		return new Cell()
				.add(new Paragraph(text).setFont(font).setFontSize(8))
		.setTextAlignment(TextAlignment.RIGHT)
		.setBorder(Border.NO_BORDER);
	}
	
	public static Cell stringRightTableCell(String text) throws IOException {
	    PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
	    
		return new Cell()
				.add(new Paragraph(text).setFont(font).setFontSize(8))
		.setTextAlignment(TextAlignment.RIGHT);
	}
	
	public static Cell emptyCell() {
		return new Cell()
				.add(new Paragraph(""))
				.setBorder(Border.NO_BORDER);
	}
	
}
