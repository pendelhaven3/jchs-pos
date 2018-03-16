package com.pj.magic.gui.tables;

import javax.swing.table.TableModel;

import com.pj.magic.gui.component.MagicTableCellRenderer;

public class MagicListTable extends MagicTable {
	
    private static final long serialVersionUID = -7668410513674009880L;

    public MagicListTable(TableModel tableModel) {
		super(tableModel);
		setDefaultRenderer(Object.class, new MagicTableCellRenderer());
	}
    
    

}
