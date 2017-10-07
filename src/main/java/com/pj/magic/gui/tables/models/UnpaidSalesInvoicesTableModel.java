package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.UnpaidSalesInvoicesTable;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.util.FormatterUtil;

@Component
public class UnpaidSalesInvoicesTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = 
		{"", "SI No.", "Transaction Date", "Net Amount"};
	
	private List<SalesInvoice> salesInvoices = new ArrayList<>();
	private List<Integer> selected = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return salesInvoices.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SalesInvoice salesInvoice = salesInvoices.get(rowIndex);
		switch (columnIndex) {
		case UnpaidSalesInvoicesTable.SELECTION_CHECKBOX_COLUMN_INDEX:
			return selected.contains(rowIndex);
		case UnpaidSalesInvoicesTable.SALES_INVOICE_NUMBER_COLUMN_INDEX:
			return salesInvoice.getSalesInvoiceNumber();
		case UnpaidSalesInvoicesTable.TRANSACTION_DATE_COLUMN_INDEX:
			return FormatterUtil.formatDate(salesInvoice.getTransactionDate());
		case UnpaidSalesInvoicesTable.NET_AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(salesInvoice.getTotalNetAmount());
		default:
			throw new RuntimeException("Fetch invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case UnpaidSalesInvoicesTable.SELECTION_CHECKBOX_COLUMN_INDEX:
			if (selected.contains(rowIndex)) {
				selected.remove(selected.indexOf(rowIndex));
			} else {
				selected.add(rowIndex);
			}
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == UnpaidSalesInvoicesTable.SELECTION_CHECKBOX_COLUMN_INDEX;
	}
	
	public void setSalesInvoices(List<SalesInvoice> salesInvoices) {
		this.salesInvoices = salesInvoices;
		selected.clear();
		fireTableDataChanged();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return Boolean.class;
		case UnpaidSalesInvoicesTable.NET_AMOUNT_COLUMN_INDEX:
			return Number.class;
		default:
			return Object.class;
		}
	}

	public List<SalesInvoice> getSelectedSalesInvoices() {
		List<SalesInvoice> selectedSalesInvoices = new ArrayList<>();
		for (Integer i : selected) {
			selectedSalesInvoices.add(salesInvoices.get(i));
		}
		return selectedSalesInvoices;
	}
	
}
