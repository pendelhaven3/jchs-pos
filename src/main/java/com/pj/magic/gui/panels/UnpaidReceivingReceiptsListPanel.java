package com.pj.magic.gui.panels;

import java.awt.Dimension;
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
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SelectSupplierDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.report.UnpaidReceivingReceiptsReport;
import com.pj.magic.model.search.ReceivingReceiptSearchCriteria;
import com.pj.magic.service.ReceivingReceiptService;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class UnpaidReceivingReceiptsListPanel extends StandardMagicPanel {

	private static final int RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX = 0;
	private static final int RECEIVED_DATE_COLUMN_INDEX = 1;
	private static final int SUPPLIER_COLUMN_INDEX = 2;
	private static final int NET_AMOUNT_COLUMN_INDEX = 3;
	
	@Autowired private ReceivingReceiptService receivingReceiptService;
	@Autowired private SupplierService supplierService;
	@Autowired private SelectSupplierDialog selectSupplierDialog;
	
	private MagicListTable table;
	private ReceivingReceiptsTableModel tableModel;
	private JLabel totalAmountLabel;
	private MagicTextField supplierCodeField;
	private JLabel supplierNameLabel;
	private EllipsisButton selectSupplierButton;
	private JButton searchButton;
	
	@Override
	protected void initializeComponents() {
		supplierCodeField = new MagicTextField();
		supplierCodeField.setMaximumLength(Constants.SUPPLIER_CODE_MAXIMUM_LENGTH);
		
		selectSupplierButton = new EllipsisButton("Select Supplier (F5)");
		selectSupplierButton.addActionListener(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectSupplierDialog();
			}
		});
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchReceivingReceipts();
			}
		});
		
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void openSelectSupplierDialog() {
		selectSupplierDialog.searchSuppliers(supplierCodeField.getText());
		selectSupplierDialog.setVisible(true);
		
		Supplier supplier = selectSupplierDialog.getSelectedSupplier();
		if (supplier != null) {
			supplierCodeField.setText(supplier.getCode());
			supplierNameLabel.setText(supplier.getName());
		} else {
			supplierNameLabel.setText(null);
		}
	}

	private void initializeTable() {
		tableModel = new ReceivingReceiptsTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(RECEIVED_DATE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(SUPPLIER_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(NET_AMOUNT_COLUMN_INDEX).setPreferredWidth(100);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToReportsMenuPanel();
	}
	
	public void updateDisplay() {
		supplierCodeField.setText(null);
		supplierNameLabel.setText(null);
		searchReceivingReceipts();
	}

	private void searchReceivingReceipts() {
		UnpaidReceivingReceiptsReport report = createReport();
		tableModel.setReceivingReceipts(report.getReceivingReceipts());
		if (!report.getReceivingReceipts().isEmpty()) {
			table.changeSelection(0, 0);
		}
		totalAmountLabel.setText(FormatterUtil.formatAmount(report.getTotalAmount()));
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
		mainPanel.add(ComponentUtil.createLabel(100, "Supplier:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createSupplierPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.insets.top = 10;
		searchButton.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(searchButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(400, 200));
		mainPanel.add(scrollPane, c);
		
		currentRow++;

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}

	private JPanel createSupplierPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierCodeField.setPreferredSize(new Dimension(100, 25));
		panel.add(supplierCodeField, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectSupplierButton.setPreferredSize(new Dimension(30, 25));
		panel.add(selectSupplierButton, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(Box.createHorizontalStrut(10), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierNameLabel = ComponentUtil.createLabel(300);
		panel.add(supplierNameLabel, c);
		
		return panel;
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
//		toolBar.add(printPreviewButton);
		
		JButton printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print();
			}
		});
//		toolBar.add(printButton);
	}

	private void print() {
//		UnpaidSalesInvoicesReport report = createUnpaidSalesInvoicesReport();
//		printService.print(report);
	}

	private void printPreview() {
//		UnpaidReceivingReceiptsReport report = createUnpaidSalesInvoicesReport();
//		printPreviewDialog.updateDisplay(printService.generateReportAsString(report));
//		printPreviewDialog.setVisible(true);
	}

	private UnpaidReceivingReceiptsReport createReport() {
		ReceivingReceiptSearchCriteria criteria = new ReceivingReceiptSearchCriteria();
		criteria.setPaid(false);
		criteria.setOrderBy("a.RECEIVED_DT, a.RECEIVING_RECEIPT_NO");
		
		String supplierCode = supplierCodeField.getText();
		if (!StringUtils.isEmpty(supplierCode)) {
			Supplier supplier = supplierService.findSupplierByCode(supplierCode);
			criteria.setSupplier(supplier);
			if (supplier != null) {
				supplierCodeField.setText(supplier.getCode());
				supplierNameLabel.setText(supplier.getName());
			} else {
				supplierNameLabel.setText(null);
			}
		}
		
		UnpaidReceivingReceiptsReport report = new UnpaidReceivingReceiptsReport();
		report.setReceivingReceipts(receivingReceiptService.search(criteria));
		return report;
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
	
	private class ReceivingReceiptsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"RR No.", "Received Date", "Supplier", "Net Amount"};
		
		private List<ReceivingReceipt> receivingReceipts = new ArrayList<>();
		
		public void setReceivingReceipts(List<ReceivingReceipt> receivingReceipts) {
			this.receivingReceipts = receivingReceipts;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return receivingReceipts.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ReceivingReceipt receivingReceipt = receivingReceipts.get(rowIndex);
			switch (columnIndex) {
			case RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX:
				return receivingReceipt.getReceivingReceiptNumber();
			case RECEIVED_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(receivingReceipt.getReceivedDate());
			case SUPPLIER_COLUMN_INDEX:
				return receivingReceipt.getSupplier().getName();
			case NET_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(receivingReceipt.getTotalNetAmountWithVat());
			default:
				throw new RuntimeException("Fetch invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == NET_AMOUNT_COLUMN_INDEX) {
				return Number.class;
			} else {
				return Object.class;
			}
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
	}
	
}