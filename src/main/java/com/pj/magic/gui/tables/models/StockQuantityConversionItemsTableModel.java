package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.StockQuantityConversionItemsTable;
import com.pj.magic.gui.tables.rowitems.StockQuantityConversionItemRowItem;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.StockQuantityConversionItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.StockQuantityConversionService;

/*
 * [PJ 8/25/2014] 
 * An item can have a Product instance but an invalid code.
 * Product id is used instead to check for product code validity.
 */

@Component
public class StockQuantityConversionItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "From Unit", "", "Qty", "To Unit", "Converted Qty"};
	
	@Autowired private ProductService productService;
	@Autowired private StockQuantityConversionService stockQuantityConversionService;
	
	private List<StockQuantityConversionItemRowItem> rowItems = new ArrayList<>();
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
		StockQuantityConversionItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case StockQuantityConversionItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getProductCode();
		case StockQuantityConversionItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return rowItem.getProductDescription();
		case StockQuantityConversionItemsTable.FROM_UNIT_COLUMN_INDEX:
			return rowItem.getFromUnit();
		case StockQuantityConversionItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getQuantity();
		case StockQuantityConversionItemsTable.ADD_QUANTITY_COLUMN_INDEX:
			return null;
		case StockQuantityConversionItemsTable.TO_UNIT_COLUMN_INDEX:
			return rowItem.getToUnit();
		case StockQuantityConversionItemsTable.CONVERTED_QUANTITY_COLUMN_INDEX:
			return rowItem.isValid() ? rowItem.getItem().getConvertedQuantity() : null;
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public List<StockQuantityConversionItem> getItems() {
		List<StockQuantityConversionItem> items = new ArrayList<>();
		for (StockQuantityConversionItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setStockQuantityConversion(StockQuantityConversion stockQuantityConversion) {
		rowItems.clear();
		for (StockQuantityConversionItem item : stockQuantityConversion.getItems()) {
			rowItems.add(new StockQuantityConversionItemRowItem(item));
		}
		editable = !stockQuantityConversion.isPosted();
		fireTableDataChanged();
	}
	
	public void addItem(StockQuantityConversionItem item) {
		rowItems.add(new StockQuantityConversionItemRowItem(item));
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		StockQuantityConversionItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case StockQuantityConversionItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			if (rowItem.getProduct() != null && rowItem.getProduct().getCode().equals(val)) {
				return;
			}
			rowItem.setProduct(productService.findProductByCode(val));
			rowItem.setFromUnit(null);
			rowItem.setToUnit(null);
			break;
		case StockQuantityConversionItemsTable.FROM_UNIT_COLUMN_INDEX:
			rowItem.setFromUnit(val);
			rowItem.setToUnit(null);
			break;
		case StockQuantityConversionItemsTable.QUANTITY_COLUMN_INDEX:
			rowItem.setQuantity(Integer.valueOf(val));
			break;
		case StockQuantityConversionItemsTable.TO_UNIT_COLUMN_INDEX:
			rowItem.setToUnit(val);
			break;
		}
		if (rowItem.isValid()) {
			StockQuantityConversionItem item = rowItem.getItem();
			item.setProduct(rowItem.getProduct());
			item.setFromUnit(rowItem.getFromUnit());
			item.setQuantity(rowItem.getQuantity());
			item.setToUnit(rowItem.getToUnit());
			
			boolean newItem = (item.getId() == null);
			stockQuantityConversionService.save(item);
			if (newItem) {
				item.getParent().getItems().add(item);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return editable && (columnIndex == StockQuantityConversionItemsTable.PRODUCT_CODE_COLUMN_INDEX
				|| columnIndex == StockQuantityConversionItemsTable.FROM_UNIT_COLUMN_INDEX
				|| columnIndex == StockQuantityConversionItemsTable.TO_UNIT_COLUMN_INDEX
				|| columnIndex == StockQuantityConversionItemsTable.QUANTITY_COLUMN_INDEX);
	}
	
	public StockQuantityConversionItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		StockQuantityConversionItemRowItem rowItem = rowItems.remove(rowIndex);
		stockQuantityConversionService.delete(rowItem.getItem());
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public void clearAndAddItem(StockQuantityConversionItem item) {
		rowItems.clear();
		addItem(item);
	}

	public boolean hasDuplicate(String toUnit, StockQuantityConversionItemRowItem checkRowItem) {
		for (StockQuantityConversionItemRowItem rowItem : rowItems) {
			if (checkRowItem.getProduct().equals(rowItem.getProduct()) 
					&& checkRowItem.getFromUnit().equals(rowItem.getFromUnit()) 
					&& toUnit.equals(rowItem.getToUnit()) && rowItem != checkRowItem) {
				return true;
			}
		}
		return false;
	}

	public boolean hasNonBlankItem() {
		return hasItems() && rowItems.get(0).isValid();
	}

	public void reset(int row) {
		rowItems.get(row).reset();
		fireTableRowsUpdated(row, row);
	}

}