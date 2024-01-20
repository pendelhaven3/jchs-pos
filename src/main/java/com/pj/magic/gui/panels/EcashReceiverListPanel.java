package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.CustomAction;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.EcashReceiver;
import com.pj.magic.service.EcashReceiverService;

@Component
public class EcashReceiverListPanel extends StandardMagicPanel {

	private static final int NAME_COLUMN_INDEX = 0;
	private static final int TYPE_COLUMN_INDEX = 1;
	
	@Autowired
	private EcashReceiverService ecashReceiverService;
	
	private MagicListTable table;
	private EcashReceiversTableModel tableModel = new EcashReceiversTableModel();
	
	public void updateDisplay() {
		tableModel.setItems(ecashReceiverService.getAllEcashReceivers());
		if (tableModel.hasItems()) {
			table.selectFirstRow();
		}
	}

	@Override
	protected void initializeComponents() {
		table = new MagicListTable(tableModel);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(5), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(new JScrollPane(table), c);
	}

	@Override
	protected void registerKeyBindings() {
		table.onEnterKeyAndDoubleClick(new CustomAction() {
			
			@Override
			public void doAction() {
				selectEcashReceiver();
			}
		});
		
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}

	protected void selectEcashReceiver() {
		EcashReceiver ecashReceiver = tableModel.getItem(table.getSelectedRow());
		getMagicFrame().switchToEditEcashReceiverPanel(ecashReceiver);
	}

	private void switchToNewEcashReceieverPanel() {
		getMagicFrame().switchToAddEcashReceiverPanel();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToRecordsMaintenanceMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton postButton = new MagicToolBarButton("plus", "New", e -> switchToNewEcashReceieverPanel());
		toolBar.add(postButton);
	}

	private class EcashReceiversTableModel extends ListBackedTableModel<EcashReceiver>{

		private final String[] columnNames = {"Name", "Type"};
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			EcashReceiver ecashReceiver = getItem(rowIndex);
			switch (columnIndex) {
			case NAME_COLUMN_INDEX:
				return ecashReceiver.getName();
			case TYPE_COLUMN_INDEX:
				return ecashReceiver.getEcashType().getCode();
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}

		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}

	}
	
}