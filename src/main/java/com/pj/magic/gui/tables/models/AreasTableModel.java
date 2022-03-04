package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.Area;

@Component
public class AreasTableModel extends AbstractTableModel {

	private static final String[] columnNames = {"Name"};
	private static final int NAME_COLUMN_INDEX = 0;
	
	private List<Area> areas = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return areas.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Area area = areas.get(rowIndex);
		switch (columnIndex) {
		case NAME_COLUMN_INDEX:
			return area.getName();
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public void setAreas(List<Area> areas) {
		this.areas = areas;
		fireTableDataChanged();
	}
	
	public Area getArea(int rowIndex) {
		return areas.get(rowIndex);
	}
	
}
