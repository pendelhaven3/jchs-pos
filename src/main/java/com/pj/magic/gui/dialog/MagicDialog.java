package com.pj.magic.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.pj.magic.Constants;

public abstract class MagicDialog extends JDialog {

	private static final String CLOSE_ACTION_NAME = "close";
	
	public MagicDialog() {
		setModal(true);
		closeDialogWhenEscapeKeyPressed();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	protected void closeDialogWhenEscapeKeyPressed() {
		final JDialog dialog = this;
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CLOSE_ACTION_NAME);
		getRootPane().getActionMap().put(CLOSE_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doWhenEscapeKeyPressed();
				setVisible(false);
				dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
			}

		});
	}

	protected abstract void doWhenEscapeKeyPressed();
	
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
	
	protected boolean confirm(String message) {
		int confirm = JOptionPane.showConfirmDialog(this, message, "Confirmation Message", JOptionPane.YES_NO_OPTION);
		return confirm == JOptionPane.OK_OPTION;
	}
	
	protected void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message);
	}
	
	protected void showUnexpectedErrorMessage() {
		showErrorMessage(Constants.UNEXPECTED_ERROR_MESSAGE);
	}
	
}