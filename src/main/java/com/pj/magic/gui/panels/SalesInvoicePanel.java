package com.pj.magic.gui.panels;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.MagicFrame;
import com.pj.magic.gui.component.ExcelFileFilter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.AvailedPromoRewardsDialog;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.dialog.PromoQualifyingAmountsDialog;
import com.pj.magic.gui.dialog.SalesInvoiceStatusDialog;
import com.pj.magic.gui.dialog.SetDiscountsForAllItemsDialog;
import com.pj.magic.gui.tables.SalesInvoiceItemsTable;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.service.ExcelService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FileUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.HtmlUtil;

@Component
public class SalesInvoicePanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(SalesInvoicePanel.class);
	
	@Autowired private SalesInvoiceItemsTable itemsTable;
	@Autowired private PrintService printService;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private SalesInvoiceStatusDialog salesInvoiceStatusDialog;
	@Autowired private ExcelService excelService;
	@Autowired private PromoQualifyingAmountsDialog promoQualifyingAmountsDialog;
	@Autowired private AvailedPromoRewardsDialog availedPromoRewardsDialog;
	@Autowired private SetDiscountsForAllItemsDialog setDiscountsForAllItemsDialog;
	
	private SalesInvoice salesInvoice;
	private JLabel salesInvoiceNumberField;
	private JLabel customerNameField;
	private JLabel transactionDateField;
	private JLabel encoderField;
	private JLabel pricingSchemeNameField;
	private JLabel modeField;
	private JLabel remarksField;
	private JLabel printedField;
	private JLabel paymentTermNameField;
	private JLabel statusField;
	private JLabel totalItemsField;
	private JLabel totalAmountField;
	private JLabel totalDiscountedAmountField;
	private JLabel totalNetAmountField;
	private JButton cancelButton;
	private JButton showDiscountsButton;
	private JButton setDiscountsForAllButton;
	private boolean showDiscounts;
	private JFileChooser excelFileChooser;
	
	@Override
	protected void initializeComponents() {
		statusField = new JLabel();
		statusField.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				salesInvoiceStatusDialog.updateDisplay(salesInvoice);
				salesInvoiceStatusDialog.setVisible(true);
			}
			
		});
		statusField.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		excelFileChooser = new JFileChooser();
		excelFileChooser.setCurrentDirectory(new File(FileUtil.getDesktopFolderPath()));
		excelFileChooser.setFileFilter(ExcelFileFilter.getInstance());
		
		focusOnComponentWhenThisPanelIsDisplayed(itemsTable);
		updateTotalsPanelWhenItemsTableChanges();
	}

	public void updateDisplay(SalesInvoice salesInvoice) {
		showDiscounts = false;
		updateDisplay(salesInvoice, showDiscounts);
	}
	
	public void updateDisplay(SalesInvoice salesInvoice, boolean showDiscountDetails) {
		this.salesInvoice = salesInvoice = salesInvoiceService.get(salesInvoice.getId());
		
		salesInvoiceNumberField.setText(salesInvoice.getSalesInvoiceNumber().toString());
		customerNameField.setText(salesInvoice.getCustomer().getCode() + " - " + salesInvoice.getCustomer().getName());
		transactionDateField.setText(FormatterUtil.formatDate(salesInvoice.getTransactionDate()));
		encoderField.setText(salesInvoice.getEncoder().getUsername());
		pricingSchemeNameField.setText(salesInvoice.getPricingScheme().getName());
		modeField.setText(salesInvoice.getMode());
		remarksField.setText(salesInvoice.getRemarks());
		printedField.setText(salesInvoice.isPrinted() ? "Yes" : "No");
		paymentTermNameField.setText(salesInvoice.getPaymentTerm().getName());
		statusField.setText(HtmlUtil.blueUnderline(salesInvoice.getStatus()));
		totalItemsField.setText(String.valueOf(salesInvoice.getTotalNumberOfItems()));
		totalAmountField.setText(FormatterUtil.formatAmount(salesInvoice.getTotalAmount()));
		totalDiscountedAmountField.setText(FormatterUtil.formatAmount(salesInvoice.getTotalDiscounts()));
		totalNetAmountField.setText(FormatterUtil.formatAmount(salesInvoice.getTotalNetAmount()));
		
		itemsTable.clearSelection(); // to avoid screwing product quantity/price info table listener
		itemsTable.setSalesInvoice(salesInvoice, showDiscountDetails);
		if (showDiscountDetails && salesInvoice.isNew()) {
			itemsTable.selectAndEditCellAt(0, SalesInvoiceItemsTable.DISCOUNT_1_COLUMN_INDEX);
		} else {
			itemsTable.changeSelection(0, 0, false, false);
		}
		
		cancelButton.setEnabled(salesInvoice.isNew());
		setDiscountsForAllButton.setEnabled(salesInvoice.isNew());
	}

	@Override
	protected void registerKeyBindings() {
		// none
	}
	
	@Override
	protected void doOnBack() {
		getMagicFrame().back(MagicFrame.SALES_INVOICES_LIST_PANEL);
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 30), c);

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "SI No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		salesInvoiceNumberField = ComponentUtil.createLabel(200, "");
		mainPanel.add(salesInvoiceNumberField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(140, "Transaction Date:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		transactionDateField = ComponentUtil.createLabel(150, "");
		mainPanel.add(transactionDateField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Customer Name:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		customerNameField = ComponentUtil.createLabel(300, "");
		mainPanel.add(customerNameField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Encoder:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		encoderField = ComponentUtil.createLabel(150, "");
		mainPanel.add(encoderField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Payment Term:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentTermNameField = ComponentUtil.createLabel(100);
		mainPanel.add(paymentTermNameField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Status:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusField.setPreferredSize(new Dimension(150, 20));
		mainPanel.add(statusField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Pricing Scheme:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		pricingSchemeNameField = ComponentUtil.createLabel(100);
		mainPanel.add(pricingSchemeNameField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Mode:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		modeField = ComponentUtil.createLabel(100);
		mainPanel.add(modeField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Remarks:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField = ComponentUtil.createLabel(200);
		mainPanel.add(remarksField, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Printed:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		printedField = ComponentUtil.createLabel(100);
		mainPanel.add(printedField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 10), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		showDiscountsButton = new MagicToolBarButton("discount", "Show/Hide Discount Details");
		showDiscountsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showDiscounts = !showDiscounts;
				updateDisplay(salesInvoice, showDiscounts);
			}
		});
		toolBar.add(showDiscountsButton);

		setDiscountsForAllButton = new MagicToolBarButton("discount_all", "Set Discounts For All Items");
		setDiscountsForAllButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSetDiscountsForAllItemsDialog();
			}
		});
		toolBar.add(setDiscountsForAllButton);
		
		cancelButton = new MagicToolBarButton("cancel", "Cancel");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelSalesInvoice();
			}
		});
		toolBar.add(cancelButton);
		
		JButton printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreviewDialog.updateDisplay(printService.generateReportAsString(salesInvoice));
				printPreviewDialog.setVisible(true);
			}
		});
		toolBar.add(printPreviewButton);
		
		JButton printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(e -> printSalesInvoice());
		toolBar.add(printButton);
		
		JButton markAsPrintedButton = new MagicToolBarButton("mark_print", "Mark As Printed");
		markAsPrintedButton.addActionListener(e -> markSalesInvoiceAsPrinted());
		toolBar.add(markAsPrintedButton);
		
		JButton printBirFormCashButton = 
				new MagicToolBarButton("print_bir_form_cash", "Print BIR form (Cash)");
		printBirFormCashButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printService.printBirCashForm(salesInvoice);
			}
		});
		toolBar.add(printBirFormCashButton);
		
		JButton printBirFormChargeButton = 
				new MagicToolBarButton("print_bir_form_charge", "Print BIR form (Charge)");
		printBirFormChargeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printService.printBirChargeForm(salesInvoice);
			}
		});
		toolBar.add(printBirFormChargeButton);
		
		JButton copyButton = new MagicToolBarButton("copy", "Create New Sales Requisition Based On Sales Invoice");
		copyButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				createNewSalesRequisitionBasedOnSalesInvoice();
			}
		});
		toolBar.add(copyButton);
		
		JButton toExcelButton = new MagicToolBarButton("excel", "Generate Excel spreadsheet from Sales Invoice");
		toExcelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateExcelSpreadsheetFromSalesInvoice();
			}
		});
		toolBar.add(toExcelButton);
		
		MagicToolBarButton showPromoQualifyingAmountsButton = 
				new MagicToolBarButton("qualify", "Show Qualifying Amounts for Promos");
		showPromoQualifyingAmountsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showPromoQualifyingAmountsDialog(salesInvoice);
			}
		});
		toolBar.add(showPromoQualifyingAmountsButton);
		
		JButton showAvailedPromoRewardsButton = 
				new MagicToolBarButton("present", "Show Availed Promo Rewards");
		showAvailedPromoRewardsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showAvailedPromoRewards();
			}
		});
		toolBar.add(showAvailedPromoRewardsButton);
	}

	private void printSalesInvoice() {
		salesInvoice = salesInvoiceService.get(salesInvoice.getId());
		printService.print(salesInvoice);
		updateDisplay(salesInvoice);
	}

	private void markSalesInvoiceAsPrinted() {
		salesInvoiceService.markAsPrinted(salesInvoice);
		showMessage("Saved");
		updateDisplay(salesInvoice);
	}

	protected void openSetDiscountsForAllItemsDialog() {
		setDiscountsForAllItemsDialog.updateDisplay(salesInvoice);
		setDiscountsForAllItemsDialog.setVisible(true);
		
		updateDisplay(salesInvoice);
	}

	private void showAvailedPromoRewards() {
		availedPromoRewardsDialog.updateDisplay(salesInvoice);
		availedPromoRewardsDialog.setVisible(true);
	}

	private void showPromoQualifyingAmountsDialog(SalesInvoice salesInvoice) {
		promoQualifyingAmountsDialog.updateDisplay(salesInvoice);
		promoQualifyingAmountsDialog.setVisible(true);
	}

	private void generateExcelSpreadsheetFromSalesInvoice() {
		excelFileChooser.setSelectedFile(new File(generateDefaultSpreadsheetName() + ".xlsx"));
		
		int returnVal = excelFileChooser.showSaveDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		try (
			Workbook workbook = excelService.generateSpreadsheet(salesInvoice);
			FileOutputStream out = new FileOutputStream(excelFileChooser.getSelectedFile());
		) {
			workbook.write(out);
			showMessage("Excel spreadsheet generated successfully");
		} catch (IOException e) {
			showErrorMessage("Unexpected error during excel generation");
		}
	}

	private String generateDefaultSpreadsheetName() {
		return new StringBuilder()
			.append(salesInvoice.getCustomer().getName())
			.append(" - ")
			.append(new SimpleDateFormat("MMM-dd-yyyy").format(salesInvoice.getTransactionDate()))
			.append(" - SI ")
			.append(salesInvoice.getSalesInvoiceNumber())
			.toString();
	}

	protected void cancelSalesInvoice() {
		if (confirm("Cancel this Sales Invoice?")) {
			try {
				salesInvoiceService.cancel(salesInvoice);
				showMessage("Sales Invoice cancelled");
				updateDisplay(salesInvoice, showDiscounts);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred");
			}
		}
	}

	private void createNewSalesRequisitionBasedOnSalesInvoice() {
		SalesRequisition salesRequisition = salesInvoiceService.createSalesRequisitionFromSalesInvoice(salesInvoice);
		getMagicFrame().switchToSalesRequisitionPanel(salesRequisition);
	}

	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsField = ComponentUtil.createLabel(60, "");
		panel.add(totalItemsField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = ComponentUtil.createRightLabel(100, "");
		panel.add(totalAmountField, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createFiller(10, 1), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Disc. Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalDiscountedAmountField = ComponentUtil.createRightLabel(100, "");
		panel.add(totalDiscountedAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(140, "Total Net Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalNetAmountField = ComponentUtil.createRightLabel(100, "");
		panel.add(totalNetAmountField, c);
		
		return panel;
	}
	
	private void updateTotalsPanelWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				switch (e.getColumn()) {
				case SalesInvoiceItemsTable.DISCOUNT_1_COLUMN_INDEX:
				case SalesInvoiceItemsTable.DISCOUNT_2_COLUMN_INDEX:
				case SalesInvoiceItemsTable.DISCOUNT_3_COLUMN_INDEX:
				case SalesInvoiceItemsTable.FLAT_RATE_DISCOUNT_COLUMN_INDEX:
					totalDiscountedAmountField.setText(
							FormatterUtil.formatAmount(salesInvoice.getTotalDiscounts()));
					totalNetAmountField.setText(FormatterUtil.formatAmount(salesInvoice.getTotalNetAmount()));
					break;
				}
			}
		});
	}
	
}