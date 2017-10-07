package com.pj.magic.gui.dialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductPriceHistory;
import com.pj.magic.model.Unit;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.FormatterUtil;

@Component
public class ProductPriceHistoryDialog extends MagicDialog {

	private static final int DATE_COLUMN_INDEX = 0;
	private static final int USER_COLUMN_INDEX = 1;
	private static final int UNIT_PRICE_CASE_COLUMN_INDEX = 2;
	private static final int UNIT_PRICE_TIE_COLUMN_INDEX = 3;
	private static final int UNIT_PRICE_CARTON_COLUMN_INDEX = 4;
	private static final int UNIT_PRICE_DOZEN_COLUMN_INDEX = 5;
	private static final int UNIT_PRICE_PIECES_COLUMN_INDEX = 6;
	
	@Autowired private ProductService productService;

	private MagicListTable table;
	private ProductPriceHistoryTableModel tableModel;
	
	public ProductPriceHistoryDialog() {
		setSize(800, 400);
		setLocationRelativeTo(null);
		setTitle("Product Price History");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		initializeTable();
		layoutComponents();
	}

	public void initializeTable() {
		tableModel = new ProductPriceHistoryTableModel();
		table = new MagicListTable(tableModel);
		
		table.getColumnModel().getColumn(DATE_COLUMN_INDEX).setPreferredWidth(140);
		
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
	}
	
	private void layoutComponents() {
		add(new JScrollPane(table));
	}
	
	public void updateDisplay(Product product, PricingScheme pricingScheme) {
		List<ProductPriceHistory> histories = productService.getProductPriceHistory(product, pricingScheme);
		tableModel.setHistories(histories);
		if (!histories.isEmpty()) {
			table.changeSelection(0, 0);
		}
	}

	private class ProductPriceHistoryTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Date", "User", "CSE", "TIE", "CTN", "DOZ", "PCS"};
		
		private List<ProductPriceHistory> histories = new ArrayList<>();
		
		public void setHistories(List<ProductPriceHistory> histories) {
			this.histories = histories;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return histories.size();
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
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case UNIT_PRICE_CASE_COLUMN_INDEX:
			case UNIT_PRICE_TIE_COLUMN_INDEX:
			case UNIT_PRICE_CARTON_COLUMN_INDEX:
			case UNIT_PRICE_DOZEN_COLUMN_INDEX:
			case UNIT_PRICE_PIECES_COLUMN_INDEX:
				return Number.class;
			default:
				return String.class;
			}
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ProductPriceHistory history = histories.get(rowIndex);
			switch (columnIndex) {
			case DATE_COLUMN_INDEX:
				return FormatterUtil.formatDateTime(history.getUpdateDate());
			case USER_COLUMN_INDEX:
				return history.getUpdatedBy().getUsername();
			case UNIT_PRICE_CASE_COLUMN_INDEX:
				BigDecimal unitPriceCase = history.getUnitPrice(Unit.CASE);
				return (unitPriceCase != null) ? FormatterUtil.formatAmount(unitPriceCase) : null;
			case UNIT_PRICE_TIE_COLUMN_INDEX:
				BigDecimal unitPriceTie = history.getUnitPrice(Unit.TIE);
				return (unitPriceTie != null) ? FormatterUtil.formatAmount(unitPriceTie) : null;
			case UNIT_PRICE_CARTON_COLUMN_INDEX:
				BigDecimal unitPriceCarton = history.getUnitPrice(Unit.CARTON);
				return (unitPriceCarton != null) ? FormatterUtil.formatAmount(unitPriceCarton) : null;
			case UNIT_PRICE_DOZEN_COLUMN_INDEX:
				BigDecimal unitPriceDozen = history.getUnitPrice(Unit.DOZEN);
				return (unitPriceDozen != null) ? FormatterUtil.formatAmount(unitPriceDozen) : null;
			case UNIT_PRICE_PIECES_COLUMN_INDEX:
				BigDecimal unitPricePieces = history.getUnitPrice(Unit.PIECES);
				return (unitPricePieces != null) ? FormatterUtil.formatAmount(unitPricePieces) : null;
			default:
				throw new RuntimeException("Fetcing invalid column index: " + columnIndex);
			}
		}
		
	}
	
}