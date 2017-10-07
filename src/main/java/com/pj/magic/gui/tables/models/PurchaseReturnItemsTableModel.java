package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.PurchaseReturnItemsTable;
import com.pj.magic.gui.tables.rowitems.PurchaseReturnItemRowItem;
import com.pj.magic.model.PurchaseReturn;
import com.pj.magic.model.PurchaseReturnItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.PurchaseReturnService;
import com.pj.magic.util.FormatterUtil;

@Component
public class PurchaseReturnItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "Unit", "Qty", "Unit Price", "Amount"};
	
	@Autowired private ProductService productService;
	@Autowired private PurchaseReturnService purchaseReturnService;
	
	private List<PurchaseReturnItemRowItem> rowItems = new ArrayList<>();
	private PurchaseReturn purchaseReturn;
	
	public void setPurchaseReturn(PurchaseReturn purchaseReturn) {
		this.purchaseReturn = purchaseReturn;
		setItems(purchaseReturn.getItems());
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
		PurchaseReturnItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PurchaseReturnItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getProductCode();
		case PurchaseReturnItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return rowItem.getProductDescription();
		case PurchaseReturnItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.getUnit();
		case PurchaseReturnItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getQuantity();
		case PurchaseReturnItemsTable.UNIT_COST_COLUMN_INDEX:
			BigDecimal unitCost = rowItem.getUnitCost();
			return (unitCost != null) ? FormatterUtil.formatAmount(unitCost) : null;
		case PurchaseReturnItemsTable.AMOUNT_COLUMN_INDEX:
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

	public List<PurchaseReturnItem> getItems() {
		List<PurchaseReturnItem> items = new ArrayList<>();
		for (PurchaseReturnItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setItems(List<PurchaseReturnItem> items) {
		rowItems.clear();
		for (PurchaseReturnItem item : items) {
			rowItems.add(new PurchaseReturnItemRowItem(item));
		}
		fireTableDataChanged();
	}
	
	public void addItem(PurchaseReturnItem item) {
		rowItems.add(new PurchaseReturnItemRowItem(item));
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		PurchaseReturnItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case PurchaseReturnItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			if (rowItem.getProduct() != null && rowItem.getProduct().getCode().equals(val)) {
				return;
			}
			rowItem.setProduct(productService.findProductByCode(val));
			rowItem.setUnit(null);
			break;
		case PurchaseReturnItemsTable.UNIT_COLUMN_INDEX:
			rowItem.setUnit(val);
			break;
		case PurchaseReturnItemsTable.QUANTITY_COLUMN_INDEX:
			rowItem.setQuantity(Integer.valueOf(val));
			break;
		}
		if (rowItem.isValid()) {
			PurchaseReturnItem item = rowItem.getItem();
			item.setReceivingReceiptItem(
					purchaseReturn.getReceivingReceipt().findItemByProductAndUnit(rowItem.getProduct(), rowItem.getUnit()));
			item.setQuantity(rowItem.getQuantity());
			
			boolean newItem = (item.getId() == null);
			purchaseReturnService.save(item);
			if (newItem) {
				item.getParent().getItems().add(item);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (purchaseReturn.isPosted()) {
			return false;
		}
		
		PurchaseReturnItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PurchaseReturnItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return true;
		case PurchaseReturnItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.getProduct() != null;
		case PurchaseReturnItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getProduct() != null && !StringUtils.isEmpty(rowItem.getUnit());
		default:
			return false;
		}
	}
	
	public PurchaseReturnItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		PurchaseReturnItemRowItem item = rowItems.remove(rowIndex);
		purchaseReturnService.delete(item.getItem());
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public boolean hasNonBlankItem() {
		return hasItems() && rowItems.get(0).isValid();
	}
	
	public void clearAndAddItem(PurchaseReturnItem item) {
		rowItems.clear();
		addItem(item);
	}

	public List<PurchaseReturnItemRowItem> getRowItems() {
		return rowItems;
	}

	public boolean hasDuplicate(String unit, PurchaseReturnItemRowItem checkRowItem) {
		for (PurchaseReturnItemRowItem rowItem : rowItems) {
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