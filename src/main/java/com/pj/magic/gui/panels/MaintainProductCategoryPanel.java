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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.dialog.MaintainProductSubcategoryDialog;
import com.pj.magic.gui.tables.ProductSubcategoriesTable;
import com.pj.magic.model.ProductCategory;
import com.pj.magic.model.ProductSubcategory;
import com.pj.magic.service.ProductCategoryService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.KeyUtil;

@Component
public class MaintainProductCategoryPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(MaintainProductCategoryPanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	private static final String EDIT_SUBCATEGORY_ACTION_NAME = "editSubcategory";
	
	@Autowired private ProductCategoryService productCategoryService;
	@Autowired private ProductSubcategoriesTable subcategoriesTable;
	@Autowired private MaintainProductSubcategoryDialog maintainProductSubcategoryDialog;
	
	private ProductCategory category;
	private MagicTextField nameField;
	private JButton saveButton;
	private JButton addSubcategoryButton;
	
	@Override
	protected void initializeComponents() {
		nameField = new MagicTextField();
		nameField.setMaximumLength(50);
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveProductCategory();
			}
		});
		
		addSubcategoryButton = new JButton("Add Subcategory");
		addSubcategoryButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addSubcategory();
			}
		});
		
		subcategoriesTable.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (subcategoriesTable.getSelectedColumn() == ProductSubcategoriesTable.BUTTON_COLUMN_INDEX) {
					deleteProductSubcategory();
				}
			}
			
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(nameField);
	}

	protected void deleteProductSubcategory() {
		int confirm = showConfirmMessage("Delete?");
		if (confirm == JOptionPane.OK_OPTION) {
			int selectedRow = subcategoriesTable.getSelectedRow();
			ProductSubcategory subcategory = subcategoriesTable.getSubcategory(selectedRow);
			productCategoryService.delete(subcategory);
			category.getSubcategories().remove(subcategory);
			subcategoriesTable.updateDisplay(category);
		}
	}

	private void addSubcategory() {
		ProductSubcategory subcategory = new ProductSubcategory();
		subcategory.setParent(category);
		openMaintainSubcategoryDialog(subcategory);
	}
	
	private void openMaintainSubcategoryDialog(ProductSubcategory subcategory) {
		maintainProductSubcategoryDialog.updateDisplay(subcategory);
		maintainProductSubcategoryDialog.setVisible(true);
		
		if (maintainProductSubcategoryDialog.hasChanged()) {
			category = productCategoryService.getProductCategory(category.getId());
			subcategoriesTable.updateDisplay(category);
		}
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(nameField);
		focusOrder.add(saveButton);
	}
	
	protected void saveProductCategory() {
		if (!validateProductCategory()) {
			return;
		}
		
		int confirm = showConfirmMessage("Save?");
		if (confirm == JOptionPane.OK_OPTION) {
			category.setName(nameField.getText());
			
			try {
				productCategoryService.save(category);
				showMessage("Saved!");
				updateDisplay(category);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private boolean validateProductCategory() {
		try {
			validateMandatoryField(nameField, "Name");
		} catch (ValidationException e) {
			return false;
		}
		return true;
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 30), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(60, "Name: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		nameField.setPreferredSize(new Dimension(300, 25));
		mainPanel.add(nameField, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0; // right space filler
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		saveButton.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(saveButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 30), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Subcategories: "), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.WEST;
		JScrollPane scrollPane = new JScrollPane(subcategoriesTable);
		scrollPane.setPreferredSize(new Dimension(600, 200));
		mainPanel.add(scrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createVerticalFiller(15), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(addSubcategoryButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
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
				saveProductCategory();
			}
		});
		
		subcategoriesTable.getInputMap().put(KeyUtil.getEnterKey(), EDIT_SUBCATEGORY_ACTION_NAME);
		subcategoriesTable.getActionMap().put(EDIT_SUBCATEGORY_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				editSubcategory();
			}
		});
		
		subcategoriesTable.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				editSubcategory();
			}
		});
	}

	protected void editSubcategory() {
		ProductSubcategory subcategory = subcategoriesTable.getCurrentlySelectedSubcategory();
		openMaintainSubcategoryDialog(subcategory);
	}

	public void updateDisplay(ProductCategory category) {
		this.category = category;
		if (category.getId() == null) {
			clearDisplay();
			return;
		}
		
		this.category = category = productCategoryService.getProductCategory(category.getId());
		
		nameField.setText(category.getName());
		subcategoriesTable.updateDisplay(category);
		addSubcategoryButton.setEnabled(true);
	}

	private void clearDisplay() {
		nameField.setText(null);
		subcategoriesTable.clearDisplay();
		addSubcategoryButton.setEnabled(false);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToProductCategoryListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}
