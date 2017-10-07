package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JComboBox;
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
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.RequiredFieldCellEditor;
import com.pj.magic.gui.dialog.SelectDateDialog;
import com.pj.magic.gui.tables.models.PurchasePaymentCreditCardPaymentsTableModel;
import com.pj.magic.gui.tables.rowitems.PurchasePaymentCreditCardPaymentRowItem;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.service.CreditCardService;
import com.pj.magic.util.FormatterUtil;

@Component
public class PurchasePaymentCreditCardPaymentsTable extends MagicTable {
	
	public static final int AMOUNT_COLUMN_INDEX = 0;
	public static final int CREDIT_CARD_COLUMN_INDEX = 1;
	public static final int TRANSACTION_DATE_COLUMN_INDEX = 2;
	public static final int APPROVAL_CODE_COLUMN_INDEX = 3;
	private static final String CANCEL_ACTION_NAME = "cancelAddMode";
	private static final String DELETE_ITEM_ACTION_NAME = "deleteItem";
	private static final String F10_ACTION_NAME = "F10";
	private static final String F5_ACTION_NAME = "F5";

	@Autowired private PurchasePaymentCreditCardPaymentsTableModel tableModel;
	@Autowired private SelectDateDialog selectDateDialog;
	@Autowired private CreditCardService creditCardService;
	
	private PurchasePayment purchasePayment;
	private JComboBox<CreditCard> creditCardComboBox;
	
	@Autowired
	public PurchasePaymentCreditCardPaymentsTable(PurchasePaymentCreditCardPaymentsTableModel tableModel) {
		super(tableModel);
		initializeColumns();
		initializeModelListener();
		registerKeyBindings();
	}
	
	private void initializeColumns() {
		MagicTextField amountTextField = new MagicTextField();
		amountTextField.setMaximumLength(12);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setCellEditor(new AmountCellEditor(amountTextField));
		
		creditCardComboBox = new MagicComboBox<>();
		columnModel.getColumn(CREDIT_CARD_COLUMN_INDEX).setCellEditor(new DefaultCellEditor(creditCardComboBox));

		MagicTextField transactionDateField = new MagicTextField();
		transactionDateField.setMaximumLength(10);
		columnModel.getColumn(TRANSACTION_DATE_COLUMN_INDEX).setCellEditor(
				new DateCellEditor(transactionDateField, "Transaction Date"));
		
		MagicTextField approvalCodeTextField = new MagicTextField();
		approvalCodeTextField.setMaximumLength(30);
		columnModel.getColumn(APPROVAL_CODE_COLUMN_INDEX).setCellEditor(
				new RequiredFieldCellEditor(approvalCodeTextField, "Approval Code"));
	}
	
	public void addNewRow() {
		if (getRowCount() > 0 && !tableModel.getRowItem(getRowCount() - 1).isUpdating()) {
			return;
		}
		
		PurchasePaymentCreditCardPayment creditCardPayment = new PurchasePaymentCreditCardPayment();
		creditCardPayment.setParent(purchasePayment);
		tableModel.addItem(creditCardPayment);
		selectAndEditCellAt(getRowCount() - 1, 0);
	}
	
	public boolean isLastRowSelected() {
		return getSelectedRow() + 1 == tableModel.getRowCount();
	}

	public void doDeleteCurrentlySelectedItem() {
		int selectedRowIndex = getSelectedRow();
		PurchasePaymentCreditCardPayment item = getCurrentlySelectedRowItem().getCreditCardPayment();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		purchasePayment.getCreditCardPayments().remove(item);
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
		
		List<CreditCard> creditCards = creditCardService.getAllCreditCards();
		creditCardComboBox.setModel(new DefaultComboBoxModel<>(
				creditCards.toArray(new CreditCard[creditCards.size()])));
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
		return getSelectedColumn() == TRANSACTION_DATE_COLUMN_INDEX;
	}

	private void openSelectDateDialog() {
		if (!isEditing()) {
			editCellAt(getSelectedRow(), TRANSACTION_DATE_COLUMN_INDEX);
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

	public PurchasePaymentCreditCardPaymentRowItem getCurrentlySelectedRowItem() {
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
						case AMOUNT_COLUMN_INDEX:
							selectAndEditCellAt(row, CREDIT_CARD_COLUMN_INDEX);
							break;
						case CREDIT_CARD_COLUMN_INDEX:
							selectAndEditCellAt(row, TRANSACTION_DATE_COLUMN_INDEX);
							break;
						case TRANSACTION_DATE_COLUMN_INDEX:
							selectAndEditCellAt(row, APPROVAL_CODE_COLUMN_INDEX);
							break;
						case APPROVAL_CODE_COLUMN_INDEX:
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