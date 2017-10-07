package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.util.FormatterUtil;

@Component
public class StockQuantityConversionsTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = {"SQC No.", "Remarks", "Posted", "Printed", "Post Date", "Posted By"};
	private static final int STOCK_QUANTITY_CONVERSION_NUMBER_COLUMN_INDEX = 0;
	private static final int REMARKS_COLUMN_INDEX = 1;
	private static final int POSTED_COLUMN_INDEX = 2;
	private static final int PRINTED_COLUMN_INDEX = 3;
	private static final int POST_DATE_COLUMN_INDEX = 4;
	private static final int POSTED_BY_COLUMN_INDEX = 5;
	
	private List<StockQuantityConversion> stockQuantityConversions = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return stockQuantityConversions.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		StockQuantityConversion stockQuantityConversion = stockQuantityConversions.get(rowIndex);
		switch (columnIndex) {
		case STOCK_QUANTITY_CONVERSION_NUMBER_COLUMN_INDEX:
			return stockQuantityConversion.getStockQuantityConversionNumber();
		case REMARKS_COLUMN_INDEX:
			return stockQuantityConversion.getRemarks();
		case POSTED_COLUMN_INDEX:
			return stockQuantityConversion.isPosted() ? "Yes" : "No";
		case PRINTED_COLUMN_INDEX:
			return stockQuantityConversion.isPrinted() ? "Yes" : "No";
		case POST_DATE_COLUMN_INDEX:
			if (stockQuantityConversion.isPosted()) {
				return FormatterUtil.formatDateTime(stockQuantityConversion.getPostDate());
			} else {
				return null;
			}
		case POSTED_BY_COLUMN_INDEX:
			if (stockQuantityConversion.isPosted()) {
				return stockQuantityConversion.getPostedBy().getUsername();
			} else {
				return null;
			}
		default:
			throw new RuntimeException("Fetch invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}
	
	public void setStockQuantityConversions(List<StockQuantityConversion> stockQuantityConversions) {
		this.stockQuantityConversions = stockQuantityConversions;
		fireTableDataChanged();
	}
	
	public StockQuantityConversion getStockQuantityConversion(int rowIndex) {
		return stockQuantityConversions.get(rowIndex);
	}

	public void remove(StockQuantityConversion stockQuantityConversion) {
		stockQuantityConversions.remove(stockQuantityConversion);
		fireTableDataChanged();
	}
	
}