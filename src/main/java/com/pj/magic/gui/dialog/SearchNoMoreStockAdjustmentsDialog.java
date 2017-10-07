package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.Customer;
import com.pj.magic.model.search.NoMoreStockAdjustmentSearchCriteria;
import com.pj.magic.service.CustomerService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.KeyUtil;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class SearchNoMoreStockAdjustmentsDialog extends MagicDialog {

	private static final int STATUS_ALL = 0;
	private static final int STATUS_NEW = 1;
	private static final int STATUS_POSTED = 2;
	private static final int STATUS_PAID = 3;
	
	@Autowired private CustomerService customerService;
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	
	private MagicTextField noMoreStockAdjustmentNumberField;
	private MagicTextField customerCodeField;
	private JLabel customerNameField;
	private MagicComboBox<String> statusComboBox;
	private UtilCalendarModel postDateModel;
	private JButton searchButton;
	private NoMoreStockAdjustmentSearchCriteria searchCriteria;
	private JButton selectCustomerButton;
	
	public SearchNoMoreStockAdjustmentsDialog() {
		setSize(600, 250);
		setLocationRelativeTo(null);
		setTitle("Search No More Stock Adjustments");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		noMoreStockAdjustmentNumberField = new MagicTextField();
		
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
		statusComboBox.setModel(new DefaultComboBoxModel<>(new String[] {"All", "New", "Posted/Unpaid", "Paid"}));
		
		postDateModel = new UtilCalendarModel();
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveNoMoreStockAdjustmentCriteria();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(noMoreStockAdjustmentNumberField);
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

	private void saveNoMoreStockAdjustmentCriteria() {
		searchCriteria = new NoMoreStockAdjustmentSearchCriteria();
		
		if (!StringUtils.isEmpty(noMoreStockAdjustmentNumberField.getText())) {
			searchCriteria.setNoMoreStockAdjustmentNumber(Long.valueOf(noMoreStockAdjustmentNumberField.getText()));
		}
		
		Customer customer = customerService.findCustomerByCode(customerCodeField.getText());
		searchCriteria.setCustomer(customer);
		if (customer != null) {
			customerNameField.setText(customer.getName());
		} else {
			customerNameField.setText(null);
		}
		
		if (statusComboBox.getSelectedIndex() != STATUS_ALL) {
			switch (statusComboBox.getSelectedIndex()) {
			case STATUS_NEW:
				searchCriteria.setPosted(false);
				searchCriteria.setPaid(false);
				break;
			case STATUS_POSTED:
				searchCriteria.setPosted(true);
				searchCriteria.setPaid(false);
				break;
			case STATUS_PAID:
				searchCriteria.setPaid(true);
				break;
			}
		}

		if (postDateModel.getValue() != null) {
			searchCriteria.setPostDate(postDateModel.getValue().getTime());
		}
		
		setVisible(false);
	}

	private void registerKeyBindings() {
		noMoreStockAdjustmentNumberField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				customerCodeField.requestFocusInWindow();
			}
		});
		
		customerCodeField.onF5Key(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectCustomerDialog();
			}
		});
		
		customerCodeField.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		customerCodeField.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchButton.requestFocusInWindow();
			}
		});
		
		searchButton.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		searchButton.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveNoMoreStockAdjustmentCriteria();
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
		add(ComponentUtil.createLabel(150, "NMS No.:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		noMoreStockAdjustmentNumberField.setPreferredSize(new Dimension(100, 25));
		add(noMoreStockAdjustmentNumberField, c);

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
		add(ComponentUtil.createLabel(140, "Post Date:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl datePanel = new JDatePanelImpl(postDateModel);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		add(datePicker, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createVerticalStrut(5), c);
		
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
		add(Box.createGlue(), c);
	}
	
	public NoMoreStockAdjustmentSearchCriteria getSearchCriteria() {
		NoMoreStockAdjustmentSearchCriteria returnCriteria = searchCriteria;
		searchCriteria = null;
		return returnCriteria;
	}
	
	public void updateDisplay() {
		searchCriteria = null;
		noMoreStockAdjustmentNumberField.setText(null);
		customerCodeField.setText(null);
		customerNameField.setText(null);
		statusComboBox.setSelectedIndex(STATUS_ALL);
		postDateModel.setValue(null);
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
		panel.add(Box.createHorizontalStrut(10), c);
		
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