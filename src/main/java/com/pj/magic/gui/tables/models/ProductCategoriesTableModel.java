package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.ProductCategory;

@Component
public class ProductCategoriesTableModel extends AbstractTableModel {

	private static final String[] columnNames = {"Category"};
	
	private List<ProductCategory> categories = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return categories.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ProductCategory category = categories.get(rowIndex);
		return category.getName();
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public void setProductCategories(List<ProductCategory> categories) {
		this.categories = categories;
		fireTableDataChanged();
	}
	
	public ProductCategory getProductCategory(int rowIndex) {
		return categories.get(rowIndex);
	}
	
}
