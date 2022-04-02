package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.List;

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
import com.pj.magic.gui.dialog.SelectProductBySpecialCodeDialog;
import com.pj.magic.gui.tables.models.PurchaseOrderBySpecialCodesItemsTableModel;
import com.pj.magic.gui.tables.rowitems.PurchaseOrderItemRowItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchaseOrderItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.NumberUtil;

/*
 * PurchaseOrderItemsTable has 3 modes: New, Delivered, and Posted
 */

@Component
public class PurchaseOrderBySpecialCodesItemsTable extends MagicTable {
	
    public static final int CUSTOM_CODE_COLUMN_INDEX = 0;
    public static final int PRODUCT_CODE_COLUMN_INDEX = 1;
	public static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 2;
	public static final int UNIT_COLUMN_INDEX = 3;
	public static final int SUGGESTED_ORDER_COLUMN_INDEX = 4;
	public static final int QUANTITY_COLUMN_INDEX = 5;
	
	private static final String SHOW_SELECTION_DIALOG_ACTION_NAME = "showSelectionDialog";
	private static final String CANCEL_ACTION_NAME = "cancelAddMode";
	private static final String DELETE_ITEM_ACTION_NAME = "deleteItem";
	private static final String ADD_ITEM_ACTION_NAME = "addItem";
	private static final String F4_ACTION_NAME = "f4";

	@Autowired private SelectProductBySpecialCodeDialog selectProductDialog;
	@Autowired private ProductService productService;
	@Autowired private PurchaseOrderBySpecialCodesItemsTableModel tableModel;
	
	// dynamic columns
	private int orderedColumnIndex = 6;
	private int actualQuantityColumnIndex = 7;
	private int costColumnIndex;
	private int amountColumnIndex;
	
	private PurchaseOrder purchaseOrder;
	private String previousSelectProductCriteria;
	
	@Autowired
	public PurchaseOrderBySpecialCodesItemsTable(PurchaseOrderBySpecialCodesItemsTableModel tableModel) {
		super(tableModel);
		tableModel.setTable(this);
		initializeModelListener();
		registerKeyBindings();
	}
	
	private void initializeColumns() {
		TableColumnModel columnModel = getColumnModel();
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(80);
		columnModel.getColumn(CUSTOM_CODE_COLUMN_INDEX).setPreferredWidth(80);
		columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(30);
		columnModel.getColumn(SUGGESTED_ORDER_COLUMN_INDEX).setPreferredWidth(60);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(40);
		columnModel.getColumn(costColumnIndex).setPreferredWidth(50);
		columnModel.getColumn(amountColumnIndex).setPreferredWidth(60);
		if (purchaseOrder.isDelivered()) {
			columnModel.getColumn(orderedColumnIndex).setPreferredWidth(40);
			columnModel.getColumn(actualQuantityColumnIndex).setPreferredWidth(50);
		}
		
		MagicTextField customCodeTextField = new MagicTextField();
		columnModel.getColumn(CUSTOM_CODE_COLUMN_INDEX).setCellEditor(new CustomCodeCellEditor(customCodeTextField));
		
		MagicTextField quantityTextField = new MagicTextField();
		quantityTextField.setMaximumLength(Constants.QUANTITY_MAXIMUM_LENGTH);
		quantityTextField.setNumbersOnly(true);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setCellEditor(new QuantityCellEditor(quantityTextField));
		
		MagicTextField actualQuantityTextField = new MagicTextField();
		actualQuantityTextField.setMaximumLength(Constants.QUANTITY_MAXIMUM_LENGTH);
		actualQuantityTextField.setNumbersOnly(true);
		columnModel.getColumn(actualQuantityColumnIndex)
			.setCellEditor(new ActualQuantityCellEditor(actualQuantityTextField));
		
		columnModel.getColumn(costColumnIndex).setCellEditor(new CostCellEditor(new JTextField()));
		
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		getColumnModel().getColumn(costColumnIndex).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(amountColumnIndex).setCellRenderer(rightRenderer);
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
	
	public boolean isCustomCodeFieldSelected() {
		return getSelectedColumn() == CUSTOM_CODE_COLUMN_INDEX;
	}

	public boolean isProductCodeFieldSelected() {
		return getSelectedColumn() == PRODUCT_CODE_COLUMN_INDEX;
	}

	public boolean isLastRowSelected() {
		return getSelectedRow() + 1 == tableModel.getRowCount();
	}

	public void switchToEditMode() {
		clearSelection();
		if (isEditing()) {
			getCellEditor().cancelCellEditing();
		}
		
		addMode = false;
		List<PurchaseOrderItem> items = purchaseOrder.getItems();
//		items.addAll(tableModel.getItems());
		tableModel.setItems(items);
		
		if (items.size() > 0) {
			changeSelection(0, 0, false, false);
		}
	}
	
	public void doDeleteCurrentlySelectedRow() {
		int selectedRowIndex = getSelectedRow();
		PurchaseOrderItem item = getCurrentlySelectedRowItem().getItem();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		purchaseOrder.getItems().remove(item);
		tableModel.removeItem(selectedRowIndex);
		
		if (tableModel.hasItems()) {
			if (selectedRowIndex == getModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}
	
	public PurchaseOrderItemRowItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	private boolean hasDuplicate(String code, PurchaseOrderItemRowItem rowItem) {
		for (PurchaseOrderItem item : purchaseOrder.getItems()) {
			if (item.getCode().equals(code) && item != rowItem.getItem()) {
				return true;
			}
		}
		return tableModel.hasDuplicate(code, rowItem);
	}
	
	public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
		clearSelection();
		addMode = false;
		this.purchaseOrder = purchaseOrder;
		
		if (purchaseOrder.isDelivered()) {
			costColumnIndex = 8;
			amountColumnIndex = 9;
		} else {
			costColumnIndex = 6;
			amountColumnIndex = 7;
		}
		tableModel.setPurchaseOrder(purchaseOrder);
		initializeColumns();
		previousSelectProductCriteria = null;
	}
	
	private PurchaseOrderItem createBlankItem() {
		PurchaseOrderItem item = new PurchaseOrderItem();
		item.setParent(purchaseOrder);
		return item;
	}
	
	protected void registerKeyBindings() {
		InputMap inputMap = getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), DELETE_ITEM_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), F4_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), SHOW_SELECTION_DIALOG_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), ADD_ITEM_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL_ACTION_NAME);
		
		ActionMap actionMap = getActionMap();
		actionMap.put(SHOW_SELECTION_DIALOG_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showSelectionDialog();
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
				removeCurrentlySelectedRow();
			}
		});
		actionMap.put(ADD_ITEM_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToAddMode();
			}
		});
		actionMap.put(F4_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (getSelectedColumn()) {
				case PRODUCT_CODE_COLUMN_INDEX:
					openSelectProductDialogUsingPreviousCriteria();
					break;
				case UNIT_COLUMN_INDEX:
				case QUANTITY_COLUMN_INDEX:
					copyValueFromPreviousRow();
					break;
				default:
					if (getSelectedColumn() == getCostColumnIndex()) {
						copyValueFromPreviousRow();
					} else if (getSelectedColumn() == getActualQuantityColumnIndex()
							&& purchaseOrder.isDelivered()) {
						copyValueFromPreviousRow();
					}
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
				(String)getValueAt(getSelectedRow() - 1, CUSTOM_CODE_COLUMN_INDEX));
	}
	
	private void openSelectProductDialog(String criteria, String currentlySelectedCode) {
		previousSelectProductCriteria = criteria;
		
		selectProductDialog.searchProducts(criteria, currentlySelectedCode, purchaseOrder.getSupplier());
		selectProductDialog.setVisible(true);
		
		Product product = selectProductDialog.getSelectedProduct();
		if (product != null) {
			((JTextField)getEditorComponent()).setText(product.getCode());
			getCellEditor().stopCellEditing();
		}
	}

	public void removeCurrentlySelectedRow() {
		if (tableModel.hasItems()) {
			if (tableModel.isValid(getSelectedRow())) { // check valid row to prevent deleting the blank row
				if (confirm("Do you wish to delete the selected item?")) {
					doDeleteCurrentlySelectedRow();
				}
			}
		}
	}

	protected void showSelectionDialog() {
		if (isCustomCodeFieldSelected()) {
			if (!isEditing() && tableModel.isCellEditable(getSelectedRow(), CUSTOM_CODE_COLUMN_INDEX)) {
				editCellAt(getSelectedRow(), CUSTOM_CODE_COLUMN_INDEX);
			}
			String criteria = (String)getCellEditor().getCellEditorValue();
			openSelectProductDialog(criteria, criteria);
		}
	}

	public void highlightColumn(PurchaseOrderItem item, int column) {
		int row = purchaseOrder.getItems().indexOf(item);
		changeSelection(row, column, false, false);
		editCellAt(row, column);
		getEditorComponent().requestFocusInWindow();
	}
	
	public void highlight() {
		if (!purchaseOrder.hasItems()) {
			switchToAddMode();
		} else {
			if (purchaseOrder.isDelivered()) {
				changeSelection(0, actualQuantityColumnIndex, false, false);
				editCellAtCurrentRow(actualQuantityColumnIndex);
			} else {
				changeSelection(0, 0, false, false);
				requestFocusInWindow();
			}
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
						case CUSTOM_CODE_COLUMN_INDEX:
							// Fire entire row. Firing single cells causes this listener to be re-triggered.
							model.fireTableRowsUpdated(row, row);
							int nextField = QUANTITY_COLUMN_INDEX;
							if (purchaseOrder.isDelivered()) {
								nextField = actualQuantityColumnIndex;
							}
							selectAndEditCellAt(row, nextField);
							break;
						case QUANTITY_COLUMN_INDEX:
							model.fireTableRowsUpdated(row, row);
							selectAndEditCellAt(row, costColumnIndex);
							break;
						default:
							if (column == costColumnIndex) {
								model.fireTableRowsUpdated(row, row);
								if (isAdding()) {
									if (isLastRowSelected()) {
										if (getCurrentlySelectedRowItem().isValid()) {
											addNewRow();
										}
									} else {
										selectAndEditCellAt(row + 1, CUSTOM_CODE_COLUMN_INDEX);
									}
								} else {
									if (purchaseOrder.isDelivered()) {
										if (!isLastRowSelected()) {
											selectAndEditCellAt(row + 1, actualQuantityColumnIndex);
										}
									}
								}
							} else if (column == actualQuantityColumnIndex) {
								model.fireTableRowsUpdated(row, row);
								selectAndEditCellAt(row, costColumnIndex);
							}
						}
					}
				});
			}
		});
	}
	
	public int getCostColumnIndex() {
		return costColumnIndex;
	}
	
	public int getAmountColumnIndex() {
		return amountColumnIndex;
	}
	
	public int getOrderedColumnIndex() {
		return orderedColumnIndex;
	}
	
	public int getActualQuantityColumnIndex() {
		return actualQuantityColumnIndex;
	}

	private class CustomCodeCellEditor extends MagicCellEditor {

		public CustomCodeCellEditor(JTextField textField) {
			super(textField);
		}
		
		@Override
		public boolean stopCellEditing() {
			String code = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(code)) {
				showErrorMessage("Custom code must be specified");
			} else if (productService.findProductByCode(code) == null) {
				showErrorMessage("No product matching custom code");
			} else {
				PurchaseOrderItemRowItem rowItem = getCurrentlySelectedRowItem();
				if (hasDuplicate(code, rowItem)) {
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
	
	private class CostCellEditor extends MagicCellEditor {
		
		public CostCellEditor(JTextField textField) {
			super(textField);
		}
		
		@Override
		public boolean stopCellEditing() {
			String cost = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(cost)) {
				showErrorMessage("Cost must be specified");
			} else if (!NumberUtil.isAmount(cost)){
				showErrorMessage("Cost must be a valid amount");
			} else if (NumberUtil.toBigDecimal(cost).equals(BigDecimal.ZERO.setScale(2))){
				showErrorMessage("Cost must not be 0");
			} else {
				valid = true;
			}
			return (valid) ? super.stopCellEditing() : false;
		}
		
	}
	
	private class ActualQuantityCellEditor extends MagicCellEditor {
		
		public ActualQuantityCellEditor(JTextField textField) {
			super(textField);
		}
		
		@Override
		public boolean stopCellEditing() {
			String quantity = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(quantity)) {
				showErrorMessage("Actual Quantity must be specified");
			} else {
				valid = true;
			}
			return (valid) ? super.stopCellEditing() : false;
		}
		
	}
	
}