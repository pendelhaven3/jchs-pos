package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.util.FormatterUtil;

@Component
public class ReceivingReceiptsTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = {"RR No.", "Received Date", "Supplier", "Reference No.", "Net Amount", "Status"};
	public static final int RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX = 0;
	public static final int RECEIVED_DATE_COLUMN_INDEX = 1;
	public static final int SUPPLIER_COLUMN_INDEX = 2;
	public static final int REFERENCE_NUMBER_COLUMN_INDEX = 3;
	public static final int NET_AMOUNT_COLUMN_INDEX = 4;
	public static final int STATUS_COLUMN_INDEX = 5;
	
	private List<ReceivingReceipt> receivingReceipts = new ArrayList<>();
	
	public void setReceivingReceipts(List<ReceivingReceipt> receivingReceipts) {
		this.receivingReceipts = receivingReceipts;
		fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		return receivingReceipts.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ReceivingReceipt receivingReceipt = receivingReceipts.get(rowIndex);
		switch (columnIndex) {
		case RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX:
			return receivingReceipt.getReceivingReceiptNumber().toString();
		case RECEIVED_DATE_COLUMN_INDEX:
			return FormatterUtil.formatDate(receivingReceipt.getReceivedDate());
		case SUPPLIER_COLUMN_INDEX:
			return receivingReceipt.getSupplier().getName();
		case REFERENCE_NUMBER_COLUMN_INDEX:
			return receivingReceipt.getReferenceNumber();
		case NET_AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(receivingReceipt.getTotalNetAmount());
		case STATUS_COLUMN_INDEX:
			return receivingReceipt.getStatus();
		default:
			throw new RuntimeException("Fetch invalid column index: " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	public ReceivingReceipt getReceivingReceipt(int row) {
		return receivingReceipts.get(row);
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == NET_AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}
	
}
