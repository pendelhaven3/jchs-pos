package com.pj.magic.gui.panels.menu;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.MagicSubmenuTable;

@Component
public class PurchasesMenuPanel extends MenuPanel {

    private static final long serialVersionUID = -551199834687614065L;
    
    private static final String PRODUCT_CANVASS = "Product Canvass";
	private static final String RECEIVING_RECEIPT = "Receiving Receipt";
	private static final String PURCHASE_ORDER = "Purchase Order";
	private static final String PURCHASE_RETURN = "Purchase Return - Good Stock";
	private static final String BAD_PURCHASE_RETURN = "Purchase Return - Bad Stock";
	private static final String PRODUCT_COST_LIST = "Product Cost List";
	private static final String PURCHASE_ORDER_BY_SPECIAL_CODES = "Purchase Order by Special Codes";
	private static final String RECEIVE_DELIVERY = "Receive Delivery";

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
		table.changeSelection(0, 0, false, false);
	}
	
	private void selectMenuItem() {
		switch ((String)table.getValueAt(table.getSelectedRow(), 0)) {
		case PURCHASE_ORDER:
			getMagicFrame().switchToPurchaseOrderListPanel();
			break;
		case RECEIVING_RECEIPT:
			getMagicFrame().switchToReceivingReceiptListPanel();
			break;
		case PRODUCT_CANVASS:
			getMagicFrame().switchToProductCanvassPanel();
			break;
		case PURCHASE_RETURN:
			getMagicFrame().switchToPurchaseReturnListPanel();
			break;
		case BAD_PURCHASE_RETURN:
			getMagicFrame().switchToPurchaseReturnBadStockListPanel();
			break;
		case PRODUCT_COST_LIST:
			getMagicFrame().switchToProductCostListPanel();
			break;
		case PURCHASE_ORDER_BY_SPECIAL_CODES:
			getMagicFrame().switchToPurchaseOrderBySpecialCodesListPanel();
			break;
		case RECEIVE_DELIVERY:
			getMagicFrame().switchToReceiveDeliveryListPanel();
			break;
		}
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	private class MainMenuTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 2994571598043738315L;
        
        private final List<String> menuItems = Arrays.asList(
				PURCHASE_ORDER,
				RECEIVING_RECEIPT,
				PRODUCT_CANVASS,
				PURCHASE_RETURN,
				BAD_PURCHASE_RETURN,
				PRODUCT_COST_LIST,
				PURCHASE_ORDER_BY_SPECIAL_CODES,
				RECEIVE_DELIVERY
		);
		
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
		
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}