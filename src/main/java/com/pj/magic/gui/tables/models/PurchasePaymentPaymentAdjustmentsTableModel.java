package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.PaymentPaymentAdjustmentsTable;
import com.pj.magic.gui.tables.PurchasePaymentPaymentAdjustmentsTable;
import com.pj.magic.gui.tables.rowitems.PurchasePaymentAdjustmentRowItem;
import com.pj.magic.model.PurchasePaymentAdjustmentType;
import com.pj.magic.model.PurchaseReturn;
import com.pj.magic.model.PurchaseReturnBadStock;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentAdjustment;
import com.pj.magic.model.PurchasePaymentPaymentAdjustment;
import com.pj.magic.service.PurchasePaymentAdjustmentTypeService;
import com.pj.magic.service.PurchaseReturnBadStockService;
import com.pj.magic.service.PurchaseReturnService;
import com.pj.magic.service.PurchasePaymentAdjustmentService;
import com.pj.magic.service.PurchasePaymentService;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class PurchasePaymentPaymentAdjustmentsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Adjustment Type", "Reference No.", "Amount"};
	
	@Autowired private PurchasePaymentService purchasePaymentService;
	@Autowired private PurchasePaymentAdjustmentService purchasePaymentAdjustmentService;
	@Autowired private PurchasePaymentAdjustmentTypeService purchasePaymentAdjustmentTypeService;
	@Autowired private PurchaseReturnService purchaseReturnService;
	@Autowired private PurchaseReturnBadStockService purchaseReturnBadStockService;
	
	private List<PurchasePaymentAdjustmentRowItem> rowItems = new ArrayList<>();
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
		PurchasePaymentAdjustmentRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PaymentPaymentAdjustmentsTable.ADJUSTMENT_TYPE_COLUMN_INDEX:
			return rowItem.getAdjustmentType();
		case PaymentPaymentAdjustmentsTable.REFERENCE_NUMBER_COLUMN_INDEX:
			return rowItem.getReferenceNumber();
		case PaymentPaymentAdjustmentsTable.AMOUNT_COLUMN_INDEX:
			BigDecimal amount = rowItem.getAmount();
			return (amount != null) ? FormatterUtil.formatAmount(amount) : null;
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public void setPurchasePayment(PurchasePayment purchasePayment) {
		this.payment = purchasePayment;
		rowItems.clear();
		if (purchasePayment != null) {
			for (PurchasePaymentPaymentAdjustment adjustment : purchasePayment.getPaymentAdjustments()) {
				rowItems.add(new PurchasePaymentAdjustmentRowItem(adjustment));
			}
		}
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		PurchasePaymentAdjustmentRowItem rowItem = rowItems.get(rowIndex);
		
		String val = (String)value;
		switch (columnIndex) {
		case PurchasePaymentPaymentAdjustmentsTable.ADJUSTMENT_TYPE_COLUMN_INDEX:
			PurchasePaymentAdjustmentType adjustmentType = purchasePaymentAdjustmentTypeService.findAdjustmentTypeByCode(val);
			if (rowItem.getAdjustmentType() != null && rowItem.getAdjustmentType().getCode().equals(val)) {
				fireTableCellUpdated(rowIndex, columnIndex);
				return;
			}
			rowItem.setAdjustmentType(adjustmentType);
			rowItem.setReferenceNumber(null);
			rowItem.setAmount(null);
			break;
		case PurchasePaymentPaymentAdjustmentsTable.REFERENCE_NUMBER_COLUMN_INDEX:
			columnIndex = PaymentPaymentAdjustmentsTable.AMOUNT_COLUMN_INDEX;
			
			if (val.equals(rowItem.getReferenceNumber())) {
				return;
			}
			
			rowItem.setReferenceNumber(val);
			
			switch (rowItem.getAdjustmentType().getCode()) {
			case PurchasePaymentAdjustmentType.PURCHASE_RETURN_GOOD_STOCK_CODE:
				PurchaseReturn purchaseReturn = purchaseReturnService
					.findPurchaseReturnByPurchaseReturnNumber(Long.parseLong(val));
				rowItem.setAmount(purchaseReturn.getTotalAmount());
				break;
			case PurchasePaymentAdjustmentType.PURCHASE_RETURN_BAD_STOCK_CODE:
				PurchaseReturnBadStock purchaseReturnBadStock = purchaseReturnBadStockService
					.findPurchaseReturnBadStockByPurchaseReturnBadStockNumber(Long.parseLong(val));
				rowItem.setAmount(purchaseReturnBadStock.getTotalAmount());
				break;
			default:
				PurchasePaymentAdjustment paymentAdjustment = purchasePaymentAdjustmentService
					.findPurchasePaymentAdjustmentByPurchasePaymentAdjustmentNumber(Long.parseLong(val));
				rowItem.setAmount(paymentAdjustment.getAmount());
				break;
			}
			
			
			break;
		case PurchasePaymentPaymentAdjustmentsTable.AMOUNT_COLUMN_INDEX:
			String amount = (String)value;
			if (NumberUtil.toBigDecimal(amount).equals(rowItem.getAmount())) {
				return;
			}
			rowItem.setAmount(NumberUtil.toBigDecimal(amount));
			break;
		}
		
		if (rowItem.isValid()) {
			PurchasePaymentPaymentAdjustment adjustment = rowItem.getPaymentAdjustment();
			adjustment.setAdjustmentType(rowItem.getAdjustmentType());
			adjustment.setReferenceNumber(rowItem.getReferenceNumber());
			adjustment.setAmount(rowItem.getAmount());
			
			boolean newAdjustment = (adjustment.getId() == null);
			purchasePaymentService.save(adjustment);
			if (newAdjustment) {
				adjustment.getParent().getPaymentAdjustments().add(adjustment);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (payment.isPosted()) {
			return false;
		}
		
		PurchasePaymentAdjustmentRowItem rowItem = rowItems.get(rowIndex);
		boolean editable = true;
		switch (columnIndex) {
		case PaymentPaymentAdjustmentsTable.AMOUNT_COLUMN_INDEX:
			editable = false;
			break;
		case PaymentPaymentAdjustmentsTable.REFERENCE_NUMBER_COLUMN_INDEX:
			editable = (rowItem.getAdjustmentType() != null);
			break;
		}
		return editable;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == PaymentPaymentAdjustmentsTable.AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}

	public void addItem(PurchasePaymentPaymentAdjustment adjustment) {
		rowItems.add(new PurchasePaymentAdjustmentRowItem(adjustment));
		fireTableDataChanged();
	}

	public void reset(int row) {
		rowItems.get(row).reset();
	}

	public PurchasePaymentAdjustmentRowItem getRowItem(int row) {
		return rowItems.get(row);
	}

	public void removeItem(int row) {
		PurchasePaymentAdjustmentRowItem rowItem = rowItems.remove(row);
		purchasePaymentService.delete(rowItem.getPaymentAdjustment());
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
}