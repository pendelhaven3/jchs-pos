package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.SalesRequisitionSeparateItemsTable;
import com.pj.magic.model.Product;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.SalesRequisitionService;

@Component
public class SalesRequisitionSeparateItemsTableModel extends AbstractTableModel {

	private static final String[] columnNames = {"Code", "Description"};
	
	@Autowired private ProductService productService;
	@Autowired private SalesRequisitionService salesRequisitionService;
	
	private List<Product> products = new ArrayList<>();
	
	public void setProducts(List<Product> products) {
		this.products = products;
		fireTableDataChanged();
	}
	
	public boolean isLastProductBlank() {
		if (products.isEmpty()) {
			return false;
		} else {
			return products.get(products.size() - 1).getId() == null;
		}
	}

	public void addProduct(Product product) {
		products.add(product);
		fireTableDataChanged();
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
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Product product = products.get(rowIndex);
		switch (columnIndex) {
		case SalesRequisitionSeparateItemsTable.CODE_COLUMN_INDEX:
			return product.getCode();
		case SalesRequisitionSeparateItemsTable.DESCRIPTION_COLUMN_INDEX:
			return product.getDescription();
		default:
			return null;
		}
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		Product product = productService.findProductByCode((String)value);
		if (product != null) {
			salesRequisitionService.addSalesRequisitionSeparateItem(product);
			products.set(rowIndex, product);
		}
		fireTableCellUpdated(rowIndex, SalesRequisitionSeparateItemsTable.CODE_COLUMN_INDEX);
		fireTableCellUpdated(rowIndex, SalesRequisitionSeparateItemsTable.DESCRIPTION_COLUMN_INDEX);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return isCodeColumn(columnIndex) && isLastProductBlank();
	}

	private boolean isCodeColumn(int columnIndex) {
		return columnIndex == SalesRequisitionSeparateItemsTable.CODE_COLUMN_INDEX;
	}

	public void removeLastProduct() {
		products.remove(products.size() - 1);
		fireTableDataChanged();
	}

	public void removeProduct(int row) {
		Product product = products.remove(row);
		salesRequisitionService.removeSalesRequisitionSeparateItem(product);
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !products.isEmpty();
	}
	
}
