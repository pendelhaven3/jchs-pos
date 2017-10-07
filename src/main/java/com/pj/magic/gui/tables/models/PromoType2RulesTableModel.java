package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.PromoType2RulesTable;
import com.pj.magic.gui.tables.rowitems.PromoType2RuleRowItem;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType2Rule;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.impl.PromoService;

@Component
public class PromoType2RulesTableModel extends AbstractTableModel {

	private static final String[] columnNames = 
		{"Code", "Description", "Unit", "Quantity", "Code", "Description", "Unit", "Quantity"};
	
	@Autowired private ProductService productService;
	@Autowired private PromoService promoService;
	
	private List<PromoType2RuleRowItem> rowItems = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return rowItems.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PromoType2RuleRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PromoType2RulesTable.PROMO_PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getPromoProductCode();
		case PromoType2RulesTable.PROMO_PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return rowItem.getPromoProductDescription();
		case PromoType2RulesTable.PROMO_UNIT_COLUMN_INDEX:
			return rowItem.getPromoUnit();
		case PromoType2RulesTable.PROMO_QUANTITY_COLUMN_INDEX:
			return rowItem.getPromoQuantity();
		case PromoType2RulesTable.FREE_PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getFreeProductCode();
		case PromoType2RulesTable.FREE_PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return rowItem.getFreeProductDescription();
		case PromoType2RulesTable.FREE_UNIT_COLUMN_INDEX:
			return rowItem.getFreeUnit();
		case PromoType2RulesTable.FREE_QUANTITY_COLUMN_INDEX:
			return rowItem.getFreeQuantity();
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}

	public PromoType2RuleRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}

	public void addItem(PromoType2Rule rule) {
		rowItems.add(new PromoType2RuleRowItem(rule));
		fireTableDataChanged();
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		PromoType2RuleRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case PromoType2RulesTable.PROMO_PRODUCT_CODE_COLUMN_INDEX:
			if (rowItem.getPromoProduct() != null && rowItem.getPromoProduct().getCode().equals(val)) {
				return;
			}
			rowItem.setPromoProduct(productService.findProductByCode(val));
			rowItem.setPromoUnit(null);
			break;
		case PromoType2RulesTable.PROMO_UNIT_COLUMN_INDEX:
			if (val.equals(rowItem.getPromoUnit())) {
				return;
			}
			rowItem.setPromoUnit(val);
			break;
		case PromoType2RulesTable.PROMO_QUANTITY_COLUMN_INDEX:
			if (Integer.valueOf(val).equals(rowItem.getPromoQuantity())) {
				return;
			}
			rowItem.setPromoQuantity(Integer.valueOf(val));
			break;
		case PromoType2RulesTable.FREE_PRODUCT_CODE_COLUMN_INDEX:
			if (rowItem.getFreeProduct() != null && rowItem.getFreeProduct().getCode().equals(val)) {
				return;
			}
			rowItem.setFreeProduct(productService.findProductByCode(val));
			rowItem.setFreeUnit(null);
			break;
		case PromoType2RulesTable.FREE_UNIT_COLUMN_INDEX:
			if (val.equals(rowItem.getFreeUnit())) {
				return;
			}
			rowItem.setFreeUnit(val);
			break;
		case PromoType2RulesTable.FREE_QUANTITY_COLUMN_INDEX:
			if (Integer.valueOf(val).equals(rowItem.getFreeQuantity())) {
				return;
			}
			rowItem.setFreeQuantity(Integer.valueOf(val));
			break;
		}
		
		if (rowItem.isValid()) {
			PromoType2Rule rule = rowItem.getRule();
			rule.setPromoProduct(rowItem.getPromoProduct());
			rule.setPromoUnit(rowItem.getPromoUnit());
			rule.setPromoQuantity(rowItem.getPromoQuantity());
			rule.setFreeProduct(rowItem.getFreeProduct());
			rule.setFreeUnit(rowItem.getFreeUnit());
			rule.setFreeQuantity(rowItem.getFreeQuantity());
			
			boolean newRule = (rule.getId() == null);
			promoService.save(rule);
			if (newRule) {
				rule.getParent().getPromoType2Rules().add(rule);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case PromoType2RulesTable.PROMO_PRODUCT_DESCRIPTION_COLUMN_INDEX:
		case PromoType2RulesTable.FREE_PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return false;
		default:
			return true;
		}
	}
	
	public void setPromo(Promo promo) {
		rowItems.clear();
		if (promo != null) {
			for (PromoType2Rule rule : promo.getPromoType2Rules()) {
				rowItems.add(new PromoType2RuleRowItem(rule));
			}
		}
		fireTableDataChanged();
	}

	public void reset(int rowIndex) {
		rowItems.get(rowIndex).reset();
	}

	public void removeRule(int rowIndex) {
		PromoType2RuleRowItem item = rowItems.remove(rowIndex);
		promoService.delete(item.getRule());
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !rowItems.isEmpty();
	}

}