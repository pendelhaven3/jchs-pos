package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.CustomerDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.search.CustomerSearchCriteria;

@Repository
public class CustomerDaoImpl extends MagicDao implements CustomerDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, CODE, a.NAME, BUSINESS_ADDRESS, DELIVERY_ADDRESS, CONTACT_PERSON, CONTACT_NUMBER,"
			+ " TIN, APPROVED_CREDIT_LINE, BUSINESS_TYPE, OWNERS, BANK_REFERENCES, HOLD_IND, REMARKS, ACTIVE_IND,"
			+ " PAYMENT_TERM_ID, b.NAME as PAYMENT_TERM_NAME"
			+ " from CUSTOMER a"
			+ " left join PAYMENT_TERM b"
			+ "   on b.ID = a.PAYMENT_TERM_ID"
			+ " where 1 = 1";
	
	@PersistenceContext
	private EntityManager entityManager;
	
	private CustomerRowMapper customerRowMapper = new CustomerRowMapper();
	
	@Override
	public void save(Customer customer) {
		if (customer.getId() == null) {
			entityManager.persist(customer);
		} else {
			entityManager.merge(customer);
		}
	}

	@Override
	public List<Customer> getAll() {
        return entityManager.createQuery("SELECT c FROM Customer c order by c.code", Customer.class).getResultList();
	}
	
	private class CustomerRowMapper implements RowMapper<Customer> {

		@Override
		public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
			Customer customer = new Customer();
			customer.setCode(rs.getString("CODE"));
			customer.setId(rs.getLong("ID"));
			customer.setName(rs.getString("NAME"));
			customer.setBusinessAddress(rs.getString("BUSINESS_ADDRESS"));
			customer.setDeliveryAddress(rs.getString("DELIVERY_ADDRESS"));
			customer.setContactPerson(rs.getString("CONTACT_PERSON"));
			customer.setContactNumber(rs.getString("CONTACT_NUMBER"));
			if (rs.getLong("PAYMENT_TERM_ID") != 0) {
				customer.setPaymentTerm(
						new PaymentTerm(rs.getLong("PAYMENT_TERM_ID"), rs.getString("PAYMENT_TERM_NAME")));
			}
			customer.setTin(rs.getString("TIN"));
			customer.setApprovedCreditLine(rs.getBigDecimal("APPROVED_CREDIT_LINE"));
			customer.setBusinessType(rs.getString("BUSINESS_TYPE"));
			customer.setOwners(rs.getString("OWNERS"));
			customer.setBankReferences(rs.getString("BANK_REFERENCES"));
			customer.setHold("Y".equals(rs.getString("HOLD_IND")));
			customer.setActive("Y".equals(rs.getString("ACTIVE_IND")));
			customer.setRemarks(rs.getString("REMARKS"));
			return customer;
		}
		
	}

	@Override
	public Customer get(long id) {
		return entityManager.find(Customer.class, id);
	}

	private static final String FIND_FIRST_WITH_CODE_LIKE_SQL =
			BASE_SELECT_SQL + " and a.CODE like ? limit 1";
	
	// TODO: Use JPA query here
	@Override
	public Customer findFirstWithCodeLike(String code) {
		try {
			return getJdbcTemplate().queryForObject(FIND_FIRST_WITH_CODE_LIKE_SQL, customerRowMapper, code + "%");
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String FIND_BY_CODE_SQL =
			BASE_SELECT_SQL + " and a.CODE = ?";
	
	@Override
	public Customer findByCode(String code) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_CODE_SQL, customerRowMapper, code);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String FIND_ALL_WITH_NAME_LIKE_SQL =
			BASE_SELECT_SQL + " and a.NAME like ? order by a.NAME";
	
	@Override
	public List<Customer> findAllWithNameLike(String name) {
		return getJdbcTemplate().query(FIND_ALL_WITH_NAME_LIKE_SQL, customerRowMapper, name + "%");
	}

	@Override
	public List<Customer> search(CustomerSearchCriteria criteria) {
		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		
		if (!StringUtils.isEmpty(criteria.getNameLike())) {
			sql.append(" and a.NAME like ?");
			params.add(criteria.getNameLike() + "%");
		}
		
		if (criteria.getActive() != null) {
			sql.append(" and a.ACTIVE_IND = ?");
			params.add(criteria.getActive() ? "Y" : "N");
		}
		
		sql.append(" order by a.NAME");
		
		return getJdbcTemplate().query(sql.toString(), customerRowMapper, params.toArray());
	}

	private static final String DELETE_CUSTOMER_SQL = "delete from CUSTOMER where ID = ?";
	
	@Override
	public void delete(Customer customer) {
		getJdbcTemplate().update(DELETE_CUSTOMER_SQL, customer.getId());
	}
	
}
