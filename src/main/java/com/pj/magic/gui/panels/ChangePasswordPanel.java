package com.pj.magic.gui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.MagicPasswordField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.UserService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.KeyUtil;
import com.pj.magic.util.PasswordTransformer;

@Component
public class ChangePasswordPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(ChangePasswordPanel.class);
	private static final int PASSWORD_MINIMUM_LENGTH = 8;
	
	@Autowired private LoginService loginService;
	@Autowired private UserService userService;
	
	private MagicPasswordField oldPasswordField;
	private MagicPasswordField newPasswordField;
	private MagicPasswordField retypePasswordField;
	private JButton changePasswordButton;
	
	@Override
	protected void initializeComponents() {
		oldPasswordField = new MagicPasswordField();
		
		newPasswordField = new MagicPasswordField();
		newPasswordField.setMaximumLength(15);
		
		retypePasswordField = new MagicPasswordField();
		retypePasswordField.setMaximumLength(15);
		
		changePasswordButton = new JButton("Change Password");
		changePasswordButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				changePassword();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(oldPasswordField);
	}

	protected void changePassword() {
		String oldPassword = new String(oldPasswordField.getPassword());
		String newPassword = new String(newPasswordField.getPassword());
		String retypePassword = new String(retypePasswordField.getPassword());
		
		boolean valid = false;
		if (StringUtils.isEmpty(oldPassword)) {
			showErrorMessage("Old Password must be specified");
			oldPasswordField.requestFocusInWindow();
		} else if (!loginService.getLoggedInUser().getPassword().equals(PasswordTransformer.transform(oldPassword))) {
			showErrorMessage("Old Password is incorrect");
			oldPasswordField.requestFocusInWindow();
		} else if (StringUtils.isEmpty(newPassword)) {
			showErrorMessage("New Password must be specified");
			newPasswordField.requestFocusInWindow();
		} else if (oldPassword.equals(newPassword)) {
			showErrorMessage("Old and New Password must be different");
			newPasswordField.requestFocusInWindow();
		} else if (newPassword.length() < PASSWORD_MINIMUM_LENGTH) {
			showErrorMessage("New Password must be at least 8 characters long");
			newPasswordField.requestFocusInWindow();
		} else if (StringUtils.isEmpty(retypePassword)) {
			showErrorMessage("Retype Password must be specified");
			retypePasswordField.requestFocusInWindow();
		} else if (!retypePassword.equals(newPassword)) {
			showErrorMessage("Retype Password must be the same as New Password");
			retypePasswordField.requestFocusInWindow();
		} else {
			valid = true;
		}
		
		if (!valid) {
			return;
		}
		
		try {
			userService.changePassword(loginService.getLoggedInUser(), newPassword);
			showMessage("Password changed!");
			getMagicFrame().switchToMainMenuPanel();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			showErrorMessage("Unexpected error upon saving");
		}
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = c.weighty = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		mainPanel.add(createLoginFieldsPanel(), c);
	}

	private JPanel createLoginFieldsPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(Constants.PASTEL_BLUE);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Old Password: "), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		oldPasswordField.setPreferredSize(new Dimension(120, 25));
		panel.add(oldPasswordField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "New Password: "), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		newPasswordField.setPreferredSize(new Dimension(120, 25));
		panel.add(newPasswordField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(140, "Retype Password: "), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		retypePasswordField.setPreferredSize(new Dimension(120, 25));
		panel.add(retypePasswordField, c);

		currentRow++;
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createFiller(1, 20), c);
		
		currentRow++;
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(changePasswordButton, c);
		
		JPanel outerPanel = new JPanel();
		outerPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		outerPanel.setBackground(Constants.PASTEL_BLUE);
		outerPanel.add(panel);
		return outerPanel;
	}

	@Override
	protected void registerKeyBindings() {
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyUtil.getEnterKey(), "enter");
		getActionMap().put("enter", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				focusNextField();
			}
			
		});
		
		changePasswordButton.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		changePasswordButton.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				changePassword();
			}
		});
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(oldPasswordField);
		focusOrder.add(newPasswordField);
		focusOrder.add(retypePasswordField);
		focusOrder.add(changePasswordButton);
	}
	
	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	public void updateDisplay() {
		oldPasswordField.setText(null);
		newPasswordField.setText(null);
		retypePasswordField.setText(null);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

}
