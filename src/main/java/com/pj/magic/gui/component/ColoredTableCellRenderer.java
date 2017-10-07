package com.pj.magic.gui.component;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public abstract class ColoredTableCellRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		if (isCellColored(row, column)) {
			c.setBackground(getColor());
		} else if (!isSelected) {
			c.setBackground(null);
		}
		return c;
	}
	
	abstract protected Color getColor();
	
	abstract protected boolean isCellColored(int row, int column);
	
}
