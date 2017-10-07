package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.model.User;
import com.pj.magic.service.UserService;
import com.pj.magic.util.ComponentUtil;

@Component
public class ResetPasswordPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(ResetPasswordPanel.class);
	private static final String RESET_PASSWORD_ACTION_NAME = "resetPassword";
	
	@Autowired private UserService userService;
	
	private JComboBox<User> userComboBox;
	private JButton resetPasswordButton;
	
	@Override
	protected void initializeComponents() {
		userComboBox = new JComboBox<>();
		
		resetPasswordButton = new JButton("Reset Password");
		resetPasswordButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				resetPassword();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(userComboBox);
	}

	protected void resetPassword() {
		User user = (User)userComboBox.getSelectedItem();
		if (user == null) {
			showErrorMessage("User must be specified");
			return;
		}
		
		if (confirm("Reset Password?")) {
			try {
				userService.resetPassword(user);
				JOptionPane.showMessageDialog(this, createNewPasswordPanel(user));
				updateDisplay();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private JPanel createNewPasswordPanel(User user) {
		JPanel panel = new JPanel();
		panel.add(new JLabel("Saved! New user password: "));
		
		JTextField textField = new JTextField(user.getPlainPassword());
		textField.setPreferredSize(new Dimension(90, 25));
		textField.setEditable(false);
		panel.add(textField);
		
		return panel;
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
		mainPanel.add(ComponentUtil.createLabel(80, "User: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		userComboBox.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(userComboBox, c);
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0; // right space filler
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
		
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
		resetPasswordButton.setPreferredSize(new Dimension(160, 25));
		mainPanel.add(resetPasswordButton, c);
		
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
		resetPasswordButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), RESET_PASSWORD_ACTION_NAME);
		resetPasswordButton.getActionMap().put(RESET_PASSWORD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				resetPassword();
			}
		});
	}

	public void updateDisplay() {
		List<User> users = userService.getAllUsers();
		userComboBox.setModel(
				new DefaultComboBoxModel<>(users.toArray(new User[users.size()])));
		userComboBox.setSelectedItem(null);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToAdminMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}
