package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.tables.PurchaseReturnBadStockItemsTable;
import com.pj.magic.gui.tables.SalesRequisitionItemsTable;
import com.pj.magic.gui.tables.rowitems.PurchaseReturnBadStockItemRowItem;
import com.pj.magic.model.PurchaseReturnBadStock;
import com.pj.magic.model.PurchaseReturnBadStockItem;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.PurchaseReturnBadStockService;
import com.pj.magic.service.ReceivingReceiptService;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class PurchaseReturnBadStockItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "Unit", "Qty", "Unit Cost", "Amount"};
	
	@Autowired private ProductService productService;
	@Autowired private PurchaseReturnBadStockService purchaseReturnBadStockService;
	@Autowired private ReceivingReceiptService receivingReceiptService;
	
	private List<PurchaseReturnBadStockItemRowItem> rowItems = new ArrayList<>();
	private PurchaseReturnBadStock purchaseReturnBadStock;
	
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
		PurchaseReturnBadStockItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PurchaseReturnBadStockItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getProductCode();
		case PurchaseReturnBadStockItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return (rowItem.getProduct() != null) ? rowItem.getProduct().getDescription() : null;
		case PurchaseReturnBadStockItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.getUnit();
		case PurchaseReturnBadStockItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getQuantity();
		case PurchaseReturnBadStockItemsTable.UNIT_COST_COLUMN_INDEX:
			BigDecimal unitCost = rowItem.getUnitCost();
			return (unitCost != null) ? FormatterUtil.formatAmount(unitCost) : "";
		case PurchaseReturnBadStockItemsTable.AMOUNT_COLUMN_INDEX:
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

	public List<PurchaseReturnBadStockItem> getItems() {
		List<PurchaseReturnBadStockItem> items = new ArrayList<>();
		for (PurchaseReturnBadStockItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setPurchaseReturnBadStock(PurchaseReturnBadStock purchaseReturnBadStock) {
		this.purchaseReturnBadStock = purchaseReturnBadStock;
		setItems(purchaseReturnBadStock.getItems());
	}
	
	public void setItems(List<PurchaseReturnBadStockItem> items) {
		rowItems.clear();
		for (PurchaseReturnBadStockItem item : items) {
			rowItems.add(new PurchaseReturnBadStockItemRowItem(item));
		}
		fireTableDataChanged();
	}
	
	public void addItem(PurchaseReturnBadStockItem item) {
		rowItems.add(new PurchaseReturnBadStockItemRowItem(item));
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		PurchaseReturnBadStockItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case PurchaseReturnBadStockItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			if (rowItem.getProduct() != null && rowItem.getProduct().getCode().equals(val)) {
				return;
			}
			rowItem.setProduct(productService.findProductByCode(val));
			rowItem.setUnit(null);
			break;
		case PurchaseReturnBadStockItemsTable.UNIT_COLUMN_INDEX:
			if (!StringUtils.isEmpty(rowItem.getUnit()) && rowItem.getUnit().equals(val)) {
				return;
			}
			rowItem.setUnit(val);
			rowItem.setUnitCost(null);
			rowItem.getItem().setUnitCost(null);
			break;
		case PurchaseReturnBadStockItemsTable.QUANTITY_COLUMN_INDEX:
			if (rowItem.getQuantity() != null && rowItem.getQuantity().equals(Integer.valueOf(val))) {
				return;
			}
			rowItem.setQuantity(Integer.valueOf(val));
			break;
		case PurchaseReturnBadStockItemsTable.UNIT_COST_COLUMN_INDEX:
			if (rowItem.getUnitCost() != null && rowItem.getUnitCost().equals(NumberUtil.toBigDecimal(val))) {
				fireTableCellUpdated(rowIndex, columnIndex);
				return;
			}
			rowItem.setUnitCost(NumberUtil.toBigDecimal(val));
			break;
		}
		if (rowItem.isValid()) {
			PurchaseReturnBadStockItem item = rowItem.getItem();
			item.setProduct(rowItem.getProduct());
			item.setUnit(rowItem.getUnit());
			item.setQuantity(Integer.valueOf(rowItem.getQuantity()));
			if (item.getUnitCost() != null) {
				item.setUnitCost(rowItem.getUnitCost());
			} else {
				BigDecimal unitCost = getDefaultUnitCost(rowItem);
				item.setUnitCost(unitCost);
				rowItem.setUnitCost(unitCost);
			}
			
			boolean newItem = (item.getId() == null);
			purchaseReturnBadStockService.save(item);
			if (newItem) {
				item.getParent().getItems().add(item);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	private BigDecimal getDefaultUnitCost(PurchaseReturnBadStockItemRowItem rowItem) {
		ReceivingReceiptItem receivingReceiptItem =
				receivingReceiptService.findMostRecentReceivingReceiptItem(
						purchaseReturnBadStock.getSupplier(), rowItem.getProduct());
		if (receivingReceiptItem != null) {
			return receivingReceiptItem.getProduct().getFinalCost(rowItem.getUnit());
		} else {
			return Constants.ZERO;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (purchaseReturnBadStock.isPosted()) {
			return false;
		} else {
			PurchaseReturnBadStockItemRowItem rowItem = rowItems.get(rowIndex);
			switch (columnIndex) {
			case SalesRequisitionItemsTable.PRODUCT_CODE_COLUMN_INDEX:
				return true;
			case SalesRequisitionItemsTable.UNIT_COLUMN_INDEX:
				return rowItem.getProduct() != null;
			case SalesRequisitionItemsTable.QUANTITY_COLUMN_INDEX:
				return rowItem.getProduct() != null && !StringUtils.isEmpty(rowItem.getUnit());
			case SalesRequisitionItemsTable.UNIT_PRICE_COLUMN_INDEX:
				return rowItem.getProduct() != null && !StringUtils.isEmpty(rowItem.getUnit())
					&& rowItem.getQuantity() != null;
			default:
				return false;
			}
		}
	}
	
	public PurchaseReturnBadStockItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		PurchaseReturnBadStockItemRowItem rowItem = rowItems.remove(rowIndex);
		purchaseReturnBadStockService.delete(rowItem.getItem());
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public void clearAndAddItem(PurchaseReturnBadStockItem item) {
		rowItems.clear();
		addItem(item);
	}

	public boolean hasDuplicate(PurchaseReturnBadStockItemRowItem checkItem) {
		for (PurchaseReturnBadStockItemRowItem rowItem : rowItems) {
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
		if (columnIndex == PurchaseReturnBadStockItemsTable.UNIT_COST_COLUMN_INDEX
				|| columnIndex == PurchaseReturnBadStockItemsTable.AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}

	public boolean hasDuplicate(String unit, PurchaseReturnBadStockItemRowItem checkRowItem) {
		for (PurchaseReturnBadStockItemRowItem rowItem : rowItems) {
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

	public boolean hasNonBlankItem() {
		return hasItems() && rowItems.get(0).isValid();
	}
	
}