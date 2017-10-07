package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.MagicCellEditor;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.dialog.SelectUnitDialog;
import com.pj.magic.gui.tables.models.AdjustmentInItemsTableModel;
import com.pj.magic.gui.tables.rowitems.AdjustmentInItemRowItem;
import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.AdjustmentInItem;
import com.pj.magic.model.Product;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.KeyUtil;

@Component
public class AdjustmentInItemsTable extends MagicTable {
	
	public static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	public static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	public static final int UNIT_COLUMN_INDEX = 2;
	public static final int QUANTITY_COLUMN_INDEX = 3;
	public static final int COST_COLUMN_INDEX = 4;
	public static final int AMOUNT_COLUMN_INDEX = 5;
	private static final String SHOW_SELECTION_DIALOG_ACTION_NAME = "showSelectionDialog";
	private static final String CANCEL_ACTION_NAME = "cancelAddMode";
	private static final String DELETE_ITEM_ACTION_NAME = "deleteItem";
	private static final String F10_ACTION_NAME = "F10";
	private static final String F4_ACTION_NAME = "F4";

	@Autowired private SelectProductDialog selectProductDialog;
	@Autowired private SelectUnitDialog selectUnitDialog;
	@Autowired private ProductService productService;
	@Autowired private AdjustmentInItemsTableModel tableModel;
	
	private boolean addMode;
	private AdjustmentIn adjustmentIn;
	private String previousSelectProductCriteria;
	
	@Autowired
	public AdjustmentInItemsTable(AdjustmentInItemsTableModel tableModel) {
		super(tableModel);
		initializeColumns();
		initializeModelListener();
		registerKeyBindings();
	}
	
	private void initializeColumns() {
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(120);
		columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(70);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(70);
		columnModel.getColumn(COST_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setPreferredWidth(100);
		
		MagicTextField productCodeTextField = new MagicTextField();
		productCodeTextField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		productCodeTextField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent event) {
				if (KeyUtil.isAlphaNumericKeyCode(event.getKeyCode())) {
					JTextField textField = (JTextField)event.getComponent();
					if (textField.getText().length() == Constants.PRODUCT_CODE_MAXIMUM_LENGTH) {
						getCellEditor().stopCellEditing();
					};
				}
			}
		});
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setCellEditor(new ProductCodeCellEditor(productCodeTextField));
		
		MagicTextField unitTextField = new MagicTextField();
		unitTextField.setMaximumLength(Constants.UNIT_MAXIMUM_LENGTH);
		unitTextField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent event) {
				if (KeyUtil.isAlphaNumericKeyCode(event.getKeyCode())) {
					JTextField textField = (JTextField)event.getComponent();
					if (textField.getText().length() == Constants.UNIT_MAXIMUM_LENGTH) {
						getCellEditor().stopCellEditing();
					}
				}
			}
		});
		columnModel.getColumn(UNIT_COLUMN_INDEX).setCellEditor(new UnitCellEditor(unitTextField));
		
		MagicTextField quantityTextField = new MagicTextField();
		quantityTextField.setMaximumLength(Constants.QUANTITY_MAXIMUM_LENGTH);
		quantityTextField.setNumbersOnly(true);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setCellEditor(new QuantityCellEditor(quantityTextField));
	}
	
	public void switchToAddMode() {
		if (adjustmentIn.isPosted()) {
			return;
		}
		
		clearSelection();
		if (isEditing()) {
			getCellEditor().cancelCellEditing();
		}
		
		addMode = true;
		tableModel.clearAndAddItem(createBlankItem());
		changeSelection(0, 0, false, false);
		editCellAt(0, 0);
		getEditorComponent().requestFocusInWindow();
	}
	
	public void addNewRow() {
		int newRowIndex = getSelectedRow() + 1;
		tableModel.addItem(createBlankItem());
		changeSelection(newRowIndex, 0, false, false);
		editCellAt(newRowIndex, 0);
		getEditorComponent().requestFocusInWindow();
	}
	
	public boolean isQuantityFieldSelected() {
		return getSelectedColumn() == QUANTITY_COLUMN_INDEX;
	}
	
	public boolean isProductCodeFieldSelected() {
		return getSelectedColumn() == PRODUCT_CODE_COLUMN_INDEX;
	}

	public boolean isUnitFieldSelected() {
		return getSelectedColumn() == UNIT_COLUMN_INDEX;
	}
	
	public boolean isLastRowSelected() {
		return getSelectedRow() + 1 == tableModel.getRowCount();
	}

	public boolean isAdding() {
		return addMode;
	}
	
	public void switchToEditMode() {
		clearSelection();
		if (isEditing()) {
			getCellEditor().cancelCellEditing();
		}
		
		addMode = false;
		List<AdjustmentInItem> items = adjustmentIn.getItems();
//		items.addAll(tableModel.getItems());
		tableModel.setItems(items);
		
		if (items.size() > 0) {
			changeSelection(0, 0, false, false);
		}
	}
	
	public void doDeleteCurrentlySelectedItem() {
		int selectedRowIndex = getSelectedRow();
		AdjustmentInItem item = getCurrentlySelectedRowItem().getItem();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		adjustmentIn.getItems().remove(item);
		tableModel.removeItem(selectedRowIndex);
		
		if (tableModel.hasItems()) {
			if (selectedRowIndex == getModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}
	
	public AdjustmentInItemRowItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	private boolean hasDuplicate(String unit, AdjustmentInItemRowItem rowItem) {
		for (AdjustmentInItem item : adjustmentIn.getItems()) {
			if (item.getProduct().equals(rowItem.getProduct()) 
					&& item.getUnit().equals(unit) && item != rowItem.getItem()) {
				return true;
			}
		}
		return tableModel.hasDuplicate(unit, rowItem);
	}
	
	public void setAdjustmentIn(AdjustmentIn adjustmentIn) {
		clearSelection();
		addMode = false;
		this.adjustmentIn = adjustmentIn;
		tableModel.setAdjustmentIn(adjustmentIn);
		previousSelectProductCriteria = null;
	}
	
	private AdjustmentInItem createBlankItem() {
		AdjustmentInItem item = new AdjustmentInItem();
		item.setParent(adjustmentIn);
		return item;
	}
	
	protected void registerKeyBindings() {
		InputMap inputMap = getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), DELETE_ITEM_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), F4_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), SHOW_SELECTION_DIALOG_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), F10_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL_ACTION_NAME);
		
		ActionMap actionMap = getActionMap();
		actionMap.put(F10_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToAddMode();
			}
		});
		actionMap.put(SHOW_SELECTION_DIALOG_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (adjustmentIn.isPosted()) {
					return;
				}
				
				if (isProductCodeFieldSelected()) {
					if (!isEditing()) {
						editCellAt(getSelectedRow(), PRODUCT_CODE_COLUMN_INDEX);
					}
					String criteria = (String)getCellEditor().getCellEditorValue();
					openSelectProductDialog(criteria, criteria);
				} else if (isUnitFieldSelected()) {
					openSelectUnitDialog();
				}
			}
		});
		actionMap.put(CANCEL_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isEditing()) {
					getCellEditor().cancelCellEditing();
					if (getCurrentlySelectedRowItem().isUpdating()) {
						tableModel.reset(getSelectedRow());
					}
				} else if (isAdding()) {
					switchToEditMode();
				}
			}
		});
		actionMap.put(DELETE_ITEM_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeCurrentlySelectedItem();
			}
		});
		
		actionMap.put(F4_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isProductCodeFieldSelected()) {
					openSelectProductDialogUsingPreviousCriteria();
				} else if (isUnitFieldSelected() || isQuantityFieldSelected()) {
					copyValueFromPreviousRow();
				}
			}
		});
	}
	
	private void copyValueFromPreviousRow() {
		if (!(isAdding() && isLastRowSelected() && tableModel.hasNonBlankItem())) {
			return;
		}
		
		int row = getSelectedRow();
		int column = getSelectedColumn();
		
		if (!isEditing()) {
			editCellAt(row, column);
		}
		
		JTextField textField = (JTextField)((DefaultCellEditor)getCellEditor()).getComponent();
		textField.setText((String)getValueAtAsString(row - 1, column));
		getCellEditor().stopCellEditing();
	}
	
	private void openSelectProductDialogUsingPreviousCriteria() {
		if (!(isAdding() && isLastRowSelected())) {
			return;
		}
		
		if (!isEditing()) {
			editCellAt(getSelectedRow(), getSelectedColumn());
		}

		openSelectProductDialog(previousSelectProductCriteria,
				(String)getValueAt(getSelectedRow() - 1, PRODUCT_CODE_COLUMN_INDEX));
	}

	public void removeCurrentlySelectedItem() {
		if (adjustmentIn.isPosted()) {
			return;
		}
		
		if (getSelectedRow() != -1) {
			if (getCurrentlySelectedRowItem().isValid()) { // check valid row to prevent deleting the blank row
				if (confirm("Do you wish to delete the selected item?")) {
					doDeleteCurrentlySelectedItem();
				}
			}
		}
	}

	protected void openSelectUnitDialog() {
		if (!isEditing()) {
			editCellAt(getSelectedRow(), UNIT_COLUMN_INDEX);
		}
		
		selectUnitDialog.setUnits(getCurrentlySelectedRowItem().getProduct().getUnits());
		selectUnitDialog.searchUnits((String)getCellEditor().getCellEditorValue());
		selectUnitDialog.setVisible(true);
		
		String unit = selectUnitDialog.getSelectedUnit();
		if (unit != null) {
			((JTextField)getEditorComponent()).setText(unit);
			getCellEditor().stopCellEditing();
		}
	}

	private void openSelectProductDialog(String criteria, String currentlySelectedCode) {
		previousSelectProductCriteria = criteria;
		
		selectProductDialog.searchProducts(criteria, currentlySelectedCode);
		selectProductDialog.setVisible(true);
		
		Product product = selectProductDialog.getSelectedProduct();
		if (product != null) {
			((JTextField)getEditorComponent()).setText(product.getCode());
			getCellEditor().stopCellEditing();
		}
	}

	public void highlightColumn(AdjustmentInItem item, int column) {
		int row = adjustmentIn.getItems().indexOf(item);
		changeSelection(row, column, false, false);
		editCellAt(row, column);
		getEditorComponent().requestFocusInWindow();
	}
	
	public void highlight() {
		if (!adjustmentIn.hasItems()) {
			switchToAddMode();
		} else {
			changeSelection(0, 0, false, false);
			requestFocusInWindow();
		}
	}
	
	private void initializeModelListener() {
		getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				final AbstractTableModel model = (AbstractTableModel)e.getSource();
				final int row = e.getFirstRow();
				final int column = e.getColumn();
				
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						switch (column) {
						case PRODUCT_CODE_COLUMN_INDEX:
							model.fireTableCellUpdated(row, COST_COLUMN_INDEX);
							model.fireTableCellUpdated(row, AMOUNT_COLUMN_INDEX);
							selectAndEditCellAt(row, UNIT_COLUMN_INDEX);
							break;
						case UNIT_COLUMN_INDEX:
							model.fireTableCellUpdated(row, COST_COLUMN_INDEX);
							model.fireTableCellUpdated(row, AMOUNT_COLUMN_INDEX);
							selectAndEditCellAt(row, QUANTITY_COLUMN_INDEX);
							break;
						case QUANTITY_COLUMN_INDEX:
							model.fireTableCellUpdated(row, AMOUNT_COLUMN_INDEX);
							if (isAdding() && isLastRowSelected() && getCurrentlySelectedRowItem().isValid()) {
								addNewRow();
							}
							break;
						}
					}
				});
			}
		});
	}
	
	private class ProductCodeCellEditor extends MagicCellEditor {

		public ProductCodeCellEditor(JTextField textField) {
			super(textField);
		}
		
		@Override
		public boolean stopCellEditing() {
			String code = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(code)) {
				showErrorMessage("Product code must be specified");
			} else if (productService.findProductByCode(code) == null) {
				showErrorMessage("No product matching code specified");
			} else {
				valid = true;
			}
			return (valid) ? super.stopCellEditing() : false;
		}

	}
	
	private class UnitCellEditor extends MagicCellEditor {
		
		public UnitCellEditor(JTextField textField) {
			super(textField);
		}
		
		@Override
		public boolean stopCellEditing() {
			String unit = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(unit)) {
				showErrorMessage("Unit must be specified");
			} else {
				AdjustmentInItemRowItem rowItem = getCurrentlySelectedRowItem();
				if (!rowItem.getProduct().hasUnit(unit)) {
					showErrorMessage("Product does not have unit specified");
				} else if (hasDuplicate(unit, rowItem)) {
					showErrorMessage("Duplicate item");
				} else {
					valid = true;
				}
			}
			return (valid) ? super.stopCellEditing() : false;
		}
		
	}
	
	private class QuantityCellEditor extends MagicCellEditor {
		
		public QuantityCellEditor(JTextField textField) {
			super(textField);
		}
		
		@Override
		public boolean stopCellEditing() {
			String quantity = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(quantity)) {
				showErrorMessage("Quantity must be specified");
			} else if (Integer.parseInt(quantity) == 0) {
				showErrorMessage("Quantity must be greater than 0");
			} else {
				valid = true;
			}
			return (valid) ? super.stopCellEditing() : false;
		}
		
	}
	
}