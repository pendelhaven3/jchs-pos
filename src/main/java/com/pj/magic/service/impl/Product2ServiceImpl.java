package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.SupplierDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.Product2;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.Unit;
import com.pj.magic.repository.Product2Repository;
import com.pj.magic.service.Product2Service;

@Service
public class Product2ServiceImpl implements Product2Service {

	@Autowired
	private Product2Repository product2Repository;
	
	@Autowired
	private SupplierDao supplierDao;
	
	@Override
	public Long saveFromTrisys(Product product) {
		Product2 existing = null;
		
		if (product.getProduct2Id() != null) {
			existing = product2Repository.get(product.getProduct2Id());
		} else {
			existing = product2Repository.findByDescription(product.getDescription());
		}
		
		if (existing != null) {
			for (String unit : Unit.values()) {
				if (product.hasUnit(unit)) {
					existing.addUnit(unit);
					existing.addActiveUnit(unit);
					existing.setUnitConversion(unit, product.getUnitConversion(unit));
				}
				
				existing.setActive(true);
				product2Repository.updateFromTrisys(existing);
			}
			
			return existing.getId();
		} else {
			product.setActiveUnits(product.getUnits());
			return product2Repository.createFromTrisys(product);
		}
	}

	@Override
	public Product2 getProduct(Long id) {
		Product2 product = product2Repository.get(id);
		product.setUnitSkus(product2Repository.getUnitSkus(product));
		return product;
	}

	@Override
	public void save(Product2 product) {
		product2Repository.update(product);
	}

	@Transactional
	@Override
	public void addProductSupplier(Product2 product, Supplier supplier) {
		supplierDao.saveSupplierProduct(supplier, product);
	}

	@Transactional
	@Override
	public void deleteProductSupplier(Product2 product, Supplier supplier) {
		supplierDao.deleteSupplierProduct(supplier, product);
	}

	@Override
	public List<Supplier> getProductSuppliers(Product2 product) {
		return supplierDao.findAllByProduct(product);
	}

}
