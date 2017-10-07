package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.AlreadyCancelledException;
import com.pj.magic.exception.AlreadyPaidException;
import com.pj.magic.exception.NoItemException;
import com.pj.magic.gui.MagicFrame;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.dialog.StatusDetailsDialog;
import com.pj.magic.gui.tables.BadStockReturnItemsTable;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.User;
import com.pj.magic.service.BadStockReturnService;
import com.pj.magic.service.CustomerService;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PaymentTerminalService;
import com.pj.magic.service.PrintService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.HtmlUtil;

@Component
public class BadStockReturnPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(BadStockReturnPanel.class);
	
	private static final String SAVE_CUSTOMER_ACTION_NAME = "SAVE_CUSTOMER_ACTION_NAME";
	private static final String OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME = 
			"OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME";
	
	@Autowired private BadStockReturnItemsTable itemsTable;
	@Autowired private BadStockReturnService badStockReturnService;
	@Autowired private CustomerService customerService;
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	@Autowired private PrintService printService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private LoginService loginService;
	@Autowired private PaymentTerminalService paymentTerminalService;
	@Autowired private StatusDetailsDialog statusDetailsDialog;
	
	private BadStockReturn badStockReturn;
	private JLabel badStockReturnNumberField;
	private JTextField customerCodeField;
	private JLabel customerNameField;
	private JButton selectCustomerButton;
	private JLabel paymentNumberField;
	private JLabel statusField;
	private MagicTextField remarksField;
	private JLabel totalItemsField;
	private JLabel totalAmountField;
	private JButton postButton;
	private JButton markAsPaidButton;
	private JButton cancelButton;
	private JButton addItemButton;
	private JButton deleteItemButton;
	private JButton printPreviewButton;
	private JButton printButton;
	
	@Override
	protected void initializeComponents() {
		customerCodeField = new MagicTextField();
		customerNameField = new JLabel();
		
		selectCustomerButton = new EllipsisButton();
		selectCustomerButton.setToolTipText("Select Customer (F5)");
		selectCustomerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectCustomer();
			}
		});;
		
		statusField = new JLabel();
		statusField.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				statusDetailsDialog.updateDisplay(badStockReturn);
				statusDetailsDialog.setVisible(true);
			}
			
		});
		statusField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(100);
		remarksField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveRemarks();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(customerCodeField);
		updateTotalAmountFieldWhenItemsTableChanges();
	}

	private void saveRemarks() {
		if (!remarksField.getText().equals(badStockReturn.getRemarks())) {
			badStockReturn.setRemarks(remarksField.getText());
			badStockReturnService.save(badStockReturn);
		}
	}
	
	private void selectCustomer() {
		selectCustomerDialog.searchActiveCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			if (badStockReturn.getCustomer() != null && badStockReturn.getCustomer().equals(customer)) {
				// skip saving Bad Stock Return since there is no change
				remarksField.requestFocusInWindow();
				return;
			} else {
				badStockReturn.setCustomer(customer);
				try {
					badStockReturnService.save(badStockReturn);
					updateDisplay(badStockReturn);
					remarksField.requestFocusInWindow();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					showMessageForUnexpectedError();
				}
			}
		}
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
		
		remarksField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.highlight();
			}
		});
	}

	private void saveCustomer() {
		if (badStockReturn.getCustomer() != null) {
			if (badStockReturn.getCustomer().getCode().equals(customerCodeField.getText())) {
				// skip saving Bad Stock Return since there is no change in customer
				remarksField.requestFocusInWindow();
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
			badStockReturn.setCustomer(customer);
			try {
				badStockReturnService.save(badStockReturn);
				updateDisplay(badStockReturn);
				remarksField.requestFocusInWindow();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
			}
		}
	}

	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().back(MagicFrame.BAD_STOCK_RETURN_LIST_PANEL);
	}
	
	private void updateTotalAmountFieldWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalItemsField.setText(String.valueOf(badStockReturn.getTotalItems()));
				totalAmountField.setText(FormatterUtil.formatAmount(badStockReturn.getTotalAmount()));
			}
		});
	}

	public void updateDisplay(BadStockReturn badStockReturn) {
		if (badStockReturn.getId() == null) {
			this.badStockReturn = badStockReturn;
			clearDisplay();
			return;
		}
		
		this.badStockReturn = badStockReturn = badStockReturnService.getBadStockReturn(badStockReturn.getId());
		
		badStockReturnNumberField.setText(badStockReturn.getBadStockReturnNumber().toString());
		customerCodeField.setText(badStockReturn.getCustomer().getCode());
		customerCodeField.setEnabled(!badStockReturn.isPosted());
		customerNameField.setText(badStockReturn.getCustomer().getName());
		selectCustomerButton.setEnabled(!badStockReturn.isPosted());
		if (badStockReturn.getPaymentNumber() != null) {
			paymentNumberField.setText(badStockReturn.getPaymentNumber().toString());
		} else {
			paymentNumberField.setText(null);
		}
		statusField.setText(HtmlUtil.blueUnderline(badStockReturn.getStatus()));
		remarksField.setText(badStockReturn.getRemarks());
		remarksField.setEnabled(!badStockReturn.isPosted());
		totalItemsField.setText(String.valueOf(badStockReturn.getTotalItems()));
		totalAmountField.setText(badStockReturn.getTotalAmount().toString());
		postButton.setEnabled(!badStockReturn.isPosted());
		markAsPaidButton.setEnabled(badStockReturn.isPosted() && !badStockReturn.isPaid() && !badStockReturn.isCancelled());
		cancelButton.setEnabled(badStockReturn.isPosted() && !badStockReturn.isPaid() && !badStockReturn.isCancelled());
		addItemButton.setEnabled(!badStockReturn.isPosted());
		deleteItemButton.setEnabled(!badStockReturn.isPosted());
		printPreviewButton.setEnabled(true);
		printButton.setEnabled(true);
		
		itemsTable.setBadStockReturn(badStockReturn);
	}

	private void clearDisplay() {
		badStockReturnNumberField.setText(null);
		customerCodeField.setText(null);
		customerCodeField.setEnabled(true);
		customerNameField.setText(null);
		selectCustomerButton.setEnabled(true);
		statusField.setText(null);
		remarksField.setText(null);
		remarksField.setEnabled(false);
		totalItemsField.setText(null);
		totalAmountField.setText(null);
		itemsTable.setBadStockReturn(badStockReturn);
		postButton.setEnabled(false);
		markAsPaidButton.setEnabled(false);
		cancelButton.setEnabled(false);
		addItemButton.setEnabled(false);
		deleteItemButton.setEnabled(false);
		printPreviewButton.setEnabled(false);
		printButton.setEnabled(false);
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
		mainPanel.add(ComponentUtil.createLabel(100, "BSR No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		badStockReturnNumberField = ComponentUtil.createLabel(200, "");
		mainPanel.add(badStockReturnNumberField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Status:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(statusField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Customer:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createCustomerPanel(), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Payment No.:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentNumberField = ComponentUtil.createLabel(100);
		mainPanel.add(paymentNumberField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Remarks:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(remarksField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createItemsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}
	
	private JPanel createCustomerPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		customerCodeField.setPreferredSize(new Dimension(100, 25));
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
		panel.add(Box.createHorizontalStrut(10), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		customerNameField.setPreferredSize(new Dimension(200, 20));
		panel.add(customerNameField, c);
		
		return panel;
	}
	
	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsField = ComponentUtil.createLabel(60);
		panel.add(totalItemsField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(Box.createHorizontalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = ComponentUtil.createLabel(100);
		panel.add(totalAmountField, c);
		
		return panel;
	}
	
	private JPanel createItemsTableToolBar() {
		JPanel panel = new JPanel();
		
		addItemButton = new MagicToolBarButton("plus_small", "Add Item", true);
		addItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.switchToAddMode();
			}
		});
		panel.add(addItemButton, BorderLayout.WEST);
		
		deleteItemButton = new MagicToolBarButton("minus_small", "Delete Item", true);
		deleteItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
	}

	private void postBadStockReturn() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}
		
		if (confirm("Do you want to post this Bad Stock Return?")) { 
			if (!badStockReturn.hasItems()) {
				showErrorMessage("Cannot post a Bad Stock Return with no items");
				itemsTable.requestFocusInWindow();
				return;
			}
			try {
				badStockReturnService.post(badStockReturn);
				showMessage("Post successful!");
				updateDisplay(badStockReturn);
			} catch (NoItemException e) {
				showErrorMessage(e.getMessage());
				updateDisplay(badStockReturn);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postBadStockReturn();
			}
		});
		toolBar.add(postButton);
		
		markAsPaidButton = new MagicToolBarButton("coins", "Mark As Paid");
		markAsPaidButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				markBadStockReturnAsPaid();
			}
		});
		toolBar.add(markAsPaidButton);
		
		cancelButton = new MagicToolBarButton("cancel", "Cancel");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelBadStockReturn();
			}
		});
		toolBar.add(cancelButton);
		
		printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreviewDialog.updateDisplay(printService.generateReportAsString(badStockReturn));
				printPreviewDialog.setVisible(true);
			}
		});
		toolBar.add(printPreviewButton);
		
		printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printService.print(badStockReturn);
			}
		});
		toolBar.add(printButton);
	}

	private void cancelBadStockReturn() {
		if (confirm("Do you want to cancel this Bad Stock Return?")) { 
			try {
				badStockReturnService.cancel(badStockReturn);
				showMessage("Bad Stock Return cancelled");
				updateDisplay(badStockReturn);
			} catch (AlreadyCancelledException e) {
				showErrorMessage("Bad Stock Return is already cancelled");
				updateDisplay(badStockReturn);
			} catch (AlreadyPaidException e) {
				showErrorMessage(e.getMessage());
				updateDisplay(badStockReturn);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
	}

	private void markBadStockReturnAsPaid() {
		if (!isUserAssignedToPaymentTerminal()) {
			showErrorMessage("User is not assigned to a payment terminal");
			return;
		}
		
		if (confirm("Mark Bad Stock Return as paid?")) {
			try {
				badStockReturnService.markAsPaid(badStockReturn);
				JOptionPane.showMessageDialog(this, "Bad Stock Return marked as paid");
				updateDisplay(badStockReturn);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
			}
		}
	}

	private boolean isUserAssignedToPaymentTerminal() {
		User user = loginService.getLoggedInUser();
		PaymentTerminalAssignment assignment = paymentTerminalService.findPaymentTerminalAssignment(user);
		return assignment != null;
	}
	
}