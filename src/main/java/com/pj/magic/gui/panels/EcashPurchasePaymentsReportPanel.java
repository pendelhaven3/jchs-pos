package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.TableColumnModel;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicFileChooser;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.EcashReceiver;
import com.pj.magic.model.EcashType;
import com.pj.magic.model.PurchasePaymentEcashPayment;
import com.pj.magic.model.report.EcashPurchasePaymentsReport;
import com.pj.magic.model.search.PurchasePaymentEcashPaymentSearchCriteria;
import com.pj.magic.report.excel.EcashPurchasePaymentsReportExcelGenerator;
import com.pj.magic.repository.EcashReceiverRepository;
import com.pj.magic.service.PurchasePaymentService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.ExcelUtil;
import com.pj.magic.util.FileUtil;
import com.pj.magic.util.FormatterUtil;

import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class EcashPurchasePaymentsReportPanel extends StandardMagicPanel {

	private static final int REFERENCE_NUMBER_COLUMN_INDEX = 0;
	private static final int AMOUNT_COLUMN_INDEX = 1;
	private static final int ECASH_RECEIVER_NAME_COLUMN_INDEX = 2;
	private static final int PAID_DATE_COLUMN_INDEX = 3;
	private static final int PAID_BY_COLUMN_INDEX = 4;
	private static final int PURCHASE_PAYMENT_NUMBER_COLUMN_INDEX = 5;
	
	@Autowired private PurchasePaymentService purchasePaymentService;
	@Autowired private EcashReceiverRepository ecashReceiverDao;
	
	private UtilCalendarModel fromDateModel;
	private UtilCalendarModel toDateModel;
	private JComboBox<EcashReceiver> ecashReceiverComboBox;
	private JComboBox<EcashType> ecashTypeComboBox;
	private JButton generateButton;
	private MagicListTable table;
	private EcashPaymentsTableModel tableModel;
	private JLabel totalAmountLabel;
	
	@Override
	public String getTitle() {
		return "E-Cash Payments Report";
	}
	
	@Override
	protected void initializeComponents() {
		fromDateModel = new UtilCalendarModel();
		toDateModel = new UtilCalendarModel();
		
		ecashReceiverComboBox = new JComboBox<>();
		ecashTypeComboBox = new JComboBox<>();
		
		generateButton = new JButton("Generate");
		generateButton.addActionListener(e -> generateReport());
		
		initializeTable();
	}

	private void initializeTable() {
		tableModel = new EcashPaymentsTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(REFERENCE_NUMBER_COLUMN_INDEX).setPreferredWidth(100);
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
		List<PurchasePaymentEcashPayment> ecashPayments = retrieveReportItems();
		tableModel.setItems(ecashPayments);
		if (ecashPayments.isEmpty()) {
			showErrorMessage("No records found");
		}
		
		totalAmountLabel.setText(FormatterUtil.formatAmount(getTotalAmount(ecashPayments)));
	}

	private BigDecimal getTotalAmount(List<PurchasePaymentEcashPayment> ecashPayments) {
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (PurchasePaymentEcashPayment ecashPayment : ecashPayments) {
			totalAmount = totalAmount.add(ecashPayment.getAmount());
		}
		return totalAmount;
	}

	private List<PurchasePaymentEcashPayment> retrieveReportItems() {
		PurchasePaymentEcashPaymentSearchCriteria criteria = new PurchasePaymentEcashPaymentSearchCriteria();
		criteria.setEcashReceiver((EcashReceiver)ecashReceiverComboBox.getSelectedItem());
		criteria.setDateFrom(fromDateModel.getValue().getTime());
		criteria.setDateTo(toDateModel.getValue().getTime());
		criteria.setEcashType((EcashType)ecashTypeComboBox.getSelectedItem());
		
		return purchasePaymentService.searchPurchasePaymentEcashPayments(criteria);
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
		mainPanel.add(ComponentUtil.createLabel(170, "E-cash Receiver: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ecashReceiverComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(170, "Received Date From: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createDatePicker(fromDateModel), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(170, "Received Date To: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createDatePicker(toDateModel), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
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
		mainPanel.add(Box.createVerticalStrut(30), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 7;
		mainPanel.add(ComponentUtil.createScrollPane(table, 600, 100), c);
		
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
		panel.add(ComponentUtil.createLabel(150, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountLabel = ComponentUtil.createRightLabel(100, "");
		panel.add(totalAmountLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(Box.createHorizontalStrut(10), c);
		
		return panel;
	}

	@Override
	protected void registerKeyBindings() {
	}

	public void updateDisplay() {
		List<EcashReceiver> ecashReceivers = ecashReceiverDao.getAll();
		ecashReceiverComboBox.setModel(new DefaultComboBoxModel<>(
				ecashReceivers.toArray(new EcashReceiver[ecashReceivers.size()])));
		ecashReceiverComboBox.insertItemAt(null, 0);
		ecashReceiverComboBox.setSelectedItem(null);
		
		fromDateModel.setValue(Calendar.getInstance());
		toDateModel.setValue(Calendar.getInstance());
		tableModel.clear();
		totalAmountLabel.setText(null);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToReportsMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton excelButton = new MagicToolBarButton("excel", "Generate Excel", e -> generateExcel());
		toolBar.add(excelButton);
	}

	private void generateExcel() {
		MagicFileChooser excelFileChooser = FileUtil.createSaveFileChooser("E-Cash Purchase Payments Report.xlsx");
		if (!excelFileChooser.selectSaveFile(this)) {
			return;
		}
		
		EcashPurchasePaymentsReport report = new EcashPurchasePaymentsReport();
		report.setPayments(retrieveReportItems());
		
		try (
			Workbook workbook = new EcashPurchasePaymentsReportExcelGenerator().generate(report);
			FileOutputStream out = new FileOutputStream(excelFileChooser.getSelectedFile());
		) {
			workbook.write(out);
			showMessage("Excel spreadsheet generated successfully");
		} catch (IOException e) {
			showErrorMessage("Unexpected error during excel generation");
		}
		
		try {
			ExcelUtil.openExcelFile(excelFileChooser.getSelectedFile());
		} catch (IOException e) {
			showMessageForUnexpectedError();
		}
	}

	private class EcashPaymentsTableModel extends ListBackedTableModel<PurchasePaymentEcashPayment> {

		private final String[] columnNames =
			{"Reference No.", "Amount", "E-Cash Receiver", "Paid Date", "Paid By", "Purchase Payment No."};
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PurchasePaymentEcashPayment item = getItem(rowIndex);
			switch (columnIndex) {
			case REFERENCE_NUMBER_COLUMN_INDEX:
				return item.getReferenceNumber();
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(item.getAmount());
			case ECASH_RECEIVER_NAME_COLUMN_INDEX:
				return item.getEcashReceiver().getName();
			case PAID_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(item.getPaidDate());
			case PAID_BY_COLUMN_INDEX:
				return item.getPaidBy().getUsername();
			case PURCHASE_PAYMENT_NUMBER_COLUMN_INDEX:
				return String.valueOf(item.getParent().getPurchasePaymentNumber());
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case AMOUNT_COLUMN_INDEX:
				return Number.class;
			default:
				return Object.class;
			}
		}

		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}
		
	}
	
}