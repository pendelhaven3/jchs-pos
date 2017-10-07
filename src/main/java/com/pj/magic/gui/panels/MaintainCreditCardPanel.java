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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.model.CreditCard;
import com.pj.magic.service.CreditCardService;
import com.pj.magic.util.ComponentUtil;

@Component
public class MaintainCreditCardPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(MaintainCreditCardPanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	
	@Autowired private CreditCardService creditCardService;
	
	private CreditCard creditCard;
	private MagicTextField userField;
	private MagicTextField bankField;
	private MagicTextField cardNumberField;
	private MagicTextField customerNumberField;
	private JButton saveButton;
	
	@Override
	protected void initializeComponents() {
		userField = new MagicTextField();
		userField.setMaximumLength(20);
		
		bankField = new MagicTextField();
		bankField.setMaximumLength(20);
		
		cardNumberField = new MagicTextField();
		cardNumberField.setMaximumLength(20);
		
		customerNumberField = new MagicTextField();
		customerNumberField.setMaximumLength(30);
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveCreditCard();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(userField);
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(userField);
		focusOrder.add(bankField);
		focusOrder.add(cardNumberField);
		focusOrder.add(customerNumberField);
		focusOrder.add(saveButton);
	}
	
	protected void saveCreditCard() {
		if (!validateCreditCard()) {
			return;
		}
		
		int confirm = showConfirmMessage("Save?");
		if (confirm == JOptionPane.OK_OPTION) {
			creditCard.setUser(userField.getText());
			creditCard.setBank(bankField.getText());
			creditCard.setCardNumber(cardNumberField.getText());
			creditCard.setCustomerNumber(customerNumberField.getText());
			
			try {
				creditCardService.save(creditCard);
				showMessage("Saved!");
				getMagicFrame().switchToEditCreditCardPanel(creditCard);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private boolean validateCreditCard() {
		try {
			validateMandatoryField(userField, "User");
			validateMandatoryField(bankField, "Bank");
			validateMandatoryField(cardNumberField, "Card Number");
			validateMandatoryField(customerNumberField, "Customer Number");
		} catch (ValidationException e) {
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
		mainPanel.add(Box.createHorizontalStrut(30), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "User: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		userField.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(userField, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0; // right space filler
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Bank: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		bankField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(bankField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Card Number: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		cardNumberField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(cardNumberField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Customer Number:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		customerNumberField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(customerNumberField, c);
		
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
				saveCreditCard();
			}
		});
	}

	public void updateDisplay(CreditCard creditCard) {
		this.creditCard = creditCard;
		if (creditCard.getId() == null) {
			clearDisplay();
			return;
		}
		
		userField.setText(creditCard.getUser());
		bankField.setText(creditCard.getBank());
		cardNumberField.setText(creditCard.getCardNumber());
		customerNumberField.setText(creditCard.getCustomerNumber());
	}

	private void clearDisplay() {
		userField.setText(null);
		bankField.setText(null);
		cardNumberField.setText(null);
		customerNumberField.setText(null);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToCreditCardListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}