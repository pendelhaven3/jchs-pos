package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.PaymentCashPaymentsTable;
import com.pj.magic.gui.tables.rowitems.PaymentCashPaymentRowItem;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentCashPayment;
import com.pj.magic.model.User;
import com.pj.magic.service.PaymentService;
import com.pj.magic.util.DateUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class PaymentCashPaymentsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Amount", "Received Date", "Received By"};
	
	@Autowired private PaymentService paymentService;
	
	private List<PaymentCashPaymentRowItem> rowItems = new ArrayList<>();
	private Payment payment;
	
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
		PaymentCashPaymentRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PaymentCashPaymentsTable.AMOUNT_COLUMN_INDEX:
			BigDecimal amount = rowItem.getAmount();
			return (amount != null) ? FormatterUtil.formatAmount(amount) : null;
		case PaymentCashPaymentsTable.RECEIVED_DATE_COLUMN_INDEX:
			Date receivedDate = rowItem.getReceivedDate();
			return (receivedDate != null) ? FormatterUtil.formatDate(receivedDate) : null;
		case PaymentCashPaymentsTable.RECEIVED_BY_COLUMN_INDEX:
			return rowItem.getReceivedBy();
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
		rowItems.clear();
		if (payment != null) {
			for (PaymentCashPayment cashPayment : payment.getCashPayments()) {
				rowItems.add(new PaymentCashPaymentRowItem(cashPayment));
			}
		}
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		PaymentCashPaymentRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PaymentCashPaymentsTable.AMOUNT_COLUMN_INDEX:
			String amount = (String)value;
			if (NumberUtil.toBigDecimal(amount).equals(rowItem.getAmount())) {
				return;
			}
			rowItem.setAmount(NumberUtil.toBigDecimal(amount));
			break;
		case PaymentCashPaymentsTable.RECEIVED_DATE_COLUMN_INDEX:
			String dateString = (String)value;
			if (DateUtil.toDate(dateString).equals(rowItem.getReceivedDate())) {
				return;
			}
			rowItem.setReceivedDate(DateUtil.toDate(dateString));
			break;
		case PaymentCashPaymentsTable.RECEIVED_BY_COLUMN_INDEX:
			User receivedBy = (User)value;
			if (receivedBy == null || receivedBy.equals(rowItem.getReceivedBy())) {
				return;
			}
			rowItem.setReceivedBy(receivedBy);
			break;
		}
		
		if (rowItem.isValid()) {
			PaymentCashPayment cashPayment = rowItem.getCashPayment();
			cashPayment.setAmount(rowItem.getAmount());
			cashPayment.setReceivedDate(rowItem.getReceivedDate());
			cashPayment.setReceivedBy(rowItem.getReceivedBy());
			
			boolean newCashPayment = (cashPayment.getId() == null);
			paymentService.save(cashPayment);
			if (newCashPayment) {
				cashPayment.getParent().getCashPayments().add(cashPayment);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (payment.isPosted()) {
			return false;
		}
		
		PaymentCashPaymentRowItem rowItem = rowItems.get(rowIndex);
		boolean editable = true;
		switch (columnIndex) {
		case PaymentCashPaymentsTable.RECEIVED_BY_COLUMN_INDEX:
			editable = (rowItem.getReceivedDate() != null);
		case PaymentCashPaymentsTable.RECEIVED_DATE_COLUMN_INDEX:
			editable = (rowItem.getAmount() != null);
		case PaymentCashPaymentsTable.AMOUNT_COLUMN_INDEX:
			break;
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

	public void addItem(PaymentCashPayment cashPayment) {
		rowItems.add(new PaymentCashPaymentRowItem(cashPayment));
		fireTableDataChanged();
	}

	public void reset(int row) {
		rowItems.get(row).reset();
	}

	public PaymentCashPaymentRowItem getRowItem(int row) {
		return rowItems.get(row);
	}

	public void removeItem(int row) {
		PaymentCashPaymentRowItem rowItem = rowItems.remove(row);
		paymentService.delete(rowItem.getCashPayment());
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
}