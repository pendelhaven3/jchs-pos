package com.pj.magic.gui.tables;

import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.models.ProductSuppliersTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.model.Supplier;
import com.pj.magic.service.ProductService;

@Component
public class ProductSuppliersTable extends MagicListTable {

	public static final int BUTTON_COLUMN_INDEX = 1;
	
	@Autowired private ProductService productService;
	@Autowired private ProductSuppliersTableModel tableModel;
	
	private DeleteSupplierButton tableButton = new DeleteSupplierButton();
	
	@Autowired
	public ProductSuppliersTable(ProductSuppliersTableModel tableModel) {
		super(tableModel);
		setTableHeader(null);

		getColumnModel().getColumn(0).setPreferredWidth(270);

		TableColumn buttonColumn = getColumnModel().getColumn(1);
		buttonColumn.setCellEditor(tableButton);
		buttonColumn.setCellRenderer(tableButton);
		buttonColumn.setPreferredWidth(80);
	}

	public void updateDisplay(Product product) {
		tableModel.setSuppliers(productService.getProductSuppliers(product));
	}
	
	public void clearDisplay() {
		tableModel.setSuppliers(new ArrayList<Supplier>());
	}
	
	public Supplier getSupplier(int rowIndex) {
		return tableModel.getSupplier(rowIndex);
	}

	private class DeleteSupplierButton extends JButton implements TableCellEditor, TableCellRenderer {

		public DeleteSupplierButton() {
			super("Delete");
		}
		
		@Override
		public Object getCellEditorValue() {
			return "";
		}

		@Override
		public boolean isCellEditable(EventObject anEvent) {
			return false;
		}

		@Override
		public boolean shouldSelectCell(EventObject anEvent) {
			return true;
		}

		@Override
		public boolean stopCellEditing() {
			return true;
		}

		@Override
		public void cancelCellEditing() {
		}

		@Override
		public void addCellEditorListener(CellEditorListener l) {
		}

		@Override
		public void removeCellEditorListener(CellEditorListener l) {
		}

		@Override
		public java.awt.Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			return this;
		}

		@Override
		public java.awt.Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			return this;
		}
		
	}
	
}
