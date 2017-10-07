package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.PurchaseOrder;

@Component
public class PurchaseOrdersTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = {"PO No.", "Supplier", "Status"};
	private static final int PURCHASE_ORDER_NUMBER_COLUMN_INDEX = 0;
	private static final int SUPPLIER_COLUMN_INDEX = 1;
	private static final int STATUS_COLUMN_INDEX = 2;
	
	private List<PurchaseOrder> purchaseOrders = new ArrayList<>();
	
	public void setPurchaseOrders(List<PurchaseOrder> purchaseOrders) {
		this.purchaseOrders = purchaseOrders;
		fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		return purchaseOrders.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PurchaseOrder purchaseOrder = purchaseOrders.get(rowIndex);
		switch (columnIndex) {
		case PURCHASE_ORDER_NUMBER_COLUMN_INDEX:
			return purchaseOrder.getPurchaseOrderNumber().toString();
		case SUPPLIER_COLUMN_INDEX:
			return purchaseOrder.getSupplier().getName();
		case STATUS_COLUMN_INDEX:
			return purchaseOrder.getStatus();
		default:
			throw new RuntimeException("Fetch invalid column index: " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	public PurchaseOrder getPurchaseOrder(int row) {
		return purchaseOrders.get(row);
	}

	public void remove(PurchaseOrder purchaseOrder) {
		int row = purchaseOrders.indexOf(purchaseOrder);
		purchaseOrders.remove(row);
		fireTableRowsDeleted(row, row);
	}
	
}
