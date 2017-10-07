package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.MagicCellEditor;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.dialog.SelectUnitDialog;
import com.pj.magic.gui.tables.models.SalesReturnItemsTableModel;
import com.pj.magic.gui.tables.rowitems.SalesReturnItemRowItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.SalesReturnItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.KeyUtil;

/*
 * [PJ 7/10/2014] 
 * ItemsTable has 2 modes: edit (default) and add (allows adding blank rows after the last row).
 * 
 */

@Component
public class SalesReturnItemsTable extends MagicTable {
	
	public static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	public static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	public static final int UNIT_COLUMN_INDEX = 2;
	public static final int QUANTITY_COLUMN_INDEX = 3;
	public static final int UNIT_PRICE_COLUMN_INDEX = 4;
	public static final int AMOUNT_COLUMN_INDEX = 5;
	private static final String SHOW_SELECTION_DIALOG_ACTION_NAME = "showSelectionDialog";
	private static final String F10_ACTION_NAME = "F10";
	private static final String CANCEL_ACTION_NAME = "cancelAddMode";
	private static final String DELETE_ITEM_ACTION_NAME = "deleteItem";
	private static final String F4_ACTION_NAME = "F4";

	@Autowired private SelectProductDialog selectProductDialog;
	@Autowired private SelectUnitDialog selectUnitDialog;
	@Autowired private ProductService productService;
	@Autowired private SalesReturnItemsTableModel tableModel;
	
	private boolean addMode;
	private SalesReturn salesReturn;
	private String previousSelectProductCriteria;
	
	@Autowired
	public SalesReturnItemsTable(SalesReturnItemsTableModel tableModel) {
		super(tableModel);
	}
	
	@PostConstruct
	public void initialize() {
		initializeColumns();
		initializeModelListener();
		registerKeyBindings();
	}
	
	private void initializeColumns() {
		TableColumnModel columnModel = getColumnModel();
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(120);
		columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(70);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(70);
		columnModel.getColumn(UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(100);
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
		
		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
		cellRenderer.setHorizontalAlignment(JLabel.RIGHT);
		columnModel.getColumn(UNIT_PRICE_COLUMN_INDEX).setCellRenderer(cellRenderer);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setCellRenderer(cellRenderer);
	}
	
	public void switchToAddMode() {
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
		List<SalesReturnItem> items = salesReturn.getItems();
		tableModel.setItems(items);
		
		if (items.size() > 0) {
			changeSelection(0, 0, false, false);
		}
	}
	
	private void doDeleteCurrentlySelectedItem() {
		int selectedRowIndex = getSelectedRow();
		SalesReturnItem item = getCurrentlySelectedRowItem().getItem();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		salesReturn.getItems().remove(item);
		tableModel.removeItem(selectedRowIndex);
		
		if (tableModel.hasItems()) {
			if (selectedRowIndex == getModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}
	
	public SalesReturnItemRowItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	public void editCellAtCurrentRow(int columnIndex) {
		editCellAt(getSelectedRow(), columnIndex);
		getEditorComponent().requestFocusInWindow();
	}
	
	private boolean hasDuplicate(String unit, SalesReturnItemRowItem rowItem) {
		for (SalesReturnItem item : salesReturn.getItems()) {
			if (item.getSalesInvoiceItem().getProduct().equals(rowItem.getProduct()) 
					&& item.getSalesInvoiceItem().getUnit().equals(unit) && item != rowItem.getItem()) {
				return true;
			}
		}
		return tableModel.hasDuplicate(unit, rowItem);
	}
	
	public void setSalesReturn(SalesReturn salesReturn) {
		clearSelection();
		addMode = false;
		this.salesReturn = salesReturn;
		tableModel.setSalesReturn(salesReturn);
		previousSelectProductCriteria = null;
	}
	
	private SalesReturnItem createBlankItem() {
		SalesReturnItem item = new SalesReturnItem();
		item.setParent(salesReturn);
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
				if (!salesReturn.isNew()) {
					return;
				}
				switchToAddMode();
			}
		});
		actionMap.put(SHOW_SELECTION_DIALOG_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!salesReturn.isNew()) {
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
				if (!salesReturn.isNew()) {
					return;
				}
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
				if (!salesReturn.isNew()) {
					return;
				}
				removeCurrentlySelectedItem();
			}
		});
		
		actionMap.put(F4_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!salesReturn.isNew()) {
					return;
				}
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
		textField.setText((String)getValueAt(row - 1, column));
		getCellEditor().stopCellEditing();
	}

	public void removeCurrentlySelectedItem() {
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
		
//		selectProductDialog.searchProducts(criteria, currentlySelectedCode, salesReturn.getPricingScheme());
		selectProductDialog.searchProducts(criteria, currentlySelectedCode);
		selectProductDialog.setVisible(true);
		
		Product product = selectProductDialog.getSelectedProduct();
		if (product != null) {
			((JTextField)getEditorComponent()).setText(product.getCode());
			getCellEditor().stopCellEditing();
		}
	}
	
	protected void openSelectProductDialogUsingPreviousCriteria() {
		if (!(isAdding() && isLastRowSelected())) {
			return;
		}
		
		if (!isEditing()) {
			editCellAt(getSelectedRow(), getSelectedColumn());
		}

		openSelectProductDialog(previousSelectProductCriteria,
				(String)getValueAt(getSelectedRow() - 1, PRODUCT_CODE_COLUMN_INDEX));
	}

	public void highlightColumn(SalesReturnItem item, int column) {
		int row = salesReturn.getItems().indexOf(item);
		changeSelection(row, column, false, false);
		editCellAt(row, column);
		getEditorComponent().requestFocusInWindow();
	}
	
	public void highlight() {
		if (!salesReturn.hasItems()) {
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
							model.fireTableCellUpdated(row, UNIT_PRICE_COLUMN_INDEX);
							model.fireTableCellUpdated(row, AMOUNT_COLUMN_INDEX);
							selectAndEditCellAt(row, UNIT_COLUMN_INDEX);
							break;
						case UNIT_COLUMN_INDEX:
							model.fireTableCellUpdated(row, UNIT_PRICE_COLUMN_INDEX);
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
			} else {
				Product product = productService.findProductByCode(code);
				if (product == null) {
					showErrorMessage("No product matching code specified");
				} else if (!salesReturn.getSalesInvoice().hasProduct(product)) {
					showErrorMessage("Sales Invoice does not have specified product");
				} else {
					valid = true;
				}
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
				SalesReturnItemRowItem rowItem = getCurrentlySelectedRowItem();
				if (!rowItem.getProduct().hasUnit(unit)) {
					showErrorMessage("Product does not have unit specified");
				} else if (hasDuplicate(unit, rowItem)) {
					showErrorMessage("Duplicate item");
				} else if (!salesReturn.getSalesInvoice().hasProductAndUnit(rowItem.getProduct(), unit)) {
					showErrorMessage("Sales Invoice does not have specified product and unit");
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
				SalesReturnItemRowItem rowItem = getCurrentlySelectedRowItem();
				SalesInvoiceItem item = salesReturn.getSalesInvoice()
						.findItemByProductAndUnit(rowItem.getProduct(), rowItem.getUnit());
				if (Integer.parseInt(quantity) > item.getQuantity()) {
					showErrorMessage("Quantity cannot be more than Sales Invoice item quantity");
				} else {
					valid = true;
				}
			}
			return (valid) ? super.stopCellEditing() : false;
		}
		
	}
	
}