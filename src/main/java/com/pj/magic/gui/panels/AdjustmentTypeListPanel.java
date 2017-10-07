package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.AdjustmentType;
import com.pj.magic.service.AdjustmentTypeService;
import com.pj.magic.util.ComponentUtil;

@Component
public class AdjustmentTypeListPanel extends StandardMagicPanel {
	
	private static final int CODE_COLUMN_INDEX = 0;
	private static final int DESCRIPTION_COLUMN_INDEX = 1;
	
	@Autowired private AdjustmentTypeService adjustmentTypeService;
	
	private MagicListTable table;
	private AdjustmentTypesTableModel tableModel;
	
	@Override
	public void initializeComponents() {
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void initializeTable() {
		tableModel = new AdjustmentTypesTableModel();
		table = new MagicListTable(tableModel);
	}

	public void updateDisplay() {
		List<AdjustmentType> adjustmentTypes = adjustmentTypeService.getAllAdjustmentTypes();
		tableModel.setAdjustmentTypes(adjustmentTypes);
		if (!adjustmentTypes.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
	}

	public void displayAdjustmentTypeDetails(AdjustmentType adjustmentType) {
		getMagicFrame().switchToEditAdjustmentTypePanel(adjustmentType);
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		
		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, c);
	}
	
	@Override
	protected void registerKeyBindings() {
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAdjustmentType();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectAdjustmentType();
			}
		});
	}
	
	private void selectAdjustmentType() {
		displayAdjustmentTypeDetails(getCurrentlySelectedAdjustmentType());
	}

	public AdjustmentType getCurrentlySelectedAdjustmentType() {
		return tableModel.getAdjustmentType(table.getSelectedRow());
	}
	
	protected void switchToNewAdjustmentTypePanel() {
		getMagicFrame().switchToAddNewAdjustmentTypePanel();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToRecordsMaintenanceMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton addButton = new MagicToolBarButton("plus", "New");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewAdjustmentTypePanel();
			}
		});
		toolBar.add(addButton);
	}

	private class AdjustmentTypesTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Code", "Description"};
		
		private List<AdjustmentType> adjustmentTypes = new ArrayList<>();
		
		public void setAdjustmentTypes(List<AdjustmentType> adjustmentTypes) {
			this.adjustmentTypes = adjustmentTypes;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return adjustmentTypes.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			AdjustmentType adjustmentType = adjustmentTypes.get(rowIndex);
			switch (columnIndex) {
			case CODE_COLUMN_INDEX:
				return adjustmentType.getCode();
			case DESCRIPTION_COLUMN_INDEX:
				return adjustmentType.getDescription();
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
		public AdjustmentType getAdjustmentType(int index) {
			return adjustmentTypes.get(index);
		}
		
	}
	
}