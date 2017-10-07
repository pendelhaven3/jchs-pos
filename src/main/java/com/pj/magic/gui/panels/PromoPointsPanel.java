package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.AddPromoPointsClaimDialog;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.AvailedPromoPointsItem;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoPointsClaim;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
import com.pj.magic.service.PromoRedemptionService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.service.SalesReturnService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

/**
 * Panel class for Availed Promo Points screen
 * 
 * @author PJ Miranda
 *
 */
@Component
public class PromoPointsPanel extends StandardMagicPanel {

	private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 0;
	private static final int TRANSACTION_DATE_COLUMN_INDEX = 1;
	private static final int QUALIFYING_AMOUNT_COLUMN_INDEX = 2;
	private static final int ADJUSTED_AMOUNT_COLUMN_INDEX = 3;
	private static final int POINTS_EARNED_COLUMN_INDEX = 4;

	private static final int CLAIM_NUMBER_COLUMN_INDEX = 0;
	private static final int POINTS_CLAIMED_COLUMN_INDEX = 1;
	private static final int REMARKS_COLUMN_INDEX = 2;
	private static final int CLAIM_DATE_COLUMN_INDEX = 3;
	private static final int CLAIM_BY_COLUMN_INDEX = 4;
	
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private SalesReturnService salesReturnService;
	@Autowired private PromoRedemptionService promoRedemptionService;
	@Autowired private AddPromoPointsClaimDialog addPromoPointsClaimDialog;
	
	private Promo promo;
	private Customer customer;
	private JLabel promoNameLabel;
	private MagicTextField customerCodeField;
	private JLabel customerNameLabel;
	private EllipsisButton selectCustomerButton;
	private MagicListTable salesInvoicesTable;
	private SalesInvoicesTableModel salesInvoicesTableModel;
	private MagicListTable claimsTable;
	private ClaimsTableModel claimsTableModel;
	private JLabel totalPointsEarnedLabel;
	private JLabel totalPointsClaimedLabel;
	private JLabel totalPointsRemainingLabel;
	private JTabbedPane tabbedPane;
	private MagicToolBarButton addClaimButton;
	private MagicToolBarButton deleteClaimButton;
	
	@Override
	protected void initializeComponents() {
		promoNameLabel = new JLabel();
		
		customerCodeField = new MagicTextField();
		customerNameLabel = new JLabel();
		
		selectCustomerButton = new EllipsisButton("Select Customer (F5)");
		selectCustomerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectCustomerDialog();
			}
		});;
		
		initializeTables();
		
		totalPointsEarnedLabel = new JLabel();
		totalPointsClaimedLabel = new JLabel();
		totalPointsRemainingLabel = new JLabel();
		
		focusOnComponentWhenThisPanelIsDisplayed(customerCodeField);
	}
	
	private void initializeTables() {
		salesInvoicesTableModel = new SalesInvoicesTableModel();
		salesInvoicesTable = new MagicListTable(salesInvoicesTableModel);
		
		claimsTableModel = new ClaimsTableModel();
		claimsTable = new MagicListTable(claimsTableModel);
		
		TableColumnModel columnModel = claimsTable.getColumnModel();
		columnModel.getColumn(CLAIM_NUMBER_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(POINTS_CLAIMED_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(REMARKS_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(CLAIM_DATE_COLUMN_INDEX).setPreferredWidth(150);
		columnModel.getColumn(CLAIM_BY_COLUMN_INDEX).setPreferredWidth(100);
	}
	
	private void openSelectCustomerDialog() {
		selectCustomerDialog.searchCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			updateDisplay(customer);
			tabbedPane.setSelectedIndex(0);
		}
	}

	private void updateDisplay(Customer customer) {
		this.customer = customer;
		
		customerCodeField.setText(customer.getCode());
		customerNameLabel.setText(customer.getName());
		
		tabbedPane.setEnabled(true);
		addClaimButton.setEnabled(true);
		deleteClaimButton.setEnabled(true);
		
		List<SalesInvoice> salesInvoices = searchSalesInvoicesQualifiedForPromo(customer);
		List<AvailedPromoPointsItem> items = promo.evaluateForPoints(salesInvoices, getRelatedSalesReturns(salesInvoices));
		salesInvoicesTableModel.setItems(items);
		
		List<PromoPointsClaim> claims = promoRedemptionService.findAllPromoPointsClaimByPromoAndCustomer(promo, customer);
		orderByLatestClaimNumberFirst(claims);
		claimsTableModel.setClaims(claims);
		
		int totalPointsEarned = computeTotalPointsEarned(items);
		int totalPointsClaimed = computeTotalPointsClaimed(claims);
		int totalPointsRemaining = totalPointsEarned - totalPointsClaimed;
		
		totalPointsEarnedLabel.setText(String.valueOf(totalPointsEarned));
		totalPointsClaimedLabel.setText(String.valueOf(totalPointsClaimed));
		totalPointsRemainingLabel.setText(String.valueOf(totalPointsRemaining));
	}

	private static void orderByLatestClaimNumberFirst(List<PromoPointsClaim> claims) {
		Collections.sort(claims, new Comparator<PromoPointsClaim>() {

			@Override
			public int compare(PromoPointsClaim o1, PromoPointsClaim o2) {
				return (o1.getClaimNumber() > o2.getClaimNumber()) ? -1 : 1;
			}
		});
	}

	private static int computeTotalPointsClaimed(List<PromoPointsClaim> claims) {
		int total = 0;
		for (PromoPointsClaim claim : claims) {
			total += claim.getPoints();
		}
		return total;
	}

	private List<SalesReturn> getRelatedSalesReturns(List<SalesInvoice> salesInvoices) {
		List<SalesReturn> salesReturns = new ArrayList<>();
		for (SalesInvoice salesInvoice : salesInvoices) {
			salesReturns.addAll(salesReturnService.findPostedSalesReturnsBySalesInvoice(salesInvoice));
		}
		return salesReturns;
	}

	private static int computeTotalPointsEarned(List<AvailedPromoPointsItem> items) {
		int total = 0;
		for (AvailedPromoPointsItem item : items) {
			total += item.getPoints();
		}
		return total;
	}

	private List<SalesInvoice> searchSalesInvoicesQualifiedForPromo(Customer customer) {
		SalesInvoiceSearchCriteria criteria = new SalesInvoiceSearchCriteria();
		criteria.setCustomer(customer);
		criteria.setTransactionDateFrom(promo.getStartDate());
		criteria.setTransactionDateTo(promo.getEndDate());
		criteria.setPricingScheme(promo.getPricingScheme());
		criteria.setCancelled(false);
		
		return salesInvoiceService.search(criteria);
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
		mainPanel.add(ComponentUtil.createLabel(100, "Promo:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		promoNameLabel = ComponentUtil.createLabel(300);
		mainPanel.add(promoNameLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Customer:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createCustomerPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(30), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		
		tabbedPane = createTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(600, 250));
		mainPanel.add(tabbedPane, c);
				
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridy = currentRow;
		c.gridwidth = 3;
		mainPanel.add(createTotalsPanel(), c);
	}

	private JPanel createCustomerPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		customerCodeField.setPreferredSize(new Dimension(100, 25));
		panel.add(customerCodeField, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectCustomerButton.setPreferredSize(new Dimension(30, 24));
		panel.add(selectCustomerButton, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(Box.createHorizontalStrut(10), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(customerNameLabel, c);
		
		return panel;
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

	@Override
	protected void registerKeyBindings() {
		customerCodeField.onF5Key(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectCustomerDialog();
			}
		});
		
		claimsTable.onDeleteKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deletePromoPointsClaim();
			}
		});
		
		claimsTable.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				editPromoPointsClaim();
			}
			
		});
		
	}

	private void editPromoPointsClaim() {
		PromoPointsClaim claim = claimsTableModel.getClaim(claimsTable.getSelectedRow());
		claim.setPromo(promo);
		
		addPromoPointsClaimDialog.updateDisplay(claim);
		addPromoPointsClaimDialog.setVisible(true);
		
		updateDisplay(customer);
	}

	public void updateDisplay(Promo promo) {
		this.promo = promo;
		this.customer = null;
		
		promoNameLabel.setText(promo.getName());
		customerCodeField.setText(null);
		customerNameLabel.setText(null);
		
		tabbedPane.setEnabled(false);
		tabbedPane.setSelectedIndex(0);
		addClaimButton.setEnabled(false);
		deleteClaimButton.setEnabled(false);
		salesInvoicesTableModel.clear();
		claimsTableModel.clear();
		totalPointsEarnedLabel.setText(null);
		totalPointsClaimedLabel.setText(null);
		totalPointsRemainingLabel.setText(null);
	}
	
	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPromoRedemptionPromoListPanel();
	}

	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(180, "Total Points Earned:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(totalPointsEarnedLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(180, "Total Points Claimed:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(totalPointsClaimedLabel, c);
		
		currentRow++;

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(180, "Total Points Remaining:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(totalPointsRemainingLabel, c);
		
		return panel;
	}
	
	private JTabbedPane createTabbedPane() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Sales Invoices", createSalesInvoicesPanel());
		tabbedPane.addTab("Claims", createClaimsPanel());
		return tabbedPane;
	}
	
	private JPanel createClaimsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createClaimsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane scrollPane = new JScrollPane(claimsTable);
		scrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(scrollPane, c);
		
		return panel;
	}

	private JPanel createClaimsTableToolBar() {
		JPanel panel = new JPanel();
		
		addClaimButton = new MagicToolBarButton("plus_small", "Add Claim", true);
		addClaimButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addPromoPointsClaim();
			}
		});
		panel.add(addClaimButton, BorderLayout.WEST);
		
		deleteClaimButton = new MagicToolBarButton("minus_small", "Delete Claim (Delete)", true);
		deleteClaimButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deletePromoPointsClaim();
			}
		});
		panel.add(deleteClaimButton, BorderLayout.WEST);
		
		return panel;
	}

	private void deletePromoPointsClaim() {
		if (claimsTable.getSelectedRow() != -1) {
			if (confirm("Are you sure you want to delete the selected record?")) {
				promoRedemptionService.delete(claimsTableModel.getClaim(claimsTable.getSelectedRow()));
				updateDisplay(customer);
			}
		}
	}

	private void addPromoPointsClaim() {
		PromoPointsClaim claim = new PromoPointsClaim();
		claim.setPromo(promo);
		claim.setCustomer(customer);
		
		addPromoPointsClaimDialog.updateDisplay(claim);
		addPromoPointsClaimDialog.setVisible(true);
		
		updateDisplay(customer);
	}

	private JPanel createSalesInvoicesPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		JScrollPane scrollPane = new JScrollPane(salesInvoicesTable);
		scrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(scrollPane, c);
		
		return panel;
	}

	private class SalesInvoicesTableModel extends AbstractTableModel {

		private final String[] columnNames = {"SI No.", "Transaction Date", "Qualifying Amount", "Adjusted Amount", "Points"};
		
		private List<AvailedPromoPointsItem> items = new ArrayList<>();
		
		public void setItems(List<AvailedPromoPointsItem> items) {
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
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			AvailedPromoPointsItem item = items.get(rowIndex);
			switch (columnIndex) {
			case SALES_INVOICE_NUMBER_COLUMN_INDEX:
				return item.getSalesInvoiceNumber();
			case TRANSACTION_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(item.getTransactionDate());
			case QUALIFYING_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(item.getQualifyingAmount());
			case ADJUSTED_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(item.getAdjustedAmount());
			case POINTS_EARNED_COLUMN_INDEX:
				return item.getPoints();
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case QUALIFYING_AMOUNT_COLUMN_INDEX:
			case ADJUSTED_AMOUNT_COLUMN_INDEX:
			case POINTS_EARNED_COLUMN_INDEX:
				return Number.class;
			default:
				return Object.class;
			}
		}
		
	}

	private class ClaimsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Claim No.", "Points Claimed", "Remarks", "Claim Date", "Claim By"};
		
		private List<PromoPointsClaim> claims = new ArrayList<>();
		
		public void setClaims(List<PromoPointsClaim> claims) {
			this.claims = claims;
			fireTableDataChanged();
		}
		
		public PromoPointsClaim getClaim(int row) {
			return claims.get(row);
		}

		public void clear() {
			claims.clear();
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			return claims.size();
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
			PromoPointsClaim claim = claims.get(rowIndex);
			switch (columnIndex) {
			case CLAIM_NUMBER_COLUMN_INDEX:
				return claim.getClaimNumber();
			case POINTS_CLAIMED_COLUMN_INDEX:
				return claim.getPoints();
			case REMARKS_COLUMN_INDEX:
				return claim.getRemarks();
			case CLAIM_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDateTime(claim.getClaimDate());
			case CLAIM_BY_COLUMN_INDEX:
				return claim.getClaimBy().getUsername();
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
	}
	
}