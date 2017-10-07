package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.InventoryCorrection;
import com.pj.magic.service.InventoryCorrectionService;
import com.pj.magic.util.FormatterUtil;

@Component
public class InventoryCorrectionListPanel extends StandardMagicPanel {
	
	private static final int INVENTORY_CORRECTION_NUMBER_COLUMN_INDEX = 0;
	private static final int POST_DATE_COLUMN_INDEX = 1;
	private static final int PRODUCT_CODE_COLUMN_INDEX = 2;
	private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 3;
	private static final int UNIT_COLUMN_INDEX = 4;
	private static final int QUANTITY_COLUMN_INDEX = 5;
	
	@Autowired private InventoryCorrectionService inventoryCorrectionService;
	
	private MagicListTable table;
	private InventoryCorrectionsTableModel tableModel;
	
	@Override
	public void initializeComponents() {
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void initializeTable() {
		tableModel = new InventoryCorrectionsTableModel();
		table = new MagicListTable(tableModel);
		
		table.onEnterKeyAndDoubleClick(() -> displaySelectedInventoryCorrectionDetails());
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(INVENTORY_CORRECTION_NUMBER_COLUMN_INDEX).setPreferredWidth(120);
		columnModel.getColumn(POST_DATE_COLUMN_INDEX).setPreferredWidth(150);
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(120);
		columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(80);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(80);
	}

	public void updateDisplay() {
		tableModel.setItems(inventoryCorrectionService.getAllInventoryCorrections());
		if (tableModel.hasItems()) {
			table.selectFirstRow();
		}
	}

	private void displaySelectedInventoryCorrectionDetails() {
		getMagicFrame().switchToInventoryCorrectionPanel(tableModel.getItem(table.getSelectedRow()));
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		c.insets.top = 5;
		
		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, c);
	}
	
	@Override
	protected void registerKeyBindings() {
	}
	
	protected void switchToNewInventoryCorrectionPanel() {
		getMagicFrame().switchToInventoryCorrectionPanel(new InventoryCorrection());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToInventoryCheckMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton addButton = new MagicToolBarButton("plus", "New");
		addButton.addActionListener(e -> switchToNewInventoryCorrectionPanel());
		toolBar.add(addButton);
	}

	private class InventoryCorrectionsTableModel extends ListBackedTableModel<InventoryCorrection> {
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			InventoryCorrection item = getItem(rowIndex);
			switch (columnIndex) {
			case INVENTORY_CORRECTION_NUMBER_COLUMN_INDEX:
				return item.getInventoryCorrectionNumber();
			case POST_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDateTime(item.getPostDate());
			case PRODUCT_CODE_COLUMN_INDEX:
				return item.getProduct().getCode();
			case PRODUCT_DESCRIPTION_COLUMN_INDEX:
				return item.getProduct().getDescription();
			case UNIT_COLUMN_INDEX:
				return item.getUnit();
			case QUANTITY_COLUMN_INDEX:
				return item.getQuantity();
			default:
				return null;
			}
		}

		@Override
		protected String[] getColumnNames() {
			return new String[] {"Inv. Correction No.", "Post Date", "Product Code", "Product Description", "Unit", "Quantity"};
		}
		
	}
	
}
