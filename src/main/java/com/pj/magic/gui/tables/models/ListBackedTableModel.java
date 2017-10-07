package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public abstract class ListBackedTableModel<T> extends AbstractTableModel {

	private List<T> items = new ArrayList<>();
	
	public void setItems(List<T> items) {
		this.items = items;
		fireTableDataChanged();
	}
	
	public T getItem(int rowIndex) {
		return items.get(rowIndex);
	}
	
	public List<T> getItems() {
		return items;
	}
	
	@Override
	public int getRowCount() {
		return items.size();
	}
	
	protected abstract String[] getColumnNames();
	
	/**
	 * Return a constant/final String array here consisting of column names
	 */
	@Override
	public String getColumnName(int column) {
		return getColumnNames()[column];
	}
	
	@Override
	public int getColumnCount() {
		return getColumnNames().length;
	}
	
	public boolean hasItems() {
		return !items.isEmpty();
	}
	
	public void clear() {
		items = Collections.emptyList();
		fireTableDataChanged();
	}
	
}
