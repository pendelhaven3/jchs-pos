package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.Manufacturer;

@Component
public class ManufacturersTableModel extends AbstractTableModel {

	private static final String[] columnNames = {"Name"};
	
	private List<Manufacturer> manufacturers = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return manufacturers.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Manufacturer manufacturer = manufacturers.get(rowIndex);
		return manufacturer.getName();
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public void setManufacturers(List<Manufacturer> manufacturers) {
		this.manufacturers = manufacturers;
		fireTableDataChanged();
	}
	
	public Manufacturer getManufacturer(int rowIndex) {
		return manufacturers.get(rowIndex);
	}
	
}
