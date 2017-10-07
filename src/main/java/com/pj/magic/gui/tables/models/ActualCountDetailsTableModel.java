package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.AreaInventoryReportItem;

@Component
public class ActualCountDetailsTableModel extends AbstractTableModel {

	private static final String[] columnNames = {"Report No.", "Area", "Unit", "Quantity"};
	private static final int REPORT_NO_COLUMN_INDEX = 0;
	private static final int AREA_COLUMN_INDEX = 1;
	private static final int UNIT_COLUMN_INDEX = 2;
	private static final int QUANTITY_COLUMN_INDEX = 3;
	
	private List<AreaInventoryReportItem> items = new ArrayList<>();
	
	public void setItems(List<AreaInventoryReportItem> items) {
		this.items = items;
		fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		return items.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		AreaInventoryReportItem item = items.get(rowIndex);
		switch (columnIndex) {
		case REPORT_NO_COLUMN_INDEX:
			return item.getParent().getReportNumber();
		case AREA_COLUMN_INDEX:
			return item.getParent().getArea().getName();
		case UNIT_COLUMN_INDEX:
			return item.getUnit();
		case QUANTITY_COLUMN_INDEX:
			return item.getQuantity();
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

}