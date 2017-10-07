package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.CustomerDao;
import com.pj.magic.dao.SalesRequisitionDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.search.CustomerSearchCriteria;
import com.pj.magic.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired private CustomerDao customerDao;
	@Autowired private SalesRequisitionDao salesRequisitionDao;
	
	@Override
	public List<Customer> getAllCustomers() {
		return customerDao.getAll();
	}

	@Override
	public Customer findFirstCustomerWithCodeLike(String code) {
		return customerDao.findFirstWithCodeLike(code);
	}

	@Override
	public Customer findCustomerByCode(String code) {
		return customerDao.findByCode(code);
	}

	@Transactional
	@Override
	public void save(Customer customer) {
		customerDao.save(customer);
	}

	@Override
	public List<Customer> findAllWithNameLike(String name) {
		return customerDao.findAllWithNameLike(name);
	}

	@Override
	public List<Customer> searchCustomers(CustomerSearchCriteria criteria) {
		return customerDao.search(criteria);
	}

	@Transactional
	@Override
	public void deleteCustomer(Customer customer) {
		customerDao.delete(customer);
	}

	@Override
	public boolean canDeleteCustomer(Customer customer) {
		return salesRequisitionDao.findAllByCustomer(customer).isEmpty();
	}

}
