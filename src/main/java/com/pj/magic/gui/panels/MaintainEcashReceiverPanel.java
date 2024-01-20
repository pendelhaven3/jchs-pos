package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.model.EcashReceiver;
import com.pj.magic.model.EcashType;
import com.pj.magic.service.EcashReceiverService;
import com.pj.magic.service.EcashTypeService;
import com.pj.magic.util.ComponentUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MaintainEcashReceiverPanel extends StandardMagicPanel {

	@Autowired private EcashReceiverService ecashReceiverService;
	@Autowired private EcashTypeService ecashTypeService;
	
	private EcashReceiver ecashReceiver;
	private MagicTextField nameField;
	private MagicComboBox<EcashType> ecashTypeComboBox; 
	private JButton saveButton;
	private JButton deleteButton;
	
	@Override
	protected void initializeComponents() {
		nameField = new MagicTextField();
		nameField.setMaximumLength(100);
		
		ecashTypeComboBox = new MagicComboBox<>(); 
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(e -> saveEcashReceiver());
		
		focusOnComponentWhenThisPanelIsDisplayed(nameField);
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(nameField);
		focusOrder.add(ecashTypeComboBox); 
		focusOrder.add(saveButton);
	}
	
	private void saveEcashReceiver() {
		if (!validateEcashReceiver()) {
			return;
		}
		
		if (confirm("Save?")) {
			ecashReceiver.setName(nameField.getText());
			ecashReceiver.setEcashType((EcashType)ecashTypeComboBox.getSelectedItem());
			
			try {
				ecashReceiverService.save(ecashReceiver);
				showMessage("Saved!");
				getMagicFrame().switchToEditEcashReceiverPanel(ecashReceiver);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private boolean validateEcashReceiver() {
		try {
			validateMandatoryField(nameField, "Name");
			validateUniqueName();
			validateMandatoryField(ecashTypeComboBox, "Ecash Type");
		} catch (ValidationException e) {
			return false;
		}
		return true;
	}

	private void validateUniqueName() throws ValidationException {
		EcashReceiver existing = ecashReceiverService.getEcashReceiverByName(nameField.getText());
		if (existing != null && !existing.getId().equals(ecashReceiver.getId())) {
			showErrorMessage("E-Cash Receiver with same name already exists");
			nameField.requestFocusInWindow();
			throw new ValidationException();
		}
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.insets.top = 50;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Name: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		nameField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(nameField, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Ecash Type: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		ecashTypeComboBox.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(ecashTypeComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 25;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		saveButton.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(saveButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createGlue(), c);
	}

	@Override
	protected void registerKeyBindings() {
		nameField.onEnterKey(() -> focusNextField());
		ecashTypeComboBox.onEnterKey(() -> focusNextField());
	}

	public void updateDisplay(EcashReceiver ecashReceiver) {
		List<EcashType> ecashTypes = ecashTypeService.getAllEcashTypes();
		ecashTypeComboBox.setModel(new DefaultComboBoxModel<>(ecashTypes.toArray(new EcashType[ecashTypes.size()])));
		ecashTypeComboBox.insertItemAt(null, 0);
		
		this.ecashReceiver = ecashReceiver;
		if (ecashReceiver.getId() == null) {
			clearDisplay();
			return;
		}
		
		nameField.setText(ecashReceiver.getName());
		ecashTypeComboBox.setSelectedItem(ecashReceiver.getEcashType());
		deleteButton.setEnabled(true);
	}

	private void clearDisplay() {
		nameField.setText(null);
		ecashTypeComboBox.setSelectedItem(null);
		deleteButton.setEnabled(false);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToEcashReceiverListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		deleteButton = new MagicToolBarButton("trash", "Delete", e -> deleteEcashReceiver());
		toolBar.add(deleteButton);
	}

	private void deleteEcashReceiver() {
		if (ecashReceiverService.isBeingUsed(ecashReceiver)) {
			showErrorMessage("Cannot delete an E-Cash Receiver that is already used");
			return;
		}
		
		if (confirm("Do you want to delete this E-Cash Receiver?")) {
			ecashReceiverService.delete(ecashReceiver);
			showMessage("E-Cash Receiver deleted");
			getMagicFrame().switchToEcashReceiverListPanel();
		}
	}

}
