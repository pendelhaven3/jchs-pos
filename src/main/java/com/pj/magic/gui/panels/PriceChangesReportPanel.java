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
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicFileChooser;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.ProductPriceHistory;
import com.pj.magic.model.Unit;
import com.pj.magic.model.report.PriceChangesReport;
import com.pj.magic.model.search.ProductPriceHistorySearchCriteria;
import com.pj.magic.service.PricingSchemeService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.PrintServiceImpl;
import com.pj.magic.service.ProductPriceService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.ExcelUtil;
import com.pj.magic.util.FileUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.ListUtil;
import com.pj.magic.util.PriceChangesExcelReportGenerator;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class PriceChangesReportPanel extends StandardMagicPanel {

	private static final int PRODUCT_COLUMN_INDEX = 0;
	private static final int EFFECTIVE_DATE_COLUMN_INDEX = 1;
	private static final int UNIT_PRICE_CASE_COLUMN_INDEX = 2;
	private static final int UNIT_PRICE_TIE_COLUMN_INDEX = 3;
	private static final int UNIT_PRICE_CARTON_COLUMN_INDEX = 4;
	private static final int UNIT_PRICE_DOZEN_COLUMN_INDEX = 5;
	private static final int UNIT_PRICE_PIECES_COLUMN_INDEX = 6;
	
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	@Autowired private ProductPriceService productPriceService;
	@Autowired private PricingSchemeService pricingSchemeService;
	
	private MagicListTable table;
	private PriceChangesTableModel tableModel;
	private UtilCalendarModel fromDateModel;
	private UtilCalendarModel toDateModel;
	private MagicComboBox<PricingScheme> pricingSchemeComboBox;
	private JButton generateButton;
	
	@Override
	protected void initializeComponents() {
		fromDateModel = new UtilCalendarModel();
		toDateModel = new UtilCalendarModel();
		pricingSchemeComboBox = new MagicComboBox<>();
		
		generateButton = new JButton("Generate Report");
		generateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateReport();
			}
		});
		
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void generateReport() {
		if (!validateCriteria()) {
			return;
		}
		
		List<ProductPriceHistory> items = retrieveReportItems();
		tableModel.setItems(items);
		if (!items.isEmpty()) {
			table.changeSelection(0, 0);
		} else {
			showErrorMessage("No matching records");
		}
	}

	private List<ProductPriceHistory> retrieveReportItems() {
		ProductPriceHistorySearchCriteria criteria = new ProductPriceHistorySearchCriteria();
		criteria.setFromDate(fromDateModel.getValue().getTime());
		criteria.setToDate(toDateModel.getValue().getTime());
//		criteria.setFromDate(DateUtil.toDate("02/01/2015"));
//		criteria.setToDate(DateUtil.toDate("02/28/2015"));
		criteria.setPricingScheme((PricingScheme)pricingSchemeComboBox.getSelectedItem());
		
		return productPriceService.searchProductPriceHistories(criteria);
	}
	
	private PriceChangesReport createPriceChangesReport() {
		Date fromDate = fromDateModel.getValue().getTime();
		Date toDate = toDateModel.getValue().getTime();
		
		PriceChangesReport report = new PriceChangesReport();
		report.setFromDate(fromDate);
		report.setToDate(toDate);
		report.setItems(retrieveReportItems());
		return report;
	}

	private void initializeTable() {
		tableModel = new PriceChangesTableModel();
		table = new MagicListTable(tableModel);
		
		table.getColumnModel().getColumn(PRODUCT_COLUMN_INDEX).setPreferredWidth(300);
		table.getColumnModel().getColumn(EFFECTIVE_DATE_COLUMN_INDEX).setPreferredWidth(150);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToInventoryMenuPanel();
	}
	
	public void updateDisplay() {
		Calendar cal = Calendar.getInstance();
		fromDateModel.setValue(cal);
		toDateModel.setValue(cal);
		tableModel.clear();
		
		pricingSchemeComboBox.setModel(
				ListUtil.toDefaultComboBoxModel(pricingSchemeService.getAllPricingSchemes()));
		pricingSchemeComboBox.setSelectedItem(0);
	}

	@Override
	protected void registerKeyBindings() {
		// none
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
		mainPanel.add(ComponentUtil.createLabel(120, "From Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl fromDatePanel = new JDatePanelImpl(fromDateModel);
		JDatePickerImpl datePicker = new JDatePickerImpl(fromDatePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(30), c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "To Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl toDatePanel = new JDatePanelImpl(toDateModel);
		JDatePickerImpl toDatePicker = new JDatePickerImpl(toDatePanel, new DatePickerFormatter());
		mainPanel.add(toDatePicker, c);

		c = new GridBagConstraints();
		c.gridx = 6;
		c.gridy = currentRow;
		c.weightx = 1.0;
		mainPanel.add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Pricing Scheme:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(pricingSchemeComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.WEST;
		c.insets.top = 20;
		generateButton.setPreferredSize(new Dimension(160, 25));
		mainPanel.add(generateButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 7;
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(600, 200));
		mainPanel.add(scrollPane, c);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreview();
			}
		});
		toolBar.add(printPreviewButton);
		
		JButton printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print();
			}
		});
		toolBar.add(printButton);
		
		JButton generateExcelButton = new MagicToolBarButton("excel", "Generate Excel");
		generateExcelButton.addActionListener(e -> generateExcel());
		toolBar.add(generateExcelButton);
	}

	private void print() {
		if (!validateCriteria()) {
			return;
		}
		printService.print(createPriceChangesReport());
	}

	private boolean validateCriteria() {
		if (fromDateModel.getValue() == null) {
			showErrorMessage("From Date must be specified");
			return false;
		}
		
		if (toDateModel.getValue() == null) {
			showErrorMessage("To Date must be specified");
			return false;
		}
		
		return true;
	}

	private void printPreview() {
		if (!validateCriteria()) {
			return;
		}
		
		PriceChangesReport report = createPriceChangesReport();
		printPreviewDialog.updateDisplay(printService.generateReportAsString(report));
		printPreviewDialog.setColumnsPerLine(PrintServiceImpl.PRICE_CHANGES_REPORT_CHARACTERS_PER_LINE);
		printPreviewDialog.setUseCondensedFontForPrinting(true);
		printPreviewDialog.setVisible(true);
	}

	private void generateExcel() {
		if (!validateCriteria()) {
			return;
		}
		
		MagicFileChooser saveFileChooser = FileUtil.createSaveFileChooser(constructDefaultExcelFileName());
		if (!saveFileChooser.selectSaveFile(this)) {
			return;
		}
		
		PriceChangesReport report = createPriceChangesReport();
		tableModel.setItems(report.getItems());
		
		File file = saveFileChooser.getSelectedFile();
		
		try (
			Workbook workbook = PriceChangesExcelReportGenerator.generate(report);
			FileOutputStream out = new FileOutputStream(file);
		) {
			workbook.write(out);
		} catch (IOException e) {
			showMessageForUnexpectedError();
			return;
		}
		
		if (confirm("Excel file generated.\nDo you wish to open the file?")) {
			openExcelFile(file);
		}
	}
	
	private void openExcelFile(File file) {
		try {
			ExcelUtil.openExcelFile(file);
		} catch (IOException e) {
			showMessageForUnexpectedError();
		}
	}
	
	private String constructDefaultExcelFileName() {
		Date from = fromDateModel.getValue().getTime();
		Date to = toDateModel.getValue().getTime();
		
		if (from.equals(to)) {
			return new StringBuilder()
					.append("Price Changes Report - ")
					.append(FormatterUtil.formatDateInFilename(from))
					.append(".xlsx")
					.toString();
		} else {
			return new StringBuilder()
					.append("Price Changes Report ")
					.append(FormatterUtil.formatDateInFilename(from))
					.append(" - ")
					.append(FormatterUtil.formatDateInFilename(to))
					.append(".xlsx")
					.toString();
		}
	}
	
	private class PriceChangesTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Product", "Effective Date/Time", "CSE", "TIE", "CTN", "DOZ", "PCS"};
		
		private List<ProductPriceHistory> items = new ArrayList<>();
		
		public void setItems(List<ProductPriceHistory> items) {
			this.items = items;
			fireTableDataChanged();
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
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case UNIT_PRICE_CASE_COLUMN_INDEX:
			case UNIT_PRICE_TIE_COLUMN_INDEX:
			case UNIT_PRICE_CARTON_COLUMN_INDEX:
			case UNIT_PRICE_DOZEN_COLUMN_INDEX:
			case UNIT_PRICE_PIECES_COLUMN_INDEX:
				return Number.class;
			default:
				return String.class;
			}
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ProductPriceHistory item = items.get(rowIndex);
			switch (columnIndex) {
			case PRODUCT_COLUMN_INDEX:
				return item.getProduct().getDescription();
			case EFFECTIVE_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDateTime(item.getUpdateDate());
			case UNIT_PRICE_CASE_COLUMN_INDEX:
				BigDecimal unitPriceCase = item.getUnitPrice(Unit.CASE);
				return (unitPriceCase != null) ? FormatterUtil.formatAmount(unitPriceCase) : null;
			case UNIT_PRICE_TIE_COLUMN_INDEX:
				BigDecimal unitPriceTie = item.getActiveUnitPrice(Unit.TIE);
				return (unitPriceTie != null) ? FormatterUtil.formatAmount(unitPriceTie) : null;
			case UNIT_PRICE_CARTON_COLUMN_INDEX:
				BigDecimal unitPriceCarton = item.getActiveUnitPrice(Unit.CARTON);
				return (unitPriceCarton != null) ? FormatterUtil.formatAmount(unitPriceCarton) : null;
			case UNIT_PRICE_DOZEN_COLUMN_INDEX:
				BigDecimal unitPriceDozen = item.getActiveUnitPrice(Unit.DOZEN);
				return (unitPriceDozen != null) ? FormatterUtil.formatAmount(unitPriceDozen) : null;
			case UNIT_PRICE_PIECES_COLUMN_INDEX:
				BigDecimal unitPricePieces = item.getActiveUnitPrice(Unit.PIECES);
				return (unitPricePieces != null) ? FormatterUtil.formatAmount(unitPricePieces) : null;
			default:
				throw new RuntimeException("Fetcing invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
	}
	
}