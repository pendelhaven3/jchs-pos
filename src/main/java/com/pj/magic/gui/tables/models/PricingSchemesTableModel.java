package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.PricingScheme;

@Component
public class PricingSchemesTableModel extends AbstractTableModel {

	private static final String[] columnNames = {"Name"};
	
	private List<PricingScheme> pricingSchemes = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return pricingSchemes.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return pricingSchemes.get(rowIndex).getName();
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public void setPricingSchemes(List<PricingScheme> pricingSchemes) {
		this.pricingSchemes = pricingSchemes;
		fireTableDataChanged();
	}
	
	public PricingScheme getPricingScheme(int rowIndex) {
		return pricingSchemes.get(rowIndex);
	}
	
}
