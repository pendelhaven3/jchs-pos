package com.pj.magic.report.pdf;

import java.io.File;
import java.math.RoundingMode;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.util.FormatterUtil;

public class ReceivingReceiptPdfGenerator {

	private final File file;
	
    public ReceivingReceiptPdfGenerator(File file) {
		this.file = file;
	}

	public void generate(ReceivingReceipt receivingReceipt) throws Exception {
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdf = new PdfDocument(writer);
        
        Document document = new Document(pdf);
        
        Paragraph para = new Paragraph("JCHS GROCERY\nRECEIVING REPORT").setTextAlignment(TextAlignment.CENTER);
        document.add(para);

        document.add(new Paragraph("\n"));
        
        Table table = new Table(UnitValue.createPercentArray(new float[]{0.75f, 4, 0.5f, 1})).useAllAvailableWidth();
        
        table.addCell(PdfUtil.stringCell("Supplier:"));
        table.addCell(PdfUtil.stringCell(receivingReceipt.getSupplier().getName()));
        table.addCell(PdfUtil.stringCell("RR #:"));
        table.addCell(PdfUtil.stringCell(String.valueOf(receivingReceipt.getReceivingReceiptNumber())));

        table.addCell(PdfUtil.stringCell("Address:"));
        table.addCell(PdfUtil.stringCell(String.valueOf(receivingReceipt.getRelatedPurchaseOrderNumber())));
        table.addCell(PdfUtil.stringCell("Date:"));
        table.addCell(PdfUtil.stringCell(FormatterUtil.formatDate(receivingReceipt.getReceivedDate())));
        
        table.addCell(PdfUtil.stringCell("Fax:"));
        table.addCell(PdfUtil.stringCell(receivingReceipt.getSupplier().getFaxNumber()));
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.emptyCell());
        
        table.addCell(PdfUtil.stringCell("Contact:"));
        table.addCell(PdfUtil.stringCell(receivingReceipt.getSupplier().getContactNumber()));
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.emptyCell());
        
        document.add(table);
        
        document.add(new Paragraph("\n"));
        
        table = new Table(UnitValue.createPercentArray(new float[]{2, 5, 1, 1, 1.5f, 1.75f})).useAllAvailableWidth();
        
    	table.addHeaderCell(PdfUtil.headerCell("CODE"));
    	table.addHeaderCell(PdfUtil.headerCell("DESCRIPTION"));
    	table.addHeaderCell(PdfUtil.headerCell("UNIT"));
    	table.addHeaderCell(PdfUtil.headerCell("QTY"));
    	table.addHeaderCell(PdfUtil.headerCell("NET COST"));
    	table.addHeaderCell(PdfUtil.headerCell("WITH VAT"));
    	
    	for (ReceivingReceiptItem item : receivingReceipt.getItems()) {
        	table.addCell(PdfUtil.stringTableCell(item.getCode()));
        	table.addCell(PdfUtil.stringTableCell(item.getProduct().getDescription()));
        	table.addCell(PdfUtil.stringTableCell(item.getUnit()));
        	table.addCell(PdfUtil.stringRightTableCell(String.valueOf(item.getQuantity())));
        	table.addCell(PdfUtil.stringRightTableCell(FormatterUtil.formatAmount(item.getFinalCost())));
        	
        	if (receivingReceipt.isVatInclusive()) {
            	table.addCell(PdfUtil.stringRightTableCell(FormatterUtil.formatAmount(item.getFinalCost())));
        	} else {
            	table.addCell(PdfUtil.stringRightTableCell(FormatterUtil.formatAmount(
            			item.getFinalCostWithVat().setScale(2, RoundingMode.HALF_UP))));
        	}
    	}
        
        document.add(table);

        document.add(new Paragraph("\n"));
        
        table = new Table(UnitValue.createPercentArray(new float[]{1, 0.75f, 0.75f, 3, 1, 1})).useAllAvailableWidth();
        
        table.addCell(PdfUtil.stringCell("Total Items:"));
        table.addCell(PdfUtil.stringCell(String.valueOf(receivingReceipt.getTotalNumberOfItems())));
        table.addCell(PdfUtil.stringCell("Total Qty:"));
        table.addCell(PdfUtil.stringCell(String.valueOf(receivingReceipt.getTotalQuantity())));
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.emptyCell());

        document.add(table);

        document.add(new Paragraph("\n"));
        
        table = new Table(UnitValue.createPercentArray(new float[]{1, 6.5f})).useAllAvailableWidth();
        
        table.addCell(PdfUtil.stringCell("Prepared By:"));
        table.addCell(PdfUtil.stringCell(receivingReceipt.getReceivedBy().getUsername()));
        
        document.add(table);
        
        document.add(new Paragraph("\n"));
        
        table = new Table(UnitValue.createPercentArray(new float[]{1, 6.5f})).useAllAvailableWidth();
        
        table.addCell(PdfUtil.stringCell("Remarks:"));
        table.addCell(PdfUtil.stringCell(receivingReceipt.getRemarks()));
        
        document.add(table);
        
        document.close();
    }
	
}
