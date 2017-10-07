package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.AreaInventoryReportItemsTable;
import com.pj.magic.gui.tables.rowitems.AreaInventoryReportItemRowItem;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.AreaInventoryReportItem;
import com.pj.magic.service.AreaInventoryReportService;
import com.pj.magic.service.ProductService;

@Component
public class AreaInventoryReportItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "Unit", "Qty"};
	
	@Autowired private ProductService productService;
	@Autowired private AreaInventoryReportService areaInventoryReportService;
	
	private List<AreaInventoryReportItemRowItem> rowItems = new ArrayList<>();
	private boolean editable;
	
	public void setAreaInventoryReport(AreaInventoryReport areaInventoryReport) {
		editable = !(areaInventoryReport.getParent().isPosted() || areaInventoryReport.isReviewed());
		setItems(areaInventoryReport.getItems());
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
		AreaInventoryReportItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case AreaInventoryReportItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getProductCode();
		case AreaInventoryReportItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return rowItem.getProductDescription();
		case AreaInventoryReportItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.getUnit();
		case AreaInventoryReportItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getQuantity();
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public List<AreaInventoryReportItem> getItems() {
		List<AreaInventoryReportItem> items = new ArrayList<>();
		for (AreaInventoryReportItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setItems(List<AreaInventoryReportItem> items) {
		rowItems.clear();
		for (AreaInventoryReportItem item : items) {
			rowItems.add(new AreaInventoryReportItemRowItem(item));
		}
		fireTableDataChanged();
	}
	
	public void addItem(AreaInventoryReportItem item) {
		rowItems.add(new AreaInventoryReportItemRowItem(item));
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		AreaInventoryReportItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case AreaInventoryReportItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			if (rowItem.getProduct() != null && rowItem.getProduct().getCode().equals(val)) {
				return;
			}
			rowItem.setProduct(productService.findProductByCode(val));
			rowItem.setUnit(null);
			break;
		case AreaInventoryReportItemsTable.UNIT_COLUMN_INDEX:
			rowItem.setUnit(val);
			break;
		case AreaInventoryReportItemsTable.QUANTITY_COLUMN_INDEX:
			rowItem.setQuantity(Integer.valueOf(val));
			break;
		}
		if (rowItem.isValid()) {
			AreaInventoryReportItem item = rowItem.getItem();
			item.setProduct(rowItem.getProduct());
			item.setUnit(rowItem.getUnit());
			item.setQuantity(rowItem.getQuantity());

			boolean newItem = (item.getId() == null);
			areaInventoryReportService.save(item);
			if (newItem) {
				item.getParent().getItems().add(item);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (!editable) {
			return false;
		}
		
		AreaInventoryReportItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case AreaInventoryReportItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return true;
		case AreaInventoryReportItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.hasValidProduct();
		case AreaInventoryReportItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.hasValidUnit();
		default:
			return false;
		}
	}
	
	public AreaInventoryReportItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		AreaInventoryReportItemRowItem item = rowItems.remove(rowIndex);
		areaInventoryReportService.delete(item.getItem());
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public boolean hasNonBlankItem() {
		return hasItems() && rowItems.get(0).isValid();
	}
	
	public void clearAndAddItem(AreaInventoryReportItem item) {
		rowItems.clear();
		addItem(item);
	}

	public List<AreaInventoryReportItemRowItem> getRowItems() {
		return rowItems;
	}

	public boolean hasDuplicate(String unit, AreaInventoryReportItemRowItem checkRowItem) {
		for (AreaInventoryReportItemRowItem rowItem : rowItems) {
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