package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SelectSupplierDialog;
import com.pj.magic.gui.tables.ProductSuppliersTable;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductCategory;
import com.pj.magic.model.ProductSubcategory;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.Unit;
import com.pj.magic.service.ManufacturerService;
import com.pj.magic.service.ProductCategoryService;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class MaintainProductPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(MaintainProductPanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	
	@Autowired private ProductService productService;
	@Autowired private ManufacturerService manufacturerService;
	@Autowired private ProductCategoryService categoryService;
	@Autowired private ProductSuppliersTable productSuppliersTable;
	@Autowired private SelectSupplierDialog selectSupplierDialog;
	
	private Product product;
	private MagicTextField codeField;
	private MagicTextField descriptionField;
	private MagicTextField maximumStockLevelField;
	private MagicTextField minimumStockLevelField;
	private JCheckBox activeIndicatorCheckBox;
	private JCheckBox caseUnitIndicatorCheckBox;
	private JCheckBox tieUnitIndicatorCheckBox;
	private JCheckBox cartonUnitIndicatorCheckBox;
	private JCheckBox dozenUnitIndicatorCheckBox;
	private JCheckBox piecesUnitIndicatorCheckBox;
	private JCheckBox tieActiveUnitIndicatorCheckBox;
	private JCheckBox cartonActiveUnitIndicatorCheckBox;
	private JCheckBox dozenActiveUnitIndicatorCheckBox;
	private JCheckBox piecesActiveUnitIndicatorCheckBox;
	private JLabel caseQuantityField = new JLabel();
	private JLabel tieQuantityField = new JLabel();
	private JLabel cartonQuantityField = new JLabel();
	private JLabel dozenQuantityField = new JLabel();
	private JLabel piecesQuantityField = new JLabel();
	private MagicTextField caseUnitConversionField;
	private MagicTextField tieUnitConversionField;
	private MagicTextField cartonUnitConversionField;
	private MagicTextField dozenUnitConversionField;
	private MagicTextField piecesUnitConversionField;
	private JComboBox<Manufacturer> manufacturerComboBox;
	private MagicComboBox<ProductCategory> categoryComboBox;
	private JComboBox<ProductSubcategory> subcategoryComboBox;
	private MagicTextField companyListPriceField;
	private JButton saveButton;
	private JButton copyAsNewButton;
	private JButton addSupplierButton;
	private JButton deleteButton;
	
	@Override
	protected void initializeComponents() {
		codeField = new MagicTextField();
		codeField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		
		descriptionField = new MagicTextField();
		descriptionField.setMaximumLength(50);
		descriptionField.setAllowLowerCase(true);
		
		maximumStockLevelField = new MagicTextField();
		maximumStockLevelField.setMaximumLength(4);
		maximumStockLevelField.setNumbersOnly(true);
		
		minimumStockLevelField = new MagicTextField();
		minimumStockLevelField.setMaximumLength(4);
		minimumStockLevelField.setNumbersOnly(true);

		activeIndicatorCheckBox = new JCheckBox("Yes");
		
		caseUnitIndicatorCheckBox = new JCheckBox("Case");
		tieUnitIndicatorCheckBox = new JCheckBox("Tie");
		tieUnitIndicatorCheckBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				tieActiveUnitIndicatorCheckBox.setSelected(tieUnitIndicatorCheckBox.isSelected());
			}
		});
		cartonUnitIndicatorCheckBox = new JCheckBox("Carton");
		cartonUnitIndicatorCheckBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				cartonActiveUnitIndicatorCheckBox.setSelected(cartonUnitIndicatorCheckBox.isSelected());
			}
		});
		dozenUnitIndicatorCheckBox = new JCheckBox("Dozen");
		dozenUnitIndicatorCheckBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				dozenActiveUnitIndicatorCheckBox.setSelected(dozenUnitIndicatorCheckBox.isSelected());
			}
		});
		piecesUnitIndicatorCheckBox = new JCheckBox("Pieces");
		piecesUnitIndicatorCheckBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				piecesActiveUnitIndicatorCheckBox.setSelected(piecesUnitIndicatorCheckBox.isSelected());
			}
		});
		
		tieActiveUnitIndicatorCheckBox = new JCheckBox();
		cartonActiveUnitIndicatorCheckBox = new JCheckBox();
		dozenActiveUnitIndicatorCheckBox = new JCheckBox();
		piecesActiveUnitIndicatorCheckBox = new JCheckBox();
		
		caseUnitConversionField = new MagicTextField();
		caseUnitConversionField.setMaximumLength(5);
		caseUnitConversionField.setNumbersOnly(true);
		
		tieUnitConversionField = new MagicTextField();
		tieUnitConversionField.setMaximumLength(5);
		tieUnitConversionField.setNumbersOnly(true);
		
		cartonUnitConversionField = new MagicTextField();
		cartonUnitConversionField.setMaximumLength(5);
		cartonUnitConversionField.setNumbersOnly(true);
		
		dozenUnitConversionField = new MagicTextField();
		dozenUnitConversionField.setMaximumLength(5);
		dozenUnitConversionField.setNumbersOnly(true);
		
		piecesUnitConversionField = new MagicTextField();
		piecesUnitConversionField.setMaximumLength(5);
		piecesUnitConversionField.setNumbersOnly(true);
		
		manufacturerComboBox = new JComboBox<>();
		categoryComboBox = new MagicComboBox<>();
		subcategoryComboBox = new JComboBox<>();
		
		categoryComboBox.addOnSelectListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				updateSubcategoryComboBox();
			}
		});
		
		companyListPriceField = new MagicTextField();
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveProduct();
			}
		});
		
		copyAsNewButton = new JButton("Copy As New");
		copyAsNewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				copyAsNewProduct();
			}
		});
		
		addSupplierButton = new JButton("Add Supplier");
		addSupplierButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addProductSupplier();
			}
		});
		
		productSuppliersTable.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (productSuppliersTable.getSelectedColumn() == ProductSuppliersTable.BUTTON_COLUMN_INDEX) {
					deleteProductSupplier();
				}
			}
			
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(codeField);
	}

	private void copyAsNewProduct() {
		getMagicFrame().setTitle("Add New Product");
		product.setId(null);
		product.getUnitQuantities().clear();
		caseQuantityField.setText("0");
		tieQuantityField.setText("0");
		cartonQuantityField.setText("0");
		dozenQuantityField.setText("0");
		piecesQuantityField.setText("0");
		deleteButton.setEnabled(false);
		productSuppliersTable.clearDisplay();
		addSupplierButton.setEnabled(false);
		codeField.requestFocusInWindow();
	}

	private void updateSubcategoryComboBox() {
		ProductCategory category = (ProductCategory)categoryComboBox.getSelectedItem();
		if (category != null) {
			category = categoryService.getProductCategory(category.getId());
			List<ProductSubcategory> subcategories = category.getSubcategories();
			subcategoryComboBox.setModel(
					new DefaultComboBoxModel<>(subcategories.toArray(new ProductSubcategory[subcategories.size()])));
		} else {
			subcategoryComboBox.setModel(new DefaultComboBoxModel<>(new ProductSubcategory[] {}));
		}
	}

	protected void deleteProductSupplier() {
		int confirm = showConfirmMessage("Delete?");
		if (confirm == JOptionPane.OK_OPTION) {
			int selectedRow = productSuppliersTable.getSelectedRow();
			Supplier supplier = productSuppliersTable.getSupplier(selectedRow);
			productService.deleteProductSupplier(product, supplier);
			productSuppliersTable.updateDisplay(product);
		}
	}

	protected void addProductSupplier() {
		selectSupplierDialog.searchAvailableSuppliers(product);
		selectSupplierDialog.setVisible(true);
		
		Supplier supplier = selectSupplierDialog.getSelectedSupplier();
		if (supplier != null) {
			productService.addProductSupplier(product, supplier);
			productSuppliersTable.updateDisplay(product);
		}
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(codeField);
		focusOrder.add(descriptionField);
		focusOrder.add(categoryComboBox);
		focusOrder.add(subcategoryComboBox);
		focusOrder.add(maximumStockLevelField);
		focusOrder.add(minimumStockLevelField);
		focusOrder.add(activeIndicatorCheckBox);
		focusOrder.add(manufacturerComboBox);
		focusOrder.add(caseUnitIndicatorCheckBox);
		focusOrder.add(caseUnitConversionField);
		focusOrder.add(tieUnitIndicatorCheckBox);
		focusOrder.add(tieUnitConversionField);
		focusOrder.add(cartonUnitIndicatorCheckBox);
		focusOrder.add(cartonUnitConversionField);
		focusOrder.add(dozenUnitIndicatorCheckBox);
		focusOrder.add(dozenUnitConversionField);
		focusOrder.add(piecesUnitIndicatorCheckBox);
		focusOrder.add(piecesUnitConversionField);
		focusOrder.add(companyListPriceField);
		focusOrder.add(saveButton);
	}
	
	private void saveProduct() {
		if (!validateProduct()) {
			return;
		}
		
		if (confirm(getConfirmationQuestion())) {
			product.setCode(codeField.getText());
			product.setDescription(descriptionField.getText());
			product.setMaximumStockLevel(Integer.parseInt(maximumStockLevelField.getText()));
			product.setMinimumStockLevel(Integer.parseInt(minimumStockLevelField.getText()));
			product.setActive(activeIndicatorCheckBox.isSelected());
			if (!StringUtils.isEmpty(companyListPriceField.getText())) {
				product.setCompanyListPrice(NumberUtil.toBigDecimal(companyListPriceField.getText()));
			} else {
				product.setCompanyListPrice(Constants.ZERO);
			}
			
			product.getUnits().clear();
			product.getActiveUnits().clear();
			if (caseUnitIndicatorCheckBox.isSelected()) {
				product.getUnits().add(Unit.CASE);
				product.setUnitConversion(Unit.CASE, Integer.parseInt(caseUnitConversionField.getText()));
			}
			if (tieUnitIndicatorCheckBox.isSelected()) {
				product.getUnits().add(Unit.TIE);
				product.setUnitConversion(Unit.TIE, Integer.parseInt(tieUnitConversionField.getText()));
				if (tieActiveUnitIndicatorCheckBox.isSelected()) {
					product.getActiveUnits().add(Unit.TIE);
				}
			}
			if (cartonUnitIndicatorCheckBox.isSelected()) {
				product.getUnits().add(Unit.CARTON);
				product.setUnitConversion(Unit.CARTON, Integer.parseInt(cartonUnitConversionField.getText()));
				if (cartonActiveUnitIndicatorCheckBox.isSelected()) {
					product.getActiveUnits().add(Unit.CARTON);
				}
			}
			if (dozenUnitIndicatorCheckBox.isSelected()) {
				product.getUnits().add(Unit.DOZEN);
				product.setUnitConversion(Unit.DOZEN, Integer.parseInt(dozenUnitConversionField.getText()));
				if (dozenActiveUnitIndicatorCheckBox.isSelected()) {
					product.getActiveUnits().add(Unit.DOZEN);
				}
			}
			if (piecesUnitIndicatorCheckBox.isSelected()) {
				product.getUnits().add(Unit.PIECES);
				product.setUnitConversion(Unit.PIECES, Integer.parseInt(piecesUnitConversionField.getText()));
				if (piecesActiveUnitIndicatorCheckBox.isSelected()) {
					product.getActiveUnits().add(Unit.PIECES);
				}
			}
			
			product.setCategory((ProductCategory)categoryComboBox.getSelectedItem());
			product.setSubcategory((ProductSubcategory)subcategoryComboBox.getSelectedItem());
			product.setManufacturer((Manufacturer)manufacturerComboBox.getSelectedItem());
			
			try {
				productService.save(product);
				showMessage("Saved!");
				getMagicFrame().switchToEditProductPanel(product);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
			
		}
	}

	private String getConfirmationQuestion() {
		if (product.getId() != null && !product.getCode().equals(codeField.getText())) {
			return "Product Code change detected. Continue save?";
		} else {
			return "Save?";
		}
	}

	private boolean validateProduct() {
		try {
			validateMandatoryField(codeField, "Code");
			validateProductCode();
			validateMandatoryField(descriptionField, "Description");
			validateMandatoryField(maximumStockLevelField, "Maximum Stock Level");
			validateMandatoryField(minimumStockLevelField, "Minimum Stock Level");
			validateStockLevel();
			if (caseUnitIndicatorCheckBox.isSelected()) {
				validateMandatoryField(caseUnitConversionField, "Unit Conversion (Case)");
			}
			if (tieUnitIndicatorCheckBox.isSelected()) {
				validateMandatoryField(tieUnitConversionField, "Unit Conversion (Tie)");
			}
			if (cartonUnitIndicatorCheckBox.isSelected()) {
				validateMandatoryField(cartonUnitConversionField, "Unit Conversion (Carton)");
			}
			if (dozenUnitIndicatorCheckBox.isSelected()) {
				validateMandatoryField(dozenUnitConversionField, "Unit Conversion (Dozen)");
			}
			if (piecesUnitIndicatorCheckBox.isSelected()) {
				validateMandatoryField(piecesUnitConversionField, "Unit Conversion (Pieces)");
			}
			validateCompanyListPrice();
		} catch (ValidationException e) {
			return false;
		}
		return true;
	}

	private void validateProductCode() throws ValidationException {
		String code = codeField.getText();
		Product existingProduct = productService.findProductByCode(code);
		if (existingProduct != null) {
			if (product.getId() == null || existingProduct.getId().longValue() != product.getId().longValue()) {
				showErrorMessage("Code is already in use by another product");
				codeField.requestFocusInWindow();
				throw new ValidationException();
			}
		}
	}

	private void validateCompanyListPrice() throws ValidationException {
		String companyListPrice = companyListPriceField.getText();
		if (!companyListPrice.isEmpty() && !NumberUtil.isAmount(companyListPrice)) {
			showErrorMessage("Company List Price must be a valid amount");
			companyListPriceField.requestFocusInWindow();
			throw new ValidationException();
		}
	}

	private void validateStockLevel() throws ValidationException {
		int maximumStockLevel = Integer.parseInt(maximumStockLevelField.getText());
		int minimumStockLevel = Integer.parseInt(minimumStockLevelField.getText());
		if (maximumStockLevel < minimumStockLevel) {
			showErrorMessage("Maximum stock level must be greater than or equal to minimum stock level");
			maximumStockLevelField.requestFocusInWindow();
			throw new ValidationException();
		}
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Code: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		codeField.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(codeField, c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 20), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Suppliers:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);

		c = new GridBagConstraints();
		c.weightx = 1.0; // right space filler
		c.gridx = 5;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Description: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		descriptionField.setPreferredSize(new Dimension(300, 25));
		mainPanel.add(descriptionField, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.gridwidth = 2;
		c.gridheight = 4;
		c.anchor = GridBagConstraints.NORTHWEST;
		JScrollPane scrollPane = new JScrollPane(productSuppliersTable);
		scrollPane.setPreferredSize(new Dimension(400, 110));
		mainPanel.add(scrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Category: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		categoryComboBox.setPreferredSize(new Dimension(300, 25));
		mainPanel.add(categoryComboBox, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Subcategory: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		subcategoryComboBox.setPreferredSize(new Dimension(300, 25));
		mainPanel.add(subcategoryComboBox, c);

		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createLabel(175, "Maximum Stock Level: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		maximumStockLevelField.setPreferredSize(new Dimension(50, 25));
		mainPanel.add(maximumStockLevelField, c);

		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createLabel(175, "Minimum Stock Level: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		minimumStockLevelField.setPreferredSize(new Dimension(50, 25));
		mainPanel.add(minimumStockLevelField, c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(addSupplierButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Active? "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(activeIndicatorCheckBox, c);

		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createLabel(150, "Manufacturer:"), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		manufacturerComboBox.setPreferredSize(new Dimension(300, 25));
		mainPanel.add(manufacturerComboBox, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Company List Price:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		companyListPriceField.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(companyListPriceField, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(10, 10), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.CENTER;
		mainPanel.add(new JSeparator(), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(10, 10), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Units: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 3;
		mainPanel.add(createUnitsPanel(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		saveButton.setPreferredSize(new Dimension(100, 25));
		copyAsNewButton.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(ComponentUtil.createGenericPanel(saveButton, copyAsNewButton), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.0;
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 1), c);
	}

	private JPanel createUnitsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createFiller(80, 20), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createCenterLabel(130, "Unit Conversion"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createCenterLabel(110, "Available Qty"), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createCenterLabel(60, "Active?"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 4;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createFiller(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(caseUnitIndicatorCheckBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		caseUnitConversionField.setPreferredSize(new Dimension(50, 20));
		panel.add(caseUnitConversionField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		caseQuantityField = ComponentUtil.createCenterLabel(50);
		panel.add(caseQuantityField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(tieUnitIndicatorCheckBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		tieUnitConversionField.setPreferredSize(new Dimension(50, 20));
		panel.add(tieUnitConversionField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		tieQuantityField = ComponentUtil.createCenterLabel(50);
		panel.add(tieQuantityField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		panel.add(tieActiveUnitIndicatorCheckBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(cartonUnitIndicatorCheckBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		cartonUnitConversionField.setPreferredSize(new Dimension(50, 20));
		panel.add(cartonUnitConversionField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		cartonQuantityField = ComponentUtil.createCenterLabel(50);
		panel.add(cartonQuantityField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		panel.add(cartonActiveUnitIndicatorCheckBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(dozenUnitIndicatorCheckBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		dozenUnitConversionField.setPreferredSize(new Dimension(50, 20));
		panel.add(dozenUnitConversionField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		dozenQuantityField = ComponentUtil.createCenterLabel(50);
		panel.add(dozenQuantityField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		panel.add(dozenActiveUnitIndicatorCheckBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(piecesUnitIndicatorCheckBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		piecesUnitConversionField.setPreferredSize(new Dimension(50, 20));
		panel.add(piecesUnitConversionField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		piecesQuantityField = ComponentUtil.createCenterLabel(50);
		panel.add(piecesQuantityField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		panel.add(piecesActiveUnitIndicatorCheckBox, c);
		
		currentRow++;

		c = new GridBagConstraints();
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createFiller(), c);
		
		return panel;
	}

	@Override
	protected void registerKeyBindings() {
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), NEXT_FIELD_ACTION_NAME);
		getActionMap().put(NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				focusNextField();
			}
		});
		
		manufacturerComboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), NEXT_FIELD_ACTION_NAME);
		manufacturerComboBox.getActionMap().put(NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				focusNextField();
			}
		});
		
		categoryComboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), NEXT_FIELD_ACTION_NAME);
		categoryComboBox.getActionMap().put(NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				focusNextField();
			}
		});
		
		subcategoryComboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), NEXT_FIELD_ACTION_NAME);
		subcategoryComboBox.getActionMap().put(NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				focusNextField();
			}
		});
		
		saveButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_ACTION_NAME);
		saveButton.getActionMap().put(SAVE_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveProduct();
			}
		});
	}

	public void updateDisplay(Product product) {
		updateComboBoxes();
		
		this.product = product;
		if (product.getId() == null) {
			clearDisplay();
			return;
		}
		
		this.product = product = productService.getProduct(product.getId());
		
		codeField.setText(product.getCode());
		descriptionField.setText(product.getDescription());
		maximumStockLevelField.setText(String.valueOf(product.getMaximumStockLevel()));
		minimumStockLevelField.setText(String.valueOf(product.getMinimumStockLevel()));
		activeIndicatorCheckBox.setSelected(product.isActive());
		caseUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.CASE));
		if (caseUnitIndicatorCheckBox.isSelected()) {
			caseQuantityField.setText(String.valueOf(product.getUnitQuantity(Unit.CASE)));
			caseUnitConversionField.setText(String.valueOf(product.getUnitConversion(Unit.CASE)));
		} else {
			caseQuantityField.setText("0");
			caseUnitConversionField.setText(null);
		}
		tieUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.TIE));
		tieActiveUnitIndicatorCheckBox.setSelected(product.hasActiveUnit(Unit.TIE));
		if (tieUnitIndicatorCheckBox.isSelected()) {
			tieQuantityField.setText(String.valueOf(product.getUnitQuantity(Unit.TIE)));
			tieUnitConversionField.setText(String.valueOf(product.getUnitConversion(Unit.TIE)));
		} else {
			tieQuantityField.setText("0");
			tieUnitConversionField.setText(null);
		}
		cartonUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.CARTON));
		cartonActiveUnitIndicatorCheckBox.setSelected(product.hasActiveUnit(Unit.CARTON));
		if (cartonUnitIndicatorCheckBox.isSelected()) {
			cartonQuantityField.setText(String.valueOf(product.getUnitQuantity(Unit.CARTON)));
			cartonUnitConversionField.setText(String.valueOf(product.getUnitConversion(Unit.CARTON)));
		} else {
			cartonQuantityField.setText("0");
			cartonUnitConversionField.setText(null);
		}
		dozenUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.DOZEN));
		dozenActiveUnitIndicatorCheckBox.setSelected(product.hasActiveUnit(Unit.DOZEN));
		if (dozenUnitIndicatorCheckBox.isSelected()) {
			dozenQuantityField.setText(String.valueOf(product.getUnitQuantity(Unit.DOZEN)));
			dozenUnitConversionField.setText(String.valueOf(product.getUnitConversion(Unit.DOZEN)));
		} else {
			dozenQuantityField.setText("0");
			dozenUnitConversionField.setText(null);
		}
		piecesUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.PIECES));
		piecesActiveUnitIndicatorCheckBox.setSelected(product.hasActiveUnit(Unit.PIECES));
		if (piecesUnitIndicatorCheckBox.isSelected()) {
			piecesQuantityField.setText(String.valueOf(product.getUnitQuantity(Unit.PIECES)));
			piecesUnitConversionField.setText(String.valueOf(product.getUnitConversion(Unit.PIECES)));
		} else {
			piecesQuantityField.setText("0");
			piecesUnitConversionField.setText(null);
		}
		
		if (product.getManufacturer() != null) {
			manufacturerComboBox.setSelectedItem(product.getManufacturer());
		} else {
			manufacturerComboBox.setSelectedItem(null);
		}
		
		if (product.getCategory() != null) {
			categoryComboBox.setSelectedItem(product.getCategory());
		} else {
			categoryComboBox.setSelectedItem(null);
		}
		
		if (product.getSubcategory() != null) {
			subcategoryComboBox.setSelectedItem(product.getSubcategory());
		} else {
			subcategoryComboBox.setSelectedItem(null);
		}
		
		companyListPriceField.setText(FormatterUtil.formatAmount(product.getCompanyListPrice()));
		
		productSuppliersTable.updateDisplay(product);
		addSupplierButton.setEnabled(true);
		deleteButton.setEnabled(true);
	}

	private void updateComboBoxes() {
		List<ProductCategory> categories = categoryService.getAllProductCategories();
		categoryComboBox.setModel(
				new DefaultComboBoxModel<>(categories.toArray(new ProductCategory[categories.size()])));
		
		List<Manufacturer> manufacturers = manufacturerService.getAllManufacturers();
		manufacturerComboBox.setModel(
				new DefaultComboBoxModel<>(manufacturers.toArray(new Manufacturer[manufacturers.size()])));
	}

	private void clearDisplay() {
		codeField.setText(null);
		descriptionField.setText(null);
		maximumStockLevelField.setText(null);
		minimumStockLevelField.setText(null);
		activeIndicatorCheckBox.setSelected(true);
		caseUnitIndicatorCheckBox.setSelected(false);
		caseUnitConversionField.setText(null);
		caseQuantityField.setText("0");
		tieUnitIndicatorCheckBox.setSelected(false);
		tieActiveUnitIndicatorCheckBox.setSelected(false);
		tieUnitConversionField.setText(null);
		tieQuantityField.setText("0");
		cartonUnitIndicatorCheckBox.setSelected(false);
		cartonActiveUnitIndicatorCheckBox.setSelected(false);
		cartonUnitConversionField.setText(null);
		cartonQuantityField.setText("0");
		dozenUnitIndicatorCheckBox.setSelected(false);
		dozenActiveUnitIndicatorCheckBox.setSelected(false);
		dozenUnitConversionField.setText(null);
		dozenQuantityField.setText("0");
		piecesUnitIndicatorCheckBox.setSelected(false);
		piecesActiveUnitIndicatorCheckBox.setSelected(false);
		piecesUnitConversionField.setText(null);
		piecesQuantityField.setText("0");
		manufacturerComboBox.setSelectedItem(null);
		categoryComboBox.setSelectedItem(null, false);
		subcategoryComboBox.setModel(
				new DefaultComboBoxModel<>(new ProductSubcategory[]{}));
		subcategoryComboBox.setSelectedItem(null);
		companyListPriceField.setText(null);
		addSupplierButton.setEnabled(false);
		productSuppliersTable.clearDisplay();
		deleteButton.setEnabled(false);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToProductListPanel(false);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		deleteButton = new MagicToolBarButton("trash", "Delete");
		deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteProduct();
			}
		});
		toolBar.add(deleteButton);
	}

	private void deleteProduct() {
		if (!productService.canDeleteProduct(product)) {
			showErrorMessage("Cannot delete Product that is already referenced");
			return;
		}
		
		if (confirm("Delete Product?")) {
			try {
				productService.deleteProduct(product);
				showMessage("Product deleted");
				getMagicFrame().switchToProductListPanel();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error");
			}
		}
	}

}
