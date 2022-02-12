package com.pj.magic.dao.impl;

import java.util.Date;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.SystemDao;

@Repository
public class SystemDaoImpl extends MagicDao implements SystemDao {

	private static final String GET_CURRENT_DATE_SQL = "select current_timestamp()";
	
	@Override
	public Date getCurrentDateTime() {
		return getJdbcTemplate().queryForObject(GET_CURRENT_DATE_SQL, Date.class);
	}

	private static final String SYNCH_SUPPLIERS_SQL =
			"insert into supplier_product" + 
			" select c.supplier_id, a.id" + 
			" from product a" + 
			" join product b" + 
			"   on a.code = concat(b.code, '01')" + 
			" join supplier_product c" + 
			"   on c.product_id = b.id" + 
			" where not exists (" + 
			"   select 1 from supplier_product d" + 
			"   where d.product_id = a.id" + 
			"   and d.supplier_id = c.supplier_id" + 
			" )";
	
	@Override
	public void synchSuppliers() {
		getJdbcTemplate().update(SYNCH_SUPPLIERS_SQL);
	}

}
