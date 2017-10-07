package com.pj.magic.gui.component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public abstract class MagicCellEditor extends DefaultCellEditor {

	public MagicCellEditor(JTextField textField) {
		super(textField);
	}

	public MagicCellEditor(JComboBox<?> comboBox) {
		super(comboBox);
	}

	protected void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(
				SwingUtilities.getWindowAncestor(getComponent()), 
				message,
				"Error Message",
				JOptionPane.ERROR_MESSAGE
		);
	}

	protected boolean confirm(String message) {
		return JOptionPane.showConfirmDialog(
				SwingUtilities.getWindowAncestor(getComponent()), 
				message,
				"Confirm",
				JOptionPane.YES_NO_OPTION
		) == JOptionPane.OK_OPTION;
	}
	
}
