package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.SalesReturn;
import com.pj.magic.util.FormatterUtil;

@Component
public class SalesReturnsTableModel extends AbstractTableModel {

	private static final String[] columnNames = {"SR No.", "SI No.", "Customer", "Total Amount",
		"Status", "Paid Date"};
	
	private static final int SALES_RETURN_NUMBER_COLUMN_INDEX = 0;
	private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 1;
	private static final int CUSTOMER_COLUMN_INDEX = 2;
	private static final int TOTAL_AMOUNT_COLUMN_INDEX = 3;
	private static final int STATUS_COLUMN_INDEX = 4;
	private static final int PAID_DATE_COLUMN_INDEX = 5;
	
	private List<SalesReturn> salesReturns = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return salesReturns.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SalesReturn salesReturn = salesReturns.get(rowIndex);
		switch (columnIndex) {
		case SALES_RETURN_NUMBER_COLUMN_INDEX:
			return salesReturn.getSalesReturnNumber();
		case SALES_INVOICE_NUMBER_COLUMN_INDEX:
			return salesReturn.getSalesInvoice().getSalesInvoiceNumber();
		case CUSTOMER_COLUMN_INDEX:
			return salesReturn.getSalesInvoice().getCustomer().getName();
		case TOTAL_AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(salesReturn.getTotalAmount());
		case STATUS_COLUMN_INDEX:
			return salesReturn.getStatus();
		case PAID_DATE_COLUMN_INDEX:
			return salesReturn.isPaid() ? FormatterUtil.formatDateTime(salesReturn.getPaidDate()) : null;
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == TOTAL_AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public void setSalesReturns(List<SalesReturn> salesReturns) {
		this.salesReturns = salesReturns;
		fireTableDataChanged();
	}
	
	public SalesReturn getSalesReturn(int rowIndex) {
		return salesReturns.get(rowIndex);
	}
	
}