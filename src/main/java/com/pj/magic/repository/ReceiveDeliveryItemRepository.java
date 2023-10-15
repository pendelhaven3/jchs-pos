package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.ReceiveDelivery;
import com.pj.magic.model.ReceiveDeliveryItem;

public interface ReceiveDeliveryItemRepository {

	void save(ReceiveDeliveryItem item);

	List<ReceiveDeliveryItem> findAllByReceiveDelivery(ReceiveDelivery receiveDelivery);

	void delete(ReceiveDeliveryItem item);

	void deleteAllByReceiveDelivery(ReceiveDelivery receiveDelivery);
	
}
