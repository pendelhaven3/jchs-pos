package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.ReceiveDelivery;
import com.pj.magic.model.ReceiveDeliveryItem;

public interface ReceiveDeliveryService {

	List<ReceiveDelivery> getAllUnpostedReceiveDeliveries();

	void save(ReceiveDelivery receiveDelivery);

	ReceiveDelivery getDeliveryService(Long id);

	void save(ReceiveDeliveryItem item);

	void delete(ReceiveDeliveryItem item);

	PurchaseOrder post(ReceiveDelivery receiveDelivery);

	void delete(ReceiveDelivery receiveDelivery);
	
}
