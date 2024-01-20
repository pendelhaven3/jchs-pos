package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.PaymentCashPaymentsTable;
import com.pj.magic.gui.tables.PurchasePaymentEcashPaymentsTable;
import com.pj.magic.gui.tables.rowitems.PurchasePaymentEcashPaymentRowItem;
import com.pj.magic.model.EcashReceiver;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentEcashPayment;
import com.pj.magic.model.User;
import com.pj.magic.service.PurchasePaymentService;
import com.pj.magic.util.DateUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class PurchasePaymentEcashPaymentsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Amount", "E-Cash Receiver", "Reference No", "Paid Date", "Paid By"};
	
	@Autowired
	private PurchasePaymentService purchasePaymentService;
	
	private List<PurchasePaymentEcashPaymentRowItem> rowItems = new ArrayList<>();
	private PurchasePayment payment;
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	
	@Override
	public int getRowCount() {
		return rowItems.size();
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PurchasePaymentEcashPaymentRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PurchasePaymentEcashPaymentsTable.AMOUNT_COLUMN_INDEX:
			BigDecimal amount = rowItem.getAmount();
			return (amount != null) ? FormatterUtil.formatAmount(amount) : null;
		case PurchasePaymentEcashPaymentsTable.ECASH_RECEIVER_COLUMN_INDEX:
			return rowItem.getEcashReceiver() != null ? rowItem.getEcashReceiver().getName() : null;
		case PurchasePaymentEcashPaymentsTable.REFERENCE_NUMBER_COLUMN_INDEX:
			return rowItem.getReferenceNumber();
		case PurchasePaymentEcashPaymentsTable.PAID_DATE_COLUMN_INDEX:
			Date paidDate = rowItem.getPaidDate();
			return (paidDate != null) ? FormatterUtil.formatDate(paidDate) : null;
		case PurchasePaymentEcashPaymentsTable.PAID_BY_COLUMN_INDEX:
			return rowItem.getPaidBy();
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public void setPurchasePayment(PurchasePayment payment) {
		this.payment = payment;
		rowItems.clear();
		if (payment != null) {
			for (PurchasePaymentEcashPayment ecashPayment : payment.getEcashPayments()) {
				rowItems.add(new PurchasePaymentEcashPaymentRowItem(ecashPayment));
			}
		}
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		PurchasePaymentEcashPaymentRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PurchasePaymentEcashPaymentsTable.AMOUNT_COLUMN_INDEX:
			String amount = (String)value;
			if (NumberUtil.toBigDecimal(amount).equals(rowItem.getAmount())) {
				return;
			}
			rowItem.setAmount(NumberUtil.toBigDecimal(amount));
			break;
		case PurchasePaymentEcashPaymentsTable.ECASH_RECEIVER_COLUMN_INDEX:
			rowItem.setEcashReceiver((EcashReceiver)value);
			break;
		case PurchasePaymentEcashPaymentsTable.REFERENCE_NUMBER_COLUMN_INDEX:
			rowItem.setReferenceNumber((String)value);
			break;
		case PurchasePaymentEcashPaymentsTable.PAID_DATE_COLUMN_INDEX:
			String dateString = (String)value;
			if (DateUtil.toDate(dateString).equals(rowItem.getPaidDate())) {
				return;
			}
			rowItem.setPaidDate(DateUtil.toDate(dateString));
			break;
		case PurchasePaymentEcashPaymentsTable.PAID_BY_COLUMN_INDEX:
			User receivedBy = (User)value;
			if (receivedBy == null || receivedBy.equals(rowItem.getPaidBy())) {
				return;
			}
			rowItem.setPaidBy(receivedBy);
			break;
		}
		
		if (rowItem.isValid()) {
			PurchasePaymentEcashPayment ecashPayment = rowItem.getEcashPayment();
			ecashPayment.setAmount(rowItem.getAmount());
			ecashPayment.setEcashReceiver(rowItem.getEcashReceiver());
			ecashPayment.setReferenceNumber(rowItem.getReferenceNumber());
			ecashPayment.setPaidDate(rowItem.getPaidDate());
			ecashPayment.setPaidBy(rowItem.getPaidBy());
			
			boolean newCashPayment = (ecashPayment.getId() == null);
			purchasePaymentService.save(ecashPayment);
			if (newCashPayment) {
				ecashPayment.getParent().getEcashPayments().add(ecashPayment);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (payment.isPosted()) {
			return false;
		}
		
		PurchasePaymentEcashPaymentRowItem rowItem = rowItems.get(rowIndex);
		boolean editable = true;
		switch (columnIndex) {
		case PurchasePaymentEcashPaymentsTable.PAID_BY_COLUMN_INDEX:
			editable = (rowItem.getPaidDate() != null);
		case PurchasePaymentEcashPaymentsTable.PAID_DATE_COLUMN_INDEX:
			editable = (rowItem.getAmount() != null);
		}
		return editable;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == PaymentCashPaymentsTable.AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}

	public void addItem(PurchasePaymentEcashPayment ecashPayment) {
		rowItems.add(new PurchasePaymentEcashPaymentRowItem(ecashPayment));
		fireTableDataChanged();
	}

	public void reset(int row) {
		rowItems.get(row).reset();
	}

	public PurchasePaymentEcashPaymentRowItem getRowItem(int row) {
		return rowItems.get(row);
	}

	public void removeItem(int row) {
		PurchasePaymentEcashPaymentRowItem rowItem = rowItems.remove(row);
		purchasePaymentService.delete(rowItem.getEcashPayment());
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
}