package com.pj.magic.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.pj.magic.Constants;
import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.MagicFrame;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.service.LoginService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.KeyUtil;

import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

public abstract class AbstractMagicPanel extends JPanel {

	private static final String FOCUS_NEXT_FIELD_ACTION_NAME = "focusNextField";
	
	@Autowired private LoginService loginService;
	
	private List<JComponent> focusOrder;
	private JLabel loggedInUserField;
	
	@PostConstruct
	public void initialize() {
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		registerBackKeyBinding();
		initializeComponents();
		layoutComponents();
		registerKeyBindings();
	}
	
	/**
	 * Initialize screen components
	 */
	protected abstract void initializeComponents();

	/**
	 * Layout screen components
	 */
	protected abstract void layoutComponents();

	protected abstract void registerKeyBindings();
	
	/**
	 * What to do when back key (currently F9) is pressed.
	 */
	protected abstract void doOnBack();
	
	/**
	 * Add the components to focusOrder list in the order that you want the focus to be.
	 * Custom focus order only used when calling focusNextField() method.
	 * 
	 * @param focusOrder
	 */
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		// meant to be overriden
	}
	
	protected void focusNextField() {
		if (focusOrder == null) {
			focusOrder = new ArrayList<>();
			initializeFocusOrder(focusOrder);
		}
		java.awt.Component focusOwner = FocusManager.getCurrentManager().getFocusOwner();
		int focusOwnerIndex = focusOrder.indexOf(focusOwner);
		if (focusOwnerIndex != focusOrder.size() - 1) {
			focusOrder.get(focusOwnerIndex + 1).requestFocusInWindow();
		}
	}

	protected void registerBackKeyBinding() {
		final String actionName = "back";
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), actionName);
		getActionMap().put(actionName, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}

	protected int showConfirmMessage(String message) {
		return JOptionPane.showConfirmDialog(this, message, "Confirmation Message", JOptionPane.YES_NO_OPTION);
	}
	
	// TODO: Migrate references here
	protected boolean confirm(String message) {
		int confirm = JOptionPane.showConfirmDialog(this, message, "Confirmation Message", JOptionPane.YES_NO_OPTION);
		return confirm == JOptionPane.OK_OPTION;
	}
	
	protected void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message);
	}
	
	protected MagicFrame getMagicFrame() {
		return (MagicFrame)SwingUtilities.getRoot(this);
	}
	
	protected void focusOnComponentWhenThisPanelIsDisplayed(JComponent component) {
		final JComponent target = component;
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				if (!target.isFocusable()) {
					target.setFocusable(true);
				}
				target.requestFocusInWindow();
			}
		});
	}
	
	protected void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(this, message, "Error Message", JOptionPane.ERROR_MESSAGE);
	}
	
	protected void validateMandatoryField(JTextField field, String description) throws ValidationException {
		if (StringUtils.isEmpty(field.getText())) {
			showErrorMessage(description + " must be specified");
			field.requestFocusInWindow();
			throw new ValidationException();
		}
	}
	
	protected void validateMandatoryField(@SuppressWarnings("rawtypes") JComboBox comboBox, String description) throws ValidationException {
		if (comboBox.getSelectedItem() == null) {
			showErrorMessage(description + " must be specified");
			comboBox.requestFocusInWindow();
			throw new ValidationException();
		}
	}
	
	protected void validateMandatoryField(UtilCalendarModel dateModel, String description) throws ValidationException {
		if (dateModel.getValue() == null) {
			showErrorMessage(description + " must be specified");
			throw new ValidationException();
		}
	}
	
	protected void addBackButton(JToolBar toolBar) {
		JButton backButton = new MagicToolBarButton("back", "Back (F9)");
		backButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
		toolBar.add(backButton);
	}
	
	protected void addUsernameFieldAndLogoutButton(MagicToolBar toolBar) {
		if (toolBar.hasRightSideContent()) {
			return;
		}
		toolBar.setRightSideContent(true);
		toolBar.add(Box.createHorizontalGlue());
		
		loggedInUserField = ComponentUtil.createRightLabel(150, "");
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				loggedInUserField.setText("Welcome, " + 
						loginService.getLoggedInUser().getUsername());
			}
		});
		
		toolBar.add(loggedInUserField);
		toolBar.add(ComponentUtil.createFiller(10, 1));
		
		JButton logoutButton = new MagicToolBarButton("logout", "Logout");
		logoutButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loginService.logout();
				getMagicFrame().switchToLoginPanel();
			}
		});
		toolBar.add(logoutButton);
	}
	
	protected void setFocusOnNextFieldOnEnterKey(JComponent component) {
		component.getInputMap().put(KeyUtil.getEnterKey(), FOCUS_NEXT_FIELD_ACTION_NAME);
		component.getActionMap().put(FOCUS_NEXT_FIELD_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				focusNextField();
			}
		});
	}
	
	protected void showMessageForUnexpectedError() {
		showErrorMessage(Constants.UNEXPECTED_ERROR_MESSAGE);
	}
	
}
