package com.pj.magic.gui.panels;

import static java.util.Calendar.*;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.excel.BirForm2307ReportExcelGenerator;
import com.pj.magic.gui.MagicFrame;
import com.pj.magic.gui.component.ExcelFileFilter;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicFileChooser;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.BirForm2307Report;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.EwtReportCriteria;
import com.pj.magic.service.ReportService;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.ExcelUtil;
import com.pj.magic.util.FileUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.ListUtil;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
@Slf4j
public class BirForm2307ReportPanel extends StandardMagicPanel {

	@Autowired
	private SupplierService supplierService;
	
	@Autowired
	private ReportService reportService;
	
	private MagicComboBox<Supplier> supplierComboBox;
	private UtilCalendarModel fromDateModel;
	private UtilCalendarModel toDateModel;
	private JButton generateExcelButton;
    private JButton regenerateExcelButton;
	private JLabel reportNumberLabel;
    private JLabel createDateLabel;
    private JLabel createdByLabel;
    
    private MagicListTable reportTable;
    private BirForm2307ReportTableModel tableModel;
	
	private BirForm2307Report report;
	
	public BirForm2307ReportPanel() {
	    setTitle("BIR Form 2307 Report");
	}
	
	@Override
	protected void initializeComponents() {
		supplierComboBox = new MagicComboBox<>();
		fromDateModel = new UtilCalendarModel();
		toDateModel = new UtilCalendarModel();
		
		generateExcelButton = new JButton("Generate Excel");
		generateExcelButton.addActionListener(e -> generateExcelReport());
		
        regenerateExcelButton = new JButton("Regenerate Excel");
        regenerateExcelButton.addActionListener(e -> regenerateExcelReport());
        
		reportNumberLabel = new JLabel();
		createDateLabel = new JLabel();
		createdByLabel = new JLabel();
		
		tableModel = new BirForm2307ReportTableModel();
		reportTable = new MagicListTable(tableModel);
	}

	public void updateDisplay() {
	    report = null;
	    
	    generateExcelButton.setText("Generate Excel");
        regenerateExcelButton.setEnabled(false);
		supplierComboBox.setModel(ListUtil.toDefaultComboBoxModel(supplierService.getAllSuppliers(), true));
		supplierComboBox.setSelectedIndex(0);
		fromDateModel.setValue(null);
		toDateModel.setValue(null);
        reportNumberLabel.setText(null);
        tableModel.clear();
        createDateLabel.setText(null);
        createdByLabel.setText(null);
	}
	
    public void updateDisplay(BirForm2307Report report) {
        this.report = report = reportService.getBirForm2307Report(report.getId());
        
        generateExcelButton.setText("Download Excel");
        regenerateExcelButton.setEnabled(true);
        supplierComboBox.setModel(ListUtil.toDefaultComboBoxModel(supplierService.getAllSuppliers(), true));
        supplierComboBox.setSelectedItem(report.getSupplier());
        fromDateModel.setValue(DateUtils.toCalendar(report.getFromDate()));
        toDateModel.setValue(DateUtils.toCalendar(report.getToDate()));
        reportNumberLabel.setText(report.getReportNumber().toString());
        tableModel.setItems(Arrays.asList(report));
        createDateLabel.setText(FormatterUtil.formatDateTime(report.getCreateDate()));
    }
    
	@Override
	protected void registerKeyBindings() {
		// none
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchPanel(MagicFrame.BIR_FORM_2307_REPORT_LIST_PANEL);
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets.top = 10;
		mainPanel.add(createControlsPanel(), c);
		
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.insets.top = 10;
        mainPanel.add(createTablePanel(), c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.insets.top = 5;
        mainPanel.add(createFooterPanel(), c);
        
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

	private JPanel createControlsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Supplier:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(supplierComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "From Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createDatePicker(fromDateModel), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "To Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createDatePicker(toDateModel), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.insets.top = 10;
        c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createGenericPanel(generateExcelButton, Box.createHorizontalStrut(10), regenerateExcelButton), c);
		
		return panel;
	}
	
    private JPanel createTablePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        
        int currentRow = 0;
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = currentRow;
        c.anchor = GridBagConstraints.WEST;
        c.insets.left = 50;
        panel.add(ComponentUtil.createLabel(90, "Report No:"), c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = currentRow;
        c.anchor = GridBagConstraints.WEST;
        reportNumberLabel.setPreferredSize(new Dimension(100, 30));
        panel.add(reportNumberLabel, c);
        
        currentRow++;
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = currentRow;
        c.gridwidth = 2;
        c.weightx = c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets.top = 10;
        panel.add(ComponentUtil.createScrollPane(reportTable), c);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        
        int currentRow = 0;
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = currentRow;
        c.anchor = GridBagConstraints.WEST;
        panel.add(ComponentUtil.createLabel(120, "Create Date:"), c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = currentRow;
        c.anchor = GridBagConstraints.WEST;
        createDateLabel.setPreferredSize(new Dimension(150, 30));
        panel.add(createDateLabel, c);
        
        return panel;
    }
    
	private BirForm2307Report doGenerateReport() {
		EwtReportCriteria criteria = new EwtReportCriteria();
		criteria.setSupplier((Supplier)supplierComboBox.getSelectedItem());
		criteria.setFromDate(fromDateModel.getValue().getTime());
		criteria.setToDate(toDateModel.getValue().getTime());
		
		return reportService.generateBirForm2307Report(criteria);
	}

    private BirForm2307Report doRegenerateReport() {
        EwtReportCriteria criteria = new EwtReportCriteria();
        criteria.setSupplier((Supplier)supplierComboBox.getSelectedItem());
        criteria.setFromDate(fromDateModel.getValue().getTime());
        criteria.setToDate(toDateModel.getValue().getTime());
        
        return reportService.regenerateBirForm2307Report(report, criteria);
    }

	private boolean validateFields() {
		if (isSupplierNotSpecified()) {
			showErrorMessage("Supplier must be specified");
			supplierComboBox.requestFocus();
			return false;
		}
		
		if (isFromDateNotSpecified()) {
			showErrorMessage("From Date must be specified");
			return false;
		}
		
		if (isToDateNotSpecified()) {
			showErrorMessage("To Date must be specified");
			return false;
		}
		
		if (!isFromDateLessThanToDate()) {
            showErrorMessage("From Date must be less than To Date");
            return false;
		}
		
		if (!isSameQuarter()) {
            showErrorMessage("From Date and To Date must be within same quarter");
            return false;
		}
		
		return true;
	}

    private boolean isSupplierNotSpecified() {
		return supplierComboBox.getSelectedItem() == null;
	}

	private boolean isFromDateNotSpecified() {
		return fromDateModel.getValue() == null;
	}

	private boolean isToDateNotSpecified() {
		return toDateModel.getValue() == null;
	}
	
	private boolean isFromDateLessThanToDate() {
        Date from = fromDateModel.getValue().getTime();
        Date to = toDateModel.getValue().getTime();
        
        return from.compareTo(to) <= 0;
	}
	
    private boolean isSameQuarter() {
        Calendar from = fromDateModel.getValue();
        Calendar to = toDateModel.getValue();
        
        int fromYear = from.get(YEAR);
        int toYear = to.get(YEAR);
        
        if (fromYear != toYear) {
            return false;
        }
        
        int fromMonth = from.get(MONTH);
        int toMonth = to.get(MONTH);
        
        if (fromMonth >= JANUARY && fromMonth <= MARCH && toMonth >= fromMonth && toMonth <= MARCH) {
            return true;
        }
        
        if (fromMonth >= APRIL && fromMonth <= JUNE && toMonth >= fromMonth && toMonth <= JUNE) {
            return true;
        }
        
        if (fromMonth >= JULY && fromMonth <= SEPTEMBER && toMonth >= fromMonth && toMonth <= SEPTEMBER) {
            return true;
        }
        
        if (fromMonth >= OCTOBER && fromMonth <= DECEMBER && toMonth >= fromMonth && toMonth <= DECEMBER) {
            return true;
        }
        
        return false;
    }
	
	private void generateExcelReport() {
	    if (this.report == null) {
	        if (!validateFields()) {
	            return;
	        }
	    }
		
		MagicFileChooser saveFileChooser = createSaveFileChooser();
		if (!saveFileChooser.selectSaveFile(this)) {
			return;
		}
		
		BirForm2307Report report = this.report;
		if (report == null) {
	        try {
	            report = doGenerateReport();
	        } catch (Exception e) {
	            log.error("Unable to generate Form 2307 Report", e);
	            showErrorMessage("Unable to generate Form 2307 Report");
	            return;
	        }
		}
		
		File file = saveFileChooser.getSelectedFile();
		BirForm2307ReportExcelGenerator excelGenerator = new BirForm2307ReportExcelGenerator();
		
		try (
			Workbook workbook = excelGenerator.generate(report);
			FileOutputStream out = new FileOutputStream(file);
		) {
			workbook.write(out);
		} catch (Exception e) {
		    log.error("Unable to generate Form 2307 Excel Report ", e);
            showErrorMessage("Unable to generate Form 2307 Excel Report");
			return;
		}
		
        updateDisplay(report);
        
        if (confirm("Excel file generated.\nDo you wish to open the file?")) {
            openExcelFile(file);
        }
	}

    private void regenerateExcelReport() {
        if (!validateFields()) {
            return;
        }
        
        if (!confirm("Regenerate Form 2307?\nReport No. will still be retained.")) {
            return;
        }
        
        MagicFileChooser saveFileChooser = createSaveFileChooser();
        if (!saveFileChooser.selectSaveFile(this)) {
            return;
        }
        
        BirForm2307Report report;
        try {
            report = doRegenerateReport();
        } catch (Exception e) {
            log.error("Unable to generate Form 2307 Report", e);
            showErrorMessage("Unable to generate Form 2307 Report");
            return;
        }
        
        File file = saveFileChooser.getSelectedFile();
        BirForm2307ReportExcelGenerator excelGenerator = new BirForm2307ReportExcelGenerator();
        
        try (
            Workbook workbook = excelGenerator.generate(report);
            FileOutputStream out = new FileOutputStream(file);
        ) {
            workbook.write(out);
        } catch (Exception e) {
            log.error("Unable to generate Form 2307 Excel Report ", e);
            showErrorMessage("Unable to generate Form 2307 Excel Report");
            return;
        }
        
        updateDisplay(report);
        
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

	private MagicFileChooser createSaveFileChooser() {
		MagicFileChooser fileChooser = new MagicFileChooser();
		fileChooser.setCurrentDirectory(FileUtil.getDesktopFolderPathAsFile());
		fileChooser.setFileFilter(ExcelFileFilter.getInstance());
		fileChooser.setSelectedFile(new File(constructDefaultExcelFileName()));
		return fileChooser;
	}

	private String constructDefaultExcelFileName() {
		Supplier supplier = (Supplier)supplierComboBox.getSelectedItem();
		
		return new StringBuilder()
				.append(supplier.getName())
				.append(" - FORM 2307 - ")
				.append(FormatterUtil.formatDateInFilename(new Date()))
				.append(".xls")
				.toString();
	}
	
	private static final String[] COLUMN_NAMES = {"Month 1 Net Amount", "Month 2 Net Amount", "Month 3 Net Amount", "Total EWT Amount"};
	private static final int MONTH_1_NET_AMOUNT_COLUMN_INDEX = 0;
    private static final int MONTH_2_NET_AMOUNT_COLUMN_INDEX = 1;
    private static final int MONTH_3_NET_AMOUNT_COLUMN_INDEX = 2;
    private static final int TOTAL_EWT_AMOUNT_COLUMN_INDEX = 3;
	
	private class BirForm2307ReportTableModel extends ListBackedTableModel<BirForm2307Report> {

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            BirForm2307Report report = getItems().get(0);
            
            switch (columnIndex) {
            case MONTH_1_NET_AMOUNT_COLUMN_INDEX:
                return FormatterUtil.formatAmount(report.getMonth1NetAmount());
            case MONTH_2_NET_AMOUNT_COLUMN_INDEX:
                return FormatterUtil.formatAmount(report.getMonth2NetAmount());
            case MONTH_3_NET_AMOUNT_COLUMN_INDEX:
                return FormatterUtil.formatAmount(report.getMonth3NetAmount());
            case TOTAL_EWT_AMOUNT_COLUMN_INDEX:
                return FormatterUtil.formatAmount(report.getTotalEwtAmount());
            default:
                throw new RuntimeException("Invalid column index: " + columnIndex);
            }
        }

        @Override
        protected String[] getColumnNames() {
            return COLUMN_NAMES;
        }
	    
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Number.class;
        }
        
	}
	
}