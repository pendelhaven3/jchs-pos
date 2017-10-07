package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.List;

import com.pj.magic.Constants;
import com.pj.magic.model.ReceivingReceipt;

public class UnpaidReceivingReceiptsReport {

	private List<ReceivingReceipt> receivingReceipts;

	public List<ReceivingReceipt> getReceivingReceipts() {
		return receivingReceipts;
	}

	public void setReceivingReceipts(List<ReceivingReceipt> receivingReceipts) {
		this.receivingReceipts = receivingReceipts;
	}
	
	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (ReceivingReceipt receivingReceipt : receivingReceipts) {
			total = total.add(receivingReceipt.getTotalNetAmountWithVat());
		}
		return total;
	}
	
}