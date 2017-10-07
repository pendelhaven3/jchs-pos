package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.SalesInvoicesTable;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.util.FormatterUtil;

@Component
public class SalesInvoicesTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = 
		{"SI No.", "Transaction Date", "Customer", "Encoder", "Net Amount", "Status", "Printed"};
	
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
		case SalesInvoicesTable.SALES_INVOICE_NUMBER_COLUMN_INDEX:
			return salesInvoice.getSalesInvoiceNumber();
		case SalesInvoicesTable.CUSTOMER_COLUMN_INDEX:
			return salesInvoice.getCustomer().getName();
		case SalesInvoicesTable.TRANSACTION_DATE_COLUMN_INDEX:
			return FormatterUtil.formatDate(salesInvoice.getTransactionDate());
		case SalesInvoicesTable.ENCODER_COLUMN_INDEX:
			return salesInvoice.getEncoder().getUsername();
		case SalesInvoicesTable.NET_AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(salesInvoice.getTotalNetAmount());
		case SalesInvoicesTable.STATUS_COLUMN_INDEX:
			return salesInvoice.getStatus();
		case SalesInvoicesTable.PRINTED_COLUMN_INDEX:
			return salesInvoice.isPrinted() ? "Yes" : "No";
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
	
	public void setSalesInvoices(List<SalesInvoice> salesInvoices) {
		this.salesInvoices = salesInvoices;
		fireTableDataChanged();
	}
	
	public SalesInvoice getSalesInvoice(int rowIndex) {
		return salesInvoices.get(rowIndex);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == SalesInvoicesTable.NET_AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return String.class;
		}
	}
	
}
