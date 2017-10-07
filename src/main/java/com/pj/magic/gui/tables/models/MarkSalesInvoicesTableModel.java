package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.MarkSalesInvoicesTable;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.util.FormatterUtil;

@Component
public class MarkSalesInvoicesTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = 
		{"SI No.", "Transaction Date", "Customer", "Encoder", "Net Amount", "Mark"};
	
	private List<SalesInvoice> salesInvoices = new ArrayList<>();
	
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
		case MarkSalesInvoicesTable.SALES_INVOICE_NUMBER_COLUMN_INDEX:
			return salesInvoice.getSalesInvoiceNumber();
		case MarkSalesInvoicesTable.CUSTOMER_NAME_COLUMN_INDEX:
			return salesInvoice.getCustomer().getName();
		case MarkSalesInvoicesTable.TRANSACTION_DATE_COLUMN_INDEX:
			return FormatterUtil.formatDate(salesInvoice.getCreateDate());
		case MarkSalesInvoicesTable.ENCODER_COLUMN_INDEX:
			return salesInvoice.getEncoder().getUsername();
		case MarkSalesInvoicesTable.NET_AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(salesInvoice.getTotalNetAmount());
		case MarkSalesInvoicesTable.MARK_COLUMN_INDEX:
			return salesInvoice.isMarked();
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
		return columnIndex == MarkSalesInvoicesTable.MARK_COLUMN_INDEX;
	}
	
	public void setSalesInvoices(List<SalesInvoice> salesInvoices) {
		this.salesInvoices = salesInvoices;
		fireTableDataChanged();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case MarkSalesInvoicesTable.NET_AMOUNT_COLUMN_INDEX:
			return Number.class;
		case MarkSalesInvoicesTable.MARK_COLUMN_INDEX:
			return Boolean.class;
		default:
			return Object.class;
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		SalesInvoice salesInvoice = salesInvoices.get(rowIndex);
		switch (columnIndex) {
		case MarkSalesInvoicesTable.MARK_COLUMN_INDEX:
			salesInvoice.setMarked(!salesInvoice.isMarked());
			break;
		default:
			throw new RuntimeException("Setting invalid column index: " + columnIndex);
		}
		fireTableCellUpdated(rowIndex, MarkSalesInvoicesTable.MARK_COLUMN_INDEX);
	}

	public List<SalesInvoice> getSalesInvoices() {
		return salesInvoices;
	}
	
}