package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.SalesRequisitionSeparateItemDao;
import com.pj.magic.model.Product;

@Repository
public class SalesRequisitionSeparateItemDaoImpl extends MagicDao implements SalesRequisitionSeparateItemDao {

	private static final String BASE_SELECT_SQL =
			"select a.PRODUCT_ID, b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION"
			+ " from SALES_REQUISITION_SEPARATE_ITEM a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID";
	
	private static final String GET_ALL_SQL = BASE_SELECT_SQL
			+ " order by b.CODE";
	
	public List<Product> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, new RowMapper<Product>() {

			@Override
			public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
				Product product = new Product();
				product.setId(rs.getLong("PRODUCT_ID"));
				product.setCode(rs.getString("PRODUCT_CODE"));
				product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
				return product;
			}
			
		});
	}

	private static final String INSERT_SQL = 
			"insert into SALES_REQUISITION_SEPARATE_ITEM (PRODUCT_ID) values (?)";
	
	@Override
	public void add(Product product) {
		getJdbcTemplate().update(INSERT_SQL, product.getId());
	}

	private static final String REMOVE_SQL =
			"delete from SALES_REQUISITION_SEPARATE_ITEM where PRODUCT_ID = ?";
	
	@Override
	public void remove(Product product) {
		getJdbcTemplate().update(REMOVE_SQL, product.getId());
	}
	
}
