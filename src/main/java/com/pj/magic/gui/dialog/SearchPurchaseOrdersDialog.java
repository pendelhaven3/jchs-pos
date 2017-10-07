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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.PurchaseOrderSearchCriteria;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.KeyUtil;

@Component
public class SearchPurchaseOrdersDialog extends MagicDialog {

	@Autowired private SupplierService supplierService;
	
	private MagicTextField purchaseOrderNumberField;
	private MagicComboBox<Supplier> supplierComboBox;
	private MagicComboBox<String> statusComboBox;
	private JButton searchButton;
	private PurchaseOrderSearchCriteria searchCriteria;
	
	public SearchPurchaseOrdersDialog() {
		setSize(530, 210);
		setLocationRelativeTo(null);
		setTitle("Search Purchase Orders");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		purchaseOrderNumberField = new MagicTextField();
		
		supplierComboBox = new MagicComboBox<>();
		
		statusComboBox = new MagicComboBox<>();
		statusComboBox.setModel(new DefaultComboBoxModel<>(new String[] {"All", "Non-Posted", "Posted"}));
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePurchaseOrderCriteria();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(purchaseOrderNumberField);
	}

	private void savePurchaseOrderCriteria() {
		searchCriteria = new PurchaseOrderSearchCriteria();
		
		if (!StringUtils.isEmpty(purchaseOrderNumberField.getText())) {
			searchCriteria.setPurchaseOrderNumber(Long.valueOf(purchaseOrderNumberField.getText()));
			
		}
		
		searchCriteria.setSupplier((Supplier)supplierComboBox.getSelectedItem());
		
		if (statusComboBox.getSelectedIndex() != 0) {
			searchCriteria.setPosted(statusComboBox.getSelectedIndex() == 2);
		}
		
		setVisible(false);
	}

	private void registerKeyBindings() {
		purchaseOrderNumberField.onEnterKey(new AbstractAction() {
			
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
				savePurchaseOrderCriteria();
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
		add(ComponentUtil.createLabel(150, "Purchase Order No.:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		purchaseOrderNumberField.setPreferredSize(new Dimension(100, 25));
		add(purchaseOrderNumberField, c);

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
	
	public PurchaseOrderSearchCriteria getSearchCriteria() {
		PurchaseOrderSearchCriteria returnCriteria = searchCriteria;
		searchCriteria = null;
		return returnCriteria;
	}
	
	public void updateDisplay() {
		searchCriteria = null;
		purchaseOrderNumberField.setText(null);

		List<Supplier> suppliers = supplierService.getAllSuppliers();
		suppliers.add(0, null);
		supplierComboBox.setModel(
				new DefaultComboBoxModel<>(suppliers.toArray(new Supplier[suppliers.size()])));
		
		statusComboBox.setSelectedIndex(0);
	}
	
}