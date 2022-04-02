package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.pj.magic.model.ProductBySpecialCode;

public class ProductsBySpecialCodeTableModel extends AbstractTableModel {

	public static final int CUSTOM_CODE_COLUMN_INDEX = 0;
	public static final int CODE_COLUMN_INDEX = 1;
	public static final int DESCRIPTION_COLUMN_INDEX = 2;
	public static final int UNIT_COLUMN_INDEX = 3;
	
	private static final String[] columnNames = {"Custom Code", "Product Code", "Description", "Unit"};
	
	private List<ProductBySpecialCode> products = new ArrayList<>();
	
	public void setProducts(List<ProductBySpecialCode> products) {
		this.products = products;
		fireTableDataChanged();
	}
	
	public List<ProductBySpecialCode> getProducts() {
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
		ProductBySpecialCode product = products.get(rowIndex);
		switch (columnIndex) {
		case CODE_COLUMN_INDEX:
			return product.getProduct().getCode();
		case CUSTOM_CODE_COLUMN_INDEX:
			return product.getCustomCode().getCode();
		case DESCRIPTION_COLUMN_INDEX:
			return product.getCustomCode().getProduct().getDescription();
		case UNIT_COLUMN_INDEX:
			return product.getProduct().getUnits().get(0);
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	public ProductBySpecialCode getProduct(int rowIndex) {
		return products.get(rowIndex);
	}
	
}
