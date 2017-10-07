package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.InventoryCheckSummaryTable;
import com.pj.magic.model.InventoryCheckSummaryItem;
import com.pj.magic.util.FormatterUtil;

@Component
public class InventoryCheckSummaryTableModel extends AbstractTableModel {

	private static final String[] columnNames = 
		{"Code", "Description", "Unit", "Beginning Inv.", "Actual Count", "Difference", "Cost", "Actual Value"};
	
	private List<InventoryCheckSummaryItem> items = new ArrayList<>();
	
	public void setItems(List<InventoryCheckSummaryItem> items) {
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
		InventoryCheckSummaryItem item = items.get(rowIndex);
		switch (columnIndex) {
		case InventoryCheckSummaryTable.PRODUCT_CODE_COLUMN_INDEX:
			return item.getProduct().getCode();
		case InventoryCheckSummaryTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return item.getProduct().getDescription();
		case InventoryCheckSummaryTable.UNIT_COLUMN_INDEX:
			return item.getUnit();
		case InventoryCheckSummaryTable.BEGINNING_INVENTORY_COLUMN_INDEX:
			return item.getProduct().getUnitQuantity(item.getUnit());
		case InventoryCheckSummaryTable.ACTUAL_COUNT_COLUMN_INDEX:
			return item.getQuantity();
		case InventoryCheckSummaryTable.QUANTITY_DIFFERENCE_COLUMN_INDEX:
			return item.getQuantityDifference();
		case InventoryCheckSummaryTable.COST_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getCost());
		case InventoryCheckSummaryTable.ACTUAL_VALUE_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getActualValue());
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

	public InventoryCheckSummaryItem getItem(int row) {
		return items.get(row);
	}
	
}
