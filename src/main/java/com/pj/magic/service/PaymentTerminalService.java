package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.User;

public interface PaymentTerminalService {

	List<PaymentTerminalAssignment> getAllPaymentTerminalAssignments();

	List<PaymentTerminal> getAllPaymentTerminals();

	void save(PaymentTerminalAssignment paymentTerminalAssignment);

	void delete(PaymentTerminalAssignment paymentTerminalAssignment);

	PaymentTerminalAssignment findPaymentTerminalAssignment(User user);
	
}
