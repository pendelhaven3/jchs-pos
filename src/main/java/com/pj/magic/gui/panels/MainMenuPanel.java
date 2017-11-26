package com.pj.magic.gui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicTable;
import com.pj.magic.model.User;
import com.pj.magic.service.LoginService;

@Component
public class MainMenuPanel extends StandardMagicPanel {

    private static final long serialVersionUID = -8283914877511747572L;
    
    private static final String CHANGE_PASSWORD = "Change Password";
	private static final String ADMIN = "Admin";
	private static final String BACKUP_RESTORE_DATA = "Backup/Restore Data";
	private static final String RECORDS_MAINTENANCE = "Records Maintenance";
	private static final String INVENTORY_CHECK = "<html>Inventory Check<br>and Correction</html>";
	private static final String REPORTS = "Reports";
	private static final String STOCK_MOVEMENT = "Stock Movement";
	private static final String SALES_PAYMENT = "Sales Payment";
	private static final String PURCHASE_PAYMENT = "Purchase Payment";
	private static final String PRODUCT_MAINTENANCE_AND_PRICING_SCHEMES = "<html>Product Maintenance<br>and Pricing Schemes</html>";
	private static final String PURCHASES = "Purchases";
	private static final String SALES = "Sales";
	private static final String PROMO_REDEMPTION = "Promo Redemption";
	private static final String PROMO = "Promo";
    private static final String PRODUCTS = "Products";

	private static final Map<String, String> MENU_ITEM_IMAGE_MAP = new HashMap<>();
	
	static {
		MENU_ITEM_IMAGE_MAP.put(ADMIN, "admin");
		MENU_ITEM_IMAGE_MAP.put(SALES_PAYMENT, "money_large");
		MENU_ITEM_IMAGE_MAP.put(PURCHASE_PAYMENT, "money_large");
		MENU_ITEM_IMAGE_MAP.put(PRODUCT_MAINTENANCE_AND_PRICING_SCHEMES, "products");
		MENU_ITEM_IMAGE_MAP.put(STOCK_MOVEMENT, "stock_movement");
		MENU_ITEM_IMAGE_MAP.put(REPORTS, "reports");
		MENU_ITEM_IMAGE_MAP.put(INVENTORY_CHECK, "inventory_check");
		MENU_ITEM_IMAGE_MAP.put(RECORDS_MAINTENANCE, "records");
		MENU_ITEM_IMAGE_MAP.put(BACKUP_RESTORE_DATA, "database_backup");
		MENU_ITEM_IMAGE_MAP.put(CHANGE_PASSWORD, "change_password");
		MENU_ITEM_IMAGE_MAP.put(PROMO, "promo");
		MENU_ITEM_IMAGE_MAP.put(PROMO_REDEMPTION, "promo");
        MENU_ITEM_IMAGE_MAP.put(PRODUCTS, "products");
	}
	
	@Autowired private LoginService loginService;
	
	private MagicTable table;
	private MainMenuTableModel tableModel;
	
	@Override
	protected void initializeComponents() {
		initializeTable();

		table.changeSelection(0, 0, false, false);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void initializeTable() {
		tableModel = new MainMenuTableModel();
		table = new MagicTable(tableModel);
		table.setRowHeight(80);
		table.setShowGrid(false);
		table.setRowSelectionAllowed(false);
		
		table.setDefaultRenderer(Object.class, new TableCellRenderer() {
			
			@Override
			public JPanel getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row,
					int column) {
				JPanel panel = new JPanel(new GridBagLayout());
				
				if (hasFocus) {
					panel.setBackground(new Color(184, 207, 229));
				}
				
				String menuItem = (String)table.getValueAt(row, column);
				if (menuItem == null) {
					return panel;
				}

				JLabel menuLabel = new JLabel();
				menuLabel.setFont(new FontUIResource("Arial", Font.BOLD, 24));
				menuLabel.setText((String)table.getValueAt(row, column));
				
				String imageName = MENU_ITEM_IMAGE_MAP.get(menuItem);
				if (imageName == null) {
					imageName = "buy";
				}
				
				GridBagConstraints c = new GridBagConstraints();
				c.gridx = 0;
				
				JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource(
						"/images/" + imageName + ".png")));
				iconLabel.setPreferredSize(new Dimension(64, 64));
				iconLabel.setVerticalAlignment(JLabel.CENTER);
				iconLabel.setHorizontalAlignment(JLabel.CENTER);
				
				panel.add(iconLabel, c);
				
				c = new GridBagConstraints();
				c.gridx = 1;
				c.weightx = 1.0;
				panel.add(menuLabel, c);
				
				return panel;
			}
		});
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(table, c);
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
		String menuItem = (String)table.getValueAt(table.getSelectedRow(), table.getSelectedColumn());
		if (StringUtils.isEmpty(menuItem)) {
			return;
		}
		
		switch (menuItem) {
		case SALES:
			getMagicFrame().switchToSalesMenuPanel();
			break;
		case PURCHASES:
			getMagicFrame().switchToPurchasesMenuPanel();
			break;
		case PRODUCT_MAINTENANCE_AND_PRICING_SCHEMES:
			getMagicFrame().switchToInventoryMenuPanel();
			break;
		case SALES_PAYMENT:
			getMagicFrame().switchToSalesPaymentsMenuPanel();
			break;
		case PURCHASE_PAYMENT:
            getMagicFrame().switchToPurchasePaymentsMenuPanel();
			break;
		case STOCK_MOVEMENT:
			getMagicFrame().switchToStockMovementMenuPanel();
			break;
		case REPORTS:
			getMagicFrame().switchToReportsMenuPanel();
			break;
		case INVENTORY_CHECK:
			getMagicFrame().switchToInventoryCheckMenuPanel();
			break;
		case RECORDS_MAINTENANCE:
			getMagicFrame().switchToRecordsMaintenanceMenuPanel();
			break;
		case BACKUP_RESTORE_DATA:
			getMagicFrame().switchToBackupDataPanel();
			break;
		case ADMIN:
			getMagicFrame().switchToAdminMenuPanel();
			break;
		case CHANGE_PASSWORD:
			getMagicFrame().switchToChangePasswordPanel();
			break;
		case PROMO:
			getMagicFrame().switchToPromoListPanel();
			break;
		case PROMO_REDEMPTION:
			getMagicFrame().switchToPromoRedemptionPromoListPanel();
			break;
        case PRODUCTS:
            getMagicFrame().switchToProductListPanel();
            break;
		}
	}

	@Override
	protected void doOnBack() {
		// do nothing
	}

	private class MainMenuTableModel extends AbstractTableModel {

        private static final long serialVersionUID = -8941683427387469626L;

        private final List<String> allMenuItems = Arrays.asList(
//				SALES,
                PRODUCTS,
				PURCHASES,
//                PRODUCT_MAINTENANCE_AND_PRICING_SCHEMES,
//				SALES_PAYMENT,
				PURCHASE_PAYMENT,
//				PROMO,
//				PROMO_REDEMPTION,
//				STOCK_MOVEMENT,
				REPORTS,
//				INVENTORY_CHECK,
				RECORDS_MAINTENANCE
//				BACKUP_RESTORE_DATA,
//				ADMIN,
//				CHANGE_PASSWORD
		);
		
		private List<String> menuItems = new ArrayList<>();
		
		@Override
		public int getRowCount() {
			int itemCount = menuItems.size();
			return itemCount / 2 + itemCount % 2;
		}

		public void setUser(User user) {
			menuItems.clear();
			menuItems.addAll(allMenuItems);
			if (!user.isSupervisor()) {
				menuItems.remove(ADMIN);
			}
			fireTableDataChanged();
		}
		
		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			int index = rowIndex + columnIndex * getRowCount(); // * rowCount
			if (index < menuItems.size()) {
				return menuItems.get(index);
			} else {
				return null;
			}
		}
		
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		toolBar.removeAll();
	}

}