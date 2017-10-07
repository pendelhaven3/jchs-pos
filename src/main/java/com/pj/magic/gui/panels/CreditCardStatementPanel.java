package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.MagicDialog;
import com.pj.magic.gui.tables.CreditCardStatementPaymentsTable;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.model.CreditCardStatementItem;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.model.search.PurchasePaymentCreditCardPaymentSearchCriteria;
import com.pj.magic.service.CreditCardService;
import com.pj.magic.service.PurchasePaymentService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class CreditCardStatementPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(CreditCardStatementPanel.class);

	private static final int PURCHASE_PAYMENT_COLUMN_INDEX = 0;
	private static final int SUPPLIER_COLUMN_INDEX = 1;
	private static final int AMOUNT_COLUMN_INDEX = 2;
	private static final int CREDIT_CARD_COLUMN_INDEX = 3;
	private static final int TRANSACTION_DATE_COLUMN_INDEX = 4;
	
	@Autowired private CreditCardService creditCardService;
	@Autowired private PurchasePaymentService purchasePaymentService;
	@Autowired private CreditCardStatementPaymentsTable paymentsTable;
	
	private CreditCardStatement statement;
	private JLabel customerNumberField;
	private JLabel statementDateField;
	private JLabel statusField;
	private MagicListTable itemsTable;
	private CreditCardStatementItemsTableModel itemsTableModel;
	private JLabel totalPurchasesField;
	private JLabel totalPurchaseAmountField;
	private JLabel totalPaymentsField;
	private JLabel totalPaymentAmountField;
	private JLabel balanceField;
	private JButton postButton;
	private JButton deleteButton;
	private JButton addItemButton;
	private JButton deleteItemButton;
	private JButton addPaymentButton;
	private JButton deletePaymentButton;
	private AddCreditCardPaymentsToStatementDialog addCreditCardPaymentsToStatementDialog;
	private JTabbedPane tabbedPane;
	
	@Override
	protected void initializeComponents() {
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(itemsTable);
		updateTotalFieldsWhenTablesChange();
		
		addCreditCardPaymentsToStatementDialog = new AddCreditCardPaymentsToStatementDialog();
	}

	private void updateTotalFieldsWhenTablesChange() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				updateTotalFields(statement);
			}
		});
		paymentsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				updateTotalFields(statement);
			}
		});
	}

	private void initializeTable() {
		itemsTableModel = new CreditCardStatementItemsTableModel();
		itemsTable = new MagicListTable(itemsTableModel);
	}

	@Override
	protected void registerKeyBindings() {
	}
	
	@Override
	protected void doOnBack() {
		getMagicFrame().switchToCreditCardStatementListPanel();
	}
	
	public void updateDisplay(CreditCardStatement statement) {
		customerNumberField.setText(statement.getCustomerNumber());
		statementDateField.setText(FormatterUtil.formatDate(statement.getStatementDate()));
		statusField.setText(statement.getStatus());
		
		this.statement = statement = creditCardService.getCreditCardStatement(statement.getId());
		
		itemsTableModel.setItems(statement.getItems());
		paymentsTable.setStatement(statement);
		updateTotalFields(statement);
		
		statusField.setText(statement.getStatus());
		postButton.setEnabled(!statement.isPosted());
		addItemButton.setEnabled(!statement.isPosted());
		deleteItemButton.setEnabled(!statement.isPosted());
		addPaymentButton.setEnabled(!statement.isPosted());
		deletePaymentButton.setEnabled(!statement.isPosted());
		tabbedPane.setSelectedIndex(0);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Customer Number:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		customerNumberField = ComponentUtil.createLabel(200);
		mainPanel.add(customerNumberField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(140, "Statement Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statementDateField = ComponentUtil.createLabel(200, "");
		mainPanel.add(statementDateField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Status:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusField = ComponentUtil.createLabel(200, "");
		mainPanel.add(statusField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		mainPanel.add(createTabbedPane(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(5), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		mainPanel.add(createTotalsPanel(), c);
	}
	
	private JTabbedPane createTabbedPane() {
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Purchases", createItemsPanel());
		tabbedPane.addTab("Payments", createPaymentsPanel());
		return tabbedPane;
	}

	private JPanel createItemsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createItemsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane scrollPane = new JScrollPane(itemsTable);
		scrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(scrollPane, c);
		
		return panel;
	}

	private JPanel createPaymentsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createPaymentsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane scrollPane = new JScrollPane(paymentsTable);
		scrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(scrollPane, c);
		
		return panel;
	}

	private JPanel createPaymentsTableToolBar() {
		JPanel panel = new JPanel();
		
		addPaymentButton = new MagicToolBarButton("plus_small", "Add Item", true);
		addPaymentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addPayment();
			}
		});
		panel.add(addPaymentButton, BorderLayout.WEST);
		
		deletePaymentButton = new MagicToolBarButton("minus_small", "Delete Item", true);
		deletePaymentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deletePayment();
			}
		});
		panel.add(deletePaymentButton, BorderLayout.WEST);
		
		return panel;
	}

	private void addPayment() {
		paymentsTable.addNewRow();
	}

	private void deletePayment() {
		paymentsTable.removeCurrentlySelectedItem();
	}

	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Purchases:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalPurchasesField = ComponentUtil.createLabel(60);
		panel.add(totalPurchasesField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(Box.createHorizontalStrut(50));
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Payments:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalPaymentsField = ComponentUtil.createLabel(60);
		panel.add(totalPaymentsField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(180, "Total Purchase Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalPurchaseAmountField = ComponentUtil.createLabel(120);
		panel.add(totalPurchaseAmountField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(180, "Total Payment Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalPaymentAmountField = ComponentUtil.createLabel(120);
		panel.add(totalPaymentAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridwidth = 5;
		c.gridx = 0;
		c.gridy = currentRow;
		balanceField = new JLabel();
		panel.add(ComponentUtil.createGenericPanel(
				ComponentUtil.createLabel(100, "Balance:"),
				balanceField), c);
		
		return panel;
	}
	
	private JPanel createItemsTableToolBar() {
		JPanel panel = new JPanel();
		
		addItemButton = new MagicToolBarButton("plus_small", "Add Item", true);
		addItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addItem();
			}
		});
		panel.add(addItemButton, BorderLayout.WEST);
		
		deleteItemButton = new MagicToolBarButton("minus_small", "Delete Item", true);
		deleteItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteCurrentlySelectedItem();
			}
		});
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
	}

	private void addItem() {
		addCreditCardPaymentsToStatementDialog.updateDisplay();
		addCreditCardPaymentsToStatementDialog.setVisible(true);
		
		List<PurchasePaymentCreditCardPayment> selectedCreditCardPayments = 
				addCreditCardPaymentsToStatementDialog.getSelectedItems();
		if (!selectedCreditCardPayments.isEmpty()) {
			for (PurchasePaymentCreditCardPayment creditCardPayment : selectedCreditCardPayments) {
				CreditCardStatementItem item = new CreditCardStatementItem();
				item.setParent(statement);
				item.setCreditCardPayment(creditCardPayment);
				creditCardService.save(item);
				statement.getItems().add(item);
			}
			itemsTableModel.setItems(statement.getItems());
		}
	}

	private void deleteCurrentlySelectedItem() {
		if (statement.isPosted()) {
			return;
		}
		
		if (itemsTable.hasNoSelectedRow()) {
			return;
		}
		
		if (confirm("Remove currently selected item?")) {
			try {
				doDeleteCurrentlySelectedItem();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
				return;
			}
			
			showMessage("Item deleted");
			updateTotalFields(statement);
		}
	}

	private void doDeleteCurrentlySelectedItem() {
		int selectedRowIndex = itemsTable.getSelectedRow();
		itemsTable.clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		itemsTableModel.removeItem(selectedRowIndex);
		
		if (itemsTableModel.hasItems()) {
			if (selectedRowIndex == itemsTableModel.getRowCount()) {
				itemsTable.changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				itemsTable.changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}

	private void updateTotalFields(CreditCardStatement statement) {
		totalPurchasesField.setText(String.valueOf(statement.getTotalPurchases()));
		totalPurchaseAmountField.setText(FormatterUtil.formatAmount(statement.getTotalPurchaseAmount()));
		totalPaymentsField.setText(String.valueOf(statement.getTotalPayments()));
		totalPaymentAmountField.setText(FormatterUtil.formatAmount(statement.getTotalPaymentAmount()));
		balanceField.setText(FormatterUtil.formatAmount(statement.getBalance()));
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postCreditCardStatement();
			}
		});
		toolBar.add(postButton);
		
		deleteButton = new MagicToolBarButton("trash", "Delete");
		deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteCreditCardStatement();
			}
		});
		toolBar.add(deleteButton);
	}

	protected void deleteCreditCardStatement() {
		if (!confirm("Do you want to delete this credit card statement?")) {
			return;
		}
		
		try {
			creditCardService.delete(statement);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			showMessageForUnexpectedError();
			return;
		}
		
		showMessage("Credit card statement deleted!");
		getMagicFrame().switchToCreditCardStatementListPanel();
	}

	private void postCreditCardStatement() {
		if (!confirm("Do you want to post this credit card statement?")) {
			return;
		}
		
		statement.setPosted(true);
		try {
			creditCardService.save(statement);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			showMessageForUnexpectedError();
			return;
		}
		
		showMessage("Credit card statement posted!");
		updateDisplay(statement);
	}

	private class CreditCardStatementItemsTableModel extends ListBackedTableModel<CreditCardStatementItem> {

		private final String[] columnNames = {"PP No.", "Supplier", "Amount", "Credit Card", "Transaction Date"};
		
		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}
		
		public void removeItem(int rowIndex) {
			CreditCardStatementItem item = getItems().remove(rowIndex);
			creditCardService.delete(item);
			fireTableDataChanged();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			CreditCardStatementItem item = getItem(rowIndex);
			PurchasePaymentCreditCardPayment payment = item.getCreditCardPayment();
			switch (columnIndex) {
			case PURCHASE_PAYMENT_COLUMN_INDEX:
				return payment.getParent().getPurchasePaymentNumber();
			case SUPPLIER_COLUMN_INDEX:
				return payment.getParent().getSupplier().getName();
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(payment.getAmount());
			case CREDIT_CARD_COLUMN_INDEX:
				return payment.getCreditCard();
			case TRANSACTION_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(payment.getTransactionDate());
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case AMOUNT_COLUMN_INDEX:
				return Number.class;
			default:
				return Object.class;
			}
		}
		
	}
	
	private class AddCreditCardPaymentsToStatementDialog extends MagicDialog {

		private final int DIALOG_SELECT_COLUMN_INDEX = 0;
		private final int DIALOG_PURCHASE_PAYMENT_NUMBER_COLUMN_INDEX = 1;
		private final int DIALOG_SUPPLIER_COLUMN_INDEX = 2;
		private final int DIALOG_AMOUNT_COLUMN_INDEX = 3;
		private final int DIALOG_CREDIT_CARD_COLUMN_INDEX = 4;
		private final int DIALOG_TRANSACTION_DATE_COLUMN_INDEX = 5;

		private MagicListTable table;
		private PurchasePaymentCreditCardPaymentsTableModel tableModel;
		private JButton addButton;
		private JButton addAllButton;
		private List<PurchasePaymentCreditCardPayment> selectedPayments = new ArrayList<>();
		
		public AddCreditCardPaymentsToStatementDialog() {
			setSize(800, 250);
			setLocationRelativeTo(null);
			setTitle("Add Credit Card Payments to Statement");
			initializeComponents();
			layoutComponents();
		}

		private void initializeComponents() {
			addButton = new JButton("Add Selected");
			addButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectedPayments.addAll(tableModel.getSelectedItems());
					setVisible(false);
				}
			});
			
			addAllButton = new JButton("Add All");
			addAllButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectedPayments.addAll(tableModel.getItems());
					setVisible(false);
				}
			});
			
			initializeTable();
		}

		private void initializeTable() {
			tableModel = new PurchasePaymentCreditCardPaymentsTableModel();
			table = new MagicListTable(tableModel);
			
			TableColumnModel columnModel = table.getColumnModel();
			columnModel.getColumn(DIALOG_SELECT_COLUMN_INDEX).setPreferredWidth(20);
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
		
		public void updateDisplay() {
			selectedPayments.clear();
			List<PurchasePaymentCreditCardPayment> creditCardPayments =
					getUnpaidCreditCardPaymentsNotIncludedInStatement();
			tableModel.setItems(creditCardPayments);
		}

		private List<PurchasePaymentCreditCardPayment> 
				getUnpaidCreditCardPaymentsNotIncludedInStatement() {
			PurchasePaymentCreditCardPaymentSearchCriteria criteria =
					new PurchasePaymentCreditCardPaymentSearchCriteria();
			criteria.setNotIncludedInStatement(true);
			criteria.setCustomerNumber(statement.getCustomerNumber());
			
			return purchasePaymentService.searchCreditCardPayments(criteria);
		}
		
		private List<PurchasePaymentCreditCardPayment> getSelectedItems() {
			return tableModel.getSelectedItems();
		}
		
		private class PurchasePaymentCreditCardPaymentsTableModel 
				extends ListBackedTableModel<PurchasePaymentCreditCardPayment> {

			private final String[] columnNames = 
				{"", "PP No.", "Supplier", "Amount", "Credit Card", "Transaction Date"};
			
			private List<Integer> selected = new ArrayList<>();
			
			@Override
			public void setItems(List<PurchasePaymentCreditCardPayment> items) {
				super.setItems(items);
				selected.clear();
			}
			
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				PurchasePaymentCreditCardPayment creditCardPayment = getItem(rowIndex);
				switch (columnIndex) {
				case DIALOG_SELECT_COLUMN_INDEX:
					return selected.contains(rowIndex);
				case DIALOG_PURCHASE_PAYMENT_NUMBER_COLUMN_INDEX:
					return creditCardPayment.getParent().getPurchasePaymentNumber();
				case DIALOG_SUPPLIER_COLUMN_INDEX:
					return creditCardPayment.getParent().getSupplier().getName();
				case DIALOG_AMOUNT_COLUMN_INDEX:
					return FormatterUtil.formatAmount(creditCardPayment.getAmount());
				case DIALOG_CREDIT_CARD_COLUMN_INDEX:
					return creditCardPayment.getCreditCard();
				case DIALOG_TRANSACTION_DATE_COLUMN_INDEX:
					return FormatterUtil.formatDate(creditCardPayment.getTransactionDate());
				default:
					throw new RuntimeException("Fetch invalid column index: " + columnIndex);
				}
			}
			
			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				switch (columnIndex) {
				case DIALOG_SELECT_COLUMN_INDEX:
					if (selected.contains(rowIndex)) {
						selected.remove(selected.indexOf(rowIndex));
					} else {
						selected.add(rowIndex);
					}
				}
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return columnIndex == DIALOG_SELECT_COLUMN_INDEX;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case DIALOG_SELECT_COLUMN_INDEX:
					return Boolean.class;
				case DIALOG_AMOUNT_COLUMN_INDEX:
					return Number.class;
				default:
					return Object.class;
				}
			}

			@Override
			protected String[] getColumnNames() {
				return columnNames;
			}
			
			public List<PurchasePaymentCreditCardPayment> getSelectedItems() {
				List<PurchasePaymentCreditCardPayment> selectedItems = new ArrayList<>();
				for (Integer i : selected) {
					selectedItems.add(getItem(i));
				}
				return selectedItems;
			}
			
		}
		
	}
	
}