package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.EcashReceiver;

public interface EcashReceiverService {

	void save(EcashReceiver ecashReceiver);
	
	List<EcashReceiver> getAllEcashReceivers();
	
	EcashReceiver getEcashReceiver(long id);

	boolean isBeingUsed(EcashReceiver ecashReceiver);

	void delete(EcashReceiver ecashReceiver);

	EcashReceiver getEcashReceiverByName(String text);
	
}
