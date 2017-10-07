package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.models.PurchasePaymentReceivingReceiptsTableModel;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentReceivingReceipt;


@Component
public class PurchasePaymentReceivingReceiptsTable extends MagicTable {

	public static final int RECEIVED_DATE_COLUMN_INDEX = 0;
	public static final int RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX = 1;
	public static final int NET_AMOUNT_COLUMN_INDEX = 2;
	private static final String DELETE_ITEM_ACTION_NAME = "DELETE_ITEM_ACTION_NAME";
	
	@Autowired private PurchasePaymentReceivingReceiptsTableModel tableModel;
	
	private PurchasePayment purchasePayment;
	
	@Autowired
	public PurchasePaymentReceivingReceiptsTable(PurchasePaymentReceivingReceiptsTableModel tableModel) {
		super(tableModel);
		registerKeyBindings();
	}
	
	private void registerKeyBindings() {
		getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), DELETE_ITEM_ACTION_NAME);
	
		getActionMap().put(DELETE_ITEM_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeCurrentlySelectedItem();
			}
		});
	}

	public void setPurchasePayment(PurchasePayment purchasePayment) {
		this.purchasePayment = purchasePayment;
		tableModel.setPurchasePayment(purchasePayment);
	}

	public void clearDisplay() {
		tableModel.setPurchasePayment(null);
	}
	
	public void removeCurrentlySelectedItem() {
		if (!purchasePayment.isNew()) {
			return;
		}
		
		if (getSelectedRow() != -1) {
			if (confirm("Do you wish to delete the selected item?")) {
				doDeleteCurrentlySelectedItem();
			}
		}
	}

	private void doDeleteCurrentlySelectedItem() {
		int selectedRowIndex = getSelectedRow();
		PurchasePaymentReceivingReceipt paymentReceivingReceipt = tableModel.getPaymentReceivingReceipt(selectedRowIndex);
		clearSelection();
		purchasePayment.getReceivingReceipts().remove(paymentReceivingReceipt);
		tableModel.removeItem(paymentReceivingReceipt);
		
		if (tableModel.hasItems()) {
			if (selectedRowIndex == getModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}
	
}