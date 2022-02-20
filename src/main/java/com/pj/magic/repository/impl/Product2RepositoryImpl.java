package com.pj.magic.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.impl.MagicDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;
import com.pj.magic.model.UnitConversion;
import com.pj.magic.model.UnitCost;
import com.pj.magic.model.UnitPrice;
import com.pj.magic.model.UnitQuantity;
import com.pj.magic.model.UnitSku;
import com.pj.magic.repository.Product2Repository;

@Repository
public class Product2RepositoryImpl extends MagicDao implements Product2Repository {

	private static final String BASE_SELECT_SQL =
			"select a.ID, DESCRIPTION, MAX_STOCK_LEVEL, MIN_STOCK_LEVEL, ACTIVE_IND,"
			+ " UNIT_IND_CASE, UNIT_IND_TIES, UNIT_IND_PACK, UNIT_IND_HDZN, UNIT_IND_PCS,"
			+ " ACTIVE_UNIT_IND_CASE, ACTIVE_UNIT_IND_TIES, ACTIVE_UNIT_IND_PACK, ACTIVE_UNIT_IND_HDZN, ACTIVE_UNIT_IND_PCS,"
			+ " 0 as UNIT_PRICE_CASE, 0 as UNIT_PRICE_TIES, 0 as UNIT_PRICE_PACK, 0 as UNIT_PRICE_HDZN, 0 as UNIT_PRICE_PCS,"
			+ " AVAIL_QTY_CASE, AVAIL_QTY_TIES, AVAIL_QTY_PACK, AVAIL_QTY_HDZN, AVAIL_QTY_PCS,"
			+ " UNIT_CONV_CASE, UNIT_CONV_TIES, UNIT_CONV_PACK, UNIT_CONV_HDZN, UNIT_CONV_PCS,"
			+ " GROSS_COST_CASE, GROSS_COST_TIES, GROSS_COST_PACK, GROSS_COST_HDZN, GROSS_COST_PCS,"
			+ " FINAL_COST_CASE, FINAL_COST_TIES, FINAL_COST_PACK, FINAL_COST_HDZN, FINAL_COST_PCS"
//			+ " COMPANY_LIST_PRICE,"
//			+ " MANUFACTURER_ID, c.NAME as MANUFACTURER_NAME,"
//			+ " CATEGORY_ID, d.NAME as CATEGORY_NAME,"
//			+ " SUBCATEGORY_ID, e.NAME as SUBCATEGORY_NAME"
			+ " from PRODUCT2 a"
//			+ " join PRODUCT_PRICE b"
//			+ " 	on b.PRODUCT_ID = a.ID"
//			+ " left join MANUFACTURER c"
//			+ "		on c.ID = a.MANUFACTURER_ID"
//			+ " left join PRODUCT_CATEGORY d"
//			+ "		on d.ID = a.CATEGORY_ID"
//			+ " left join PRODUCT_SUBCATEGORY e"
//			+ "		on e.ID = a.SUBCATEGORY_ID"
			+ " where 1 = 1";
	
	private RowMapper<Product> rowMapper = new RowMapper<Product>() {

		@Override
		public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
			Product product = new Product();
			product.setId(rs.getLong("ID"));
			product.setDescription(rs.getString("DESCRIPTION"));
			product.setMaximumStockLevel(rs.getInt("MAX_STOCK_LEVEL"));
			product.setMinimumStockLevel(rs.getInt("MIN_STOCK_LEVEL"));
			product.setActive("Y".equals(rs.getString("ACTIVE_IND")));
			
			if ("Y".equals(rs.getString("UNIT_IND_CASE"))) {
				product.getUnits().add(Unit.CASE);
				product.getUnitPrices().add(new UnitPrice(Unit.CASE, rs.getBigDecimal("UNIT_PRICE_CASE")));
				product.getUnitQuantities().add(new UnitQuantity(Unit.CASE, rs.getInt("AVAIL_QTY_CASE")));
				product.getUnitConversions().add(new UnitConversion(Unit.CASE, rs.getInt("UNIT_CONV_CASE")));
				product.getUnitCosts().add(
						new UnitCost(Unit.CASE, rs.getBigDecimal("GROSS_COST_CASE"), rs.getBigDecimal("FINAL_COST_CASE")));
				if ("Y".equals(rs.getString("ACTIVE_UNIT_IND_CASE"))) {
					product.getActiveUnits().add(Unit.CASE);
				}
			}
			if ("Y".equals(rs.getString("UNIT_IND_TIES"))) {
				product.getUnits().add(Unit.TIES);
				product.getUnitPrices().add(new UnitPrice(Unit.TIES, rs.getBigDecimal("UNIT_PRICE_TIES")));
				product.getUnitQuantities().add(new UnitQuantity(Unit.TIES, rs.getInt("AVAIL_QTY_TIES")));
				product.getUnitConversions().add(new UnitConversion(Unit.TIES, rs.getInt("UNIT_CONV_TIES")));
				product.getUnitCosts().add(
						new UnitCost(Unit.TIES, rs.getBigDecimal("GROSS_COST_TIES"), rs.getBigDecimal("FINAL_COST_TIES")));
				if ("Y".equals(rs.getString("ACTIVE_UNIT_IND_TIES"))) {
					product.getActiveUnits().add(Unit.TIES);
				}
			}
			if ("Y".equals(rs.getString("UNIT_IND_PACK"))) {
				product.getUnits().add(Unit.PACK);
				product.getUnitPrices().add(new UnitPrice(Unit.PACK, rs.getBigDecimal("UNIT_PRICE_PACK")));
				product.getUnitQuantities().add(new UnitQuantity(Unit.PACK, rs.getInt("AVAIL_QTY_PACK")));
				product.getUnitConversions().add(new UnitConversion(Unit.PACK, rs.getInt("UNIT_CONV_PACK")));
				product.getUnitCosts().add(
						new UnitCost(Unit.PACK, rs.getBigDecimal("GROSS_COST_PACK"), rs.getBigDecimal("FINAL_COST_PACK")));
				if ("Y".equals(rs.getString("ACTIVE_UNIT_IND_PACK"))) {
					product.getActiveUnits().add(Unit.PACK);
				}
			}
			if ("Y".equals(rs.getString("UNIT_IND_HDZN"))) {
				product.getUnits().add(Unit.HDZN);
				product.getUnitPrices().add(new UnitPrice(Unit.HDZN, rs.getBigDecimal("UNIT_PRICE_HDZN")));
				product.getUnitQuantities().add(new UnitQuantity(Unit.HDZN, rs.getInt("AVAIL_QTY_HDZN")));
				product.getUnitConversions().add(new UnitConversion(Unit.HDZN, rs.getInt("UNIT_CONV_HDZN")));
				product.getUnitCosts().add(
						new UnitCost(Unit.HDZN, rs.getBigDecimal("GROSS_COST_HDZN"), rs.getBigDecimal("FINAL_COST_HDZN")));
				if ("Y".equals(rs.getString("ACTIVE_UNIT_IND_HDZN"))) {
					product.getActiveUnits().add(Unit.HDZN);
				}
			}
			if ("Y".equals(rs.getString("UNIT_IND_PCS"))) {
				product.getUnits().add(Unit.PIECES);
				product.getUnitPrices().add(new UnitPrice(Unit.PIECES, rs.getBigDecimal("UNIT_PRICE_PCS")));
				product.getUnitQuantities().add(new UnitQuantity(Unit.PIECES, rs.getInt("AVAIL_QTY_PCS")));
				product.getUnitConversions().add(new UnitConversion(Unit.PIECES, rs.getInt("UNIT_CONV_PCS")));
				product.getUnitCosts().add(
						new UnitCost(Unit.PIECES, rs.getBigDecimal("GROSS_COST_PCS"), rs.getBigDecimal("FINAL_COST_PCS")));
				if ("Y".equals(rs.getString("ACTIVE_UNIT_IND_PCS"))) {
					product.getActiveUnits().add(Unit.PIECES);
				}
			}
			
			/*
			if (rs.getLong("MANUFACTURER_ID") != 0) {
				Manufacturer manufacturer = new Manufacturer();
				manufacturer.setId(rs.getLong("MANUFACTURER_ID"));
				manufacturer.setName(rs.getString("MANUFACTURER_NAME"));
				product.setManufacturer(manufacturer);
			}
			
			if (rs.getLong("CATEGORY_ID") != 0) {
				ProductCategory category = new ProductCategory();
				category.setId(rs.getLong("CATEGORY_ID"));
				category.setName(rs.getString("CATEGORY_NAME"));
				product.setCategory(category);
			}
			
			if (rs.getLong("SUBCATEGORY_ID") != 0) {
				ProductSubcategory subcategory = new ProductSubcategory();
				subcategory.setId(rs.getLong("SUBCATEGORY_ID"));
				subcategory.setName(rs.getString("SUBCATEGORY_NAME"));
				product.setSubcategory(subcategory);
			}

			product.setCompanyListPrice(rs.getBigDecimal("COMPANY_LIST_PRICE"));
			*/
			
			return product;
		}
		
	};
	
//	@Override
//	public List<Product> getAll() {
//		return findAllByPricingScheme(new PricingScheme(Constants.CANVASSER_PRICING_SCHEME_ID));
//	}
//
//	@Override
//	public Product findByCode(String code) {
//		return findByCodeAndPricingScheme(code, new PricingScheme(Constants.CANVASSER_PRICING_SCHEME_ID));
//	}
//	
//	private static final String UPDATE_AVAILABLE_QUANTITIES_SQL =
//			"update PRODUCT"
//			+ " set AVAIL_QTY_CASE = ?, AVAIL_QTY_PACK = ?, AVAIL_QTY_TIE = ?, AVAIL_QTY_HDZN = ?, AVAIL_QTY_PCS = ?"
//			+ " where ID = ?";
//	
//	@Override
//	public void updateAvailableQuantities(Product product) {
//		getJdbcTemplate().update(UPDATE_AVAILABLE_QUANTITIES_SQL,
//				product.getUnitQuantity(Unit.CASE),
//				product.getUnitQuantity(Unit.PACK),
//				product.getUnitQuantity(Unit.TIE),
//				product.getUnitQuantity(Unit.HDZN),
//				product.getUnitQuantity(Unit.PIECES),
//				product.getId());
//	}

	private static final String INSERT_FROM_TRISYS_SQL =
			"insert into PRODUCT2 (DESCRIPTION,"
			+ " UNIT_IND_CASE, UNIT_IND_TIES, UNIT_IND_PACK, UNIT_IND_HDZN, UNIT_IND_PCS,"
			+ " UNIT_CONV_CASE, UNIT_CONV_TIES, UNIT_CONV_PACK, UNIT_CONV_HDZN, UNIT_CONV_PCS,"
			+ " ACTIVE_UNIT_IND_CASE, ACTIVE_UNIT_IND_TIES, ACTIVE_UNIT_IND_PACK, ACTIVE_UNIT_IND_HDZN, ACTIVE_UNIT_IND_PCS)"
			+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	@Override
	public Long createFromTrisys(final Product product) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_FROM_TRISYS_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, product.getDescription());
				ps.setString(2, product.hasUnit(Unit.CASE) ? "Y" : "N");
				ps.setString(3, product.hasUnit(Unit.TIES) ? "Y" : "N");
				ps.setString(4, product.hasUnit(Unit.PACK) ? "Y" : "N");
				ps.setString(5, product.hasUnit(Unit.HDZN) ? "Y" : "N");
				ps.setString(6, product.hasUnit(Unit.PIECES) ? "Y" : "N");
				ps.setInt(7, product.getUnitConversion(Unit.CASE));
				ps.setInt(8, product.getUnitConversion(Unit.TIES));
				ps.setInt(9, product.getUnitConversion(Unit.PACK));
				ps.setInt(10, product.getUnitConversion(Unit.HDZN));
				ps.setInt(11, product.getUnitConversion(Unit.PIECES));
				ps.setString(12, product.hasActiveUnit(Unit.CASE) ? "Y" : "N");
				ps.setString(13, product.hasActiveUnit(Unit.TIES) ? "Y" : "N");
				ps.setString(14, product.hasActiveUnit(Unit.PACK) ? "Y" : "N");
				ps.setString(15, product.hasActiveUnit(Unit.HDZN) ? "Y" : "N");
				ps.setString(16, product.hasActiveUnit(Unit.PIECES) ? "Y" : "N");
				return ps;
			}
		}, holder);
		
		return holder.getKey().longValue();
	}

	private static final String FIND_BY_DESCRIPTION_SQL = BASE_SELECT_SQL + " and a.DESCRIPTION = ?";
	
	@Override
	public Product findByDescription(String description) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_DESCRIPTION_SQL, rowMapper, description);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String UPDATE_FROM_TRISYS_SQL =
			"update PRODUCT2 set DESCRIPTION = ?, ACTIVE_IND = ?,"
			+ " UNIT_IND_CASE = ?, UNIT_CONV_CASE = ?,"
			+ " UNIT_IND_TIES = ?, UNIT_CONV_TIES = ?,"
			+ " UNIT_IND_PACK = ?, UNIT_CONV_PACK = ?,"
			+ " UNIT_IND_HDZN = ?, UNIT_CONV_HDZN = ?,"
			+ " UNIT_IND_PCS = ?, UNIT_CONV_PCS = ?,"
			+ " ACTIVE_UNIT_IND_CASE = ?, ACTIVE_UNIT_IND_TIES = ?, ACTIVE_UNIT_IND_PACK = ?, ACTIVE_UNIT_IND_HDZN = ?, ACTIVE_UNIT_IND_PCS = ?"
			+ " where ID = ?";
	
	@Override
	public void updateFromTrisys(Product product) {
		getJdbcTemplate().update(UPDATE_FROM_TRISYS_SQL, 
				product.getDescription(),
				product.isActive() ? "Y" : "N",
				product.hasUnit(Unit.CASE) ? "Y" : "N",
				product.getUnitConversion(Unit.CASE),
				product.hasUnit(Unit.TIES) ? "Y" : "N",
				product.getUnitConversion(Unit.TIES),
				product.hasUnit(Unit.PACK) ? "Y" : "N",
				product.getUnitConversion(Unit.PACK),
				product.hasUnit(Unit.HDZN) ? "Y" : "N",
				product.getUnitConversion(Unit.HDZN),
				product.hasUnit(Unit.PIECES) ? "Y" : "N",
				product.getUnitConversion(Unit.PIECES),
				product.hasActiveUnit(Unit.CASE) ? "Y" : "N",
				product.hasActiveUnit(Unit.TIES) ? "Y" : "N",
				product.hasActiveUnit(Unit.PACK) ? "Y" : "N",
				product.hasActiveUnit(Unit.HDZN) ? "Y" : "N",
				product.hasActiveUnit(Unit.PIECES) ? "Y" : "N",
				product.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " and a.ID = ?";
	
	@Override
	public Product get(Long id) {
		return getJdbcTemplate().queryForObject(GET_SQL, rowMapper, id);
	}

	private static final String UPDATE_SQL =
			"update PRODUCT2 set MAX_STOCK_LEVEL = ?, MIN_STOCK_LEVEL = ?, ACTIVE_IND = ?,"
			+ " ACTIVE_UNIT_IND_CASE = ?, ACTIVE_UNIT_IND_TIES = ?, ACTIVE_UNIT_IND_PACK = ?, ACTIVE_UNIT_IND_HDZN = ?, ACTIVE_UNIT_IND_PCS = ?"
			+ " where ID = ?";
	
	@Override
	public void update(Product product) {
		getJdbcTemplate().update(UPDATE_SQL, 
				product.getMaximumStockLevel(),
				product.getMinimumStockLevel(),
				product.isActive() ? "Y" : "N",
				product.hasActiveUnit(Unit.CASE) ? "Y" : "N",
				product.hasActiveUnit(Unit.TIES) ? "Y" : "N",
				product.hasActiveUnit(Unit.PACK) ? "Y" : "N",
				product.hasActiveUnit(Unit.HDZN) ? "Y" : "N",
				product.hasActiveUnit(Unit.PIECES) ? "Y" : "N",
				product.getId());
	}

	private static final String GET_UNIT_SKUS_SQL = "select CODE, UOM_CODE from PRODUCT where PRODUCT2_ID = ?";
	
	private RowMapper<UnitSku> unitSkuRowMapper = new RowMapper<UnitSku>() {

		@Override
		public UnitSku mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new UnitSku(rs.getString("UOM_CODE"), rs.getString("CODE"));
		}
		
	};
	
	@Override
	public List<UnitSku> getUnitSkus(Product product) {
		return getJdbcTemplate().query(GET_UNIT_SKUS_SQL, unitSkuRowMapper, product.getId());
	}

	/*
	@Override
	public List<Product> search(ProductSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		List<Object> params = new ArrayList<>();
		
		if (criteria.getActive() != null) {
			sql.append(" and ACTIVE_IND = ?");
			params.add(criteria.getActive() ? "Y" : "N");
		}
		
		sql.append(" and b.PRICING_SCHEME_ID = ?");
		params.add(criteria.getPricingScheme().getId());
		
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
			sql.append(" and exists(select 1 from SUPPLIER_PRODUCT sp where sp.PRODUCT_ID = a.ID and sp.SUPPLIER_ID = ?)");
			params.add(criteria.getSupplier().getId());
		}
		
		sql.append(" order by a.CODE");
		
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

	private static final String UPDATE_COSTS_SQL =
			"update PRODUCT"
			+ " set GROSS_COST_CASE = ?, GROSS_COST_TIE = ?, GROSS_COST_PACK = ?, "
			+ " GROSS_COST_HDZN = ?, GROSS_COST_PCS = ?, FINAL_COST_CASE = ?, "
			+ " FINAL_COST_TIE = ?, FINAL_COST_PACK = ?, FINAL_COST_HDZN = ?, "
			+ " FINAL_COST_PCS = ?"
			+ " where ID = ?"; 
	
	@Override
	public void updateCosts(Product product) {
		getJdbcTemplate().update(UPDATE_COSTS_SQL,
				product.getGrossCost(Unit.CASE),
				product.getGrossCost(Unit.TIES),
				product.getGrossCost(Unit.PACK),
				product.getGrossCost(Unit.HDZN),
				product.getGrossCost(Unit.PIECES),
				product.getFinalCost(Unit.CASE),
				product.getFinalCost(Unit.TIES),
				product.getFinalCost(Unit.PACK),
				product.getFinalCost(Unit.HDZN),
				product.getFinalCost(Unit.PIECES),
				product.getId());
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

	private static final String DELETE_SQL = "delete from PRODUCT where ID = ?";
	
	@Override
	public void delete(Product product) {
		getJdbcTemplate().update(DELETE_SQL, product.getId());
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

	private static final String UPDATE_COMPANY_LIST_PRICE_SQL =
	        "update PRODUCT set COMPANY_LIST_PRICE = ? where ID = ?";
	
    @Override
    public void updateCompanyListPrice(Product product, BigDecimal companyListPrice) {
        getJdbcTemplate().update(UPDATE_COMPANY_LIST_PRICE_SQL, companyListPrice, product.getId());
    }
    */

}
