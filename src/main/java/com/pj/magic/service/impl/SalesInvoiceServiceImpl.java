package com.pj.magic.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.pj.magic.dao.CustomerDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.PromoRedemptionDao;
import com.pj.magic.dao.SalesInvoiceDao;
import com.pj.magic.dao.SalesInvoiceItemDao;
import com.pj.magic.dao.SalesRequisitionDao;
import com.pj.magic.dao.SalesRequisitionItemDao;
import com.pj.magic.dao.SalesReturnDao;
import com.pj.magic.dao.SalesReturnItemDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.exception.SellingPriceLessThanCostException;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Product;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.SalesReturnItem;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PromoRedemptionService;
import com.pj.magic.service.SalesInvoiceService;

@Service
public class SalesInvoiceServiceImpl implements SalesInvoiceService {

	@Autowired private SalesInvoiceDao salesInvoiceDao;
	@Autowired private SalesInvoiceItemDao salesInvoiceItemDao;
	@Autowired private ProductDao productDao;
	@Autowired private CustomerDao customerDao;
	@Autowired private SalesRequisitionDao salesRequisitionDao;
	@Autowired private SalesRequisitionItemDao salesRequisitionItemDao;
	@Autowired private LoginService loginService;
	@Autowired private SalesReturnDao salesReturnDao;
	@Autowired private SalesReturnItemDao salesReturnItemDao;
	@Autowired private SystemDao systemDao;
	@Autowired private PromoRedemptionDao promoRedemptionDao;
	@Autowired private PromoRedemptionService promoRedemptionService;
	
	@Transactional
	@Override
	public void save(SalesInvoice salesInvoice) {
		salesInvoice.setPostDate(systemDao.getCurrentDateTime());
		if (salesInvoice.getPostedBy() == null) {
	        salesInvoice.setPostedBy(loginService.getLoggedInUser());
		}
		salesInvoiceDao.save(salesInvoice);
		for (SalesInvoiceItem item : salesInvoice.getItems()) {
			salesInvoiceItemDao.save(item);
		}
	}

	private void loadSalesInvoiceDetails(SalesInvoice salesInvoice) {
		salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		Collections.sort(salesInvoice.getItems());
		salesInvoice.setCustomer(customerDao.get(salesInvoice.getCustomer().getId())); // TODO: Review next
	}

	@Override
	public SalesInvoice get(long id) {
		SalesInvoice salesInvoice = salesInvoiceDao.get(id);
		loadSalesInvoiceDetails(salesInvoice);
		return salesInvoice;
	}

	@Transactional
	@Override
	public SalesRequisition createSalesRequisitionFromSalesInvoice(SalesInvoice salesInvoice) {
		SalesRequisition salesRequisition = salesInvoice.createSalesRequisition();
		salesRequisition.setCreateDate(systemDao.getCurrentDateTime());
		salesRequisition.setEncoder(loginService.getLoggedInUser());
		salesRequisition.setTransactionDate(salesInvoice.getTransactionDate());
		salesRequisitionDao.save(salesRequisition);
		
		for (SalesRequisitionItem item : salesRequisition.getItems()) {
			salesRequisitionItemDao.save(item);
		}
		
		return salesRequisition;
	}

	@Transactional
	@Override
	public void mark(SalesInvoice salesInvoice) {
		salesInvoice.setMarked(true);
		salesInvoice.setMarkDate(systemDao.getCurrentDateTime());
		salesInvoice.setMarkedBy(loginService.getLoggedInUser());
		salesInvoiceDao.save(salesInvoice);
	}

	@Transactional
	@Override
	public void cancel(SalesInvoice salesInvoice) {
		Date today = systemDao.getCurrentDateTime();
		
		salesInvoice.setCancelled(true);
		salesInvoice.setCancelDate(today);
		salesInvoice.setCancelledBy(loginService.getLoggedInUser());
		salesInvoiceDao.save(salesInvoice);
		
		for (SalesInvoiceItem item : salesInvoice.getItems()) {
			Product product = productDao.get(item.getProduct().getId());
			product.addUnitQuantity(item.getUnit(), item.getQuantity());
			productDao.updateAvailableQuantities(product);
		}
		
		for (SalesReturn salesReturn : salesReturnDao.findAllBySalesInvoice(salesInvoice)) {
			if (!salesReturn.isCancelled()) {
				salesReturn.setCancelled(true);
				salesReturn.setCancelDate(today);
				salesReturn.setCancelledBy(loginService.getLoggedInUser());
				salesReturnDao.save(salesReturn);
				
				if (salesReturn.isPosted()) {
					for (SalesReturnItem salesReturnItem : salesReturnItemDao.findAllBySalesReturn(salesReturn)) {
						Product product = productDao.get(salesReturnItem.getSalesInvoiceItem().getProduct().getId());
						product.subtractUnitQuantity(
								salesReturnItem.getSalesInvoiceItem().getUnit(), salesReturnItem.getQuantity());
						productDao.updateAvailableQuantities(product);
					}
				}
			}
		}
		
		for (PromoRedemption promoRedemption : promoRedemptionDao.findAllBySalesInvoice(salesInvoice)) {
			if (promoRedemption.getPromo().isPromoType2()) {
				promoRedemptionService.cancel(promoRedemption);
			}
		};
	}

	@Override
	public List<SalesInvoice> getNewSalesInvoices() {
		SalesInvoiceSearchCriteria criteria = new SalesInvoiceSearchCriteria();
		criteria.setMarked(false);
		criteria.setCancelled(false);
		
		List<SalesInvoice> salesInvoices = salesInvoiceDao.search(criteria);
		for (SalesInvoice salesInvoice : salesInvoices) {
			salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		}
		return salesInvoices;
	}

	@Transactional
	@Override
	public void markSalesInvoices(List<SalesInvoice> salesInvoices) {
		for (SalesInvoice salesInvoice : salesInvoices) {
			if (salesInvoice.isMarked()) {
				mark(salesInvoice);
			}
		}
	}

	@Override
	public void save(SalesInvoiceItem item) {
		salesInvoiceItemDao.save(item);
	}

	@Override
	public List<SalesInvoice> search(SalesInvoiceSearchCriteria criteria) {
		List<SalesInvoice> salesInvoices = salesInvoiceDao.search(criteria);
		for (SalesInvoice salesInvoice : salesInvoices) {
			salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		}
		return salesInvoices;
	}

	@Override
	public List<SalesInvoice> getAllNewSalesInvoices() {
		SalesInvoiceSearchCriteria criteria = new SalesInvoiceSearchCriteria();
		criteria.setMarked(false);
		criteria.setCancelled(false);
		
		List<SalesInvoice> salesInvoices = salesInvoiceDao.search(criteria);
		for (SalesInvoice salesInvoice : salesInvoices) {
			salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		}
		return salesInvoices;
	}

	@Override
	public SalesInvoice findBySalesInvoiceNumber(long salesInvoiceNumber) {
		SalesInvoice salesInvoice = salesInvoiceDao.findBySalesInvoiceNumber(salesInvoiceNumber);
		salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		return salesInvoice;
	}

	@Override
	public List<SalesInvoice> findAllSalesInvoicesForPaymentByCustomer(Customer customer) {
		List<SalesInvoice> salesInvoices = salesInvoiceDao.findAllForPaymentByCustomer(customer);
		for (SalesInvoice salesInvoice : salesInvoices) {
			salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		}
		return salesInvoices;
	}

	@Override
	public SalesInvoice getMostRecentSalesInvoice(Customer customer, Product product) {
		SalesInvoice salesInvoice = salesInvoiceDao.findMostRecentByCustomerAndProduct(customer, product);
		if (salesInvoice != null) {
			salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		}
		return salesInvoice;
	}

	@Transactional
	@Override
	public void setAllItemDiscounts(SalesInvoice salesInvoice, BigDecimal discount1, BigDecimal discount2,
			BigDecimal discount3) {
		for (SalesInvoiceItem item : salesInvoice.getItems()) {
			item.setDiscount1(discount1);
			item.setDiscount2(discount2);
			item.setDiscount3(discount3);
			if (item.hasNetPriceLessThanCost()) {
				throw new SellingPriceLessThanCostException(item);
			}
			salesInvoiceItemDao.save(item);
		}
	}

	@Transactional
	@Override
	public void markAsPrinted(SalesInvoice salesInvoice) {
		salesInvoice.setPrinted(true);
		salesInvoiceDao.save(salesInvoice);
	}

}