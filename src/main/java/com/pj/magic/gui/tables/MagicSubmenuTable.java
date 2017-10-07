package com.pj.magic.gui.tables;

import javax.swing.table.TableModel;

import com.pj.magic.Constants;

public class MagicSubmenuTable extends MagicListTable {

	public MagicSubmenuTable(TableModel tableModel) {
		super(tableModel);
		setFont(Constants.SUBMENU_FONT);
		setRowHeight(40);
	}

}