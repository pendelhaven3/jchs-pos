package com.pj.magic.gui.tables;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.models.InventoryCheckSummaryTableModel;
import com.pj.magic.model.InventoryCheckSummaryItem;

@Component
public class InventoryCheckSummaryTable extends MagicListTable {

	public static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	public static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	public static final int UNIT_COLUMN_INDEX = 2;
	public static final int ACTUAL_COUNT_COLUMN_INDEX = 3;
	public static final int COST_COLUMN_INDEX = 4;
	public static final int ACTUAL_VALUE_COLUMN_INDEX = 5;

	@Autowired private InventoryCheckSummaryTableModel tableModel;
	
	@Autowired
	public InventoryCheckSummaryTable(InventoryCheckSummaryTableModel tableModel) {
		super(tableModel);
	}
	
	@PostConstruct
	public void initialize() {
		initializeColumns();
    }
	
	private void initializeColumns() {
		TableColumnModel columnModel = getColumnModel();
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(110);
		columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(30);
		columnModel.getColumn(ACTUAL_COUNT_COLUMN_INDEX).setPreferredWidth(70);
		columnModel.getColumn(COST_COLUMN_INDEX).setPreferredWidth(70);
		columnModel.getColumn(ACTUAL_VALUE_COLUMN_INDEX).setPreferredWidth(70);
	}

	public void registerKeyBindings() {
	}

	public void setItems(List<InventoryCheckSummaryItem> items) {
		tableModel.setItems(items);
	}

	public InventoryCheckSummaryItem getSelectedItem() {
		return tableModel.getItem(getSelectedRow());
	}

	public void highlight() {
		changeSelection(0, 0, false, false);
		requestFocusInWindow();
	}
	
}
