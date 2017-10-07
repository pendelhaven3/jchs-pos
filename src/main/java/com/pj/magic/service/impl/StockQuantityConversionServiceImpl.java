package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.StockQuantityConversionDao;
import com.pj.magic.dao.StockQuantityConversionItemDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.model.Product;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.StockQuantityConversionItem;
import com.pj.magic.model.search.StockQuantityConversionSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.StockQuantityConversionService;

@Service
@Primary
public class StockQuantityConversionServiceImpl implements StockQuantityConversionService {

	@Autowired private StockQuantityConversionDao stockQuantityConversionDao;
	@Autowired private StockQuantityConversionItemDao stockQuantityConversionItemDao;
	@Autowired private ProductDao productDao;
	@Autowired private LoginService loginService;
	@Autowired private SystemDao systemDao;
	
	@Transactional
	@Override
	public void save(StockQuantityConversion stockQuantityConversion) {
		stockQuantityConversionDao.save(stockQuantityConversion);
	}

	@Override
	public StockQuantityConversion getStockQuantityConversion(long id) {
		StockQuantityConversion stockQuantityConversion = stockQuantityConversionDao.get(id);
		loadStockQuantityConversionDetails(stockQuantityConversion);
		return stockQuantityConversion;
	}

	private void loadStockQuantityConversionDetails(
			StockQuantityConversion stockQuantityConversion) {
		stockQuantityConversion.setItems(
				stockQuantityConversionItemDao.findAllByStockQuantityConversion(stockQuantityConversion));
		for (StockQuantityConversionItem item : stockQuantityConversion.getItems()) {
			item.setProduct(productDao.get(item.getProduct().getId()));
		}
	}

	@Override
	public List<StockQuantityConversion> getAllStockQuantityConversions() {
		return stockQuantityConversionDao.getAll();
	}

	@Transactional
	@Override
	public void delete(StockQuantityConversion stockQuantityConversion) {
		stockQuantityConversionItemDao.deleteAllByStockQuantityConversion(stockQuantityConversion);
		stockQuantityConversionDao.delete(stockQuantityConversion);
	}

	@Transactional
	@Override
	public void post(StockQuantityConversion stockQuantityConversion) {
		StockQuantityConversion updated = getStockQuantityConversion(stockQuantityConversion.getId());
		
		if (updated.isPosted()) {
			throw new AlreadyPostedException();
		}
		
		for (StockQuantityConversionItem item : updated.getItems()) {
			// [PJ 08/26/2014] Do not update product quantity inside stock quantity conversion object
			// because it has to be "rolled back" manually when an exception happens during posting
			Product product = productDao.get(item.getProduct().getId());
			if (!product.hasAvailableUnitQuantity(item.getFromUnit(), item.getQuantity())) {
				throw new NotEnoughStocksException(item);
			} else {
				product.subtractUnitQuantity(item.getFromUnit(), item.getQuantity());
				product.addUnitQuantity(item.getToUnit(), item.getConvertedQuantity());
				productDao.updateAvailableQuantities(product);
				stockQuantityConversionItemDao.updateConvertedQuantity(item);
			}
		}
		updated.setPosted(true);
		updated.setPostDate(systemDao.getCurrentDateTime());
		updated.setPostedBy(loginService.getLoggedInUser());
		stockQuantityConversionDao.save(updated);
	}

	@Transactional
	@Override
	public void save(StockQuantityConversionItem item) {
		stockQuantityConversionItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(StockQuantityConversionItem item) {
		stockQuantityConversionItemDao.delete(item);
	}

	@Override
	public List<StockQuantityConversion> search(StockQuantityConversionSearchCriteria criteria) {
		return stockQuantityConversionDao.search(criteria);
	}

	@Transactional
	@Override
	public void saveAndPost(StockQuantityConversionItem item) {
		Product product = productDao.get(item.getProduct().getId());
		if (!product.hasAvailableUnitQuantity(item.getFromUnit(), item.getQuantity())) {
			throw new NotEnoughStocksException(item);
		} else {
			product.subtractUnitQuantity(item.getFromUnit(), item.getQuantity());
			product.addUnitQuantity(item.getToUnit(), item.getConvertedQuantity());
			productDao.updateAvailableQuantities(product);
			stockQuantityConversionItemDao.save(item);
			stockQuantityConversionItemDao.updateConvertedQuantity(item);
		}
	}

	@Override
	public int getNextPageNumber() {
		return stockQuantityConversionDao.getNextPageNumber();
	}

	@Override
	public List<StockQuantityConversion> getAllPendingStockQuantityConversions() {
		return stockQuantityConversionDao.getAllPending();
	}

	@Transactional
	@Override
	public void addAutoPostedQuantity(StockQuantityConversionItem item) {
		Product product = productDao.get(item.getProduct().getId());
		if (!product.hasAvailableUnitQuantity(item.getFromUnit())) {
			throw new NotEnoughStocksException();
		}
		
		product.subtractUnitQuantity(item.getFromUnit(), 1);
		product.addUnitQuantity(item.getToUnit(), 
				product.getConvertedQuantity(item.getFromUnit(), item.getToUnit()));
		productDao.updateAvailableQuantities(product);

		item = stockQuantityConversionItemDao.get(item.getId());
		item.setProduct(product);
		item.setQuantity(item.getQuantity() + 1);
		item.calculateConvertedQuantity();
		stockQuantityConversionItemDao.save(item);
		stockQuantityConversionItemDao.updateConvertedQuantity(item);
	}

	@Override
	public StockQuantityConversionItem getStockQuantityConversionItem(Long id) {
		StockQuantityConversionItem item = stockQuantityConversionItemDao.get(id);
		item.setProduct(productDao.get(item.getProduct().getId()));
		return item;
	}

}