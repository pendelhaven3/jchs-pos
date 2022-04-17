package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchAreaInventoryReportsDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.search.AreaInventoryReportSearchCriteria;
import com.pj.magic.service.AreaInventoryReportService;
import com.pj.magic.service.InventoryCheckService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class AreaInventoryReportListPanel extends StandardMagicPanel {
	
	private static final int INVENTORY_DATE_COLUMN_INDEX = 0;
	private static final int REPORT_NUMBER_COLUMN_INDEX = 1;
	private static final int AREA_COLUMN_INDEX = 2;
	private static final int STATUS_COLUMN_INDEX = 3;
	
	@Autowired private AreaInventoryReportService areaInventoryReportService;
	@Autowired private InventoryCheckService inventoryCheckService;
	@Autowired private SearchAreaInventoryReportsDialog searchAreaInventoryReportsDialog;
	
	private MagicListTable table;
	private AreaInventoryReportsTableModel tableModel;
	private JButton addButton;
	private JLabel totalItemsLabel;
	
	@Override
	public void initializeComponents() {
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void initializeTable() {
		tableModel = new AreaInventoryReportsTableModel();
		table = new MagicListTable(tableModel);
	}

	public void updateDisplay() {
		InventoryCheck inventoryCheck = inventoryCheckService.getNonPostedInventoryCheck();
		addButton.setEnabled(inventoryCheck != null);
		if (inventoryCheck != null) {
			List<AreaInventoryReport> areaInventoryReports = 
					areaInventoryReportService.findAllAreaInventoryReportsByInventoryCheck(inventoryCheck);
			updateFields(areaInventoryReports);
			if (!areaInventoryReports.isEmpty()) {
				table.changeSelection(0, 0);
			}
		} else {
			tableModel.clear();
		}
	}

	public void displayAreaInventoryReportDetails(AreaInventoryReport areaInventoryReport) {
		getMagicFrame().switchToAreaInventoryReportPanel(areaInventoryReport);
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
		
		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}
	
	@Override
	protected void registerKeyBindings() {
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAreaInventoryReport();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectAreaInventoryReport();
			}
		});
		
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}
	
	private void selectAreaInventoryReport() {
		displayAreaInventoryReportDetails(tableModel.getAreaInventoryReport(table.getSelectedRow()));
	}

	protected void switchToNewAreaInventoryReportPanel() {
		AreaInventoryReport areaInventoryReport = new AreaInventoryReport();
		areaInventoryReport.setParent(inventoryCheckService.getNonPostedInventoryCheck());
		
		getMagicFrame().switchToAreaInventoryReportPanel(areaInventoryReport);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToInventoryCheckMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		addButton = new MagicToolBarButton("plus", "New");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewAreaInventoryReportPanel();
			}
		});
		toolBar.add(addButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchAreaInventoryReports();
			}
		});
		
		toolBar.add(searchButton);
	}

	private void searchAreaInventoryReports() {
		searchAreaInventoryReportsDialog.setVisible(true);
		
		AreaInventoryReportSearchCriteria criteria = searchAreaInventoryReportsDialog.getSearchCriteria();
		if (criteria != null) {
			List<AreaInventoryReport> areaInventoryReports = areaInventoryReportService.search(criteria);
			updateFields(areaInventoryReports);
			if (!areaInventoryReports.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}

	private void updateFields(List<AreaInventoryReport> areaInventoryReports) {
		tableModel.setAreaInventoryReports(areaInventoryReports);
		totalItemsLabel.setText(FormatterUtil.formatInteger(areaInventoryReports.size()));
	}

	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsLabel = ComponentUtil.createRightLabel(100);
		panel.add(totalItemsLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(Box.createHorizontalStrut(10), c);
		
		return panel;
	}
	
	private class AreaInventoryReportsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Inventory Date", "Report No.", "Area", "Status"};
		
		private List<AreaInventoryReport> areaInventoryReports = new ArrayList<>();
		
		@Override
		public int getRowCount() {
			return areaInventoryReports.size();
		}

		public void clear() {
			areaInventoryReports.clear();
			fireTableDataChanged();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			AreaInventoryReport areaInventoryReport = areaInventoryReports.get(rowIndex);
			switch (columnIndex) {
			case INVENTORY_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(areaInventoryReport.getParent().getInventoryDate());
			case REPORT_NUMBER_COLUMN_INDEX:
				return areaInventoryReport.getReportNumber();
			case AREA_COLUMN_INDEX:
				return areaInventoryReport.getArea();
			case STATUS_COLUMN_INDEX:
				return areaInventoryReport.getStatus();
			default:
				throw new RuntimeException("Fetch invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		public void setAreaInventoryReports(List<AreaInventoryReport> areaInventoryReports) {
			this.areaInventoryReports = areaInventoryReports;
			fireTableDataChanged();
		}
		
		public AreaInventoryReport getAreaInventoryReport(int rowIndex) {
			return areaInventoryReports.get(rowIndex);
		}

	}
	
}