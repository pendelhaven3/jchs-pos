package com.pj.magic.util;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;

import org.springframework.stereotype.Component;

@Component
public class PrinterUtil {

	private static final DocFlavor DOC_FLAVOR = DocFlavor.INPUT_STREAM.AUTOSENSE;
	private static final String FORM_FEED = "\f";
	private static final char CONDENSED_FONT_ON = 15;
	private static final char CONDENSED_FONT_OFF = 18;
	
	// TODO: Put this in database
	private static final List<String> SUPPORTED_PRINTERS = Arrays.asList("EPSON LX-310 ESC/P", "EPSON LX-300+ /II");

	public void print(String data) throws PrintException {
		print((data + FORM_FEED).getBytes());
	}

	public void printWithCondensedFont(String data) throws PrintException {
		StringBuilder sb = new StringBuilder();
		sb.append(CONDENSED_FONT_ON);
		sb.append(data);
		sb.append(CONDENSED_FONT_OFF);
		sb.append(" ");
		sb.append(FORM_FEED);
		
		print(sb.toString().getBytes());
	}

	private static void print(byte[] data) throws PrintException {
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(DOC_FLAVOR, null);
		PrintService epsonPrintService = null;
		for (PrintService printService : printServices) {
			if (SUPPORTED_PRINTERS.contains(printService.getName()) && isAcceptingJobs(printService)) {
				epsonPrintService = printService;
				break;
			}
		}

		if (epsonPrintService == null) {
			throw new RuntimeException("No supported printer connected");
		}

		DocPrintJob printJob = epsonPrintService.createPrintJob();
		Doc doc = new SimpleDoc(new ByteArrayInputStream(data), DOC_FLAVOR, null);
		printJob.print(doc, null);
	}

	private static boolean isAcceptingJobs(PrintService printService) {
		return printService.getAttribute(PrinterIsAcceptingJobs.class)
				.equals(PrinterIsAcceptingJobs.ACCEPTING_JOBS);
	}
	
}