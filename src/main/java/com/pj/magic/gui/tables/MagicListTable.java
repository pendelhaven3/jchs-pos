package com.pj.magic.gui.tables;

import javax.swing.table.TableModel;

import com.pj.magic.gui.component.MagicTableCellRenderer;

public class MagicListTable extends MagicTable {
	
	public MagicListTable(TableModel tableModel) {
		super(tableModel);
		setDefaultRenderer(Object.class, new MagicTableCellRenderer());
	}

}
