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

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.MagicSubmenuTable;
import com.pj.magic.model.User;
import com.pj.magic.service.LoginService;
import com.pj.magic.util.ListUtil;

@Component
public class RecordsMaintenanceMenuPanel extends MenuPanel {

    private static final long serialVersionUID = 6070286737713137704L;
    private static final String AREA_LIST = "Area List";
	private static final String ADJUSTMENT_TYPE_LIST = "Adjustment Type List";
	private static final String PRODUCT_CATEGORY_LIST = "Product Category List";
	private static final String PAYMENT_TERM_LIST = "Payment Term List";
	private static final String MANUFACTURER_LIST = "Manufacturer List";
	private static final String SUPPLIER_LIST = "Supplier List";
	private static final String CUSTOMER_LIST = "Customer List";
	private static final String CREDIT_CARD_LIST = "Credit Card List";
	private static final String PURCHASE_PAYMENT_ADJUSTMENT_TYPE_LIST = "Purchase Payment Adjustment Type List";
	
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
		case CUSTOMER_LIST:
			getMagicFrame().switchToCustomerListPanel();
			break;
		case SUPPLIER_LIST:
			getMagicFrame().switchToSupplierListPanel();
			break;
		case MANUFACTURER_LIST:
			getMagicFrame().switchToManufacturerListPanel();
			break;
		case PAYMENT_TERM_LIST:
			getMagicFrame().switchToPaymentTermListPanel();
			break;
		case PRODUCT_CATEGORY_LIST:
			getMagicFrame().switchToProductCategoryListPanel();
			break;
		case ADJUSTMENT_TYPE_LIST:
			getMagicFrame().switchToAdjustmentTypeListPanel();
			break;
		case AREA_LIST:
			getMagicFrame().switchToAreaListPanel();
			break;
		case CREDIT_CARD_LIST:
			getMagicFrame().switchToCreditCardListPanel();
			break;
		case PURCHASE_PAYMENT_ADJUSTMENT_TYPE_LIST:
			getMagicFrame().switchToPurchasePaymentAdjustmentTypeListPanel();
			break;
		}
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	private class MainMenuTableModel extends AbstractTableModel {

        private static final long serialVersionUID = -8243372847458489940L;

        private final List<String> allMenuItems = ListUtil.asSortedList(Arrays.asList(
//				CUSTOMER_LIST,
				SUPPLIER_LIST,
//				MANUFACTURER_LIST,
				PAYMENT_TERM_LIST,
//				PRODUCT_CATEGORY_LIST,
//				ADJUSTMENT_TYPE_LIST,
//				AREA_LIST,
				CREDIT_CARD_LIST,
				PURCHASE_PAYMENT_ADJUSTMENT_TYPE_LIST
		));
		
		private List<String> menuItems = new ArrayList<>();
		
		@Override
		public int getRowCount() {
			return menuItems.size();
		}

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return menuItems.get(rowIndex);
		}
		
		public void setUser(User user) {
			menuItems.clear();
			menuItems.addAll(allMenuItems);
			fireTableDataChanged();
		}
		
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}