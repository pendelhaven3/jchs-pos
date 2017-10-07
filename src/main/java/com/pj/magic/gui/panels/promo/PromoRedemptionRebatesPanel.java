package com.pj.magic.gui.panels.promo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.exception.AlreadyPostedException;
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
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionRebate;
import com.pj.magic.model.PromoRedemptionSalesInvoice;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.PromoRedemptionService;
import com.pj.magic.service.impl.PromoService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.HtmlUtil;

@Component
public class PromoRedemptionRebatesPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(PromoRedemptionRebatesPanel.class);
	
	private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 0;
	private static final int SALES_INVOICE_AMOUNT_COLUMN_INDEX = 1;
	private static final int SALES_INVOICE_ADJUSTED_AMOUNT_COLUMN_INDEX = 2;
	private static final int PAYMENT_ADJUSTMENT_NUMBER_COLUMN_INDEX = 0;
	private static final int PAYMENT_ADJUSTMENT_AMOUNT_COLUMN_INDEX = 1;
	
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	@Autowired private PromoRedemptionService promoRedemptionService;
	@Autowired private SelectSalesInvoicesForPromoRedemptionDialog selectSalesInvoicesForPromoRedemptionDialog;
	@Autowired private PromoService promoService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	
	private PromoRedemption promoRedemption;
	private JLabel promoNameLabel;
	private JLabel promoMechanicsLabel;
	private JLabel promoRedemptionNumberLabel;
	private MagicTextField customerCodeField;
	private JLabel customerNameLabel;
	private EllipsisButton selectCustomerButton;
	private MagicListTable salesInvoicesTable;
	private MagicListTable rebatesTable;
	private SalesInvoicesTableModel salesInvoicesTableModel;
	private RebatesTableModel rebatesTableModel;
	private JLabel totalAmountLabel;
	private JLabel totalRebatesLabel;
	private MagicToolBarButton addSalesInvoiceButton;
	private MagicToolBarButton removeSalesInvoiceButton;
	private MagicToolBarButton postButton;
	private MagicToolBarButton printPreviewButton;
	private MagicToolBarButton printButton;
	
	@Override
	protected void initializeComponents() {
		customerCodeField = new MagicTextField();
		customerCodeField.setMaximumLength(Constants.CUSTOMER_CODE_MAXIMUM_LENGTH);
		
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
					totalRebatesLabel.setText(FormatterUtil.formatAmount(promoRedemption.getTotalRebates()));
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
		
		rebatesTableModel = new RebatesTableModel();
		rebatesTable = new MagicListTable(rebatesTableModel);
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
		promoNameLabel = new JLabel();
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
		promoMechanicsLabel = new JLabel();
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
		promoRedemptionNumberLabel = new JLabel();
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
		customerNameLabel = ComponentUtil.createLabel(200);
		panel.add(customerNameLabel, c);
		
		return panel;
	}

	private JPanel createTablesPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.6;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		panel.add(createTabbedPane("Sales Invoices", createSalesInvoicesPanel()), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.4;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		panel.add(createTabbedPane("Rebates", createRebatesPanel()), c);
		
		return panel;
	}
	
	private JTabbedPane createTabbedPane(String title, JPanel content) {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(title, content);
		return tabbedPane;
	}

	private JPanel createRebatesPanel() {
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
		JScrollPane salesInvoicesScrollPane = new JScrollPane(rebatesTable);
		salesInvoicesScrollPane.setPreferredSize(new Dimension(200, 150));
		panel.add(salesInvoicesScrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		c.gridwidth = 2;
		totalRebatesLabel = ComponentUtil.createRightLabel(100);
		panel.add(ComponentUtil.createGenericPanel(
				new JLabel("Total Rebates: "), totalRebatesLabel, Box.createHorizontalStrut(10)), c);
		
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
				new JLabel("Total Adj. Amount: "), totalAmountLabel, Box.createHorizontalStrut(10)), c);
		
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
				PromoRedemptionSalesInvoice salesInvoice = salesInvoicesTableModel.getItem(selectedRow);
				salesInvoicesTable.clearSelection();
				promoRedemption.getRedemptionSalesInvoices().remove(salesInvoice);
				salesInvoicesTableModel.removeItem(salesInvoice);
				
				if (!salesInvoicesTableModel.getItems().isEmpty()) {
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
			for (SalesInvoice salesInvoice : selectedSalesInvoices) {
				PromoRedemptionSalesInvoice redemptionSalesInvoice = new PromoRedemptionSalesInvoice();
				redemptionSalesInvoice.setParent(promoRedemption);
				redemptionSalesInvoice.setSalesInvoice(salesInvoice);
				promoRedemptionService.save(redemptionSalesInvoice);
				promoRedemption.getRedemptionSalesInvoices().add(redemptionSalesInvoice);
			}
			promoRedemption = promoRedemptionService.getPromoRedemption(promoRedemption.getId());
			salesInvoicesTableModel.setItems(promoRedemption.getRedemptionSalesInvoices());
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
			} catch (SalesInvoiceIneligibleForPromoRedemptionException e) {
				showErrorMessage(MessageFormat.format("Sales Invoice No. {0} is ineligible for promo redemption", 
						e.getSalesInvoice().getSalesInvoiceNumber().toString()));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
	}

	private void printPreview() {
		printPreviewDialog.updateDisplay(printService.generateReportAsString(promoRedemption));
		printPreviewDialog.setVisible(true);
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
		totalAmountLabel.setText(FormatterUtil.formatAmount(promoRedemption.getTotalAmount()));
		totalRebatesLabel.setText(FormatterUtil.formatAmount(promoRedemption.getTotalRebates()));
		
		salesInvoicesTableModel.setItems(promoRedemption.getRedemptionSalesInvoices());
		rebatesTableModel.setItems(promoRedemption.getRebates());
		
		boolean isNew = !promoRedemption.isPosted();
		selectCustomerButton.setEnabled(isNew);
		addSalesInvoiceButton.setEnabled(isNew);
		removeSalesInvoiceButton.setEnabled(isNew);
		postButton.setEnabled(isNew);
		printPreviewButton.setEnabled(true);
		printButton.setEnabled(true);
	}
	
	private void clearDisplay() {
		promoRedemption.setPromo(promoService.getPromo(promoRedemption.getPromo().getId()));
		promoNameLabel.setText(promoRedemption.getPromo().getName());
		promoMechanicsLabel.setText(promoRedemption.getPromo().getMechanicsDescription());
		
		promoRedemptionNumberLabel.setText(null);
		customerCodeField.setEnabled(true);
		customerCodeField.setText(null);
		customerNameLabel.setText(null);
		totalAmountLabel.setText(null);
		totalRebatesLabel.setText(null);
		
		salesInvoicesTableModel.clear();
		rebatesTableModel.clear();
		
		selectCustomerButton.setEnabled(true);
		addSalesInvoiceButton.setEnabled(false);
		removeSalesInvoiceButton.setEnabled(false);
		postButton.setEnabled(false);
		printPreviewButton.setEnabled(false);
		printButton.setEnabled(false);
	}

	private class SalesInvoicesTableModel extends ListBackedTableModel<PromoRedemptionSalesInvoice> {

		private final String[] columnNames = {"SI No.", "Amount", "Adj. Amount"};
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case SALES_INVOICE_AMOUNT_COLUMN_INDEX:
			case SALES_INVOICE_ADJUSTED_AMOUNT_COLUMN_INDEX:
				return Number.class;
			default:
				return String.class;
			}
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			SalesInvoice salesInvoice = getItem(rowIndex).getSalesInvoice();
			switch (columnIndex) {
			case SALES_INVOICE_NUMBER_COLUMN_INDEX:
				return salesInvoice.getSalesInvoiceNumber();
			case SALES_INVOICE_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(
						promoRedemption.getPromo().getPromoType5Rule().getQualifyingAmount(
								createSalesInvoiceCopyWithNoAdjustments(salesInvoice)));
			case SALES_INVOICE_ADJUSTED_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(
						promoRedemption.getPromo().getPromoType5Rule().getQualifyingAmount(salesInvoice));
			default:
				return null;
			}
		}
		
		private SalesInvoice createSalesInvoiceCopyWithNoAdjustments(SalesInvoice salesInvoice) {
			SalesInvoice copy = new SalesInvoice();
			copy.setItems(salesInvoice.getItems());
			return copy;
		}

		public void removeItem(PromoRedemptionSalesInvoice salesInvoice) {
			getItems().remove(salesInvoice);
			promoRedemptionService.delete(salesInvoice);
			fireTableDataChanged();
		}

		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}
		
	}
	
	private class RebatesTableModel extends ListBackedTableModel<PromoRedemptionRebate> {

		private final String[] columnNames = {"Payment Adj. No.", "Amount"};
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return (columnIndex == SALES_INVOICE_AMOUNT_COLUMN_INDEX) ? Number.class : String.class;
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PaymentAdjustment paymentAdjustment = getItem(rowIndex).getPaymentAdjustment();
			switch (columnIndex) {
			case PAYMENT_ADJUSTMENT_NUMBER_COLUMN_INDEX:
				return paymentAdjustment.getPaymentAdjustmentNumber();
			case PAYMENT_ADJUSTMENT_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(paymentAdjustment.getAmount());
			default:
				return null;
			}
		}
		
		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}
		
	}

}