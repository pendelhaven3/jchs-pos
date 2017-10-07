package com.pj.magic.gui.panels.promo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.exception.NothingToRedeemException;
import com.pj.magic.exception.SalesInvoiceIneligibleForPromoRedemptionException;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.dialog.SelectSalesInvoicesForPromoRedemptionDialog;
import com.pj.magic.gui.panels.StandardMagicPanel;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionReward;
import com.pj.magic.model.PromoRedemptionSalesInvoice;
import com.pj.magic.model.PromoType1Rule;
import com.pj.magic.model.PromoType2Rule;
import com.pj.magic.model.PromoType3Rule;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.PromoRedemptionService;
import com.pj.magic.service.impl.PromoService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.HtmlUtil;

@Component
public class PromoRedemptionPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(PromoRedemptionPanel.class);
	
	private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 0;
	private static final int AMOUNT_COLUMN_INDEX = 1;
	private static final int ITEM_DESCRIPTION_COLUMN_INDEX = 0;
	private static final int UNIT_COLUMN_INDEX = 1;
	private static final int QUANTITY_COLUMN_INDEX = 2;
	
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	@Autowired private PromoRedemptionService promoRedemptionService;
	@Autowired private SelectSalesInvoicesForPromoRedemptionDialog selectSalesInvoicesForPromoRedemptionDialog;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	@Autowired private PromoService promoService;
	
	private PromoRedemption promoRedemption;
	private JLabel promoNameLabel;
	private JLabel promoMechanicsLabel;
	private JLabel promoRedemptionNumberLabel;
	private MagicTextField customerCodeField;
	private JLabel customerNameLabel;
	private JLabel statusLabel;
	private EllipsisButton selectCustomerButton;
	private MagicListTable salesInvoicesTable;
	private MagicListTable prizesTable;
	private SalesInvoicesTableModel salesInvoicesTableModel;
	private PromoRedemptionPrizesTableModel prizesTableModel;
	private JLabel totalAmountLabel;
	private MagicToolBarButton addSalesInvoiceButton;
	private MagicToolBarButton removeSalesInvoiceButton;
	private MagicToolBarButton postButton;
	private MagicToolBarButton printPreviewButton;
	private MagicToolBarButton printButton;
	
	@Override
	protected void initializeComponents() {
		promoNameLabel = new JLabel();
		promoMechanicsLabel = new JLabel();
		
		customerCodeField = new MagicTextField();
		customerCodeField.setMaximumLength(Constants.CUSTOMER_CODE_MAXIMUM_LENGTH);
		
		customerNameLabel = new JLabel();
		statusLabel = new JLabel();
		
		selectCustomerButton = new EllipsisButton("Select Customer (F5)");
		selectCustomerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectCustomerDialog();
			}
		});
		
		initializeTables();
		initializeModelListener();
		
		focusOnComponentWhenThisPanelIsDisplayed(customerCodeField);
	}

	private void initializeModelListener() {
		salesInvoicesTableModel.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				if (!promoRedemption.isPosted()) {
					totalAmountLabel.setText(FormatterUtil.formatAmount(promoRedemption.getTotalAmount()));
					prizesTableModel.fireTableDataChanged();
				}
			}
			
		});
	}

	private void openSelectCustomerDialog() {
		selectCustomerDialog.searchActiveCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			if (promoRedemption.getCustomer() != null && promoRedemption.getCustomer().equals(customer)) {
				// skip saving since there is no change
				return;
			}
			
			promoRedemption.setCustomer(customer);
			
			try {
				promoRedemptionService.save(promoRedemption);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error during saving");
				return;
			}
			
			updateDisplay(promoRedemption);
		}
	}

	private void initializeTables() {
		salesInvoicesTableModel = new SalesInvoicesTableModel();
		salesInvoicesTable = new MagicListTable(salesInvoicesTableModel);
		
		prizesTableModel = new PromoRedemptionPrizesTableModel();
		prizesTable = new MagicListTable(prizesTableModel);
		
		prizesTable.getColumnModel().getColumn(ITEM_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
		prizesTable.getColumnModel().getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(50);
		prizesTable.getColumnModel().getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(50);
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
		mainPanel.add(promoNameLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Mechanics:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		promoMechanicsLabel.setPreferredSize(new Dimension(600, 50));
		mainPanel.add(promoMechanicsLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(180, "Promo Redemption No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		promoRedemptionNumberLabel = ComponentUtil.createLabel(100);
		mainPanel.add(promoRedemptionNumberLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Customer:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createCustomerPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Status:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		statusLabel.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(statusLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		mainPanel.add(createTablesPanel(), c);
	}

	private JPanel createCustomerPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		
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
		customerNameLabel.setPreferredSize(new Dimension(200, 20));
		panel.add(customerNameLabel, c);
		
		return panel;
	}

	private JPanel createTablesPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.3;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		
		JTabbedPane salesInvoicesTabbedPane = new JTabbedPane();
		salesInvoicesTabbedPane.addTab("Sales Invoices", createSalesInvoicesPanel());
		panel.add(salesInvoicesTabbedPane, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.7;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		
		JTabbedPane prizesTabbedPane = new JTabbedPane();
		prizesTabbedPane.addTab("Prizes", createPrizesPanel());
		panel.add(prizesTabbedPane, c);
		
		return panel;
	}

	private JPanel createPrizesPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(Box.createVerticalStrut(38), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane salesInvoicesScrollPane = new JScrollPane(prizesTable);
		salesInvoicesScrollPane.setPreferredSize(new Dimension(200, 150));
		panel.add(salesInvoicesScrollPane, c);
		
		return panel;
	}

	private JPanel createSalesInvoicesPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createSalesInvoicesTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane salesInvoicesScrollPane = new JScrollPane(salesInvoicesTable);
		salesInvoicesScrollPane.setPreferredSize(new Dimension(200, 150));
		panel.add(salesInvoicesScrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		c.gridwidth = 2;
		totalAmountLabel = ComponentUtil.createRightLabel(100);
		panel.add(ComponentUtil.createGenericPanel(
				new JLabel("Total Amount: "), totalAmountLabel, Box.createHorizontalStrut(10)), c);
		
		return panel;
	}

	private JPanel createSalesInvoicesTableToolBar() {
		JPanel panel = new JPanel();
		
		addSalesInvoiceButton = new MagicToolBarButton("plus_small", "Add Sales Invoice (F10)", true);
		addSalesInvoiceButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectSalesInvoicesForPromoRedemptionDialog();
			}
		});
		panel.add(addSalesInvoiceButton, BorderLayout.WEST);
		
		removeSalesInvoiceButton = new MagicToolBarButton("minus_small", "Remove Sales Invoice (Delete)", true);
		removeSalesInvoiceButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeCurrentlySelectedSalesInvoice();
			}
		});
		panel.add(removeSalesInvoiceButton, BorderLayout.WEST);
		
		return panel;
	}

	private void removeCurrentlySelectedSalesInvoice() {
		int selectedRow = salesInvoicesTable.getSelectedRow();
		if (selectedRow != -1) {
			if (confirm("Do you wish to delete the selected item?")) {
				PromoRedemptionSalesInvoice salesInvoice = 
						salesInvoicesTableModel.getPromoRedemptionSalesInvoice(selectedRow);
				salesInvoicesTable.clearSelection();
				promoRedemption.getRedemptionSalesInvoices().remove(salesInvoice);
				salesInvoicesTableModel.removeItem(salesInvoice);
				
				if (!salesInvoicesTableModel.getSalesInvoices().isEmpty()) {
					if (selectedRow == salesInvoicesTableModel.getRowCount()) {
						salesInvoicesTable.changeSelection(selectedRow - 1, 0);
					} else {
						salesInvoicesTable.changeSelection(selectedRow, 0);
					}
				}
			}
		}
	}

	private void openSelectSalesInvoicesForPromoRedemptionDialog() {
		selectSalesInvoicesForPromoRedemptionDialog.searchSalesInvoicesForPromoRedemption(promoRedemption);
		selectSalesInvoicesForPromoRedemptionDialog.setVisible(true);
		
		List<SalesInvoice> selectedSalesInvoices = 
				selectSalesInvoicesForPromoRedemptionDialog.getSelectedSalesInvoices();
		if (!selectedSalesInvoices.isEmpty()) {
			if (promoRedemption.getPromo().isPromoType1()) {
				PromoType1Rule rule = promoRedemption.getPromo().getPromoType1Rule();
				if (rule.getDailyRedeemLimitPerCustomer() > 0) {
					int redeemed = promoRedemptionService.getNumberOfRedemptionsToday(
							promoRedemption.getPromo(), promoRedemption.getCustomer());
					
					if (redeemed == rule.getDailyRedeemLimitPerCustomer()) {
						showErrorMessage("Customer has already redeemed the daily limit for this promo");
						return;
					} else if (redeemed > 0) {
						showErrorMessage("Customer can redeem " + (rule.getDailyRedeemLimitPerCustomer() - redeemed) 
								+ " more times for this day");
					}
				}
			} else if (promoRedemption.getPromo().isPromoType1()) {
				PromoType3Rule rule = promoRedemption.getPromo().getPromoType3Rule();
				if (rule.getDailyRedeemLimitPerCustomer() > 0) {
					int redeemed = promoRedemptionService.getNumberOfRedemptionsToday(
							promoRedemption.getPromo(), promoRedemption.getCustomer());
					
					if (redeemed == rule.getDailyRedeemLimitPerCustomer()) {
						showErrorMessage("Customer has already redeemed the daily limit for this promo");
						return;
					} else if (redeemed > 0) {
						showErrorMessage("Customer can redeem " + (rule.getDailyRedeemLimitPerCustomer() - redeemed) 
								+ " more times for this day");
					}
				}
			}
			
			for (SalesInvoice salesInvoice : selectedSalesInvoices) {
				PromoRedemptionSalesInvoice redemptionSalesInvoice = new PromoRedemptionSalesInvoice();
				redemptionSalesInvoice.setParent(promoRedemption);
				redemptionSalesInvoice.setSalesInvoice(salesInvoice);
				promoRedemptionService.save(redemptionSalesInvoice);
				promoRedemption.getRedemptionSalesInvoices().add(redemptionSalesInvoice);
			}
			salesInvoicesTableModel.setPromoRedemption(promoRedemption);
		}
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postPromoRedemption();
			}
		});
		toolBar.add(postButton);
		
		printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreview();
			}
		});
		toolBar.add(printPreviewButton);
		
		printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printService.print(promoRedemption);
			}
		});
		toolBar.add(printButton);
	}

	protected void printPreview() {
		printPreviewDialog.updateDisplay(printService.generateReportAsString(promoRedemption));
		printPreviewDialog.setVisible(true);
	}

	private void postPromoRedemption() {
		if (confirm("Do you want to post this Promo Redemption?")) {
			try {
				promoRedemptionService.post(promoRedemption);
				JOptionPane.showMessageDialog(this, "Promo Redemption posted");
				updateDisplay(promoRedemption);
			} catch (NothingToRedeemException e) {
				showErrorMessage("Not enough Sales Invoice amount to be able redeem anything");
				updateDisplay(promoRedemption);
			} catch (AlreadyPostedException e) {
				showErrorMessage("Promo Redemption is already posted");
				updateDisplay(promoRedemption);
			} catch (NotEnoughStocksException e) {
				showErrorMessage("Not enough stocks of promo prize");
			} catch (SalesInvoiceIneligibleForPromoRedemptionException e) {
				showErrorMessage(MessageFormat.format("Sales Invoice No. {0} is ineligible for promo redemption", 
						e.getSalesInvoice().getSalesInvoiceNumber().toString()));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
	}

	@Override
	protected void registerKeyBindings() {
		customerCodeField.onF5Key(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectCustomerDialog();
			}
		});
		
		salesInvoicesTable.onDeleteKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!promoRedemption.isPosted()) {
					removeCurrentlySelectedSalesInvoice();
				}
			}
		});
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPromoRedemptionListPanel(promoRedemption.getPromo());
	}

	public void updateDisplay(PromoRedemption promoRedemption) {
		this.promoRedemption = promoRedemption;
		
		if (promoRedemption.getId() == null) {
			clearDisplay();
			return;
		}
		
		this.promoRedemption = promoRedemption = 
				promoRedemptionService.getPromoRedemption(promoRedemption.getId());
		
		promoNameLabel.setText(promoRedemption.getPromo().getName());
		promoMechanicsLabel.setText(HtmlUtil.html(promoRedemption.getPromo().getMechanicsDescription()));
		promoRedemptionNumberLabel.setText(promoRedemption.getPromoRedemptionNumber().toString());
		customerCodeField.setEnabled(!promoRedemption.isPosted());
		customerCodeField.setText(promoRedemption.getCustomer().getCode());
		customerNameLabel.setText(promoRedemption.getCustomer().getName());
		statusLabel.setText(promoRedemption.getStatus());
		totalAmountLabel.setText(FormatterUtil.formatAmount(promoRedemption.getTotalAmount()));
		
		salesInvoicesTableModel.setPromoRedemption(promoRedemption);
		prizesTableModel.setPromoRedemption(promoRedemption);
		
		boolean isNew = !promoRedemption.isPosted();
		selectCustomerButton.setEnabled(isNew);
		addSalesInvoiceButton.setEnabled(isNew);
		removeSalesInvoiceButton.setEnabled(isNew);
		postButton.setEnabled(isNew);
		printPreviewButton.setEnabled(!isNew);
		printButton.setEnabled(!isNew);
	}
	
	private void clearDisplay() {
		promoRedemption.setPromo(promoService.getPromo(promoRedemption.getPromo().getId()));
		prizesTableModel.setPromoRedemption(promoRedemption);
		promoNameLabel.setText(promoRedemption.getPromo().getName());
		promoMechanicsLabel.setText(promoRedemption.getPromo().getMechanicsDescription());
		
		promoRedemptionNumberLabel.setText(null);
		customerCodeField.setEnabled(true);
		customerCodeField.setText(null);
		customerNameLabel.setText(null);
		statusLabel.setText(null);
		totalAmountLabel.setText(null);
		
		salesInvoicesTableModel.clear();
		prizesTableModel.clear();
		
		selectCustomerButton.setEnabled(true);
		addSalesInvoiceButton.setEnabled(false);
		removeSalesInvoiceButton.setEnabled(false);
		postButton.setEnabled(false);
		printPreviewButton.setEnabled(false);
		printButton.setEnabled(false);
	}

	private class SalesInvoicesTableModel extends AbstractTableModel {

		private final String[] columnNames = {"SI No.", "Amount"};
		
		private List<PromoRedemptionSalesInvoice> salesInvoices = new ArrayList<>();
		
		public void setPromoRedemption(PromoRedemption promoRedemption) {
			this.salesInvoices = promoRedemption.getRedemptionSalesInvoices();
			fireTableDataChanged();
		}

		public void clear() {
			salesInvoices.clear();
			fireTableDataChanged();
		}

		public List<PromoRedemptionSalesInvoice> getSalesInvoices() {
			return salesInvoices;
		}
		
		public PromoRedemptionSalesInvoice getPromoRedemptionSalesInvoice(int rowIndex) {
			return salesInvoices.get(rowIndex);
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
		public Class<?> getColumnClass(int columnIndex) {
			return (columnIndex == AMOUNT_COLUMN_INDEX) ? Number.class : String.class;
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			SalesInvoice salesInvoice = salesInvoices.get(rowIndex).getSalesInvoice();
			switch (columnIndex) {
			case SALES_INVOICE_NUMBER_COLUMN_INDEX:
				return salesInvoice.getSalesInvoiceNumber();
			case AMOUNT_COLUMN_INDEX:
				switch (promoRedemption.getPromo().getPromoType()) {
				case PROMO_TYPE_1:
					PromoType1Rule rule = promoRedemption.getPromo().getPromoType1Rule();
					return FormatterUtil.formatAmount(salesInvoice.getSalesByManufacturer(
							rule.getManufacturer()));
				case PROMO_TYPE_3:
					BigDecimal amount = 
						promoRedemption.getPromo().getPromoType3Rule().getQualifyingAmount(salesInvoice);
					return FormatterUtil.formatAmount(amount);
				default:
					return null;
				}
			default:
				return null;
			}
		}
		
		public void removeItem(PromoRedemptionSalesInvoice salesInvoice) {
			salesInvoices.remove(salesInvoice);
			promoRedemptionService.delete(salesInvoice);
			fireTableDataChanged();
		}
		
	}
	
	private class PromoRedemptionPrizesTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Item Description", "Unit", "Quantity"};
		
		private PromoRedemption promoRedemption;
		private PromoType1Rule promoType1Rule;
		private PromoType3Rule promoType3Rule;
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		public void clear() {
			promoRedemption = null;
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			if (promoRedemption == null) {
				return 0;
			}
			
			Promo promo = promoRedemption.getPromo();
			switch (promo.getPromoType()) {
			case PROMO_TYPE_1:
				return 1;
			case PROMO_TYPE_2:
				return promo.getPromoType2Rules().size();
			case PROMO_TYPE_3:
				return 1;
			default:
				return 0;
			}
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return (columnIndex == QUANTITY_COLUMN_INDEX) ? Number.class : String.class;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Promo promo = promoRedemption.getPromo();
			switch (promo.getPromoType()) {
			case PROMO_TYPE_1:
				return getValueAtForPromoType1(rowIndex, columnIndex);
			case PROMO_TYPE_2:
				return getValueAtForPromoType2(rowIndex, columnIndex);
			case PROMO_TYPE_3:
				return getValueAtForPromoType3(rowIndex, columnIndex);
			default:
				return null;
			}
		}
		
		public Object getValueAtForPromoType3(int rowIndex, int columnIndex) {
			Promo promo = promoRedemption.getPromo();
			PromoType3Rule rule = (promoType3Rule != null) ? promoType3Rule : promo.getPromoType3Rule();
			
			switch (columnIndex) {
			case ITEM_DESCRIPTION_COLUMN_INDEX:
				return rule.getFreeProduct().getDescription();
			case UNIT_COLUMN_INDEX:
				return rule.getFreeUnit();
			case QUANTITY_COLUMN_INDEX:
				if (promoRedemption.isPosted()) {
					return promoRedemption.getRewards().get(0).getQuantity();
				} else {
					PromoRedemptionReward reward = rule.evaluate(promoRedemption.getSalesInvoices());
					if (reward != null) {
						return reward.getQuantity();
					} else {
						return 0;
					}
				}
			default:
				return null;
			}
		}

		public Object getValueAtForPromoType2(int rowIndex, int columnIndex) {
			Promo promo = promoRedemption.getPromo();
			PromoType2Rule rule = promo.getPromoType2Rules().get(rowIndex);
			switch (columnIndex) {
			case ITEM_DESCRIPTION_COLUMN_INDEX:
				return rule.getFreeProduct().getDescription();
			case UNIT_COLUMN_INDEX:
				return rule.getFreeUnit();
			case QUANTITY_COLUMN_INDEX:
				if (promoRedemption.isPosted()) {
					PromoRedemptionReward reward = promoRedemption.getRewardByRule(rule);
					return (reward != null) ? reward.getQuantity() : 0;
				} else {
					return promoRedemption.getFreeQuantity(rule);
				}
			default:
				return null;
			}
		}

		public Object getValueAtForPromoType1(int rowIndex, int columnIndex) {
			Promo promo = promoRedemption.getPromo();
			PromoType1Rule rule = (promoType1Rule != null) ? promoType1Rule : promo.getPromoType1Rule();
			
			switch (columnIndex) {
			case ITEM_DESCRIPTION_COLUMN_INDEX:
				return rule.getProduct().getDescription();
			case UNIT_COLUMN_INDEX:
				return rule.getUnit();
			case QUANTITY_COLUMN_INDEX:
				if (promoRedemption.isPosted()) {
					return promoRedemption.getRewards().get(0).getQuantity();
				} else {
					PromoRedemptionReward reward = rule.evaluate(promoRedemption.getSalesInvoices());
					if (reward != null) {
						return reward.getQuantity();
					} else {
						return 0;
					}
				}
			default:
				return null;
			}
		}
		
		public void setPromoRedemption(PromoRedemption promoRedemption) {
			this.promoRedemption = promoRedemption;
			promoType1Rule = null;
			promoType3Rule = null;
			
			if (promoRedemption.getPromo().isPromoType1()) {
				PromoType1Rule rule = promoRedemption.getPromo().getPromoType1Rule();
				
				if (rule.getDailyRedeemLimitPerCustomer() > 0) {
					int redeemed = promoRedemptionService.getNumberOfRedemptionsToday(
							promoRedemption.getPromo(), promoRedemption.getCustomer());
					
					promoType1Rule = (PromoType1Rule)ObjectUtils.clone(rule);
					promoType1Rule.setDailyRedeemLimitPerCustomer(rule.getDailyRedeemLimitPerCustomer() - redeemed);
				}
			} else if (promoRedemption.getPromo().isPromoType3()) {
				PromoType3Rule rule = promoRedemption.getPromo().getPromoType3Rule();
				
				if (rule.getDailyRedeemLimitPerCustomer() > 0) {
					int redeemed = promoRedemptionService.getNumberOfRedemptionsToday(
							promoRedemption.getPromo(), promoRedemption.getCustomer());
					
					promoType3Rule = (PromoType3Rule)ObjectUtils.clone(rule);
					promoType3Rule.setDailyRedeemLimitPerCustomer(rule.getDailyRedeemLimitPerCustomer() - redeemed);
				}
			}
			
			fireTableDataChanged();
		}
		
	}

}