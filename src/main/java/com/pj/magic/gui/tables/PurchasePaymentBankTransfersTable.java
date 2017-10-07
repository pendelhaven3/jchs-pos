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
import com.pj.magic.gui.tables.models.PurchasePaymentBankTransfersTableModel;
import com.pj.magic.gui.tables.rowitems.PurchasePaymentBankTransferRowItem;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentBankTransfer;
import com.pj.magic.util.FormatterUtil;

@Component
public class PurchasePaymentBankTransfersTable extends MagicTable {
	
	public static final int BANK_COLUMN_INDEX = 0;
	public static final int AMOUNT_COLUMN_INDEX = 1;
	public static final int REFERENCE_NUMBER_COLUMN_INDEX = 2;
	public static final int TRANSFER_DATE_COLUMN_INDEX = 3;
	private static final String CANCEL_ACTION_NAME = "cancelAddMode";
	private static final String DELETE_ITEM_ACTION_NAME = "deleteItem";
	private static final String F10_ACTION_NAME = "F10";
	private static final String F5_ACTION_NAME = "F5";

	@Autowired private PurchasePaymentBankTransfersTableModel tableModel;
	@Autowired private SelectDateDialog selectDateDialog;
	
	private PurchasePayment purchasePayment;
	
	@Autowired
	public PurchasePaymentBankTransfersTable(PurchasePaymentBankTransfersTableModel tableModel) {
		super(tableModel);
		initializeColumns();
		initializeModelListener();
		registerKeyBindings();
	}
	
	private void initializeColumns() {
		MagicTextField bankTextField = new MagicTextField();
		columnModel.getColumn(BANK_COLUMN_INDEX).setCellEditor(
				new RequiredFieldCellEditor(bankTextField, "Bank"));

		MagicTextField amountTextField = new MagicTextField();
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setCellEditor(new AmountCellEditor(amountTextField));

		MagicTextField referenceNumberField = new MagicTextField();
		columnModel.getColumn(REFERENCE_NUMBER_COLUMN_INDEX).setCellEditor(
				new RequiredFieldCellEditor(referenceNumberField, "Reference Number"));

		MagicTextField receivedDateField = new MagicTextField();
		columnModel.getColumn(TRANSFER_DATE_COLUMN_INDEX).setCellEditor(
				new DateCellEditor(receivedDateField, "Transfer Date"));
	}
	
	public void addNewRow() {
		if (getRowCount() > 0 && !tableModel.getRowItem(getRowCount() - 1).isUpdating()) {
			return;
		}
		
		PurchasePaymentBankTransfer bankTransfer = new PurchasePaymentBankTransfer();
		bankTransfer.setParent(purchasePayment);
		tableModel.addItem(bankTransfer);
		selectAndEditCellAt(getRowCount() - 1, 0);
	}
	
	public boolean isLastRowSelected() {
		return getSelectedRow() + 1 == tableModel.getRowCount();
	}

	public void doDeleteCurrentlySelectedItem() {
		int selectedRowIndex = getSelectedRow();
		PurchasePaymentBankTransfer item = getCurrentlySelectedRowItem().getBankTransfer();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		purchasePayment.getBankTransfers().remove(item);
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
		tableModel.setPurchasePayment(purchasePayment);
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
				if (isTransferDateColumnSelected() && purchasePayment.isNew()) {
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
	
	private boolean isTransferDateColumnSelected() {
		return getSelectedColumn() == TRANSFER_DATE_COLUMN_INDEX;
	}

	private void openSelectDateDialog() {
		if (!isEditing()) {
			editCellAt(getSelectedRow(), TRANSFER_DATE_COLUMN_INDEX);
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

	public PurchasePaymentBankTransferRowItem getCurrentlySelectedRowItem() {
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
							selectAndEditCellAt(row, AMOUNT_COLUMN_INDEX);
							break;
						case AMOUNT_COLUMN_INDEX:
							selectAndEditCellAt(row, REFERENCE_NUMBER_COLUMN_INDEX);
							break;
						case REFERENCE_NUMBER_COLUMN_INDEX:
							selectAndEditCellAt(row, TRANSFER_DATE_COLUMN_INDEX);
							break;
						case TRANSFER_DATE_COLUMN_INDEX:
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
		tableModel.setPurchasePayment(purchasePayment);
	}
	
}