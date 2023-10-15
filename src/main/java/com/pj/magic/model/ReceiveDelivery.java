package com.pj.magic.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiveDelivery {

	private Long id;
	private Supplier supplier;
	private List<ReceiveDeliveryItem> items = new ArrayList<>();
	private boolean posted;
//	private PaymentTerm paymentTerm;
//	private String remarks;
	private String referenceNumber;
	private Date receiveDate;
	private User receivedBy;
	private Date postDate;
	private User postedBy;
//	private boolean vatInclusive;
//	private BigDecimal vatRate;

	public ReceiveDelivery() {
		// default constructor
	}
	
	public ReceiveDelivery(Long id) {
		this.id = id;
	}

}