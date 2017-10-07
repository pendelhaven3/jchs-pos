package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PromoType3RulePromoProductDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.PromoType3Rule;
import com.pj.magic.model.PromoType3RulePromoProduct;

@Repository
public class PromoType3RulePromoProductDaoImpl extends MagicDao implements PromoType3RulePromoProductDao {

	@Override
	public void save(PromoType3RulePromoProduct promoProduct) {
		if (promoProduct.isNew()) {
			insert(promoProduct);
		} else {
			update(promoProduct);
		}
	}

	private static final String UPDATE_SQL = "update PROMO_TYPE_3_RULE_PROMO"
			+ " set PRODUCT_ID = ? where ID = ?";
	
	private void update(PromoType3RulePromoProduct promoProduct) {
		getJdbcTemplate().update(UPDATE_SQL, promoProduct.getProduct().getId(),
				promoProduct.getId());
	}

	private static final String INSERT_SQL =
			"insert into PROMO_TYPE_3_RULE_PROMO_PRODUCT (PROMO_TYPE_3_RULE_ID, PRODUCT_ID) values (?, ?)";
	
	private void insert(final PromoType3RulePromoProduct promoProduct) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, promoProduct.getParent().getId());
				ps.setLong(2, promoProduct.getProduct().getId());
				return ps;
			}

		}, holder);
		
		promoProduct.setId(holder.getKey().longValue());
	}

	private static final String FIND_ALL_BY_RULE_SQL =
			"select a.ID, PROMO_TYPE_3_RULE_ID, PRODUCT_ID,"
			+ " b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION"
			+ " from PROMO_TYPE_3_RULE_PROMO_PRODUCT a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID"
			+ " where a.PROMO_TYPE_3_RULE_ID = ?"
			+ " order by b.CODE";
	
	@Override
	public List<PromoType3RulePromoProduct> findAllByRule(PromoType3Rule rule) {
		return getJdbcTemplate().query(FIND_ALL_BY_RULE_SQL, new RowMapper<PromoType3RulePromoProduct>() {

			@Override
			public PromoType3RulePromoProduct mapRow(ResultSet rs, int rowNum) throws SQLException {
				PromoType3RulePromoProduct promoProduct = new PromoType3RulePromoProduct();
				promoProduct.setId(rs.getLong("ID"));
				promoProduct.setParent(new PromoType3Rule(rs.getLong("PROMO_TYPE_3_RULE_ID")));
				
				Product product = new Product();
				product.setId(rs.getLong("PRODUCT_ID"));
				product.setCode(rs.getString("PRODUCT_CODE"));
				product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
				promoProduct.setProduct(product);
				
				return promoProduct;
			}
			
		}, rule.getId());
	}

	private static final String DELETE_SQL = "delete from PROMO_TYPE_3_RULE_PROMO_PRODUCT where ID = ?";
	
	@Override
	public void delete(PromoType3RulePromoProduct promoProduct) {
		getJdbcTemplate().update(DELETE_SQL, promoProduct.getId());
	}

	private static final String DELETE_ALL_BY_PROMO_SQL = 
			"delete from PROMO_TYPE_3_RULE_PROMO_PRODUCT where PROMO_TYPE_3_RULE_ID = ?";
	
	@Override
	public void deleteAllByRule(PromoType3Rule rule) {
		getJdbcTemplate().update(DELETE_ALL_BY_PROMO_SQL, rule.getId());
	}

}