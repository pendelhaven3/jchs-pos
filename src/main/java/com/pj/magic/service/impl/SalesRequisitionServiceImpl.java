package com.pj.magic.service.impl;

import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.CustomerDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.PromoRedemptionDao;
import com.pj.magic.dao.PromoRedemptionRewardDao;
import com.pj.magic.dao.PromoRedemptionSalesInvoiceDao;
import com.pj.magic.dao.SalesRequisitionDao;
import com.pj.magic.dao.SalesRequisitionItemDao;
import com.pj.magic.dao.SalesRequisitionSeparateItemDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.dao.UserDao;
import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.NotEnoughPromoStocksException;
import com.pj.magic.exception.SalesRequisitionItemNotEnoughStocksException;
import com.pj.magic.exception.SalesRequisitionItemPostException;
import com.pj.magic.exception.SalesRequisitionPostException;
import com.pj.magic.model.Product;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionReward;
import com.pj.magic.model.PromoRedemptionSalesInvoice;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.model.SalesRequisitionSeparateItemsList;
import com.pj.magic.model.User;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.service.SalesRequisitionService;
import com.pj.magic.service.SystemService;

@Service
@Primary
public class SalesRequisitionServiceImpl implements SalesRequisitionService {

	@Autowired private SalesRequisitionDao salesRequisitionDao;
	@Autowired private SalesRequisitionItemDao salesRequisitionItemDao;
	@Autowired private ProductDao productDao;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private CustomerDao customerDao;
	@Autowired private UserDao userDao;
	@Autowired private LoginService loginService;
	@Autowired private SystemService systemService;
	@Autowired private PromoService promoService;
	@Autowired private PromoRedemptionDao promoRedemptionDao;
	@Autowired private PromoRedemptionSalesInvoiceDao promoRedemptionSalesInvoiceDao;
	@Autowired private PromoRedemptionRewardDao promoRedemptionRewardDao;
	@Autowired private SalesRequisitionSeparateItemDao salesRequisitionSeparateItemDao;
	@Autowired private SystemDao systemDao;
	
	@Transactional
	@Override
	public void save(SalesRequisition salesRequisition) {
		boolean isNew = (salesRequisition.getId() == null);
		if (isNew) {
			salesRequisition.setCreateDate(systemDao.getCurrentDateTime());
			salesRequisition.setTransactionDate(salesRequisition.getCreateDate());
			salesRequisition.setEncoder(loginService.getLoggedInUser());
		}
		salesRequisitionDao.save(salesRequisition);
	}

	@Override
	public SalesRequisition getSalesRequisition(long id) {
		SalesRequisition salesRequisition = salesRequisitionDao.get(id);
		loadSalesRequisitionDetails(salesRequisition);
		return salesRequisition;
	}
	
	private void loadSalesRequisitionDetails(SalesRequisition salesRequisition) {
		salesRequisition.setItems(salesRequisitionItemDao.findAllBySalesRequisition(salesRequisition));
		for (SalesRequisitionItem item : salesRequisition.getItems()) {
			item.setProduct(productDao.findByIdAndPricingScheme(
					item.getProduct().getId(), salesRequisition.getPricingScheme()));
		}
		if (salesRequisition.getCustomer() != null) {
			salesRequisition.setCustomer(customerDao.get(salesRequisition.getCustomer().getId())); // TODO: Review this
		}
		salesRequisition.setEncoder(userDao.get(salesRequisition.getEncoder().getId()));
	}

	@Transactional
	@Override
	public void save(SalesRequisitionItem item) {
		salesRequisitionItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(SalesRequisitionItem item) {
		salesRequisitionItemDao.delete(item);
	}

	@Transactional
	@Override
	public void delete(SalesRequisition salesRequisition) {
		salesRequisitionItemDao.deleteAllBySalesRequisition(salesRequisition);
		salesRequisitionDao.delete(salesRequisition);
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public SalesInvoice post(SalesRequisition salesRequisition)
			throws SalesRequisitionPostException {
	    return post(salesRequisition, loginService.getLoggedInUser());
	}

	private void postAvailedPromoRewards(SalesInvoice salesInvoice) {
		Date now = systemDao.getCurrentDateTime();
		for (Promo promo : promoService.getAllActivePromos()) {
			if (!(promo.getPromoType().isType2() && promo.checkIfEligible(salesInvoice))) {
				continue;
			}
			
			List<PromoRedemptionReward> rewards = promo.evaluateForRewards(salesInvoice);
			if (!rewards.isEmpty()) {
				PromoRedemption promoRedemption = new PromoRedemption();
				promoRedemption.setPromo(promo);
				promoRedemption.setCustomer(salesInvoice.getCustomer());
				promoRedemptionDao.save(promoRedemption);
				
				promoRedemption.setPosted(true);
				promoRedemption.setPostDate(now);
				promoRedemption.setPostedBy(loginService.getLoggedInUser());
				promoRedemptionDao.save(promoRedemption);
				
				PromoRedemptionSalesInvoice promoRedemptionSalesInvoice = new PromoRedemptionSalesInvoice();
				promoRedemptionSalesInvoice.setParent(promoRedemption);
				promoRedemptionSalesInvoice.setSalesInvoice(salesInvoice);
				promoRedemptionSalesInvoiceDao.save(promoRedemptionSalesInvoice);
				
				for (PromoRedemptionReward reward : rewards) {
					Product product = productDao.get(reward.getProduct().getId());
					
					if (product.getUnitQuantity(reward.getUnit()) < reward.getQuantity().intValue()) {
						throw new NotEnoughPromoStocksException();
					}
					
					product.addUnitQuantity(reward.getUnit(), -1 * reward.getQuantity());
					productDao.updateAvailableQuantities(product);
					
					reward.setParent(promoRedemption);
					promoRedemptionRewardDao.save(reward);
				}
			}
		}
	}

	@Override
	public List<SalesRequisition> getAllNonPostedSalesRequisitions() {
		SalesRequisition criteria = new SalesRequisition();
		criteria.setPosted(false);
		
		List<SalesRequisition> salesRequisitions = salesRequisitionDao.search(criteria);
		for (SalesRequisition salesRequisition : salesRequisitions) {
			salesRequisition.setItems(salesRequisitionItemDao.findAllBySalesRequisition(salesRequisition));
		}
		return salesRequisitions;
	}

	@Transactional
	@Override
	public SalesRequisition separatePerCaseItems(SalesRequisition salesRequisition) {
		SalesRequisition newSalesRequisition = 
				salesRequisition.extractToNewSalesRequisition(getSalesRequisitionSeparateItemsList());
		
		salesRequisitionDao.save(newSalesRequisition);
		
		for (SalesRequisitionItem item : newSalesRequisition.getItems()) {
			salesRequisitionItemDao.delete(item);
			item.setId(null);
			item.setParent(newSalesRequisition);
			salesRequisitionItemDao.save(item);
		}
		
		return newSalesRequisition;
	}

	@Override
	public SalesRequisitionSeparateItemsList getSalesRequisitionSeparateItemsList() {
		SalesRequisitionSeparateItemsList whitelist = new SalesRequisitionSeparateItemsList();
		whitelist.setProducts(salesRequisitionSeparateItemDao.getAll());
		return whitelist;
	}

	@Transactional
	@Override
	public void addSalesRequisitionSeparateItem(Product product) {
		salesRequisitionSeparateItemDao.add(product);
	}

	@Transactional
	@Override
	public void removeSalesRequisitionSeparateItem(Product product) {
		salesRequisitionSeparateItemDao.remove(product);
	}

	@Transactional
    @Override
    public SalesInvoice post(SalesRequisition salesRequisition, User postedBy) throws SalesRequisitionPostException {
        SalesRequisition updated = getSalesRequisition(salesRequisition.getId());
        
        if (updated.isPosted()) {
            throw new AlreadyPostedException();
        }
        
        SalesRequisitionPostException exception = new SalesRequisitionPostException();
        
        for (SalesRequisitionItem item : updated.getItems()) {
            if (item.getProduct().hasNoSellingPrice(item.getUnit())) {
                exception.add(new SalesRequisitionItemPostException(item, "No selling price"));
            } else if (item.getProduct().hasSellingPriceLessThanCost(item.getUnit())) {
                exception.add(new SalesRequisitionItemPostException(item, "Selling price less than cost"));
            }
                
            Product product = productDao.get(item.getProduct().getId());
            if (!product.hasAvailableUnitQuantity(item.getUnit(), item.getQuantity())) {
                exception.add(new SalesRequisitionItemNotEnoughStocksException(item));
            } else if (exception.isEmpty()) {
                product.subtractUnitQuantity(item.getUnit(), item.getQuantity());
                productDao.updateAvailableQuantities(product);
            }
        }
        
        if (!exception.isEmpty()) {
            throw exception;
        }
        
        updated.setPosted(true);
        salesRequisitionDao.save(updated);

        SalesInvoice salesInvoice = updated.createSalesInvoice();
        salesInvoice.setPostedBy(postedBy);
        salesInvoice.setVatAmount(salesInvoice.getTotalNetAmount().multiply(systemService.getVatRate())
                .setScale(2, RoundingMode.HALF_UP));
        salesInvoiceService.save(salesInvoice);
        
        postAvailedPromoRewards(salesInvoice);
        
        return salesInvoice;
    }
	
}