package com.pj.magic.gui.tables;

import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;
import com.pj.magic.util.FormatterUtil;

public class ProductInfoTable extends JTable {

	private ProductInfoTableModel tableModel;
	private boolean showCost;
	
	public ProductInfoTable() {
		tableModel = new ProductInfoTableModel();
		setModel(tableModel);
		
		setTableHeader(null);
		setRowHeight(20);
		setShowGrid(false);
		setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			
			@Override
			public java.awt.Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
				return this;
			}
			
		});
	}
	
	public void setProduct(Product product) {
		tableModel.setProduct(product);
	}
	
	public void setShowCost(boolean showCost) {
		this.showCost = showCost;
	}
	
	private class ProductInfoTableModel extends AbstractTableModel {

		private Product product = new Product();
		
		public void setProduct(Product product) {
			this.product = product;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return 3;
		}

		@Override
		public int getColumnCount() {
			return 6;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (rowIndex) {
			case 0:
				if (columnIndex == 0) {
					return Unit.CASE;
				} else if (columnIndex == 3) {
					return Unit.TIE;
				}
				break;
			case 1:
				if (columnIndex == 0) {
					return Unit.CARTON;
				} else if (columnIndex == 3) {
					return Unit.DOZEN;
				}
				break;
			case 2:
				switch (columnIndex) {
				case 0:
					return Unit.PIECES;
				case 3:
				case 4:
				case 5:
					return null;
				}
				break;
			}
			
			if (product == null) {
				switch (columnIndex) {
				case 1:
				case 4:
					return "0";
				case 2:
				case 5:
					return FormatterUtil.formatAmount(BigDecimal.ZERO);
				}
			}
			
			if (rowIndex == 0) {
				switch (columnIndex) {
				case 1:
					return String.valueOf(product.getUnitQuantity(Unit.CASE));
				case 2:
					BigDecimal displayValue = getDisplayValue(Unit.CASE);
					if (displayValue == null) {
						displayValue = BigDecimal.ZERO;
					}
					return FormatterUtil.formatAmount(displayValue);
				case 4:
					return String.valueOf(product.getUnitQuantity(Unit.TIE));
				case 5:
					displayValue = getDisplayValue(Unit.TIE);
					if (displayValue == null) {
						displayValue = BigDecimal.ZERO;
					}
					return FormatterUtil.formatAmount(displayValue);
				}
			} else if (rowIndex == 1) {
				switch (columnIndex) {
				case 1:
					return String.valueOf(product.getUnitQuantity(Unit.CARTON));
				case 2:
					BigDecimal displayValue = getDisplayValue(Unit.CARTON);
					if (displayValue == null) {
						displayValue = BigDecimal.ZERO;
					}
					return FormatterUtil.formatAmount(displayValue);
				case 4:
					return String.valueOf(product.getUnitQuantity(Unit.DOZEN));
				case 5:
					displayValue = getDisplayValue(Unit.DOZEN);
					if (displayValue == null) {
						displayValue = BigDecimal.ZERO;
					}
					return FormatterUtil.formatAmount(displayValue);
				}
			} else if (rowIndex == 2) {
				switch (columnIndex) {
				case 1:
					return String.valueOf(product.getUnitQuantity(Unit.PIECES));
				case 2:
					BigDecimal displayValue = getDisplayValue(Unit.PIECES);
					if (displayValue == null) {
						displayValue = BigDecimal.ZERO;
					}
					return FormatterUtil.formatAmount(displayValue);
				}
			}
			return "";
		}
		
		private BigDecimal getDisplayValue(String unit) {
			if (showCost) {
				return product.getGrossCost(unit);
			} else {
				return product.getUnitPrice(unit);
			}
		}
		
	}
	
}