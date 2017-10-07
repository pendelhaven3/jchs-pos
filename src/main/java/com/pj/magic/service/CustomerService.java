package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.Customer;
import com.pj.magic.model.search.CustomerSearchCriteria;

public interface CustomerService {

	List<Customer> getAllCustomers();
	
	Customer findFirstCustomerWithCodeLike(String code);
	
	Customer findCustomerByCode(String code);
	
	void save(Customer customer);

	List<Customer> findAllWithNameLike(String customerCode);

	List<Customer> searchCustomers(CustomerSearchCriteria criteria);

	void deleteCustomer(Customer customer);

	boolean canDeleteCustomer(Customer customer);
	
}
