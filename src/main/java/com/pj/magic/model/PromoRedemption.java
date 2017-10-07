package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.pj.magic.Constants;
import com.pj.magic.exception.SalesInvoiceIneligibleForPromoRedemptionException;

public class PromoRedemption {

	private Long id;
	private Promo promo;
	private Long promoRedemptionNumber;
	private Customer customer;
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private List<PromoRedemptionSalesInvoice> redemptionSalesInvoices = new ArrayList<>();
	private List<PromoRedemptionReward> rewards = new ArrayList<>();
	private List<PromoRedemptionRebate> rebates = new ArrayList<>();
	private boolean cancelled;
	private Date cancelDate;
	private User cancelledBy;

	public PromoRedemption() {
		// default constructor
	}

	public PromoRedemption(long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Promo getPromo() {
		return promo;
	}

	public void setPromo(Promo promo) {
		this.promo = promo;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Long getPromoRedemptionNumber() {
		return promoRedemptionNumber;
	}

	public void setPromoRedemptionNumber(Long promoRedemptionNumber) {
		this.promoRedemptionNumber = promoRedemptionNumber;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public User getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(User postedBy) {
		this.postedBy = postedBy;
	}

	public List<PromoRedemptionSalesInvoice> getRedemptionSalesInvoices() {
		return redemptionSalesInvoices;
	}

	public void setRedemptionSalesInvoices(List<PromoRedemptionSalesInvoice> redemptionSalesInvoices) {
		this.redemptionSalesInvoices = redemptionSalesInvoices;
	}

	public List<SalesInvoice> getSalesInvoices() {
		return new ArrayList<>(Collections2.transform(redemptionSalesInvoices,
				new Function<PromoRedemptionSalesInvoice, SalesInvoice>() {

					@Override
					public SalesInvoice apply(PromoRedemptionSalesInvoice input) {
						return input.getSalesInvoice();
					}
				}));
	}

	public String getStatus() {
		if (cancelled) {
			return "Cancelled";
		} else if (posted) {
			return "Posted";
		} else {
			return "New";
		}
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;

		switch (promo.getPromoType()) {
		case PROMO_TYPE_1: {
			PromoType1Rule rule = promo.getPromoType1Rule();
			for (PromoRedemptionSalesInvoice salesInvoice : redemptionSalesInvoices) {
				total = total.add(salesInvoice.getSalesInvoice().getSalesByManufacturer(rule.getManufacturer()));
			}
			break;
		}
		case PROMO_TYPE_2:
			break;
		case PROMO_TYPE_3:
			PromoType3Rule type3Rule = promo.getPromoType3Rule();
			for (PromoRedemptionSalesInvoice salesInvoice : redemptionSalesInvoices) {
				total = total.add(type3Rule.getQualifyingAmount(salesInvoice.getSalesInvoice()));
			}
			break;
		case PROMO_TYPE_4:
			break;
		case PROMO_TYPE_5:
			total = getPromoType5TotalAmount();
			break;
		}

		return total;
	}

	private BigDecimal getPromoType5TotalAmount() {
		PromoType5Rule rule = promo.getPromoType5Rule();

		BigDecimal total = BigDecimal.ZERO;
		for (PromoRedemptionSalesInvoice salesInvoice : redemptionSalesInvoices) {
			total = total.add(rule.getQualifyingAmount(salesInvoice.getSalesInvoice()));
		}
		return total;
	}

	public int getFreeQuantity(PromoType2Rule rule) {
		return getTotalQuantityByProductAndUnit(rule.getPromoProduct(), rule.getPromoUnit()) / rule.getPromoQuantity()
				* rule.getFreeQuantity();
	}

	public int getTotalQuantityByProductAndUnit(Product product, String unit) {
		int total = 0;
		for (PromoRedemptionSalesInvoice salesInvoice : redemptionSalesInvoices) {
			SalesInvoiceItem item = salesInvoice.getSalesInvoice().findItemByProductAndUnit(product, unit);
			if (item != null) {
				total += item.getQuantity();
			}
		}
		return total;
	}

	public List<PromoRedemptionReward> getRewards() {
		return rewards;
	}

	public void setRewards(List<PromoRedemptionReward> rewards) {
		this.rewards = rewards;
	}

	public PromoRedemptionReward getRewardByRule(PromoType2Rule rule) {
		for (PromoRedemptionReward reward : rewards) {
			if (reward.getProduct().equals(rule.getFreeProduct()) && reward.getUnit().equals(rule.getFreeUnit())) {
				return reward;
			}
		}
		return null;
	}

	public PromoRedemptionReward getFreeQuantity(PromoType3Rule rule) {
		List<SalesInvoice> salesInvoices = new ArrayList<>(Collections2.transform(this.redemptionSalesInvoices,
				new Function<PromoRedemptionSalesInvoice, SalesInvoice>() {

					@Override
					public SalesInvoice apply(PromoRedemptionSalesInvoice input) {
						return input.getSalesInvoice();
					}
				}));

		return rule.evaluate(salesInvoices);
	}

	public int getTotalRewards() {
		return rewards.size();
	}

	public int getTotalRewardQuantity() {
		int total = 0;
		for (PromoRedemptionReward reward : rewards) {
			total += reward.getQuantity();
		}
		return total;
	}

	public void validateSalesInvoicesPricingScheme() throws SalesInvoiceIneligibleForPromoRedemptionException {
		PricingScheme promoPricingScheme = getPromo().getPricingScheme();
		if (promoPricingScheme != null) {
			for (PromoRedemptionSalesInvoice redemptionSalesInvoice : redemptionSalesInvoices) {
				if (!redemptionSalesInvoice.getSalesInvoice().getPricingScheme().equals(promoPricingScheme)) {
					throw new SalesInvoiceIneligibleForPromoRedemptionException(
							redemptionSalesInvoice.getSalesInvoice());
				}
			}
		}
	}

	public List<PromoRedemptionRebate> getRebates() {
		return rebates;
	}

	public void setRebates(List<PromoRedemptionRebate> rebates) {
		this.rebates = rebates;
	}

	public BigDecimal getTotalRebates() {
		if (posted) {
			BigDecimal total = BigDecimal.ZERO;
			for (PromoRedemptionRebate rebate : rebates) {
				total = total.add(rebate.getPaymentAdjustment().getAmount());
			}
			return total;
		} else {
			PromoType5Rule rule = getPromo().getPromoType5Rule();
			return getPromoType5TotalAmount().divideToIntegralValue(rule.getTargetAmount()).multiply(rule.getRebate())
					.setScale(2);
		}
	}

	public BigDecimal getRewardsTotalCost() {
		return rewards.stream()
				.map(reward -> reward.getTotalCost())
				.reduce(BigDecimal.ZERO, (x,y) -> x.add(y));
	}
	
	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public User getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(User cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

}