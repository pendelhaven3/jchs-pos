package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchaseReturnBadStock;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.report.CashFlowReport;
import com.pj.magic.model.report.PostedSalesAndProfitReport;
import com.pj.magic.model.report.PriceChangesReport;
import com.pj.magic.model.report.RemittanceReport;
import com.pj.magic.model.report.UnpaidSalesInvoicesReport;
import com.pj.magic.model.util.InventoryCheckReportType;

public interface PrintService {

	void print(SalesInvoice salesInvoice);

	void print(PurchaseOrder purchaseOrder, boolean includeCost);
	
	void print(ReceivingReceipt receivingReceipt, boolean includeDiscountDetails);
	
	void print(StockQuantityConversion stockQuantityConversion);

	List<String> generateReportAsString(PurchaseOrder purchaseOrder, boolean includeCosts);
	
	void print(PricingScheme pricingScheme, List<Product> products, boolean includeCosts);
	
	List<String> generateReportAsString(PricingScheme pricingScheme, List<Product> products, boolean includeCost);
	
	void print(List<String> printPages);

	void printWithCondensedFont(List<String> printPages);

	List<String> generateReportAsString(SalesInvoice salesInvoice);

	List<String> generateReportAsString(ReceivingReceipt receivingReceipt, boolean includeDiscountDetails);

	List<String> generateReportAsString(StockQuantityConversion stockQuantityConversion);

	List<String> generateReportAsString(InventoryCheck inventoryCheck, InventoryCheckReportType reportType);

	void print(InventoryCheck inventoryCheck, InventoryCheckReportType reportType);
	
	void printBirCashForm(SalesInvoice salesInvoice);
	
	void printBirChargeForm(SalesInvoice salesInvoice);
	
	List<String> generateReportAsString(AreaInventoryReport areaInventoryReport);

	void print(AreaInventoryReport areaInventoryReport);

	List<String> generateReportAsString(AdjustmentOut adjustmentOut);

	void print(AdjustmentOut adjustmentOut);

	List<String> generateReportAsString(AdjustmentIn adjustmentIn);

	void print(AdjustmentIn adjustmentIn);

	List<String> generateReportAsString(Payment payment);

	void print(Payment payment);

	List<String> generateReportAsString(UnpaidSalesInvoicesReport report);

	void print(UnpaidSalesInvoicesReport report);

	List<String> generateReportAsString(PostedSalesAndProfitReport report);

	void print(PostedSalesAndProfitReport createReport);

	List<String> generateReportAsString(SalesReturn salesReturn);

	void print(SalesReturn salesReturn);
	
	List<String> generateReportAsString(BadStockReturn badStockReturn);

	void print(BadStockReturn badStockReturn);
	
	void print(CashFlowReport report);

	List<String> generateReportAsString(CashFlowReport report);

	List<String> generateReportAsString(RemittanceReport report);

	void print(RemittanceReport createRemittanceReport);
	
	List<String> generateReportAsString(PriceChangesReport report);

	void print(PriceChangesReport report);

	List<String> generateReportAsString(PurchasePayment purchasePayment);

	void print(PurchasePayment purchasePayment);

	List<String> generateReportAsString(
			PurchaseReturnBadStock purchaseReturnBadStock);

	void print(PurchaseReturnBadStock purchaseReturnBadStock);

	List<String> generateReportAsString(PromoRedemption promoRedemption);

	void print(PromoRedemption promoRedemption);

	List<String> generateReportAsString(NoMoreStockAdjustment noMoreStockAdjustment);

	void print(NoMoreStockAdjustment noMoreStockAdjustment);
	
}