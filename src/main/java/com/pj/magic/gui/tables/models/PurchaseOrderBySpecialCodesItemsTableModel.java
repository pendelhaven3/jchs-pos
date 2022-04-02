package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.PurchaseOrderBySpecialCodesItemsTable;
import com.pj.magic.gui.tables.rowitems.PurchaseOrderItemRowItem;
import com.pj.magic.model.ProductBySpecialCode;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchaseOrderItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.PurchaseOrderService;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class PurchaseOrderBySpecialCodesItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = 
		{"Custom Code", "Product Code", "Description", "Unit", "Sugg. Order", "Quantity", "Cost", "Amount"};
	private static final String[] orderedColumnNames = 
		{"Custom Code", "Product Code", "Description", "Unit", "Sugg. Order", "Quantity", "Ordered", "Actual Qty", "Cost", "Amount"};
	
	@Autowired private ProductService productService;
	@Autowired private PurchaseOrderService purchaseOrderService;
	
	private List<PurchaseOrderItemRowItem> rowItems = new ArrayList<>();
	private PurchaseOrderBySpecialCodesItemsTable table;
	private boolean ordered;
	private boolean posted;
	private PurchaseOrder purchaseOrder;
	
	@Override
	public int getColumnCount() {
		return ordered ? orderedColumnNames.length : columnNames.length;
	}
	
	@Override
	public int getRowCount() {
		return rowItems.size();
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PurchaseOrderItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PurchaseOrderBySpecialCodesItemsTable.CUSTOM_CODE_COLUMN_INDEX:
			return rowItem.getItem().getCustomCode();
		case PurchaseOrderBySpecialCodesItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getProductCode();
		case PurchaseOrderBySpecialCodesItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return rowItem.getProductDescription();
		case PurchaseOrderBySpecialCodesItemsTable.UNIT_COLUMN_INDEX:
			return StringUtils.defaultString(rowItem.getUnit());
		case PurchaseOrderBySpecialCodesItemsTable.SUGGESTED_ORDER_COLUMN_INDEX:
			return rowItem.getSuggestedOrder();
		case PurchaseOrderBySpecialCodesItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getQuantity();
		default:
			if (columnIndex == table.getCostColumnIndex()) {
				if (rowItem.getCost() != null) {
					return FormatterUtil.formatAmount(rowItem.getCost());
				} else {
					return null;
				}
			} else if (columnIndex == table.getAmountColumnIndex()) {
				if (rowItem.isValid()) {
					return FormatterUtil.formatAmount(rowItem.getItem().getAmount());
				} else {
					return null;
				}
			} else if (columnIndex == table.getOrderedColumnIndex()) {
				return rowItem.getItem().isOrdered() ? "Yes" : "No";
			} else if (columnIndex == table.getActualQuantityColumnIndex()) {
				return rowItem.getActualQuantity();
			} else {
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return ordered ? orderedColumnNames[columnIndex] : columnNames[columnIndex];
	}

	public List<PurchaseOrderItem> getItems() {
		List<PurchaseOrderItem> items = new ArrayList<>();
		for (PurchaseOrderItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setItems(List<PurchaseOrderItem> items) {
		setItems(items, true);
	}
	
	public void setItems(List<PurchaseOrderItem> items, boolean update) {
		this.rowItems.clear();
		for (PurchaseOrderItem item : items) {
			this.rowItems.add(new PurchaseOrderItemRowItem(item));
		}
		if (update) {
			fireTableDataChanged();
		}
	}
	
	public void addItem(PurchaseOrderItem item) {
		if (ordered) {
			item.setQuantity(0);
		}
		rowItems.add(new PurchaseOrderItemRowItem(item));
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		PurchaseOrderItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case PurchaseOrderBySpecialCodesItemsTable.CUSTOM_CODE_COLUMN_INDEX:
			if (rowItem.getProduct() != null && rowItem.getItem().getCode().equals(val)) {
				return;
			}
			ProductBySpecialCode productBySpecialCode = productService.findProductBySpecialCode(val, purchaseOrder.getSupplier());
			rowItem.setProduct(productBySpecialCode.getCustomCode().getProduct());
			rowItem.setUnit(productBySpecialCode.getProduct().getUnits().get(0));
			rowItem.getItem().setCode(productBySpecialCode.getProduct().getCode());
			rowItem.getItem().setCustomCode(productBySpecialCode.getCustomCode().getCode());
			break;
		case PurchaseOrderBySpecialCodesItemsTable.QUANTITY_COLUMN_INDEX:
			rowItem.setQuantity(Integer.valueOf(val));
			break;
		default:
			if (columnIndex == table.getCostColumnIndex()) {
				rowItem.setCost(NumberUtil.toBigDecimal(val));
			} else if (columnIndex == table.getActualQuantityColumnIndex()) {
				rowItem.setActualQuantity(Integer.valueOf(val));
			}
		}
		// TODO: Save only when there is a change
		if (isCellEditable(rowIndex, columnIndex) && rowItem.isValid()) {
			PurchaseOrderItem item = rowItem.getItem();
			item.setProduct(rowItem.getProduct());
			item.setUnit(rowItem.getUnit());
			item.setQuantity(Integer.valueOf(rowItem.getQuantity()));
			if (item.getCost() != null) {
				item.setCost(rowItem.getCost());
			} else {
				BigDecimal originalCost = rowItem.getProduct().getGrossCost(rowItem.getUnit());
				if (!purchaseOrder.isVatInclusive()) {
					originalCost = originalCost.divide(purchaseOrder.getVatMultiplier(), 2,
							RoundingMode.HALF_UP);
				}
				item.setCost(originalCost);
				rowItem.setCost(originalCost);
			}
			item.setActualQuantity(rowItem.getActualQuantity());
			
			boolean newItem = (item.getId() == null);
			purchaseOrderService.save(item);
			if (newItem) {
				item.getParent().getItems().add(item);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (posted) {
			return false;
		} else if (ordered) {
			if (getRowItem(rowIndex).getItem().isOrdered()) {
				return columnIndex == table.getActualQuantityColumnIndex()
						|| columnIndex == table.getCostColumnIndex();
			} else {
				return columnIndex == PurchaseOrderBySpecialCodesItemsTable.CUSTOM_CODE_COLUMN_INDEX
						|| columnIndex == table.getActualQuantityColumnIndex()
						|| columnIndex == table.getCostColumnIndex();
			}
		} else {
			return columnIndex == PurchaseOrderBySpecialCodesItemsTable.CUSTOM_CODE_COLUMN_INDEX
					|| columnIndex == PurchaseOrderBySpecialCodesItemsTable.QUANTITY_COLUMN_INDEX
					|| columnIndex == table.getCostColumnIndex();
		}
	}
	
	public PurchaseOrderItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		PurchaseOrderItemRowItem rowItem = rowItems.remove(rowIndex);
		purchaseOrderService.delete(rowItem.getItem());
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public void clearAndAddItem(PurchaseOrderItem item) {
		rowItems.clear();
		addItem(item);
	}

	public boolean hasDuplicate(String code, PurchaseOrderItemRowItem checkRowItem) {
		for (PurchaseOrderItemRowItem rowItem : rowItems) {
			if (rowItem != checkRowItem && code.equals(rowItem.getItem().getCode())) {
				return true;
			}
		}
		return false;
	}

	public boolean isValid(int rowIndex) {
		return rowItems.get(rowIndex).isValid();
	}
	
	public void setTable(PurchaseOrderBySpecialCodesItemsTable table) {
		this.table = table;
	}

	public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
		ordered = purchaseOrder.isDelivered();
		posted = purchaseOrder.isPosted();
		setItems(purchaseOrder.getItems(), false);
		fireTableStructureChanged();
	}
	
	public List<PurchaseOrderItemRowItem> getRowItems() {
		return rowItems;
	}

	public void reset(int row) {
		rowItems.get(row).reset();
		fireTableRowsUpdated(row, row);
	}

	public boolean hasNonBlankItem() {
		return hasItems() && rowItems.get(0).isValid();
	}

}