package com.pj.magic.gui.tables;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.models.UnpaidSalesInvoicesTableModel;
import com.pj.magic.model.SalesInvoice;

@Component
public class UnpaidSalesInvoicesTable extends MagicTable {

	public static final int SELECTION_CHECKBOX_COLUMN_INDEX = 0;
	public static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 1;
	public static final int TRANSACTION_DATE_COLUMN_INDEX = 2;
	public static final int NET_AMOUNT_COLUMN_INDEX = 3;
	
	private UnpaidSalesInvoicesTableModel tableModel;
	
	@Autowired
	public UnpaidSalesInvoicesTable(UnpaidSalesInvoicesTableModel tableModel) {
		super(tableModel);
		this.tableModel = tableModel;
		initializeColumns();
	}
	
	private void initializeColumns() {
		columnModel.getColumn(SELECTION_CHECKBOX_COLUMN_INDEX).setPreferredWidth(20);
		columnModel.getColumn(SALES_INVOICE_NUMBER_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(TRANSACTION_DATE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(NET_AMOUNT_COLUMN_INDEX).setPreferredWidth(100);
	}

	public void setSalesInvoices(List<SalesInvoice> salesInvoices) {
		tableModel.setSalesInvoices(salesInvoices);
	}

	public void clearDisplay() {
		tableModel.setSalesInvoices(new ArrayList<SalesInvoice>());
	}

	public List<SalesInvoice> getSelectedSalesInvoices() {
		return tableModel.getSelectedSalesInvoices();
	}

}
