package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchasePaymentEcashPayment {

	private Long id;
	private PurchasePayment parent;
	private BigDecimal amount;
	private EcashReceiver ecashReceiver;
	private String referenceNumber;
	private Date paidDate;
	private User paidBy;

}