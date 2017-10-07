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
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.model.User;
import com.pj.magic.service.UserService;
import com.pj.magic.util.ComponentUtil;

@Component
public class MaintainUserPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(MaintainUserPanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	
	@Autowired private UserService userService;
	
	private User user;
	private MagicTextField usernameField;
	private JCheckBox supervisorCheckbox;
	private JCheckBox modifyPricingCheckbox;
	private JButton saveButton;
	
	@Override
	protected void initializeComponents() {
		usernameField = new MagicTextField();
		usernameField.setMaximumLength(50);
		
		supervisorCheckbox = new JCheckBox();
		modifyPricingCheckbox = new JCheckBox();
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveUser();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(usernameField);
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(usernameField);
		focusOrder.add(supervisorCheckbox);
		focusOrder.add(modifyPricingCheckbox);
		focusOrder.add(saveButton);
	}
	
	protected void saveUser() {
		if (!validateUser()) {
			return;
		}
		
		if (confirm("Save?")) {
			user.setUsername(usernameField.getText());
			user.setSupervisor(supervisorCheckbox.isSelected());
			user.setModifyPricing(modifyPricingCheckbox.isSelected() || supervisorCheckbox.isSelected());
			
			boolean newUser = (user.getId() == null);
			try {
				userService.save(user);
				if (newUser) {
					JOptionPane.showMessageDialog(this, createNewPasswordPanel());
				} else {
					showMessage("Saved!");
				}
				getMagicFrame().switchToEditUserPanel(user);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private JPanel createNewPasswordPanel() {
		JPanel panel = new JPanel();
		panel.add(new JLabel("Saved! New user password: "));
		
		JTextField textField = new JTextField(user.getPlainPassword());
		textField.setPreferredSize(new Dimension(90, 25));
		textField.setEditable(false);
		panel.add(textField);
		
		return panel;
	}

	private boolean validateUser() {
		try {
			validateMandatoryField(usernameField, "Username");
		} catch (ValidationException e) {
			return false;
		}
		
		if (isUsernameAlreadyUsed()) {
			showErrorMessage("Username is already taken by another user");
			usernameField.requestFocusInWindow();
			return false;
		}
		
		return true;
	}

	private boolean isUsernameAlreadyUsed() {
		User existing = userService.findUserByUsername(usernameField.getText());
		if (existing != null) {
			return user.getId() == null || !existing.getId().equals(user.getId());
		} else {
			return false;
		}
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(50, 1), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Username: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		usernameField.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(usernameField, c);
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0; // right space filler
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Supervisor? "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(supervisorCheckbox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Modify Pricing? "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(modifyPricingCheckbox, c);
		
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
		saveButton.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(saveButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 1), c);
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
				saveUser();
			}
		});
	}

	public void updateDisplay(User user) {
		this.user = user;
		if (user.getId() == null) {
			clearDisplay();
			return;
		}
		
		usernameField.setText(user.getUsername());
		supervisorCheckbox.setSelected(user.isSupervisor());
		modifyPricingCheckbox.setSelected(user.isModifyPricing() || user.isSupervisor());
	}

	private void clearDisplay() {
		usernameField.setText(null);
		supervisorCheckbox.setSelected(false);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToUserListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}
