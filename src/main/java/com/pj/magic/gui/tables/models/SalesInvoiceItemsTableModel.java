package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.SalesInvoiceItemsTable;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class SalesInvoiceItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = 
		{"Code", "Description", "Unit", "Quantity", "Unit Price", "Amount", "Disc. 1", "Disc. 2", "Disc. 3",
		"Flat Rate", "Disc. Amount", "Net Amount"};
	
	@Autowired private SalesInvoiceService salesInvoiceService;
	
	private List<SalesInvoiceItem> items = new ArrayList<>();
	private boolean showDiscountDetails;
	private boolean editable;
	
	@Override
	public int getColumnCount() {
		return showDiscountDetails ? columnNames.length : 6;
	}
	
	@Override
	public int getRowCount() {
		return items.size();
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SalesInvoiceItem item = items.get(rowIndex);
		switch (columnIndex) {
		case SalesInvoiceItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return item.getProduct().getCode();
		case SalesInvoiceItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return item.getProduct().getDescription();
		case SalesInvoiceItemsTable.UNIT_COLUMN_INDEX:
			return item.getUnit();
		case SalesInvoiceItemsTable.QUANTITY_COLUMN_INDEX:
			return item.getQuantity();
		case SalesInvoiceItemsTable.UNIT_PRICE_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getUnitPrice());
		case SalesInvoiceItemsTable.AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getAmount());
		case SalesInvoiceItemsTable.DISCOUNT_1_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getDiscount1());
		case SalesInvoiceItemsTable.DISCOUNT_2_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getDiscount2());
		case SalesInvoiceItemsTable.DISCOUNT_3_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getDiscount3());
		case SalesInvoiceItemsTable.FLAT_RATE_DISCOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getFlatRateDiscount());
		case SalesInvoiceItemsTable.DISCOUNTED_AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getDiscountedAmount());
		case SalesInvoiceItemsTable.NET_AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getNetAmount());
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public void setSalesInvoice(SalesInvoice salesInvoice, boolean showDiscountDetails) {
		editable = salesInvoice.isNew();
		boolean redrawColumns = (showDiscountDetails != this.showDiscountDetails);
		this.showDiscountDetails = showDiscountDetails;
		
		this.items.clear();
		this.items.addAll(salesInvoice.getItems());
		
		if (redrawColumns) {
			fireTableStructureChanged();
		} else {
			fireTableDataChanged();
		}
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return editable && (
			columnIndex == SalesInvoiceItemsTable.DISCOUNT_1_COLUMN_INDEX
			|| columnIndex == SalesInvoiceItemsTable.DISCOUNT_2_COLUMN_INDEX
			|| columnIndex == SalesInvoiceItemsTable.DISCOUNT_3_COLUMN_INDEX
			|| columnIndex == SalesInvoiceItemsTable.FLAT_RATE_DISCOUNT_COLUMN_INDEX
		);
	}
	
	public SalesInvoiceItem getRowItem(int rowIndex) {
		return items.get(rowIndex);
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
//		switch (columnIndex) {
//		case SalesInvoiceItemsTable.UNIT_PRICE_COLUMN_INDEX:
//		case SalesInvoiceItemsTable.AMOUNT_COLUMN_INDEX:
//		case SalesInvoiceItemsTable.DISCOUNT_1_COLUMN_INDEX:
//		case SalesInvoiceItemsTable.DISCOUNT_2_COLUMN_INDEX:
//		case SalesInvoiceItemsTable.DISCOUNT_3_COLUMN_INDEX:
//		case SalesInvoiceItemsTable.FLAT_RATE_DISCOUNT_COLUMN_INDEX:
//		case SalesInvoiceItemsTable.DISCOUNTED_AMOUNT_COLUMN_INDEX:
//		case SalesInvoiceItemsTable.NET_AMOUNT_COLUMN_INDEX:
//			return Number.class;
//		default:
//			return Object.class;
//		}
		return super.getColumnClass(columnIndex);
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		SalesInvoiceItem item = items.get(rowIndex);
		BigDecimal val = NumberUtil.toBigDecimal((String)aValue);
		switch (columnIndex) {
		case SalesInvoiceItemsTable.DISCOUNT_1_COLUMN_INDEX:
			if (val.equals(item.getDiscount1())) {
				return;
			}
			item.setDiscount1(val);
			break;
		case SalesInvoiceItemsTable.DISCOUNT_2_COLUMN_INDEX:
			if (val.equals(item.getDiscount2())) {
				return;
			}
			item.setDiscount2(val);
			break;
		case SalesInvoiceItemsTable.DISCOUNT_3_COLUMN_INDEX:
			if (val.equals(item.getDiscount3())) {
				return;
			}
			item.setDiscount3(val);
			break;
		case SalesInvoiceItemsTable.FLAT_RATE_DISCOUNT_COLUMN_INDEX:
			if (val.equals(item.getFlatRateDiscount())) {
				return;
			}
			item.setFlatRateDiscount(val);
			break;
		}
		
		salesInvoiceService.save(item);
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
}