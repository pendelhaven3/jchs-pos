package com.pj.magic.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.impl.MagicDao;
import com.pj.magic.model.ReceiveDelivery;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.User;
import com.pj.magic.repository.ReceiveDeliveryRepository;

@Repository
public class ReceiveDeliveryRepositoryImpl extends MagicDao implements ReceiveDeliveryRepository {

	private static final String BASE_SELECT_SQL =
			"select a.ID, a.RECEIVE_DT, a.POSTED, a.POST_DT"
			+ ", a.SUPPLIER_ID, b.CODE as SUPPLIER_CODE, b.NAME as SUPPLIER_NAME, b.VAT_INCLUSIVE as SUPPLIER_VAT_INCLUSIVE"
			+ ", a.RECEIVE_BY, c.USERNAME as RECEIVE_BY_USERNAME"
			+ ", a.POST_BY, d.USERNAME as POST_BY_USERNAME"
			+ " from RECEIVE_DELIVERY a"
			+ " join SUPPLIER b"
			+ "   on b.ID = a.SUPPLIER_ID"
			+ " join USER c"
			+ "   on c.ID = a.RECEIVE_BY"
			+ " left join USER d"
			+ "   on d.ID = a.POST_BY"
			+ " where 1 = 1";
	
	private RowMapper<ReceiveDelivery> rowMapper = (rs, rownum) -> {
		Supplier supplier = new Supplier();
		supplier.setId(rs.getLong("SUPPLIER_ID"));
		supplier.setCode(rs.getString("SUPPLIER_CODE"));
		supplier.setName(rs.getString("SUPPLIER_NAME"));
		supplier.setVatInclusive("Y".equals(rs.getString("SUPPLIER_VAT_INCLUSIVE")));
		
		ReceiveDelivery receiveDelivery = new ReceiveDelivery();
		receiveDelivery.setId(rs.getLong("ID"));
		receiveDelivery.setSupplier(supplier);
		receiveDelivery.setReceiveDate(rs.getTimestamp("RECEIVE_DT"));
		receiveDelivery.setReceivedBy(new User(rs.getLong("RECEIVE_BY"), rs.getString("RECEIVE_BY_USERNAME")));
		receiveDelivery.setPosted(rs.getBoolean("POSTED"));
		receiveDelivery.setPostDate(rs.getTimestamp("POST_DT"));
		
		String postBy = rs.getString("POST_BY");
		if (!StringUtils.isEmpty(postBy)) {
			receiveDelivery.setPostedBy(new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
		}
		
		return receiveDelivery;
	};
	
	private static final String FIND_ALL_BY_POSTED_SQL = BASE_SELECT_SQL +
			" and a.posted = ? order by receive_dt desc";
	
	@Override
	public List<ReceiveDelivery> findAllByPosted(boolean posted) {
		return getJdbcTemplate().query(FIND_ALL_BY_POSTED_SQL, new Object[] {posted}, rowMapper);
	}

	@Override
	public void save(ReceiveDelivery receiveDelivery) {
		if (receiveDelivery.getId() == null) {
			insert(receiveDelivery);
		} else {
			update(receiveDelivery);
		}
	}
	
	private static final String INSERT_SQL =
			"insert into RECEIVE_DELIVERY"
			+ " (SUPPLIER_ID, RECEIVE_DT, RECEIVE_BY)"
			+ " values"
			+ " (?, ?, ?)";
	
	private void insert(final ReceiveDelivery receiveDelivery) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, receiveDelivery.getSupplier().getId());
				ps.setTimestamp(2, new Timestamp(receiveDelivery.getReceiveDate().getTime()));
				ps.setLong(3, receiveDelivery.getReceivedBy().getId());
				return ps;
			}
		}, holder);
		
		receiveDelivery.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL =
			"update RECEIVE_DELIVERY set SUPPLIER_ID = ?, POSTED = ?, POST_DT = ?, POST_BY = ? where ID = ?";
	
	private void update(ReceiveDelivery receiveDelivery) {
		getJdbcTemplate().update(UPDATE_SQL, 
				receiveDelivery.getSupplier().getId(),
				receiveDelivery.isPosted(),
				receiveDelivery.isPosted() ? receiveDelivery.getPostDate() : null,
				receiveDelivery.isPosted() ? receiveDelivery.getPostedBy().getId() : null,
				receiveDelivery.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " and a.ID = ?";
	
	@Override
	public ReceiveDelivery get(Long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, new Object[] {id}, rowMapper);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String DELETE_SQL = "delete from RECEIVE_DELIVERY where ID = ?";
	
	@Override
	public void delete(ReceiveDelivery receiveDelivery) {
		getJdbcTemplate().update(DELETE_SQL, receiveDelivery.getId());
	}
	
}
