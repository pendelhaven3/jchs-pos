package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.SalesRequisitionsTable;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.util.FormatterUtil;

@Component
public class SalesRequisitionsTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = {"SR No.", "Customer Name", "Create Date", "Transaction Date", "Encoder", "Total Amount"};
	
	private List<SalesRequisition> salesRequisitions = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return salesRequisitions.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SalesRequisition salesRequisition = salesRequisitions.get(rowIndex);
		switch (columnIndex) {
		case SalesRequisitionsTable.SALES_REQUISITION_NUMBER_COLUMN_INDEX:
			return salesRequisition.getSalesRequisitionNumber();
		case SalesRequisitionsTable.CUSTOMER_NAME_COLUMN_INDEX:
			return salesRequisition.getCustomer().getName();
		case SalesRequisitionsTable.CREATE_DATE_COLUMN_INDEX:
			return FormatterUtil.formatDate(salesRequisition.getCreateDate());
		case SalesRequisitionsTable.TRANSACTION_DATE_COLUMN_INDEX:
			return FormatterUtil.formatDate(salesRequisition.getTransactionDate());
		case SalesRequisitionsTable.ENCODER_COLUMN_INDEX:
			return salesRequisition.getEncoder().getUsername();
		case SalesRequisitionsTable.TOTAL_AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(salesRequisition.getTotalAmount());
		default:
			throw new RuntimeException("Fetch invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	public void setSalesRequisitions(List<SalesRequisition> salesRequisitions) {
		this.salesRequisitions = salesRequisitions;
		fireTableDataChanged();
	}
	
	public SalesRequisition getSalesRequisition(int rowIndex) {
		return salesRequisitions.get(rowIndex);
	}

	public void remove(SalesRequisition salesRequisition) {
		salesRequisitions.remove(salesRequisition);
		fireTableDataChanged();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == SalesRequisitionsTable.TOTAL_AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}
	
}
