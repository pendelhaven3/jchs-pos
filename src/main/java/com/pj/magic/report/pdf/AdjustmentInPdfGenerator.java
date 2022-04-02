package com.pj.magic.report.pdf;

import java.io.File;
import java.math.RoundingMode;
import java.util.Date;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.AdjustmentInItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.util.FormatterUtil;

public class AdjustmentInPdfGenerator {

	private final File file;
	
    public AdjustmentInPdfGenerator(File file) {
		this.file = file;
	}

	public void generate(AdjustmentIn adjustmentIn) throws Exception {
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdf = new PdfDocument(writer);
        
        Document document = new Document(pdf);
        
        Paragraph para = new Paragraph("JCHS GROCERY\n\nInventory Adjustment In Report").setTextAlignment(TextAlignment.CENTER);
        document.add(para);

        document.add(new Paragraph("\n"));
        
        Table table = new Table(UnitValue.createPercentArray(new float[]{0.75f, 4, 0.5f, 1})).useAllAvailableWidth();
        
        table.addCell(PdfUtil.stringCell("Adj. In #:"));
        table.addCell(PdfUtil.stringCell(String.valueOf(adjustmentIn.getAdjustmentInNumber())));
        table.addCell(PdfUtil.stringCell("Date:"));
        table.addCell(PdfUtil.stringCell(FormatterUtil.formatDate(new Date())));
        
        table.addCell(PdfUtil.stringCell("Remarks:"));
        table.addCell(PdfUtil.stringCell(adjustmentIn.getRemarks()));
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.emptyCell());
        
        document.add(table);
        
        document.add(new Paragraph("\n"));
        
        table = new Table(UnitValue.createPercentArray(new float[]{2, 5, 1, 1, 1.5f, 1.75f})).useAllAvailableWidth();
        
    	table.addHeaderCell(PdfUtil.headerCell("CODE"));
    	table.addHeaderCell(PdfUtil.headerCell("DESCRIPTION"));
    	table.addHeaderCell(PdfUtil.headerCell("UNIT"));
    	table.addHeaderCell(PdfUtil.headerCell("QTY"));
    	table.addHeaderCell(PdfUtil.headerCell("COST"));
    	table.addHeaderCell(PdfUtil.headerCell("AMOUNT"));
    	
    	for (AdjustmentInItem item : adjustmentIn.getItems()) {
        	table.addCell(PdfUtil.stringTableCell(item.getCode()));
        	table.addCell(PdfUtil.stringTableCell(item.getProduct().getDescription()));
        	table.addCell(PdfUtil.stringTableCell(item.getUnit()));
        	table.addCell(PdfUtil.stringRightTableCell(String.valueOf(item.getQuantity())));
        	table.addCell(PdfUtil.stringRightTableCell(FormatterUtil.formatAmount(item.getEffectiveCost())));
        	table.addCell(PdfUtil.stringRightTableCell(FormatterUtil.formatAmount(item.getAmount())));
    	}
        
        document.add(table);

        table = new Table(UnitValue.createPercentArray(new float[]{1, 4.5f, 1, 1})).useAllAvailableWidth();
        table.setMarginTop(10);
        
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.stringRightCell("Total Amount:"));
        table.addCell(PdfUtil.stringRightCell(FormatterUtil.formatAmount(adjustmentIn.getTotalAmount())));

        document.add(table);
        
        document.add(new Paragraph("\n"));
        
        table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 5})).useAllAvailableWidth();
        
        table.addCell(PdfUtil.stringCell("Prepared By:"));
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.emptyCell());
        
        table.addCell(PdfUtil.stringCell("Verified By:"));
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.emptyCell());

        table.addCell(PdfUtil.stringCell("Total Items:"));
        table.addCell(PdfUtil.stringCell(String.valueOf(adjustmentIn.getTotalItems())));
        table.addCell(PdfUtil.stringCell("Approved By:"));

        document.add(table);

        document.close();
    }
	
}
