package com.pj.magic.model;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.pj.magic.Constants;
import com.pj.magic.util.FormatterUtil;

public class PromoType5Rule {

	private Long id;
	private Promo parent;
	private BigDecimal targetAmount;
	private BigDecimal rebate;
	private List<PromoType5RulePromoProduct> promoProducts = new ArrayList<>();

	public PromoType5Rule() {
	}
	
	public PromoType5Rule(long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Promo getParent() {
		return parent;
	}

	public void setParent(Promo parent) {
		this.parent = parent;
	}

	public BigDecimal getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(BigDecimal targetAmount) {
		this.targetAmount = targetAmount;
	}

	public List<PromoType5RulePromoProduct> getPromoProducts() {
		return promoProducts;
	}

	public void setPromoProducts(List<PromoType5RulePromoProduct> promoProducts) {
		this.promoProducts = promoProducts;
	}

	public boolean isNew() {
		return id == null;
	}
	
	public boolean hasPromoProduct(Product product) {
		for (PromoType5RulePromoProduct promoProduct : promoProducts) {
			if (promoProduct.getProduct().equals(product)) {
				return true;
			}
		}
		return false;
	}

	public BigDecimal getRebate() {
		return rebate;
	}

	public void setRebate(BigDecimal rebate) {
		this.rebate = rebate;
	}

	public BigDecimal getQualifyingAmount(SalesInvoice salesInvoice) {
		BigDecimal total = Constants.ZERO;
		
		for (PromoType5RulePromoProduct promoProduct : promoProducts) {
			for (SalesInvoiceItem item : salesInvoice.getItems()) {
				if (item.getProduct().getId().equals(promoProduct.getProduct().getId())) {
					int originalQuantity = item.getQuantity();
					for (SalesReturn salesReturn : salesInvoice.getSalesReturns()) {
						for (SalesReturnItem salesReturnItem : salesReturn.getItems()) {
							if (salesReturnItem.getSalesInvoiceItem().getProduct().getId().equals(item.getProduct().getId())) {
								item.setQuantity(item.getQuantity() - salesReturnItem.getQuantity());
							}
						}
					}
					for (NoMoreStockAdjustment nms : salesInvoice.getNoMoreStockAdjustments()) {
						for (NoMoreStockAdjustmentItem nmsItem : nms.getItems()) {
							if (nmsItem.getSalesInvoiceItem().getProduct().getId().equals(item.getProduct().getId())) {
								item.setQuantity(item.getQuantity() - nmsItem.getQuantity());
							}
						}
					}
					total = total.add(item.getAmount());
					item.setQuantity(originalQuantity);
				}
			}
		}
		return total;
	}

	public BigDecimal getQualifyingAmount(List<SalesInvoice> salesInvoices) {
		BigDecimal total = Constants.ZERO;
		for (SalesInvoice salesInvoice : salesInvoices) {
			total = total.add(getQualifyingAmount(salesInvoice));
		}
		return total;
	}
	
	public String getMechanicsDescription() {
		String message = "Buy {0} amount of selected products, get {1} rebate";
		return MessageFormat.format(message, 
				FormatterUtil.formatAmount(targetAmount),
				FormatterUtil.formatAmount(rebate));
	}

	/**
	 * Return corresponding rebate based on the passed sales invoices
	 * 
	 * @param salesInvoices
	 * @return
	 */
	public BigDecimal evaluate(List<SalesInvoice> salesInvoices) {
		return getQualifyingAmount(salesInvoices)
				.divideToIntegralValue(targetAmount)
				.multiply(rebate)
				.setScale(2);
	}
	
}