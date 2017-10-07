package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.panels.SalesInvoiceListPanel;
import com.pj.magic.gui.tables.models.SalesInvoicesTableModel;
import com.pj.magic.model.SalesInvoice;

@Component
public class SalesInvoicesTable extends MagicListTable {

	public static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 0;
	public static final int TRANSACTION_DATE_COLUMN_INDEX = 1;
	public static final int CUSTOMER_COLUMN_INDEX = 2;
	public static final int ENCODER_COLUMN_INDEX = 3;
	public static final int NET_AMOUNT_COLUMN_INDEX = 4;
	public static final int STATUS_COLUMN_INDEX = 5;
	public static final int PRINTED_COLUMN_INDEX = 6;
	private static final String GO_TO_SALES_INVOICE_ACTION_NAME = "goToSalesInvoice";

	@Autowired private SalesInvoicesTableModel tableModel;
	
	@Autowired
	public SalesInvoicesTable(SalesInvoicesTableModel tableModel) {
		super(tableModel);
	}
	
	@PostConstruct
	public void initialize() {
		initializeColumns();
		registerKeyBindings();
    }
	
	private void initializeColumns() {
		columnModel.getColumn(SALES_INVOICE_NUMBER_COLUMN_INDEX).setPreferredWidth(60);
		columnModel.getColumn(TRANSACTION_DATE_COLUMN_INDEX).setPreferredWidth(120);
		columnModel.getColumn(CUSTOMER_COLUMN_INDEX).setPreferredWidth(200);
		columnModel.getColumn(ENCODER_COLUMN_INDEX).setPreferredWidth(120);
		columnModel.getColumn(NET_AMOUNT_COLUMN_INDEX).setPreferredWidth(120);
		columnModel.getColumn(STATUS_COLUMN_INDEX).setPreferredWidth(80);
	}

	public void setSalesInvoices(List<SalesInvoice> salesInvoices) {
		tableModel.setSalesInvoices(salesInvoices);
		if (!salesInvoices.isEmpty()) {
			changeSelection(0, 0, false, false);
		}
	}
	
	public SalesInvoice getCurrentlySelectedSalesInvoice() {
		return tableModel.getSalesInvoice(getSelectedRow());
	}
	
	public void displaySalesInvoiceDetails(SalesInvoice salesInvoice) {
		SalesInvoiceListPanel panel = (SalesInvoiceListPanel)
				SwingUtilities.getAncestorOfClass(SalesInvoiceListPanel.class, this);
		panel.displaySalesInvoiceDetails(salesInvoice);
	}
	
	public void registerKeyBindings() {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), GO_TO_SALES_INVOICE_ACTION_NAME);
		getActionMap().put(GO_TO_SALES_INVOICE_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (getSelectedRow() != -1) {
					selectSalesInvoice();
				}
			}
		});
		
		addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectSalesInvoice();
			}
		});
	}

	protected void selectSalesInvoice() {
		displaySalesInvoiceDetails(getCurrentlySelectedSalesInvoice());
	}
	
}
