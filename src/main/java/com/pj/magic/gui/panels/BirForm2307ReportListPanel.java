package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.dao.BirForm2307ReportDao;
import com.pj.magic.gui.MagicFrame;
import com.pj.magic.gui.component.CustomAction;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.BirForm2307Report;
import com.pj.magic.util.FormatterUtil;

@Component
public class BirForm2307ReportListPanel extends StandardMagicPanel {
	
	@Autowired
	private BirForm2307ReportDao birForm2307ReportDao;
	
	private MagicListTable table;
	private BirForm2307ReportsTableModel tableModel;
	
	public BirForm2307ReportListPanel() {
	    setTitle("BIR Form 2307 Report List");
    }
	
	@Override
	public void initializeComponents() {
		tableModel = new BirForm2307ReportsTableModel();
		table = new MagicListTable(tableModel);
		
		focusOnComponentWhenThisPanelIsDisplayed(table);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(REPORT_NUMBER_COLUMN_INDEX).setPreferredWidth(80);
        columnModel.getColumn(SUPPLIER_NAME_COLUMN_INDEX).setPreferredWidth(260);
        columnModel.getColumn(FROM_DATE_COLUMN_INDEX).setPreferredWidth(80);
        columnModel.getColumn(TO_DATE_COLUMN_INDEX).setPreferredWidth(80);
        columnModel.getColumn(GENERATE_DATE_COLUMN_INDEX).setPreferredWidth(100);
	}

	@Override
	public void updateDisplay() {
		List<BirForm2307Report> reports = birForm2307ReportDao.getAll();
		tableModel.setItems(reports);
		if (!reports.isEmpty()) {
			table.selectFirstRow();
		}
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
	}
	
	@Override
	protected void registerKeyBindings() {
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
		
		table.onEnterKeyAndDoubleClick(new CustomAction() {
			
			@Override
			public void doAction() {
				selectStatement();
			}
		});
	}
	
	protected void selectStatement() {
		getMagicFrame().switchToBirForm2307ReportPanel(getSelectedItem());
	}

	private BirForm2307Report getSelectedItem() {
        return tableModel.getItem(table.getSelectedRow());
    }

    @Override
	protected void doOnBack() {
		getMagicFrame().switchToReportsMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
        toolBar.add(new MagicToolBarButton("plus", "New", e -> switchToGenerateBirForm2307ReportPanel()));
	}

	private void switchToGenerateBirForm2307ReportPanel() {
	    getMagicFrame().switchPanel(MagicFrame.BIR_FORM_2307_REPORT_PANEL);
	}
	
    private static final int REPORT_NUMBER_COLUMN_INDEX = 0;
    private static final int SUPPLIER_NAME_COLUMN_INDEX = 1;
    private static final int FROM_DATE_COLUMN_INDEX = 2;
    private static final int TO_DATE_COLUMN_INDEX = 3;
    private static final int GENERATE_DATE_COLUMN_INDEX = 4;
    
    private static final String[] COLUMN_NAMES = {"Report No.", "Supplier", "From Date", "To Date", "Generate Date"};
	
	private class BirForm2307ReportsTableModel extends ListBackedTableModel<BirForm2307Report> {

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
		    BirForm2307Report report = getItem(rowIndex);
			switch (columnIndex) {
			case REPORT_NUMBER_COLUMN_INDEX:
			    return report.getReportNumber().toString();
			case SUPPLIER_NAME_COLUMN_INDEX:
			    return report.getSupplier().getName();
			case FROM_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(report.getFromDate());
            case TO_DATE_COLUMN_INDEX:
                return FormatterUtil.formatDate(report.getToDate());
            case GENERATE_DATE_COLUMN_INDEX:
                return FormatterUtil.formatDateTime(report.getCreateDate());
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}

		@Override
		protected String[] getColumnNames() {
			return COLUMN_NAMES;
		}

	}
	
}
