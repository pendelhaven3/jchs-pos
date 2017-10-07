package com.pj.magic.gui.tables;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.models.MarkSalesInvoicesTableModel;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.service.SalesInvoiceService;

@Component
public class MarkSalesInvoicesTable extends MagicListTable {

	public static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 0;
	public static final int TRANSACTION_DATE_COLUMN_INDEX = 1;
	public static final int CUSTOMER_NAME_COLUMN_INDEX = 2;
	public static final int ENCODER_COLUMN_INDEX = 3;
	public static final int NET_AMOUNT_COLUMN_INDEX = 4;
	public static final int MARK_COLUMN_INDEX = 5;

	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private MarkSalesInvoicesTableModel tableModel;
	
	@Autowired
	public MarkSalesInvoicesTable(MarkSalesInvoicesTableModel tableModel) {
		super(tableModel);
	}
	
	@PostConstruct
	public void initialize() {
		setModel(tableModel);
		initializeColumns();
		registerKeyBindings();
    }
	
	private void initializeColumns() {
		columnModel.getColumn(SALES_INVOICE_NUMBER_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(TRANSACTION_DATE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(CUSTOMER_NAME_COLUMN_INDEX).setPreferredWidth(200);
		columnModel.getColumn(ENCODER_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(NET_AMOUNT_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(MARK_COLUMN_INDEX).setPreferredWidth(50);
	}

	public void update() {
		List<SalesInvoice> salesInvoices = salesInvoiceService.getNewSalesInvoices();
		tableModel.setSalesInvoices(salesInvoices);
		if (!salesInvoices.isEmpty()) {
			changeSelection(0, 0, false, false);
		}
	}
	
	private void registerKeyBindings() {
		// none
	}

	public List<SalesInvoice> getSalesInvoices() {
		return tableModel.getSalesInvoices();
	}
	
}
