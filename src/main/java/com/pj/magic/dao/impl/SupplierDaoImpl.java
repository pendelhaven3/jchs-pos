package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.SupplierDao;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.Product;
import com.pj.magic.model.Supplier;

@Repository
public class SupplierDaoImpl extends MagicDao implements SupplierDao {
	
	private static final String BASE_SELECT_SQL = "select a.ID, CODE, a.NAME, ADDRESS, CONTACT_NUMBER, "
			+ " CONTACT_PERSON, FAX_NUMBER, EMAIL_ADDRESS, TIN, PAYMENT_TERM_ID, REMARKS, DISCOUNT,"
			+ " VAT_INCLUSIVE,"
			+ " b.NAME as PAYMENT_TERM_NAME, b.NUMBER_OF_DAYS"
			+ " from SUPPLIER a"
			+ " left join PAYMENT_TERM b"
			+ " 	on b.ID = a.PAYMENT_TERM_ID";
	
	private SupplierRowMapper supplierRowMapper = new SupplierRowMapper();
	
	@Override
	public void save(Supplier supplier) {
		if (supplier.getId() == null) {
			insert(supplier);
		} else {
			update(supplier);
		}
	}

	private static final String INSERT_SQL = "insert into SUPPLIER"
			+ " (CODE, NAME, ADDRESS, CONTACT_NUMBER, CONTACT_PERSON, FAX_NUMBER, EMAIL_ADDRESS, "
			+ "  TIN, PAYMENT_TERM_ID, REMARKS, DISCOUNT, VAT_INCLUSIVE)"
			+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private void insert(final Supplier supplier) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, supplier.getCode());
				ps.setString(2, supplier.getName());
				ps.setString(3, supplier.getAddress());
				ps.setString(4, supplier.getContactNumber());
				ps.setString(5, supplier.getContactPerson());
				ps.setString(6, supplier.getFaxNumber());
				ps.setString(7, supplier.getEmailAddress());
				ps.setString(8, supplier.getTin());
				if (supplier.getPaymentTerm() != null) {
					ps.setLong(9, supplier.getPaymentTerm().getId());
				} else {
					ps.setNull(9, Types.INTEGER);
				}
				ps.setString(10, supplier.getRemarks());
				ps.setString(11, supplier.getDiscount());
				ps.setString(12, supplier.isVatInclusive() ? "Y" : "N");
				return ps;
			}
		}, holder);
		
		supplier.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = "update SUPPLIER"
			+ " set CODE = ?, NAME = ?, ADDRESS = ?, CONTACT_NUMBER = ?, CONTACT_PERSON = ?,"
			+ " FAX_NUMBER = ?, EMAIL_ADDRESS = ?, TIN = ?, PAYMENT_TERM_ID = ?, REMARKS = ?,"
			+ " DISCOUNT = ?, VAT_INCLUSIVE = ?"
			+ " where ID = ?";
	
	private void update(Supplier supplier) {
		getJdbcTemplate().update(UPDATE_SQL,
				supplier.getCode(),
				supplier.getName(),
				supplier.getAddress(),
				supplier.getContactNumber(),
				supplier.getContactPerson(),
				supplier.getFaxNumber(),
				supplier.getEmailAddress(),
				supplier.getTin(),
				(supplier.getPaymentTerm() != null) ? supplier.getPaymentTerm().getId() : null,
				supplier.getRemarks(),
				supplier.getDiscount(),
				supplier.isVatInclusive() ? "Y" : "N",
				supplier.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public Supplier get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, supplierRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by NAME";
	
	@Override
	public List<Supplier> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, supplierRowMapper);
	}

	private class SupplierRowMapper implements RowMapper<Supplier> {

		@Override
		public Supplier mapRow(ResultSet rs, int rowNum) throws SQLException {
			Supplier supplier = new Supplier();
			supplier.setId(rs.getLong("ID"));
			supplier.setCode(rs.getString("CODE"));
			supplier.setName(rs.getString("NAME"));
			supplier.setAddress(rs.getString("ADDRESS"));
			supplier.setContactNumber(rs.getString("CONTACT_NUMBER"));
			supplier.setContactPerson(rs.getString("CONTACT_PERSON"));
			supplier.setFaxNumber(rs.getString("FAX_NUMBER"));
			supplier.setEmailAddress(rs.getString("EMAIL_ADDRESS"));
			if (rs.getLong("PAYMENT_TERM_ID") != 0) {
				PaymentTerm paymentTerm = new PaymentTerm();
				paymentTerm.setId(rs.getLong("PAYMENT_TERM_ID"));
				paymentTerm.setName(rs.getString("PAYMENT_TERM_NAME"));
				paymentTerm.setNumberOfDays(rs.getInt("NUMBER_OF_DAYS"));
				supplier.setPaymentTerm(paymentTerm);
			}
			supplier.setTin(rs.getString("TIN"));
			supplier.setRemarks(rs.getString("REMARKS"));
			supplier.setDiscount(rs.getString("DISCOUNT"));
			supplier.setVatInclusive("Y".equals(rs.getString("VAT_INCLUSIVE")));
			return supplier;
		}
		
	}

	private static final String FIND_ALL_BY_PRODUCT = BASE_SELECT_SQL
			+ " join SUPPLIER_PRODUCT c"
			+ "		on c.SUPPLIER_ID = a.ID"
			+ " where c.PRODUCT_ID = ?"
			+ " order by a.NAME";
	
	@Override
	public List<Supplier> findAllByProduct(Product product) {
		return getJdbcTemplate().query(FIND_ALL_BY_PRODUCT, supplierRowMapper, product.getId());
	}

	private static final String SAVE_PRODUCT_SUPPLIER_SQL =
			" insert into SUPPLIER_PRODUCT (SUPPLIER_ID, PRODUCT_ID) values (?, ?)";
	
	@Override
	public void saveSupplierProduct(Supplier supplier, Product product) {
		getJdbcTemplate().update(SAVE_PRODUCT_SUPPLIER_SQL, supplier.getId(), product.getId());
	}
	
	private static final String FIND_ALL_NOT_HAVING_PRODUCT_SQL = BASE_SELECT_SQL +
			" where not exists("
			+ "		select 1"
			+ "		from SUPPLIER_PRODUCT b"
			+ "		where b.SUPPLIER_ID = a.ID"
			+ "		and b.PRODUCT_ID = ?"
			+ ")"
			+ " order by a.NAME";

	@Override
	public List<Supplier> findAllNotHavingProduct(Product product) {
		return getJdbcTemplate().query(FIND_ALL_NOT_HAVING_PRODUCT_SQL, supplierRowMapper, product.getId());
	}

	private static final String DELETE_SUPPLIER_PRODUCT_SQL =
			" delete from SUPPLIER_PRODUCT where SUPPLIER_ID = ? and PRODUCT_ID = ?";
	
	@Override
	public void deleteSupplierProduct(Supplier supplier, Product product) {
		getJdbcTemplate().update(DELETE_SUPPLIER_PRODUCT_SQL, supplier.getId(), product.getId());
	}

	private static final String FIND_BY_CODE_SQL = BASE_SELECT_SQL + " where a.CODE = ?";
	
	@Override
	public Supplier findByCode(String code) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_CODE_SQL, supplierRowMapper, code);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String DELETE_SQL = "delete from SUPPLIER where ID = ?";
	
	@Override
	public void delete(Supplier supplier) {
		getJdbcTemplate().update(DELETE_SQL, supplier.getId());
	}

	private static final String REMOVE_ALL_PRODUCTS_FROM_SUPPLIER_SQL =
			"delete from SUPPLIER_PRODUCT where SUPPLIER_ID = ?";
	
	@Override
	public void removeAllProductsFromSupplier(Supplier supplier) {
		getJdbcTemplate().update(REMOVE_ALL_PRODUCTS_FROM_SUPPLIER_SQL, supplier.getId());
	}

	private static final String DELETE_ALL_BY_PRODUCT_SQL =
			"delete from SUPPLIER_PRODUCT where PRODUCT_ID = ?";
	
	@Override
	public void deleteAllByProduct(Product product) {
		getJdbcTemplate().update(DELETE_ALL_BY_PRODUCT_SQL, product.getId());
	}
	
}
