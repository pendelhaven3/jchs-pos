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
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.Supplier;
import com.pj.magic.service.ReceivingReceiptService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class AddReceivingReceiptsToPurchasePaymentDialog extends MagicDialog {

	private static final int SELECTION_CHECKBOX_COLUMN_INDEX = 0;
	private static final int RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX = 1;
	private static final int RECEIVED_DATE_COLUMN_INDEX = 2;
	private static final int AMOUNT_COLUMN_INDEX = 3;

	@Autowired private ReceivingReceiptService receivingReceiptService;
	
	private JTable table;
	private ReceivingReceiptsTableModel tableModel;
	private JButton addButton;
	private JButton addAllButton;
	private List<ReceivingReceipt> selectedReceivingReceipts = new ArrayList<>();
	
	public AddReceivingReceiptsToPurchasePaymentDialog() {
		setSize(500, 250);
		setLocationRelativeTo(null);
		setTitle("Add Receiving Receipts to Purchase Payment");
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
				selectedReceivingReceipts.addAll(tableModel.getSelectedReceivingReceipt());
				setVisible(false);
			}
		});
		
		addAllButton = new JButton("Add All");
		addAllButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedReceivingReceipts.addAll(tableModel.getReceivingReceipts());
				setVisible(false);
			}
		});
		
		initializeTable();
	}

	private void initializeTable() {
		tableModel = new ReceivingReceiptsTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(SELECTION_CHECKBOX_COLUMN_INDEX).setPreferredWidth(40);
		columnModel.getColumn(RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX).setPreferredWidth(120);
		columnModel.getColumn(RECEIVED_DATE_COLUMN_INDEX).setPreferredWidth(120);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setPreferredWidth(120);
	}

	public List<ReceivingReceipt> getSelectedReceivingReceipts() {
		return selectedReceivingReceipts;
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
	
	public void searchReceivingReceiptsForPayment(Supplier supplier) {
		selectedReceivingReceipts.clear();
		List<ReceivingReceipt> salesInvoices = receivingReceiptService
				.findAllReceivingReceiptsForPaymentBySupplier(supplier);
		tableModel.setReceivingReceipts(salesInvoices);
	}

	private class ReceivingReceiptsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"", "RR No.", "Received Date", "Total Amount"};
		
		private List<ReceivingReceipt> receivingReceipts = new ArrayList<>();
		private List<Integer> selected = new ArrayList<>();
		
		public void setReceivingReceipts(List<ReceivingReceipt> receivingReceipts) {
			this.receivingReceipts = receivingReceipts;
			selected.clear();
			fireTableDataChanged();
		}
		
		public List<ReceivingReceipt> getReceivingReceipts() {
			return receivingReceipts;
		}

		@Override
		public int getRowCount() {
			return receivingReceipts.size();
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
			ReceivingReceipt receivingReceipt = receivingReceipts.get(rowIndex);
			switch (columnIndex) {
			case SELECTION_CHECKBOX_COLUMN_INDEX:
				return selected.contains(rowIndex);
			case RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX:
				return receivingReceipt.getReceivingReceiptNumber();
			case RECEIVED_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(receivingReceipt.getReceivedDate());
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(receivingReceipt.getTotalNetAmountWithVat());
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

		public List<ReceivingReceipt> getSelectedReceivingReceipt() {
			List<ReceivingReceipt> selectedReceivingReceipts = new ArrayList<>();
			for (Integer i : selected) {
				selectedReceivingReceipts.add(receivingReceipts.get(i));
			}
			return selectedReceivingReceipts;
		}
		
	}
	
}