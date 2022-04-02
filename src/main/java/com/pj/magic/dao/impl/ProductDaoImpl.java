package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.Product2;
import com.pj.magic.model.ProductBySpecialCode;
import com.pj.magic.model.ProductCustomCode;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.Unit;
import com.pj.magic.model.UnitConversion;
import com.pj.magic.model.UnitCost;
import com.pj.magic.model.search.ProductSearchCriteria;
import com.pj.magic.util.NumberUtil;

@Repository
public class ProductDaoImpl extends MagicDao implements ProductDao {
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, a.CODE, a.DESCRIPTION,"
			+ " a.UOM_CODE, a.UOM_CODE1, a.UOM_QTY, a.UOM_QTY1,"
			+ " MAX_STOCK_LEVEL, MIN_STOCK_LEVEL,"
			+ " a.GROSS_COST, a.GROSS_COST1, a.FINAL_COST, a.FINAL_COST1,"
			+ " a.ACTIVE_IND, a.PRODUCT2_ID"
			+ " from PRODUCT a"
			+ " where 1 = 1";
	
	private ProductRowMapper productRowMapper = new ProductRowMapper();
	
    private static final String FIND_ALL_SQL = BASE_SELECT_SQL + " order by DESCRIPTION";
	
	@Override
	public List<Product> getAll() {
        return getJdbcTemplate().query(FIND_ALL_SQL, productRowMapper);
	}

    private static final String FIND_BY_CODE_SQL = BASE_SELECT_SQL + " and a.CODE = ?";
	
	@Override
	public Product findByCode(String code) {
        try {
            return getJdbcTemplate().queryForObject(FIND_BY_CODE_SQL, productRowMapper, code);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
	}

    private static final String FIND_BY_ID_SQL = BASE_SELECT_SQL + " and a.ID = ?";
	
	@Override
	public Product get(long id) {
		return getJdbcTemplate().queryForObject(FIND_BY_ID_SQL, productRowMapper, id);
	}

	private class ProductRowMapper implements RowMapper<Product> {

		@Override
		public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
			Product product = new Product();
			product.setId(rs.getLong("ID"));
            product.setCode(rs.getString("CODE"));
			product.setDescription(rs.getString("DESCRIPTION"));
			product.setMaximumStockLevel(rs.getInt("MAX_STOCK_LEVEL"));
			product.setMinimumStockLevel(rs.getInt("MIN_STOCK_LEVEL"));
			product.setActive("Y".equals(rs.getString("ACTIVE_IND")));
			
			String unit1 = rs.getString("UOM_CODE");
			String unit2 = rs.getString("UOM_CODE1");
			
			product.getUnits().add(unit1);
            product.getUnitConversions().add(new UnitConversion(unit1, rs.getInt("UOM_QTY")));
            product.getUnitCosts().add(new UnitCost(unit1, NumberUtil.nvl(rs.getBigDecimal("GROSS_COST")), 
                    NumberUtil.nvl(rs.getBigDecimal("FINAL_COST"))));
            
            if (!StringUtils.isEmpty(unit2)) {
                product.getUnits().add(unit2);
                product.getUnitConversions().add(new UnitConversion(unit2, rs.getInt("UOM_QTY1")));
                product.getUnitCosts().add(new UnitCost(unit2, NumberUtil.nvl(rs.getBigDecimal("GROSS_COST1")), 
                        NumberUtil.nvl(rs.getBigDecimal("FINAL_COST1"))));
            }
            
            String product2Id = rs.getString("PRODUCT2_ID");
            if (!StringUtils.isEmpty(product2Id)) {
            	product.setProduct2Id(Long.valueOf(product2Id));
            }
			
			return product;
		}
		
	}

	private static final String UPDATE_AVAILABLE_QUANTITIES_SQL =
			"update PRODUCT"
			+ " set AVAIL_QTY_CSE = ?, AVAIL_QTY_CTN = ?, AVAIL_QTY_TIE = ?, AVAIL_QTY_DOZ = ?, AVAIL_QTY_PCS = ?"
			+ " where ID = ?";
	
	@Override
	public void updateAvailableQuantities(Product product) {
		getJdbcTemplate().update(UPDATE_AVAILABLE_QUANTITIES_SQL,
				product.getUnitQuantity(Unit.CASE),
				product.getUnitQuantity(Unit.PACK),
				product.getUnitQuantity(Unit.TIES),
				product.getUnitQuantity(Unit.HDZN),
				product.getUnitQuantity(Unit.PIECES),
				product.getId());
	}

	@Override
	public void save(Product product) {
		if (product.getId() == null) {
			insert(product);
		} else {
			update(product);
		}
	}

	private static final String UPDATE_SQL =
			"update PRODUCT set CODE = ?, DESCRIPTION = ?,"
			+ " UOM_CODE = ?, UOM_CODE1 = ?, UOM_QTY = ?, UOM_QTY1 = ?, PRODUCT2_ID = ?"
			+ " where ID = ?";
	
	private void update(Product product) {
		getJdbcTemplate().update(UPDATE_SQL, 
				product.getCode(), 
				product.getDescription(),
				product.getUnits().get(0),
				product.getUnits().size() > 1 ? product.getUnits().get(1) : null,
                product.getUnitConversions().get(0).getQuantity(),
                product.getUnitConversions().size() > 1 ? product.getUnitConversions().get(1).getQuantity() : null,
                product.getProduct2Id(),
				product.getId());
	}

	private static final String INSERT_SQL =
			"insert into PRODUCT (CODE, DESCRIPTION, UOM_CODE, UOM_CODE1, UOM_QTY, UOM_QTY1, PRODUCT2_ID)"
			+ " values (?, ?, ?, ?, ?, ?, ?)";
	
	private void insert(final Product product) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, product.getCode());
				ps.setString(2, product.getDescription());
				ps.setString(3, product.getUnits().get(0));
                ps.setString(4, product.getUnits().size() > 1 ? product.getUnits().get(1) : null);
                ps.setInt(5, product.getUnitConversions().get(0).getQuantity());
                if (product.getUnitConversions().size() > 1) {
                    ps.setInt(6, product.getUnitConversions().get(1).getQuantity());
                } else {
                    ps.setNull(6, Types.NUMERIC);
                }
                ps.setLong(7, product.getProduct2Id());
				return ps;
			}
		}, holder);
		
		product.setId(holder.getKey().longValue());
	}

	@Override
	public List<Product> search(ProductSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		List<Object> params = new ArrayList<>();
		
		if (criteria.getActive() != null) {
			sql.append(" and ACTIVE_IND = ?");
			params.add(criteria.getActive() ? "Y" : "N");
		}
		
//		sql.append(" and b.PRICING_SCHEME_ID = ?");
//		params.add(criteria.getPricingScheme().getId());
		
		if (criteria.getCodeOrDescriptionLike() != null) {
			sql.append(" and (CODE like ? or DESCRIPTION like ?)");
			params.add(criteria.getCodeOrDescriptionLike() + "%");
			params.add("%" + criteria.getCodeOrDescriptionLike() + "%");
		}
		
		if (criteria.getManufacturer() != null) {
			sql.append(" and MANUFACTURER_ID = ?");
			params.add(criteria.getManufacturer().getId());
		}
		
		if (criteria.getCategory() != null) {
			sql.append(" and CATEGORY_ID = ?");
			params.add(criteria.getCategory().getId());
		}
		
		if (criteria.getSubcategory() != null) {
			sql.append(" and SUBCATEGORY_ID = ?");
			params.add(criteria.getSubcategory().getId());
		}
		
		if (criteria.getSupplier() != null) {
			sql.append(" and exists(select 1 from SUPPLIER_PRODUCT sp where sp.PRODUCT_ID = a.PRODUCT2_ID and sp.SUPPLIER_ID = ?)");
			params.add(criteria.getSupplier().getId());
		}
		
		if (criteria.getWithMoreThanTwoBarcodes() != null) {
			sql.append(" and a.PRODUCT2_ID is not null and a.PRODUCT2_ID in (select PRODUCT2_ID from PRODUCT where PRODUCT2_ID is not null group by PRODUCT2_ID having count(PRODUCT2_ID) > 2)");
		}
		
		sql.append(" order by a.DESCRIPTION");
		
		return getJdbcTemplate().query(sql.toString(), productRowMapper, params.toArray());
	}

	private static final String FIND_ALL_WITH_PRICING_SCHEME_SQL = BASE_SELECT_SQL +
			" and b.PRICING_SCHEME_ID = ? order by a.CODE";
	
	@Override
	public List<Product> findAllByPricingScheme(PricingScheme pricingScheme) {
		return getJdbcTemplate().query(FIND_ALL_WITH_PRICING_SCHEME_SQL, productRowMapper, pricingScheme.getId());
	}

	private static final String FIND_ALL_ACTIVE_BY_SUPPLIER_SQL = BASE_SELECT_SQL +
			" and a.ACTIVE_IND = 'Y'"
			+ " and exists(select 1 from SUPPLIER_PRODUCT sp where sp.PRODUCT_ID = a.ID and sp.SUPPLIER_ID = ?)"
			+ " and b.PRICING_SCHEME_ID = 1"
			+ " order by a.CODE";
			
	
	@Override
	public List<Product> findAllActiveBySupplier(Supplier supplier) {
		return getJdbcTemplate().query(FIND_ALL_ACTIVE_BY_SUPPLIER_SQL, productRowMapper, supplier.getId());
	}

	private static final String FIND_BY_ID_AND_PRICING_SCHEME_SQL = BASE_SELECT_SQL
			+ " and a.ID = ? and b.PRICING_SCHEME_ID = ?";
	
	@Override
	public Product findByIdAndPricingScheme(long id, PricingScheme pricingScheme) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_ID_AND_PRICING_SCHEME_SQL,
					productRowMapper, id, pricingScheme.getId());
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String FIND_BY_CODE_AND_PRICING_SCHEME_SQL = BASE_SELECT_SQL
			+ " and a.CODE = ? and b.PRICING_SCHEME_ID = ?";
	
	@Override
	public Product findByCodeAndPricingScheme(String code, PricingScheme pricingScheme) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_CODE_AND_PRICING_SCHEME_SQL,
					productRowMapper, code, pricingScheme.getId());
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String UPDATE_MAXIMUM_STOCK_LEVEL_SQL = 
			"update PRODUCT set MAX_STOCK_LEVEL = ? where ID = ?";
	
	@Override
	public void updateMaximumStockLevel(List<Product> products) {
		List<Object[]> params = new ArrayList<>();
		for (Product product : products) {
			params.add(new Object[] {product.getMaximumStockLevel(), product.getId()});
		}
		
		getJdbcTemplate().batchUpdate(UPDATE_MAXIMUM_STOCK_LEVEL_SQL, params);
	}

	private static final String GET_ALL_ACTIVE_PRODUCT_CODES_SQL = 
			"select CODE" + 
			" from PRODUCT a" + 
			" join PRODUCT2 b" + 
			"   on b.id = a.product2_id" + 
			" where b.ACTIVE_IND = 'Y'" + 
			" and (" + 
			"	(a.UOM_CODE = 'CASE' and b.ACTIVE_UNIT_IND_CASE = 'Y')" + 
			"	or" + 
			"	(a.UOM_CODE = 'TIES' and b.ACTIVE_UNIT_IND_TIES = 'Y')" + 
			"	or" + 
			"	(a.UOM_CODE = 'PACK' and b.ACTIVE_UNIT_IND_PACK = 'Y')" + 
			"	or" + 
			"	(a.UOM_CODE = 'HDZN' and b.ACTIVE_UNIT_IND_HDZN = 'Y')" + 
			"	or" + 
			"	(a.UOM_CODE = 'PCS' and b.ACTIVE_UNIT_IND_PCS = 'Y')" + 
			")";
	
	@Override
	public List<String> getAllActiveProductCodes() {
		return getJdbcTemplate().queryForList(GET_ALL_ACTIVE_PRODUCT_CODES_SQL, String.class);
	}
	
	private static final String UPDATE_ACTIVE_INDICATOR_SQL = "update PRODUCT set ACTIVE_IND = ? where CODE = ?";

	@Override
	public void updateActiveIndicator(String productCode, boolean active) {
		getJdbcTemplate().update(UPDATE_ACTIVE_INDICATOR_SQL, (active) ? "Y" : "N", productCode);
	}

	@Override
	public void subtractAvailableQuantity(Product product, int quantity) {
		getJdbcTemplate().update("update PRODUCT set AVAIL_QTY = AVAIL_QTY - ? where ID = ?", quantity, product.getId());
	}

	@Override
	public void addAvailableQuantity(Product product, int quantity) {
		getJdbcTemplate().update("update PRODUCT set AVAIL_QTY = AVAIL_QTY + ? where ID = ?", quantity, product.getId());
	}

	@Override
	public void removeProduct2Id(Long id) {
		getJdbcTemplate().update("update PRODUCT set PRODUCT2_ID = null where ID = ?", id);
	}

	private static final String BY_SPECIAL_CODE_BASE_SELECT_SQL =
			"select b.ID, a.CODE, c.CODE as CUSTOM_CODE, b.DESCRIPTION, a.UOM_CODE,"
			+ " b.UNIT_IND_CASE, b.UNIT_IND_TIES, b.UNIT_IND_PACK, b.UNIT_IND_HDZN, b.UNIT_IND_PCS,"
			+ " b.GROSS_COST_CASE, b.GROSS_COST_TIES, b.GROSS_COST_PACK, b.GROSS_COST_HDZN, b.GROSS_COST_PCS,"
			+ " b.FINAL_COST_CASE, b.FINAL_COST_TIES, b.FINAL_COST_PACK, b.FINAL_COST_HDZN, b.FINAL_COST_PCS"
			+ " from PRODUCT a"
			+ " join PRODUCT2 b"
			+ "   on b.ID = a.PRODUCT2_ID"
			+ " join PRODUCT_CUSTOM_CODE c"
			+ "   on c.PRODUCT_ID = b.ID";
	
	private static final String SEARCH_BY_SPECIAL_CODE_SQL = BY_SPECIAL_CODE_BASE_SELECT_SQL 
			+ " where c.CODE like ?"
			+ " and c.SUPPLIER_ID = ?"
			+ " order by b.DESCRIPTION";
	
	private RowMapper<ProductBySpecialCode> productBySpecialCodeRowMapper = new RowMapper<ProductBySpecialCode>() {

		@Override
		public ProductBySpecialCode mapRow(ResultSet rs, int rowNum) throws SQLException {
			Product product = new Product();
			product.setCode(rs.getString("CODE"));
			product.setUnits(Arrays.asList(rs.getString("UOM_CODE")));
			
			Product2 product2 = new Product2();
			product2.setId(rs.getLong("ID"));
			product2.setDescription(rs.getString("DESCRIPTION"));
			
			if ("Y".equals(rs.getString("UNIT_IND_CASE"))) {
				product2.getUnits().add(Unit.CASE);
				product2.getUnitCosts().add(new UnitCost(Unit.CASE, rs.getBigDecimal("GROSS_COST_CASE"), rs.getBigDecimal("FINAL_COST_CASE")));
			}
			if ("Y".equals(rs.getString("UNIT_IND_TIES"))) {
				product2.getUnits().add(Unit.TIES);
				product2.getUnitCosts().add(new UnitCost(Unit.TIES, rs.getBigDecimal("GROSS_COST_TIES"), rs.getBigDecimal("FINAL_COST_TIES")));
			}
			if ("Y".equals(rs.getString("UNIT_IND_PACK"))) {
				product2.getUnits().add(Unit.PACK);
				product2.getUnitCosts().add(new UnitCost(Unit.PACK, rs.getBigDecimal("GROSS_COST_PACK"), rs.getBigDecimal("FINAL_COST_PACK")));
			}
			if ("Y".equals(rs.getString("UNIT_IND_HDZN"))) {
				product2.getUnits().add(Unit.HDZN);
				product2.getUnitCosts().add(new UnitCost(Unit.HDZN, rs.getBigDecimal("GROSS_COST_HDZN"), rs.getBigDecimal("FINAL_COST_HDZN")));
			}
			if ("Y".equals(rs.getString("UNIT_IND_PCS"))) {
				product2.getUnits().add(Unit.PIECES);
				product2.getUnitCosts().add(new UnitCost(Unit.PIECES, rs.getBigDecimal("GROSS_COST_PCS"), rs.getBigDecimal("FINAL_COST_PCS")));
			}
			
			ProductCustomCode customCode = new ProductCustomCode();
			customCode.setCode(rs.getString("CUSTOM_CODE"));
			customCode.setProduct(product2);
			
			ProductBySpecialCode productBySpecialCode = new ProductBySpecialCode();
			productBySpecialCode.setProduct(product);
			productBySpecialCode.setCustomCode(customCode);
			
			return productBySpecialCode;
		}
	};
	
	@Override
	public List<ProductBySpecialCode> searchProductsBySpecialCode(String customCode, Supplier supplier) {
        return getJdbcTemplate().query(SEARCH_BY_SPECIAL_CODE_SQL, productBySpecialCodeRowMapper, "%" + customCode + "%", supplier.getId());
	}

	private static final String FIND_BY_SPECIAL_CODE_SQL = BY_SPECIAL_CODE_BASE_SELECT_SQL 
			+ " where a.CODE = ?"
			+ " and c.SUPPLIER_ID = ?";
	
	@Override
	public ProductBySpecialCode findProductBySpecialCode(String productCode, Supplier supplier) {
        try {
			return getJdbcTemplate().queryForObject(FIND_BY_SPECIAL_CODE_SQL, productBySpecialCodeRowMapper, productCode, supplier.getId());
		} catch (DataAccessException e) {
			return null;
		}
	}

}
