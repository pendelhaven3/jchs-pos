package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.PromoDao;
import com.pj.magic.dao.PromoRedemptionDao;
import com.pj.magic.dao.PromoType1RuleDao;
import com.pj.magic.dao.PromoType2RuleDao;
import com.pj.magic.dao.PromoType3RuleDao;
import com.pj.magic.dao.PromoType3RulePromoProductDao;
import com.pj.magic.dao.PromoType4RuleDao;
import com.pj.magic.dao.PromoType4RulePromoProductDao;
import com.pj.magic.dao.PromoType5RuleDao;
import com.pj.magic.dao.PromoType5RulePromoProductDao;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType2Rule;
import com.pj.magic.model.PromoType3Rule;
import com.pj.magic.model.PromoType3RulePromoProduct;
import com.pj.magic.model.PromoType4Rule;
import com.pj.magic.model.PromoType4RulePromoProduct;
import com.pj.magic.model.PromoType5Rule;
import com.pj.magic.model.PromoType5RulePromoProduct;
import com.pj.magic.model.search.PromoSearchCriteria;

@Service
public class PromoServiceImpl implements PromoService {

	@Autowired private PromoDao promoDao;
	@Autowired private PromoType1RuleDao promoType1RuleDao;
	@Autowired private PromoType2RuleDao promoType2RuleDao;
	@Autowired private PromoType3RuleDao promoType3RuleDao;
	@Autowired private PromoType4RuleDao promoType4RuleDao;
	@Autowired private PromoType5RuleDao promoType5RuleDao;
	@Autowired private ProductDao productDao;
	@Autowired private PromoRedemptionDao promoRedemptionDao;
	@Autowired private PromoType3RulePromoProductDao promoType3RulePromoProductDao;
	@Autowired private PromoType4RulePromoProductDao promoType4RulePromoProductDao;
	@Autowired private PromoType5RulePromoProductDao promoType5RulePromoProductDao;
	
	@Override
	public List<Promo> getAllPromos() {
		return promoDao.getAll();
	}

	@Transactional
	@Override
	public void save(Promo promo) {
		boolean isNew = (promo.getId() == null);
		promoDao.save(promo);
		if (isNew) {
			promoRedemptionDao.insertNewPromoRedemptionSequence(promo);
		} else {
			switch (promo.getPromoType()) {
			case PROMO_TYPE_1:
				promoType1RuleDao.save(promo.getPromoType1Rule());
				break;
			case PROMO_TYPE_3:
				promoType3RuleDao.save(promo.getPromoType3Rule());
				break;
			case PROMO_TYPE_4:
				promoType4RuleDao.save(promo.getPromoType4Rule());
				break;
			case PROMO_TYPE_5:
				promoType5RuleDao.save(promo.getPromoType5Rule());
				break;
			default:
				break;
			}
		}
	}

	@Override
	public Promo getPromo(long id) {
		Promo promo = promoDao.get(id);
		loadPromoDetails(promo);
		return promo;
	}

	private void loadPromoDetails(Promo promo) {
		switch (promo.getPromoType()) {
		case PROMO_TYPE_1:
			promo.setPromoType1Rule(promoType1RuleDao.findByPromo(promo));
			break;
		case PROMO_TYPE_2:
			promo.setPromoType2Rules(promoType2RuleDao.findAllByPromo(promo));
			for (PromoType2Rule rule : promo.getPromoType2Rules()) {
				rule.setParent(promo);
				rule.setPromoProduct(productDao.get(rule.getPromoProduct().getId()));
				rule.setFreeProduct(productDao.get(rule.getFreeProduct().getId()));
			}
			break;
		case PROMO_TYPE_3:
			PromoType3Rule type3Rule = promoType3RuleDao.findByPromo(promo);
			if (type3Rule != null) {
				type3Rule.setPromoProducts(promoType3RulePromoProductDao.findAllByRule(type3Rule));
			}
			promo.setPromoType3Rule(type3Rule);
			break;
		case PROMO_TYPE_4:
			PromoType4Rule type4Rule = promoType4RuleDao.findByPromo(promo);
			if (type4Rule != null) {
				type4Rule.setPromoProducts(promoType4RulePromoProductDao.findAllByRule(type4Rule));
			}
			promo.setPromoType4Rule(type4Rule);
			break;
		case PROMO_TYPE_5:
			PromoType5Rule type5Rule = promoType5RuleDao.findByPromo(promo);
			if (type5Rule != null) {
				type5Rule.setPromoProducts(promoType5RulePromoProductDao.findAllByRule(type5Rule));
			}
			promo.setPromoType5Rule(type5Rule);
			break;
		}
	}

	@Transactional
	@Override
	public void save(PromoType2Rule rule) {
		promoType2RuleDao.save(rule);
	}

	@Transactional
	@Override
	public void delete(PromoType2Rule rule) {
		promoType2RuleDao.delete(rule);
	}

	@Override
	public List<Promo> getAllActivePromos() {
		List<Promo> promos = promoDao.findAllByActive(true);
		for (Promo promo : promos) {
			loadPromoDetails(promo);
		}
		return promos;
	}

	@Transactional
	@Override
	public void save(PromoType3RulePromoProduct promoProduct) {
		promoType3RulePromoProductDao.save(promoProduct);
	}

	@Transactional
	@Override
	public void delete(PromoType3RulePromoProduct promoProduct) {
		promoType3RulePromoProductDao.delete(promoProduct);		
	}

	@Transactional
	@Override
	public void addAllPromoProducts(PromoType3Rule rule) {
		removeAllPromoProducts(rule);
		promoType3RuleDao.addAllPromoProducts(rule);
	}

	@Transactional
	@Override
	public void removeAllPromoProducts(PromoType3Rule rule) {
		promoType3RulePromoProductDao.deleteAllByRule(rule);
	}

	@Transactional
	@Override
	public void save(PromoType4RulePromoProduct promoProduct) {
		promoType4RulePromoProductDao.save(promoProduct);
	}

	@Transactional
	@Override
	public void delete(PromoType4RulePromoProduct promoProduct) {
		promoType4RulePromoProductDao.delete(promoProduct);		
	}

	@Transactional
	@Override
	public void addAllPromoProducts(PromoType4Rule rule) {
		removeAllPromoProducts(rule);
		promoType4RuleDao.addAllPromoProducts(rule);
	}

	@Transactional
	@Override
	public void removeAllPromoProducts(PromoType4Rule rule) {
		promoType4RulePromoProductDao.deleteAllByRule(rule);
	}

	@Override
	public List<Promo> search(PromoSearchCriteria criteria) {
		List<Promo> promos = promoDao.search(criteria);
		for (Promo promo : promos) {
			loadPromoDetails(promo);
		}
		return promos;
	}

	@Transactional
	@Override
	public void save(PromoType5RulePromoProduct promoProduct) {
		promoType5RulePromoProductDao.save(promoProduct);
	}

	@Transactional
	@Override
	public void delete(PromoType5RulePromoProduct promoProduct) {
		promoType5RulePromoProductDao.delete(promoProduct);		
	}

	@Transactional
	@Override
	public void addAllPromoProducts(PromoType5Rule rule) {
		removeAllPromoProducts(rule);
		promoType5RuleDao.addAllPromoProducts(rule);
	}

	@Transactional
	@Override
	public void removeAllPromoProducts(PromoType5Rule rule) {
		promoType5RulePromoProductDao.deleteAllByRule(rule);
	}

	@Override
	public void addAllPromoProductsByManufacturer(PromoType4Rule rule, Manufacturer manufacturer) {
		removeAllPromoProducts(rule);
		promoType4RuleDao.addAllPromoProductsByManufacturer(rule, manufacturer);
	}

}