package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.UnpaidSalesInvoicesTable;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoType1Rule;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.service.PromoRedemptionService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class SelectSalesInvoicesForPromoRedemptionDialog extends MagicDialog {

	private static final int SELECTION_CHECKBOX_COLUMN_INDEX = 0;
	private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 1;
	private static final int TRANSACTION_DATE_COLUMN_INDEX = 2;
	private static final int AMOUNT_COLUMN_INDEX = 3;

	@Autowired private PromoRedemptionService promoRedemptionService;
	
	private JTable table;
	private SalesInvoicesTableModel tableModel;
	private JButton addButton;
	private JButton addAllButton;
	private List<SalesInvoice> selectedSalesInvoices = new ArrayList<>();
	private PromoRedemption promoRedemption;
	
	public SelectSalesInvoicesForPromoRedemptionDialog() {
		setSize(500, 250);
		setLocationRelativeTo(null);
		setTitle("Select Sales Invoices for Promo Redemption");
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		layoutComponents();
	}

	private void initializeComponents() {
		addButton = new JButton("Add Selected");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedSalesInvoices.addAll(tableModel.getSelectedSalesInvoices());
				setVisible(false);
			}
		});
		
		addAllButton = new JButton("Add All");
		addAllButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedSalesInvoices.addAll(tableModel.getSalesInvoices());
				setVisible(false);
			}
		});
		
		initializeTable();
	}

	private void initializeTable() {
		tableModel = new SalesInvoicesTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(SELECTION_CHECKBOX_COLUMN_INDEX).setPreferredWidth(40);
		columnModel.getColumn(SALES_INVOICE_NUMBER_COLUMN_INDEX).setPreferredWidth(120);
		columnModel.getColumn(TRANSACTION_DATE_COLUMN_INDEX).setPreferredWidth(120);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setPreferredWidth(120);
	}

	public List<SalesInvoice> getSelectedSalesInvoices() {
		return selectedSalesInvoices;
	}
	
	@Override
	protected void doWhenEscapeKeyPressed() {
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(400, 200));
		add(scrollPane, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createVerticalFiller(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		add(ComponentUtil.createGenericPanel(
				addButton, addAllButton), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createVerticalFiller(20), c);
	}
	
	public void searchSalesInvoicesForPromoRedemption(PromoRedemption promoRedemption) {
		this.promoRedemption = promoRedemption;
		selectedSalesInvoices.clear();
		List<SalesInvoice> salesInvoices = promoRedemptionService
				.findAllUnreedemedSalesInvoices(promoRedemption.getPromo(), promoRedemption.getCustomer());
		tableModel.setSalesInvoices(salesInvoices);
	}

	private class SalesInvoicesTableModel extends AbstractTableModel {

		private final String[] columnNames = {"", "SI No.", "Transaction Date", "Total Amount"};
		
		private List<SalesInvoice> salesInvoices = new ArrayList<>();
		private List<Integer> selected = new ArrayList<>();
		
		public void setSalesInvoices(List<SalesInvoice> salesInvoices) {
			this.salesInvoices = salesInvoices;
			selected.clear();
			fireTableDataChanged();
		}
		
		public List<SalesInvoice> getSalesInvoices() {
			return salesInvoices;
		}

		@Override
		public int getRowCount() {
			return salesInvoices.size();
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
			SalesInvoice salesInvoice = salesInvoices.get(rowIndex);
			switch (columnIndex) {
			case SELECTION_CHECKBOX_COLUMN_INDEX:
				return selected.contains(rowIndex);
			case SALES_INVOICE_NUMBER_COLUMN_INDEX:
				return salesInvoice.getSalesInvoiceNumber();
			case TRANSACTION_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(salesInvoice.getTransactionDate());
			case AMOUNT_COLUMN_INDEX:
				switch (promoRedemption.getPromo().getPromoType()) {
				case PROMO_TYPE_1:
					PromoType1Rule rule = promoRedemption.getPromo().getPromoType1Rule();
					return FormatterUtil.formatAmount(salesInvoice.getSalesByManufacturer(
							rule.getManufacturer()));
				case PROMO_TYPE_3: {
					BigDecimal amount = promoRedemption.getPromo().getPromoType3Rule().getQualifyingAmount(salesInvoice);
					return FormatterUtil.formatAmount(amount);
				}
				case PROMO_TYPE_5: {
					BigDecimal amount = promoRedemption.getPromo().getPromoType5Rule().getQualifyingAmount(salesInvoice);
					return FormatterUtil.formatAmount(amount);
				}
				default:
					return "-";
				}
			default:
				return null;
			}
		}
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case UnpaidSalesInvoicesTable.SELECTION_CHECKBOX_COLUMN_INDEX:
				if (selected.contains(rowIndex)) {
					selected.remove(selected.indexOf(rowIndex));
				} else {
					selected.add(rowIndex);
				}
			}
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == SELECTION_CHECKBOX_COLUMN_INDEX;
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case SELECTION_CHECKBOX_COLUMN_INDEX:
				return Boolean.class;
			case AMOUNT_COLUMN_INDEX:
				return Number.class;
			default:
				return Object.class;
			}
		}

		public List<SalesInvoice> getSelectedSalesInvoices() {
			List<SalesInvoice> selectedSalesInvoices = new ArrayList<>();
			for (Integer i : selected) {
				selectedSalesInvoices.add(salesInvoices.get(i));
			}
			return selectedSalesInvoices;
		}
		
	}
	
}
