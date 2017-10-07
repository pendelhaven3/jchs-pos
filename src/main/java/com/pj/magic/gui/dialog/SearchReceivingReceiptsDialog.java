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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.ReceivingReceiptSearchCriteria;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.KeyUtil;

@Component
public class SearchReceivingReceiptsDialog extends MagicDialog {

	private static final int STATUS_NEW = 1;
	private static final int STATUS_POSTED = 2;
	private static final int STATUS_CANCELLED = 3;
	
	@Autowired private SupplierService supplierService;
	
	private MagicTextField receivingReceiptNumberField;
	private MagicComboBox<Supplier> supplierComboBox;
	private MagicComboBox<String> statusComboBox;
	private UtilCalendarModel receivedDateFromModel;
	private UtilCalendarModel receivedDateToModel;
	private JButton searchButton;
	private ReceivingReceiptSearchCriteria searchCriteria;
	
	public SearchReceivingReceiptsDialog() {
		setSize(550, 250);
		setLocationRelativeTo(null);
		setTitle("Search Receiving Receipts");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		receivingReceiptNumberField = new MagicTextField();
		receivingReceiptNumberField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		
		supplierComboBox = new MagicComboBox<>();
		
		statusComboBox = new MagicComboBox<>();
		statusComboBox.setModel(new DefaultComboBoxModel<>(
				new String[] {"All", "New", "Posted", "Cancelled"}));
		
		receivedDateFromModel = new UtilCalendarModel();
		receivedDateToModel = new UtilCalendarModel();
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveReceivingReceiptCriteria();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(receivingReceiptNumberField);
	}

	private void saveReceivingReceiptCriteria() {
		searchCriteria = new ReceivingReceiptSearchCriteria();
		
		if (!StringUtils.isEmpty(receivingReceiptNumberField.getText())) {
			searchCriteria.setReceivingReceiptNumber(Long.valueOf(receivingReceiptNumberField.getText()));
			
		}
		
		searchCriteria.setSupplier((Supplier)supplierComboBox.getSelectedItem());
		
		switch (statusComboBox.getSelectedIndex()) {
		case STATUS_NEW:
			searchCriteria.setPosted(false);
			searchCriteria.setCancelled(false);
			break;
		case STATUS_POSTED:
			searchCriteria.setPosted(true);
			break;
		case STATUS_CANCELLED:
			searchCriteria.setCancelled(true);
			break;
		}
		
		if (receivedDateFromModel.getValue() != null) {
			searchCriteria.setReceivedDateFrom(receivedDateFromModel.getValue().getTime());
		}
		if (receivedDateToModel.getValue() != null) {
			searchCriteria.setReceivedDateTo(receivedDateToModel.getValue().getTime());
		}
		
		setVisible(false);
	}

	private void registerKeyBindings() {
		receivingReceiptNumberField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				supplierComboBox.requestFocusInWindow();
			}
		});
		
		supplierComboBox.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		supplierComboBox.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchButton.requestFocusInWindow();
			}
		});
		
		searchButton.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		searchButton.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveReceivingReceiptCriteria();
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
		add(ComponentUtil.createLabel(170, "Receiving Receipt No.:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		receivingReceiptNumberField.setPreferredSize(new Dimension(100, 25));
		add(receivingReceiptNumberField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Supplier:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		supplierComboBox.setPreferredSize(new Dimension(300, 25));
		add(supplierComboBox, c);

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
		add(ComponentUtil.createLabel(150, "Received Date From:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl dateFromPanel = new JDatePanelImpl(receivedDateFromModel);
		JDatePickerImpl dateFromPicker = new JDatePickerImpl(dateFromPanel, new DatePickerFormatter());
		add(dateFromPicker, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "Received Date To:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl dateToPanel = new JDatePanelImpl(receivedDateToModel);
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
	
	public ReceivingReceiptSearchCriteria getSearchCriteria() {
		ReceivingReceiptSearchCriteria returnCriteria = searchCriteria;
		searchCriteria = null;
		return returnCriteria;
	}
	
	public void updateDisplay() {
		searchCriteria = null;
		receivingReceiptNumberField.setText(null);

		List<Supplier> suppliers = supplierService.getAllSuppliers();
		suppliers.add(0, null);
		supplierComboBox.setModel(
				new DefaultComboBoxModel<>(suppliers.toArray(new Supplier[suppliers.size()])));
		
		statusComboBox.setSelectedIndex(0);
		
		receivedDateFromModel.setValue(null);
	}
	
}