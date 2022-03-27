package com.pj.magic.report.pdf;

import java.io.File;
import java.util.List;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.service.PrintService;

public class PurchasePaymentPdfGenerator {

	private final PrintService printService;
	private final File file;
	
    public PurchasePaymentPdfGenerator(PrintService printService, File file) {
    	this.printService = printService;
		this.file = file;
	}

	public void generate(PurchasePayment purchasePayment) throws Exception {
		List<String> printouts = printService.generateReportAsString(purchasePayment);
		
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdf = new PdfDocument(writer);
        
        Document document = new Document(pdf);
        
        Text text = new Text(printouts.get(0));
        text.setNextRenderer(new CustomTextRenderer(text));
        
        Paragraph para = new Paragraph(text);
        para.setFont(PdfFontFactory.createFont(StandardFonts.COURIER));
        document.add(para);
        
        /*
        Paragraph para = new Paragraph("JOSELLE CHRISTIAN GENERAL MERCHANDISE\nPAYMENT VOUCHER").setTextAlignment(TextAlignment.CENTER);
        document.add(para);

        document.add(new Paragraph("\n"));
        
        Table table = new Table(UnitValue.createPercentArray(new float[]{0.75f, 4, 0.5f, 1})).useAllAvailableWidth();
        
        table.addCell(PdfUtil.stringCell("Supplier:"));
        table.addCell(PdfUtil.stringCell(receivingReceipt.getSupplier().getName()));
        table.addCell(PdfUtil.stringCell("RR #:"));
        table.addCell(PdfUtil.stringCell(String.valueOf(receivingReceipt.getReceivingReceiptNumber())));

        table.addCell(PdfUtil.stringCell("PO #:"));
        table.addCell(PdfUtil.stringCell(String.valueOf(receivingReceipt.getRelatedPurchaseOrderNumber())));
        table.addCell(PdfUtil.stringCell("Ref #:"));
        table.addCell(PdfUtil.stringCell(receivingReceipt.getReferenceNumber()));
        
        table.addCell(PdfUtil.stringCell("Terms:"));
        table.addCell(PdfUtil.stringCell(receivingReceipt.getPaymentTerm().getName()));
        table.addCell(PdfUtil.stringCell("Date:"));
        table.addCell(PdfUtil.stringCell(FormatterUtil.formatDate(receivingReceipt.getReceivedDate())));
        
        document.add(table);
        
        document.add(new Paragraph("\n"));
        
        table = new Table(UnitValue.createPercentArray(new float[]{2, 5, 1, 1, 1.5f, 1.75f})).useAllAvailableWidth();
        
    	table.addHeaderCell(PdfUtil.headerCell("CODE"));
    	table.addHeaderCell(PdfUtil.headerCell("DESCRIPTION"));
    	table.addHeaderCell(PdfUtil.headerCell("UNIT"));
    	table.addHeaderCell(PdfUtil.headerCell("QTY"));
    	table.addHeaderCell(PdfUtil.headerCell("COST"));
    	table.addHeaderCell(PdfUtil.headerCell("AMOUNT"));
    	
    	for (ReceivingReceiptItem item : receivingReceipt.getItems()) {
        	table.addCell(PdfUtil.stringTableCell(item.getCode()));
        	table.addCell(PdfUtil.stringTableCell(item.getProduct().getDescription()));
        	table.addCell(PdfUtil.stringTableCell(item.getUnit()));
        	table.addCell(PdfUtil.stringRightTableCell(String.valueOf(item.getQuantity())));
        	table.addCell(PdfUtil.stringRightTableCell(FormatterUtil.formatAmount(item.getCost())));
        	table.addCell(PdfUtil.stringRightTableCell(FormatterUtil.formatAmount(item.getAmount())));
    	}
        
        document.add(table);

        document.add(new Paragraph("\n"));
        
        table = new Table(UnitValue.createPercentArray(new float[]{1, 0.75f, 0.75f, 3, 1, 1})).useAllAvailableWidth();
        
        table.addCell(PdfUtil.stringCell("Total Items:"));
        table.addCell(PdfUtil.stringCell(String.valueOf(receivingReceipt.getTotalNumberOfItems())));
        table.addCell(PdfUtil.stringCell("Encoder:"));
        table.addCell(PdfUtil.stringCell(receivingReceipt.getReceivedBy().getUsername()));
        table.addCell(PdfUtil.stringRightCell("Sub Total:"));
        table.addCell(PdfUtil.stringRightCell(FormatterUtil.formatAmount(receivingReceipt.getSubTotalAmount())));

        table.addCell(PdfUtil.stringCell("Total Qty Order:"));
        table.addCell(PdfUtil.stringCell(String.valueOf(receivingReceipt.getTotalQuantity())));
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.stringRightCell("Discount:"));
        table.addCell(PdfUtil.stringRightCell(FormatterUtil.formatAmount(receivingReceipt.getTotalDiscountedAmount())));
        
        document.add(table);
        
        table = new Table(UnitValue.createPercentArray(new float[]{1, 4.5f, 1, 1})).useAllAvailableWidth();
        
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.stringRightCell("--------------------"));
        
        table.addCell(PdfUtil.stringCell("Remarks:"));
        table.addCell(PdfUtil.stringCell(receivingReceipt.getRemarks()));
        table.addCell(PdfUtil.stringRightCell("Net Amount:"));
        table.addCell(PdfUtil.stringRightCell(FormatterUtil.formatAmount(receivingReceipt.getTotalNetAmount())));

        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.stringRightCell("VAT Amount:"));
        table.addCell(PdfUtil.stringRightCell(FormatterUtil.formatAmount(receivingReceipt.getVatAmount())));
        
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.stringRightCell("--------------------"));
        
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.stringRightCell("TOTAL Amount:"));
        table.addCell(PdfUtil.stringRightCell(FormatterUtil.formatAmount(receivingReceipt.getTotalAmount())));

        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.emptyCell());
        table.addCell(PdfUtil.stringRightCell("=========="));
        
        document.add(table);
        */
        
        document.close();
    }
	
}
