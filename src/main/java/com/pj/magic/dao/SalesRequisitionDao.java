package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Customer;
import com.pj.magic.model.SalesRequisition;

public interface SalesRequisitionDao {

	SalesRequisition get(long id);

	void save(SalesRequisition salesRequisition);

	void delete(SalesRequisition salesRequisition);

	List<SalesRequisition> search(SalesRequisition criteria);

	List<SalesRequisition> findAllByCustomer(Customer customer);
	
}
