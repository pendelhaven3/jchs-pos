package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.PurchasePaymentCashPaymentsTable;
import com.pj.magic.gui.tables.rowitems.PurchasePaymentCashPaymentRowItem;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCashPayment;
import com.pj.magic.model.User;
import com.pj.magic.service.PurchasePaymentService;
import com.pj.magic.util.DateUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class PurchasePaymentCashPaymentsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Amount", "Paid Date", "Paid By"};
	
	@Autowired private PurchasePaymentService purchasePaymentService;
	
	private List<PurchasePaymentCashPaymentRowItem> rowItems = new ArrayList<>();
	private PurchasePayment purchasePayment;
	
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
		PurchasePaymentCashPaymentRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PurchasePaymentCashPaymentsTable.AMOUNT_COLUMN_INDEX:
			BigDecimal amount = rowItem.getAmount();
			return (amount != null) ? FormatterUtil.formatAmount(amount) : null;
		case PurchasePaymentCashPaymentsTable.PAID_DATE_COLUMN_INDEX:
			Date paidDate = rowItem.getPaidDate();
			return (paidDate != null) ? FormatterUtil.formatDate(paidDate) : null;
		case PurchasePaymentCashPaymentsTable.PAID_BY_COLUMN_INDEX:
			return rowItem.getPaidBy();
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public void setPurchasePayment(PurchasePayment purchasePayment) {
		this.purchasePayment = purchasePayment;
		rowItems.clear();
		if (purchasePayment != null) {
			for (PurchasePaymentCashPayment cashPayment : purchasePayment.getCashPayments()) {
				rowItems.add(new PurchasePaymentCashPaymentRowItem(cashPayment));
			}
		}
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		PurchasePaymentCashPaymentRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PurchasePaymentCashPaymentsTable.AMOUNT_COLUMN_INDEX:
			String amount = (String)value;
			if (NumberUtil.toBigDecimal(amount).equals(rowItem.getAmount())) {
				return;
			}
			rowItem.setAmount(NumberUtil.toBigDecimal(amount));
			break;
		case PurchasePaymentCashPaymentsTable.PAID_DATE_COLUMN_INDEX:
			String dateString = (String)value;
			if (DateUtil.toDate(dateString).equals(rowItem.getPaidDate())) {
				return;
			}
			rowItem.setPaidDate(DateUtil.toDate(dateString));
			break;
		case PurchasePaymentCashPaymentsTable.PAID_BY_COLUMN_INDEX:
			User paidBy = (User)value;
			if (paidBy == null || paidBy.equals(rowItem.getPaidBy())) {
				return;
			}
			rowItem.setPaidBy(paidBy);
			break;
		}
		
		if (rowItem.isValid()) {
			PurchasePaymentCashPayment cashPayment = rowItem.getCashPayment();
			cashPayment.setAmount(rowItem.getAmount());
			cashPayment.setPaidDate(rowItem.getPaidDate());
			cashPayment.setPaidBy(rowItem.getPaidBy());
			
			boolean newCashPayment = (cashPayment.getId() == null);
			purchasePaymentService.save(cashPayment);
			if (newCashPayment) {
				cashPayment.getParent().getCashPayments().add(cashPayment);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (purchasePayment.isPosted()) {
			return false;
		}
		
		PurchasePaymentCashPaymentRowItem rowItem = rowItems.get(rowIndex);
		boolean editable = true;
		switch (columnIndex) {
		case PurchasePaymentCashPaymentsTable.PAID_BY_COLUMN_INDEX:
			editable = (rowItem.getPaidDate() != null);
		case PurchasePaymentCashPaymentsTable.PAID_DATE_COLUMN_INDEX:
			editable = (rowItem.getAmount() != null);
		case PurchasePaymentCashPaymentsTable.AMOUNT_COLUMN_INDEX:
			break;
		}
		return editable;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == PurchasePaymentCashPaymentsTable.AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}

	public void addItem(PurchasePaymentCashPayment cashPayment) {
		rowItems.add(new PurchasePaymentCashPaymentRowItem(cashPayment));
		fireTableDataChanged();
	}

	public void reset(int row) {
		rowItems.get(row).reset();
	}

	public PurchasePaymentCashPaymentRowItem getRowItem(int row) {
		return rowItems.get(row);
	}

	public void removeItem(int row) {
		PurchasePaymentCashPaymentRowItem rowItem = rowItems.remove(row);
		purchasePaymentService.delete(rowItem.getCashPayment());
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
}