package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PromoRedemptionRebateDao;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionRebate;

@Repository
public class PromoRedemptionRebateDaoImpl extends MagicDao implements PromoRedemptionRebateDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PROMO_REDEMPTION_ID, b.ID as PAYMENT_ADJUSTMENT_ID, PAYMENT_ADJUSTMENT_NO, b.AMOUNT"
			+ " from PROMO_REDEMPTION_REBATE a"
			+ " join PAYMENT_ADJUSTMENT b"
			+ "   on b.ID = a.PAYMENT_ADJUSTMENT_ID";
	
	private PromoRedemptionRebateRowMapper rewardRowMapper = new PromoRedemptionRebateRowMapper();
	
	private static final String INSERT_SQL =
			"insert into PROMO_REDEMPTION_REBATE"
			+ " (PROMO_REDEMPTION_ID, PAYMENT_ADJUSTMENT_ID) values (?, ?)";
	
	@Override
	public void save(final PromoRedemptionRebate rebate) {
		getJdbcTemplate().update(INSERT_SQL, 		
				rebate.getPromoRedemption().getId(),
				rebate.getPaymentAdjustment().getId());
	}

	private static final String FIND_ALL_BY_PROMO_REDEMPTION_SQL = BASE_SELECT_SQL
			+ " where a.PROMO_REDEMPTION_ID = ?";
	
	@Override
	public List<PromoRedemptionRebate> findAllByPromoRedemption(PromoRedemption promoRedemption) {
		List<PromoRedemptionRebate> rewards = getJdbcTemplate().query(FIND_ALL_BY_PROMO_REDEMPTION_SQL, 
				rewardRowMapper, promoRedemption.getId());
		for (PromoRedemptionRebate reward : rewards) {
			reward.setPromoRedemption(promoRedemption);
		}
		return rewards;
	}

	private class PromoRedemptionRebateRowMapper implements RowMapper<PromoRedemptionRebate> {

		@Override
		public PromoRedemptionRebate mapRow(ResultSet rs, int rowNum) throws SQLException {
			PromoRedemptionRebate rebate = new PromoRedemptionRebate();
			rebate.setId(rs.getLong("ID"));
			rebate.setPromoRedemption(new PromoRedemption(rs.getLong("PROMO_REDEMPTION_ID")));
			rebate.setPaymentAdjustment(mapPaymentAdjustment(rs));
			return rebate;
		}

		private PaymentAdjustment mapPaymentAdjustment(ResultSet rs) throws SQLException {
			PaymentAdjustment paymentAdjustment = new PaymentAdjustment();
			paymentAdjustment.setId(rs.getLong("PAYMENT_ADJUSTMENT_ID"));
			paymentAdjustment.setPaymentAdjustmentNumber(rs.getLong("PAYMENT_ADJUSTMENT_NO"));
			paymentAdjustment.setAmount(rs.getBigDecimal("AMOUNT"));
			return paymentAdjustment;
		}
		
	}
	
}