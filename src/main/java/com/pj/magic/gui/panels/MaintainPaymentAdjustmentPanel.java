package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.exception.PaymentAdjustmentAlreadyUsedException;
import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.model.AdjustmentType;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.User;
import com.pj.magic.service.AdjustmentTypeService;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PaymentAdjustmentService;
import com.pj.magic.service.PaymentTerminalService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class MaintainPaymentAdjustmentPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(MaintainPaymentAdjustmentPanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	
	@Autowired private PaymentAdjustmentService paymentAdjustmentService;
	@Autowired private AdjustmentTypeService adjustmentTypeService;
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	@Autowired private LoginService loginService;
	@Autowired private PaymentTerminalService paymentTerminalService;
	
	private PaymentAdjustment paymentAdjustment;
	private JLabel paymentAdjustmentNumberLabel;
	private JLabel statusLabel;
	private MagicTextField customerCodeField;
	private EllipsisButton selectCustomerButton;
	private JLabel customerNameLabel;
	private JComboBox<AdjustmentType> adjustmentTypeComboBox;
	private MagicTextField amountField;
	private MagicTextField remarksField;
	private JLabel paymentNumberLabel;
	private JButton saveButton;
	private JButton postButton;
	private JButton unpostButton;
	private JButton markAsPaidButton;
	
	@Override
	protected void initializeComponents() {
		paymentAdjustmentNumberLabel = new JLabel();
		statusLabel = new JLabel();
		
		customerCodeField = new MagicTextField();
		customerCodeField.setMaximumLength(Constants.CUSTOMER_CODE_MAXIMUM_LENGTH);
		
		customerNameLabel = new JLabel();
		
		selectCustomerButton = new EllipsisButton();
		selectCustomerButton.setToolTipText("Select Customer");
		selectCustomerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectCustomerDialog();
			}
		});
		
		adjustmentTypeComboBox = new JComboBox<>();
		
		amountField = new MagicTextField();
		amountField.setMaximumLength(12);
		
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(100);
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePaymentAdjustment();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(customerCodeField);
	}

	private void openSelectCustomerDialog() {
		selectCustomerDialog.searchActiveCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			if (paymentAdjustment.getCustomer() != null && paymentAdjustment.getCustomer().equals(customer)) {
				// skip saving since there is no change
				focusNextField();
				return;
			}
			
			paymentAdjustment.setCustomer(customer);
			customerCodeField.setText(customer.getCode());
			customerNameLabel.setText(customer.getName());
			adjustmentTypeComboBox.requestFocusInWindow();
		}
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(customerCodeField);
		focusOrder.add(adjustmentTypeComboBox);
		focusOrder.add(amountField);
		focusOrder.add(remarksField);
		focusOrder.add(saveButton);
	}
	
	private void savePaymentAdjustment() {
		if (!validatePaymentAdjustment()) {
			return;
		}
		
		if (confirm("Save?")) {
			paymentAdjustment.setAdjustmentType((AdjustmentType)adjustmentTypeComboBox.getSelectedItem());
			paymentAdjustment.setAmount(NumberUtil.toBigDecimal(amountField.getText()));
			paymentAdjustment.setRemarks(remarksField.getText());
			
			try {
				paymentAdjustmentService.save(paymentAdjustment);
				showMessage("Saved!");
				getMagicFrame().switchToEditPaymentAdjustmentPanel(paymentAdjustment);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private boolean validatePaymentAdjustment() {
		try {
			validateMandatoryField(customerCodeField, "Customer");
			validateMandatoryField(adjustmentTypeComboBox, "Adjustment Type");
			validateMandatoryField(amountField, "Amount");
		} catch (ValidationException e) {
			return false;
		}
		
		if (!NumberUtil.isAmount(amountField.getText())) {
			showErrorMessage("Amount must be a valid amount");
			amountField.requestFocusInWindow();
			return false;
		}
		
		return true;
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50));
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Payment Adj. No.: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentAdjustmentNumberLabel.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(paymentAdjustmentNumberLabel, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0; // right space filler
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Status: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(statusLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Customer: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createCustomerPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Adjustment Type: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		adjustmentTypeComboBox.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(adjustmentTypeComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Amount: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		amountField.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(amountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Remarks: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(300, 25));
		mainPanel.add(remarksField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Payment No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentNumberLabel = ComponentUtil.createLabel(100);
		mainPanel.add(paymentNumberLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		saveButton.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(saveButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createGlue(), c);
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
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectCustomerButton.setPreferredSize(new Dimension(30, 24));
		panel.add(selectCustomerButton, c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createFiller(10, 20), c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		customerNameLabel.setPreferredSize(new Dimension(250, 20));
		panel.add(customerNameLabel, c);
		
		return panel;
	}

	@Override
	protected void registerKeyBindings() {
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), NEXT_FIELD_ACTION_NAME);
		getActionMap().put(NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				focusNextField();
			}
		});
		
		customerCodeField.onF5Key(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectCustomerDialog();
			}
		});
		
		saveButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_ACTION_NAME);
		saveButton.getActionMap().put(SAVE_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePaymentAdjustment();
			}
		});
	}

	public void updateDisplay(PaymentAdjustment paymentAdjustment) {
		List<AdjustmentType> adjustmentTypes = adjustmentTypeService.getRegularAdjustmentTypes();
		adjustmentTypeComboBox.setModel(
				new DefaultComboBoxModel<>(adjustmentTypes.toArray(new AdjustmentType[adjustmentTypes.size()])));
		
		this.paymentAdjustment = paymentAdjustment;
		if (paymentAdjustment.getId() == null) {
			clearDisplay();
			return;
		}
		
		this.paymentAdjustment = paymentAdjustment = 
				paymentAdjustmentService.getPaymentAdjustment(paymentAdjustment.getId());
		
		boolean posted = paymentAdjustment.isPosted();
		
		paymentAdjustmentNumberLabel.setText(String.valueOf(paymentAdjustment.getPaymentAdjustmentNumber()));
		statusLabel.setText(paymentAdjustment.getStatus());
		customerCodeField.setEnabled(!posted);
		customerCodeField.setText(paymentAdjustment.getCustomer().getCode());
		customerNameLabel.setText(paymentAdjustment.getCustomer().getName());
		adjustmentTypeComboBox.setEnabled(!posted);
		adjustmentTypeComboBox.setSelectedItem(paymentAdjustment.getAdjustmentType());
		amountField.setEnabled(!posted);
		amountField.setText(FormatterUtil.formatAmount(paymentAdjustment.getAmount()));
		remarksField.setEnabled(!posted);
		remarksField.setText(paymentAdjustment.getRemarks());
		if (paymentAdjustment.getPaymentNumber() != null) {
			paymentNumberLabel.setText(paymentAdjustment.getPaymentNumber().toString());
		} else {
			paymentNumberLabel.setText(null);
		}
		saveButton.setEnabled(!posted);
		postButton.setEnabled(!posted);
		unpostButton.setEnabled(posted);
		selectCustomerButton.setEnabled(!posted);
		markAsPaidButton.setEnabled(posted && !paymentAdjustment.isPaid());
	}

	private void clearDisplay() {
		paymentAdjustmentNumberLabel.setText(null);
		statusLabel.setText(null);
		customerCodeField.setEnabled(true);
		customerCodeField.setText(null);
		customerNameLabel.setText(null);
		adjustmentTypeComboBox.setEnabled(true);
		adjustmentTypeComboBox.setSelectedItem(null);
		amountField.setEnabled(true);
		amountField.setText(null);
		remarksField.setEnabled(true);
		remarksField.setText(null);
		saveButton.setEnabled(true);
		postButton.setEnabled(false);
		unpostButton.setEnabled(false);
		selectCustomerButton.setEnabled(true);
		markAsPaidButton.setEnabled(false);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPaymentAdjustmentListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postPaymentAdjustment();
			}
		});
		
		toolBar.add(postButton);
		
		unpostButton = new MagicToolBarButton("unpost", "Unpost");
		unpostButton.addActionListener(e -> unpostPaymentAdjustment());
		toolBar.add(unpostButton);
		
		markAsPaidButton = new MagicToolBarButton("coins", "Mark As Paid");
		markAsPaidButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				markPaymentAdjustmentAsPaid();
			}
		});
		toolBar.add(markAsPaidButton);
	}

	private void markPaymentAdjustmentAsPaid() {
		if (!isUserAssignedToPaymentTerminal()) {
			showErrorMessage("User is not assigned to a payment terminal");
			return;
		}
		
		if (confirm("Mark Payment Adjustment as paid?")) {
			try {
				paymentAdjustmentService.markAsPaid(paymentAdjustment);
				showMessage("Payment Adjustment marked as paid");
				updateDisplay(paymentAdjustment);
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
	
	private void postPaymentAdjustment() {
		if (confirm("Do you want to post this Payment Adjustment?")) {
			try {
				paymentAdjustmentService.post(paymentAdjustment);
				showMessage("Payment Adjustment posted");
				updateDisplay(paymentAdjustment);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
	}

	private void unpostPaymentAdjustment() {
		if (confirm("Unpost Payment Adjustment?")) {
			try {
				paymentAdjustmentService.unpost(paymentAdjustment);
				showMessage("Payment Adjustment unposted");
				updateDisplay(paymentAdjustment);
			} catch (PaymentAdjustmentAlreadyUsedException e) {
				showMessage("Cannot unpost. Payment Adjustment already used by Payment No. " + e.getPayment().getPaymentNumber());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
	}
	
}