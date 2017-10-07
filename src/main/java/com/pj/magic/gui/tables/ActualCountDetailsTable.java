package com.pj.magic.gui.tables;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.models.ActualCountDetailsTableModel;
import com.pj.magic.model.InventoryCheckSummaryItem;
import com.pj.magic.service.InventoryCheckService;

@Component
public class ActualCountDetailsTable extends MagicListTable {

	@Autowired private ActualCountDetailsTableModel tableModel;
	@Autowired private InventoryCheckService inventoryCheckService;
	
	@Autowired
	public ActualCountDetailsTable(ActualCountDetailsTableModel tableModel) {
		super(tableModel);
	}

	public void updateDisplay(InventoryCheckSummaryItem item) {
		tableModel.setItems(inventoryCheckService.getItemActualCountDetails(item));
	}
	
}
