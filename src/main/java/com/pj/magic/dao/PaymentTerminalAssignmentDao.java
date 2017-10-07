package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.User;

public interface PaymentTerminalAssignmentDao {
	
	List<PaymentTerminalAssignment> getAll();

	void save(PaymentTerminalAssignment paymentTerminalAssignment);

	void delete(PaymentTerminalAssignment paymentTerminalAssignment);

	PaymentTerminalAssignment findByUser(User user);
	
}
