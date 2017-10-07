package com.pj.magic.gui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.exception.InvalidUsernamePasswordException;
import com.pj.magic.gui.component.MagicButton;
import com.pj.magic.gui.component.MagicPasswordField;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.service.LoginService;
import com.pj.magic.util.ComponentUtil;

@Component
public class LoginPanel extends AbstractMagicPanel {

	@Autowired private LoginService loginService;
	
	private MagicTextField usernameField;
	private MagicPasswordField passwordField;
	private MagicButton loginButton;
	
	@Override
	protected void initializeComponents() {
		usernameField = new MagicTextField();
		usernameField.setMaximumLength(15);
		
		passwordField = new MagicPasswordField();
		
		loginButton = new MagicButton("Login");
		loginButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(usernameField);
	}

	protected void login() {
		String username = usernameField.getText();
		String password = new String(passwordField.getPassword());
		
		try {
			loginService.login(username, password);
			getMagicFrame().switchToMainMenuPanel();
		} catch (InvalidUsernamePasswordException e) {
			showErrorMessage("Invalid username/password");
			passwordField.setText(null);
			usernameField.requestFocusInWindow();
		}
	}

	@Override
	protected void layoutComponents() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = c.weighty = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		add(createLoginFieldsPanel(), c);
	}

	private JPanel createLoginFieldsPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(Constants.PASTEL_BLUE);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createLabel(100, "Username: "), c);

		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		usernameField.setPreferredSize(new Dimension(100, 20));
		panel.add(usernameField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createLabel(100, "Password: "), c);

		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		passwordField.setPreferredSize(new Dimension(100, 20));
		panel.add(passwordField, c);

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
		panel.add(loginButton, c);
		
		JPanel outerPanel = new JPanel();
		outerPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		outerPanel.setBackground(Constants.PASTEL_BLUE);
		outerPanel.add(panel);
		return outerPanel;
	}

	@Override
	protected void registerKeyBindings() {
		usernameField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				passwordField.requestFocusInWindow();
			}
		});
		
		passwordField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		
		loginButton.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
	}

	@Override
	protected void doOnBack() {
		// do nothing
	}

	public void updateDisplay() {
		usernameField.setText(null);
		passwordField.setText(null);
	}

}
