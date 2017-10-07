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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.MagicTextArea;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.service.CustomerService;
import com.pj.magic.service.PaymentTermService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.KeyUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class MaintainCustomerPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(MaintainCustomerPanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	
	@Autowired private CustomerService customerService;
	@Autowired private PaymentTermService paymentTermService;
	
	private Customer customer;
	private MagicTextField codeField;
	private MagicTextField nameField;
	private MagicTextField businessAddressField;
	private MagicTextField deliveryAddressField;
	private MagicTextField contactPersonField;
	private MagicTextField contactNumberField;
	private MagicTextField tinField;
	private MagicTextField approvedCreditLineField;
	private JComboBox<PaymentTerm> paymentTermComboBox;
	private JComboBox<String> businessTypeComboBox;
	private MagicTextArea ownersTextArea;
	private MagicTextArea bankReferencesTextArea;
	private JCheckBox holdIndicatorCheckBox;
	private JCheckBox activeIndicatorCheckBox;
	private MagicTextField remarksField;
	private JButton saveButton;
	private JButton deleteButton;
	
	@Override
	protected void initializeComponents() {
		codeField = new MagicTextField();
		codeField.setMaximumLength(12);
		
		nameField = new MagicTextField();
		nameField.setMaximumLength(50);
		
		businessAddressField = new MagicTextField();
		businessAddressField.setMaximumLength(100);
		
		deliveryAddressField = new MagicTextField();
		deliveryAddressField.setMaximumLength(100);
		
		contactPersonField = new MagicTextField();
		contactPersonField.setMaximumLength(100);
		
		contactNumberField = new MagicTextField();
		contactNumberField.setMaximumLength(100);

		tinField = new MagicTextField();
		tinField.setMaximumLength(20);
		
		approvedCreditLineField = new MagicTextField();
		approvedCreditLineField.setMaximumLength(12);
		
		paymentTermComboBox = new JComboBox<>();

		businessTypeComboBox = new JComboBox<>();
		businessTypeComboBox.setModel(
				new DefaultComboBoxModel<>(new String[] {null, "PROPRIERTORSHIP", "PARTNERSHIP", "CORPORATION"}));
		
		ownersTextArea = new MagicTextArea();
		ownersTextArea.setMaximumLength(500);
		
		bankReferencesTextArea = new MagicTextArea();
		bankReferencesTextArea.setMaximumLength(500);
		
		holdIndicatorCheckBox = new JCheckBox();
		activeIndicatorCheckBox = new JCheckBox();
		
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(100);
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveCustomer();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(codeField);
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(codeField);
		focusOrder.add(nameField);
		focusOrder.add(businessAddressField);
		focusOrder.add(deliveryAddressField);
		focusOrder.add(contactPersonField);
		focusOrder.add(contactNumberField);
		focusOrder.add(tinField);
		focusOrder.add(paymentTermComboBox);
		focusOrder.add(approvedCreditLineField);
		focusOrder.add(businessTypeComboBox);
		focusOrder.add(holdIndicatorCheckBox);
		focusOrder.add(activeIndicatorCheckBox);
		focusOrder.add(remarksField);
		focusOrder.add(ownersTextArea);
	}
	
	protected void saveCustomer() {
		if (!validateCustomer()) {
			return;
		}
		
		if (confirm("Save?")) {
			customer.setCode(codeField.getText());
			customer.setName(nameField.getText());
			customer.setBusinessAddress(businessAddressField.getText());
			customer.setDeliveryAddress(deliveryAddressField.getText());
			customer.setContactPerson(contactPersonField.getText());
			customer.setContactNumber(contactNumberField.getText());
			customer.setTin(tinField.getText());
			customer.setPaymentTerm((PaymentTerm)paymentTermComboBox.getSelectedItem());
			if (!StringUtils.isEmpty(approvedCreditLineField.getText())) {
				customer.setApprovedCreditLine(NumberUtil.toBigDecimal(approvedCreditLineField.getText()));
			} else {
				customer.setApprovedCreditLine(null);
			}
			customer.setBusinessType((String)businessTypeComboBox.getSelectedItem());
			customer.setHold(holdIndicatorCheckBox.isSelected());
			customer.setActive(activeIndicatorCheckBox.isSelected());
			customer.setRemarks(remarksField.getText());
			customer.setOwners(ownersTextArea.getText());
			customer.setBankReferences(bankReferencesTextArea.getText());
			try {
				customerService.save(customer);
				showMessage("Saved!");
				getMagicFrame().switchToEditCustomerPanel(customer);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private boolean validateCustomer() {
		try {
			validateMandatoryField(codeField, "Code");
			validateMandatoryField(nameField, "Name");
		} catch (ValidationException e) {
			return false;
		}
		
		Customer existing = customerService.findCustomerByCode(codeField.getText());
		if (existing != null && (customer.getId() == null || !customer.getId().equals(existing.getId()))) {
			showErrorMessage("Code is already used by another record");
			codeField.requestFocusInWindow();
			return false;
		}
		
		if (!StringUtils.isEmpty(approvedCreditLineField.getText())) {
			if (!NumberUtil.isAmount(approvedCreditLineField.getText())) {
				showErrorMessage("Approved Credit Line must be a valid amount");
				approvedCreditLineField.requestFocusInWindow();
				return false;
			}
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
		mainPanel.add(ComponentUtil.createFiller(30, 1), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Code: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		codeField.setPreferredSize(new Dimension(120, 25));
		mainPanel.add(codeField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Owners: "), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0; // right space filler
		c.gridx = 5;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Business Name: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		nameField.setPreferredSize(new Dimension(300, 25));
		mainPanel.add(nameField, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "(Name / Address)"), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Business Address: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		businessAddressField.setPreferredSize(new Dimension(300, 25));
		mainPanel.add(businessAddressField, c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridheight = 3;
		JScrollPane ownersScrollPane = new JScrollPane(ownersTextArea);
		ownersScrollPane.setPreferredSize(new Dimension(400, 80));
		mainPanel.add(ownersScrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Delivery Address: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		deliveryAddressField.setPreferredSize(new Dimension(300, 25));
		mainPanel.add(deliveryAddressField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Contact Person: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		contactPersonField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(contactPersonField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Contact Number: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		contactNumberField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(contactNumberField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "TIN: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		tinField.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(tinField, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Bank References: "), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Payment Term: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentTermComboBox.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(paymentTermComboBox, c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(300, "(Bank Name / Branch / Acct Name / Acct)"), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(170, "Approved Credit Line: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		approvedCreditLineField.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(approvedCreditLineField, c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridheight = 3;
		JScrollPane bankReferencesScrollPane = new JScrollPane(bankReferencesTextArea);
		bankReferencesScrollPane.setPreferredSize(new Dimension(400, 80));
		mainPanel.add(bankReferencesScrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Business Type: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		businessTypeComboBox.setPreferredSize(new Dimension(170, 25));
		mainPanel.add(businessTypeComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "On-hold?: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(holdIndicatorCheckBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Active?: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(activeIndicatorCheckBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Remarks: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(300, 25));
		mainPanel.add(remarksField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 25), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		saveButton.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(saveButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
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
		
		paymentTermComboBox.getInputMap().put(KeyUtil.getEnterKey(), NEXT_FIELD_ACTION_NAME);
		paymentTermComboBox.getActionMap().put(NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				approvedCreditLineField.requestFocusInWindow();
			}
		});
		
		businessTypeComboBox.getInputMap().put(KeyUtil.getEnterKey(), NEXT_FIELD_ACTION_NAME);
		businessTypeComboBox.getActionMap().put(NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				holdIndicatorCheckBox.requestFocusInWindow();
			}
		});
		
		saveButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_ACTION_NAME);
		saveButton.getActionMap().put(SAVE_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveCustomer();
			}
		});
	}

	public void updateDisplay(Customer customer) {
		List<PaymentTerm> paymentTerms = paymentTermService.getAllPaymentTerms();
		paymentTermComboBox.setModel(
				new DefaultComboBoxModel<>(paymentTerms.toArray(new PaymentTerm[paymentTerms.size()])));
		
		this.customer = customer;
		if (customer.getId() == null) {
			clearDisplay();
			return;
		}
		
		codeField.setText(customer.getCode());
		nameField.setText(customer.getName());
		businessAddressField.setText(customer.getBusinessAddress());
		deliveryAddressField.setText(customer.getDeliveryAddress());
		contactPersonField.setText(customer.getContactPerson());
		contactNumberField.setText(customer.getContactNumber());
		tinField.setText(customer.getTin());
		paymentTermComboBox.setSelectedItem(customer.getPaymentTerm());
		if (customer.getApprovedCreditLine() != null) {
			approvedCreditLineField.setText(FormatterUtil.formatAmount(customer.getApprovedCreditLine()));
		}
		businessTypeComboBox.setSelectedItem(customer.getBusinessType());
		ownersTextArea.setText(customer.getOwners());
		bankReferencesTextArea.setText(customer.getBankReferences());
		holdIndicatorCheckBox.setSelected(customer.isOnHold());
		activeIndicatorCheckBox.setSelected(customer.getActive());
		remarksField.setText(customer.getRemarks());
		deleteButton.setEnabled(true);
	}

	private void clearDisplay() {
		codeField.setText(null);
		nameField.setText(null);
		businessAddressField.setText(null);
		deliveryAddressField.setText(null);
		contactPersonField.setText(null);
		contactNumberField.setText(null);
		tinField.setText(null);
		paymentTermComboBox.setSelectedItem(null);
		approvedCreditLineField.setText(null);
		businessTypeComboBox.setSelectedItem(null);
		ownersTextArea.setText(null);
		bankReferencesTextArea.setText(null);
		holdIndicatorCheckBox.setSelected(false);
		activeIndicatorCheckBox.setSelected(true);
		remarksField.setText(null);
		deleteButton.setEnabled(false);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToCustomerListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		deleteButton = new MagicToolBarButton("trash", "Delete");
		deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteCustomer();
			}
		});
		toolBar.add(deleteButton);
	}

	private void deleteCustomer() {
		if (!customerService.canDeleteCustomer(customer)) {
			showErrorMessage("Cannot delete Customer that is already referenced in a Sales Requisition");
			return;
		}
		
		if (confirm("Delete Customer?")) {
			try {
				customerService.deleteCustomer(customer);
				showMessage("Customer deleted");
				getMagicFrame().switchToCustomerListPanel();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error");
			}
		}
	}

}
