package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.MessageFormat;
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
import com.pj.magic.exception.UserNotAssignedToPaymentTerminalException;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.AddCashPaymentAndPostDialog;
import com.pj.magic.gui.dialog.AddSalesInvoicesToPaymentDialog;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.dialog.StatusDetailsDialog;
import com.pj.magic.gui.tables.PaymentCashPaymentsTable;
import com.pj.magic.gui.tables.PaymentCheckPaymentsTable;
import com.pj.magic.gui.tables.PaymentPaymentAdjustmentsTable;
import com.pj.magic.gui.tables.PaymentSalesInvoicesTable;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.User;
import com.pj.magic.service.CustomerService;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PaymentService;
import com.pj.magic.service.PaymentTerminalService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.SalesReturnService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.HtmlUtil;

@Component
public class PaymentPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(PaymentPanel.class);
	
	private static final String SAVE_CUSTOMER_ACTION_NAME = "saveCustomer";
	private static final String OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME = "openSelectCustomerDialog";
	
	@Autowired private PaymentSalesInvoicesTable salesInvoicesTable;
	@Autowired private PaymentService paymentService;
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	@Autowired private CustomerService customerService;
	@Autowired private AddSalesInvoicesToPaymentDialog addSalesInvoicesToPaymentDialog;
	@Autowired private PaymentCheckPaymentsTable checksTable;
	@Autowired private PaymentCashPaymentsTable cashPaymentsTable;
	@Autowired private PaymentPaymentAdjustmentsTable adjustmentsTable;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	@Autowired private LoginService loginService;
	@Autowired private PaymentTerminalService paymentTerminalService;
	@Autowired private SalesReturnService salesReturnService;
	@Autowired private StatusDetailsDialog statusDetailsDialog;
	@Autowired private AddCashPaymentAndPostDialog addCashPaymentAndPostDialog;
	
	private Payment payment;
	private JLabel paymentNumberField;
	private JLabel statusField;
	private MagicTextField customerCodeField;
	private JLabel customerNameField;
	private JLabel paymentTerminalField;
	private JLabel cashAmountGivenField;
	private JLabel cashChangeField;
	private JLabel totalAmountDueField;
	private JLabel totalCashPaymentsField;
	private JLabel totalCheckPaymentsField;
	private JLabel totalPaymentsField;
	private JLabel totalAdjustmentsField;
	private JLabel overOrShortField;
	private JButton selectCustomerButton;
	private MagicToolBarButton addSalesInvoiceButton;
	private MagicToolBarButton deleteSalesInvoiceButton;
	private MagicToolBarButton addCheckPaymentButton;
	private MagicToolBarButton deleteCheckPaymentButton;
	private MagicToolBarButton addCashPaymentButton;
	private MagicToolBarButton deleteCashPaymentButton;
	private MagicToolBarButton addAdjustmentButton;
	private MagicToolBarButton deleteAdjustmentButton;
	private MagicToolBarButton cancelButton;
	private MagicToolBarButton postButton;
	private MagicToolBarButton createCashPaymentAndPostButton;
	private MagicToolBarButton unpostButton;
	private JButton printPreviewButton;
	private JButton printButton;
	private JTabbedPane tabbedPane;
	
	@Override
	protected void initializeComponents() {
		paymentNumberField = new JLabel();
		
		statusField = new JLabel();
		statusField.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				statusDetailsDialog.updateDisplay(payment);				
				statusDetailsDialog.setVisible(true);				
			}
		});
		statusField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		customerCodeField = new MagicTextField();
		customerCodeField.setMaximumLength(Constants.CUSTOMER_CODE_MAXIMUM_LENGTH);
		
		selectCustomerButton = new EllipsisButton();
		selectCustomerButton.setToolTipText("Select Customer");
		selectCustomerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectCustomer();
			}
		});;
		
		focusOnComponentWhenThisPanelIsDisplayed(customerCodeField);
		
		initializeModelListeners();
	}

	@Override
	protected void registerKeyBindings() {
		customerCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_CUSTOMER_ACTION_NAME);
		customerCodeField.getActionMap().put(SAVE_CUSTOMER_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveCustomer();
			}
		});
		
		customerCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME);
		customerCodeField.getActionMap().put(OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectCustomer();
			}
		});
	}

	private void saveCustomer() {
		if (payment.getCustomer() != null) {
			if (payment.getCustomer().getCode().equals(customerCodeField.getText())) {
				// skip saving sales requisition since there is no change in customer
				return;
			}
		}
		
		if (StringUtils.isEmpty(customerCodeField.getText())) {
			showErrorMessage("Customer must be specified");
			return;
		}
		
		Customer customer = customerService.findCustomerByCode(customerCodeField.getText());
		if (customer == null || !customer.isActive()) {
			showErrorMessage("No customer matching code specified");
			return;
		} else {
			payment.setCustomer(customer);
			paymentService.save(payment);
			updateDisplay(payment);
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					tabbedPane.requestFocusInWindow();
					tabbedPane.setSelectedIndex(0);
				}
			});
		}
	}

	protected void selectCustomer() {
		selectCustomerDialog.searchActiveCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			if (payment.getCustomer() != null && payment.getCustomer().equals(customer)) {
				// skip saving sales requisition since there is no change
				return;
			}
			payment.setCustomer(customer);
			paymentService.save(payment);
			updateDisplay(payment);
		}
	}

	@Override
	protected void doOnBack() {
		if (salesInvoicesTable.isEditing()) {
			salesInvoicesTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToPaymentListPanel();
	}
	
	public void updateDisplay(Payment payment) {
		tabbedPane.setSelectedIndex(0);
		
		if (payment.getId() == null) {
			this.payment = payment;
			clearDisplay();
			return;
		}
		
		this.payment = payment = paymentService.getPayment(payment.getId());
		
		paymentNumberField.setText(payment.getPaymentNumber().toString());
		statusField.setText(HtmlUtil.blueUnderline(payment.getStatus()));
		customerCodeField.setText(payment.getCustomer().getCode());
		customerCodeField.setEnabled(payment.isNew());
		customerNameField.setText(payment.getCustomer().getName());
		paymentTerminalField.setText(payment.isPosted() ? payment.getPaymentTerminal().getName() : null);
		if (payment.getCashAmountGiven() != null) {
			cashAmountGivenField.setText(FormatterUtil.formatAmount(payment.getCashAmountGiven()));
			cashChangeField.setText(FormatterUtil.formatAmount(payment.getCashChange()));
		} else {
			cashAmountGivenField.setText(null);
			cashChangeField.setText(null);
		}
		totalAmountDueField.setText(FormatterUtil.formatAmount(payment.getTotalAmountDue()));
		totalCashPaymentsField.setText(FormatterUtil.formatAmount(payment.getTotalCashPayments()));
		totalCheckPaymentsField.setText(FormatterUtil.formatAmount(payment.getTotalCheckPayments()));
		totalPaymentsField.setText(FormatterUtil.formatAmount(payment.getTotalPayments()));
		totalAdjustmentsField.setText(FormatterUtil.formatAmount(payment.getTotalAdjustments()));
		overOrShortField.setText(FormatterUtil.formatAmount(payment.getOverOrShort()));
		
		salesInvoicesTable.setPayment(payment);
		checksTable.setPayment(payment);
		cashPaymentsTable.setPayment(payment);
		adjustmentsTable.setPayment(payment);
		
		boolean newPayment = payment.isNew();
		selectCustomerButton.setEnabled(newPayment);
		cancelButton.setEnabled(newPayment);
		postButton.setEnabled(newPayment);
		createCashPaymentAndPostButton.setEnabled(newPayment);
		addSalesInvoiceButton.setEnabled(newPayment);
		deleteSalesInvoiceButton.setEnabled(newPayment);
		addCashPaymentButton.setEnabled(newPayment);
		deleteCashPaymentButton.setEnabled(newPayment);
		addCheckPaymentButton.setEnabled(newPayment);
		deleteCheckPaymentButton.setEnabled(newPayment);
		addAdjustmentButton.setEnabled(newPayment);
		deleteAdjustmentButton.setEnabled(newPayment);
		
		printPreviewButton.setEnabled(!payment.isCancelled());
		printButton.setEnabled(!payment.isCancelled());
		
		unpostButton.setEnabled(payment.isPosted() && loginService.getLoggedInUser().isSupervisor());
	}

	private void clearDisplay() {
		paymentNumberField.setText(null);
		statusField.setText(null);
		customerCodeField.setEnabled(true);
		customerCodeField.setText(null);
		customerNameField.setText(null);
		selectCustomerButton.setEnabled(true);
		paymentTerminalField.setText(null);
		cashAmountGivenField.setText(null);
		cashChangeField.setText(null);
		
		salesInvoicesTable.clearDisplay();
		checksTable.clearDisplay();
		cashPaymentsTable.clearDisplay();
		adjustmentsTable.clearDisplay();
		
		totalAmountDueField.setText(null);
		totalCashPaymentsField.setText(null);
		totalCheckPaymentsField.setText(null);
		totalPaymentsField.setText(null);
		totalAdjustmentsField.setText(null);
		overOrShortField.setText(null);
		
		addSalesInvoiceButton.setEnabled(false);
		deleteSalesInvoiceButton.setEnabled(false);
		addCashPaymentButton.setEnabled(false);
		deleteCashPaymentButton.setEnabled(false);
		addCheckPaymentButton.setEnabled(false);
		deleteCheckPaymentButton.setEnabled(false);
		addAdjustmentButton.setEnabled(false);
		deleteAdjustmentButton.setEnabled(false);
		
		cancelButton.setEnabled(false);
		postButton.setEnabled(false);
		createCashPaymentAndPostButton.setEnabled(false);
		unpostButton.setEnabled(false);
		printPreviewButton.setEnabled(false);
		printButton.setEnabled(false);
	}

	private JPanel createCustomerPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(customerCodeField, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectCustomerButton.setPreferredSize(new Dimension(30, 24));
		panel.add(selectCustomerButton, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createFiller(10, 20), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		customerNameField.setPreferredSize(new Dimension(300, 20));
		panel.add(customerNameField, c);
		
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
		mainPanel.add(ComponentUtil.createFiller(50, 30), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Payment No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentNumberField = ComponentUtil.createLabel(150, "");
		mainPanel.add(paymentNumberField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(100), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(80, "Status:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusField.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(statusField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Customer:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 4;
		
		customerCodeField.setPreferredSize(new Dimension(140, 25));
		customerNameField = ComponentUtil.createLabel(190, "");
		
		mainPanel.add(createCustomerPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Terminal:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentTerminalField = ComponentUtil.createLabel(100, "");
		mainPanel.add(paymentTerminalField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Amount Given:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		cashAmountGivenField = ComponentUtil.createLabel(100, "");
		mainPanel.add(cashAmountGivenField, c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Change:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		cashChangeField = ComponentUtil.createLabel(100, "");
		mainPanel.add(cashChangeField, c);

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
		c.gridwidth = 6;
		
		tabbedPane = createTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(600, 250));
		mainPanel.add(tabbedPane, c);
				
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(5), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		mainPanel.add(createTotalsPanel(), c);
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
				checksTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteCheckPaymentButton, BorderLayout.WEST);
		
		return panel;
	}

	private void addCheckPayment() {
		checksTable.addNewRow();
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
		toolBar.add(cancelButton);
		
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
		
		createCashPaymentAndPostButton = new MagicToolBarButton("cash_pay_and_post", 
				"Create Cash Payment and Post");
		createCashPaymentAndPostButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openAddCashPaymentAndPostDialog();
			}
		});
		toolBar.add(createCashPaymentAndPostButton);

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
	}

	private void openAddCashPaymentAndPostDialog() {
		if (payment.hasCashPayment()) {
			showErrorMessage("Cannot use this option if cash payment has already been specified");
			return;
		}
		
		addCashPaymentAndPostDialog.updateDisplay(payment);
		addCashPaymentAndPostDialog.setVisible(true);
		
		updateDisplay(payment);
	}

	private void printPaymentSummary() {
		printService.print(payment);
	}

	private void printPreview() {
		cancelEditing();
		printPreviewDialog.updateDisplay(printService.generateReportAsString(payment));
		printPreviewDialog.setUseCondensedFontForPrinting(true);
		printPreviewDialog.setVisible(true);
	}

	private void cancelEditing() {
		if (salesInvoicesTable.isEditing()) {
			salesInvoicesTable.getCellEditor().cancelCellEditing();
		}
		if (cashPaymentsTable.isEditing()) {
			cashPaymentsTable.getCellEditor().cancelCellEditing();
		}
		if (checksTable.isEditing()) {
			checksTable.getCellEditor().cancelCellEditing();
		}
		if (adjustmentsTable.isEditing()) {
			adjustmentsTable.getCellEditor().cancelCellEditing();
		}
	}

	private void postPayment() {
		cancelEditing();
		
		if (!isUserAssignedToPaymentTerminal()) {
			showErrorMessage("User is not assigned to a payment terminal");
			return;
		}
		
		if (payment.getTotalPayments().equals(Constants.ZERO)) {
			showErrorMessage("Cannot post with no cash or check payments");
			return;
		}

		if (confirm(getPostConfirmMessage())) {
			try {
				paymentService.post(payment);
				showMessage("Payment posted");
				updateDisplay(payment);
			} catch (UserNotAssignedToPaymentTerminalException e) {
				showErrorMessage("User " + loginService.getLoggedInUser().getUsername()
						+ " is not assigned to payment terminal");
				return;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
	}

	private boolean isUserAssignedToPaymentTerminal() {
		User user = loginService.getLoggedInUser();
		PaymentTerminalAssignment assignment = paymentTerminalService.findPaymentTerminalAssignment(user);
		return assignment != null;
	}

	private String getPostConfirmMessage() {
		String message = "Do you want to post this Payment?";
		BigDecimal overOrShort = payment.getOverOrShort();
		if (overOrShort.compareTo(Constants.ZERO) != 0) {
			message = "This payment is over/short by {0}.\nAre you sure you want to post this payment?";
			message = MessageFormat.format(message, FormatterUtil.formatAmount(overOrShort));
		}
		return message;
	}
	
	private void cancelPayment() {
		if (confirm("Cancel Payment?")) {
			try {
				paymentService.cancel(payment);
				showMessage("Payment cancelled");
				getMagicFrame().switchToPaymentPanel(payment);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
			}
		}
	}

	private JPanel createSalesInvoicesTableToolBar() {
		JPanel panel = new JPanel();
		
		addSalesInvoiceButton = new MagicToolBarButton("plus_small", "Add Item (F10)", true);
		addSalesInvoiceButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addSalesInvoice();
			}
		});
		panel.add(addSalesInvoiceButton, BorderLayout.WEST);
		
		deleteSalesInvoiceButton = new MagicToolBarButton("minus_small", "Delete Item (Delete)", true);
		deleteSalesInvoiceButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				salesInvoicesTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteSalesInvoiceButton, BorderLayout.WEST);
		
		return panel;
	}

	private void addSalesInvoice() {
		addSalesInvoicesToPaymentDialog.searchSalesInvoicesForPayment(payment.getCustomer());
		addSalesInvoicesToPaymentDialog.setVisible(true);
		
		List<SalesInvoice> selectedSalesInvoices = addSalesInvoicesToPaymentDialog.getSelectedSalesInvoices();
		if (!selectedSalesInvoices.isEmpty()) {
			for (SalesInvoice salesInvoice : selectedSalesInvoices) {
				PaymentSalesInvoice paymentSalesInvoice = new PaymentSalesInvoice();
				paymentSalesInvoice.setParent(payment);
				paymentSalesInvoice.setSalesInvoice(salesInvoice);
				paymentService.save(paymentSalesInvoice);
				paymentSalesInvoice.setSalesReturns(
						salesReturnService.findPostedSalesReturnsBySalesInvoice(salesInvoice));
				payment.getSalesInvoices().add(paymentSalesInvoice);
			}
			salesInvoicesTable.setPayment(payment);
		}
	}
	
	private JTabbedPane createTabbedPane() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Sales Invoices", createSalesInvoicesPanel());
		tabbedPane.addTab("Cash Payments", createCashPaymentsPanel());
		tabbedPane.addTab("Check Payments", createCheckPaymentsPanel());
		tabbedPane.addTab("Adjustments", createAdjustmentsPanel());
		return tabbedPane;
	}

	private JPanel createSalesInvoicesPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createSalesInvoicesTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane salesInvoicesTableScrollPane = new JScrollPane(salesInvoicesTable);
		salesInvoicesTableScrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(salesInvoicesTableScrollPane, c);
		
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
		JScrollPane scrollPane = new JScrollPane(checksTable);
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
		totalAmountDueField = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalAmountDueField, c);
		
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
		totalCheckPaymentsField = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalCheckPaymentsField, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Payment:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalPaymentsField = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalPaymentsField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Cash Payments:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalCashPaymentsField = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalCashPaymentsField, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Adjustments:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAdjustmentsField = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalAdjustmentsField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Over/Short:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		overOrShortField = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(overOrShortField, c);
		
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
		
		deleteCashPaymentButton = new MagicToolBarButton("minus_small", "Delete Cash Payment", true);
		deleteCashPaymentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cashPaymentsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteCashPaymentButton, BorderLayout.WEST);
		
		return panel;
	}

	private void addCashPayment() {
		cashPaymentsTable.addNewRow();
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
		JScrollPane scrollPane = new JScrollPane(adjustmentsTable);
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
				adjustmentsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteAdjustmentButton, BorderLayout.WEST);
		
		return panel;
	}

	private void addAdjustment() {
		adjustmentsTable.addNewRow();
	}

	private void initializeModelListeners() {
		salesInvoicesTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalAmountDueField.setText(FormatterUtil.formatAmount(payment.getTotalAmountDue()));
				overOrShortField.setText(FormatterUtil.formatAmount(payment.getOverOrShort()));
			}
		});
		
		checksTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalCheckPaymentsField.setText(FormatterUtil.formatAmount(payment.getTotalCheckPayments()));
				totalPaymentsField.setText(FormatterUtil.formatAmount(payment.getTotalPayments()));
				overOrShortField.setText(FormatterUtil.formatAmount(payment.getOverOrShort()));
			}
		});

		cashPaymentsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalCashPaymentsField.setText(FormatterUtil.formatAmount(payment.getTotalCashPayments()));
				totalPaymentsField.setText(FormatterUtil.formatAmount(payment.getTotalPayments()));
				overOrShortField.setText(FormatterUtil.formatAmount(payment.getOverOrShort()));
			}
		});

		adjustmentsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalAdjustmentsField.setText(FormatterUtil.formatAmount(payment.getTotalAdjustments()));
				overOrShortField.setText(FormatterUtil.formatAmount(payment.getOverOrShort()));
			}
		});
	}

	private void unpostPayment() {
		cancelEditing();
		
		if (confirm("Unpost payment?")) {
			try {
				paymentService.unpost(payment);
				showMessage("Payment unposted");
				updateDisplay(payment);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during unposting!");
			}
		}
	}
	
}