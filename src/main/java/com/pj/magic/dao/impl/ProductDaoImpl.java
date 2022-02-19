package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

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
import com.pj.magic.model.Supplier;
import com.pj.magic.model.Unit;
import com.pj.magic.model.UnitConversion;
import com.pj.magic.model.UnitCost;
import com.pj.magic.model.UnitPrice;
import com.pj.magic.model.UnitQuantity;
import com.pj.magic.model.search.ProductSearchCriteria;

@Repository
public class ProductDaoImpl extends MagicDao implements ProductDao {
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, a.DESCRIPTION,"
			+ " a.SKU_CASE, a.SKU_TIES, a.SKU_PACK, a.SKU_HDZN, a.SKU_PCS,"
			+ " UNIT_IND_CASE, UNIT_IND_TIES, UNIT_IND_PACK, UNIT_IND_HDZN, UNIT_IND_PCS,"
			+ " ACTIVE_UNIT_IND_CASE, ACTIVE_UNIT_IND_TIES, ACTIVE_UNIT_IND_PACK, ACTIVE_UNIT_IND_HDZN, ACTIVE_UNIT_IND_PCS,"
			+ " 0 as UNIT_PRICE_CASE, 0 as UNIT_PRICE_TIES, 0 as UNIT_PRICE_PACK, 0 as UNIT_PRICE_HDZN, 0 as UNIT_PRICE_PCS,"
			+ " AVAIL_QTY_CASE, AVAIL_QTY_TIES, AVAIL_QTY_PACK, AVAIL_QTY_HDZN, AVAIL_QTY_PCS,"
			+ " UNIT_CONV_CASE, UNIT_CONV_TIES, UNIT_CONV_PACK, UNIT_CONV_HDZN, UNIT_CONV_PCS,"
			+ " GROSS_COST_CASE, GROSS_COST_TIES, GROSS_COST_PACK, GROSS_COST_HDZN, GROSS_COST_PCS,"
			+ " FINAL_COST_CASE, FINAL_COST_TIES, FINAL_COST_PACK, FINAL_COST_HDZN, FINAL_COST_PCS,"
			+ " MAX_STOCK_LEVEL, MIN_STOCK_LEVEL,"
			+ " a.ACTIVE_IND"
			+ " from PRODUCT a"
			+ " where 1 = 1";
	
	private RowMapper<Product> productRowMapper = new RowMapper<Product>() {

		@Override
		public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
			Product product = new Product();
			product.setId(rs.getLong("ID"));
//            product.setCode(rs.getString("CODE"));
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
			
			product.setSkuCase(rs.getString("SKU_CASE"));
			product.setSkuTies(rs.getString("SKU_TIES"));
			product.setSkuPack(rs.getString("SKU_PACK"));
			product.setSkuHdzn(rs.getString("SKU_HDZN"));
			product.setSkuPieces(rs.getString("SKU_PCS"));
			
			return product;
		}
		
	};
	
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
			+ " UOM_CODE = ?, UOM_CODE1 = ?, UOM_QTY = ?, UOM_QTY1 = ?, MAX_STOCK_LEVEL = ?, MIN_STOCK_LEVEL = ?, ACTIVE_IND = ?"
			+ " where ID = ?";
	
	private void update(Product product) {
		getJdbcTemplate().update(UPDATE_SQL, 
				product.getCode(), 
				product.getDescription(),
				product.getUnits().get(0),
				product.getUnits().size() > 1 ? product.getUnits().get(1) : null,
                product.getUnitConversions().get(0).getQuantity(),
                product.getUnitConversions().size() > 1 ? product.getUnitConversions().get(1).getQuantity() : null,
                product.getMaximumStockLevel(),
                product.getMinimumStockLevel(),
				product.isActive() ? "Y" : "N",
				product.getId());
	}

	private static final String INSERT_SQL =
			"insert into PRODUCT (CODE, DESCRIPTION, UOM_CODE, UOM_CODE1, UOM_QTY, UOM_QTY1)"
			+ " values (?, ?, ?, ?, ?, ?)";
	
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
			sql.append(" and exists(select 1 from SUPPLIER_PRODUCT sp where sp.PRODUCT_ID = a.ID and sp.SUPPLIER_ID = ?)");
			params.add(criteria.getSupplier().getId());
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

	private static final String UPDATE_COSTS_SQL =
			"update PRODUCT set GROSS_COST = ?, GROSS_COST1 = ?, FINAL_COST = ?, FINAL_COST1 = ? where ID = ?"; 
	
	@Override
	public void updateCosts(Product product) {
	    UnitCost unitCost1 = product.getUnitCosts().get(0);
        UnitCost unitCost2 = product.getUnitCosts().size() > 1 ? product.getUnitCosts().get(1) : null;
	    
		getJdbcTemplate().update(UPDATE_COSTS_SQL,
				unitCost1.getGrossCost(),
				(unitCost2 != null) ? unitCost2.getGrossCost() : null,
                unitCost1.getFinalCost(),
                (unitCost2 != null) ? unitCost2.getFinalCost() : null,
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
			"select a.CODE from PRODUCT_SKU a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID"
			+ " where b.ACTIVE_IND = 'Y'";

	@Override
	public List<String> getAllActiveProductCodes() {
		return getJdbcTemplate().queryForList(GET_ALL_ACTIVE_PRODUCT_CODES_SQL, String.class);
	}

	@Override
	public void updateActiveIndicator(String productCode, boolean active) {
		getJdbcTemplate().update("update PRODUCT set ACTIVE_IND = ? where CODE = ?", active ? "Y" : "N", productCode);
	}

	@Override
	public void subtractAvailableQuantity(Product product, int quantity) {
		getJdbcTemplate().update("update PRODUCT set AVAIL_QTY = AVAIL_QTY - ? where ID = ?", quantity, product.getId());
	}

	@Override
	public void addAvailableQuantity(Product product, int quantity) {
		getJdbcTemplate().update("update PRODUCT set AVAIL_QTY = AVAIL_QTY + ? where ID = ?", quantity, product.getId());
	}

}
