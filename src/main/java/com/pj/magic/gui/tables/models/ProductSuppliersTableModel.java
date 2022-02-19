package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.Supplier;

// TODO: inline this one
@Component
public class ProductSuppliersTableModel extends AbstractTableModel {

	private List<Supplier> suppliers = new ArrayList<>();
	
	public void setSuppliers(List<Supplier> suppliers) {
		this.suppliers = suppliers;
		fireTableDataChanged();
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		return suppliers.get(row).getName();
	}
	
	@Override
	public int getRowCount() {
		return suppliers.size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	public Supplier getSupplier(int rowIndex) {
		return suppliers.get(rowIndex);
	}
	
}
