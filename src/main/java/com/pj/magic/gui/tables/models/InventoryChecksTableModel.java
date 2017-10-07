package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.InventoryChecksTable;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.util.FormatterUtil;

@Component
public class InventoryChecksTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = {"Inventory Date", "Status"};
	
	private List<InventoryCheck> inventoryChecks = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return inventoryChecks.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		InventoryCheck inventoryCheck = inventoryChecks.get(rowIndex);
		switch (columnIndex) {
		case InventoryChecksTable.INVENTORY_DATE_COLUMN_INDEX:
			return FormatterUtil.formatDate(inventoryCheck.getInventoryDate());
		case InventoryChecksTable.STATUS_COLUMN_INDEX:
			return inventoryCheck.isPosted() ? "Posted" : "New";
		default:
			throw new RuntimeException("Fetch invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}
	
	public void setInventoryChecks(List<InventoryCheck> inventoryChecks) {
		this.inventoryChecks = inventoryChecks;
		fireTableDataChanged();
	}
	
	public InventoryCheck getInventoryCheck(int rowIndex) {
		return inventoryChecks.get(rowIndex);
	}

	public void remove(InventoryCheck adjustmentIn) {
		inventoryChecks.remove(adjustmentIn);
		fireTableDataChanged();
	}
	
}
