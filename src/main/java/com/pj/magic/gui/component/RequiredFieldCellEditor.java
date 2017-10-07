package com.pj.magic.gui.component;

import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;

public class RequiredFieldCellEditor extends MagicCellEditor {

	private String fieldName;
	
	public RequiredFieldCellEditor(JTextField textField, String fieldName) {
		super(textField);
		this.fieldName = fieldName;
	}
	
	@Override
	public boolean stopCellEditing() {
		String amount = ((JTextField)getComponent()).getText();
		boolean valid = false;
		if (StringUtils.isEmpty(amount)) {
			showErrorMessage(fieldName + " must be specified");
		} else {
			valid = true;
		}
		return (valid) ? super.stopCellEditing() : false;
	}

}