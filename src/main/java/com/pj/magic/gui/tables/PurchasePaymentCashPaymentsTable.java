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
import com.pj.magic.gui.dialog.SelectDateDialog;
import com.pj.magic.gui.tables.models.PurchasePaymentCashPaymentsTableModel;
import com.pj.magic.gui.tables.rowitems.PurchasePaymentCashPaymentRowItem;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCashPayment;
import com.pj.magic.util.FormatterUtil;

@Component
public class PurchasePaymentCashPaymentsTable extends MagicTable {
	
    private static final long serialVersionUID = -1062660095045207443L;
    
    public static final int AMOUNT_COLUMN_INDEX = 0;
	public static final int PAID_DATE_COLUMN_INDEX = 1;
	private static final String CANCEL_ACTION_NAME = "cancelAddMode";
	private static final String DELETE_ITEM_ACTION_NAME = "deleteItem";
	private static final String F10_ACTION_NAME = "F10";
	private static final String F5_ACTION_NAME = "F5";

	@Autowired private PurchasePaymentCashPaymentsTableModel tableModel;
	@Autowired private SelectDateDialog selectDateDialog;
	
	private PurchasePayment purchasePayment;
	
	@Autowired
	public PurchasePaymentCashPaymentsTable(PurchasePaymentCashPaymentsTableModel tableModel) {
		super(tableModel);
		initializeColumns();
		initializeModelListener();
		registerKeyBindings();
	}
	
	private void initializeColumns() {
		MagicTextField amountTextField = new MagicTextField();
		amountTextField.setMaximumLength(12);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setCellEditor(new AmountCellEditor(amountTextField));

		MagicTextField receivedDateField = new MagicTextField();
		receivedDateField.setMaximumLength(10);
		columnModel.getColumn(PAID_DATE_COLUMN_INDEX).setCellEditor(
				new DateCellEditor(receivedDateField, "Paid Date"));
	}
	
	public void addNewRow() {
		if (getRowCount() > 0 && !tableModel.getRowItem(getRowCount() - 1).isUpdating()) {
			return;
		}
		
		PurchasePaymentCashPayment cashPayment = new PurchasePaymentCashPayment();
		cashPayment.setParent(purchasePayment);
		tableModel.addItem(cashPayment);
		selectAndEditCellAt(getRowCount() - 1, 0);
	}
	
	public boolean isLastRowSelected() {
		return getSelectedRow() + 1 == tableModel.getRowCount();
	}

	public void doDeleteCurrentlySelectedItem() {
		int selectedRowIndex = getSelectedRow();
		PurchasePaymentCashPayment item = getCurrentlySelectedRowItem().getCashPayment();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		purchasePayment.getCashPayments().remove(item);
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
				if (isPaidDateColumnSelected() && purchasePayment.isNew()) {
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
	
	private boolean isPaidDateColumnSelected() {
		return getSelectedColumn() == PAID_DATE_COLUMN_INDEX;
	}

	private void openSelectDateDialog() {
		if (!isEditing()) {
			editCellAt(getSelectedRow(), PAID_DATE_COLUMN_INDEX);
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

	public PurchasePaymentCashPaymentRowItem getCurrentlySelectedRowItem() {
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
						case AMOUNT_COLUMN_INDEX:
							selectAndEditCellAt(row, PAID_DATE_COLUMN_INDEX);
							break;
						case PAID_DATE_COLUMN_INDEX:
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