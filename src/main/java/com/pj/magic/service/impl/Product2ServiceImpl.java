package com.pj.magic.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;
import com.pj.magic.repository.Product2Repository;
import com.pj.magic.service.Product2Service;

@Service
public class Product2ServiceImpl implements Product2Service {

	@Autowired
	private Product2Repository product2Repository;
	
	@Override
	public Long saveFromTrisys(Product product) {
		Product existing = null;
		
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
	public Product getProduct(Long id) {
		return product2Repository.get(id);
	}

}
