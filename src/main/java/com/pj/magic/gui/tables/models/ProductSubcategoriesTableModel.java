package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.ProductSubcategory;

@Component
public class ProductSubcategoriesTableModel extends AbstractTableModel {

	private List<ProductSubcategory> subcategories = new ArrayList<>();
	
	public void setSubcategories(List<ProductSubcategory> subcategories) {
		this.subcategories = subcategories;
		fireTableDataChanged();
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		return subcategories.get(row).getName();
	}
	
	@Override
	public int getRowCount() {
		return subcategories.size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	public ProductSubcategory getSubcategory(int rowIndex) {
		return subcategories.get(rowIndex);
	}
	
}
