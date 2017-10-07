package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.NoMoreStockAdjustmentItemsTable;
import com.pj.magic.gui.tables.rowitems.NoMoreStockAdjustmentItemRowItem;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.NoMoreStockAdjustmentItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.NoMoreStockAdjustmentService;
import com.pj.magic.util.FormatterUtil;

@Component
public class NoMoreStockAdjustmentItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "Unit", "Qty", "Unit Price", "Amount"};
	
	@Autowired private ProductService productService;
	@Autowired private NoMoreStockAdjustmentService noMoreStockAdjustmentService;
	
	private List<NoMoreStockAdjustmentItemRowItem> rowItems = new ArrayList<>();
	private NoMoreStockAdjustment noMoreStockAdjustment;
	
	public void setNoMoreStockAdjustment(NoMoreStockAdjustment noMoreStockAdjustment) {
		this.noMoreStockAdjustment = noMoreStockAdjustment;
		setItems(noMoreStockAdjustment.getItems());
	}
	
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
		NoMoreStockAdjustmentItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case NoMoreStockAdjustmentItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getProductCode();
		case NoMoreStockAdjustmentItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return rowItem.getProductDescription();
		case NoMoreStockAdjustmentItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.getUnit();
		case NoMoreStockAdjustmentItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getQuantity();
		case NoMoreStockAdjustmentItemsTable.UNIT_PRICE_COLUMN_INDEX:
			BigDecimal unitPrice = rowItem.getUnitPrice();
			return (unitPrice != null) ? FormatterUtil.formatAmount(unitPrice) : null;
		case NoMoreStockAdjustmentItemsTable.AMOUNT_COLUMN_INDEX:
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

	public List<NoMoreStockAdjustmentItem> getItems() {
		List<NoMoreStockAdjustmentItem> items = new ArrayList<>();
		for (NoMoreStockAdjustmentItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setItems(List<NoMoreStockAdjustmentItem> items) {
		rowItems.clear();
		for (NoMoreStockAdjustmentItem item : items) {
			rowItems.add(new NoMoreStockAdjustmentItemRowItem(item));
		}
		fireTableDataChanged();
	}
	
	public void addItem(NoMoreStockAdjustmentItem item) {
		rowItems.add(new NoMoreStockAdjustmentItemRowItem(item));
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		NoMoreStockAdjustmentItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case NoMoreStockAdjustmentItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			if (rowItem.getProduct() != null && rowItem.getProduct().getCode().equals(val)) {
				return;
			}
			rowItem.setProduct(productService.findProductByCode(val));
			rowItem.setUnit(null);
			break;
		case NoMoreStockAdjustmentItemsTable.UNIT_COLUMN_INDEX:
			rowItem.setUnit(val);
			break;
		case NoMoreStockAdjustmentItemsTable.QUANTITY_COLUMN_INDEX:
			rowItem.setQuantity(Integer.valueOf(val));
			break;
		}
		if (rowItem.isValid()) {
			NoMoreStockAdjustmentItem item = rowItem.getItem();
			item.setSalesInvoiceItem(
					noMoreStockAdjustment.getSalesInvoice().findItemByProductAndUnit(rowItem.getProduct(), rowItem.getUnit()));
			item.setQuantity(rowItem.getQuantity());
			
			boolean newItem = (item.getId() == null);
			noMoreStockAdjustmentService.save(item);
			if (newItem) {
				item.getParent().getItems().add(item);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (noMoreStockAdjustment.isPosted()) {
			return false;
		}
		
		NoMoreStockAdjustmentItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case NoMoreStockAdjustmentItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return true;
		case NoMoreStockAdjustmentItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.getProduct() != null;
		case NoMoreStockAdjustmentItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getProduct() != null && !StringUtils.isEmpty(rowItem.getUnit());
		default:
			return false;
		}
	}
	
	public NoMoreStockAdjustmentItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		NoMoreStockAdjustmentItemRowItem item = rowItems.remove(rowIndex);
		noMoreStockAdjustmentService.delete(item.getItem());
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public boolean hasNonBlankItem() {
		return hasItems() && rowItems.get(0).isValid();
	}
	
	public void clearAndAddItem(NoMoreStockAdjustmentItem item) {
		rowItems.clear();
		addItem(item);
	}

	public List<NoMoreStockAdjustmentItemRowItem> getRowItems() {
		return rowItems;
	}

	public boolean hasDuplicate(String unit, NoMoreStockAdjustmentItemRowItem checkRowItem) {
		for (NoMoreStockAdjustmentItemRowItem rowItem : rowItems) {
			if (checkRowItem.getProduct().equals(rowItem.getProduct()) 
					&& unit.equals(rowItem.getUnit()) && rowItem != checkRowItem) {
				return true;
			}
		}
		return false;
	}

	public void reset(int row) {
		rowItems.get(row).reset();
		fireTableRowsUpdated(row, row);
	}
	
}