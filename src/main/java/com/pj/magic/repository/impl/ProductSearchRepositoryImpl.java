package com.pj.magic.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.impl.MagicDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;
import com.pj.magic.model.UnitConversion;
import com.pj.magic.model.UnitCost;
import com.pj.magic.model.UnitPrice;
import com.pj.magic.model.UnitQuantity;
import com.pj.magic.model.search.ProductSearchCriteria;
import com.pj.magic.repository.ProductSearchRepository;

@Repository
public class ProductSearchRepositoryImpl extends MagicDao implements ProductSearchRepository {

	private static final String BASE_SELECT_SQL =
			"select a.ID, b.CODE, a.DESCRIPTION, b.UOM_CODE,"
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
			+ " join PRODUCT b"
			+ "     on b.PRODUCT2_ID = a.ID"
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
			product.setCode(rs.getString("CODE"));
			product.setDescription(rs.getString("DESCRIPTION"));
			product.getUnits().add(rs.getString("UOM_CODE"));
			
			return product;
		}
		
	};
	
	@Override
	public List<Product> search(ProductSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		List<Object> params = new ArrayList<>();
		
		if (criteria.getActive() != null) {
			sql.append(" and a.ACTIVE_IND = ?");
			params.add(criteria.getActive() ? "Y" : "N");
		}
		
//		sql.append(" and b.PRICING_SCHEME_ID = ?");
//		params.add(criteria.getPricingScheme().getId());
		
		if (criteria.getCodeOrDescriptionLike() != null) {
			sql.append(" and (b.CODE like ? or a.DESCRIPTION like ?)");
			params.add(criteria.getCodeOrDescriptionLike() + "%");
			params.add("%" + criteria.getCodeOrDescriptionLike() + "%");
		}
		
//		if (criteria.getManufacturer() != null) {
//			sql.append(" and MANUFACTURER_ID = ?");
//			params.add(criteria.getManufacturer().getId());
//		}
//		
//		if (criteria.getCategory() != null) {
//			sql.append(" and CATEGORY_ID = ?");
//			params.add(criteria.getCategory().getId());
//		}
//		
//		if (criteria.getSubcategory() != null) {
//			sql.append(" and SUBCATEGORY_ID = ?");
//			params.add(criteria.getSubcategory().getId());
//		}
		
		if (criteria.getSupplier() != null) {
			sql.append(" and exists(select 1 from SUPPLIER_PRODUCT sp where sp.PRODUCT_ID = a.ID and sp.SUPPLIER_ID = ?)");
			params.add(criteria.getSupplier().getId());
		}
		
		sql.append(" order by a.DESCRIPTION");
		
		return getJdbcTemplate().query(sql.toString(), rowMapper, params.toArray());
	}

	/*
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
