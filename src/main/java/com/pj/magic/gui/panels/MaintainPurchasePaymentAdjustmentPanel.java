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
import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SelectSupplierDialog;
import com.pj.magic.model.PurchasePaymentAdjustmentType;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.PurchasePaymentAdjustment;
import com.pj.magic.service.PurchasePaymentAdjustmentTypeService;
import com.pj.magic.service.PurchasePaymentAdjustmentService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class MaintainPurchasePaymentAdjustmentPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(MaintainPurchasePaymentAdjustmentPanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	
	@Autowired private PurchasePaymentAdjustmentService purchasePaymentAdjustmentService;
	@Autowired private PurchasePaymentAdjustmentTypeService purchasePaymentAdjustmentTypeService;
	@Autowired private SelectSupplierDialog selectSupplierDialog;
	
	private PurchasePaymentAdjustment paymentAdjustment;
	private JLabel paymentAdjustmentNumberLabel;
	private JLabel statusLabel;
	private MagicTextField supplierCodeField;
	private EllipsisButton selectSupplierButton;
	private JLabel supplierNameLabel;
	private JComboBox<PurchasePaymentAdjustmentType> adjustmentTypeComboBox;
	private MagicTextField amountField;
	private MagicTextField remarksField;
	private JButton saveButton;
	private JButton postButton;
	
	@Override
	protected void initializeComponents() {
		paymentAdjustmentNumberLabel = new JLabel();
		statusLabel = new JLabel();
		
		supplierCodeField = new MagicTextField();
		supplierCodeField.setMaximumLength(Constants.SUPPLIER_CODE_MAXIMUM_LENGTH);
		
		supplierNameLabel = new JLabel();
		
		selectSupplierButton = new EllipsisButton("Select Supplier");
		selectSupplierButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectSupplierDialog();
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
		
		focusOnComponentWhenThisPanelIsDisplayed(supplierCodeField);
	}

	private void openSelectSupplierDialog() {
		selectSupplierDialog.searchSuppliers(supplierCodeField.getText());
		selectSupplierDialog.setVisible(true);
		
		Supplier supplier = selectSupplierDialog.getSelectedSupplier();
		if (supplier != null) {
			if (paymentAdjustment.getSupplier() != null && paymentAdjustment.getSupplier().equals(supplier)) {
				// skip saving since there is no change
				focusNextField();
				return;
			}
			
			paymentAdjustment.setSupplier(supplier);
			supplierCodeField.setText(supplier.getCode());
			supplierNameLabel.setText(supplier.getName());
			adjustmentTypeComboBox.requestFocusInWindow();
		}
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(supplierCodeField);
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
			paymentAdjustment.setAdjustmentType(
					(PurchasePaymentAdjustmentType)adjustmentTypeComboBox.getSelectedItem());
			paymentAdjustment.setAmount(NumberUtil.toBigDecimal(amountField.getText()));
			paymentAdjustment.setRemarks(remarksField.getText());
			
			try {
				purchasePaymentAdjustmentService.save(paymentAdjustment);
				showMessage("Saved!");
				getMagicFrame().switchToEditPurchasePaymentAdjustmentPanel(paymentAdjustment);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private boolean validatePaymentAdjustment() {
		try {
			validateMandatoryField(supplierCodeField, "Supplier");
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
		statusLabel.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(statusLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Supplier: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createSupplierPanel(), c);
		
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

	private JPanel createSupplierPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierCodeField.setPreferredSize(new Dimension(100, 25));
		panel.add(supplierCodeField, c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectSupplierButton.setPreferredSize(new Dimension(30, 24));
		panel.add(selectSupplierButton, c);
		
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
		supplierNameLabel.setPreferredSize(new Dimension(250, 20));
		panel.add(supplierNameLabel, c);
		
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
		
		supplierCodeField.onF5Key(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectSupplierDialog();
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

	public void updateDisplay(PurchasePaymentAdjustment paymentAdjustment) {
		List<PurchasePaymentAdjustmentType> adjustmentTypes = 
				purchasePaymentAdjustmentTypeService.getRegularAdjustmentTypes();
		adjustmentTypeComboBox.setModel(
				new DefaultComboBoxModel<>(adjustmentTypes.toArray(
						new PurchasePaymentAdjustmentType[adjustmentTypes.size()])));
		
		this.paymentAdjustment = paymentAdjustment;
		if (paymentAdjustment.getId() == null) {
			clearDisplay();
			return;
		}
		
		this.paymentAdjustment = paymentAdjustment = 
				purchasePaymentAdjustmentService.getPurchasePaymentAdjustment(paymentAdjustment.getId());
		
		boolean posted = paymentAdjustment.isPosted();
		
		paymentAdjustmentNumberLabel.setText(String.valueOf(paymentAdjustment.getPurchasePaymentAdjustmentNumber()));
		statusLabel.setText(paymentAdjustment.getStatus());
		supplierCodeField.setEnabled(!posted);
		supplierCodeField.setText(paymentAdjustment.getSupplier().getCode());
		supplierNameLabel.setText(paymentAdjustment.getSupplier().getName());
		adjustmentTypeComboBox.setEnabled(!posted);
		adjustmentTypeComboBox.setSelectedItem(paymentAdjustment.getAdjustmentType());
		amountField.setEnabled(!posted);
		amountField.setText(FormatterUtil.formatAmount(paymentAdjustment.getAmount()));
		remarksField.setEnabled(!posted);
		remarksField.setText(paymentAdjustment.getRemarks());
		saveButton.setEnabled(!posted);
		postButton.setEnabled(!posted);
		selectSupplierButton.setEnabled(!posted);
	}

	private void clearDisplay() {
		paymentAdjustmentNumberLabel.setText(null);
		statusLabel.setText(null);
		supplierCodeField.setEnabled(true);
		supplierCodeField.setText(null);
		supplierNameLabel.setText(null);
		adjustmentTypeComboBox.setEnabled(true);
		adjustmentTypeComboBox.setSelectedItem(null);
		amountField.setEnabled(true);
		amountField.setText(null);
		remarksField.setEnabled(true);
		remarksField.setText(null);
		saveButton.setEnabled(true);
		postButton.setEnabled(false);
		selectSupplierButton.setEnabled(true);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPurchasePaymentAdjustmentListPanel();
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
		
//		toolBar.add(postButton);
	}

	private void postPaymentAdjustment() {
		if (confirm("Do you want to post this Purchase Payment Adjustment?")) {
			try {
				purchasePaymentAdjustmentService.post(paymentAdjustment);
				showMessage("Purchase Payment Adjustment posted");
				updateDisplay(paymentAdjustment);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
	}

}