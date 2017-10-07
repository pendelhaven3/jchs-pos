package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.MagicFrame;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.AddReceivingReceiptsToPurchasePaymentDialog;
import com.pj.magic.gui.dialog.PrintChequeDialog;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.dialog.SelectSupplierDialog;
import com.pj.magic.gui.tables.PurchasePaymentBankTransfersTable;
import com.pj.magic.gui.tables.PurchasePaymentCashPaymentsTable;
import com.pj.magic.gui.tables.PurchasePaymentCheckPaymentsTable;
import com.pj.magic.gui.tables.PurchasePaymentCreditCardPaymentsTable;
import com.pj.magic.gui.tables.PurchasePaymentPaymentAdjustmentsTable;
import com.pj.magic.gui.tables.PurchasePaymentReceivingReceiptsTable;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentReceivingReceipt;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.Supplier;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.PurchasePaymentService;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class PurchasePaymentPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(PurchasePaymentPanel.class);
	
	private static final String SAVE_SUPPLIER_ACTION_NAME = "saveSupplier";
	private static final String OPEN_SELECT_SUPPLIER_DIALOG_ACTION_NAME = "openSelectSupplierDialog";
	
	@Autowired private PurchasePaymentReceivingReceiptsTable receivingReceiptsTable;
	@Autowired private PurchasePaymentService purchasePaymentService;
	@Autowired private SelectSupplierDialog selectSupplierDialog;
	@Autowired private SupplierService supplierService;
	@Autowired private AddReceivingReceiptsToPurchasePaymentDialog addReceivingReceiptsToPurchasePaymentDialog;
	@Autowired private PurchasePaymentCashPaymentsTable cashPaymentsTable;
	@Autowired private PurchasePaymentCreditCardPaymentsTable creditCardPaymentsTable;
	@Autowired private PurchasePaymentCheckPaymentsTable checkPaymentsTable;
	@Autowired private PurchasePaymentBankTransfersTable bankTransfersTable;
	@Autowired private PurchasePaymentPaymentAdjustmentsTable paymentAdjustmentsTable;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	@Autowired private LoginService loginService;
	@Autowired private PrintChequeDialog printChequeDialog;
	
	private PurchasePayment purchasePayment;
	private JLabel purchasePaymentNumberField;
	private JLabel statusLabel;
	private MagicTextField supplierCodeField;
	private JLabel supplierNameLabel;
	private JLabel paymentTermLabel;
	private JLabel totalAmountLabel;
	private JLabel totalCashPaymentsLabel;
	private JLabel totalCreditCardPaymentsLabel;
	private JLabel totalCheckPaymentsLabel;
	private JLabel totalBankTransfersLabel;
	private JLabel totalPaymentsLabel;
	private JLabel totalAdjustmentsLabel;
	private JLabel overOrShortLabel;
	private JButton selectSupplierButton;
	private MagicToolBarButton addReceivingReceiptButton;
	private MagicToolBarButton removeReceivingReceiptButton;
	private MagicToolBarButton addCashPaymentButton;
	private MagicToolBarButton removeCashPaymentButton;
	private MagicToolBarButton addCreditCardPaymentButton;
	private MagicToolBarButton removeCreditCardPaymentButton;
	private MagicToolBarButton addCheckPaymentButton;
	private MagicToolBarButton deleteCheckPaymentButton;
	private MagicToolBarButton addBankTransferButton;
	private MagicToolBarButton removeBankTransferButton;
	private MagicToolBarButton addAdjustmentButton;
	private MagicToolBarButton deleteAdjustmentButton;
	private MagicToolBarButton cancelButton;
	private MagicToolBarButton postButton;
	private MagicToolBarButton unpostButton;
	private JButton printPreviewButton;
	private JButton printButton;
	private JTabbedPane tabbedPane;
	
	@Override
	protected void initializeComponents() {
		purchasePaymentNumberField = new JLabel();
		statusLabel = new JLabel();
		
		supplierCodeField = new MagicTextField();
		supplierCodeField.setMaximumLength(Constants.CUSTOMER_CODE_MAXIMUM_LENGTH);
		
		selectSupplierButton = new EllipsisButton("Select Customer");
		selectSupplierButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectSupplierDialog();
			}
		});;
		
		focusOnComponentWhenThisPanelIsDisplayed(supplierCodeField);
		
		initializeModelListeners();
	}

	@Override
	protected void registerKeyBindings() {
		supplierCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_SUPPLIER_ACTION_NAME);
		supplierCodeField.getActionMap().put(SAVE_SUPPLIER_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSupplier();
			}
		});
		
		supplierCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), OPEN_SELECT_SUPPLIER_DIALOG_ACTION_NAME);
		supplierCodeField.getActionMap().put(OPEN_SELECT_SUPPLIER_DIALOG_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectSupplierDialog();
			}
		});
	}

	private void saveSupplier() {
		if (purchasePayment.getSupplier() != null) {
			if (purchasePayment.getSupplier().getCode().equals(supplierCodeField.getText())) {
				// skip saving since there is no change in supplier
				return;
			}
		}
		
		if (StringUtils.isEmpty(supplierCodeField.getText())) {
			showErrorMessage("Supplier must be specified");
			return;
		}
		
		Supplier supplier = supplierService.findSupplierByCode(supplierCodeField.getText());
		if (supplier == null) {
			showErrorMessage("No supplier matching code specified");
			return;
		} else {
			purchasePayment.setSupplier(supplier);
			purchasePaymentService.save(purchasePayment);
			updateDisplay(purchasePayment);
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					tabbedPane.requestFocusInWindow();
					tabbedPane.setSelectedIndex(0);
				}
			});
		}
	}

	protected void openSelectSupplierDialog() {
		selectSupplierDialog.searchSuppliers(supplierCodeField.getText());
		selectSupplierDialog.setVisible(true);
		
		Supplier supplier = selectSupplierDialog.getSelectedSupplier();
		if (supplier != null) {
			if (purchasePayment.getSupplier() != null && purchasePayment.getSupplier().equals(supplier)) {
				// skip saving since there is no change
				return;
			}
			purchasePayment.setSupplier(supplier);
			purchasePaymentService.save(purchasePayment);
			updateDisplay(purchasePayment);
		}
	}

	@Override
	protected void doOnBack() {
		if (receivingReceiptsTable.isEditing()) {
			receivingReceiptsTable.getCellEditor().cancelCellEditing();
		}
        getMagicFrame().back(MagicFrame.PURCHASE_PAYMENT_LIST_PANEL);
	}
	
	public void updateDisplay(PurchasePayment purchasePayment) {
		tabbedPane.setSelectedIndex(0);
		
		if (purchasePayment.getId() == null) {
			this.purchasePayment = purchasePayment;
			clearDisplay();
			return;
		}
		
		this.purchasePayment = purchasePayment = purchasePaymentService.getPurchasePayment(purchasePayment.getId());
		
		purchasePaymentNumberField.setText(purchasePayment.getPurchasePaymentNumber().toString());
		statusLabel.setText(purchasePayment.getStatus());
		supplierCodeField.setText(purchasePayment.getSupplier().getCode());
		supplierCodeField.setEnabled(!purchasePayment.isPosted());
		supplierNameLabel.setText(purchasePayment.getSupplier().getName());
		paymentTermLabel.setText(purchasePayment.getSupplier().getPaymentTerm().getName());
		totalAmountLabel.setText(FormatterUtil.formatAmount(purchasePayment.getTotalAmount()));
		totalCashPaymentsLabel.setText(FormatterUtil.formatAmount(purchasePayment.getTotalCashPayments()));
		totalCreditCardPaymentsLabel.setText(
				FormatterUtil.formatAmount(purchasePayment.getTotalCreditCardPayments()));
		totalCheckPaymentsLabel.setText(FormatterUtil.formatAmount(purchasePayment.getTotalCheckPayments()));
		totalBankTransfersLabel.setText(FormatterUtil.formatAmount(purchasePayment.getTotalBankTransfers()));
		totalPaymentsLabel.setText(FormatterUtil.formatAmount(purchasePayment.getTotalPayments()));
		totalAdjustmentsLabel.setText(FormatterUtil.formatAmount(purchasePayment.getTotalAdjustments()));
		overOrShortLabel.setText(FormatterUtil.formatAmount(purchasePayment.getOverOrShort()));
		
		receivingReceiptsTable.setPurchasePayment(purchasePayment);
		cashPaymentsTable.setPurchasePayment(purchasePayment);
		creditCardPaymentsTable.setPurchasePayment(purchasePayment);
		checkPaymentsTable.setPurchasePayment(purchasePayment);
		bankTransfersTable.setPurchasePayment(purchasePayment);
		paymentAdjustmentsTable.setPurchasePayment(purchasePayment);
		
		boolean newPayment = !purchasePayment.isPosted();
		selectSupplierButton.setEnabled(newPayment);
		cancelButton.setEnabled(newPayment);
		postButton.setEnabled(newPayment);
		unpostButton.setEnabled(purchasePayment.isPosted() && loginService.getLoggedInUser().isSupervisor());
		addReceivingReceiptButton.setEnabled(newPayment);
		removeReceivingReceiptButton.setEnabled(newPayment);
		addCashPaymentButton.setEnabled(newPayment);
		removeCashPaymentButton.setEnabled(newPayment);
		addCreditCardPaymentButton.setEnabled(newPayment);
		removeCreditCardPaymentButton.setEnabled(newPayment);
		addCheckPaymentButton.setEnabled(newPayment);
		deleteCheckPaymentButton.setEnabled(newPayment);
		addBankTransferButton.setEnabled(newPayment);
		removeBankTransferButton.setEnabled(newPayment);
		addAdjustmentButton.setEnabled(newPayment);
		deleteAdjustmentButton.setEnabled(newPayment);
		
		printPreviewButton.setEnabled(!purchasePayment.isCancelled());
		printButton.setEnabled(!purchasePayment.isCancelled());
	}

	private void clearDisplay() {
		purchasePaymentNumberField.setText(null);
		supplierCodeField.setEnabled(true);
		supplierCodeField.setText(null);
		supplierNameLabel.setText(null);
		paymentTermLabel.setText(null);
		selectSupplierButton.setEnabled(true);
		
		receivingReceiptsTable.clearDisplay();
		cashPaymentsTable.clearDisplay();
		creditCardPaymentsTable.clearDisplay();
		checkPaymentsTable.clearDisplay();
		bankTransfersTable.clearDisplay();
		paymentAdjustmentsTable.clearDisplay();
		
		totalAmountLabel.setText(null);
		totalCashPaymentsLabel.setText(null);
		totalCreditCardPaymentsLabel.setText(null);
		totalCheckPaymentsLabel.setText(null);
		totalBankTransfersLabel.setText(null);
		totalPaymentsLabel.setText(null);
		totalAdjustmentsLabel.setText(null);
		overOrShortLabel.setText(null);
		
		addReceivingReceiptButton.setEnabled(false);
		removeReceivingReceiptButton.setEnabled(false);
		addCashPaymentButton.setEnabled(false);
		removeCashPaymentButton.setEnabled(false);
		addCreditCardPaymentButton.setEnabled(false);
		removeCreditCardPaymentButton.setEnabled(false);
		addCheckPaymentButton.setEnabled(false);
		deleteCheckPaymentButton.setEnabled(false);
		addBankTransferButton.setEnabled(false);
		removeBankTransferButton.setEnabled(false);
		addAdjustmentButton.setEnabled(false);
		deleteAdjustmentButton.setEnabled(false);
		
		cancelButton.setEnabled(false);
		postButton.setEnabled(false);
		unpostButton.setEnabled(false);
		printPreviewButton.setEnabled(false);
		printButton.setEnabled(false);
	}

	private JPanel createSupplierPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(supplierCodeField, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectSupplierButton.setPreferredSize(new Dimension(30, 24));
		panel.add(selectSupplierButton, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createFiller(10, 20), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierNameLabel.setPreferredSize(new Dimension(300, 20));
		panel.add(supplierNameLabel, c);
		
		return panel;
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(180, "Purchase Payment No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		purchasePaymentNumberField = ComponentUtil.createLabel(150, "");
		mainPanel.add(purchasePaymentNumberField, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Status:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusLabel = ComponentUtil.createLabel(150, "");
		mainPanel.add(statusLabel, c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		mainPanel.add(Box.createGlue(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Supplier:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 3;
		
		supplierCodeField.setPreferredSize(new Dimension(140, 25));
		supplierNameLabel = ComponentUtil.createLabel(190, "");
		
		mainPanel.add(createSupplierPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Payment Term:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentTermLabel = ComponentUtil.createLabel(100);
		mainPanel.add(paymentTermLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 8;
		
		tabbedPane = createTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(600, 250));
		mainPanel.add(tabbedPane, c);
				
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createVerticalFiller(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 8;
		mainPanel.add(createTotalsPanel(), c);
				
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
//		mainPanel.add(ComponentUtil.createFiller(), c);
	}

	private JPanel createCheckPaymentsTableToolBar() {
		JPanel panel = new JPanel();
		
		addCheckPaymentButton = new MagicToolBarButton("plus_small", "Add Check Payment", true);
		addCheckPaymentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addCheckPayment();
			}
		});
		panel.add(addCheckPaymentButton, BorderLayout.WEST);
		
		deleteCheckPaymentButton = new MagicToolBarButton("minus_small", "Delete Check Payment", true);
		deleteCheckPaymentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				checkPaymentsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteCheckPaymentButton, BorderLayout.WEST);
		
		return panel;
	}

	private void addCheckPayment() {
		checkPaymentsTable.addNewRow();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		cancelButton = new MagicToolBarButton("cancel", "Cancel");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelPayment();
			}
		});
//		toolBar.add(cancelButton);
		
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postPayment();
			}
		});
		
		toolBar.add(postButton);
		
		unpostButton = new MagicToolBarButton("unpost", "Unpost");
		unpostButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				unpostPayment();
			}
		});
		toolBar.add(unpostButton);
		
		printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreview();
			}
		});
		toolBar.add(printPreviewButton);
		
		printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPaymentSummary();
			}
		});	
		toolBar.add(printButton);
		
		MagicToolBarButton printChequeButton = new MagicToolBarButton("cheque", "Print Cheque");
		printChequeButton.addActionListener(e -> printCheque());
		toolBar.add(printChequeButton);
	}

	private void printPaymentSummary() {
		printService.print(purchasePayment);
	}

	private void printPreview() {
		cancelEditing();
		printPreviewDialog.updateDisplay(printService.generateReportAsString(purchasePayment));
		printPreviewDialog.setUseCondensedFontForPrinting(true);
		printPreviewDialog.setVisible(true);
	}

	private void cancelEditing() {
		if (receivingReceiptsTable.isEditing()) {
			receivingReceiptsTable.getCellEditor().cancelCellEditing();
		}
		if (cashPaymentsTable.isEditing()) {
			cashPaymentsTable.getCellEditor().cancelCellEditing();
		}
		if (checkPaymentsTable.isEditing()) {
			checkPaymentsTable.getCellEditor().cancelCellEditing();
		}
		if (paymentAdjustmentsTable.isEditing()) {
			paymentAdjustmentsTable.getCellEditor().cancelCellEditing();
		}
	}

	private void postPayment() {
		cancelEditing();
		
		if (purchasePayment.getTotalPayments().equals(Constants.ZERO) &&
				purchasePayment.getTotalAdjustments().equals(Constants.ZERO)) {
			showErrorMessage("Cannot post with no payments or adjustments");
			return;
		}

		if (confirm("Do you want to post this Payment?")) {
			try {
				purchasePaymentService.post(purchasePayment);
				showMessage("Payment posted");
				updateDisplay(purchasePayment);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
	}

	private void cancelPayment() {
//		if (confirm("Cancel Payment?")) {
//			try {
//				supplierPaymentService.cancel(supplierPayment);
//				showMessage("Payment cancelled");
//				getMagicFrame().switchToSupplierPaymentPanel(supplierPayment);
//			} catch (Exception e) {
//				logger.error(e.getMessage(), e);
//				showMessageForUnexpectedError();
//			}
//		}
	}

	private JPanel createReceivingReceiptsTableToolBar() {
		JPanel panel = new JPanel();
		
		addReceivingReceiptButton = new MagicToolBarButton("plus_small", "Add Item (F10)", true);
		addReceivingReceiptButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addReceivingReceipt();
			}
		});
		panel.add(addReceivingReceiptButton, BorderLayout.WEST);
		
		removeReceivingReceiptButton = new MagicToolBarButton("minus_small", "Delete Item (Delete)", true);
		removeReceivingReceiptButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				receivingReceiptsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(removeReceivingReceiptButton, BorderLayout.WEST);
		
		return panel;
	}

	private void addReceivingReceipt() {
		addReceivingReceiptsToPurchasePaymentDialog.searchReceivingReceiptsForPayment(purchasePayment.getSupplier());
		addReceivingReceiptsToPurchasePaymentDialog.setVisible(true);
		
		List<ReceivingReceipt> receivingReceipts = 
				addReceivingReceiptsToPurchasePaymentDialog.getSelectedReceivingReceipts();
		if (!receivingReceipts.isEmpty()) {
			for (ReceivingReceipt receivingReceipt : receivingReceipts) {
				PurchasePaymentReceivingReceipt paymentReceivingReceipt = new PurchasePaymentReceivingReceipt();
				paymentReceivingReceipt.setParent(purchasePayment);
				paymentReceivingReceipt.setReceivingReceipt(receivingReceipt);
				purchasePaymentService.save(paymentReceivingReceipt);
				purchasePayment.getReceivingReceipts().add(paymentReceivingReceipt);
			}
			receivingReceiptsTable.setPurchasePayment(purchasePayment);
		}
	}
	
	private JTabbedPane createTabbedPane() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Receiving Receipts", createReceivingReceiptsPanel());
		tabbedPane.addTab("Cash Payments", createCashPaymentsPanel());
		tabbedPane.addTab("Credit Card Payments", createCreditCardPaymentsPanel());
		tabbedPane.addTab("Bank Transfers", createBankTransfersPanel());
		tabbedPane.addTab("Check Payments", createCheckPaymentsPanel());
		tabbedPane.addTab("Adjustments", createAdjustmentsPanel());
		return tabbedPane;
	}

	private JPanel createBankTransfersPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createBankTransfersTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane scrollPane = new JScrollPane(bankTransfersTable);
		scrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(scrollPane, c);
		
		return panel;
	}

	private JPanel createReceivingReceiptsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createReceivingReceiptsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane receivingReceiptsTableScrollPane = new JScrollPane(receivingReceiptsTable);
		receivingReceiptsTableScrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(receivingReceiptsTableScrollPane, c);
		
		return panel;
	}
	
	private JPanel createCheckPaymentsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createCheckPaymentsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane scrollPane = new JScrollPane(checkPaymentsTable);
		scrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(scrollPane, c);
		
		return panel;
	}
	
	private JPanel createTotalsPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;

		currentRow++;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Cash Payments:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalCashPaymentsLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalCashPaymentsLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Amount Due:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalAmountLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(200, "Total Credit Card Payments:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalCreditCardPaymentsLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalCreditCardPaymentsLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Payment:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalPaymentsLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalPaymentsLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(160, "Total Bank Transfers:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalBankTransfersLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalBankTransfersLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Adjustments:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAdjustmentsLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalAdjustmentsLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(160, "Total Check Payments:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalCheckPaymentsLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalCheckPaymentsLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Over/Short:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		overOrShortLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(overOrShortLabel, c);
		
		return mainPanel;
	}
	
	private JPanel createCashPaymentsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createCashPaymentsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane scrollPane = new JScrollPane(cashPaymentsTable);
		scrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(scrollPane, c);
		
		return panel;
	}
	
	private JPanel createCreditCardPaymentsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createCreditCardPaymentsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane scrollPane = new JScrollPane(creditCardPaymentsTable);
		scrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(scrollPane, c);
		
		return panel;
	}
	
	private JPanel createBankTransfersTableToolBar() {
		JPanel panel = new JPanel();
		
		addBankTransferButton = new MagicToolBarButton("plus_small", "Add Bank Transfer", true);
		addBankTransferButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addBankTransfer();
			}
		});
		panel.add(addBankTransferButton, BorderLayout.WEST);
		
		removeBankTransferButton = new MagicToolBarButton("minus_small", "Remove Bank Transfer", true);
		removeBankTransferButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				bankTransfersTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(removeBankTransferButton, BorderLayout.WEST);
		
		return panel;
	}

	private JPanel createCashPaymentsTableToolBar() {
		JPanel panel = new JPanel();
		
		addCashPaymentButton = new MagicToolBarButton("plus_small", "Add Cash Payment", true);
		addCashPaymentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addCashPayment();
			}
		});
		panel.add(addCashPaymentButton, BorderLayout.WEST);
		
		removeCashPaymentButton = new MagicToolBarButton("minus_small", "Delete Cash Payment", true);
		removeCashPaymentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cashPaymentsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(removeCashPaymentButton, BorderLayout.WEST);
		
		return panel;
	}

	private JPanel createCreditCardPaymentsTableToolBar() {
		JPanel panel = new JPanel();
		
		addCreditCardPaymentButton = new MagicToolBarButton("plus_small", "Add Credit Card Payment", true);
		addCreditCardPaymentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addCreditCardPayment();
			}
		});
		panel.add(addCreditCardPaymentButton, BorderLayout.WEST);
		
		removeCreditCardPaymentButton = new MagicToolBarButton("minus_small", "Remove Credit Card Payment", true);
		removeCreditCardPaymentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				creditCardPaymentsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(removeCreditCardPaymentButton, BorderLayout.WEST);
		
		return panel;
	}

	private void addCreditCardPayment() {
		creditCardPaymentsTable.addNewRow();
	}

	private void addCashPayment() {
		cashPaymentsTable.addNewRow();
	}

	private void addBankTransfer() {
		bankTransfersTable.addNewRow();
	}

	private JPanel createAdjustmentsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createAdjustmentsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane scrollPane = new JScrollPane(paymentAdjustmentsTable);
		scrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(scrollPane, c);
		
		return panel;
	}

	private JPanel createAdjustmentsTableToolBar() {
		JPanel panel = new JPanel();
		
		addAdjustmentButton = new MagicToolBarButton("plus_small", "Add Adjustment", true);
		addAdjustmentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addAdjustment();
			}
		});
		panel.add(addAdjustmentButton, BorderLayout.WEST);
		
		deleteAdjustmentButton = new MagicToolBarButton("minus_small", "Delete Adjustment", true);
		deleteAdjustmentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				paymentAdjustmentsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteAdjustmentButton, BorderLayout.WEST);
		
		return panel;
	}

	private void addAdjustment() {
		paymentAdjustmentsTable.addNewRow();
	}

	private void initializeModelListeners() {
		receivingReceiptsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalAmountLabel.setText(FormatterUtil.formatAmount(purchasePayment.getTotalAmount()));
			}
		});
		
		cashPaymentsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalCashPaymentsLabel.setText(FormatterUtil.formatAmount(purchasePayment.getTotalCashPayments()));
				totalPaymentsLabel.setText(FormatterUtil.formatAmount(purchasePayment.getTotalPayments()));
				overOrShortLabel.setText(FormatterUtil.formatAmount(purchasePayment.getOverOrShort()));
			}
		});

		creditCardPaymentsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalCreditCardPaymentsLabel.setText(
						FormatterUtil.formatAmount(purchasePayment.getTotalCreditCardPayments()));
				totalPaymentsLabel.setText(FormatterUtil.formatAmount(purchasePayment.getTotalPayments()));
				overOrShortLabel.setText(FormatterUtil.formatAmount(purchasePayment.getOverOrShort()));
			}
		});
		
		checkPaymentsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalCheckPaymentsLabel.setText(FormatterUtil.formatAmount(purchasePayment.getTotalCheckPayments()));
				totalPaymentsLabel.setText(FormatterUtil.formatAmount(purchasePayment.getTotalPayments()));
				overOrShortLabel.setText(FormatterUtil.formatAmount(purchasePayment.getOverOrShort()));
			}
		});

		bankTransfersTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalBankTransfersLabel.setText(FormatterUtil.formatAmount(purchasePayment.getTotalBankTransfers()));
				totalPaymentsLabel.setText(FormatterUtil.formatAmount(purchasePayment.getTotalPayments()));
				overOrShortLabel.setText(FormatterUtil.formatAmount(purchasePayment.getOverOrShort()));
			}
		});

		paymentAdjustmentsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalAdjustmentsLabel.setText(FormatterUtil.formatAmount(purchasePayment.getTotalAdjustments()));
				overOrShortLabel.setText(FormatterUtil.formatAmount(purchasePayment.getOverOrShort()));
			}
		});
	}
	
	private void unpostPayment() {
		cancelEditing();
		
		if (confirm("Unpost payment?")) {
			try {
				purchasePaymentService.unpost(purchasePayment);
				showMessage("Payment unposted");
				updateDisplay(purchasePayment);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
			}
		}
	}
	
	private void printCheque() {
		printChequeDialog.updateDisplay(purchasePayment);
		printChequeDialog.setVisible(true);
	}
	
}