package com.pj.magic.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.impl.MagicDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.ReceiveDelivery;
import com.pj.magic.model.ReceiveDeliveryItem;
import com.pj.magic.repository.ReceiveDeliveryItemRepository;

@Repository
public class ReceiveDeliveryItemRepositoryImpl extends MagicDao implements ReceiveDeliveryItemRepository {

	private static final String BASE_SELECT_SQL =
			"select a.ID, a.RECEIVE_DELIVERY_ID, a.CODE, a.UNIT, a.QUANTITY"
			+ " , b.DESCRIPTION as PRODUCT_DESCRIPTION"
			+ " from RECEIVE_DELIVERY_ITEM a"
			+ " left join PRODUCT b"
			+ "   on b.CODE = a.CODE"
			+ " where 1 = 1";
	
	private RowMapper<ReceiveDeliveryItem> rowMapper = (rs, rownum) -> {
		ReceiveDeliveryItem item = new ReceiveDeliveryItem();
		item.setId(rs.getLong("ID"));
		item.setParent(new ReceiveDelivery(rs.getLong("RECEIVE_DELIVERY_ID")));
		item.setCode(rs.getString("CODE"));
		item.setUnit(rs.getString("UNIT"));
		item.setQuantity(rs.getInt("QUANTITY"));
		
		String description = rs.getString("PRODUCT_DESCRIPTION");
		if (!StringUtils.isEmpty(description)) {
			Product product = new Product();
			product.setCode(item.getCode());
			product.setDescription(description);
			item.setProduct(product);
		}
		
		return item;
	};

	@Override
	public void save(ReceiveDeliveryItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL =
			"insert into RECEIVE_DELIVERY_ITEM"
			+ " (RECEIVE_DELIVERY_ID, CODE, UNIT, QUANTITY)"
			+ " values (?, ?, ?, ?)";
	
	private void insert(final ReceiveDeliveryItem item) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, item.getParent().getId());
				ps.setString(2, item.getCode());
				ps.setString(3, item.getUnit());
				ps.setInt(4, item.getQuantity());
				return ps;
			}
		}, holder);
		
		item.setId(holder.getKey().longValue());
	}
	
	private static final String UPDATE_SQL =
			"update RECEIVE_DELIVERY_ITEM"
			+ " set QUANTITY = ?"
			+ " where ID = ?";
	
	private void update(ReceiveDeliveryItem item) {
		getJdbcTemplate().update(UPDATE_SQL,
				item.getQuantity(),
				item.getId());
	}
	
	private static final String FIND_ALL_BY_RECEIVE_DELIVERY_SQL = BASE_SELECT_SQL +
			" and a.RECEIVE_DELIVERY_ID = ? order by a.CODE, a.UNIT";

	@Override
	public List<ReceiveDeliveryItem> findAllByReceiveDelivery(ReceiveDelivery receiveDelivery) {
		List<ReceiveDeliveryItem> items = getJdbcTemplate().query(FIND_ALL_BY_RECEIVE_DELIVERY_SQL, 
				rowMapper, receiveDelivery.getId());
		for (ReceiveDeliveryItem item : items) {
			item.setParent(receiveDelivery);
		}
		return items;
	}

	private static final String DELETE_SQL = "delete from RECEIVE_DELIVERY_ITEM where ID = ?";
	
	@Override
	public void delete(ReceiveDeliveryItem item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}

	private static final String DELETE_ALL_BY_RECEIVE_DELIVERY_SQL =
			"delete from RECEIVE_DELIVERY_ITEM where RECEIVE_DELIVERY_ID = ?";
	
	@Override
	public void deleteAllByReceiveDelivery(ReceiveDelivery receiveDelivery) {
		getJdbcTemplate().update(DELETE_ALL_BY_RECEIVE_DELIVERY_SQL, receiveDelivery.getId());
	}

}
