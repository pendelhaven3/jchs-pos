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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.dialog.SelectSupplierDialog;
import com.pj.magic.gui.tables.ProductSuppliersTable;
import com.pj.magic.model.Product;
import com.pj.magic.model.Supplier;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;

@Component
@SuppressWarnings("serial")
public class MaintainProductPanel extends StandardMagicPanel {

    private static final Logger logger = LoggerFactory.getLogger(MaintainProductPanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	
	@Autowired private ProductService productService;
	@Autowired private ProductSuppliersTable productSuppliersTable;
	@Autowired private SelectSupplierDialog selectSupplierDialog;
	
	private Product product;
	private MagicTextField codeField;
	private MagicTextField descriptionField;
	private MagicTextField maximumStockLevelField;
	private MagicTextField minimumStockLevelField;
	private JButton saveButton;
	private JButton addSupplierButton;
	
	@Override
	protected void initializeComponents() {
		codeField = new MagicTextField();
		codeField.setEditable(false);
		
		descriptionField = new MagicTextField();
		descriptionField.setEditable(false);
		
		maximumStockLevelField = new MagicTextField();
		maximumStockLevelField.setMaximumLength(4);
		maximumStockLevelField.setNumbersOnly(true);
		
		minimumStockLevelField = new MagicTextField();
		minimumStockLevelField.setMaximumLength(4);
		minimumStockLevelField.setNumbersOnly(true);

		saveButton = new JButton("Save");
		saveButton.addActionListener(e -> saveProduct());
		
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
		
		focusOnComponentWhenThisPanelIsDisplayed(maximumStockLevelField);
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
		focusOrder.add(maximumStockLevelField);
		focusOrder.add(minimumStockLevelField);
		focusOrder.add(saveButton);
	}
	
	private void saveProduct() {
		if (!validateProduct()) {
			return;
		}
		
		if (confirm("Save?")) {
			product.setMaximumStockLevel(Integer.parseInt(maximumStockLevelField.getText()));
			product.setMinimumStockLevel(Integer.parseInt(minimumStockLevelField.getText()));
			
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

	private boolean validateProduct() {
		try {
			validateMandatoryField(maximumStockLevelField, "Maximum Stock Level");
			validateMandatoryField(minimumStockLevelField, "Minimum Stock Level");
			validateStockLevel();
		} catch (ValidationException e) {
			return false;
		}
		return true;
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
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		codeField.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(codeField, c);
		
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

//		c = new GridBagConstraints();
//		c.gridx = 3;
//		c.gridy = currentRow;
//		c.gridwidth = 2;
//		c.gridheight = 4;
//		c.anchor = GridBagConstraints.NORTHWEST;
//		JScrollPane scrollPane = new JScrollPane(productSuppliersTable);
//		scrollPane.setPreferredSize(new Dimension(400, 110));
//		mainPanel.add(scrollPane, c);
		
		currentRow++;
		
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
        c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(175, "Maximum Stock Level: "), c);
		
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		maximumStockLevelField.setPreferredSize(new Dimension(50, 25));
		mainPanel.add(maximumStockLevelField, c);

		currentRow++;
		
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
        c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(175, "Minimum Stock Level: "), c);
		
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		minimumStockLevelField.setPreferredSize(new Dimension(50, 25));
		mainPanel.add(minimumStockLevelField, c);

		currentRow++;
		
        c = new GridBagConstraints();
        c.insets.top = 10;
        c.gridx = 0;
        c.gridy = currentRow;
        c.gridwidth = 2;
        mainPanel.add(saveButton, c);
		
        currentRow++;
		
        c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 1), c);
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
		
		saveButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_ACTION_NAME);
		saveButton.getActionMap().put(SAVE_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveProduct();
			}
		});
	}

	public void updateDisplay(Product product) {
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
		
//		productSuppliersTable.updateDisplay(product);
		addSupplierButton.setEnabled(true);
	}

	private void clearDisplay() {
		codeField.setText(null);
		descriptionField.setText(null);
		maximumStockLevelField.setText(null);
		minimumStockLevelField.setText(null);
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
