package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

/*
 * Wrapper class to separate table gui concerns of inputting Receiving Receipt Items
 * from the business logic of Receiving Receipt Item model.
 */
public class ReceivingReceiptItemRowItem {

	private ReceivingReceiptItem item;
	private String discount1;
	private String discount2;
	private String discount3;
	private String flatRateDiscount;

	public ReceivingReceiptItemRowItem(ReceivingReceiptItem item) {
		this.item = item;
		discount1 = item.getDiscount1().toString();
		discount2 = item.getDiscount2().toString();
		discount3 = item.getDiscount3().toString();
		flatRateDiscount = FormatterUtil.formatAmount(item.getFlatRateDiscount());
	}
	
	public ReceivingReceiptItem getItem() {
		return item;
	}

	public void setItem(ReceivingReceiptItem item) {
		this.item = item;
	}

	public boolean isValid() {
		return (NumberUtil.isAmount(discount1) || StringUtils.isEmpty(discount1))
				&& (NumberUtil.isAmount(discount2) || StringUtils.isEmpty(discount2))
				&& (NumberUtil.isAmount(discount3) || StringUtils.isEmpty(discount3))
				&& (NumberUtil.isAmount(flatRateDiscount) || StringUtils.isEmpty(flatRateDiscount));
	}

	public String getDiscount1() {
		return discount1;
	}
	
	public BigDecimal getDiscount1AsBigDecimal() {
		if (StringUtils.isEmpty(discount1)) {
			discount1 = BigDecimal.ZERO.setScale(2).toString();
		}
		return NumberUtil.toBigDecimal(discount1);
	}

	public void setDiscount1(String discount1) {
		this.discount1 = discount1;
	}

	public String getDiscount2() {
		return discount2;
	}

	public void setDiscount2(String discount2) {
		this.discount2 = discount2;
	}

	public String getDiscount3() {
		return discount3;
	}

	public void setDiscount3(String discount3) {
		this.discount3 = discount3;
	}

	public String getFlatRateDiscount() {
		return flatRateDiscount;
	}

	public void setFlatRateDiscount(String flatRateDiscount) {
		this.flatRateDiscount = flatRateDiscount;
	}

	public BigDecimal getDiscount2AsBigDecimal() {
		if (StringUtils.isEmpty(discount2)) {
			discount2 = BigDecimal.ZERO.setScale(2).toString();
		}
		return NumberUtil.toBigDecimal(discount2);
	}

	public BigDecimal getDiscount3AsBigDecimal() {
		if (StringUtils.isEmpty(discount3)) {
			discount3 = BigDecimal.ZERO.setScale(2).toString();
		}
		return NumberUtil.toBigDecimal(discount3);
	}

	public BigDecimal getFlatRateDiscountAsBigDecimal() {
		if (StringUtils.isEmpty(flatRateDiscount)) {
			flatRateDiscount = BigDecimal.ZERO.setScale(2).toString();
		}
		return NumberUtil.toBigDecimal(flatRateDiscount);
	}

}
