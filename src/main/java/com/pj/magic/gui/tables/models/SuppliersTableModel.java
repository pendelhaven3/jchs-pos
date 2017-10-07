package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.Supplier;

@Component
public class SuppliersTableModel extends AbstractTableModel {

	public static final int CODE_COLUMN_INDEX = 0;
	public static final int NAME_COLUMN_INDEX = 1;
	
	private static final String[] columnNames = {"Code", "Business Name"};
	
	private List<Supplier> suppliers = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return suppliers.size();
	}

	public List<Supplier> getSuppliers() {
		return suppliers;
	}
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Supplier supplier = suppliers.get(rowIndex);
		switch (columnIndex) {
		case CODE_COLUMN_INDEX:
			return supplier.getCode();
		case NAME_COLUMN_INDEX:
			return supplier.getName();
		default:
			throw new RuntimeException("Fetching unexpected index: " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public void setSuppliers(List<Supplier> suppliers) {
		this.suppliers = suppliers;
		fireTableDataChanged();
	}
	
	public Supplier getSupplier(int rowIndex) {
		return suppliers.get(rowIndex);
	}
	
}
