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
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.User;
import com.pj.magic.service.PaymentTerminalService;
import com.pj.magic.service.UserService;
import com.pj.magic.util.ComponentUtil;

@Component
public class MaintainPaymentTerminalAssignmentPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(MaintainPaymentTerminalAssignmentPanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	
	@Autowired private PaymentTerminalService paymentTerminalService;
	@Autowired private UserService userService;
	
	private PaymentTerminalAssignment paymentTerminalAssignment;
	private JComboBox<User> userComboBox;
	private JComboBox<PaymentTerminal> paymentTerminalComboBox;
	private JButton saveButton;
	private JButton deleteButton;
	
	@Override
	protected void initializeComponents() {
		userComboBox = new JComboBox<>();
		paymentTerminalComboBox = new JComboBox<>();
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePaymentTerminalAssignment();
			}
		});
	}

	protected void savePaymentTerminalAssignment() {
		if (!validatePaymentTerminalAssignment()) {
			return;
		}
		
		int confirm = showConfirmMessage("Save?");
		if (confirm == JOptionPane.OK_OPTION) {
			paymentTerminalAssignment.setUser((User)userComboBox.getSelectedItem());
			paymentTerminalAssignment.setPaymentTerminal(
					(PaymentTerminal)paymentTerminalComboBox.getSelectedItem());
			
			try {
				paymentTerminalService.save(paymentTerminalAssignment);
				showMessage("Saved!");
				getMagicFrame().switchToEditPaymentTerminalAssignmentPanel(paymentTerminalAssignment);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private boolean validatePaymentTerminalAssignment() {
		try {
			validateMandatoryField(userComboBox, "User");
			validateMandatoryField(paymentTerminalComboBox, "Payment Terminal");
		} catch (ValidationException e) {
			return false;
		}
		
//		PaymentTerminalAssignment existing = paymentTerminalService.findPaymentTerminalAssignmentByName(nameField.getText());
//		if (existing != null) {
//			if (paymentTerminalAssignment.getId() == null || !existing.getId().equals(paymentTerminalAssignment.getId())) {			
//				showErrorMessage("Name is already used by another PaymentTerminalAssignment record");
//				nameField.requestFocusInWindow();
//				return false;
//			}	
//		}
		
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
		mainPanel.add(ComponentUtil.createLabel(100, "User: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		userComboBox.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(userComboBox, c);
		
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
		mainPanel.add(ComponentUtil.createLabel(150, "Payment Terminal: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentTerminalComboBox.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(paymentTerminalComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createVerticalFiller(20), c);
		
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
				savePaymentTerminalAssignment();
			}
		});
	}

	public void updateDisplay(PaymentTerminalAssignment paymentTerminalAssignment) {
		List<User> users = userService.getAllUsers();
		userComboBox.setModel(
				new DefaultComboBoxModel<>(users.toArray(new User[users.size()])));
		
		List<PaymentTerminal> paymentTerminals = paymentTerminalService.getAllPaymentTerminals();
		paymentTerminalComboBox.setModel(
				new DefaultComboBoxModel<>(paymentTerminals.toArray(new PaymentTerminal[paymentTerminals.size()])));
		
		this.paymentTerminalAssignment = paymentTerminalAssignment;
		if (paymentTerminalAssignment.getUser() == null) {
			clearDisplay();
			return;
		}
		
		userComboBox.setSelectedItem(paymentTerminalAssignment.getUser());
		userComboBox.setEnabled(false);
		paymentTerminalComboBox.setSelectedItem(paymentTerminalAssignment.getPaymentTerminal());
		deleteButton.setEnabled(true);
	}

	private void clearDisplay() {
		userComboBox.setSelectedItem(null);
		userComboBox.setEnabled(true);
		paymentTerminalComboBox.setSelectedItem(null);
		deleteButton.setEnabled(false);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPaymentTerminalAssignmentListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		deleteButton = new MagicToolBarButton("trash", "Delete");
		deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deletePaymentTerminalAssignment();
			}
		});
		toolBar.add(deleteButton);
	}

	private void deletePaymentTerminalAssignment() {
		if (confirm("Delete Payment Terminal Assignment?")) {
			try {
				paymentTerminalService.delete(paymentTerminalAssignment);
				showMessage("Assignment deleted");
				getMagicFrame().switchToPaymentTerminalAssignmentListPanel();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occured");
			}
		}
	}

}
