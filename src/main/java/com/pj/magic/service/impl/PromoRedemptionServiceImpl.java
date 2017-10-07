package com.pj.magic.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.AdjustmentTypeDao;
import com.pj.magic.dao.PaymentAdjustmentDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.PromoPointsClaimDao;
import com.pj.magic.dao.PromoRedemptionDao;
import com.pj.magic.dao.PromoRedemptionRebateDao;
import com.pj.magic.dao.PromoRedemptionRewardDao;
import com.pj.magic.dao.PromoRedemptionSalesInvoiceDao;
import com.pj.magic.dao.SalesInvoiceItemDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.NotEnoughPromoPointsException;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.exception.NothingToRedeemException;
import com.pj.magic.model.AvailedPromoPointsItem;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.Product;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoPointsClaim;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionRebate;
import com.pj.magic.model.PromoRedemptionReward;
import com.pj.magic.model.PromoRedemptionSalesInvoice;
import com.pj.magic.model.PromoType;
import com.pj.magic.model.PromoType1Rule;
import com.pj.magic.model.PromoType2Rule;
import com.pj.magic.model.PromoType3Rule;
import com.pj.magic.model.PromoType5Rule;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.search.PromoRedemptionSearchCriteria;
import com.pj.magic.model.search.PromoSearchCriteria;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.NoMoreStockAdjustmentService;
import com.pj.magic.service.PromoRedemptionService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.service.SalesReturnService;

@Service
public class PromoRedemptionServiceImpl implements PromoRedemptionService {

	@Autowired private PromoRedemptionDao promoRedemptionDao;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private PromoRedemptionSalesInvoiceDao promoRedemptionSalesInvoiceDao;
	@Autowired private SalesInvoiceItemDao salesInvoiceItemDao;
	@Autowired private LoginService loginService;
	@Autowired private PromoService promoService;
	@Autowired private ProductDao productDao;
	@Autowired private PromoRedemptionRewardDao promoRedemptionRewardDao;
	@Autowired private PromoRedemptionRebateDao promoRedemptionRebateDao;
	@Autowired private PromoPointsClaimDao promoPointsClaimDao;
	@Autowired private AdjustmentTypeDao adjustmentTypeDao;
	@Autowired private PaymentAdjustmentDao paymentAdjustmentDao;
	@Autowired private SystemDao systemDao;
	@Autowired private SalesReturnService salesReturnService;
	@Autowired private NoMoreStockAdjustmentService noMoreStockAdjustmentService;
	
	@Transactional
	@Override
	public void save(PromoRedemption promoRedemption) {
		boolean isNew = (promoRedemption.getId() == null);
		promoRedemptionDao.save(promoRedemption);
		if (!isNew) {
			promoRedemptionSalesInvoiceDao.deleteAllByPromoRedemption(promoRedemption);
		}
	}

	@Override
	public List<SalesInvoice> findAllUnreedemedSalesInvoices(Promo promo, Customer customer) {
		SalesInvoiceSearchCriteria criteria = new SalesInvoiceSearchCriteria();
		criteria.setUnredeemedPromo(promo);
		criteria.setCustomer(customer);
		criteria.setTransactionDateFrom(promo.getStartDate());
		criteria.setTransactionDateTo(promo.getEndDate());
		criteria.setPricingScheme(promo.getPricingScheme());
		
		return salesInvoiceService.search(criteria);
	}

	@Transactional
	@Override
	public void save(PromoRedemptionSalesInvoice salesInvoice) {
		promoRedemptionSalesInvoiceDao.save(salesInvoice);
	}

	@Override
	public PromoRedemption getPromoRedemption(long id) {
		PromoRedemption promoRedemption = promoRedemptionDao.get(id);
		promoRedemption.setPromo(promoService.getPromo(promoRedemption.getPromo().getId()));
		
		promoRedemption.setRedemptionSalesInvoices(promoRedemptionSalesInvoiceDao
				.findAllByPromoRedemption(promoRedemption));
		for (PromoRedemptionSalesInvoice promoRedemptionSalesInvoice : promoRedemption.getRedemptionSalesInvoices()) {
			SalesInvoice salesInvoice = promoRedemptionSalesInvoice.getSalesInvoice();
			salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
			salesInvoice.setSalesReturns(salesReturnService.findPostedSalesReturnsBySalesInvoice(salesInvoice));
			salesInvoice.setNoMoreStockAdjustments(
					noMoreStockAdjustmentService.findPostedNoMoreStockAdjustmentsBySalesInvoice(salesInvoice));
		}
		
		if (promoRedemption.isPosted()) {
			if (promoRedemption.getPromo().isPromoType5()) {
				promoRedemption.setRebates(promoRedemptionRebateDao.findAllByPromoRedemption(promoRedemption));
			} else {
				promoRedemption.setRewards(promoRedemptionRewardDao.findAllByPromoRedemption(promoRedemption));
			}
		}
		
		return promoRedemption;
	}

	@Transactional
	@Override
	public void delete(PromoRedemptionSalesInvoice salesInvoice) {
		promoRedemptionSalesInvoiceDao.delete(salesInvoice);
	}

	@Transactional
	@Override
	public void post(PromoRedemption promoRedemption) {
		PromoRedemption updated = getPromoRedemption(promoRedemption.getId());
		
		if (updated.isPosted()) {
			throw new AlreadyPostedException();
		}
		
		switch (updated.getPromo().getPromoType()) {
		case PROMO_TYPE_1:
			postForPromoType1(updated);
			break;
		case PROMO_TYPE_2:
			postForPromoType2(updated);
			break;
		case PROMO_TYPE_3:
			postForPromoType3(updated);
			break;
		case PROMO_TYPE_4:
			break;
		case PROMO_TYPE_5:
			postForPromoType5(updated);
			break;
		}
		
		updated.setPosted(true);
		updated.setPostDate(systemDao.getCurrentDateTime());
		updated.setPostedBy(loginService.getLoggedInUser());
		promoRedemptionDao.save(updated);
	}

	private void postForPromoType3(PromoRedemption promoRedemption) {
		promoRedemption.validateSalesInvoicesPricingScheme();
		
		PromoType3Rule rule = promoRedemption.getPromo().getPromoType3Rule();
		
		PromoRedemptionReward reward = rule.evaluate(promoRedemption.getSalesInvoices());
		if (reward != null) {
			int freeQuantity = reward.getQuantity().intValue();
			
			if (rule.getDailyRedeemLimitPerCustomer() > 0) {
				int redeemed = getNumberOfRedemptionsToday(promoRedemption.getPromo(), promoRedemption.getCustomer());
				if (freeQuantity > (rule.getDailyRedeemLimitPerCustomer() - redeemed)) {
					freeQuantity = (rule.getDailyRedeemLimitPerCustomer() - redeemed);
				}
			}
			
			Product product = productDao.get(rule.getFreeProduct().getId());
			
			if (product.getUnitQuantity(rule.getFreeUnit()) < freeQuantity) {
				throw new NotEnoughStocksException();
			}
			
			product.addUnitQuantity(rule.getFreeUnit(), -1 * freeQuantity);
			productDao.updateAvailableQuantities(product);
			
			reward.setParent(promoRedemption);
			reward.setProduct(rule.getFreeProduct());
			reward.setUnit(rule.getFreeUnit());
			reward.setQuantity(freeQuantity);
			promoRedemptionRewardDao.save(reward);
		}
	}

	private void postForPromoType2(PromoRedemption promoRedemption) {
		for (PromoType2Rule rule : promoRedemption.getPromo().getPromoType2Rules()) {
			int freeQuantity = promoRedemption.getFreeQuantity(rule);
			if (freeQuantity > 0) {
				Product product = productDao.get(rule.getFreeProduct().getId());
				
				if (product.getUnitQuantity(rule.getFreeUnit()) < freeQuantity) {
					throw new NotEnoughStocksException();
				}
				
				product.addUnitQuantity(rule.getFreeUnit(), -1 * freeQuantity);
				productDao.updateAvailableQuantities(product);
				
				PromoRedemptionReward reward = new PromoRedemptionReward();
				reward.setParent(promoRedemption);
				reward.setProduct(rule.getFreeProduct());
				reward.setUnit(rule.getFreeUnit());
				reward.setQuantity(freeQuantity);
				promoRedemptionRewardDao.save(reward);
			}
		}
	}

	private void postForPromoType1(PromoRedemption promoRedemption) {
		promoRedemption.validateSalesInvoicesPricingScheme();
		
		PromoType1Rule rule = promoRedemption.getPromo().getPromoType1Rule();
		PromoRedemptionReward reward = rule.evaluate(promoRedemption.getSalesInvoices());
		if (reward == null) {
			throw new NothingToRedeemException();
		}
		
		if (rule.getDailyRedeemLimitPerCustomer() > 0) {
			int redeemed = getNumberOfRedemptionsToday(promoRedemption.getPromo(), promoRedemption.getCustomer());
			if (reward.getQuantity() > (rule.getDailyRedeemLimitPerCustomer() - redeemed)) {
				reward.setQuantity(rule.getDailyRedeemLimitPerCustomer() - redeemed);
			}
		}
		
		Product product = productDao.get(rule.getProduct().getId());
		if (product.getUnitQuantity(rule.getUnit()) < rule.getQuantity().intValue()) {
			throw new NotEnoughStocksException();
		}
		
		product.addUnitQuantity(rule.getUnit(), -1 * reward.getQuantity().intValue());
		productDao.updateAvailableQuantities(product);
		
		reward.setParent(promoRedemption);
		promoRedemptionRewardDao.save(reward);
	}

	private void postForPromoType5(PromoRedemption promoRedemption) {
		promoRedemption.validateSalesInvoicesPricingScheme();
		
		PromoType5Rule rule = promoRedemption.getPromo().getPromoType5Rule();
		BigDecimal rebate = rule.evaluate(promoRedemption.getSalesInvoices());
		
		if (rebate.compareTo(BigDecimal.ZERO) == 0) {
			throw new NothingToRedeemException();
		}
		
		PaymentAdjustment paymentAdjustment = new PaymentAdjustment();
		paymentAdjustment.setCustomer(promoRedemption.getCustomer());
		paymentAdjustment.setAdjustmentType(adjustmentTypeDao.findByCode("CM"));
		paymentAdjustment.setAmount(rebate);
		paymentAdjustment.setRemarks("PROMO REDEMPTION NO. " + promoRedemption.getPromoRedemptionNumber());
		paymentAdjustment.setPosted(true);
		paymentAdjustment.setPostedBy(loginService.getLoggedInUser());
		paymentAdjustment.setPostDate(systemDao.getCurrentDateTime());
		paymentAdjustmentDao.save(paymentAdjustment);
		
		PromoRedemptionRebate promoRedemptionRebate = new PromoRedemptionRebate();
		promoRedemptionRebate.setPromoRedemption(promoRedemption);
		promoRedemptionRebate.setPaymentAdjustment(paymentAdjustment);
		promoRedemptionRebateDao.save(promoRedemptionRebate);
	}
	
	@Override
	public List<PromoRedemption> getPromoRedemptionsByPromo(Promo promo) {
		return promoRedemptionDao.findAllByPromo(promo);
	}

	@Override
	public List<PromoRedemption> findAllBySalesInvoice(SalesInvoice salesInvoice) {
		List<PromoRedemption> promoRedemptions = promoRedemptionDao.findAllBySalesInvoice(salesInvoice);
		for (PromoRedemption promoRedemption : promoRedemptions) {
			promoRedemption.setRewards(promoRedemptionRewardDao.findAllByPromoRedemption(promoRedemption));
		}
		return promoRedemptions;
	}

	@Override
	public List<PromoRedemption> search(PromoRedemptionSearchCriteria criteria) {
		List<PromoRedemption> promoRedemptions = promoRedemptionDao.search(criteria);
		for (PromoRedemption promoRedemption : promoRedemptions) {
			promoRedemption.setRewards(promoRedemptionRewardDao.findAllByPromoRedemption(promoRedemption));
		}
		return promoRedemptions;
	}

	@Override
	public List<PromoRedemption> findAllAvailedPromoRedemptions(SalesInvoice salesInvoice) {
		PromoRedemptionSearchCriteria criteria = new PromoRedemptionSearchCriteria();
		criteria.setSalesInvoice(salesInvoice);
		criteria.setPosted(true);
		criteria.setPromoType(PromoType.PROMO_TYPE_2);
		
		List<PromoRedemption> promoRedemptions = search(criteria);
		for (PromoRedemption promoRedemption : promoRedemptions) {
			promoRedemption.setPromo(promoService.getPromo(promoRedemption.getPromo().getId()));
		}
		
		return promoRedemptions;
	}

	@Override
	public List<AvailedPromoPointsItem> findAllAvailedPromoPoints(SalesInvoice salesInvoice) {
		List<AvailedPromoPointsItem> items = new ArrayList<>();
		
		PromoSearchCriteria criteria = new PromoSearchCriteria();
		criteria.setPromoType(PromoType.PROMO_TYPE_4);
		criteria.setPromoDate(salesInvoice.getTransactionDate());
		criteria.setAcceptedPricingScheme(salesInvoice.getPricingScheme());
		criteria.setActive(true);

		List<Promo> promos = promoService.search(criteria);
		if (!promos.isEmpty()) {
			List<SalesReturn> salesReturns = salesReturnService.findPostedSalesReturnsBySalesInvoice(salesInvoice);
			for (Promo promo : promos) {
				AvailedPromoPointsItem item = promo.evaluateForPoints(salesInvoice, salesReturns);
				if (item.hasPoints()) {
					items.add(item);
				}
			}
		}
		
		return items;
	}

	@Override
	public List<PromoPointsClaim> findAllPromoPointsClaimByPromoAndCustomer(Promo promo, Customer customer) {
		return promoPointsClaimDao.findAllByPromoAndCustomer(promo, customer);
	}

	@Override
	public PromoPointsClaim getPromoPointsClaim(long id) {
		return promoPointsClaimDao.get(id);
	}

	@Transactional
	@Override
	public void save(PromoPointsClaim claim) throws NotEnoughPromoPointsException {
		if (claim.isNew()) {
			int availablePoints = getAvailablePromoPoints(claim.getPromo(), claim.getCustomer());
			if (availablePoints < claim.getPoints()) {
				throw new NotEnoughPromoPointsException(claim.getPoints(), availablePoints);
			}
			claim.setClaimDate(systemDao.getCurrentDateTime());
			claim.setClaimBy(loginService.getLoggedInUser());
		} else {
			int availablePoints = getAvailablePromoPoints(claim.getPromo(), claim.getCustomer());
			int previousClaimPoints = getPromoPointsClaim(claim.getId()).getPoints();
			int adjustedAvailablePoints = availablePoints + previousClaimPoints;
			if (adjustedAvailablePoints < claim.getPoints()) {
				throw new NotEnoughPromoPointsException(claim.getPoints(), adjustedAvailablePoints);
			}
		}
		
		promoPointsClaimDao.save(claim);
	}

	private int getAvailablePromoPoints(Promo promo, Customer customer) {
		return getTotalPromoPoints(promo, customer) - getClaimedPromoPoints(promo, customer);
	}

	private int getClaimedPromoPoints(Promo promo, Customer customer) {
		int claimedPoints = 0;
		for (PromoPointsClaim claim : findAllPromoPointsClaimByPromoAndCustomer(promo, customer)) {
			claimedPoints += claim.getPoints();
		}
		return claimedPoints;
	}

	private int getTotalPromoPoints(Promo promo, Customer customer) {
		List<SalesInvoice> salesInvoices = searchSalesInvoicesQualifiedForPromo(promo, customer);
		if (salesInvoices.isEmpty()) {
			return 0;
		}
		
		int totalPoints = 0;
		for (AvailedPromoPointsItem item : promo.evaluateForPoints(salesInvoices, getRelatedSalesReturns(salesInvoices))) {
			totalPoints += item.getPoints();
		}
		return totalPoints;
	}

	private List<SalesReturn> getRelatedSalesReturns(List<SalesInvoice> salesInvoices) {
		List<SalesReturn> salesReturns = new ArrayList<>();
		for (SalesInvoice salesInvoice : salesInvoices) {
			salesReturns.addAll(salesReturnService.findPostedSalesReturnsBySalesInvoice(salesInvoice));
		}
		return salesReturns;
	}
	
	private List<SalesInvoice> searchSalesInvoicesQualifiedForPromo(Promo promo, Customer customer) {
		SalesInvoiceSearchCriteria criteria = new SalesInvoiceSearchCriteria();
		criteria.setCustomer(customer);
		criteria.setTransactionDateFrom(promo.getStartDate());
		criteria.setTransactionDateTo(promo.getEndDate());
		criteria.setPricingScheme(promo.getPricingScheme());
		criteria.setCancelled(false);
		
		return salesInvoiceService.search(criteria);
	}

	@Transactional
	@Override
	public void delete(PromoPointsClaim claim) {
		promoPointsClaimDao.delete(claim);
	}

	@Transactional
	@Override
	public void cancel(PromoRedemption promoRedemption) {
		promoRedemption = getPromoRedemption(promoRedemption.getId());
		
		promoRedemption.setCancelled(true);
		promoRedemption.setCancelledBy(loginService.getLoggedInUser());
		promoRedemption.setCancelDate(systemDao.getCurrentDateTime());
		promoRedemptionDao.save(promoRedemption);
		
		for (PromoRedemptionReward reward : promoRedemption.getRewards()) {
			Product product = productDao.get(reward.getProduct().getId());
			product.addUnitQuantity(reward.getUnit(), reward.getQuantity());
			productDao.updateAvailableQuantities(product);
		}
	}

	@Override
	public int getNumberOfRedemptionsToday(Promo promo, Customer customer) {
		PromoRedemptionSearchCriteria criteria = new PromoRedemptionSearchCriteria();
		criteria.setPromo(promo);
		criteria.setCustomer(customer);
		criteria.setPostDateFrom(new Date());
		
		return search(criteria).stream()
			.mapToInt(promoRedemption -> {
				return promoRedemption.getRewards().stream()
						.mapToInt(reward -> reward.getQuantity())
						.sum();
			})
			.sum();
	}

}