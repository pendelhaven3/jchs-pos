package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.ReceivingReceiptItemsTable;
import com.pj.magic.gui.tables.rowitems.ReceivingReceiptItemRowItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.service.ReceivingReceiptService;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class ReceivingReceiptItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = 
		{"Code", "Description", "Unit", "Quantity", "Cost", "Amount", "Disc. 1", "Disc. 2", "Disc. 3",
				"Flat Rate", "Disc. Amount", "Net Amount"};
	
	@Autowired private ReceivingReceiptService receivingReceiptService;
	
	private List<ReceivingReceiptItemRowItem> rowItems = new ArrayList<>();
	private boolean editable;
	
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
		ReceivingReceiptItemRowItem rowItem = rowItems.get(rowIndex);
		ReceivingReceiptItem item = rowItem.getItem();
		switch (columnIndex) {
		case ReceivingReceiptItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return item.getProduct().getCode();
		case ReceivingReceiptItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return item.getProduct().getDescription();
		case ReceivingReceiptItemsTable.UNIT_COLUMN_INDEX:
			return item.getUnit();
		case ReceivingReceiptItemsTable.QUANTITY_COLUMN_INDEX:
			return item.getQuantity().toString();
		case ReceivingReceiptItemsTable.COST_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getCost());
		case ReceivingReceiptItemsTable.AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getAmount());
		case ReceivingReceiptItemsTable.DISCOUNT_1_COLUMN_INDEX:
			if (!StringUtils.isEmpty(rowItem.getDiscount1()) && NumberUtil.isAmount(rowItem.getDiscount1())) {
				return FormatterUtil.formatAmount(rowItem.getDiscount1AsBigDecimal());
			} else {
				return StringUtils.defaultString(rowItem.getDiscount1());
			}
		case ReceivingReceiptItemsTable.DISCOUNT_2_COLUMN_INDEX:
			if (!StringUtils.isEmpty(rowItem.getDiscount2()) && NumberUtil.isAmount(rowItem.getDiscount2())) {
				return FormatterUtil.formatAmount(rowItem.getDiscount2AsBigDecimal());
			} else {
				return StringUtils.defaultString(rowItem.getDiscount2());
			}
		case ReceivingReceiptItemsTable.DISCOUNT_3_COLUMN_INDEX:
			if (!StringUtils.isEmpty(rowItem.getDiscount3()) && NumberUtil.isAmount(rowItem.getDiscount3())) {
				return FormatterUtil.formatAmount(rowItem.getDiscount3AsBigDecimal());
			} else {
				return StringUtils.defaultString(rowItem.getDiscount3());
			}
		case ReceivingReceiptItemsTable.FLAT_RATE_COLUMN_INDEX:
			if (!StringUtils.isEmpty(rowItem.getFlatRateDiscount()) && NumberUtil.isAmount(rowItem.getFlatRateDiscount())) {
				return FormatterUtil.formatAmount(rowItem.getFlatRateDiscountAsBigDecimal());
			} else {
				return StringUtils.defaultString(rowItem.getFlatRateDiscount());
			}
		case ReceivingReceiptItemsTable.DISCOUNTED_AMOUNT_COLUMN_INDEX:
			if (rowItem.isValid()) {
				return FormatterUtil.formatAmount(item.getDiscountedAmount());
			} else {
				return "";
			}
		case ReceivingReceiptItemsTable.NET_AMOUNT_COLUMN_INDEX:
			if (rowItem.isValid()) {
				return FormatterUtil.formatAmount(item.getNetAmount());
			} else {
				return "";
			}
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public List<ReceivingReceiptItem> getItems() {
		List<ReceivingReceiptItem> items = new ArrayList<>();
		for (ReceivingReceiptItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setItems(List<ReceivingReceiptItem> items) {
		this.rowItems.clear();
		for (ReceivingReceiptItem item : items) {
			this.rowItems.add(new ReceivingReceiptItemRowItem(item));
		}
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		ReceivingReceiptItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case ReceivingReceiptItemsTable.DISCOUNT_1_COLUMN_INDEX:
			rowItem.setDiscount1(val);
			break;
		case ReceivingReceiptItemsTable.DISCOUNT_2_COLUMN_INDEX:
			rowItem.setDiscount2(val);
			break;
		case ReceivingReceiptItemsTable.DISCOUNT_3_COLUMN_INDEX:
			rowItem.setDiscount3(val);
			break;
		case ReceivingReceiptItemsTable.FLAT_RATE_COLUMN_INDEX:
			rowItem.setFlatRateDiscount(val);
			break;
		}
		
		// TODO: Save only when there is a change
		if (rowItem.isValid()) {
			ReceivingReceiptItem item = rowItem.getItem();
			item.setDiscount1(rowItem.getDiscount1AsBigDecimal());
			item.setDiscount2(rowItem.getDiscount2AsBigDecimal());
			item.setDiscount3(rowItem.getDiscount3AsBigDecimal());
			item.setFlatRateDiscount(rowItem.getFlatRateDiscountAsBigDecimal());
			receivingReceiptService.save(item);
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return editable && (columnIndex == ReceivingReceiptItemsTable.DISCOUNT_1_COLUMN_INDEX
				|| columnIndex == ReceivingReceiptItemsTable.DISCOUNT_2_COLUMN_INDEX
				|| columnIndex == ReceivingReceiptItemsTable.DISCOUNT_3_COLUMN_INDEX
				|| columnIndex == ReceivingReceiptItemsTable.FLAT_RATE_COLUMN_INDEX);
	}
	
	public ReceivingReceiptItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public boolean isValid(int rowIndex) {
		return rowItems.get(rowIndex).isValid();
	}
	
	public void setReceivingReceipt(ReceivingReceipt receivingReceipt) {
		setItems(receivingReceipt.getItems());
		setEditable(receivingReceipt.isNew());
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
}