package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.PurchasePaymentBankTransfer;
import com.pj.magic.model.PurchasePaymentCheckPayment;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.model.report.DisbursementReport;
import com.pj.magic.model.search.PurchasePaymentBankTransferSearchCriteria;
import com.pj.magic.model.search.PurchasePaymentCashPaymentSearchCriteria;
import com.pj.magic.model.search.PurchasePaymentCheckPaymentSearchCriteria;
import com.pj.magic.model.search.PurchasePaymentCreditCardPaymentSearchCriteria;
import com.pj.magic.service.PurchasePaymentService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class DisbursementReportPanel extends StandardMagicPanel {

	@Autowired private PurchasePaymentService purchasePaymentService;
	
	private MagicListTable checkPaymentsTable;
	private MagicListTable creditCardPaymentsTable;
	private MagicListTable bankTransfersTable;
	private CheckPaymentsTableModel checkPaymentsTableModel;
	private CreditCardPaymentsTableModel creditCardPaymentsTableModel;
	private BankTransfersTableModel bankTransfersTableModel;
	private UtilCalendarModel fromDateModel;
	private UtilCalendarModel toDateModel;
	private JButton generateButton;
	private JLabel totalCashPaymentsAmountLabel;
	private JLabel totalCreditCardPaymentsLabel;
	private JLabel totalCreditCardPaymentsAmountLabel;
	private JLabel totalCheckPaymentsLabel;
	private JLabel totalCheckPaymentsAmountLabel;
	private JLabel totalBankTransfersLabel;
	private JLabel totalBankTransfersAmountLabel;
	private JLabel totalAmountLabel;
	private JTabbedPane tabbedPane;
	
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
		
		initializeTables();
	}

	private void generateReport() {
		if (fromDateModel.getValue() == null) {
			showErrorMessage("From Date must be specified");
			return;
		}
		if (toDateModel.getValue() == null) {
			showErrorMessage("To Date must be specified");
			return;
		}
		
		DisbursementReport report = doGenerateReport();
		checkPaymentsTableModel.setCheckPayments(report.getCheckPayments());
		creditCardPaymentsTableModel.setCreditCardPayments(report.getCreditCardPayments());
		bankTransfersTableModel.setBankTransfers(report.getBankTransfers());
		
		totalCashPaymentsAmountLabel.setText(
				FormatterUtil.formatAmount(report.getTotalCashPaymentsAmount()));
		totalCheckPaymentsLabel.setText(String.valueOf(report.getCheckPayments().size()));
		totalCheckPaymentsAmountLabel.setText(
				FormatterUtil.formatAmount(report.getTotalCheckPaymentsAmount()));
		totalCreditCardPaymentsLabel.setText(String.valueOf(report.getCreditCardPayments().size()));
		totalCreditCardPaymentsAmountLabel.setText(
				FormatterUtil.formatAmount(report.getTotalCreditCardPaymentsAmount()));
		totalBankTransfersLabel.setText(String.valueOf(report.getBankTransfers().size()));
		totalBankTransfersAmountLabel.setText(
				FormatterUtil.formatAmount(report.getTotalBankTransfersAmount()));
		totalAmountLabel.setText(FormatterUtil.formatAmount(report.getTotalAmount()));
	}

	private DisbursementReport doGenerateReport() {
		DisbursementReport report = new DisbursementReport();
		report.setFromDate(fromDateModel.getValue().getTime());
		report.setToDate(toDateModel.getValue().getTime());
		
		PurchasePaymentCashPaymentSearchCriteria cashPaymentCriteria = 
				new PurchasePaymentCashPaymentSearchCriteria();
		cashPaymentCriteria.setPosted(true);
		cashPaymentCriteria.setFromDate(report.getFromDate());
		cashPaymentCriteria.setToDate(report.getToDate());
		report.setCashPayments(purchasePaymentService.searchCashPayments(cashPaymentCriteria));
		
		PurchasePaymentBankTransferSearchCriteria bankTransferCriteria = 
				new PurchasePaymentBankTransferSearchCriteria();
		bankTransferCriteria.setPosted(true);
		bankTransferCriteria.setFromDate(report.getFromDate());
		bankTransferCriteria.setToDate(report.getToDate());
		report.setBankTransfers(purchasePaymentService.searchBankTransfers(bankTransferCriteria));
		
		PurchasePaymentCheckPaymentSearchCriteria checkPaymentCriteria = 
				new PurchasePaymentCheckPaymentSearchCriteria();
		checkPaymentCriteria.setPosted(true);
		checkPaymentCriteria.setFromDate(report.getFromDate());
		checkPaymentCriteria.setToDate(report.getToDate());
		report.setCheckPayments(purchasePaymentService.searchCheckPayments(checkPaymentCriteria));
		
		PurchasePaymentCreditCardPaymentSearchCriteria creditCardCriteria = 
				new PurchasePaymentCreditCardPaymentSearchCriteria();
		creditCardCriteria.setPosted(true);
		creditCardCriteria.setFromDate(report.getFromDate());
		creditCardCriteria.setToDate(report.getToDate());
		report.setCreditCardPayments(purchasePaymentService.searchCreditCardPayments(creditCardCriteria));
		
		return report;
	}

	private void initializeTables() {
		checkPaymentsTableModel = new CheckPaymentsTableModel();
		checkPaymentsTable = new MagicListTable(checkPaymentsTableModel);
		
		creditCardPaymentsTableModel = new CreditCardPaymentsTableModel();
		creditCardPaymentsTable = new MagicListTable(creditCardPaymentsTableModel);
		
		bankTransfersTableModel = new BankTransfersTableModel();
		bankTransfersTable = new MagicListTable(bankTransfersTableModel);
	}

	@Override
	protected void registerKeyBindings() {
		// none
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToReportsMenuPanel();
	}
	
	public void updateDisplay() {
		Calendar now = Calendar.getInstance();
		fromDateModel.setValue(now);
		toDateModel.setValue(now);
		
		tabbedPane.setSelectedIndex(0);
		
		creditCardPaymentsTableModel.clearDisplay();
		checkPaymentsTableModel.clearDisplay();
		bankTransfersTableModel.clearDisplay();
		
		totalCashPaymentsAmountLabel.setText("-");
		totalCreditCardPaymentsLabel.setText("-");
		totalCreditCardPaymentsAmountLabel.setText("-");
		totalCheckPaymentsLabel.setText("-");
		totalCheckPaymentsAmountLabel.setText("-");
		totalBankTransfersLabel.setText("-");
		totalBankTransfersAmountLabel.setText("-");
		totalAmountLabel.setText("-");
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
		JDatePickerImpl fromDatePicker = new JDatePickerImpl(fromDatePanel, new DatePickerFormatter());
		mainPanel.add(fromDatePicker, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(30), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(generateButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "To Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl toDatePanel = new JDatePanelImpl(toDateModel);
		JDatePickerImpl toDatePicker = new JDatePickerImpl(toDatePanel, new DatePickerFormatter());
		mainPanel.add(toDatePicker, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		mainPanel.add(createTotalsPanel(), c);
		
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
		c.gridwidth = 5;
		
		tabbedPane = createTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(600, 250));
		mainPanel.add(tabbedPane, c);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

	private JTabbedPane createTabbedPane() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Check Payments", createCheckPaymentsPanel());
		tabbedPane.addTab("Credit Card Payments", createCreditCardPaymentsPanel());
		tabbedPane.addTab("Bank Transfers", createBankTransfersPanel());
		return tabbedPane;
	}

	private JPanel createBankTransfersPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane scrollPane = new JScrollPane(bankTransfersTable);
		scrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(scrollPane, c);
		
		return panel;
	}

	private JPanel createCreditCardPaymentsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane scrollPane = new JScrollPane(creditCardPaymentsTable);
		scrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(scrollPane, c);
		
		return panel;
	}

	private JPanel createCheckPaymentsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		JScrollPane scrollPane = new JScrollPane(checkPaymentsTable);
		scrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(scrollPane, c);
		
		return panel;
	}
	
	private JPanel createTotalsPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;

		currentRow++;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(300, "Total Cash Payments Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalCashPaymentsAmountLabel = ComponentUtil.createRightLabel(120);
		mainPanel.add(totalCashPaymentsAmountLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(200, "Total Check Payments:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalCheckPaymentsLabel = ComponentUtil.createRightLabel(50);
		mainPanel.add(totalCheckPaymentsLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(300, "Total Check Payments Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalCheckPaymentsAmountLabel = ComponentUtil.createRightLabel(120);
		mainPanel.add(totalCheckPaymentsAmountLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(200, "Total Credit Card Payments:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalCreditCardPaymentsLabel = ComponentUtil.createRightLabel(50);
		mainPanel.add(totalCreditCardPaymentsLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(300, "Total Credit Card Payments Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalCreditCardPaymentsAmountLabel = ComponentUtil.createRightLabel(120);
		mainPanel.add(totalCreditCardPaymentsAmountLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(200, "Total Bank Transfers:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalBankTransfersLabel = ComponentUtil.createRightLabel(50);
		mainPanel.add(totalBankTransfersLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(300, "Total Bank Transfers Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalBankTransfersAmountLabel = ComponentUtil.createRightLabel(120);
		mainPanel.add(totalBankTransfersAmountLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountLabel = ComponentUtil.createRightLabel(120);
		mainPanel.add(totalAmountLabel, c);
		
		return mainPanel;
	}
	
	private class CheckPaymentsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Bank", "Check Date", "Check No.", "Amount"};
		private final int BANK_COLUMN_INDEX = 0;
		private final int CHECK_DATE_COLUMN_INDEX = 1;
		private final int CHECK_NUMBER_COLUMN_INDEX = 2;
		private final int AMOUNT_COLUMN_INDEX = 3;
		
		private List<PurchasePaymentCheckPayment> checkPayments = new ArrayList<>();
		
		public void setCheckPayments(List<PurchasePaymentCheckPayment> checkPayments) {
			this.checkPayments = checkPayments;
			fireTableDataChanged();
		}
		
		public void clearDisplay() {
			checkPayments.clear();
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			return checkPayments.size();
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
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == AMOUNT_COLUMN_INDEX) {
				return Number.class;
			} else {
				return String.class;
			}
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PurchasePaymentCheckPayment checkPayment = checkPayments.get(rowIndex);
			switch (columnIndex) {
			case BANK_COLUMN_INDEX:
				return checkPayment.getBank();
			case CHECK_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(checkPayment.getCheckDate());
			case CHECK_NUMBER_COLUMN_INDEX:
				return checkPayment.getCheckNumber();
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(checkPayment.getAmount());
			default:
				throw new RuntimeException("Error fetching invalid column index: " + columnIndex);
			}
		}
		
	}
	
	private class BankTransfersTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Bank", "Amount", "Reference Number", "Transfer Date"};
		private final int BANK_COLUMN_INDEX = 0;
		private final int AMOUNT_COLUMN_INDEX = 1;
		private final int REFERENCE_NUMBER_COLUMN_INDEX = 2;
		private final int TRANSFER_DATE_COLUMN_INDEX = 3;
		
		private List<PurchasePaymentBankTransfer> bankTransfers = new ArrayList<>();
		
		public void setBankTransfers(List<PurchasePaymentBankTransfer> bankTransfers) {
			this.bankTransfers = bankTransfers;
			fireTableDataChanged();
		}
		
		public void clearDisplay() {
			bankTransfers.clear();
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			return bankTransfers.size();
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
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == AMOUNT_COLUMN_INDEX) {
				return Number.class;
			} else {
				return String.class;
			}
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PurchasePaymentBankTransfer bankTransfer = bankTransfers.get(rowIndex);
			switch (columnIndex) {
			case BANK_COLUMN_INDEX:
				return bankTransfer.getBank();
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(bankTransfer.getAmount());
			case REFERENCE_NUMBER_COLUMN_INDEX:
				return bankTransfer.getReferenceNumber();
			case TRANSFER_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(bankTransfer.getTransferDate());
			default:
				throw new RuntimeException("Error fetching invalid column index: " + columnIndex);
			}
		}
		
	}
	
	private class CreditCardPaymentsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Amount", "Credit Card", "Transaction Date", "Approval Code"};
		private final int AMOUNT_COLUMN_INDEX = 0;
		private final int CREDIT_CARD_COLUMN_INDEX = 1;
		private final int TRANSACTION_DATE_COLUMN_INDEX = 2;
		private final int APPROVAL_CODE_NUMBER_COLUMN_INDEX = 3;
		
		private List<PurchasePaymentCreditCardPayment> creditCardPayments = new ArrayList<>();
		
		public void setCreditCardPayments(List<PurchasePaymentCreditCardPayment> creditCardPayments) {
			this.creditCardPayments = creditCardPayments;
			fireTableDataChanged();
		}
		
		public void clearDisplay() {
			creditCardPayments.clear();
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			return creditCardPayments.size();
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
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == AMOUNT_COLUMN_INDEX) {
				return Number.class;
			} else {
				return String.class;
			}
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PurchasePaymentCreditCardPayment creditCardPayment = creditCardPayments.get(rowIndex);
			switch (columnIndex) {
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(creditCardPayment.getAmount());
			case CREDIT_CARD_COLUMN_INDEX:
				return creditCardPayment.getCreditCard().toString();
			case TRANSACTION_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(creditCardPayment.getTransactionDate());
			case APPROVAL_CODE_NUMBER_COLUMN_INDEX:
				return creditCardPayment.getApprovalCode();
			default:
				throw new RuntimeException("Error fetching invalid column index: " + columnIndex);
			}
		}
		
	}
	
}