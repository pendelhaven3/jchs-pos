package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.PurchasePaymentCreditCardPaymentsTable;
import com.pj.magic.gui.tables.rowitems.PurchasePaymentCreditCardPaymentRowItem;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.service.PurchasePaymentService;
import com.pj.magic.util.DateUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class PurchasePaymentCreditCardPaymentsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Amount", "Credit Card", "Transaction Date", "Approval Code"};
	
	@Autowired private PurchasePaymentService purchasePaymentService;
	
	private List<PurchasePaymentCreditCardPaymentRowItem> rowItems = new ArrayList<>();
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
		PurchasePaymentCreditCardPaymentRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PurchasePaymentCreditCardPaymentsTable.AMOUNT_COLUMN_INDEX:
			BigDecimal amount = rowItem.getAmount();
			return (amount != null) ? FormatterUtil.formatAmount(amount) : null;
		case PurchasePaymentCreditCardPaymentsTable.CREDIT_CARD_COLUMN_INDEX:
			return rowItem.getCreditCard();
		case PurchasePaymentCreditCardPaymentsTable.TRANSACTION_DATE_COLUMN_INDEX:
			Date transactionDate = rowItem.getTransactionDate();
			return (transactionDate != null) ? FormatterUtil.formatDate(transactionDate) : null;
		case PurchasePaymentCreditCardPaymentsTable.APPROVAL_CODE_COLUMN_INDEX:
			return rowItem.getApprovalCode();
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
			for (PurchasePaymentCreditCardPayment creditCardPayment : purchasePayment.getCreditCardPayments()) {
				rowItems.add(new PurchasePaymentCreditCardPaymentRowItem(creditCardPayment));
			}
		}
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		PurchasePaymentCreditCardPaymentRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PurchasePaymentCreditCardPaymentsTable.AMOUNT_COLUMN_INDEX:
			String amount = (String)value;
			if (NumberUtil.toBigDecimal(amount).equals(rowItem.getAmount())) {
				return;
			}
			rowItem.setAmount(NumberUtil.toBigDecimal(amount));
			break;
		case PurchasePaymentCreditCardPaymentsTable.CREDIT_CARD_COLUMN_INDEX:
			CreditCard creditCard = (CreditCard)value;
			if (creditCard.equals(rowItem.getCreditCard())) {
				return;
			}
			rowItem.setCreditCard(creditCard);
			break;
		case PurchasePaymentCreditCardPaymentsTable.TRANSACTION_DATE_COLUMN_INDEX:
			String dateString = (String)value;
			if (DateUtil.toDate(dateString).equals(rowItem.getTransactionDate())) {
				return;
			}
			rowItem.setTransactionDate(DateUtil.toDate(dateString));
			break;
		case PurchasePaymentCreditCardPaymentsTable.APPROVAL_CODE_COLUMN_INDEX:
			String approvalCode = (String)value;
			if (StringUtils.isEmpty(approvalCode) || approvalCode.equals(rowItem.getApprovalCode())) {
				return;
			}
			rowItem.setApprovalCode(approvalCode);
			break;
		}
		
		if (rowItem.isValid()) {
			PurchasePaymentCreditCardPayment creditCardPayment = rowItem.getCreditCardPayment();
			creditCardPayment.setAmount(rowItem.getAmount());
			creditCardPayment.setCreditCard(rowItem.getCreditCard());
			creditCardPayment.setTransactionDate(rowItem.getTransactionDate());
			creditCardPayment.setApprovalCode(rowItem.getApprovalCode());
			
			boolean newCreditCardPayment = (creditCardPayment.getId() == null);
			purchasePaymentService.save(creditCardPayment);
			if (newCreditCardPayment) {
				creditCardPayment.getParent().getCreditCardPayments().add(creditCardPayment);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (purchasePayment.isPosted()) {
			return false;
		}
		
		PurchasePaymentCreditCardPaymentRowItem rowItem = rowItems.get(rowIndex);
		boolean editable = true;
		switch (columnIndex) {
		case PurchasePaymentCreditCardPaymentsTable.APPROVAL_CODE_COLUMN_INDEX:
			editable = (rowItem.getTransactionDate() != null);
		case PurchasePaymentCreditCardPaymentsTable.TRANSACTION_DATE_COLUMN_INDEX:
			editable = (rowItem.getCreditCard() != null);
		case PurchasePaymentCreditCardPaymentsTable.CREDIT_CARD_COLUMN_INDEX:
			editable = (rowItem.getAmount() != null);
		case PurchasePaymentCreditCardPaymentsTable.AMOUNT_COLUMN_INDEX:
			break;
		}
		return editable;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == PurchasePaymentCreditCardPaymentsTable.AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}

	public void addItem(PurchasePaymentCreditCardPayment creditCardPayment) {
		rowItems.add(new PurchasePaymentCreditCardPaymentRowItem(creditCardPayment));
		fireTableDataChanged();
	}

	public void reset(int row) {
		rowItems.get(row).reset();
	}

	public PurchasePaymentCreditCardPaymentRowItem getRowItem(int row) {
		return rowItems.get(row);
	}

	public void removeItem(int row) {
		PurchasePaymentCreditCardPaymentRowItem rowItem = rowItems.remove(row);
		purchasePaymentService.delete(rowItem.getCreditCardPayment());
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
}