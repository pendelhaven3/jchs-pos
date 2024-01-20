package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pj.magic.model.EcashReceiver;
import com.pj.magic.model.EcashType;
import com.pj.magic.model.PurchasePaymentEcashPayment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcashPurchasePaymentsReport {

	private EcashReceiver ecashReceiver;
	private Date dateFrom;
	private Date dateTo;
	private EcashType ecashType;
	private List<PurchasePaymentEcashPayment> payments;
	
	public BigDecimal getTotalAmount() {
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (PurchasePaymentEcashPayment payment : payments) {
			totalAmount = totalAmount.add(payment.getAmount());
		}
		return totalAmount.setScale(2);
	}
	
}
