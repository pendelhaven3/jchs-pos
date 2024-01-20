package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.model.EcashReceiver;
import com.pj.magic.repository.EcashReceiverRepository;
import com.pj.magic.repository.PurchasePaymentEcashPaymentRepository;
import com.pj.magic.service.EcashReceiverService;

@Service
public class EcashReceiverServiceImpl implements EcashReceiverService {

	@Autowired private EcashReceiverRepository ecashReceiverRepository;
	@Autowired private PurchasePaymentEcashPaymentRepository purchasePaymentEcashPaymentRepository;
	
	@Override
	public void save(EcashReceiver ecashReceiver) {
		ecashReceiverRepository.save(ecashReceiver);
	}

	@Override
	public List<EcashReceiver> getAllEcashReceivers() {
		return ecashReceiverRepository.getAll();
	}

	@Override
	public EcashReceiver getEcashReceiver(long id) {
		return ecashReceiverRepository.get(id);
	}

	@Override
	public boolean isBeingUsed(EcashReceiver ecashReceiver) {
		return purchasePaymentEcashPaymentRepository.findOneByEcashReceiver(ecashReceiver) != null;
	}

	@Override
	public void delete(EcashReceiver ecashReceiver) {
		ecashReceiverRepository.delete(ecashReceiver);
	}

	@Override
	public EcashReceiver getEcashReceiverByName(String name) {
		return ecashReceiverRepository.findByName(name);
	}

}
