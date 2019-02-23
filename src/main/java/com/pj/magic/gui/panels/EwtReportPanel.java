package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.excel.EwtReportExcelGenerator;
import com.pj.magic.gui.component.ExcelFileFilter;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicFileChooser;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.report.EwtReport;
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
public class EwtReportPanel extends StandardMagicPanel {

	@Autowired
	private SupplierService supplierService;
	
	@Autowired
	private ReportService reportService;
	
	private MagicComboBox<Supplier> supplierComboBox;
	private UtilCalendarModel fromDateModel;
	private UtilCalendarModel toDateModel;
	private JButton generateExcelButton;
	
	public EwtReportPanel() {
	    setTitle("BIR EWT Report");
	}
	
	@Override
	protected void initializeComponents() {
		supplierComboBox = new MagicComboBox<>();
		fromDateModel = new UtilCalendarModel();
		toDateModel = new UtilCalendarModel();
		
		generateExcelButton = new JButton("Generate Excel");
		generateExcelButton.addActionListener(e -> generateExcelReport());
	}

	public void updateDisplay() {
		supplierComboBox.setModel(
				ListUtil.toDefaultComboBoxModel(supplierService.getAllSuppliers(), true));
		supplierComboBox.setSelectedIndex(0);
		fromDateModel.setValue(null);
		toDateModel.setValue(null);
	}
	
	@Override
	protected void registerKeyBindings() {
		// none
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToReportsMenuPanel();
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
        mainPanel.add(Box.createGlue(), c);
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
		panel.add(generateExcelButton, c);
		
		return panel;
	}
	
	private EwtReport doGenerateReport() {
		EwtReportCriteria criteria = new EwtReportCriteria();
		criteria.setSupplier((Supplier)supplierComboBox.getSelectedItem());
		criteria.setFromDate(fromDateModel.getValue().getTime());
		criteria.setToDate(toDateModel.getValue().getTime());
		
		return reportService.generateEwtReport(criteria);
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
	
	private void generateExcelReport() {
		if (!validateFields()) {
			return;
		}
		
		MagicFileChooser saveFileChooser = createSaveFileChooser();
		if (!saveFileChooser.selectSaveFile(this)) {
			return;
		}
		
		EwtReport report;
		try {
	        report = doGenerateReport();
		} catch (Exception e) {
            log.error("Unable to generate EWT Report", e);
            showErrorMessage("Unable to generate EWT Report");
            return;
		}
		
		File file = saveFileChooser.getSelectedFile();
		EwtReportExcelGenerator excelGenerator = new EwtReportExcelGenerator();
		
		try (
			Workbook workbook = excelGenerator.generate(report);
			FileOutputStream out = new FileOutputStream(file);
		) {
			workbook.write(out);
		} catch (Exception e) {
		    log.error("Unable to generate EWT Excel Report ", e);
            showErrorMessage("Unable to generate EWT Excel Report");
			return;
		}
		
        if (confirm("EWT Report generated.\nDo you wish to open the file?")) {
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
				.append(" - ")
				.append(FormatterUtil.formatDateInFilename(new Date()))
				.append(".xlsx")
				.toString();
	}
	
}