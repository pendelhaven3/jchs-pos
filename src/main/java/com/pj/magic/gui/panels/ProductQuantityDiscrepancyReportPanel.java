package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.report.ProductQuantityDiscrepancyReport;
import com.pj.magic.model.report.ProductQuantityDiscrepancyReportItem;
import com.pj.magic.service.ReportService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class ProductQuantityDiscrepancyReportPanel extends StandardMagicPanel {
	
	private static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	private static final int UNIT_COLUMN_INDEX = 2;
	private static final int PREVIOUS_QUANTITY_COLUMN_INDEX = 3;
	private static final int QUANTITY_MOVED_COLUMN_INDEX = 4;
	private static final int NEW_QUANTITY_COLUMN_INDEX = 5;
	private static final int DISCREPANCY_COLUMN_INDEX = 6;
	
	@Autowired private ReportService reportService;
	
	private MagicListTable table;
	private ItemsTableModel tableModel;
	private JLabel dateLabel;
	
	@Override
	public void initializeComponents() {
		tableModel = new ItemsTableModel();
		table = new MagicListTable(tableModel);
		
		dateLabel = new JLabel();
		
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay(ProductQuantityDiscrepancyReport report) {
		report = reportService.getProductQuantityDiscrepancyReport(report.getDate());
		dateLabel.setText(FormatterUtil.formatDate(report.getDate()));
		tableModel.setItems(report.getItems());
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Date:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(dateLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 2;
		
		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, c);
	}
	
	@Override
	protected void registerKeyBindings() {
	}
	
	protected void switchToNewAdjustmentInPanel() {
		getMagicFrame().switchToAdjustmentInPanel(new AdjustmentIn());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToDailyProductQuantityDiscrepancyReportListPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

	private class ItemsTableModel extends ListBackedTableModel<ProductQuantityDiscrepancyReportItem> {

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ProductQuantityDiscrepancyReportItem item = getItem(rowIndex);
			switch (columnIndex) {
			case PRODUCT_CODE_COLUMN_INDEX:
				return item.getProduct().getCode();
			case PRODUCT_DESCRIPTION_COLUMN_INDEX:
				return item.getProduct().getDescription();
			case UNIT_COLUMN_INDEX:
				return item.getUnit();
			case PREVIOUS_QUANTITY_COLUMN_INDEX:
				return item.getPreviousQuantity();
			case QUANTITY_MOVED_COLUMN_INDEX:
				return item.getQuantityMoved() * -1;
			case NEW_QUANTITY_COLUMN_INDEX:
				return item.getNewQuantity();
			case DISCREPANCY_COLUMN_INDEX:
				return item.getDiscrepancy() * -1;
			default:
				return null;
			}
		}

		@Override
		protected String[] getColumnNames() {
			return new String[] {"Code", "Description", "Unit", "Previous Quantity", "Quantity Moved", "New Quantity", "Discrepancy"};
		}
		
	}
	
}
