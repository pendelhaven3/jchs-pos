package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.Customer;

@Component
public class CustomersTableModel extends AbstractTableModel {

	private static final int CODE_COLUMN_INDEX = 0;
	private static final int NAME_COLUMN_INDEX = 1;
	private static final String[] columnNames = {"Code", "Name"};
	
	private List<Customer> customers = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return customers.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Customer customer = customers.get(rowIndex);
		switch (columnIndex) {
		case CODE_COLUMN_INDEX:
			return customer.getCode();
		case NAME_COLUMN_INDEX:
			return customer.getName();
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
		fireTableDataChanged();
	}
	
	public Customer getCustomer(int rowIndex) {
		return customers.get(rowIndex);
	}
	
}
