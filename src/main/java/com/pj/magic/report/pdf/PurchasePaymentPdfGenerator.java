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
        
        document.close();
    }
	
}
