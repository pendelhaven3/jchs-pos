package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.EcashReceiver;

public interface EcashReceiverRepository {

	void save(EcashReceiver ecashReceiver);
	
	EcashReceiver get(long id);
	
	List<EcashReceiver> getAll();

	void delete(EcashReceiver ecashReceiver);

	EcashReceiver findByName(String name);

}
