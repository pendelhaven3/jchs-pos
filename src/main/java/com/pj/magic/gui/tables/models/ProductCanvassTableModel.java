package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.ProductCanvassTable;
import com.pj.magic.model.ProductCanvassItem;
import com.pj.magic.util.FormatterUtil;

@Component
public class ProductCanvassTableModel extends AbstractTableModel {

	private static final String[] columnNames = 
		{"Date", "RR No.", "Supplier", "Unit", "Final Cost", "Gross Cost", "Ref No."};
	
	private List<ProductCanvassItem> items = new ArrayList<>();
	
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
		ProductCanvassItem item = items.get(rowIndex);
		switch (columnIndex) {
		case ProductCanvassTable.RECEIVED_DATE_COLUMN_INDEX:
			return FormatterUtil.formatDate(item.getReceivedDate());
		case ProductCanvassTable.RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX:
			return item.getReceivingReceiptNumber();
		case ProductCanvassTable.SUPPLIER_COLUMN_INDEX:
			return item.getSupplier().getName();
		case ProductCanvassTable.UNIT_COLUMN_INDEX:
			return item.getUnit();
		case ProductCanvassTable.FINAL_COST_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getFinalCost());
		case ProductCanvassTable.GROSS_COST_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getGrossCost());
		case ProductCanvassTable.REFERENCE_NUMBER_COLUMN_INDEX:
			return item.getReferenceNumber();
		default:
			throw new RuntimeException("Fetch invalid column index: " + columnIndex);
		}
	}

	public void setItems(List<ProductCanvassItem> items) {
		this.items.clear();
		this.items.addAll(items);
		fireTableDataChanged();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case ProductCanvassTable.FINAL_COST_COLUMN_INDEX:
		case ProductCanvassTable.GROSS_COST_COLUMN_INDEX:
			return Number.class;
		default:
			return Object.class;
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
}
