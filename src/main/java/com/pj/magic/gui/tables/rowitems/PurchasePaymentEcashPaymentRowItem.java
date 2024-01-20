package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.pj.magic.model.EcashReceiver;
import com.pj.magic.model.PurchasePaymentEcashPayment;
import com.pj.magic.model.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchasePaymentEcashPaymentRowItem {

	private PurchasePaymentEcashPayment ecashPayment;
	private BigDecimal amount;
	private EcashReceiver ecashReceiver;
	private String referenceNumber;
	private Date paidDate;
	private User paidBy;

	public PurchasePaymentEcashPaymentRowItem(PurchasePaymentEcashPayment ecashPayment) {
		this.ecashPayment = ecashPayment;
		amount = ecashPayment.getAmount();
		ecashReceiver = ecashPayment.getEcashReceiver();
		referenceNumber = ecashPayment.getReferenceNumber();
		paidDate = ecashPayment.getPaidDate();
		paidBy = ecashPayment.getPaidBy();
	}
	
	public void reset() {
		amount = ecashPayment.getAmount();
		ecashReceiver = ecashPayment.getEcashReceiver();
		referenceNumber = ecashPayment.getReferenceNumber();
		paidDate = ecashPayment.getPaidDate();
		paidBy = ecashPayment.getPaidBy();
	}

	public boolean isValid() {
		return amount != null && ecashReceiver != null && !StringUtils.isEmpty(referenceNumber)
				&& paidDate != null && paidBy != null;
	}

	public boolean isUpdating() {
		return ecashPayment.getId() != null;
	}

}