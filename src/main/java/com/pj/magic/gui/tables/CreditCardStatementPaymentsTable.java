package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.AmountCellEditor;
import com.pj.magic.gui.component.DateCellEditor;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.dialog.SelectDateDialog;
import com.pj.magic.gui.tables.models.CreditCardStatementPaymentsTableModel;
import com.pj.magic.gui.tables.rowitems.CreditCardStatementPaymentRowItem;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.model.CreditCardStatementPayment;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.ListUtil;

@Component
public class CreditCardStatementPaymentsTable extends MagicTable {
	
	public static final int PAYMENT_DATE_COLUMN_INDEX = 0;
	public static final int AMOUNT_COLUMN_INDEX = 1;
	public static final int PAYMENT_TYPE_COLUMN_INDEX = 2;
	public static final int REMARKS_COLUMN_INDEX = 3;
	
	@Autowired private CreditCardStatementPaymentsTableModel tableModel;
	@Autowired private SelectDateDialog selectDateDialog;
	
	private CreditCardStatement statement;
	private JComboBox<String> paymentTypeComboBox;
	
	@Autowired
	public CreditCardStatementPaymentsTable(CreditCardStatementPaymentsTableModel tableModel) {
		super(tableModel);
		initializeColumns();
		initializeModelListener();
		registerKeyBindings();
	}
	
	private void initializeColumns() {
		MagicTextField paymentDateField = new MagicTextField();
		paymentDateField.setMaximumLength(10);
		columnModel.getColumn(PAYMENT_DATE_COLUMN_INDEX).setCellEditor(
				new DateCellEditor(paymentDateField, "Payment Date"));
		
		MagicTextField amountTextField = new MagicTextField();
		amountTextField.setMaximumLength(12);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setCellEditor(new AmountCellEditor(amountTextField));
		
		paymentTypeComboBox = new JComboBox<>();
		paymentTypeComboBox.setModel(ListUtil.toDefaultComboBoxModel(
				Arrays.asList("CASH", "CHECK", "ONLINE", "REBATE", "PREVIOUS BALANCE"), true));
		columnModel.getColumn(PAYMENT_TYPE_COLUMN_INDEX).setCellEditor(
				new DefaultCellEditor(paymentTypeComboBox));
		
		MagicTextField remarksField = new MagicTextField();
		remarksField.setMaximumLength(100);
		columnModel.getColumn(REMARKS_COLUMN_INDEX).setCellEditor(
				new DefaultCellEditor(remarksField));
		
	}
	
	public void addNewRow() {
		if (getRowCount() > 0 && !tableModel.getRowItem(getRowCount() - 1).isUpdating()) {
			return;
		}
		
		CreditCardStatementPayment payment = new CreditCardStatementPayment();
		payment.setParent(statement);
		tableModel.addItem(payment);
		selectAndEditCellAt(getRowCount() - 1, 0);
	}
	
	public boolean isLastRowSelected() {
		return getSelectedRow() + 1 == tableModel.getRowCount();
	}

	public void doDeleteCurrentlySelectedItem() {
		int selectedRowIndex = getSelectedRow();
		CreditCardStatementPayment item = getCurrentlySelectedRowItem().getPayment();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		statement.getPayments().remove(item);
		tableModel.removeItem(selectedRowIndex);
		
		if (tableModel.hasItems()) {
			if (selectedRowIndex == getModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}
	
	public void setStatement(CreditCardStatement statement) {
		if (isEditing()) {
			cancelEditing();
		}
		
		clearSelection();
		this.statement = statement;
		tableModel.setStatement(statement);
	}
	
	protected void registerKeyBindings() {
		onF5Key(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isPaymentDateColumnSelected() && !statement.isPosted()) {
					openSelectDateDialog();
				}
			}
		});
		
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelEditing();
			}
		});
		
		onDeleteKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeCurrentlySelectedItem();
			}
		});
	}
	
	private boolean isPaymentDateColumnSelected() {
		return getSelectedColumn() == PAYMENT_DATE_COLUMN_INDEX;
	}

	private void openSelectDateDialog() {
		if (!isEditing()) {
			editCellAt(getSelectedRow(), PAYMENT_DATE_COLUMN_INDEX);
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

	public CreditCardStatementPaymentRowItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	public void removeCurrentlySelectedItem() {
		if (statement.isPosted()) {
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
						case PAYMENT_DATE_COLUMN_INDEX:
							selectAndEditCellAt(row, AMOUNT_COLUMN_INDEX);
							break;
						case AMOUNT_COLUMN_INDEX:
							selectAndEditCellAt(row, PAYMENT_TYPE_COLUMN_INDEX);
							break;
						case PAYMENT_TYPE_COLUMN_INDEX:
							selectAndEditCellAt(row, REMARKS_COLUMN_INDEX);
							break;
						case REMARKS_COLUMN_INDEX:
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
		statement = null;
		tableModel.setStatement(statement);
	}
	
}