package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.PurchasePaymentBankTransfersTable;
import com.pj.magic.gui.tables.rowitems.PurchasePaymentBankTransferRowItem;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentBankTransfer;
import com.pj.magic.service.PurchasePaymentService;
import com.pj.magic.util.DateUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class PurchasePaymentBankTransfersTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Bank", "Amount", "Reference Number", "Transfer Date"};
	
	@Autowired private PurchasePaymentService purchasePaymentService;
	
	private List<PurchasePaymentBankTransferRowItem> rowItems = new ArrayList<>();
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
		PurchasePaymentBankTransferRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PurchasePaymentBankTransfersTable.BANK_COLUMN_INDEX:
			return rowItem.getBank();
		case PurchasePaymentBankTransfersTable.AMOUNT_COLUMN_INDEX:
			BigDecimal amount = rowItem.getAmount();
			return (amount != null) ? FormatterUtil.formatAmount(amount) : null;
		case PurchasePaymentBankTransfersTable.REFERENCE_NUMBER_COLUMN_INDEX:
			return rowItem.getReferenceNumber();
		case PurchasePaymentBankTransfersTable.TRANSFER_DATE_COLUMN_INDEX:
			Date transferDate = rowItem.getTransferDate();
			return (transferDate != null) ? FormatterUtil.formatDate(transferDate) : null;
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
			for (PurchasePaymentBankTransfer bankTransfer : purchasePayment.getBankTransfers()) {
				rowItems.add(new PurchasePaymentBankTransferRowItem(bankTransfer));
			}
		}
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		PurchasePaymentBankTransferRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PurchasePaymentBankTransfersTable.BANK_COLUMN_INDEX:
			String bank = (String)value;
			if (bank.equals(rowItem.getBank())) {
				return;
			}
			rowItem.setBank(bank);
			break;
		case PurchasePaymentBankTransfersTable.AMOUNT_COLUMN_INDEX:
			String amount = (String)value;
			if (NumberUtil.toBigDecimal(amount).equals(rowItem.getAmount())) {
				return;
			}
			rowItem.setAmount(NumberUtil.toBigDecimal(amount));
			break;
		case PurchasePaymentBankTransfersTable.REFERENCE_NUMBER_COLUMN_INDEX:
			String referenceNumber = (String)value;
			if (referenceNumber.equals(rowItem.getReferenceNumber())) {
				return;
			}
			rowItem.setReferenceNumber(referenceNumber);
			break;
		case PurchasePaymentBankTransfersTable.TRANSFER_DATE_COLUMN_INDEX:
			String transferDateString = (String)value;
			if (DateUtil.toDate(transferDateString).equals(rowItem.getTransferDate())) {
				return;
			}
			rowItem.setTransferDate(DateUtil.toDate(transferDateString));
			break;
		}
		
		if (rowItem.isValid()) {
			PurchasePaymentBankTransfer bankTransfer = rowItem.getBankTransfer();
			bankTransfer.setBank(rowItem.getBank());
			bankTransfer.setAmount(rowItem.getAmount());
			bankTransfer.setReferenceNumber(rowItem.getReferenceNumber());
			bankTransfer.setTransferDate(rowItem.getTransferDate());
			
			boolean newBankTransfer = (bankTransfer.getId() == null);
			purchasePaymentService.save(bankTransfer);
			if (newBankTransfer) {
				bankTransfer.getParent().getBankTransfers().add(bankTransfer);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (purchasePayment.isPosted()) {
			return false;
		}
		
		PurchasePaymentBankTransferRowItem rowItem = rowItems.get(rowIndex);
		boolean editable = true;
		switch (columnIndex) {
		case PurchasePaymentBankTransfersTable.TRANSFER_DATE_COLUMN_INDEX:
			editable = !StringUtils.isEmpty(rowItem.getReferenceNumber());
		case PurchasePaymentBankTransfersTable.REFERENCE_NUMBER_COLUMN_INDEX:
			editable = (rowItem.getAmount() != null);
		case PurchasePaymentBankTransfersTable.AMOUNT_COLUMN_INDEX:
			editable = !StringUtils.isEmpty(rowItem.getBank());
		case PurchasePaymentBankTransfersTable.BANK_COLUMN_INDEX:
			break;
		}
		return editable;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == PurchasePaymentBankTransfersTable.AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}

	public void addItem(PurchasePaymentBankTransfer bankTransfer) {
		rowItems.add(new PurchasePaymentBankTransferRowItem(bankTransfer));
		fireTableDataChanged();
	}

	public void reset(int row) {
		rowItems.get(row).reset();
	}

	public PurchasePaymentBankTransferRowItem getRowItem(int row) {
		return rowItems.get(row);
	}

	public void removeItem(int row) {
		PurchasePaymentBankTransferRowItem rowItem = rowItems.remove(row);
		purchasePaymentService.delete(rowItem.getBankTransfer());
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
}