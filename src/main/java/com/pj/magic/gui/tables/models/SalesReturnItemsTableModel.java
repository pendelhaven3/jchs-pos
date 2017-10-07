package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.SalesReturnItemsTable;
import com.pj.magic.gui.tables.rowitems.SalesReturnItemRowItem;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.SalesReturnItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.SalesReturnService;
import com.pj.magic.util.FormatterUtil;

@Component
public class SalesReturnItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "Unit", "Qty", "Unit Price", "Amount"};
	
	@Autowired private ProductService productService;
	@Autowired private SalesReturnService salesReturnService;
	
	private List<SalesReturnItemRowItem> rowItems = new ArrayList<>();
	private SalesReturn salesReturn;
	
	public void setSalesReturn(SalesReturn salesReturn) {
		this.salesReturn = salesReturn;
		setItems(salesReturn.getItems());
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
		SalesReturnItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case SalesReturnItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getProductCode();
		case SalesReturnItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return rowItem.getProductDescription();
		case SalesReturnItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.getUnit();
		case SalesReturnItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getQuantity();
		case SalesReturnItemsTable.UNIT_PRICE_COLUMN_INDEX:
			BigDecimal unitPrice = rowItem.getUnitPrice();
			return (unitPrice != null) ? FormatterUtil.formatAmount(unitPrice) : null;
		case SalesReturnItemsTable.AMOUNT_COLUMN_INDEX:
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

	public List<SalesReturnItem> getItems() {
		List<SalesReturnItem> items = new ArrayList<>();
		for (SalesReturnItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setItems(List<SalesReturnItem> items) {
		rowItems.clear();
		for (SalesReturnItem item : items) {
			rowItems.add(new SalesReturnItemRowItem(item));
		}
		fireTableDataChanged();
	}
	
	public void addItem(SalesReturnItem item) {
		rowItems.add(new SalesReturnItemRowItem(item));
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		SalesReturnItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case SalesReturnItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			if (rowItem.getProduct() != null && rowItem.getProduct().getCode().equals(val)) {
				return;
			}
			rowItem.setProduct(productService.findProductByCode(val));
			rowItem.setUnit(null);
			break;
		case SalesReturnItemsTable.UNIT_COLUMN_INDEX:
			rowItem.setUnit(val);
			break;
		case SalesReturnItemsTable.QUANTITY_COLUMN_INDEX:
			rowItem.setQuantity(Integer.valueOf(val));
			break;
		}
		if (rowItem.isValid()) {
			SalesReturnItem item = rowItem.getItem();
			item.setSalesInvoiceItem(
					salesReturn.getSalesInvoice().findItemByProductAndUnit(rowItem.getProduct(), rowItem.getUnit()));
			item.setQuantity(rowItem.getQuantity());
			
			boolean newItem = (item.getId() == null);
			salesReturnService.save(item);
			if (newItem) {
				item.getParent().getItems().add(item);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (!salesReturn.isNew()) {
			return false;
		}
		
		SalesReturnItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case SalesReturnItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return true;
		case SalesReturnItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.getProduct() != null;
		case SalesReturnItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getProduct() != null && !StringUtils.isEmpty(rowItem.getUnit());
		default:
			return false;
		}
	}
	
	public SalesReturnItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		SalesReturnItemRowItem item = rowItems.remove(rowIndex);
		salesReturnService.delete(item.getItem());
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public boolean hasNonBlankItem() {
		return hasItems() && rowItems.get(0).isValid();
	}
	
	public void clearAndAddItem(SalesReturnItem item) {
		rowItems.clear();
		addItem(item);
	}

	public List<SalesReturnItemRowItem> getRowItems() {
		return rowItems;
	}

	public boolean hasDuplicate(String unit, SalesReturnItemRowItem checkRowItem) {
		for (SalesReturnItemRowItem rowItem : rowItems) {
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