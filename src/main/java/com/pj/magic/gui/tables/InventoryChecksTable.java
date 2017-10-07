package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.panels.InventoryCheckListPanel;
import com.pj.magic.gui.tables.models.InventoryChecksTableModel;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.service.InventoryCheckService;

@Component
public class InventoryChecksTable extends MagicListTable {

	public static final int INVENTORY_DATE_COLUMN_INDEX = 0;
	public static final int STATUS_COLUMN_INDEX = 1;
	private static final String GO_TO_ADJUSTMENT_IN_ACTION_NAME = "goToInventoryCheck";

	@Autowired private InventoryCheckService inventoryCheckService;
	@Autowired private InventoryChecksTableModel tableModel;
	
	@Autowired
	public InventoryChecksTable(InventoryChecksTableModel tableModel) {
		super(tableModel);
	}
	
	@PostConstruct
	public void initialize() {
		registerKeyBindings();
    }
	
	public void update() {
		List<InventoryCheck> inventoryChecks = inventoryCheckService.getAllInventoryChecks();
		tableModel.setInventoryChecks(inventoryChecks);
		if (!inventoryChecks.isEmpty()) {
			changeSelection(0, 0, false, false);
		}
	}
	
	public InventoryCheck getCurrentlySelectedInventoryCheck() {
		return tableModel.getInventoryCheck(getSelectedRow());
	}
	
	public void displayInventoryCheckDetails(InventoryCheck salesRequisition) {
		InventoryCheckListPanel panel = (InventoryCheckListPanel)
				SwingUtilities.getAncestorOfClass(InventoryCheckListPanel.class, this);
		panel.displayInventoryCheckDetails(salesRequisition);
	}
	
	public void registerKeyBindings() {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), GO_TO_ADJUSTMENT_IN_ACTION_NAME);
		
		getActionMap().put(GO_TO_ADJUSTMENT_IN_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (getSelectedRow() != -1) {
					selectInventoryCheck();
				}
			}
		});
		
		addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			public void onDoubleClick() {
				selectInventoryCheck();
			}
		});
	}

	protected void selectInventoryCheck() {
		displayInventoryCheckDetails(getCurrentlySelectedInventoryCheck());
	}
	
}
