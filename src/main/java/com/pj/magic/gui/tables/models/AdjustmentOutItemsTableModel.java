package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.AdjustmentOutItemsTable;
import com.pj.magic.gui.tables.SalesRequisitionItemsTable;
import com.pj.magic.gui.tables.rowitems.AdjustmentOutItemRowItem;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.AdjustmentOutItem;
import com.pj.magic.service.AdjustmentOutService;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.FormatterUtil;

@Component
public class AdjustmentOutItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "Unit", "Qty", "Unit Price", "Amount"};
	
	@Autowired private ProductService productService;
	@Autowired private AdjustmentOutService adjustmentOutService;
	
	private List<AdjustmentOutItemRowItem> rowItems = new ArrayList<>();
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
		AdjustmentOutItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case AdjustmentOutItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getProductCode();
		case AdjustmentOutItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return (rowItem.getProduct() != null) ? rowItem.getProduct().getDescription() : null;
		case AdjustmentOutItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.getUnit();
		case AdjustmentOutItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getQuantity();
		case AdjustmentOutItemsTable.UNIT_PRICE_COLUMN_INDEX:
			BigDecimal unitPrice = rowItem.getUnitPrice();
			return (unitPrice != null) ? FormatterUtil.formatAmount(unitPrice) : "";
		case AdjustmentOutItemsTable.AMOUNT_COLUMN_INDEX:
			BigDecimal amount = rowItem.getAmount();
			return (amount != null) ? FormatterUtil.formatAmount(amount) : "";
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public List<AdjustmentOutItem> getItems() {
		List<AdjustmentOutItem> items = new ArrayList<>();
		for (AdjustmentOutItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setItems(List<AdjustmentOutItem> items) {
		rowItems.clear();
		for (AdjustmentOutItem item : items) {
			rowItems.add(new AdjustmentOutItemRowItem(item));
		}
		fireTableDataChanged();
	}
	
	public void addItem(AdjustmentOutItem item) {
		rowItems.add(new AdjustmentOutItemRowItem(item));
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		AdjustmentOutItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case AdjustmentOutItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			if (rowItem.getProduct() != null && rowItem.getProduct().getCode().equals(val)) {
				return;
			}
			rowItem.setProduct(productService.findProductByCode(val));
			rowItem.setUnit(null);
			break;
		case AdjustmentOutItemsTable.UNIT_COLUMN_INDEX:
			rowItem.setUnit(val);
			break;
		case AdjustmentOutItemsTable.QUANTITY_COLUMN_INDEX:
			rowItem.setQuantity(Integer.valueOf(val));
			break;
		}
		// TODO: Save only when there is a change
		if (rowItem.isValid()) {
			AdjustmentOutItem item = rowItem.getItem();
			item.setProduct(rowItem.getProduct());
			item.setUnit(rowItem.getUnit());
			item.setQuantity(Integer.valueOf(rowItem.getQuantity()));
			
			boolean newItem = (item.getId() == null);
			adjustmentOutService.save(item);
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
		} else {
			AdjustmentOutItemRowItem rowItem = rowItems.get(rowIndex);
			switch (columnIndex) {
			case SalesRequisitionItemsTable.PRODUCT_CODE_COLUMN_INDEX:
				return true;
			case SalesRequisitionItemsTable.UNIT_COLUMN_INDEX:
				return rowItem.hasValidProduct();
			case SalesRequisitionItemsTable.QUANTITY_COLUMN_INDEX:
				return rowItem.hasValidProduct() && rowItem.hasValidUnit();
			default:
				return false;
			}
		}
	}
	
	public AdjustmentOutItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		AdjustmentOutItemRowItem rowItem = rowItems.remove(rowIndex);
		adjustmentOutService.delete(rowItem.getItem());
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public void clearAndAddItem(AdjustmentOutItem item) {
		rowItems.clear();
		addItem(item);
	}

	public boolean hasDuplicate(AdjustmentOutItemRowItem checkItem) {
		for (AdjustmentOutItemRowItem rowItem : rowItems) {
			if (rowItem.equals(checkItem) && rowItem != checkItem) {
				return true;
			}
		}
		return false;
	}

	public boolean isValid(int rowIndex) {
		return rowItems.get(rowIndex).isValid();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == AdjustmentOutItemsTable.UNIT_PRICE_COLUMN_INDEX
				|| columnIndex == AdjustmentOutItemsTable.AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}

	public boolean hasDuplicate(String unit, AdjustmentOutItemRowItem checkRowItem) {
		for (AdjustmentOutItemRowItem rowItem : rowItems) {
			if (checkRowItem.getProduct().equals(rowItem.getProduct()) 
					&& unit.equals(rowItem.getUnit()) && rowItem != checkRowItem) {
				return true;
			}
		}
		return false;
	}

	public void setAdjustmentOut(AdjustmentOut adjustmentOut) {
		setItems(adjustmentOut.getItems());
		editable = !adjustmentOut.isPosted();
	}

	public void reset(int row) {
		rowItems.get(row).reset();
		fireTableRowsUpdated(row, row);
	}

	public boolean hasNonBlankItem() {
		return hasItems() && rowItems.get(0).isValid();
	}
	
}