package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.PaymentTerm;

@Component
public class PaymentTermsTableModel extends AbstractTableModel {

	private static final String[] columnNames = {"Name", "Number of Days"};
	private static final int NAME_COLUMN_INDEX = 0;
	private static final int NUMBER_OF_DAYS_COLUMN_INDEX = 1;
	
	private List<PaymentTerm> paymentTerms = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return paymentTerms.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PaymentTerm paymentTerm = paymentTerms.get(rowIndex);
		switch (columnIndex) {
		case NAME_COLUMN_INDEX:
			return paymentTerm.getName();
		case NUMBER_OF_DAYS_COLUMN_INDEX:
			return String.valueOf(paymentTerm.getNumberOfDays());
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public void setPaymentTerms(List<PaymentTerm> paymentTerms) {
		this.paymentTerms = paymentTerms;
		fireTableDataChanged();
	}
	
	public PaymentTerm getPaymentTerm(int rowIndex) {
		return paymentTerms.get(rowIndex);
	}
	
}
