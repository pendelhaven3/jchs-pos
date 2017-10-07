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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.ProductCategory;
import com.pj.magic.model.ProductSubcategory;
import com.pj.magic.model.search.ProductSearchCriteria;
import com.pj.magic.service.ManufacturerService;
import com.pj.magic.service.ProductCategoryService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.KeyUtil;

@Component
public class SearchProductsDialog extends MagicDialog {

	@Autowired private ManufacturerService manufacturerService;
	@Autowired private ProductCategoryService categoryService;
	
	private MagicTextField codeOrDescriptionField;
	private MagicComboBox<Manufacturer> manufacturerComboBox;
	private MagicComboBox<ProductCategory> categoryComboBox;
	private MagicComboBox<ProductSubcategory> subcategoryComboBox;
	private JButton searchButton;
	private ProductSearchCriteria searchCriteria;
	
	public SearchProductsDialog() {
		setSize(500, 210);
		setLocationRelativeTo(null);
		setTitle("Search Products");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		codeOrDescriptionField = new MagicTextField();
		codeOrDescriptionField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		
		manufacturerComboBox = new MagicComboBox<>();
		categoryComboBox = new MagicComboBox<>();
		subcategoryComboBox = new MagicComboBox<>();
		
		categoryComboBox.addOnSelectListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				updateSubcategoryComboBox();
			}
		});
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveProductCodeCriteria();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(codeOrDescriptionField);
	}

	private void updateSubcategoryComboBox() {
		ProductCategory category = (ProductCategory)categoryComboBox.getSelectedItem();
		if (category != null) {
			category = categoryService.getProductCategory(category.getId());
			List<ProductSubcategory> subcategories = category.getSubcategories();
			subcategories.add(0, null);
			subcategoryComboBox.setModel(
					new DefaultComboBoxModel<>(subcategories.toArray(new ProductSubcategory[subcategories.size()])));
		} else {
			subcategoryComboBox.setModel(new DefaultComboBoxModel<>(new ProductSubcategory[] {}));
		}
	}

	private void saveProductCodeCriteria() {
		searchCriteria = new ProductSearchCriteria();
		searchCriteria.setCodeOrDescriptionLike(codeOrDescriptionField.getText());
		searchCriteria.setManufacturer((Manufacturer)manufacturerComboBox.getSelectedItem());
		searchCriteria.setCategory((ProductCategory)categoryComboBox.getSelectedItem());
		searchCriteria.setSubcategory((ProductSubcategory)subcategoryComboBox.getSelectedItem());
		
		setVisible(false);
	}

	private void registerKeyBindings() {
		codeOrDescriptionField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				manufacturerComboBox.requestFocusInWindow();
			}
		});
		
		manufacturerComboBox.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		manufacturerComboBox.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchButton.requestFocusInWindow();
			}
		});
		
		searchButton.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		searchButton.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveProductCodeCriteria();
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
		add(ComponentUtil.createLabel(140, "Code/Description:"), c);

		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		codeOrDescriptionField.setPreferredSize(new Dimension(150, 25));
		add(codeOrDescriptionField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Manufacturer:"), c);

		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		manufacturerComboBox.setPreferredSize(new Dimension(300, 25));
		add(manufacturerComboBox, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Category:"), c);

		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		categoryComboBox.setPreferredSize(new Dimension(300, 25));
		add(categoryComboBox, c);

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
		subcategoryComboBox.setPreferredSize(new Dimension(300, 25));
		add(subcategoryComboBox, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createVerticalStrut(5), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		searchButton.setPreferredSize(new Dimension(100, 25));
		add(searchButton, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.0;
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createGlue(), c);
	}
	
	public ProductSearchCriteria getSearchCriteria() {
		ProductSearchCriteria returnCriteria = searchCriteria;
		searchCriteria = null;
		return returnCriteria;
	}
	
	public void updateDisplay() {
		searchCriteria = null;
		codeOrDescriptionField.setText(null);

		List<Manufacturer> manufacturers = manufacturerService.getAllManufacturers();
		manufacturers.add(0, null);
		manufacturerComboBox.setModel(
				new DefaultComboBoxModel<>(manufacturers.toArray(new Manufacturer[manufacturers.size()])));
		
		List<ProductCategory> categories = categoryService.getAllProductCategories();
		categories.add(0, null);
		categoryComboBox.setModel(
				new DefaultComboBoxModel<>(categories.toArray(new ProductCategory[categories.size()])));
		
		subcategoryComboBox.setModel(new DefaultComboBoxModel<>(new ProductSubcategory[] {}));
	}
	
}
