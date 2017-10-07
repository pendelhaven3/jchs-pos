package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.Customer;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
import com.pj.magic.service.CustomerService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.KeyUtil;

@Component
public class SearchSalesInvoicesDialog extends MagicDialog {

	@Autowired private CustomerService customerService;
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	
	private MagicTextField salesInvoiceNumberField;
	private MagicTextField customerCodeField;
	private JLabel customerNameField;
	private MagicComboBox<String> statusComboBox;
	private UtilCalendarModel transactionDateFromModel;
	private UtilCalendarModel transactionDateToModel;
	private JButton searchButton;
	private SalesInvoiceSearchCriteria searchCriteria;
	private JButton selectCustomerButton;
	
	public SearchSalesInvoicesDialog() {
		setSize(600, 270);
		setLocationRelativeTo(null);
		setTitle("Search Sales Invoices");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		salesInvoiceNumberField = new MagicTextField();
		
		customerCodeField = new MagicTextField();
		customerCodeField.setMaximumLength(Constants.CUSTOMER_CODE_MAXIMUM_LENGTH);
		
		selectCustomerButton = new EllipsisButton();
		selectCustomerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectCustomerDialog();
			}
		});
		
		statusComboBox = new MagicComboBox<>();
		statusComboBox.setModel(new DefaultComboBoxModel<>(new String[] {"All", "New", "Marked", "Cancelled"}));
		
		transactionDateFromModel = new UtilCalendarModel();
		transactionDateToModel = new UtilCalendarModel();
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSalesInvoiceCriteria();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(salesInvoiceNumberField);
	}

	private void openSelectCustomerDialog() {
		selectCustomerDialog.searchCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			customerCodeField.setText(customer.getCode());
			customerNameField.setText(customer.getName());
		}
	}

	private void saveSalesInvoiceCriteria() {
		searchCriteria = new SalesInvoiceSearchCriteria();
		
		if (!StringUtils.isEmpty(salesInvoiceNumberField.getText())) {
			searchCriteria.setSalesInvoiceNumber(Long.valueOf(salesInvoiceNumberField.getText()));
		}
		
		Customer customer = customerService.findCustomerByCode(customerCodeField.getText());
		searchCriteria.setCustomer(customer);
		if (customer != null) {
			customerNameField.setText(customer.getName());
		} else {
			customerNameField.setText(null);
		}
		
		if (statusComboBox.getSelectedIndex() != 0) {
			switch (statusComboBox.getSelectedIndex()) {
			case 1:
				searchCriteria.setMarked(false);
				searchCriteria.setCancelled(false);
				break;
			case 2:
				searchCriteria.setMarked(true);
				break;
			case 3:
				searchCriteria.setCancelled(true);
				break;
			}
		}

		if (transactionDateFromModel.getValue() != null) {
			searchCriteria.setTransactionDateFrom(transactionDateFromModel.getValue().getTime());
		}
		if (transactionDateToModel.getValue() != null) {
			searchCriteria.setTransactionDateTo(transactionDateToModel.getValue().getTime());
		}
		
		setVisible(false);
	}

	private void registerKeyBindings() {
		salesInvoiceNumberField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				customerCodeField.requestFocusInWindow();
			}
		});
		
		customerCodeField.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		customerCodeField.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchButton.requestFocusInWindow();
			}
		});
		
		customerCodeField.onF5Key(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectCustomerDialog();
			}
			
		});
		
		searchButton.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		searchButton.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSalesInvoiceCriteria();
			}
		});
		
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		// nothing
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "Sales Invoice No.:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		salesInvoiceNumberField.setPreferredSize(new Dimension(100, 25));
		add(salesInvoiceNumberField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Customer:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(createCustomerPanel(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Status:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusComboBox.setPreferredSize(new Dimension(150, 25));
		add(statusComboBox, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(140, "Trans. Date From:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl dateFromPanel = new JDatePanelImpl(transactionDateFromModel);
		JDatePickerImpl dateFromPicker = new JDatePickerImpl(dateFromPanel, new DatePickerFormatter());
		add(dateFromPicker, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(140, "Trans. Date To:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl dateToPanel = new JDatePanelImpl(transactionDateToModel);
		JDatePickerImpl dateToPicker = new JDatePickerImpl(dateToPanel, new DatePickerFormatter());
		add(dateToPicker, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		searchButton.setPreferredSize(new Dimension(100, 25));
		add(searchButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(), c);
	}
	
	public SalesInvoiceSearchCriteria getSearchCriteria() {
		SalesInvoiceSearchCriteria returnCriteria = searchCriteria;
		searchCriteria = null;
		return returnCriteria;
	}
	
	public void updateDisplay() {
		searchCriteria = null;
		salesInvoiceNumberField.setText(null);
		customerCodeField.setText(null);
		customerNameField.setText(null);
		statusComboBox.setSelectedIndex(0);
		transactionDateFromModel.setValue(null);
		transactionDateToModel.setValue(null);
	}
	
	private JPanel createCustomerPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		customerCodeField.setPreferredSize(new Dimension(120, 25));
		panel.add(customerCodeField, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectCustomerButton.setPreferredSize(new Dimension(30, 24));
		panel.add(selectCustomerButton, c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createFiller(10, 20), c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		customerNameField = ComponentUtil.createLabel(200);
		panel.add(customerNameField, c);
		
		return panel;
	}
	
}