package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Customer;
import com.pj.magic.model.search.CustomerSearchCriteria;

public interface CustomerDao {

	void save(Customer customer);
	
	List<Customer> getAll();
	
	Customer get(long id);
	
	Customer findFirstWithCodeLike(String code);
	
	Customer findByCode(String code);

	List<Customer> findAllWithNameLike(String code);

	List<Customer> search(CustomerSearchCriteria criteria);

	void delete(Customer customer);
	
}
