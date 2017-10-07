package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.UnpaidSalesInvoicesTable;
import com.pj.magic.model.Customer;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class AddSalesInvoicesToPaymentDialog extends MagicDialog {

	private static final int SELECTION_CHECKBOX_COLUMN_INDEX = 0;
	private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 1;
	private static final int TRANSACTION_DATE_COLUMN_INDEX = 2;
	private static final int AMOUNT_COLUMN_INDEX = 3;

	@Autowired private SalesInvoiceService salesInvoiceService;
	
	private JTable table;
	private SalesInvoicesTableModel tableModel;
	private JButton addButton;
	private JButton addAllButton;
	private List<SalesInvoice> selectedSalesInvoices = new ArrayList<>();
	
	public AddSalesInvoicesToPaymentDialog() {
		setSize(500, 250);
		setLocationRelativeTo(null);
		setTitle("Add Sales Invoices to Payment");
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		layoutComponents();
	}

	private void initializeComponents() {
		addButton = new JButton("Add Selected");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedSalesInvoices.addAll(tableModel.getSelectedSalesInvoices());
				setVisible(false);
			}
		});
		
		addAllButton = new JButton("Add All");
		addAllButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedSalesInvoices.addAll(tableModel.getSalesInvoices());
				setVisible(false);
			}
		});
		
		initializeTable();
	}

	private void initializeTable() {
		tableModel = new SalesInvoicesTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(SELECTION_CHECKBOX_COLUMN_INDEX).setPreferredWidth(40);
		columnModel.getColumn(SALES_INVOICE_NUMBER_COLUMN_INDEX).setPreferredWidth(120);
		columnModel.getColumn(TRANSACTION_DATE_COLUMN_INDEX).setPreferredWidth(120);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setPreferredWidth(120);
	}

	public List<SalesInvoice> getSelectedSalesInvoices() {
		return selectedSalesInvoices;
	}
	
	@Override
	protected void doWhenEscapeKeyPressed() {
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(400, 200));
		add(scrollPane, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		add(ComponentUtil.createGenericPanel(
				addButton, addAllButton), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createVerticalStrut(20), c);
	}
	
	public void searchSalesInvoicesForPayment(Customer customer) {
		selectedSalesInvoices.clear();
		List<SalesInvoice> salesInvoices = salesInvoiceService.findAllSalesInvoicesForPaymentByCustomer(customer);
		tableModel.setSalesInvoices(salesInvoices);
	}

	private class SalesInvoicesTableModel extends AbstractTableModel {

		private final String[] columnNames = {"", "SI No.", "Transaction Date", "Total Amount"};
		
		private List<SalesInvoice> salesInvoices = new ArrayList<>();
		private List<Integer> selected = new ArrayList<>();
		
		public void setSalesInvoices(List<SalesInvoice> salesInvoices) {
			this.salesInvoices = salesInvoices;
			selected.clear();
			fireTableDataChanged();
		}
		
		public List<SalesInvoice> getSalesInvoices() {
			return salesInvoices;
		}

		@Override
		public int getRowCount() {
			return salesInvoices.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			SalesInvoice salesInvoice = salesInvoices.get(rowIndex);
			switch (columnIndex) {
			case SELECTION_CHECKBOX_COLUMN_INDEX:
				return selected.contains(rowIndex);
			case SALES_INVOICE_NUMBER_COLUMN_INDEX:
				return salesInvoice.getSalesInvoiceNumber();
			case TRANSACTION_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(salesInvoice.getTransactionDate());
			case AMOUNT_COLUMN_INDEX:
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
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == SELECTION_CHECKBOX_COLUMN_INDEX;
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case SELECTION_CHECKBOX_COLUMN_INDEX:
				return Boolean.class;
			case AMOUNT_COLUMN_INDEX:
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
	
}
