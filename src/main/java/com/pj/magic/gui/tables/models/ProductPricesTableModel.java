package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;
import com.pj.magic.util.FormatterUtil;

public class ProductPricesTableModel extends AbstractTableModel {

	private static final String[] columnNames = 
		{"Code", "Description", Unit.CASE, Unit.TIE, Unit.CARTON, Unit.DOZEN, Unit.PIECES};
	public static final int CODE_COLUMN_INDEX = 0;
	public static final int DESCRIPTION_COLUMN_INDEX = 1;
	public static final int CASE_UNIT_PRICE_COLUMN_INDEX = 2;
	public static final int TIE_UNIT_PRICE_COLUMN_INDEX = 3;
	public static final int CARTON_UNIT_PRICE_COLUMN_INDEX = 4;
	public static final int DOZEN_UNIT_PRICE_COLUMN_INDEX = 5;
	public static final int PIECES_UNIT_PRICE_COLUMN_INDEX = 6;
	
	private List<Product> products = new ArrayList<>();
	
	public void setProducts(List<Product> products) {
		this.products = products;
		fireTableDataChanged();
	}
	
	public List<Product> getProducts() {
		return products;
	}
	
	@Override
	public int getRowCount() {
		return products.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Product product = products.get(rowIndex);
		switch (columnIndex) {
		case CODE_COLUMN_INDEX:
			return product.getCode();
		case DESCRIPTION_COLUMN_INDEX:
			return product.getDescription();
		case CASE_UNIT_PRICE_COLUMN_INDEX:
			if (product.hasUnit(Unit.CASE)) {
				return FormatterUtil.formatAmount(product.getUnitPrice(Unit.CASE));
			} else {
				return "-";
			}
		case TIE_UNIT_PRICE_COLUMN_INDEX:
			if (product.hasUnit(Unit.TIE)) {
				return FormatterUtil.formatAmount(product.getUnitPrice(Unit.TIE));
			} else {
				return "-";
			}
		case CARTON_UNIT_PRICE_COLUMN_INDEX:
			if (product.hasUnit(Unit.CARTON)) {
				return FormatterUtil.formatAmount(product.getUnitPrice(Unit.CARTON));
			} else {
				return "-";
			}
		case DOZEN_UNIT_PRICE_COLUMN_INDEX:
			if (product.hasUnit(Unit.DOZEN)) {
				return FormatterUtil.formatAmount(product.getUnitPrice(Unit.DOZEN));
			} else {
				return "-";
			}
		case PIECES_UNIT_PRICE_COLUMN_INDEX:
			if (product.hasUnit(Unit.PIECES)) {
				return FormatterUtil.formatAmount(product.getUnitPrice(Unit.PIECES));
			} else {
				return "-";
			}
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	public Product getProduct(int rowIndex) {
		return products.get(rowIndex);
	}
	
}
