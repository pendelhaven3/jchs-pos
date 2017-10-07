package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.PaymentSalesInvoicesTable;
import com.pj.magic.gui.tables.PurchasePaymentReceivingReceiptsTable;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentReceivingReceipt;
import com.pj.magic.service.PurchasePaymentService;
import com.pj.magic.util.FormatterUtil;

@Component
public class PurchasePaymentReceivingReceiptsTableModel extends AbstractTableModel {

	private static final String[] columnNames = {"Received Date", "RR No.", "Net Amount"};
	
	@Autowired private PurchasePaymentService purchasePaymentService;
	
	private List<PurchasePaymentReceivingReceipt> paymentReceivingReceipts = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return paymentReceivingReceipts.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PurchasePaymentReceivingReceipt paymentReceivingReceipt = paymentReceivingReceipts.get(rowIndex);
		switch (columnIndex) {
		case PurchasePaymentReceivingReceiptsTable.RECEIVED_DATE_COLUMN_INDEX:
			return FormatterUtil.formatDate(paymentReceivingReceipt.getReceivingReceipt().getReceivedDate());
		case PurchasePaymentReceivingReceiptsTable.RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX:
			return paymentReceivingReceipt.getReceivingReceipt().getReceivingReceiptNumber();
		case PaymentSalesInvoicesTable.NET_AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(paymentReceivingReceipt.getReceivingReceipt()
					.getTotalNetAmountWithVat());
		default:
			return null;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case PaymentSalesInvoicesTable.NET_AMOUNT_COLUMN_INDEX:
		case PaymentSalesInvoicesTable.ADJUSTMENT_AMOUNT_COLUMN_INDEX:
		case PaymentSalesInvoicesTable.AMOUNT_DUE_COLUMN_INDEX:
			return Number.class;
		default:
			return Object.class;
		}
	}

	public void setPurchasePayment(PurchasePayment purchasePayment) {
		if (purchasePayment != null) {
			paymentReceivingReceipts = purchasePayment.getReceivingReceipts();
		} else {
			paymentReceivingReceipts.clear();
		}
		fireTableDataChanged();
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public PurchasePaymentReceivingReceipt getPaymentReceivingReceipt(int rowIndex) {
		return paymentReceivingReceipts.get(rowIndex);
	}

	public List<PurchasePaymentReceivingReceipt> getPaymentReceivingReceipts() {
		return paymentReceivingReceipts;
	}

	public void removeItem(PurchasePaymentReceivingReceipt paymentReceivingReceipt) {
		paymentReceivingReceipts.remove(paymentReceivingReceipt);
		purchasePaymentService.delete(paymentReceivingReceipt);
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !paymentReceivingReceipts.isEmpty();
	}
	
}