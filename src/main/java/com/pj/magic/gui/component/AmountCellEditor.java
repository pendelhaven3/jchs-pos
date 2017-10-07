package com.pj.magic.gui.component;

import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;

import com.pj.magic.util.NumberUtil;

public class AmountCellEditor extends MagicCellEditor {

	public AmountCellEditor(JTextField textField) {
		super(textField);
	}
	
	@Override
	public boolean stopCellEditing() {
		String amount = ((JTextField)getComponent()).getText();
		boolean valid = false;
		if (StringUtils.isEmpty(amount)) {
			showErrorMessage("Amount must be specified");
		} else if (!NumberUtil.isAmount(amount)) {
			showErrorMessage("Amount must be a valid amount");
		} else {
			valid = true;
		}
		return (valid) ? super.stopCellEditing() : false;
	}

}