package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.SalesRequisitionItemsTable;
import com.pj.magic.gui.tables.rowitems.SalesRequisitionItemRowItem;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.SalesRequisitionService;
import com.pj.magic.util.FormatterUtil;

@Component
public class SalesRequisitionItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "Unit", "Qty", "Unit Price", "Amount"};
	
	@Autowired private ProductService productService;
	@Autowired private SalesRequisitionService salesRequisitionService;
	
	private List<SalesRequisitionItemRowItem> rowItems = new ArrayList<>();
	private PricingScheme pricingScheme;
	
	public void setSalesRequisition(SalesRequisition salesRequisition) {
		setItems(salesRequisition.getItems());
		pricingScheme = salesRequisition.getPricingScheme();
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
		SalesRequisitionItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case SalesRequisitionItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getProductCode();
		case SalesRequisitionItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return rowItem.getProductDescription();
		case SalesRequisitionItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.getUnit();
		case SalesRequisitionItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getQuantity();
		case SalesRequisitionItemsTable.UNIT_PRICE_COLUMN_INDEX:
			return rowItem.getUnitPrice();
		case SalesRequisitionItemsTable.AMOUNT_COLUMN_INDEX:
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

	public List<SalesRequisitionItem> getItems() {
		List<SalesRequisitionItem> items = new ArrayList<>();
		for (SalesRequisitionItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setItems(List<SalesRequisitionItem> items) {
		rowItems.clear();
		for (SalesRequisitionItem item : items) {
			rowItems.add(new SalesRequisitionItemRowItem(item));
		}
		fireTableDataChanged();
	}
	
	public void addItem(SalesRequisitionItem item) {
		rowItems.add(new SalesRequisitionItemRowItem(item));
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		SalesRequisitionItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case SalesRequisitionItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			if (rowItem.getProduct() != null && rowItem.getProduct().getCode().equals(val)) {
				return;
			}
			rowItem.setProduct(productService.findProductByCodeAndPricingScheme(val, pricingScheme));
			rowItem.setUnit(null);
			break;
		case SalesRequisitionItemsTable.UNIT_COLUMN_INDEX:
			rowItem.setUnit(val);
			break;
		case SalesRequisitionItemsTable.QUANTITY_COLUMN_INDEX:
			rowItem.setQuantity(val);
			break;
		}
		if (rowItem.isValid()) {
			SalesRequisitionItem item = rowItem.getItem();
			item.setProduct(rowItem.getProduct());
			item.setUnit(rowItem.getUnit());
			item.setQuantity(rowItem.getQuantityAsInt());
			
			boolean newItem = (item.getId() == null);
			salesRequisitionService.save(item);
			if (newItem) {
				item.getParent().getItems().add(item);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		SalesRequisitionItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case SalesRequisitionItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return true;
		case SalesRequisitionItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.hasValidProduct();
		case SalesRequisitionItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.hasValidUnit();
		default:
			return false;
		}
	}
	
	public SalesRequisitionItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		SalesRequisitionItemRowItem item = rowItems.remove(rowIndex);
		salesRequisitionService.delete(item.getItem());
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public boolean hasNonBlankItem() {
		return hasItems() && rowItems.get(0).isValid();
	}
	
	public void clearAndAddItem(SalesRequisitionItem item) {
		rowItems.clear();
		addItem(item);
	}

	public List<SalesRequisitionItemRowItem> getRowItems() {
		return rowItems;
	}

	public boolean hasDuplicate(String unit, SalesRequisitionItemRowItem checkRowItem) {
		for (SalesRequisitionItemRowItem rowItem : rowItems) {
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