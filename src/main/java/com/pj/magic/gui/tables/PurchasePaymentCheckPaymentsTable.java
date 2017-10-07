package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.AmountCellEditor;
import com.pj.magic.gui.component.DateCellEditor;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.RequiredFieldCellEditor;
import com.pj.magic.gui.dialog.SelectDateDialog;
import com.pj.magic.gui.tables.models.PurchasePaymentCheckPaymentsTableModel;
import com.pj.magic.gui.tables.rowitems.PurchasePaymentCheckPaymentRowItem;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCheckPayment;
import com.pj.magic.util.FormatterUtil;

@Component
public class PurchasePaymentCheckPaymentsTable extends MagicTable {
	
	public static final int BANK_COLUMN_INDEX = 0;
	public static final int CHECK_DATE_COLUMN_INDEX = 1;
	public static final int CHECK_NUMBER_COLUMN_INDEX = 2;
	public static final int AMOUNT_COLUMN_INDEX = 3;
	private static final String CANCEL_ACTION_NAME = "cancelAddMode";
	private static final String DELETE_ITEM_ACTION_NAME = "deleteItem";
	private static final String F10_ACTION_NAME = "F10";
	private static final String F5_ACTION_NAME = "F5";

	@Autowired private PurchasePaymentCheckPaymentsTableModel tableModel;
	@Autowired private SelectDateDialog selectDateDialog;
	
	private PurchasePayment purchasePayment;
	
	@Autowired
	public PurchasePaymentCheckPaymentsTable(PurchasePaymentCheckPaymentsTableModel tableModel) {
		super(tableModel);
		initializeColumns();
		initializeModelListener();
		registerKeyBindings();
	}
	
	private void initializeColumns() {
		MagicTextField bankTextField = new MagicTextField();
		bankTextField.setMaximumLength(30);
		columnModel.getColumn(BANK_COLUMN_INDEX).setCellEditor(
				new RequiredFieldCellEditor(bankTextField, "Bank"));

		MagicTextField checkDateField = new MagicTextField();
		checkDateField.setMaximumLength(10);
		columnModel.getColumn(CHECK_DATE_COLUMN_INDEX).setCellEditor(
				new DateCellEditor(checkDateField, "Check Date"));
		
		MagicTextField checkNumberTextField = new MagicTextField();
		checkNumberTextField.setMaximumLength(50);
		columnModel.getColumn(CHECK_NUMBER_COLUMN_INDEX).setCellEditor(
				new RequiredFieldCellEditor(checkNumberTextField, "Check Number"));
		
		MagicTextField amountTextField = new MagicTextField();
		amountTextField.setMaximumLength(12);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setCellEditor(new AmountCellEditor(amountTextField));
	}
	
	public void addNewRow() {
		if (getRowCount() > 0 && !tableModel.getRowItem(getRowCount() - 1).isUpdating()) {
			return;
		}
		
		PurchasePaymentCheckPayment checkPayment = new PurchasePaymentCheckPayment();
		checkPayment.setParent(purchasePayment);
		tableModel.addItem(checkPayment);
		selectAndEditCellAt(getRowCount() - 1, 0);
	}
	
	public boolean isLastRowSelected() {
		return getSelectedRow() + 1 == tableModel.getRowCount();
	}

	public void doDeleteCurrentlySelectedItem() {
		int selectedRowIndex = getSelectedRow();
		PurchasePaymentCheckPayment item = getCurrentlySelectedRowItem().getCheckPayment();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		purchasePayment.getCheckPayments().remove(item);
		tableModel.removeItem(selectedRowIndex);
		
		if (tableModel.hasItems()) {
			if (selectedRowIndex == getModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}
	
	public void setPurchasePayment(PurchasePayment purchasePayment) {
		clearSelection();
		this.purchasePayment = purchasePayment;
		tableModel.setPayment(purchasePayment);
	}
	
	protected void registerKeyBindings() {
		InputMap inputMap = getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), F10_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), F5_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), DELETE_ITEM_ACTION_NAME);
		
		ActionMap actionMap = getActionMap();
		actionMap.put(CANCEL_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelEditing();
			}
		});
		actionMap.put(F10_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addNewRow();
			}
		});
		actionMap.put(F5_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isCheckDateColumnSelected() && purchasePayment.isNew()) {
					openSelectDateDialog();
				}
			}
		});
		actionMap.put(DELETE_ITEM_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeCurrentlySelectedItem();
			}
		});
	}
	
	private boolean isCheckDateColumnSelected() {
		return getSelectedColumn() == CHECK_DATE_COLUMN_INDEX;
	}

	private void openSelectDateDialog() {
		if (!isEditing()) {
			editCellAt(getSelectedRow(), CHECK_DATE_COLUMN_INDEX);
		}
		
		selectDateDialog.setVisible(true);
		Date selectedDate = selectDateDialog.getSelectedDate();
		if (selectedDate != null) {
			((JTextField)getEditorComponent()).setText(FormatterUtil.formatDate(selectedDate));
			getCellEditor().stopCellEditing();
		}
	}

	private void cancelEditing() {
		if (isEditing()) {
			getCellEditor().cancelCellEditing();
			if (getCurrentlySelectedRowItem().isUpdating()) {
				tableModel.reset(getSelectedRow());
			}
		} else if (isLastRowSelected() && !getCurrentlySelectedRowItem().isUpdating()) {
			int selectedRow = getSelectedRow();
			clearSelection();
			tableModel.removeItem(selectedRow);
			if (getRowCount() > 0) {
				changeSelection(getRowCount() - 1, 0, false, false);
			}
		}
	}

	public PurchasePaymentCheckPaymentRowItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	public void removeCurrentlySelectedItem() {
		if (!purchasePayment.isNew()) {
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

	public void highlight() {
//		if (!adjustmentIn.hasItems()) {
//			switchToAddMode();
//		} else {
//			changeSelection(0, 0, false, false);
//			requestFocusInWindow();
//		}
	}
	
	private void initializeModelListener() {
		getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				final int row = e.getFirstRow();
				final int column = e.getColumn();
				
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						switch (column) {
						case BANK_COLUMN_INDEX:
							selectAndEditCellAt(row, CHECK_DATE_COLUMN_INDEX);
							break;
						case CHECK_DATE_COLUMN_INDEX:
							selectAndEditCellAt(row, CHECK_NUMBER_COLUMN_INDEX);
							break;
						case CHECK_NUMBER_COLUMN_INDEX:
							selectAndEditCellAt(row, AMOUNT_COLUMN_INDEX);
							break;
						case AMOUNT_COLUMN_INDEX:
							if (isLastRowSelected()) {
								addNewRow();
							}
							break;
						}
					}
				});
			}
		});
	}

	public void clearDisplay() {
		purchasePayment = null;
		tableModel.setPayment(purchasePayment);
	}
	
}