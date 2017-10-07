package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.report.ProductQuantityDiscrepancyReport;
import com.pj.magic.service.ReportService;
import com.pj.magic.util.FormatterUtil;

@Component
public class DailyProductQuantityDiscrepancyReportListPanel extends StandardMagicPanel {
	
	private static final int DATE_COLUMN_INDEX = 0;
	private static final int HAS_DISCREPANCY_COLUMN_INDEX = 1;
	
	@Autowired private ReportService reportService;
	
	private MagicListTable table;
	private ProductQuantityDiscrepancyReportsTableModel tableModel;
	
	@Override
	public void initializeComponents() {
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void initializeTable() {
		tableModel = new ProductQuantityDiscrepancyReportsTableModel();
		table = new MagicListTable(tableModel);
		
		table.onEnterKeyAndDoubleClick(() -> displayProductQuantityDiscrepancyReport());
	}

	public void updateDisplay() {
		tableModel.setItems(reportService.getProductQuantityDiscrepancyReports());
		if (!tableModel.getItems().isEmpty()) {
			table.selectFirstRow();
		}
	}

	public void displayProductQuantityDiscrepancyReport() {
		getMagicFrame().switchToProductQuantityDiscrepancyReportPanel(tableModel.getItem(table.getSelectedRow()));
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		
		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, c);
	}
	
	@Override
	protected void registerKeyBindings() {
	}
	
	@Override
	protected void doOnBack() {
		getMagicFrame().switchToReportsMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

	private class ProductQuantityDiscrepancyReportsTableModel extends ListBackedTableModel<ProductQuantityDiscrepancyReport> {

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ProductQuantityDiscrepancyReport report = getItem(rowIndex);
			switch (columnIndex) {
			case DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(report.getDate());
			case HAS_DISCREPANCY_COLUMN_INDEX:
				return !report.getItems().isEmpty() ? "Yes" : "No";
			default:
				return null;
			}
		}

		@Override
		protected String[] getColumnNames() {
			return new String[] {"Date", "Has Discrepancy"};
		}
		
	}
	
}
