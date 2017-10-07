package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.pj.magic.Constants;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.component.SelectCustomerEllipsisButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.Customer;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoType;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.report.PostedSalesAndProfitReport;
import com.pj.magic.model.report.PostedSalesAndProfitReportItem;
import com.pj.magic.model.search.BadStockReturnSearchCriteria;
import com.pj.magic.model.search.NoMoreStockAdjustmentSearchCriteria;
import com.pj.magic.model.search.PaymentAdjustmentSearchCriteria;
import com.pj.magic.model.search.PromoRedemptionSearchCriteria;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
import com.pj.magic.model.search.SalesReturnSearchCriteria;
import com.pj.magic.service.BadStockReturnService;
import com.pj.magic.service.CustomerService;
import com.pj.magic.service.NoMoreStockAdjustmentService;
import com.pj.magic.service.PaymentAdjustmentService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.PrintServiceImpl;
import com.pj.magic.service.PromoRedemptionService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.service.SalesReturnService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class PostedSalesAndProfitReportPanel extends StandardMagicPanel {

	private static final int TRANSACTION_DATE_COLUMN_INDEX = 0;
	private static final int TRANSACTION_TYPE_COLUMN_INDEX = 1;
	private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 2;
	private static final int CUSTOMER_COLUMN_INDEX = 3;
	private static final int TOTAL_AMOUNT_COLUMN_INDEX = 4;
	private static final int TOTAL_DISCOUNTS_COLUMN_INDEX = 5;
	private static final int NET_AMOUNT_COLUMN_INDEX = 6;
	private static final int NET_COST_COLUMN_INDEX = 7;
	private static final int NET_PROFIT_COLUMN_INDEX = 8;
	
	@Autowired private CustomerService customerService;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	@Autowired private SalesReturnService salesReturnService;
	@Autowired private NoMoreStockAdjustmentService noMoreStockAdjustmentService;
	@Autowired private BadStockReturnService badStockReturnService;
	@Autowired private PaymentAdjustmentService paymentAdjustmentService;
	@Autowired private PromoRedemptionService promoRedemptionService;
	
	private MagicTextField customerCodeField;
	private JLabel customerNameLabel;
	private UtilCalendarModel fromDateModel;
	private UtilCalendarModel toDateModel;
	private JCheckBox treatNoMoreStockAsSalesReturnCheckBox;
	private JButton generateButton;
	private SelectCustomerEllipsisButton selectCustomerButton;
	private MagicListTable table;
	private PostedSalesAndProfitReportItemsTableModel tableModel;
	private Customer customer;
	private JLabel totalNetAmountLabel;
	private JLabel totalCostLabel;
	private JLabel totalProfitLabel;
	
	@Override
	protected void initializeComponents() {
		customerCodeField = new MagicTextField();
		customerCodeField.setMaximumLength(Constants.CUSTOMER_CODE_MAXIMUM_LENGTH);
		
		customerNameLabel = new JLabel();
		
		selectCustomerButton = new SelectCustomerEllipsisButton(selectCustomerDialog, customerCodeField, customerNameLabel);
		
		fromDateModel = new UtilCalendarModel();
		toDateModel = new UtilCalendarModel();
		
		treatNoMoreStockAsSalesReturnCheckBox = new JCheckBox();
		
		generateButton = new JButton("Generate");
		generateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateReport();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(customerCodeField);
		
		initializeTable();
	}

	private void initializeTable() {
		tableModel = new PostedSalesAndProfitReportItemsTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(SALES_INVOICE_NUMBER_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(CUSTOMER_COLUMN_INDEX).setPreferredWidth(200);
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
		
		customer = customerService.findCustomerByCode(customerCodeField.getText());
		if (customer == null) {
			customerNameLabel.setText("-");
		} else {
			customerCodeField.setText(customer.getCode());
			customerNameLabel.setText(customer.getName());
		}
		
		PostedSalesAndProfitReport report = new PostedSalesAndProfitReport();
		report.setItems(retrieveReportItems());
		
		tableModel.setItems(report.getItems());
		if (report.getItems().isEmpty()) {
			showErrorMessage("No records found");
		}
		totalNetAmountLabel.setText(FormatterUtil.formatAmount(report.getTotalNetAmount()));
		totalCostLabel.setText(FormatterUtil.formatAmount(report.getTotalNetCost()));
		totalProfitLabel.setText(FormatterUtil.formatAmount(report.getTotalNetProfit()));
	}

	private List<PostedSalesAndProfitReportItem> retrieveReportItems() {
		List<PostedSalesAndProfitReportItem> items = new ArrayList<>();
		
		SalesInvoiceSearchCriteria salesInvoiceCriteria = new SalesInvoiceSearchCriteria();
		salesInvoiceCriteria.setMarked(true);
		salesInvoiceCriteria.setOrderBy("TRANSACTION_DT, SALES_INVOICE_NO");
		salesInvoiceCriteria.setCustomer(customer);
		salesInvoiceCriteria.setTransactionDateFrom(fromDateModel.getValue().getTime());
		salesInvoiceCriteria.setTransactionDateTo(toDateModel.getValue().getTime());
		
		List<SalesInvoice> salesInvoices = salesInvoiceService.search(salesInvoiceCriteria);
		items.addAll(
			Collections2.transform(salesInvoices, new Function<SalesInvoice, PostedSalesAndProfitReportItem>() {
	
				@Override
				public PostedSalesAndProfitReportItem apply(SalesInvoice input) {
					return new PostedSalesAndProfitReportItem(input);
				}
			})
		);
		
		SalesReturnSearchCriteria salesReturnCriteria = new SalesReturnSearchCriteria();
		salesReturnCriteria.setPosted(true);
		salesReturnCriteria.setCancelled(false);
		salesReturnCriteria.setCustomer(customer);
		salesReturnCriteria.setPostDateFrom(fromDateModel.getValue().getTime());
		salesReturnCriteria.setPostDateTo(toDateModel.getValue().getTime());
		
		List<SalesReturn> salesReturns = salesReturnService.search(salesReturnCriteria);
		items.addAll(
			Collections2.transform(salesReturns, new Function<SalesReturn, PostedSalesAndProfitReportItem>() {
	
				@Override
				public PostedSalesAndProfitReportItem apply(SalesReturn input) {
					return new PostedSalesAndProfitReportItem(input);
				}
			})
		);
		
		BadStockReturnSearchCriteria badStockReturnCriteria = new BadStockReturnSearchCriteria();
		badStockReturnCriteria.setPosted(true);
		badStockReturnCriteria.setCancelled(false);
		badStockReturnCriteria.setCustomer(customer);
		badStockReturnCriteria.setPostDateFrom(fromDateModel.getValue().getTime());
		badStockReturnCriteria.setPostDateTo(toDateModel.getValue().getTime());
		
		List<BadStockReturn> badStockReturns = badStockReturnService.search(badStockReturnCriteria);
		items.addAll(
			Collections2.transform(badStockReturns, 
					new Function<BadStockReturn, PostedSalesAndProfitReportItem>() {
	
				@Override
				public PostedSalesAndProfitReportItem apply(BadStockReturn input) {
					return new PostedSalesAndProfitReportItem(input);
				}
			})
		);
		
		NoMoreStockAdjustmentSearchCriteria noMoreStockAdjustmentCriteria = 
				new NoMoreStockAdjustmentSearchCriteria();
		noMoreStockAdjustmentCriteria.setPosted(true);
		noMoreStockAdjustmentCriteria.setCustomer(customer);
		noMoreStockAdjustmentCriteria.setPostDateFrom(fromDateModel.getValue().getTime());
		noMoreStockAdjustmentCriteria.setPostDateTo(toDateModel.getValue().getTime());
		
		List<NoMoreStockAdjustment> noMoreStockAdjustments = 
				noMoreStockAdjustmentService.search(noMoreStockAdjustmentCriteria);
		items.addAll(
			Collections2.transform(noMoreStockAdjustments, 
					new Function<NoMoreStockAdjustment, PostedSalesAndProfitReportItem>() {
	
				@Override
				public PostedSalesAndProfitReportItem apply(NoMoreStockAdjustment input) {
					return new PostedSalesAndProfitReportItem(input,
							treatNoMoreStockAsSalesReturnCheckBox.isSelected());
				}
			})
		);
		
		PaymentAdjustmentSearchCriteria paymentAdjustmentCriteria = new PaymentAdjustmentSearchCriteria();
		paymentAdjustmentCriteria.setPosted(true);
		paymentAdjustmentCriteria.setCustomer(customer);
		paymentAdjustmentCriteria.setPostDateFrom(fromDateModel.getValue().getTime());
		paymentAdjustmentCriteria.setPostDateTo(toDateModel.getValue().getTime());
		
		List<PaymentAdjustment> paymentAdjustments = 
				paymentAdjustmentService.search(paymentAdjustmentCriteria);
		items.addAll(
			Collections2.transform(paymentAdjustments, 
					new Function<PaymentAdjustment, PostedSalesAndProfitReportItem>() {
	
				@Override
				public PostedSalesAndProfitReportItem apply(PaymentAdjustment input) {
					return new PostedSalesAndProfitReportItem(input);
				}
			})
		);
		
		PromoRedemptionSearchCriteria promoRedemptionCriteria = new PromoRedemptionSearchCriteria();
		promoRedemptionCriteria.setPosted(true);
		promoRedemptionCriteria.setCustomer(customer);
		promoRedemptionCriteria.setCancelled(false);
		promoRedemptionCriteria.setPromoTypes(Arrays.asList(PromoType.PROMO_TYPE_1, PromoType.PROMO_TYPE_2, PromoType.PROMO_TYPE_3));
		promoRedemptionCriteria.setPostDateFrom(fromDateModel.getValue().getTime());
		promoRedemptionCriteria.setPostDateTo(toDateModel.getValue().getTime());
		
		List<PromoRedemption> promoRedemptions = 
				promoRedemptionService.search(promoRedemptionCriteria);
		items.addAll(
			Collections2.transform(promoRedemptions, 
					new Function<PromoRedemption, PostedSalesAndProfitReportItem>() {
	
				@Override
				public PostedSalesAndProfitReportItem apply(PromoRedemption input) {
					return new PostedSalesAndProfitReportItem(input);
				}
			})
		);
		
		Collections.sort(items, new Comparator<PostedSalesAndProfitReportItem>() {

			@Override
			public int compare(PostedSalesAndProfitReportItem o1, PostedSalesAndProfitReportItem o2) {
				int result = o1.getTransactionDate().compareTo(o2.getTransactionDate());
				if (result == 0) {
					return o1.getTransactionNumber().compareTo(o2.getTransactionNumber());
				} else {
					return result;
				}
			}
		});
		
		return items;
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
		mainPanel.add(ComponentUtil.createLabel(120, "Customer Code: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 5;
		mainPanel.add(selectCustomerButton.getFieldsPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Tran. Date From: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createDatePicker(fromDateModel), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Tran. Date To: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createDatePicker(toDateModel), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0; // right space filler
		c.gridx = 6;
		c.gridy = currentRow;
		mainPanel.add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 2;
		mainPanel.add(createNoMoreStockAsSalesReturnPanel(), c);
		
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
		panel.add(ComponentUtil.createLabel(150, "Total Net Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalNetAmountLabel = ComponentUtil.createRightLabel(100, "");
		panel.add(totalNetAmountLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(Box.createHorizontalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Cost:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalCostLabel = ComponentUtil.createRightLabel(100, "");
		panel.add(totalCostLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(140, "Total Profit:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalProfitLabel = ComponentUtil.createRightLabel(100, "");
		panel.add(totalProfitLabel, c);
		
		return panel;
	}

	private JPanel createNoMoreStockAsSalesReturnPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.insets.top = 5;
		panel.add(ComponentUtil.createLabel(210, "Treat NMS as Sales Return: "), c);
		
		c.gridx = 1;
		panel.add(treatNoMoreStockAsSalesReturnCheckBox);

		return panel;
	}

	@Override
	protected void registerKeyBindings() {
	}

	public void updateDisplay() {
		customerCodeField.setText(null);
		customerNameLabel.setText(null);
		fromDateModel.setValue(Calendar.getInstance());
		toDateModel.setValue(Calendar.getInstance());
		treatNoMoreStockAsSalesReturnCheckBox.setSelected(false);
		tableModel.clear();
		customer = null;
		totalNetAmountLabel.setText(null);
		totalCostLabel.setText(null);
		totalProfitLabel.setText(null);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToReportsMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(e -> printPreviewReport());
		toolBar.add(printPreviewButton);
		
		JButton printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(e -> printReport());
		toolBar.add(printButton);
	}

	private void printPreviewReport() {
		PostedSalesAndProfitReport report = createReport();
		printPreviewDialog.updateDisplay(printService.generateReportAsString(report));
		printPreviewDialog.setColumnsPerLine(
				PrintServiceImpl.POSTED_SALES_AND_PROFIT_REPORT_CHARACTERS_PER_LINE);
		printPreviewDialog.setUseCondensedFontForPrinting(true);
		printPreviewDialog.setVisible(true);
	}

	private PostedSalesAndProfitReport createReport() {
		PostedSalesAndProfitReport report = new PostedSalesAndProfitReport();
		report.setCustomer(customer);
		if (fromDateModel.getValue() != null) {
			report.setTransactionDateFrom(fromDateModel.getValue().getTime());
		}
		if (toDateModel.getValue() != null) {
			report.setTransactionDateTo(toDateModel.getValue().getTime());
		}
		report.setItems(tableModel.getItems());
		return report;
	}

	private void printReport() {
		printService.print(createReport());
	}

	private class PostedSalesAndProfitReportItemsTableModel extends ListBackedTableModel<PostedSalesAndProfitReportItem> {

		private final String[] columnNames =
			{"Tran. Date", "Tran. Type", "Ref. No.", "Customer", "Total Amount", "Total Disc.", "Net Amount",
				"Net Cost", "Net Profit"};
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PostedSalesAndProfitReportItem item = getItem(rowIndex);
			switch (columnIndex) {
			case TRANSACTION_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(item.getTransactionDate());
			case TRANSACTION_TYPE_COLUMN_INDEX:
				return item.getTransactionType();
			case SALES_INVOICE_NUMBER_COLUMN_INDEX:
				return item.getTransactionNumber();
			case CUSTOMER_COLUMN_INDEX:
				return item.getCustomer().getName();
			case TOTAL_AMOUNT_COLUMN_INDEX:
				BigDecimal totalAmount = item.getTotalAmount();
				return (totalAmount != null) ? FormatterUtil.formatAmount(item.getTotalAmount()) : null;
			case TOTAL_DISCOUNTS_COLUMN_INDEX:
				BigDecimal totalDiscounts = item.getTotalDiscounts();
				return (totalDiscounts != null) ? FormatterUtil.formatAmount(totalDiscounts) : null;
			case NET_AMOUNT_COLUMN_INDEX:
				BigDecimal netAmount = item.getNetAmount();
				return (netAmount != null) ? FormatterUtil.formatAmount(item.getNetAmount()) : null;
			case NET_COST_COLUMN_INDEX:
				BigDecimal netCost = item.getNetCost();
				return (netCost != null) ? FormatterUtil.formatAmount(netCost) : null;
			case NET_PROFIT_COLUMN_INDEX:
				BigDecimal netProfit = item.getNetProfit();
				return (netProfit != null) ? FormatterUtil.formatAmount(netProfit) : null;
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case TOTAL_AMOUNT_COLUMN_INDEX:
			case TOTAL_DISCOUNTS_COLUMN_INDEX:
			case NET_AMOUNT_COLUMN_INDEX:
			case NET_COST_COLUMN_INDEX:
			case NET_PROFIT_COLUMN_INDEX:
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