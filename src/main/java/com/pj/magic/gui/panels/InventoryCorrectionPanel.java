package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.MagicButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.component.SelectProductEllipsisButton;
import com.pj.magic.gui.component.UnitComboBox;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.model.InventoryCorrection;
import com.pj.magic.model.Product;
import com.pj.magic.service.InventoryCorrectionService;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class InventoryCorrectionPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(InventoryCorrectionPanel.class);
	
	@Autowired private InventoryCorrectionService inventoryCorrectionService;
	@Autowired private ProductService productService;
	@Autowired private SelectProductDialog selectProductDialog;
	
	private InventoryCorrection inventoryCorrection;
	private JLabel inventoryCorrectionNumberLabel;
	private MagicTextField postDateField;
	private MagicTextField productCodeField;
	private SelectProductEllipsisButton selectProductButton;
	private JLabel productDescriptionLabel;
	private UnitComboBox unitComboBox;
	private MagicTextField quantityField;
	private MagicTextField remarksField;
	private JLabel updateDateLabel;
	private JLabel updatedByLabel;
	private MagicButton saveButton;
	
	@Override
	protected void initializeComponents() {
		postDateField = new MagicTextField();
		
		productCodeField = new MagicTextField();
		productCodeField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		
		productDescriptionLabel = new JLabel();
		
		selectProductButton = new SelectProductEllipsisButton(selectProductDialog, productCodeField, productDescriptionLabel);
		
		unitComboBox = new UnitComboBox();
		
		quantityField = new MagicTextField();
		
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(100);
		
		updateDateLabel = new JLabel();
		updatedByLabel = new JLabel();
		
		saveButton = new MagicButton("Save");
		saveButton.addActionListener(e -> saveInventoryCorrection());
		
		focusOnComponentWhenThisPanelIsDisplayed(postDateField);
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(postDateField);
		focusOrder.add(productCodeField);
		focusOrder.add(unitComboBox);
		focusOrder.add(quantityField);
		focusOrder.add(saveButton);
	}
	
	private void saveInventoryCorrection() {
		if (!validateFields()) {
			return;
		}
		
		if (confirm("Save inventory correction?")) {
			Product product = productService.findProductByCode(productCodeField.getText());
			String unit = (String)unitComboBox.getSelectedItem();
			inventoryCorrection.setPostDate(postDateField.getTextAsDateTime());
			inventoryCorrection.setProduct(product);
			inventoryCorrection.setUnit(unit);
			inventoryCorrection.setQuantity(quantityField.getTextAsInteger());
			inventoryCorrection.setRemarks(remarksField.getText());
			try {
				inventoryCorrectionService.save(inventoryCorrection);
				showMessage("Saved!");
				getMagicFrame().switchToInventoryCorrectionListPanel();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private boolean validateFields() {
		if (isProductCodeNotSpecified()) {
			showErrorMessage("Product Code must be specified");
			productCodeField.requestFocusInWindow();
			return false;
		}
		
		if (isUnitNotSpecified()) {
			showErrorMessage("Unit must be specified");
			unitComboBox.requestFocusInWindow();
			return false;
		}
		
		if (isQuantityNotSpecified()) {
			showErrorMessage("Quantity must be specified");
			quantityField.requestFocusInWindow();
			return false;
		}
		
		return true;
	}

	private boolean isProductCodeNotSpecified() {
		return productCodeField.getText().isEmpty();
	}

	private boolean isUnitNotSpecified() {
		return unitComboBox.getSelectedItem() == null;
	}

	private boolean isQuantityNotSpecified() {
		return quantityField.getText().isEmpty();
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Inv. Correction No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		inventoryCorrectionNumberLabel = ComponentUtil.createLabel(100);
		panel.add(inventoryCorrectionNumberLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Post Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postDateField.setPreferredSize(new Dimension(150, 25));
		panel.add(postDateField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Product Code:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(selectProductButton.getFieldsPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Unit: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		unitComboBox.setPreferredSize(new Dimension(60, 25));
		panel.add(unitComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Quantity: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		quantityField.setPreferredSize(new Dimension(100, 25));
		panel.add(quantityField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Remarks: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(300, 25));
		panel.add(remarksField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Update Date: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		updateDateLabel.setPreferredSize(new Dimension(150, 25));
		panel.add(updateDateLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Update By: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		updatedByLabel.setPreferredSize(new Dimension(100, 25));
		panel.add(updatedByLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 2;
		c.insets.top = 10;
		saveButton.setPreferredSize(new Dimension(100, 25));
		panel.add(saveButton, c);
		
		mainPanel.add(panel, BorderLayout.NORTH);
	}

	@Override
	protected void registerKeyBindings() {
		saveButton.onEnterKey(() -> saveInventoryCorrection());
	}

	public void updateDisplay(InventoryCorrection inventoryCorrection) {
		this.inventoryCorrection = inventoryCorrection;
		if (inventoryCorrection.getId() == null) {
			clearDisplay();
			return;
		}
		
		this.inventoryCorrection = inventoryCorrection =
				inventoryCorrectionService.getInventoryCorrection(inventoryCorrection.getId());
		postDateField.setText(FormatterUtil.formatDateTime(inventoryCorrection.getPostDate()));
		inventoryCorrectionNumberLabel.setText(String.valueOf(inventoryCorrection.getInventoryCorrectionNumber()));
		productCodeField.setText(inventoryCorrection.getProduct().getCode());
		productDescriptionLabel.setText(inventoryCorrection.getProduct().getDescription());
		unitComboBox.setSelectedItem(inventoryCorrection.getUnit());
		quantityField.setText(String.valueOf(inventoryCorrection.getQuantity()));
		remarksField.setText(inventoryCorrection.getRemarks());
		updateDateLabel.setText(FormatterUtil.formatDateTime(inventoryCorrection.getUpdateDate()));
		updatedByLabel.setText(inventoryCorrection.getUpdatedBy().getUsername());
		
		boolean editable = !inventoryCorrection.isDeleted();
		postDateField.setEditable(editable);
		productCodeField.setEditable(editable);
		selectProductButton.setEnabled(editable);
		unitComboBox.setEnabled(editable);
		quantityField.setEditable(editable);
		remarksField.setEditable(editable);
		saveButton.setEnabled(editable);
	}

	private void clearDisplay() {
		postDateField.setText(null);
		inventoryCorrectionNumberLabel.setText(null);
		productCodeField.setText(null);
		productDescriptionLabel.setText(null);
		unitComboBox.setSelectedItem(null);
		remarksField.setText(null);
		updateDateLabel.setText(null);
		updatedByLabel.setText(null);
		quantityField.setText(null);
		saveButton.setEnabled(true);
		
		productCodeField.setEditable(true);
		selectProductButton.setEnabled(true);
		unitComboBox.setEnabled(true);
		quantityField.setEditable(true);
		remarksField.setEditable(true);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToInventoryCorrectionListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton cancelButton = new MagicToolBarButton("cancel", "Delete");
		cancelButton.addActionListener(e -> deleteInventoryCorrection());
		toolBar.add(cancelButton);
		
	}

	private void deleteInventoryCorrection() {
		if (confirm("Delete record?")) {
			inventoryCorrection.setDeleted(true);
			try {
				inventoryCorrectionService.save(inventoryCorrection);
				showMessage("Record deleted");
				getMagicFrame().switchToInventoryCorrectionListPanel();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
			}
		}
	}

}