package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.AdjustmentType;
import com.pj.magic.model.Customer;
import com.pj.magic.model.search.PaymentAdjustmentSearchCriteria;
import com.pj.magic.service.AdjustmentTypeService;
import com.pj.magic.service.CustomerService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.KeyUtil;

@Component
public class SearchPaymentAdjustmentsDialog extends MagicDialog {

	private static final int STATUS_ALL = 0;
	private static final int STATUS_NEW = 1;
	private static final int STATUS_POSTED = 2;
	private static final int STATUS_PAID = 3;
	
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	@Autowired private AdjustmentTypeService adjustmentTypeService;
	@Autowired private CustomerService customerService;
	
	private MagicTextField paymentAdjustmentNumberField;
	private MagicTextField customerCodeField;
	private JButton selectCustomerButton;
	private JLabel customerNameLabel;
	private JComboBox<AdjustmentType> adjustmentTypeComboBox;
	private JComboBox<String> statusComboBox;
	private UtilCalendarModel postDateModel;
	private JButton searchButton;
	private PaymentAdjustmentSearchCriteria searchCriteria;
	
	public SearchPaymentAdjustmentsDialog() {
		setSize(600, 250);
		setLocationRelativeTo(null);
		setTitle("Search Payment Adjustments");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		paymentAdjustmentNumberField = new MagicTextField();
		paymentAdjustmentNumberField.setNumbersOnly(true);
		paymentAdjustmentNumberField.setMaximumLength(10);
		
		customerCodeField = new MagicTextField();
		customerCodeField.setMaximumLength(Constants.CUSTOMER_CODE_MAXIMUM_LENGTH);
		
		customerNameLabel = new JLabel();
		
		selectCustomerButton = new EllipsisButton();
		selectCustomerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectCustomerDialog();
			}
		});
		
		adjustmentTypeComboBox = new JComboBox<>();
		
		statusComboBox = new JComboBox<>();
		statusComboBox.setModel(new DefaultComboBoxModel<>(new String[] {"All", "New", "Posted/Unpaid", "Paid"}));
		
		postDateModel = new UtilCalendarModel();
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePaymentAdjustmentCriteria();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(paymentAdjustmentNumberField);
	}

	private void openSelectCustomerDialog() {
		selectCustomerDialog.searchCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();	
		if (customer != null) {
			customerCodeField.setText(customer.getCode());
			customerNameLabel.setText(customer.getName());
		}
	}

	private void savePaymentAdjustmentCriteria() {
		searchCriteria = new PaymentAdjustmentSearchCriteria();
		if (!StringUtils.isEmpty(paymentAdjustmentNumberField.getText())) {
			searchCriteria.setPaymentAdjustmentNumber(Long.valueOf(paymentAdjustmentNumberField.getText()));
		}
		
		Customer customer = customerService.findCustomerByCode(customerCodeField.getText());
		searchCriteria.setCustomer(customer);
		if (customer != null) {
			customerNameLabel.setText(customer.getName());
		} else {
			customerNameLabel.setText(null);
		}
		
		searchCriteria.setAdjustmentType((AdjustmentType)adjustmentTypeComboBox.getSelectedItem());
		
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
		paymentAdjustmentNumberField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				statusComboBox.requestFocusInWindow();
			}
		});
		
		customerCodeField.getInputMap().put(KeyUtil.getF5Key(), "openSelectCustomerDialog");
		customerCodeField.getActionMap().put("openSelectCustomerDialog", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectCustomerDialog();
			}
		});
		
		statusComboBox.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		statusComboBox.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchButton.requestFocusInWindow();
			}
		});
		
		searchButton.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		searchButton.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePaymentAdjustmentCriteria();
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
		add(ComponentUtil.createLabel(140, "Payment Adj. No.:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentAdjustmentNumberField.setPreferredSize(new Dimension(100, 25));
		add(paymentAdjustmentNumberField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(80, "Customer:"), c);

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
		add(ComponentUtil.createLabel(120, "Adjustment Type:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		adjustmentTypeComboBox.setPreferredSize(new Dimension(150, 25));
		add(adjustmentTypeComboBox, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(80, "Status:"), c);

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
		add(Box.createVerticalStrut(10), c);
		
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
		customerNameLabel = ComponentUtil.createLabel(200);
		panel.add(customerNameLabel, c);
		
		return panel;
	}

	public PaymentAdjustmentSearchCriteria getSearchCriteria() {
		PaymentAdjustmentSearchCriteria returnCriteria = searchCriteria;
		searchCriteria = null;
		return returnCriteria;
	}
	
	public void updateDisplay() {
		searchCriteria = null;
		paymentAdjustmentNumberField.setText(null);
		customerCodeField.setText(null);
		customerNameLabel.setText(null);
		
		List<AdjustmentType> adjustmentTypes = adjustmentTypeService.getRegularAdjustmentTypes();
		adjustmentTypeComboBox.setModel(
				new DefaultComboBoxModel<>(adjustmentTypes.toArray(new AdjustmentType[adjustmentTypes.size()])));
		adjustmentTypeComboBox.insertItemAt(null, 0);
		adjustmentTypeComboBox.setSelectedItem(null);
		
		statusComboBox.setSelectedIndex(0);
		postDateModel.setValue(null);
	}
	
}