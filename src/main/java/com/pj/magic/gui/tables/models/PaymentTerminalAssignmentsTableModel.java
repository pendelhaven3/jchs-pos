package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.PaymentTerminalAssignment;

@Component
public class PaymentTerminalAssignmentsTableModel extends AbstractTableModel {

	private static final String[] columnNames = {"User", "Payment Terminal"};
	private static final int USERNAME_COLUMN_INDEX = 0;
	private static final int PAYMENT_TERMINAL_NAME_COLUMN_INDEX = 1;
	
	private List<PaymentTerminalAssignment> assignments = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return assignments.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PaymentTerminalAssignment assignment = assignments.get(rowIndex);
		switch (columnIndex) {
		case USERNAME_COLUMN_INDEX:
			return assignment.getUser().getUsername();
		case PAYMENT_TERMINAL_NAME_COLUMN_INDEX:
			return assignment.getPaymentTerminal().getName();
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public void setPaymentTerminalAssignments(List<PaymentTerminalAssignment> assignments) {
		this.assignments = assignments;
		fireTableDataChanged();
	}
	
	public PaymentTerminalAssignment getPaymentTerminalAssignment(int rowIndex) {
		return assignments.get(rowIndex);
	}
	
}
