package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.ReceiveDelivery;

public interface ReceiveDeliveryRepository {

	List<ReceiveDelivery> findAllByPosted(boolean posted);

	void save(ReceiveDelivery receiveDelivery);

	ReceiveDelivery get(Long id);

	void delete(ReceiveDelivery receiveDelivery);
	
}
