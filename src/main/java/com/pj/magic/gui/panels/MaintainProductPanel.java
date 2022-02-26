package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.dialog.SelectSupplierDialog;
import com.pj.magic.gui.tables.ProductSuppliersTable;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Product2;
import com.pj.magic.model.ProductCategory;
import com.pj.magic.model.ProductSubcategory;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.Unit;
import com.pj.magic.service.Product2Service;
import com.pj.magic.util.ComponentUtil;

@Component
public class MaintainProductPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(MaintainProductPanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	
	@Autowired private Product2Service product2Service;
//	@Autowired private ManufacturerService manufacturerService;
//	@Autowired private ProductCategoryService categoryService;
	@Autowired private ProductSuppliersTable productSuppliersTable;
	@Autowired private SelectSupplierDialog selectSupplierDialog;
	
	private Product2 product;
	private JTextField idField;
	private JTextField descriptionField;
	private MagicTextField maximumStockLevelField;
	private MagicTextField minimumStockLevelField;
	private JCheckBox activeIndicatorCheckBox;
	private JCheckBox caseUnitIndicatorCheckBox;
	private JCheckBox tiesUnitIndicatorCheckBox;
	private JCheckBox packUnitIndicatorCheckBox;
	private JCheckBox hdznUnitIndicatorCheckBox;
	private JCheckBox piecesUnitIndicatorCheckBox;
	private JTextField caseSkuField;
	private JTextField tiesSkuField;
	private JTextField packSkuField;
	private JTextField hdznSkuField;
	private JTextField piecesSkuField;
	private JCheckBox caseActiveUnitIndicatorCheckBox;
	private JCheckBox tiesActiveUnitIndicatorCheckBox;
	private JCheckBox packActiveUnitIndicatorCheckBox;
	private JCheckBox hdznActiveUnitIndicatorCheckBox;
	private JCheckBox piecesActiveUnitIndicatorCheckBox;
	private JTextField caseQuantityField;
	private JTextField tiesQuantityField;
	private JTextField packQuantityField;
	private JTextField hdznQuantityField;
	private JTextField piecesQuantityField;
	private JTextField caseUnitConversionField;
	private JTextField tiesUnitConversionField;
	private JTextField packUnitConversionField;
	private JTextField hdznUnitConversionField;
	private JTextField piecesUnitConversionField;
	private JComboBox<Manufacturer> manufacturerComboBox;
	private MagicComboBox<ProductCategory> categoryComboBox;
	private JComboBox<ProductSubcategory> subcategoryComboBox;
	private MagicTextField companyListPriceField;
	private JButton saveButton;
	private JButton addSupplierButton;
	
	@Override
	protected void initializeComponents() {
		idField = new JTextField();
		idField.setEditable(false);
		
		descriptionField = new JTextField();
		descriptionField.setEditable(false);
		
		maximumStockLevelField = new MagicTextField();
		maximumStockLevelField.setMaximumLength(4);
		maximumStockLevelField.setNumbersOnly(true);
		
		minimumStockLevelField = new MagicTextField();
		minimumStockLevelField.setMaximumLength(4);
		minimumStockLevelField.setNumbersOnly(true);

		activeIndicatorCheckBox = new JCheckBox("Yes");
		
		caseUnitIndicatorCheckBox = new JCheckBox("CASE");
		caseUnitIndicatorCheckBox.addItemListener(e -> caseUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.CASE)));
		
		tiesUnitIndicatorCheckBox = new JCheckBox("TIES");
		tiesUnitIndicatorCheckBox.addItemListener(e -> tiesUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.TIES)));
		
		packUnitIndicatorCheckBox = new JCheckBox("PACK");
		packUnitIndicatorCheckBox.addItemListener(e -> packUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.PACK)));
		
		hdznUnitIndicatorCheckBox = new JCheckBox("HDOZ");
		hdznUnitIndicatorCheckBox.addItemListener(e -> hdznUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.HDZN)));
		
		piecesUnitIndicatorCheckBox = new JCheckBox("PCS");
		piecesUnitIndicatorCheckBox.addItemListener(e -> piecesUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.PIECES)));
		
		caseSkuField = new JTextField();
		caseSkuField.setEditable(false);
		
		tiesSkuField = new JTextField();
		tiesSkuField.setEditable(false);
		
		packSkuField = new JTextField();
		packSkuField.setEditable(false);
		
		hdznSkuField = new JTextField();
		hdznSkuField.setEditable(false);
		
		piecesSkuField = new JTextField();
		piecesSkuField.setEditable(false);
		
		caseActiveUnitIndicatorCheckBox = new JCheckBox();
		caseActiveUnitIndicatorCheckBox.addItemListener(e -> caseActiveUnitIndicatorCheckBox.setSelected(product.hasActiveUnit(Unit.CASE)));
		
		tiesActiveUnitIndicatorCheckBox = new JCheckBox();
		tiesActiveUnitIndicatorCheckBox.addItemListener(e -> tiesActiveUnitIndicatorCheckBox.setSelected(product.hasActiveUnit(Unit.TIES)));
		
		packActiveUnitIndicatorCheckBox = new JCheckBox();
		packActiveUnitIndicatorCheckBox.addItemListener(e -> packActiveUnitIndicatorCheckBox.setSelected(product.hasActiveUnit(Unit.PACK)));
		
		hdznActiveUnitIndicatorCheckBox = new JCheckBox();
		hdznActiveUnitIndicatorCheckBox.addItemListener(e -> hdznActiveUnitIndicatorCheckBox.setSelected(product.hasActiveUnit(Unit.HDZN)));
		
		piecesActiveUnitIndicatorCheckBox = new JCheckBox();
		piecesActiveUnitIndicatorCheckBox.addItemListener(e -> piecesActiveUnitIndicatorCheckBox.setSelected(product.hasActiveUnit(Unit.PIECES)));
		
		caseUnitConversionField = new JTextField();
		caseUnitConversionField.setEditable(false);
		
		tiesUnitConversionField = new JTextField();
		tiesUnitConversionField.setEditable(false);
		
		packUnitConversionField = new JTextField();
		packUnitConversionField.setEditable(false);
		
		hdznUnitConversionField = new JTextField();
		hdznUnitConversionField.setEditable(false);
		
		piecesUnitConversionField = new JTextField();
		piecesUnitConversionField.setEditable(false);
		
		caseQuantityField = new JTextField();
		caseQuantityField.setEditable(false);
		
		tiesQuantityField = new JTextField();
		tiesQuantityField.setEditable(false);
		
		packQuantityField = new JTextField();
		packQuantityField.setEditable(false);
		
		hdznQuantityField = new JTextField();
		hdznQuantityField.setEditable(false);
		
		piecesQuantityField = new JTextField();
		piecesQuantityField.setEditable(false);
		
		manufacturerComboBox = new JComboBox<>();
		manufacturerComboBox.setEnabled(false);
		
		categoryComboBox = new MagicComboBox<>();
		categoryComboBox.setEnabled(false);
		
		subcategoryComboBox = new JComboBox<>();
		subcategoryComboBox.setEnabled(false);
		
//		categoryComboBox.addOnSelectListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				updateSubcategoryComboBox();
//			}
//		});
		
		companyListPriceField = new MagicTextField();
		companyListPriceField.setEnabled(false);
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveProduct();
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
		
		focusOnComponentWhenThisPanelIsDisplayed(idField);
	}

//	private void updateSubcategoryComboBox() {
//		ProductCategory category = (ProductCategory)categoryComboBox.getSelectedItem();
//		if (category != null) {
//			category = categoryService.getProductCategory(category.getId());
//			List<ProductSubcategory> subcategories = category.getSubcategories();
//			subcategoryComboBox.setModel(
//					new DefaultComboBoxModel<>(subcategories.toArray(new ProductSubcategory[subcategories.size()])));
//		} else {
//			subcategoryComboBox.setModel(new DefaultComboBoxModel<>(new ProductSubcategory[] {}));
//		}
//	}

	protected void deleteProductSupplier() {
		int confirm = showConfirmMessage("Delete?");
		if (confirm == JOptionPane.OK_OPTION) {
			int selectedRow = productSuppliersTable.getSelectedRow();
			Supplier supplier = productSuppliersTable.getSupplier(selectedRow);
			product2Service.deleteProductSupplier(product, supplier);
			productSuppliersTable.updateDisplay(product);
		}
	}

	protected void addProductSupplier() {
		selectSupplierDialog.searchAvailableSuppliers(product);
		selectSupplierDialog.setVisible(true);
		
		Supplier supplier = selectSupplierDialog.getSelectedSupplier();
		if (supplier != null) {
			product2Service.addProductSupplier(product, supplier);
			productSuppliersTable.updateDisplay(product);
		}
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(idField);
		focusOrder.add(descriptionField);
		focusOrder.add(categoryComboBox);
		focusOrder.add(subcategoryComboBox);
		focusOrder.add(maximumStockLevelField);
		focusOrder.add(minimumStockLevelField);
		focusOrder.add(activeIndicatorCheckBox);
		focusOrder.add(manufacturerComboBox);
		focusOrder.add(caseUnitIndicatorCheckBox);
		focusOrder.add(caseUnitConversionField);
		focusOrder.add(tiesUnitIndicatorCheckBox);
		focusOrder.add(tiesUnitConversionField);
		focusOrder.add(packUnitIndicatorCheckBox);
		focusOrder.add(packUnitConversionField);
		focusOrder.add(hdznUnitIndicatorCheckBox);
		focusOrder.add(hdznUnitConversionField);
		focusOrder.add(piecesUnitIndicatorCheckBox);
		focusOrder.add(piecesUnitConversionField);
		focusOrder.add(companyListPriceField);
		focusOrder.add(saveButton);
	}
	
	private void saveProduct() {
		if (!validateProduct()) {
			return;
		}
		
		if (confirm("Save?")) {
			product.setMaximumStockLevel(Integer.parseInt(maximumStockLevelField.getText()));
			product.setMinimumStockLevel(Integer.parseInt(minimumStockLevelField.getText()));
			product.setActive(activeIndicatorCheckBox.isSelected());
//			if (!StringUtils.isEmpty(companyListPriceField.getText())) {
//				product.setCompanyListPrice(NumberUtil.toBigDecimal(companyListPriceField.getText()));
//			} else {
//				product.setCompanyListPrice(Constants.ZERO);
//			}
			
//			product.setCategory((ProductCategory)categoryComboBox.getSelectedItem());
//			product.setSubcategory((ProductSubcategory)subcategoryComboBox.getSelectedItem());
//			product.setManufacturer((Manufacturer)manufacturerComboBox.getSelectedItem());
			
			try {
				product2Service.save(product);
				showMessage("Saved");
				getMagicFrame().switchToEditProductPanel(product);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
			
		}
	}

	private boolean validateProduct() {
		try {
			validateMandatoryField(maximumStockLevelField, "Maximum Stock Level");
			validateMandatoryField(minimumStockLevelField, "Minimum Stock Level");
			validateStockLevel();
//			validateCompanyListPrice();
		} catch (ValidationException e) {
			return false;
		}
		return true;
	}

//	private void validateCompanyListPrice() throws ValidationException {
//		String companyListPrice = companyListPriceField.getText();
//		if (!companyListPrice.isEmpty() && !NumberUtil.isAmount(companyListPrice)) {
//			showErrorMessage("Company List Price must be a valid amount");
//			companyListPriceField.requestFocusInWindow();
//			throw new ValidationException();
//		}
//	}

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
		mainPanel.add(ComponentUtil.createLabel(150, "ID: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		idField.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(idField, c);
		
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
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		saveButton.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(saveButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		c.insets.top = 10;
		mainPanel.add(new JSeparator(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 10;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		c.gridwidth = 5;
		c.weightx = 1.0;
		mainPanel.add(createUnitsPanel(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 20), c);
		
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
		panel.add(ComponentUtil.createCenterLabel(90, "Units"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createCenterLabel(150, "Code"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createCenterLabel(130, "Unit Conversion"), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createCenterLabel(110, "Available Qty"), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createCenterLabel(60, "Active?"), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(caseUnitIndicatorCheckBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.insets.bottom = 5;
		caseSkuField.setPreferredSize(new Dimension(130, 25));
		panel.add(caseSkuField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		caseUnitConversionField.setPreferredSize(new Dimension(50, 25));
		panel.add(caseUnitConversionField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		caseQuantityField.setPreferredSize(new Dimension(50, 25));
		panel.add(caseQuantityField, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		panel.add(caseActiveUnitIndicatorCheckBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(tiesUnitIndicatorCheckBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.insets.bottom = 5;
		tiesSkuField.setPreferredSize(new Dimension(130, 25));
		panel.add(tiesSkuField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		tiesUnitConversionField.setPreferredSize(new Dimension(50, 25));
		panel.add(tiesUnitConversionField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		tiesQuantityField.setPreferredSize(new Dimension(50, 25));
		panel.add(tiesQuantityField, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		panel.add(tiesActiveUnitIndicatorCheckBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(packUnitIndicatorCheckBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.insets.bottom = 5;
		packSkuField.setPreferredSize(new Dimension(130, 25));
		panel.add(packSkuField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		packUnitConversionField.setPreferredSize(new Dimension(50, 25));
		panel.add(packUnitConversionField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		packQuantityField.setPreferredSize(new Dimension(50, 25));
		panel.add(packQuantityField, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		panel.add(packActiveUnitIndicatorCheckBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(hdznUnitIndicatorCheckBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.insets.bottom = 5;
		hdznSkuField.setPreferredSize(new Dimension(130, 25));
		panel.add(hdznSkuField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		hdznUnitConversionField.setPreferredSize(new Dimension(50, 25));
		panel.add(hdznUnitConversionField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		hdznQuantityField.setPreferredSize(new Dimension(50, 25));
		panel.add(hdznQuantityField, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		panel.add(hdznActiveUnitIndicatorCheckBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(piecesUnitIndicatorCheckBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.insets.bottom = 5;
		piecesSkuField.setPreferredSize(new Dimension(130, 25));
		panel.add(piecesSkuField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		piecesUnitConversionField.setPreferredSize(new Dimension(50, 25));
		panel.add(piecesUnitConversionField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		piecesQuantityField.setPreferredSize(new Dimension(50, 25));
		panel.add(piecesQuantityField, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		panel.add(piecesActiveUnitIndicatorCheckBox, c);
		
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

	public void updateDisplay(Product2 product) {
//		updateComboBoxes();
		
		this.product = product;
		if (product.getId() == null) {
			clearDisplay();
			return;
		}
		
		this.product = product = product2Service.getProduct(product.getId());
		
		idField.setText(product.getId().toString());
		descriptionField.setText(product.getDescription());
		maximumStockLevelField.setText(String.valueOf(product.getMaximumStockLevel()));
		minimumStockLevelField.setText(String.valueOf(product.getMinimumStockLevel()));
		activeIndicatorCheckBox.setSelected(product.isActive());
		
		caseUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.CASE));
		caseActiveUnitIndicatorCheckBox.setSelected(product.hasActiveUnit(Unit.CASE));
		if (caseUnitIndicatorCheckBox.isSelected()) {
			caseQuantityField.setText(String.valueOf(product.getUnitQuantity(Unit.CASE)));
			caseUnitConversionField.setText(String.valueOf(product.getUnitConversion(Unit.CASE)));
		} else {
			caseQuantityField.setText(null);
			caseUnitConversionField.setText(null);
		}
		
		tiesUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.TIES));
		tiesActiveUnitIndicatorCheckBox.setSelected(product.hasActiveUnit(Unit.TIES));
		if (tiesUnitIndicatorCheckBox.isSelected()) {
			tiesQuantityField.setText(String.valueOf(product.getUnitQuantity(Unit.TIES)));
			tiesUnitConversionField.setText(String.valueOf(product.getUnitConversion(Unit.TIES)));
		} else {
			tiesQuantityField.setText(null);
			tiesUnitConversionField.setText(null);
		}
		packUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.PACK));
		packActiveUnitIndicatorCheckBox.setSelected(product.hasActiveUnit(Unit.PACK));
		if (packUnitIndicatorCheckBox.isSelected()) {
			packQuantityField.setText(String.valueOf(product.getUnitQuantity(Unit.PACK)));
			packUnitConversionField.setText(String.valueOf(product.getUnitConversion(Unit.PACK)));
		} else {
			packQuantityField.setText(null);
			packUnitConversionField.setText(null);
		}
		hdznUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.HDZN));
		hdznActiveUnitIndicatorCheckBox.setSelected(product.hasActiveUnit(Unit.HDZN));
		if (hdznUnitIndicatorCheckBox.isSelected()) {
			hdznQuantityField.setText(String.valueOf(product.getUnitQuantity(Unit.HDZN)));
			hdznUnitConversionField.setText(String.valueOf(product.getUnitConversion(Unit.HDZN)));
		} else {
			hdznQuantityField.setText(null);
			hdznUnitConversionField.setText(null);
		}
		piecesUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.PIECES));
		piecesActiveUnitIndicatorCheckBox.setSelected(product.hasActiveUnit(Unit.PIECES));
		if (piecesUnitIndicatorCheckBox.isSelected()) {
			piecesQuantityField.setText(String.valueOf(product.getUnitQuantity(Unit.PIECES)));
			piecesUnitConversionField.setText(String.valueOf(product.getUnitConversion(Unit.PIECES)));
		} else {
			piecesQuantityField.setText(null);
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
		
//		companyListPriceField.setText(FormatterUtil.formatAmount(product.getCompanyListPrice()));
		
		caseSkuField.setText(product.getUnitSku(Unit.CASE));
		tiesSkuField.setText(product.getUnitSku(Unit.TIES));
		packSkuField.setText(product.getUnitSku(Unit.PACK));
		hdznSkuField.setText(product.getUnitSku(Unit.HDZN));
		piecesSkuField.setText(product.getUnitSku(Unit.PIECES));
		
		productSuppliersTable.updateDisplay(product);
		addSupplierButton.setEnabled(true);
	}

//	private void updateComboBoxes() {
//		List<ProductCategory> categories = categoryService.getAllProductCategories();
//		categoryComboBox.setModel(
//				new DefaultComboBoxModel<>(categories.toArray(new ProductCategory[categories.size()])));
//		
//		List<Manufacturer> manufacturers = manufacturerService.getAllManufacturers();
//		manufacturerComboBox.setModel(
//				new DefaultComboBoxModel<>(manufacturers.toArray(new Manufacturer[manufacturers.size()])));
//	}

	private void clearDisplay() {
		idField.setText(null);
		descriptionField.setText(null);
		maximumStockLevelField.setText(null);
		minimumStockLevelField.setText(null);
		activeIndicatorCheckBox.setSelected(true);
		caseUnitIndicatorCheckBox.setSelected(false);
		caseUnitConversionField.setText(null);
		caseQuantityField.setText("0");
		tiesUnitIndicatorCheckBox.setSelected(false);
		tiesActiveUnitIndicatorCheckBox.setSelected(false);
		tiesUnitConversionField.setText(null);
		tiesQuantityField.setText("0");
		packUnitIndicatorCheckBox.setSelected(false);
		packActiveUnitIndicatorCheckBox.setSelected(false);
		packUnitConversionField.setText(null);
		packQuantityField.setText("0");
		hdznUnitIndicatorCheckBox.setSelected(false);
		hdznActiveUnitIndicatorCheckBox.setSelected(false);
		hdznUnitConversionField.setText(null);
		hdznQuantityField.setText("0");
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
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToProductListPanel(false);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

}
