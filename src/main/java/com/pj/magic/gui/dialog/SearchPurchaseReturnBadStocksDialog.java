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
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.PurchaseReturnBadStockSearchCriteria;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.KeyUtil;

@Component
public class SearchPurchaseReturnBadStocksDialog extends MagicDialog {

	private static final int STATUS_ALL = 0;
	private static final int STATUS_NEW = 1;
	private static final int STATUS_POSTED = 2;
	
	@Autowired private SupplierService supplierService;
	@Autowired private SelectSupplierDialog selectSupplierDialog;
	
	private MagicTextField purchaseReturnBadStockNumberField;
	private MagicTextField supplierCodeField;
	private JLabel supplierNameField;
	private MagicComboBox<String> statusComboBox;
	private UtilCalendarModel postDateModel;
	private JButton searchButton;
	private PurchaseReturnBadStockSearchCriteria searchCriteria;
	private JButton selectSupplierButton;
	
	public SearchPurchaseReturnBadStocksDialog() {
		setSize(600, 250);
		setLocationRelativeTo(null);
		setTitle("Search Bad Purchase Returns");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		purchaseReturnBadStockNumberField = new MagicTextField();
		
		supplierCodeField = new MagicTextField();
		supplierCodeField.setMaximumLength(Constants.CUSTOMER_CODE_MAXIMUM_LENGTH);
		
		selectSupplierButton = new EllipsisButton();
		selectSupplierButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectSupplierDialog();
			}
		});
		
		statusComboBox = new MagicComboBox<>();
		statusComboBox.setModel(new DefaultComboBoxModel<>(new String[] {"All", "New", "Posted"}));
		
		postDateModel = new UtilCalendarModel();
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePurchaseReturnBadStockCriteria();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(purchaseReturnBadStockNumberField);
	}

	private void openSelectSupplierDialog() {
		selectSupplierDialog.searchSuppliers(supplierCodeField.getText());
		selectSupplierDialog.setVisible(true);
		
		Supplier supplier = selectSupplierDialog.getSelectedSupplier();
		if (supplier != null) {
			supplierCodeField.setText(supplier.getCode());
			supplierNameField.setText(supplier.getName());
		}
	}

	private void savePurchaseReturnBadStockCriteria() {
		searchCriteria = new PurchaseReturnBadStockSearchCriteria();
		
		if (!StringUtils.isEmpty(purchaseReturnBadStockNumberField.getText())) {
			searchCriteria.setPurchaseReturnBadStockNumber(Long.valueOf(purchaseReturnBadStockNumberField.getText()));
		}
		
		Supplier supplier = supplierService.findSupplierByCode(supplierCodeField.getText());
		searchCriteria.setSupplier(supplier);
		if (supplier != null) {
			supplierNameField.setText(supplier.getName());
		} else {
			supplierNameField.setText(null);
		}
		
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
		purchaseReturnBadStockNumberField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				supplierCodeField.requestFocusInWindow();
			}
		});
		
		supplierCodeField.onF5Key(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectSupplierDialog();
			}
		});
		
		supplierCodeField.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		supplierCodeField.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchButton.requestFocusInWindow();
			}
		});
		
		searchButton.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		searchButton.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePurchaseReturnBadStockCriteria();
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
		add(ComponentUtil.createLabel(160, "PRBS No.:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		purchaseReturnBadStockNumberField.setPreferredSize(new Dimension(100, 25));
		add(purchaseReturnBadStockNumberField, c);

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
	
	public PurchaseReturnBadStockSearchCriteria getSearchCriteria() {
		PurchaseReturnBadStockSearchCriteria returnCriteria = searchCriteria;
		searchCriteria = null;
		return returnCriteria;
	}
	
	public void updateDisplay() {
		searchCriteria = null;
		purchaseReturnBadStockNumberField.setText(null);
		supplierCodeField.setText(null);
		supplierNameField.setText(null);
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
		panel.add(ComponentUtil.createFiller(10, 20), c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierNameField = ComponentUtil.createLabel(200);
		panel.add(supplierNameField, c);
		
		return panel;
	}
	
}