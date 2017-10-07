package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.Supplier;

public interface SupplierService {

	void save(Supplier supplier);
	
	Supplier getSupplier(long id);
	
	List<Supplier> getAllSuppliers();

	Supplier findSupplierByCode(String code);

	void delete(Supplier supplier);
	
}
