package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.Payment;
import com.pj.magic.util.FormatterUtil;

@Component
public class PaymentsTableModel extends AbstractTableModel {

	private static final String[] columnNames = 
		{"Payment No.", "Customer", "Create Date", "Encoder", "Status", "Posted By"};
	private static final int PAYMENT_NUMBER_COLUMN_INDEX = 0;
	private static final int CUSTOMER_COLUMN_INDEX = 1;
	private static final int CREATE_DATE_COLUMN_INDEX = 2;
	private static final int ENCODER_COLUMN_INDEX = 3;
	private static final int STATUS_COLUMN_INDEX = 4;
	private static final int POSTED_BY_COLUMN_INDEX = 5;
	
	private List<Payment> payments = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return payments.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Payment payment = payments.get(rowIndex);
		switch (columnIndex) {
		case PAYMENT_NUMBER_COLUMN_INDEX:
			return payment.getPaymentNumber();
		case CUSTOMER_COLUMN_INDEX:
			return payment.getCustomer().getName();
		case CREATE_DATE_COLUMN_INDEX:
			return FormatterUtil.formatDate(payment.getCreateDate());
		case ENCODER_COLUMN_INDEX:
			return payment.getEncoder().getUsername();
		case STATUS_COLUMN_INDEX:
			return payment.getStatus();
		case POSTED_BY_COLUMN_INDEX:
			return payment.isPosted() ? payment.getPostedBy().getUsername() : null;
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public void setPayments(List<Payment> payments) {
		this.payments = payments;
		fireTableDataChanged();
	}
	
	public Payment getPayment(int rowIndex) {
		return payments.get(rowIndex);
	}
	
}
