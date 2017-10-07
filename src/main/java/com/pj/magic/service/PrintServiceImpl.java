package com.pj.magic.service;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.PrintException;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.pj.magic.dao.SalesInvoiceDao;
import com.pj.magic.dao.StockQuantityConversionDao;
import com.pj.magic.dao.SupplierDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.dao.UserDao;
import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.AdjustmentInItem;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.AdjustmentOutItem;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.AreaInventoryReportItem;
import com.pj.magic.model.AvailedPromoPointsItem;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.BadStockReturnItem;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.InventoryCheckSummaryItem;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.NoMoreStockAdjustmentItem;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentCheckPayment;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionReward;
import com.pj.magic.model.PromoType2Rule;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchaseOrderItem;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchaseReturnBadStock;
import com.pj.magic.model.PurchaseReturnBadStockItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.SalesReturnItem;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.StockQuantityConversionItem;
import com.pj.magic.model.report.CashFlowReport;
import com.pj.magic.model.report.CashFlowReportItem;
import com.pj.magic.model.report.PostedSalesAndProfitReport;
import com.pj.magic.model.report.PostedSalesAndProfitReportItem;
import com.pj.magic.model.report.PriceChangesReport;
import com.pj.magic.model.report.RemittanceReport;
import com.pj.magic.model.report.UnpaidSalesInvoicesReport;
import com.pj.magic.model.util.InventoryCheckReportType;
import com.pj.magic.model.util.InventoryCheckSummaryPrintItem;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.PrinterUtil;
import com.pj.magic.util.report.PaymentReportUtil;
import com.pj.magic.util.report.PostedSalesAndProfitReportReportUtil;
import com.pj.magic.util.report.PriceListReportUtil;
import com.pj.magic.util.report.ReceivingReceiptReportUtil;
import com.pj.magic.util.report.ReportUtil;

@Service 
public class PrintServiceImpl implements PrintService {

	private static final Logger logger = LoggerFactory.getLogger(PrintServiceImpl.class);
	
	private static final int SALES_INVOICE_ITEMS_PER_PAGE = 44;
	private static final int STOCK_QUANTITY_CONVERSION_ITEMS_PER_PAGE = 44;
	private static final int PURCHASE_ORDER_ITEMS_PER_PAGE = 44;
	private static final int RECEIVING_RECEIPT_ITEMS_PER_PAGE = 44;
	private static final int PRICING_SCHEME_REPORT_LINES_PER_PAGE = 44;
	private static final int INVENTORY_CHECK_SUMMARY_ITEMS_PER_PAGE = 52;
	public static final int INVENTORY_REPORT_COLUMNS_PER_LINE = 84;
	public static final int INVENTORY_REPORT_COMPLETE_COLUMNS_PER_LINE = 96;
	private static final int SALES_INVOICE_BIR_FORM_ITEMS_PER_PAGE = 13;
	private static final int AREA_INVENTORY_REPORT_ITEMS_PER_PAGE = 44;
	private static final int ADJUSTMENT_OUT_ITEMS_PER_PAGE = 44;
	private static final int ADJUSTMENT_IN_ITEMS_PER_PAGE = 44;
	private static final int UNPAID_SALES_INVOICE_ITEMS_PER_PAGE = 44;
	private static final int PRICE_LIST_ITEMS_PER_PAGE = 46;
	private static final int POSTED_SALES_AND_PROFIT_REPORT_ITEMS_PER_PAGE = 44;
	private static final int SALES_RETURN_ITEMS_PER_PAGE = 44;
	private static final int BAD_STOCK_RETURN_ITEMS_PER_PAGE = 44;
	private static final int NO_MORE_STOCK_ADJUSTMENT_ITEMS_PER_PAGE = 44;
	private static final int CASH_FLOW_REPORT_ITEMS_PER_PAGE = 44;
	private static final int REMITTANCE_REPORT_ITEMS_PER_PAGE = 44;
	private static final int PRICE_CHANGES_REPORT_ITEMS_PER_PAGE = 52;
	private static final int PURCHASE_RETURN_BAD_STOCK_ITEMS_PER_PAGE = 44;
	
	public static final int UNPAID_SALES_INVOICE_REPORT_CHARACTERS_PER_LINE = 90;
	public static final int PRICE_LIST_CHARACTERS_PER_LINE = 84;
	public static final int SALES_INVOICE_REPORT_COST_PROFIT_CHARACTERS_PER_LINE = 113;
	public static final int POSTED_SALES_AND_PROFIT_REPORT_CHARACTERS_PER_LINE = 129;
	public static final int PAID_SALES_INVOICES_REPORT_CHARACTERS_PER_LINE = 101;
	public static final int CASH_FLOW_REPORT_CHARACTERS_PER_LINE = 94;
	public static final int REMITTANCE_REPORT_CHARACTERS_PER_LINE = 90;
	public static final int PRICE_CHANGES_REPORT_CHARACTERS_PER_LINE = 93;
	
	private static final int LEFT_PADDING_SIZE_FOR_CONDENSED_FONT = 25;
	public static final String LEFT_PADDING_FOR_CONDENSED_FONT =
			StringUtils.repeat(" ", LEFT_PADDING_SIZE_FOR_CONDENSED_FONT);
	
	@Autowired private SupplierDao supplierDao;
	@Autowired private UserDao userDao;
	@Autowired private StockQuantityConversionDao stockQuantityConversionDao;
	@Autowired private SalesInvoiceDao salesInvoiceDao;
	@Autowired private SystemDao systemDao;
	@Autowired private PromoRedemptionService promoRedemptionService;
	@Autowired private PrinterUtil printerUtil;
	
	public PrintServiceImpl() {
		Velocity.setProperty("file.resource.loader.class", 
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init();
	}
	
	@Override
	public void print(SalesInvoice salesInvoice) {
		salesInvoice.setPrinted(true);
		salesInvoiceDao.save(salesInvoice);
		
		try {
			for (String printPage : generateReportAsString(salesInvoice)) {
				printerUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void print(PurchaseOrder purchaseOrder, boolean includeCost) {
		try {
			for (String printPage : generateReportAsString(purchaseOrder, includeCost)) {
				printerUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void print(ReceivingReceipt receivingReceipt, boolean includeDiscountDetails) {
		try {
			for (String printPage : generateReportAsString(receivingReceipt, includeDiscountDetails)) {
				printerUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(PurchaseOrder purchaseOrder, boolean includeCost) {
		purchaseOrder.setSupplier(supplierDao.get(purchaseOrder.getSupplier().getId()));
		
		String currentDate = FormatterUtil.formatDate(systemDao.getCurrentDateTime());
		
		String reportFile = (includeCost) ? "purchaseOrder-withCost.vm" : "purchaseOrder.vm";
		
		List<List<PurchaseOrderItem>> pageItems = Lists.partition(purchaseOrder.getItems(), 
				PURCHASE_ORDER_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("purchaseOrder", purchaseOrder);
			reportData.put("items", pageItems.get(i));
			reportData.put("currentDate", currentDate);
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/" + reportFile, reportData));
		}
		return printPages;
	}

	private String generateReportAsString(String templateName, Map<String, Object> reportData) {
		Template template = Velocity.getTemplate(templateName);
		StringWriter writer = new StringWriter();
		VelocityContext context = new VelocityContext(reportData);
		if (reportData.containsKey("reportUtil")) {
			context.put("report", reportData.get("reportUtil"));
		} else {
			context.put("report", ReportUtil.class);
		}
		template.merge(context, writer);
		return writer.toString();
	}

	@Override
	public List<String> generateReportAsString(PricingScheme pricingScheme, List<Product> products,
			boolean includeCosts) {
		String currentDate = FormatterUtil.formatDate(systemDao.getCurrentDateTime());
		
		List<List<Product>> pageItems = null;
		if (includeCosts) {
			pageItems = partitionPricingSchemeProducts(products);
		} else {
			pageItems = Lists.partition(products, PRICE_LIST_ITEMS_PER_PAGE);
		}
		
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("pricingScheme", pricingScheme);
			reportData.put("products", pageItems.get(i));
			reportData.put("currentDate", currentDate);
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			if (includeCosts) {
				printPages.add(generateReportAsString("reports/pricingScheme.vm", reportData));
			} else {
				reportData.put("reportUtil", PriceListReportUtil.class);
				printPages.add(generateReportAsString("reports/pricingScheme-pricesOnly.vm", reportData));
			}
		}
		return printPages;
	}
	
	private List<List<Product>> partitionPricingSchemeProducts(List<Product> products) {
		List<List<Product>> pageItems = new ArrayList<>();
		List<Product> pageItem = new ArrayList<>();
		int counter = 0;
		for (Product product : products) {
			int lineCount = 1 + product.getActiveUnits().size();
			if (counter + lineCount > PRICING_SCHEME_REPORT_LINES_PER_PAGE) {
				pageItems.add(pageItem);
				pageItem = new ArrayList<>();
				counter = 0;
			}
			pageItem.add(product);
			counter += lineCount;
		}
		pageItems.add(pageItem);
		return pageItems;
	}

	@Override
	public void print(PricingScheme pricingScheme, List<Product> products, boolean includeCosts) {
		try {
			for (String printPage : generateReportAsString(pricingScheme, products, includeCosts)) {
				if (includeCosts) {
					printerUtil.print(printPage);
				} else {
					printerUtil.printWithCondensedFont(printPage);
				}
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void print(List<String> printPages) {
		try {
			for (String printPage : printPages) {
				printerUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void printWithCondensedFont(List<String> printPages) {
		try {
			for (String printPage : printPages) {
				printerUtil.printWithCondensedFont(printPage);
//				PrinterUtil.print(addLeftPaddingForCondensedFont(printPage));
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(SalesInvoice salesInvoice) {
		List<PromoRedemption> promoRedemptions = 
				promoRedemptionService.findAllAvailedPromoRedemptions(salesInvoice);
		List<AvailedPromoPointsItem> availedPromoPointsItems =
				promoRedemptionService.findAllAvailedPromoPoints(salesInvoice);
		
		Collections.sort(salesInvoice.getItems());
		
		List<List<SalesInvoiceItem>> pageItems = Lists.partition(salesInvoice.getItems(), 
				SALES_INVOICE_ITEMS_PER_PAGE);
		
		Map<String, Object> reportData = new HashMap<>();
		reportData.put("salesInvoice", salesInvoice);
		reportData.put("promoRedemptions", promoRedemptions);
		reportData.put("totalPages", pageItems.size());
		reportData.put("totalItems", salesInvoice.getItems().size() + getTotalRewards(promoRedemptions));
		reportData.put("totalQuantity", salesInvoice.getTotalQuantity() + 
				getTotalRewardQuantity(promoRedemptions));
		reportData.put("availedPromoPointsItems", availedPromoPointsItems);
		
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			reportData.put("currentPage", i + 1);
			reportData.put("items", pageItems.get(i));
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/salesInvoice.vm", reportData));
		}
		return printPages;
	}

	private int getTotalRewardQuantity(List<PromoRedemption> promoRedemptions) {
		int total = 0;
		for (PromoRedemption promoRedemption : promoRedemptions) {
			total += promoRedemption.getTotalRewardQuantity();
		}
		return total;
	}

	private static int getTotalRewards(List<PromoRedemption> promoRedemptions) {
		int total = 0;
		for (PromoRedemption promoRedemption : promoRedemptions) {
			total += promoRedemption.getTotalRewards();
		}
		return total;
	}

	@Override
	public List<String> generateReportAsString(ReceivingReceipt receivingReceipt,
			boolean includeDiscountDetails) {
		receivingReceipt.setSupplier(supplierDao.get(receivingReceipt.getSupplier().getId()));
		receivingReceipt.setReceivedBy(userDao.get(receivingReceipt.getReceivedBy().getId()));
		
		String receivedDate = FormatterUtil.formatDate(receivingReceipt.getReceivedDate());
		
		List<List<ReceivingReceiptItem>> pageItems = Lists.partition(receivingReceipt.getItems(), 
				RECEIVING_RECEIPT_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("receivingReceipt", receivingReceipt);
			reportData.put("items", pageItems.get(i));
			reportData.put("receivedDate", receivedDate);
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			if (includeDiscountDetails) {
				reportData.put("reportUtil", ReceivingReceiptReportUtil.class);
				printPages.add(generateReportAsString("reports/receivingReceipt.vm", reportData));
			} else {
				printPages.add(
						generateReportAsString("reports/receivingReceipt-noDiscountDetails.vm", reportData));
			}
		}
		return printPages;
	}

	@Override
	public void print(StockQuantityConversion stockQuantityConversion) {
		stockQuantityConversion.setPrinted(true);
		stockQuantityConversionDao.save(stockQuantityConversion);
		
		try {
			for (String printPage : generateReportAsString(stockQuantityConversion)) {
				printerUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(StockQuantityConversion stockQuantityConversion) {
		Collections.sort(stockQuantityConversion.getItems());
		
		String currentDate = FormatterUtil.formatDate(systemDao.getCurrentDateTime());
		
		List<List<StockQuantityConversionItem>> pageItems = Lists.partition(stockQuantityConversion.getItems(), 
				STOCK_QUANTITY_CONVERSION_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("stockQuantityConversion", stockQuantityConversion);
			reportData.put("items", pageItems.get(i));
			reportData.put("currentDate", currentDate);
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/stockQuantityConversion.vm", reportData));
		}
		return printPages;
	}

	@Override
	public List<String> generateReportAsString(InventoryCheck inventoryCheck, InventoryCheckReportType reportType) {
		List<InventoryCheckSummaryItem> items = Collections.emptyList();
		String title = null;
		BigDecimal totalValue = null;
		
		switch (reportType) {
		case BEGINNING_INVENTORY:
			items = inventoryCheck.getSummaryItemsWithBeginningInventoriesOnly();
			title = "BEGINNING INVENTORY";
			totalValue = inventoryCheck.getTotalBeginningValue();
			break;
		case ACTUAL_COUNT:
			items = inventoryCheck.getSummaryItemsWithActualCountOnly();
			title = "ACTUAL COUNT";
			totalValue = inventoryCheck.getTotalActualValue();
			break;
		case COMPLETE:
			items = inventoryCheck.getNonEmptySummaryItems();
			title = "INVENTORY CHECK";
			totalValue = inventoryCheck.getTotalActualValue();
			break;
		}
		
		Collections.sort(items);
		
		String inventoryDate = FormatterUtil.formatDate(inventoryCheck.getInventoryDate());
		
		List<InventoryCheckSummaryPrintItem> printItems = new ArrayList<>();
		for (InventoryCheckSummaryItem item : items) {
			printItems.add(new InventoryCheckSummaryPrintItem(item, reportType));
		}
		
		List<List<InventoryCheckSummaryPrintItem>> pageItems = Lists.partition(printItems, 
				INVENTORY_CHECK_SUMMARY_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("inventoryCheck", inventoryCheck);
			reportData.put("reportType", title);
			reportData.put("items", pageItems.get(i));
			reportData.put("inventoryDate", inventoryDate);
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			reportData.put("totalValue", totalValue);
			if (reportType == InventoryCheckReportType.COMPLETE) {
				printPages.add(generateReportAsString("reports/inventoryCheckReport.vm", reportData));
			} else {
				printPages.add(generateReportAsString("reports/inventoryReport.vm", reportData));
			}
		}
		return printPages;
	}

	@Override
	public void print(InventoryCheck inventoryCheck, InventoryCheckReportType reportType) {
		try {
			for (String printPage : generateReportAsString(inventoryCheck, reportType)) {
				printerUtil.print(addLeftPaddingForCondensedFont(printPage));
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private String addLeftPaddingForCondensedFont(String printPage) {
		return LEFT_PADDING_FOR_CONDENSED_FONT +
				printPage.replaceAll("\n", "\n" + LEFT_PADDING_FOR_CONDENSED_FONT);
	}

	@Override
	public void printBirCashForm(SalesInvoice salesInvoice) {
		printBirForm(salesInvoice, true);
	}
	
	@Override
	public void printBirChargeForm(SalesInvoice salesInvoice) {
		printBirForm(salesInvoice, false);
	}
	
	private void printBirForm(SalesInvoice salesInvoice, boolean cashForm) {
		salesInvoice.setPrinted(true);
		salesInvoiceDao.save(salesInvoice);
		
		Collections.sort(salesInvoice.getItems());
		
		List<PromoRedemption> promoRedemptions = promoRedemptionService.findAllAvailedPromoRedemptions(salesInvoice);
		
		List<String> itemLines = new ArrayList<>();
		for (SalesInvoiceItem item : salesInvoice.getItems()) {
			itemLines.add(
					generateReportAsString("reports/salesInvoiceBirForm-item.vm", 
							createSingletonMap("item", item)));

			for (PromoRedemption promoRedemption : promoRedemptions) {
				for (PromoType2Rule rule : promoRedemption.getPromo().getPromoType2Rules()) {
					if (rule.getPromoProduct().equals(item.getProduct())) {
						for (PromoRedemptionReward reward : promoRedemption.getRewards()) {
							if (reward.getProduct().equals(rule.getFreeProduct())) {
								SalesInvoiceItem rewardItem = new SalesInvoiceItem();
								rewardItem.setProduct(reward.getProduct());
								rewardItem.setUnit(reward.getUnit());
								rewardItem.setQuantity(reward.getQuantity());
								rewardItem.setUnitPrice(BigDecimal.ZERO);
								itemLines.add(
										generateReportAsString("reports/salesInvoiceBirForm-freeItem.vm", 
												createSingletonMap("item", (Object)rewardItem)));
							}
						}
					}
				}
			}
		}
		
		List<List<String>> pageItems = Lists.partition(itemLines, SALES_INVOICE_BIR_FORM_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("salesInvoice", salesInvoice);
			reportData.put("items", StringUtils.join(pageItems.get(i), "\r\n"));
			reportData.put("fillerLines", createFillerLines(pageItems.get(i).size()));
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			if (cashForm) {
				printPages.add(generateReportAsString("reports/salesInvoiceBirForm-cash.vm", reportData));
			} else {
				printPages.add(generateReportAsString("reports/salesInvoiceBirForm-charge.vm", reportData));
			}
		}
		
		try {
			for (String printPage : printPages) {
				printerUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private List<String> createFillerLines(int referenceSize) {
		List<String> fillerLines = new ArrayList<>();
		int numberOfLines = SALES_INVOICE_BIR_FORM_ITEMS_PER_PAGE - referenceSize;
		for (int i = 0; i < numberOfLines; i++) {
			fillerLines.add("");
		}
		return fillerLines;
	}

	private static Map<String, Object> createSingletonMap(String key, Object value) {
		Map<String, Object> map = new HashMap<>();
		map.put(key, value);
		return map;
	}
	
	@Override
	public List<String> generateReportAsString(AreaInventoryReport areaInventoryReport) {
		String inventoryDate = FormatterUtil.formatDate(areaInventoryReport.getParent().getInventoryDate());
		
		List<List<AreaInventoryReportItem>> pageItems = Lists.partition(areaInventoryReport.getItems(), 
				AREA_INVENTORY_REPORT_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("areaInventoryReport", areaInventoryReport);
			if (areaInventoryReport.getArea() != null) {
				reportData.put("area", areaInventoryReport.getArea().getName());
			} else {
				reportData.put("area", "");
			}
			reportData.put("items", pageItems.get(i));
			reportData.put("inventoryDate", inventoryDate);
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/areaInventoryReport.vm", reportData));
		}
		return printPages;
	}

	@Override
	public void print(AreaInventoryReport areaInventoryReport) {
		try {
			for (String printPage : generateReportAsString(areaInventoryReport)) {
				printerUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(AdjustmentOut adjustmentOut) {
		Collections.sort(adjustmentOut.getItems());
		
		String currentDate = FormatterUtil.formatDate(systemDao.getCurrentDateTime());
		
		List<List<AdjustmentOutItem>> pageItems = Lists.partition(adjustmentOut.getItems(), 
				ADJUSTMENT_OUT_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("adjustmentOut", adjustmentOut);
			reportData.put("currentDate", currentDate);
			reportData.put("items", pageItems.get(i));
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/adjustmentOut.vm", reportData));
		}
		return printPages;
	}

	@Override
	public void print(AdjustmentOut adjustmentOut) {
		try {
			for (String printPage : generateReportAsString(adjustmentOut)) {
				printerUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(AdjustmentIn adjustmentIn) {
		Collections.sort(adjustmentIn.getItems());
		
		String currentDate = FormatterUtil.formatDate(systemDao.getCurrentDateTime());
		
		List<List<AdjustmentInItem>> pageItems = Lists.partition(adjustmentIn.getItems(), 
				ADJUSTMENT_IN_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("adjustmentIn", adjustmentIn);
			reportData.put("currentDate", currentDate);
			reportData.put("items", pageItems.get(i));
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/adjustmentIn.vm", reportData));
		}
		return printPages;
	}

	@Override
	public void print(AdjustmentIn adjustmentIn) {
		try {
			for (String printPage : generateReportAsString(adjustmentIn)) {
				printerUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(Payment payment) {
		List<String> printPages = new ArrayList<>();
		Map<String, Object> reportData = new HashMap<>();
		reportData.put("payment", payment);
		reportData.put("reportUtil", PaymentReportUtil.class);
		printPages.add(generateReportAsString("reports/payment.vm", reportData));
		return printPages;
	}

	@Override
	public void print(Payment payment) {
		try {
			for (String printPage : generateReportAsString(payment)) {
				printerUtil.printWithCondensedFont(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(UnpaidSalesInvoicesReport report) {
		String currentDate = FormatterUtil.formatDate(systemDao.getCurrentDateTime());
		
		List<List<PaymentSalesInvoice>> pageItems = Lists.partition(report.getSalesInvoices(), 
				UNPAID_SALES_INVOICE_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("salesInvoicesReport", report);
			reportData.put("charsPerLine", UNPAID_SALES_INVOICE_REPORT_CHARACTERS_PER_LINE);
			reportData.put("currentDate", currentDate);
			reportData.put("salesInvoices", pageItems.get(i));
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/unpaidSalesInvoices.vm", reportData));
		}
		return printPages;
	}

	@Override
	public void print(UnpaidSalesInvoicesReport report) {
		try {
			for (String printPage : generateReportAsString(report)) {
				printerUtil.printWithCondensedFont(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(PostedSalesAndProfitReport report) {
		List<List<PostedSalesAndProfitReportItem>> pageItems = Lists.partition(report.getItems(), 
				POSTED_SALES_AND_PROFIT_REPORT_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("charsPerLine", POSTED_SALES_AND_PROFIT_REPORT_CHARACTERS_PER_LINE);
			reportData.put("salesReport", report);
			reportData.put("items", pageItems.get(i));
			reportData.put("customer", report.getCustomerName());
			reportData.put("transactionDate", report.getTransactionDate());
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			reportData.put("reportUtil", PostedSalesAndProfitReportReportUtil.class);
			printPages.add(generateReportAsString("reports/postedSalesAndProfitReport.vm", reportData));
		}
		return printPages;
	}

	@Override
	public void print(PostedSalesAndProfitReport report) {
		try {
			for (String printPage : generateReportAsString(report)) {
				printerUtil.printWithCondensedFont(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(SalesReturn salesReturn) {
		String currentDate = FormatterUtil.formatDate(systemDao.getCurrentDateTime());
		
		List<List<SalesReturnItem>> pageItems = Lists.partition(salesReturn.getItems(), 
				SALES_RETURN_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("currentDate", currentDate);
			reportData.put("salesReturn", salesReturn);
			reportData.put("remarks", StringUtils.defaultString(salesReturn.getRemarks()));
			reportData.put("items", pageItems.get(i));
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/salesReturn.vm", reportData));
		}
		return printPages;
	}

	@Override
	public void print(SalesReturn salesReturn) {
		try {
			for (String printPage : generateReportAsString(salesReturn)) {
				printerUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(BadStockReturn badStockReturn) {
		String currentDate = FormatterUtil.formatDate(systemDao.getCurrentDateTime());
		
		List<List<BadStockReturnItem>> pageItems = Lists.partition(badStockReturn.getItems(), 
				BAD_STOCK_RETURN_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("currentDate", currentDate);
			reportData.put("badStockReturn", badStockReturn);
			reportData.put("remarks", StringUtils.defaultString(badStockReturn.getRemarks()));
			reportData.put("items", pageItems.get(i));
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/badStockReturn.vm", reportData));
		}
		return printPages;
	}

	@Override
	public void print(BadStockReturn badStockReturn) {
		try {
			for (String printPage : generateReportAsString(badStockReturn)) {
				printerUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void print(CashFlowReport report) {
		try {
			for (String printPage : generateReportAsString(report)) {
				printerUtil.printWithCondensedFont(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(CashFlowReport report) {
		List<List<CashFlowReportItem>> pageItems = Lists.partition(report.getItems(), 
				CASH_FLOW_REPORT_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("cashFlowReport", report);
			reportData.put("charsPerLine", CASH_FLOW_REPORT_CHARACTERS_PER_LINE);
			if (report.getPaymentTerminal() != null) {
				reportData.put("paymentTerminal", report.getPaymentTerminal().getName());
			} else {
				reportData.put("paymentTerminal", "ANY");
			}
			if (report.getTimePeriod() != null) {
				reportData.put("timePeriod", report.getTimePeriod().getDescription());
			} else {
				reportData.put("timePeriod", "WHOLE DAY");
			}
			reportData.put("items", pageItems.get(i));
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/cashFlowReport.vm", reportData));
		}
		return printPages;
	}

	@Override
	public List<String> generateReportAsString(RemittanceReport report) {
		List<List<PaymentCheckPayment>> pageItems = Lists.partition(report.getCheckPayments(), 
				REMITTANCE_REPORT_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("remittanceReport", report);
			reportData.put("charsPerLine", REMITTANCE_REPORT_CHARACTERS_PER_LINE);
			if (report.getPaymentTerminal() != null) {
				reportData.put("paymentTerminal", report.getPaymentTerminal().getName());
			} else {
				reportData.put("paymentTerminal", "ANY");
			}
			if (report.getTimePeriod() != null) {
				reportData.put("timePeriod", report.getTimePeriod().getDescription());
			} else {
				reportData.put("timePeriod", "WHOLE DAY");
			}
			reportData.put("items", pageItems.get(i));
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/remittanceReport.vm", reportData));
		}
		return printPages;
	}

	@Override
	public void print(RemittanceReport report) {
		try {
			for (String printPage : generateReportAsString(report)) {
				printerUtil.printWithCondensedFont(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(PriceChangesReport report) {
		Map<String, Object> reportData = new HashMap<>();
		reportData.put("priceChangesReport", report);
		reportData.put("itemGroups", report.getItemsGroupedByDate());
		reportData.put("charsPerLine", PRICE_CHANGES_REPORT_CHARACTERS_PER_LINE);
		
		String reportBody = generateReportAsString("reports/priceChangesReport-body.vm", reportData);
		List<String> pages = partitionText(reportBody, PRICE_CHANGES_REPORT_ITEMS_PER_PAGE);
		reportData.put("totalPages", pages.size());
		
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pages.size(); i++) {
			reportData.put("currentPage", i + 1);
			StringBuilder sb = new StringBuilder();
			sb.append(generateReportAsString("reports/priceChangesReport-header.vm", reportData));
			sb.append(pages.get(i));
			printPages.add(sb.toString());
		}
		return printPages;
	}

	private static List<String> partitionText(String text, int numberOfLines) {
		String[] lines = text.split("\n");
		List<String> group = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		int lineCount = 0;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			sb.append(line).append("\n");
			lineCount++;
			if (lineCount == numberOfLines) {
				group.add(sb.toString());
				sb = new StringBuilder();
				lineCount = 0;
			}
		}
		if (sb.length() > 0) {
			group.add(sb.toString());
		}
		return group;
	}
	
	@Override
	public void print(PriceChangesReport report) {
		try {
			for (String printPage : generateReportAsString(report)) {
				printerUtil.printWithCondensedFont(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(PurchasePayment purchasePayment) {
		Map<String, Object> reportData = new HashMap<>();
		reportData.put("payment", purchasePayment);
		reportData.put("currentDate", systemDao.getCurrentDateTime());
		reportData.put("newLine", "\n");
		return Arrays.asList(generateReportAsString("reports/purchasePayment.vm", reportData));
	}

	@Override
	public void print(PurchasePayment purchasePayment) {
		try {
			for (String printPage : generateReportAsString(purchasePayment)) {
				printerUtil.printWithCondensedFont(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(PurchaseReturnBadStock purchaseReturnBadStock) {
		String currentDate = FormatterUtil.formatDate(systemDao.getCurrentDateTime());
		
		List<List<PurchaseReturnBadStockItem>> pageItems = Lists.partition(purchaseReturnBadStock.getItems(), 
				PURCHASE_RETURN_BAD_STOCK_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("currentDate", currentDate);
			reportData.put("purchaseReturnBadStock", purchaseReturnBadStock);
			reportData.put("remarks", StringUtils.defaultString(purchaseReturnBadStock.getRemarks()));
			reportData.put("items", pageItems.get(i));
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/purchaseReturnBadStock.vm", reportData));
		}
		return printPages;
	}

	@Override
	public void print(PurchaseReturnBadStock purchaseReturnBadStock) {
		try {
			for (String printPage : generateReportAsString(purchaseReturnBadStock)) {
				printerUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(PromoRedemption promoRedemption) {
		Map<String, Object> reportData = new HashMap<>();
		reportData.put("promoRedemption", promoRedemption);
		
		return Arrays.asList(centerReport(generateReportAsString("reports/promoRedemption.vm", reportData)));
	}

	private static String centerReport(String reportString) {
		List<String> lines = Arrays.asList(reportString.split("\r\n"));
		final int maxColumns = getMaximumColumn(lines);
		List<String> paddedLines = Lists.transform(lines, new Function<String, String>() {

			@Override
			public String apply(String input) {
				if (!StringUtils.isEmpty(input)) {
					return ReportUtil.center(StringUtils.rightPad(input, maxColumns));
				} else {
					return input;
				}
			}
			
		});
		return StringUtils.join(paddedLines, "\n");
	}

	private static int getMaximumColumn(List<String> lines) {
		int maxColumns = 0;
		for (String line : lines) {
			if (line.length() > maxColumns) {
				maxColumns = line.length();
			}
		}
		return maxColumns;
	}

	@Override
	public void print(PromoRedemption promoRedemption) {
		try {
			for (String printPage : generateReportAsString(promoRedemption)) {
				printerUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(NoMoreStockAdjustment noMoreStockAdjustment) {
		String currentDate = FormatterUtil.formatDate(systemDao.getCurrentDateTime());
		
		List<List<NoMoreStockAdjustmentItem>> pageItems = Lists.partition(noMoreStockAdjustment.getItems(), 
				NO_MORE_STOCK_ADJUSTMENT_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("currentDate", currentDate);
			reportData.put("noMoreStockAdjustment", noMoreStockAdjustment);
			reportData.put("remarks", StringUtils.defaultString(noMoreStockAdjustment.getRemarks()));
			reportData.put("items", pageItems.get(i));
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/noMoreStockAdjustment.vm", reportData));
		}
		return printPages;
	}

	@Override
	public void print(NoMoreStockAdjustment noMoreStockAdjustment) {
		try {
			for (String printPage : generateReportAsString(noMoreStockAdjustment)) {
				printerUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
}