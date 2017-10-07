package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.Icons;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.ExcelFileFilter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.report.CustomerSalesSummaryReport;
import com.pj.magic.model.report.CustomerSalesSummaryReportItem;
import com.pj.magic.service.ExcelService;
import com.pj.magic.service.ReportService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FileUtil;
import com.pj.magic.util.FormatterUtil;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class CustomerSalesSummaryReportPanel extends StandardMagicPanel {

	private static final int CUSTOMER_CODE_COLUMN_INDEX = 0;
	private static final int CUSTOMER_NAME_COLUMN_INDEX = 1;
	private static final int TOTAL_AMOUNT_COLUMN_INDEX = 2;
	private static final int TOTAL_COST_COLUMN_INDEX = 3;
	private static final int TOTAL_PROFIT_COLUMN_INDEX = 4;
	
	@Autowired private ReportService reportService;
	@Autowired private ExcelService excelService;
	
	private UtilCalendarModel fromDateModel;
	private UtilCalendarModel toDateModel;
	private JButton generateButton;
	private MagicListTable table;
	private CustomerSalesSummaryReportItemsTableModel tableModel;
	private JLabel totalNetAmountLabel;
	private JLabel totalCostLabel;
	private JLabel totalProfitLabel;
	private JFileChooser excelFileChooser;
	
	@Override
	protected void initializeComponents() {
		fromDateModel = new UtilCalendarModel();
		toDateModel = new UtilCalendarModel();
		
		generateButton = new JButton("Generate");
		generateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateReport();
			}
		});
		
		initializeTable();
		
		excelFileChooser = new JFileChooser();
		excelFileChooser.setCurrentDirectory(new File(FileUtil.getDesktopFolderPath()));
		excelFileChooser.setFileFilter(ExcelFileFilter.getInstance());
	}

	private void initializeTable() {
		tableModel = new CustomerSalesSummaryReportItemsTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(CUSTOMER_NAME_COLUMN_INDEX).setPreferredWidth(200);
	}

	private void generateReport() {
		if (fromDateModel.getValue() == null) {
			showErrorMessage("Tran. Date From must be specified");
			return;
		}
		if (toDateModel.getValue() == null) {
			showErrorMessage("Tran. Date To must be specified");
			return;
		}
		
		CustomerSalesSummaryReport report = reportService.getCustomerSalesSummaryReport(
				fromDateModel.getValue().getTime(), toDateModel.getValue().getTime());
		tableModel.setItems(report.getItems());
		if (report.getItems().isEmpty()) {
			showErrorMessage("No records found");
		}
		totalNetAmountLabel.setText(FormatterUtil.formatAmount(report.getTotalNetAmount()));
		totalCostLabel.setText(FormatterUtil.formatAmount(report.getTotalCost()));
		totalProfitLabel.setText(FormatterUtil.formatAmount(report.getTotalProfit()));
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Tran. Date From: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl datePanel = new JDatePanelImpl(fromDateModel);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Tran. Date To: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		datePanel = new JDatePanelImpl(toDateModel);
		datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0; // right space filler
		c.gridx = 6;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createVerticalFiller(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.gridwidth = 4;
		generateButton.setPreferredSize(new Dimension(160, 25));
		mainPanel.add(generateButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 30), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 7;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(scrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		c.gridwidth = 7;
		mainPanel.add(createTotalsPanel(), c);
	}

	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Net Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalNetAmountLabel = ComponentUtil.createRightLabel(100, "");
		panel.add(totalNetAmountLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(Box.createHorizontalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Cost:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalCostLabel = ComponentUtil.createRightLabel(100, "");
		panel.add(totalCostLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(140, "Total Profit:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalProfitLabel = ComponentUtil.createRightLabel(100, "");
		panel.add(totalProfitLabel, c);
		
		return panel;
	}

	@Override
	protected void registerKeyBindings() {
		// none
	}

	public void updateDisplay() {
		fromDateModel.setValue(Calendar.getInstance());
		toDateModel.setValue(Calendar.getInstance());
		tableModel.clear();
		totalNetAmountLabel.setText(null);
		totalCostLabel.setText(null);
		totalProfitLabel.setText(null);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton toExcelButton = new MagicToolBarButton(Icons.EXCEL, "Generate Excel spreadsheet");
		toExcelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateExcelSpreadsheet();
			}
		});
		toolBar.add(toExcelButton);
	}

	private void generateExcelSpreadsheet() {
		excelFileChooser.setSelectedFile(new File(generateDefaultSpreadsheetName() + ".xlsx"));
		
		int returnVal = excelFileChooser.showSaveDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		CustomerSalesSummaryReport report = new CustomerSalesSummaryReport();
		report.setItems(tableModel.getItems());
		
		try (
			Workbook workbook = excelService.generateSpreadsheet(report);
			FileOutputStream out = new FileOutputStream(excelFileChooser.getSelectedFile());
		) {
			workbook.write(out);
			showMessage("Excel spreadsheet generated successfully");
		} catch (IOException e) {
			showErrorMessage("Unexpected error during excel generation");
		}
	}

	private String generateDefaultSpreadsheetName() {
		return new StringBuilder()
			.append("customerSalesSummaryReport")
			.toString();
	}
	
	private class CustomerSalesSummaryReportItemsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Code", "Name", "Total Amount", "Total Cost", "Total Profit"};
		
		private List<CustomerSalesSummaryReportItem> items = new ArrayList<>();
		
		public void setItems(List<CustomerSalesSummaryReportItem> items) {
			this.items = items;
			fireTableDataChanged();
		}
		
		public List<CustomerSalesSummaryReportItem> getItems() {
			return items;
		}
		
		public void clear() {
			items.clear();
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			return items.size();
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
			CustomerSalesSummaryReportItem item = items.get(rowIndex);
			switch (columnIndex) {
			case CUSTOMER_CODE_COLUMN_INDEX:
				return item.getCustomer().getCode();
			case CUSTOMER_NAME_COLUMN_INDEX:
				return item.getCustomer().getName();
			case TOTAL_AMOUNT_COLUMN_INDEX:
				BigDecimal totalAmount = item.getTotalAmount();
				return (totalAmount != null) ? FormatterUtil.formatAmount(totalAmount) : null;
			case TOTAL_COST_COLUMN_INDEX:
				BigDecimal totalCost = item.getTotalCost();
				return (totalCost != null) ? FormatterUtil.formatAmount(totalCost) : null;
			case TOTAL_PROFIT_COLUMN_INDEX:
				BigDecimal totalProfit = item.getTotalProfit();
				return (totalProfit != null) ? FormatterUtil.formatAmount(totalProfit) : null;
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case TOTAL_AMOUNT_COLUMN_INDEX:
			case TOTAL_COST_COLUMN_INDEX:
			case TOTAL_PROFIT_COLUMN_INDEX:
				return Number.class;
			default:
				return String.class;
			}
		}
		
	}
	
}