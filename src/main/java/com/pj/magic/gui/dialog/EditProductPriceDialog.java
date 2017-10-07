package com.pj.magic.gui.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.EditProductPriceTable;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.UnitCost;
import com.pj.magic.model.UnitPrice;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class EditProductPriceDialog extends MagicDialog {

	@Autowired private ProductService productService;
	@Autowired private EditProductPriceTable table;
	@Autowired private ProductPriceHistoryDialog productPriceHistoryDialog;

	private PricingScheme pricingScheme;
	private Product product;
	private JLabel productCodeLabel;
	private JLabel productDescriptionLabel;
	private JLabel pricingSchemeNameLabel;
	private JLabel companyListPriceLabel;
	private JButton saveButton;
	private JButton showPriceHistoryButton;
	
	public EditProductPriceDialog() {
		setSize(600, 400);
		setLocationRelativeTo(null);
		setTitle("Edit Product Price");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		productCodeLabel = new JLabel();
		productDescriptionLabel = new JLabel();
		pricingSchemeNameLabel = new JLabel();
		companyListPriceLabel = new JLabel();
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveUnitCostsAndPrices();
			}
		});
		
		showPriceHistoryButton = new JButton("Show Price History");
		showPriceHistoryButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				productPriceHistoryDialog.updateDisplay(product, pricingScheme);
				productPriceHistoryDialog.setVisible(true);
			}
		});
	}

	private void saveUnitCostsAndPrices() {
		if (table.isEditing()) {
			if (!table.getCellEditor().stopCellEditing()) {
				table.getEditorComponent().requestFocusInWindow();
				return;
			}
		}
		productService.saveUnitCostsAndPrices(product, pricingScheme);
		JOptionPane.showMessageDialog(this, "Saved!");
	}

	private void registerKeyBindings() {
		// none
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		if (table.isEditing()) {
			table.getCellEditor().cancelCellEditing();
		}
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(130, "Pricing Scheme:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		pricingSchemeNameLabel.setPreferredSize(new Dimension(100, 20));
		add(pricingSchemeNameLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(130, "Product Code:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		productCodeLabel.setPreferredSize(new Dimension(150, 20));
		add(productCodeLabel, c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "Product Description:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		productDescriptionLabel.setPreferredSize(new Dimension(300, 20));
		add(productDescriptionLabel, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(160, "Company List Price:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		companyListPriceLabel.setPreferredSize(new Dimension(100, 20));
		add(companyListPriceLabel, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(createTablePanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.CENTER;
		saveButton.setPreferredSize(new Dimension(100, 25));
		add(saveButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		c.insets.top = 10;
		showPriceHistoryButton.setPreferredSize(new Dimension(180, 25));
		add(showPriceHistoryButton, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.0;
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createGlue(), c);
	}
	
	private JPanel createTablePanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(table.getTableHeader(), c);
		
		c.weightx = c.weighty = 1.0;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(table, c);

		return panel;
	}

	public void updateDisplay(Product product, PricingScheme pricingScheme) {
		this.product = clone(product);
		this.pricingScheme = pricingScheme;
		
		productCodeLabel.setText(product.getCode());
		productDescriptionLabel.setText(product.getDescription());
		pricingSchemeNameLabel.setText(pricingScheme.getName());
		companyListPriceLabel.setText(FormatterUtil.formatAmount(product.getCompanyListPrice()));
		
		table.setProduct(this.product);
		table.highlight();
	}

	private Product clone(Product product) {
		Product clone = new Product();
		clone.setId(product.getId());
		clone.getUnits().addAll(product.getUnits());
		for (UnitPrice unitPrice : product.getUnitPrices()) {
			clone.getUnitPrices().add(new UnitPrice(unitPrice));
		}
		for (UnitCost unitCost : product.getUnitCosts()) {
			clone.getUnitCosts().add(new UnitCost(unitCost));
		}
		clone.getUnitConversions().addAll(product.getUnitConversions());
		return clone;
	}

}
