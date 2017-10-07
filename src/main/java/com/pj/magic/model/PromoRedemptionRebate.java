package com.pj.magic.model;

public class PromoRedemptionRebate {

	private Long id;
	private PromoRedemption promoRedemption;
	private PaymentAdjustment paymentAdjustment;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PromoRedemption getPromoRedemption() {
		return promoRedemption;
	}

	public void setPromoRedemption(PromoRedemption promoRedemption) {
		this.promoRedemption = promoRedemption;
	}

	public PaymentAdjustment getPaymentAdjustment() {
		return paymentAdjustment;
	}

	public void setPaymentAdjustment(PaymentAdjustment paymentAdjustment) {
		this.paymentAdjustment = paymentAdjustment;
	}

}
