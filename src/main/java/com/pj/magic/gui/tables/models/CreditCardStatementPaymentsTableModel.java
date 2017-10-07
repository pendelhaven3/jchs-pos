package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pj.magic.gui.tables.CreditCardStatementPaymentsTable;
import com.pj.magic.gui.tables.PaymentCheckPaymentsTable;
import com.pj.magic.gui.tables.rowitems.CreditCardStatementPaymentRowItem;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.model.CreditCardStatementPayment;
import com.pj.magic.service.CreditCardService;
import com.pj.magic.util.DateUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class CreditCardStatementPaymentsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Payment Date", "Amount", "Payment Type", "Remarks"};
	
	@Autowired private CreditCardService creditCardService;
	
	private List<CreditCardStatementPaymentRowItem> rowItems = new ArrayList<>();
	private CreditCardStatement statement;
	
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
		CreditCardStatementPaymentRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case CreditCardStatementPaymentsTable.PAYMENT_DATE_COLUMN_INDEX:
			Date paymentDate = rowItem.getPaymentDate();
			return (paymentDate != null) ? FormatterUtil.formatDate(paymentDate) : null;
		case CreditCardStatementPaymentsTable.AMOUNT_COLUMN_INDEX:
			BigDecimal amount = rowItem.getAmount();
			return (amount != null) ? FormatterUtil.formatAmount(amount) : null;
		case CreditCardStatementPaymentsTable.PAYMENT_TYPE_COLUMN_INDEX:
			return rowItem.getPaymentType();
		case CreditCardStatementPaymentsTable.REMARKS_COLUMN_INDEX:
			return rowItem.getRemarks();
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public void setStatement(CreditCardStatement statement) {
		this.statement = statement;
		rowItems.clear();
		if (statement != null) {
			for (CreditCardStatementPayment checkPayment : statement.getPayments()) {
				rowItems.add(new CreditCardStatementPaymentRowItem(checkPayment));
			}
		}
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		CreditCardStatementPaymentRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case CreditCardStatementPaymentsTable.PAYMENT_DATE_COLUMN_INDEX:
			if (DateUtil.toDate(val).equals(rowItem.getPaymentDate())) {
				return;
			}
			rowItem.setPaymentDate(DateUtil.toDate(val));
			break;
		case CreditCardStatementPaymentsTable.AMOUNT_COLUMN_INDEX:
			if (NumberUtil.toBigDecimal(val).equals(rowItem.getAmount())) {
				return;
			}
			rowItem.setAmount(NumberUtil.toBigDecimal(val));
			break;
		case CreditCardStatementPaymentsTable.PAYMENT_TYPE_COLUMN_INDEX:
			if (val != null && val.equals(rowItem.getPaymentType())) {
				return;
			}
			rowItem.setPaymentType(val);
			break;
		case CreditCardStatementPaymentsTable.REMARKS_COLUMN_INDEX:
			if (val.equals(rowItem.getRemarks())) {
				return;
			}
			rowItem.setRemarks(val);
			break;
		}
		
		if (rowItem.isValid()) {
			CreditCardStatementPayment payment = rowItem.getPayment();
			payment.setPaymentDate(rowItem.getPaymentDate());
			payment.setAmount(rowItem.getAmount());
			payment.setPaymentType(rowItem.getPaymentType());
			payment.setRemarks(rowItem.getRemarks());
			
			boolean newPayment = payment.isNew();
			creditCardService.save(payment);
			if (newPayment) {
				payment.getParent().getPayments().add(payment);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (statement.isPosted()) {
			return false;
		}
		
		CreditCardStatementPaymentRowItem rowItem = rowItems.get(rowIndex);
		boolean editable = true;
		switch (columnIndex) {
		case CreditCardStatementPaymentsTable.REMARKS_COLUMN_INDEX:
			editable = editable && !StringUtils.isEmpty(rowItem.getPaymentType());
		case CreditCardStatementPaymentsTable.PAYMENT_TYPE_COLUMN_INDEX:
			editable = editable && (rowItem.getAmount() != null);
		case CreditCardStatementPaymentsTable.AMOUNT_COLUMN_INDEX:
			editable = (rowItem.getPaymentDate() != null);
		}
		return editable;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == PaymentCheckPaymentsTable.AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}

	public void addItem(CreditCardStatementPayment payment) {
		rowItems.add(new CreditCardStatementPaymentRowItem(payment));
		fireTableDataChanged();
	}

	public void reset(int row) {
		rowItems.get(row).reset();
	}

	public CreditCardStatementPaymentRowItem getRowItem(int row) {
		return rowItems.get(row);
	}

	public void removeItem(int row) {
		CreditCardStatementPaymentRowItem rowItem= rowItems.remove(row);
		creditCardService.delete(rowItem.getPayment());
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
}