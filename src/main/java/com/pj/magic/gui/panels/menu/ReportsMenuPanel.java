package com.pj.magic.gui.panels.menu;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.MagicFrame;
import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.MagicSubmenuTable;
import com.pj.magic.model.User;
import com.pj.magic.service.LoginService;

@Component
public class ReportsMenuPanel extends MenuPanel {

    private static final long serialVersionUID = -1041462307872718651L;
    
    private static final String POSTED_SALES_AND_PROFIT_REPORT = "Posted Sales and Profit Report";
	private static final String UNPAID_SALES_INVOICES_LIST = "Unpaid Sales Invoices List";
	private static final String CUSTOMER_SALES_SUMMARY_REPORT = "Customer Sales Summary Report";
	private static final String UNPAID_RECEIVING_RECEIPTS_LIST = "Unpaid Receiving Receipts List";
	private static final String PURCHASE_PAYMENT_BANK_TRANSFERS_REPORT = 
			"Purchase Payment Bank Transfers Report";
	private static final String PURCHASE_PAYMENT_CHECK_PAYMENTS_REPORT = 
			"Purchase Payment Check Payments Report";
	private static final String PURCHASE_PAYMENT_CREDIT_CARD_PAYMENTS_REPORT = 
			"Purchase Payment Credit Card Payments Report";
	private static final String SALES_BY_MANUFACTURER_REPORT = "Sales By Manufacturer Report";
	private static final String CUSTOMER_CHECK_PAYMENTS_REPORT = "Customer Check Payments Report";
	private static final String DISBURSEMENT_REPORT = "Disbursement Report";
	private static final String STOCK_OFFTAKE_REPORT = "Stock Offtake Report";
	private static final String PILFERAGE_REPORT = "Pilferage Report";
    private static final String EWT_REPORT = "BIR EWT Report";
    private static final String BIR_FORM_2307_REPORT_LIST = "BIR Form 2307 Report List";

	@Autowired private LoginService loginService;
	
	private MagicListTable table;
	private MainMenuTableModel tableModel;
	
	@Override
	protected void initializeComponents() {
		tableModel = new MainMenuTableModel();
		table = new MagicSubmenuTable(tableModel);
		
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(Object.class, renderer);

		table.changeSelection(0, 0, false, false);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		
		table.setTableHeader(null);
		table.setShowGrid(false);
		
		mainPanel.add(new JScrollPane(table), c);
	}

	@Override
	protected void registerKeyBindings() {
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectMenuItem();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectMenuItem();
			}
		});
	}

	public void updateDisplay() {
		tableModel.setUser(loginService.getLoggedInUser());
		table.changeSelection(0, 0, false, false);
	}
	
	private void selectMenuItem() {
		switch ((String)table.getValueAt(table.getSelectedRow(), 0)) {
		case UNPAID_SALES_INVOICES_LIST:
			getMagicFrame().switchToUnpaidSalesInvoicesListPanel();
			break;
		case POSTED_SALES_AND_PROFIT_REPORT:
			getMagicFrame().switchToPostedSalesAndProfitReportPanel();
			break;
		case CUSTOMER_SALES_SUMMARY_REPORT:
			getMagicFrame().switchToCustomerSalesSummaryReportPanel();
			break;
		case UNPAID_RECEIVING_RECEIPTS_LIST:
			getMagicFrame().switchToUnpaidReceivingReceiptsListPanel();
			break;
		case PURCHASE_PAYMENT_BANK_TRANSFERS_REPORT:
			getMagicFrame().switchToPurchasePaymentBankTransfersReportPanel();
			break;
		case PURCHASE_PAYMENT_CHECK_PAYMENTS_REPORT:
			getMagicFrame().switchToPurchasePaymentCheckPaymentsReportPanel();
			break;
		case PURCHASE_PAYMENT_CREDIT_CARD_PAYMENTS_REPORT:
			getMagicFrame().switchToPurchasePaymentCreditCardPaymentsReportPanel();
			break;
		case SALES_BY_MANUFACTURER_REPORT:
			getMagicFrame().switchToSalesByManufacturerReportPanel();
			break;
		case CUSTOMER_CHECK_PAYMENTS_REPORT:
			getMagicFrame().switchToCustomerCheckPaymentsReportPanel();
			break;
		case DISBURSEMENT_REPORT:
			getMagicFrame().switchToDisbursementReportPanel();
			break;
		case STOCK_OFFTAKE_REPORT:
			getMagicFrame().switchToStockOfftakeReportPanel();
			break;
		case PILFERAGE_REPORT:
			getMagicFrame().switchToPilferageReportPanel();
			break;
        case EWT_REPORT:
            getMagicFrame().switchPanel(MagicFrame.EWT_REPORT_PANEL);
            break;
        case BIR_FORM_2307_REPORT_LIST:
            getMagicFrame().switchPanel(MagicFrame.BIR_FORM_2307_REPORT_LIST_PANEL);
            break;
		}
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	private class MainMenuTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 8368258984397052646L;

        private final List<String> allMenuItems = Arrays.asList(
//				UNPAID_SALES_INVOICES_LIST,
//				POSTED_SALES_AND_PROFIT_REPORT,
//				CUSTOMER_SALES_SUMMARY_REPORT,
//				CUSTOMER_CHECK_PAYMENTS_REPORT,
				UNPAID_RECEIVING_RECEIPTS_LIST,
//				DISBURSEMENT_REPORT,
//				PURCHASE_PAYMENT_BANK_TRANSFERS_REPORT,
				PURCHASE_PAYMENT_CHECK_PAYMENTS_REPORT,
//				PURCHASE_PAYMENT_CREDIT_CARD_PAYMENTS_REPORT,
//				SALES_BY_MANUFACTURER_REPORT,
//				STOCK_OFFTAKE_REPORT,
//				PILFERAGE_REPORT
				EWT_REPORT,
				BIR_FORM_2307_REPORT_LIST
		);
		
		private List<String> menuItems = new ArrayList<>();
		
		@Override
		public int getRowCount() {
			return menuItems.size();
		}

		public void setUser(User user) {
			menuItems.clear();
			menuItems.addAll(allMenuItems);
			if (!user.isSupervisor()) {
				menuItems.remove(POSTED_SALES_AND_PROFIT_REPORT);
				menuItems.remove(CUSTOMER_SALES_SUMMARY_REPORT);
			}
			fireTableDataChanged();
		}
		
		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return menuItems.get(rowIndex);
		}
		
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}