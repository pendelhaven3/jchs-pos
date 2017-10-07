package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.pj.magic.model.Product;

public class ProductsTableModel extends AbstractTableModel {

	public static final int CODE_COLUMN_INDEX = 0;
	public static final int DESCRIPTION_COLUMN_INDEX = 1;
	private static final String[] columnNames = {"Code", "Description"};
	
	private List<Product> products = new ArrayList<>();
	
	public void setProducts(List<Product> products) {
		this.products = products;
		fireTableDataChanged();
	}
	
	public List<Product> getProducts() {
		return products;
	}
	
	@Override
	public int getRowCount() {
		return products.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Product product = products.get(rowIndex);
		switch (columnIndex) {
		case CODE_COLUMN_INDEX:
			return product.getCode();
		case DESCRIPTION_COLUMN_INDEX:
			return product.getDescription();
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	public Product getProduct(int rowIndex) {
		return products.get(rowIndex);
	}
	
}
