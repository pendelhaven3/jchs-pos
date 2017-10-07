package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.PaymentPaymentAdjustmentsTable;
import com.pj.magic.gui.tables.rowitems.PaymentAdjustmentRowItem;
import com.pj.magic.model.AdjustmentType;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.PaymentPaymentAdjustment;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.service.AdjustmentTypeService;
import com.pj.magic.service.BadStockReturnService;
import com.pj.magic.service.NoMoreStockAdjustmentService;
import com.pj.magic.service.PaymentAdjustmentService;
import com.pj.magic.service.PaymentService;
import com.pj.magic.service.SalesReturnService;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class PaymentPaymentAdjustmentsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Adjustment Type", "Reference No.", "Amount"};
	
	@Autowired private PaymentService paymentService;
	@Autowired private SalesReturnService salesReturnService;
	@Autowired private BadStockReturnService badStockReturnService;
	@Autowired private AdjustmentTypeService adjustmentTypeService;
	@Autowired private NoMoreStockAdjustmentService noMoreStockAdjustmentService;
	@Autowired private PaymentAdjustmentService paymentAdjustmentService;
	
	private List<PaymentAdjustmentRowItem> rowItems = new ArrayList<>();
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
		PaymentAdjustmentRowItem rowItem = rowItems.get(rowIndex);
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

	public void setPayment(Payment payment) {
		this.payment = payment;
		rowItems.clear();
		if (payment != null) {
			for (PaymentPaymentAdjustment adjustment : payment.getAdjustments()) {
				rowItems.add(new PaymentAdjustmentRowItem(adjustment));
			}
		}
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		PaymentAdjustmentRowItem rowItem = rowItems.get(rowIndex);
		
		String val = (String)value;
		switch (columnIndex) {
		case PaymentPaymentAdjustmentsTable.ADJUSTMENT_TYPE_COLUMN_INDEX:
			AdjustmentType adjustmentType = adjustmentTypeService.findAdjustmentTypeByCode(val);
			if (rowItem.getAdjustmentType() != null && rowItem.getAdjustmentType().getCode().equals(val)) {
				fireTableCellUpdated(rowIndex, columnIndex);
				return;
			}
			rowItem.setAdjustmentType(adjustmentType);
			rowItem.setReferenceNumber(null);
			rowItem.setAmount(null);
			break;
		case PaymentPaymentAdjustmentsTable.REFERENCE_NUMBER_COLUMN_INDEX:
			columnIndex = PaymentPaymentAdjustmentsTable.AMOUNT_COLUMN_INDEX;
			
			if (val.equals(rowItem.getReferenceNumber())) {
				return;
			}
			
			rowItem.setReferenceNumber(val);
			
			switch (rowItem.getAdjustmentType().getCode()) {
			case AdjustmentType.SALES_RETURN_CODE:
				SalesReturn salesReturn = salesReturnService
					.findSalesReturnBySalesReturnNumber(Long.parseLong(val));
				rowItem.setAmount(salesReturn.getTotalAmount());
				break;
			case AdjustmentType.BAD_STOCK_RETURN_CODE:
				BadStockReturn badStockReturn = badStockReturnService
					.findBadStockReturnByBadStockReturnNumber(Long.parseLong(val));
				rowItem.setAmount(badStockReturn.getTotalAmount());
				break;
			case AdjustmentType.NO_MORE_STOCK_ADJUSTMENT_CODE:
				NoMoreStockAdjustment noMoreStockAdjustment = noMoreStockAdjustmentService
					.findNoMoreStockAdjustmentByNoMoreStockAdjustmentNumber(Long.parseLong(val));
				rowItem.setAmount(noMoreStockAdjustment.getTotalAmount());
				break;
			default:
				PaymentAdjustment paymentAdjustment = paymentAdjustmentService
					.findPaymentAdjustmentByPaymentAdjustmentNumber(Long.parseLong(val));
				rowItem.setAmount(paymentAdjustment.getAmount());
				break;
			}
			
			break;
		case PaymentPaymentAdjustmentsTable.AMOUNT_COLUMN_INDEX:
			String amount = (String)value;
			if (NumberUtil.toBigDecimal(amount).equals(rowItem.getAmount())) {
				return;
			}
			rowItem.setAmount(NumberUtil.toBigDecimal(amount));
			break;
		}
		
		if (rowItem.isValid()) {
			PaymentPaymentAdjustment adjustment = rowItem.getAdjustment();
			adjustment.setAdjustmentType(rowItem.getAdjustmentType());
			adjustment.setReferenceNumber(rowItem.getReferenceNumber());
			adjustment.setAmount(rowItem.getAmount());
			
			boolean newAdjustment = (adjustment.getId() == null);
			paymentService.save(adjustment);
			if (newAdjustment) {
				adjustment.getParent().getAdjustments().add(adjustment);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (payment.isPosted()) {
			return false;
		}
		
		PaymentAdjustmentRowItem rowItem = rowItems.get(rowIndex);
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

	public void addItem(PaymentPaymentAdjustment adjustment) {
		rowItems.add(new PaymentAdjustmentRowItem(adjustment));
		fireTableDataChanged();
	}

	public void reset(int row) {
		rowItems.get(row).reset();
	}

	public PaymentAdjustmentRowItem getRowItem(int row) {
		return rowItems.get(row);
	}

	public void removeItem(int row) {
		PaymentAdjustmentRowItem rowItem = rowItems.remove(row);
		paymentService.delete(rowItem.getAdjustment());
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
}