package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.model.ProductCustomCode;
import com.pj.magic.service.Product2Service;
import com.pj.magic.util.ComponentUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MaintainProductCustomCodePanel extends StandardMagicPanel {

	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	
	@Autowired
	private Product2Service productService;
	
	private ProductCustomCode customCode;
	
	private JLabel productIdLabel = new JLabel();
	private JLabel descriptionLabel = new JLabel();
	private MagicTextField codeField;
	private MagicTextField remarksField;
	private JButton saveButton;
	private JButton deleteButton;
	
	@Override
	public String getTitle() {
		return "Maintain Product Custom Code";
	}
	
	@Override
	protected void initializeComponents() {
		codeField = new MagicTextField();
		codeField.setMaximumLength(30);
		
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(100);
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(e -> saveCustomCode());
		
		focusOnComponentWhenThisPanelIsDisplayed(codeField);
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(codeField);
		focusOrder.add(remarksField);
		focusOrder.add(saveButton);
	}
	
	protected void saveCustomCode() {
		if (!validateCustomCode()) {
			return;
		}
		
		if (confirm("Save?")) {
			customCode.setCode(codeField.getText());
			customCode.setRemarks(remarksField.getText());
			
			try {
				productService.save(customCode);
				showMessage("Saved!");
				getMagicFrame().switchToCustomCodesListPanel(customCode.getProduct());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				showMessageForUnexpectedError(e);
			}
		}
	}

	private boolean validateCustomCode() {
		try {
			validateMandatoryField(codeField, "Code");
		} catch (ValidationException e) {
			return false;
		}
		
		ProductCustomCode existing = productService.findCustomCode(customCode.getProduct().getId(), codeField.getText());
		if (existing != null) {
			if (customCode.getId() == null || !existing.getId().equals(customCode.getId())) {			
				showErrorMessage("Custom Code is already defined for Product");
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
		mainPanel.add(ComponentUtil.createLabel(100, "Product ID: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		productIdLabel.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(productIdLabel, c);
		
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
		descriptionLabel.setPreferredSize(new Dimension(300, 25));
		mainPanel.add(descriptionLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Code: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		codeField.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(codeField, c);
		
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
		mainPanel.add(ComponentUtil.createFiller(1, 20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
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
				saveCustomCode();
			}
		});
	}

	public void updateDisplay(ProductCustomCode customCode) {
		this.customCode = customCode;
		if (customCode.getId() == null) {
			clearDisplay();
			return;
		}
		
		codeField.setText(customCode.getCode());
		remarksField.setText(customCode.getRemarks());
		deleteButton.setEnabled(true);
	}

	private void clearDisplay() {
		productIdLabel.setText(String.valueOf(customCode.getProduct().getId()));
		descriptionLabel.setText(customCode.getProduct().getDescription());
		codeField.setText(null);
		remarksField.setText(null);
		deleteButton.setEnabled(false);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToCustomCodesListPanel(customCode.getProduct());
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		deleteButton = new MagicToolBarButton("trash", "Delete", e -> deleteProductCustomCode());
		toolBar.add(deleteButton);
	}

	private void deleteProductCustomCode() {
		if (confirm("Delete Custom Code?")) {
			try {
				productService.delete(customCode);
				showMessage("Custom Code deleted");
				getMagicFrame().switchToCustomCodesListPanel(customCode.getProduct());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				showMessageForUnexpectedError(e);
			}
		}
	}

}
