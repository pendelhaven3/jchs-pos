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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.PurchasePaymentAdjustmentType;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.PurchasePaymentAdjustmentSearchCriteria;
import com.pj.magic.service.PurchasePaymentAdjustmentTypeService;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.KeyUtil;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class SearchPurchasePaymentAdjustmentsDialog extends MagicDialog {

	private static final int STATUS_ALL = 0;
	private static final int STATUS_NEW = 1;
	private static final int STATUS_POSTED = 2;
	
	@Autowired private SelectSupplierDialog selectSupplierDialog;
	@Autowired private PurchasePaymentAdjustmentTypeService adjustmentTypeService;
	@Autowired private SupplierService supplierService;
	
	private MagicTextField paymentAdjustmentNumberField;
	private MagicTextField supplierCodeField;
	private JButton selectSupplierButton;
	private JLabel supplierNameLabel;
	private JComboBox<PurchasePaymentAdjustmentType> adjustmentTypeComboBox;
	private JComboBox<String> statusComboBox;
	private UtilCalendarModel postDateModel;
	private JButton searchButton;
	private PurchasePaymentAdjustmentSearchCriteria searchCriteria;
	
	public SearchPurchasePaymentAdjustmentsDialog() {
		setSize(600, 250);
		setLocationRelativeTo(null);
		setTitle("Search Purchase Payment Adjustments");
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
		
		supplierCodeField = new MagicTextField();
		supplierCodeField.setMaximumLength(Constants.SUPPLIER_CODE_MAXIMUM_LENGTH);
		
		supplierNameLabel = new JLabel();
		
		selectSupplierButton = new EllipsisButton();
		selectSupplierButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectSupplierDialog();
			}
		});
		
		adjustmentTypeComboBox = new JComboBox<>();
		
		statusComboBox = new JComboBox<>();
		statusComboBox.setModel(new DefaultComboBoxModel<>(new String[] {"All", "New", "Posted"}));
		
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

	private void openSelectSupplierDialog() {
		selectSupplierDialog.searchSuppliers(supplierCodeField.getText());
		selectSupplierDialog.setVisible(true);
		
		Supplier supplier = selectSupplierDialog.getSelectedSupplier();	
		if (supplier != null) {
			supplierCodeField.setText(supplier.getCode());
			supplierNameLabel.setText(supplier.getName());
		}
	}

	private void savePaymentAdjustmentCriteria() {
		searchCriteria = new PurchasePaymentAdjustmentSearchCriteria();
		if (!StringUtils.isEmpty(paymentAdjustmentNumberField.getText())) {
			searchCriteria.setPaymentAdjustmentNumber(Long.valueOf(paymentAdjustmentNumberField.getText()));
		}
		
		Supplier supplier = supplierService.findSupplierByCode(supplierCodeField.getText());
		searchCriteria.setSupplier(supplier);
		if (supplier != null) {
			supplierNameLabel.setText(supplier.getName());
		} else {
			supplierNameLabel.setText(null);
		}
		
		searchCriteria.setAdjustmentType(
				(PurchasePaymentAdjustmentType)adjustmentTypeComboBox.getSelectedItem());
		
		if (statusComboBox.getSelectedIndex() != STATUS_ALL) {
			switch (statusComboBox.getSelectedIndex()) {
			case STATUS_NEW:
				searchCriteria.setPosted(false);
				break;
			case STATUS_POSTED:
				searchCriteria.setPosted(true);
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
		
		supplierCodeField.getInputMap().put(KeyUtil.getF5Key(), "openSelectSupplierDialog");
		supplierCodeField.getActionMap().put("openSelectSupplierDialog", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectSupplierDialog();
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
		add(ComponentUtil.createLabel(80, "Supplier:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(createSupplierPanel(), c);

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
	
	private JPanel createSupplierPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierCodeField.setPreferredSize(new Dimension(120, 25));
		panel.add(supplierCodeField, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectSupplierButton.setPreferredSize(new Dimension(30, 24));
		panel.add(selectSupplierButton, c);
		
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
		supplierNameLabel = ComponentUtil.createLabel(200);
		panel.add(supplierNameLabel, c);
		
		return panel;
	}

	public PurchasePaymentAdjustmentSearchCriteria getSearchCriteria() {
		PurchasePaymentAdjustmentSearchCriteria returnCriteria = searchCriteria;
		searchCriteria = null;
		return returnCriteria;
	}
	
	public void updateDisplay() {
		searchCriteria = null;
		paymentAdjustmentNumberField.setText(null);
		supplierCodeField.setText(null);
		supplierNameLabel.setText(null);
		
		List<PurchasePaymentAdjustmentType> adjustmentTypes = 
				adjustmentTypeService.getRegularAdjustmentTypes();
		adjustmentTypeComboBox.setModel(
				new DefaultComboBoxModel<>(adjustmentTypes.toArray(
						new PurchasePaymentAdjustmentType[adjustmentTypes.size()])));
		adjustmentTypeComboBox.insertItemAt(null, 0);
		adjustmentTypeComboBox.setSelectedItem(null);
		
		statusComboBox.setSelectedIndex(0);
		postDateModel.setValue(null);
	}
	
}