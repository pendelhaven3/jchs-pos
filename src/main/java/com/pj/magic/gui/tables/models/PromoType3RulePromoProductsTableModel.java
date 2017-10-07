package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.PromoType3RulePromoProductsTable;
import com.pj.magic.model.PromoType3Rule;
import com.pj.magic.model.PromoType3RulePromoProduct;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.impl.PromoService;

@Component
public class PromoType3RulePromoProductsTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = {"Code", "Description"};
	
	@Autowired private ProductService productService;
	@Autowired private PromoService promoService;
	
	private List<PromoType3RulePromoProduct> products = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return products.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PromoType3RulePromoProduct promoProduct = products.get(rowIndex);
		switch (columnIndex) {
		case PromoType3RulePromoProductsTable.CODE_COLUMN_INDEX:
			if (promoProduct.getProduct() != null) {
				return promoProduct.getProduct().getCode();
			} else {
				return null;
			}
		case PromoType3RulePromoProductsTable.DESCRIPTION_COLUMN_INDEX:
			if (promoProduct.getProduct() != null) {
				return promoProduct.getProduct().getDescription();
			} else {
				return null;
			}
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		PromoType3RulePromoProduct promoProduct = products.get(rowIndex);
		switch (columnIndex) {
		case PromoType3RulePromoProductsTable.CODE_COLUMN_INDEX:
			String code = (String)aValue;
			
			if (promoProduct.getProduct() != null && code.equals(promoProduct.getProduct().getCode())) {
				return;
			}
			
			promoProduct.setProduct(productService.findProductByCode(code));
			
			boolean isNew = promoProduct.isNew();
			promoService.save(promoProduct);
			if (isNew) {
				promoProduct.getParent().getPromoProducts().add(promoProduct);
			}
			fireTableCellUpdated(rowIndex, columnIndex);
			break;
		default:
			throw new RuntimeException("Updating invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == PromoType3RulePromoProductsTable.CODE_COLUMN_INDEX;
	}
	
	public void setRule(PromoType3Rule rule) {
		products = new ArrayList<>();
		products.addAll(rule.getPromoProducts());
		fireTableDataChanged();
	}
	
	public void clear() {
		products.clear();
		fireTableDataChanged();
	}

	public void addItem(PromoType3RulePromoProduct promoProduct) {
		products.add(promoProduct);
		fireTableDataChanged();
	}

	public boolean hasNewRowNotYetSaved() {
		return !products.isEmpty() && products.get(products.size() - 1).isNew();
	}

	public PromoType3RulePromoProduct getPromoProduct(int row) {
		return products.get(row);
	}

	public void removePromoProduct(int row) {
		PromoType3RulePromoProduct promoProduct = products.remove(row);
		promoService.delete(promoProduct);
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !products.isEmpty();
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}
	
}