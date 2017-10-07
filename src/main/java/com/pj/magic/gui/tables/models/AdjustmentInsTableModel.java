package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.AdjustmentInsTable;
import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.util.FormatterUtil;

@Component
public class AdjustmentInsTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = {"Adj. In No.", "Remarks", "Posted", "Post Date", "Pilferage"};
	
	private List<AdjustmentIn> adjustmentIns = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return adjustmentIns.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		AdjustmentIn adjustmentIn = adjustmentIns.get(rowIndex);
		switch (columnIndex) {
		case AdjustmentInsTable.ADJUSTMENT_IN_NUMBER_COLUMN_INDEX:
			return adjustmentIn.getAdjustmentInNumber();
		case AdjustmentInsTable.REMARKS_COLUMN_INDEX:
			return adjustmentIn.getRemarks();
		case AdjustmentInsTable.POSTED_COLUMN_INDEX:
			return adjustmentIn.isPosted() ? "Yes" : "No";
		case AdjustmentInsTable.POST_DATE_COLUMN_INDEX:
			return adjustmentIn.isPosted() ? FormatterUtil.formatDateTime(adjustmentIn.getPostDate()) : null;
		case AdjustmentInsTable.PILFERAGE_FLAG_COLUMN_INDEX:
			return adjustmentIn.getPilferageFlag() ? "Yes" : "No";
		default:
			throw new RuntimeException("Fetch invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	public void setAdjustmentIns(List<AdjustmentIn> adjustmentIns) {
		this.adjustmentIns = adjustmentIns;
		fireTableDataChanged();
	}
	
	public AdjustmentIn getAdjustmentIn(int rowIndex) {
		return adjustmentIns.get(rowIndex);
	}

	public void remove(AdjustmentIn adjustmentIn) {
		adjustmentIns.remove(adjustmentIn);
		fireTableDataChanged();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case AdjustmentInsTable.POSTED_COLUMN_INDEX:
		case AdjustmentInsTable.PILFERAGE_FLAG_COLUMN_INDEX:
			return Number.class;
		default:
			return Object.class;
		}
	}
	
}