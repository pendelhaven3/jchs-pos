package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.tables.BadStockReturnItemsTable;
import com.pj.magic.gui.tables.rowitems.BadStockReturnItemRowItem;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.BadStockReturnItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.Unit;
import com.pj.magic.service.BadStockReturnService;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class BadStockReturnItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "Unit", "Qty", "SI No.", "Unit Price", "Amount"};
	
	@Autowired private ProductService productService;
	@Autowired private BadStockReturnService badStockReturnService;
	@Autowired private SalesInvoiceService salesInvoiceService;
	
	private List<BadStockReturnItemRowItem> rowItems = new ArrayList<>();
	private BadStockReturn badStockReturn;
	
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
		BadStockReturnItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case BadStockReturnItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getProductCode();
		case BadStockReturnItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return (rowItem.getProduct() != null) ? rowItem.getProduct().getDescription() : null;
		case BadStockReturnItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.getUnit();
		case BadStockReturnItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getQuantity();
		case BadStockReturnItemsTable.SALES_INVOICE_NUMBER_COLUMN_INDEX:
			return rowItem.getSalesInvoiceNumber() != null ? rowItem.getSalesInvoiceNumber().toString() : null;
		case BadStockReturnItemsTable.UNIT_PRICE_COLUMN_INDEX:
			BigDecimal cost = rowItem.getUnitPrice();
			return (cost != null) ? FormatterUtil.formatAmount(cost) : "";
		case BadStockReturnItemsTable.AMOUNT_COLUMN_INDEX:
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

	public List<BadStockReturnItem> getItems() {
		List<BadStockReturnItem> items = new ArrayList<>();
		for (BadStockReturnItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setBadStockReturn(BadStockReturn badStockReturn) {
		this.badStockReturn = badStockReturn;
		setItems(badStockReturn.getItems());
	}
	
	public void setItems(List<BadStockReturnItem> items) {
		rowItems.clear();
		for (BadStockReturnItem item : items) {
			rowItems.add(new BadStockReturnItemRowItem(item));
		}
		fireTableDataChanged();
	}
	
	public void addItem(BadStockReturnItem item) {
		rowItems.add(new BadStockReturnItemRowItem(item));
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		BadStockReturnItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case BadStockReturnItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			if (rowItem.getProduct() != null && rowItem.getProduct().getCode().equals(val)) {
				return;
			}
			rowItem.setProduct(productService.findProductByCode(val));
			rowItem.setUnit(null);
			rowItem.setUnitPrice(null);
			rowItem.setSalesInvoiceNumber(null);
			break;
		case BadStockReturnItemsTable.UNIT_COLUMN_INDEX:
			if (!val.equals(rowItem.getUnit())) {
				rowItem.setUnit(val);
				rowItem.setUnitPrice(null);
				rowItem.setSalesInvoiceNumber(null);
			}
			break;
		case BadStockReturnItemsTable.QUANTITY_COLUMN_INDEX:
			rowItem.setQuantity(Integer.valueOf(val));
			break;
		case BadStockReturnItemsTable.UNIT_PRICE_COLUMN_INDEX:
			rowItem.setUnitPrice(NumberUtil.toBigDecimal(val));
			break;
		}
		// TODO: Save only when there is a change
		if (rowItem.isValid()) {
			BadStockReturnItem item = rowItem.getItem();
			item.setProduct(rowItem.getProduct());
			item.setUnit(rowItem.getUnit());
			item.setQuantity(Integer.valueOf(rowItem.getQuantity()));
			if (item.getUnitPrice() != null) {
				item.setUnitPrice(rowItem.getUnitPrice());
			} else {
				assignPreviousUnitPriceAndReferenceSalesInvoiceNumber(item, rowItem);
			}
			
			boolean newItem = (item.getId() == null);
			badStockReturnService.save(item);
			if (newItem) {
				item.getParent().getItems().add(item);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	private void assignPreviousUnitPriceAndReferenceSalesInvoiceNumber(BadStockReturnItem item,
			BadStockReturnItemRowItem rowItem) {
		Product product = rowItem.getProduct();
		String unit = rowItem.getUnit();
		SalesInvoice salesInvoice = salesInvoiceService.getMostRecentSalesInvoice(badStockReturn.getCustomer(), product);
		if (salesInvoice != null) {
			BigDecimal unitPrice = null;
			SalesInvoiceItem salesInvoiceItem = salesInvoice.findItemByProductAndUnit(product, unit);
			if (salesInvoiceItem != null) {
				unitPrice = salesInvoiceItem.getDiscountedUnitPrice();
			} else {
				salesInvoiceItem = salesInvoice.findItemByProduct(product);
				unitPrice = salesInvoiceItem.getDiscountedUnitPrice().setScale(2, RoundingMode.HALF_UP);
				if (Unit.compare(unit, salesInvoiceItem.getUnit()) == -1) {
					unitPrice = unitPrice.divide(
							new BigDecimal(product.getUnitConversion(salesInvoiceItem.getUnit()) / product.getUnitConversion(unit)),
							2, RoundingMode.HALF_UP);
				} else {
					unitPrice = unitPrice.multiply(
							new BigDecimal(product.getUnitConversion(unit) / product.getUnitConversion(salesInvoiceItem.getUnit())))
							.setScale(2, RoundingMode.HALF_UP);
				}
			}
			item.setUnitPrice(unitPrice);
			rowItem.setUnitPrice(unitPrice);
			item.setSalesInvoiceNumber(salesInvoice.getSalesInvoiceNumber());
			rowItem.setSalesInvoiceNumber(salesInvoice.getSalesInvoiceNumber());
		} else {
			item.setUnitPrice(Constants.ZERO);
			rowItem.setUnitPrice(Constants.ZERO);
			item.setSalesInvoiceNumber(null);
			rowItem.setSalesInvoiceNumber(null);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (badStockReturn.isPosted()) {
			return false;
		} else {
			BadStockReturnItemRowItem rowItem = rowItems.get(rowIndex);
			switch (columnIndex) {
			case BadStockReturnItemsTable.PRODUCT_CODE_COLUMN_INDEX:
				return true;
			case BadStockReturnItemsTable.UNIT_COLUMN_INDEX:
				return rowItem.getProduct() != null;
			case BadStockReturnItemsTable.QUANTITY_COLUMN_INDEX:
				return rowItem.getProduct() != null && !StringUtils.isEmpty(rowItem.getUnit());
			case BadStockReturnItemsTable.SALES_INVOICE_NUMBER_COLUMN_INDEX:
				return false;
			case BadStockReturnItemsTable.UNIT_PRICE_COLUMN_INDEX:
				return rowItem.getProduct() != null && !StringUtils.isEmpty(rowItem.getUnit())
					&& rowItem.getQuantity() != null;
			default:
				return false;
			}
		}
	}
	
	public BadStockReturnItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		BadStockReturnItemRowItem rowItem = rowItems.remove(rowIndex);
		badStockReturnService.delete(rowItem.getItem());
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public void clearAndAddItem(BadStockReturnItem item) {
		rowItems.clear();
		addItem(item);
	}

	public boolean hasDuplicate(BadStockReturnItemRowItem checkItem) {
		for (BadStockReturnItemRowItem rowItem : rowItems) {
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
		if (columnIndex == BadStockReturnItemsTable.UNIT_PRICE_COLUMN_INDEX
				|| columnIndex == BadStockReturnItemsTable.AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}

	public boolean hasDuplicate(String unit, BadStockReturnItemRowItem checkRowItem) {
		for (BadStockReturnItemRowItem rowItem : rowItems) {
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