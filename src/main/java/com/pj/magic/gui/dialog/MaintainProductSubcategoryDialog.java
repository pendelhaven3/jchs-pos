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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.ProductSubcategory;
import com.pj.magic.service.ProductCategoryService;
import com.pj.magic.util.ComponentUtil;

@Component
public class MaintainProductSubcategoryDialog extends MagicDialog {

	private static final Logger logger = LoggerFactory.getLogger(MaintainProductSubcategoryDialog.class);
	
	@Autowired private ProductCategoryService productCategoryService;
	
	private MagicTextField subcategoryField;
	private JButton saveButton;
	private ProductSubcategory subcategory;
	private JLabel categoryField;
	private boolean changed;
	
	public MaintainProductSubcategoryDialog() {
		setSize(480, 150);
		setLocationRelativeTo(null);
		getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		subcategoryField = new MagicTextField();
		subcategoryField.setMaximumLength(60);
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSubcategory();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(subcategoryField);
	}

	protected void saveSubcategory() {
		if (StringUtils.isEmpty(subcategoryField.getText())) {
			showErrorMessage("Subcategory must be specified");
			return;
		}
		
		subcategory.setName(subcategoryField.getText());
		try {
			productCategoryService.save(subcategory);
			JOptionPane.showMessageDialog(this, "Saved!");
			changed = true;
			setTitle("Edit Product Subcategory");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			showErrorMessage("Error occurred during saving!");
		}
	}

	private void registerKeyBindings() {
		subcategoryField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSubcategory();
			}
		});
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		subcategoryField.setText(null);
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Category:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		categoryField = ComponentUtil.createLabel(300);
		add(categoryField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Subcategory:"), c);

		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		subcategoryField.setPreferredSize(new Dimension(300, 25));
		add(subcategoryField, c);

		currentRow++;
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		saveButton.setPreferredSize(new Dimension(100, 25));
		add(saveButton, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.0;
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createGlue(), c);
	}
	

	public void updateDisplay(ProductSubcategory subcategory) {
		this.subcategory = subcategory;
		categoryField.setText(subcategory.getParent().getName());
		subcategoryField.setText(subcategory.getName());
		changed = false;
		if (subcategory.getId() == null) {
			setTitle("Add Product Subcategory");
		} else {
			setTitle("Edit Product Subcategory");
		}
	}
	
	public boolean hasChanged() {
		return changed;
	}
}
