package com.pj.magic.gui.component;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class MagicTableCellRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setVerticalAlignment(CENTER);
		return this;
	}
	
}
