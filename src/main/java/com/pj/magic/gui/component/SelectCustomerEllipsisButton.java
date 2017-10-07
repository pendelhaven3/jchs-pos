package com.pj.magic.gui.component;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.model.Customer;
import com.pj.magic.util.ComponentUtil;

public class SelectCustomerEllipsisButton extends EllipsisButton {

	private static final String TOOLTIP_TEXT = "Select Customer (F5)";
	
	private SelectCustomerDialog selectCustomerDialog;
	private MagicTextField customerCodeField;
	private JLabel customerNameLabel;
	private OnSelectCustomerAction onSelectCustomerAction;
	
	public SelectCustomerEllipsisButton(SelectCustomerDialog selectCustomerDialog, 
			MagicTextField customerCodeField, JLabel customerNameLabel) {
		this.selectCustomerDialog = selectCustomerDialog;
		this.customerCodeField = customerCodeField;
		this.customerNameLabel = customerNameLabel;
		
		setText(TOOLTIP_TEXT);
		addActionListener(e -> openSelectCustomerDialog());
		customerCodeField.onF5Key(() -> openSelectCustomerDialog());
	}
	
	private void openSelectCustomerDialog() {
		selectCustomerDialog.searchCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			customerCodeField.setText(customer.getCode());
			customerNameLabel.setText(customer.getName());
			if (onSelectCustomerAction != null) {
				onSelectCustomerAction.onSelectCustomer(customer);
			}
		}
	}

	public JPanel getFieldsPanel() {
		if (!customerCodeField.isPreferredSizeSet()) {
			customerCodeField.setPreferredSize(new Dimension(100, 25));
		}
		if (!customerNameLabel.isPreferredSizeSet()) {
			customerNameLabel.setPreferredSize(new Dimension(300, 25));
		}
		return ComponentUtil.createGenericPanel(customerCodeField, this, 
				Box.createHorizontalStrut(10), customerNameLabel);
	}
	
	public void addOnSelectCustomerAction(OnSelectCustomerAction onSelectCustomerAction) {
		this.onSelectCustomerAction = onSelectCustomerAction;
	}
	
	public interface OnSelectCustomerAction {
		
		void onSelectCustomer(Customer customer);
		
	}
	
}
