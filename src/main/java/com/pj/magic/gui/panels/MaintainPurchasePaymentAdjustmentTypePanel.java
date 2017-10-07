package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.model.PurchasePaymentAdjustmentType;
import com.pj.magic.service.PurchasePaymentAdjustmentTypeService;
import com.pj.magic.util.ComponentUtil;

@Component
public class MaintainPurchasePaymentAdjustmentTypePanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(MaintainPurchasePaymentAdjustmentTypePanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	
	@Autowired private PurchasePaymentAdjustmentTypeService purchasePaymentAdjustmentTypeService;
	
	private PurchasePaymentAdjustmentType adjustmentType;
	private MagicTextField codeField;
	private MagicTextField descriptionField;
	private JButton saveButton;
	
	@Override
	protected void initializeComponents() {
		codeField = new MagicTextField();
		codeField.setMaximumLength(12);
		
		descriptionField = new MagicTextField();
		descriptionField.setMaximumLength(100);
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAdjustmentType();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(codeField);
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(codeField);
		focusOrder.add(descriptionField);
		focusOrder.add(saveButton);
	}
	
	protected void saveAdjustmentType() {
		if (!validateAdjustmentType()) {
			return;
		}
		
		if (confirm("Save?")) {
			adjustmentType.setCode(codeField.getText());
			adjustmentType.setDescription(descriptionField.getText());
			
			try {
				purchasePaymentAdjustmentTypeService.save(adjustmentType);
				showMessage("Saved!");
				getMagicFrame().switchToEditPurchasePaymentAdjustmentTypePanel(adjustmentType);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private boolean validateAdjustmentType() {
		try {
			validateMandatoryField(codeField, "Code");
			validateMandatoryField(descriptionField, "Description");
		} catch (ValidationException e) {
			return false;
		}
		
		PurchasePaymentAdjustmentType existing = 
				purchasePaymentAdjustmentTypeService.findAdjustmentTypeByCode(codeField.getText());
		if (existing != null) {
			if (adjustmentType.getId() == null || !existing.getId().equals(adjustmentType.getId())) {			
				showErrorMessage("Code is already used by another Purchase Payment Adjustment Type record");
				codeField.requestFocusInWindow();
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
		mainPanel.add(ComponentUtil.createFiller(50, 20));
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Code: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		codeField.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(codeField, c);
		
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
		mainPanel.add(ComponentUtil.createLabel(100, "Description: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		descriptionField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(descriptionField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 20), c);
		
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
		
		saveButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_ACTION_NAME);
		saveButton.getActionMap().put(SAVE_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAdjustmentType();
			}
		});
	}

	public void updateDisplay(PurchasePaymentAdjustmentType type) {
		this.adjustmentType = type;
		if (type.getId() == null) {
			clearDisplay();
			return;
		}
		
		codeField.setText(type.getCode());
		descriptionField.setText(type.getDescription());
	}

	private void clearDisplay() {
		codeField.setText(null);
		descriptionField.setText(null);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPurchasePaymentAdjustmentTypeListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}