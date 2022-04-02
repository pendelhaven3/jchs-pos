package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.CustomAction;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchPurchaseOrdersDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.search.PurchaseOrderSearchCriteria;
import com.pj.magic.service.PurchaseOrderService;

@Component
public class PurchaseOrderBySpecialCodesListPanel extends StandardMagicPanel {
	
	@Autowired private SearchPurchaseOrdersDialog searchPurchaseOrdersDialog;
	@Autowired private PurchaseOrderService purchaseOrderService;
	
	private MagicListTable table;
	private PurchaseOrdersTableModel tableModel = new PurchaseOrdersTableModel();
	
	@Override
	public String getTitle() {
		return "Purchase Order by Special Codes List";
	}
	
	@Override
	public void initializeComponents() {
		table = new MagicListTable(tableModel);
		
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay() {
		List<PurchaseOrder> purchaseOrders = purchaseOrderService.getAllNonPostedPurchaseOrders();
		tableModel.setItems(purchaseOrders);
		if (!purchaseOrders.isEmpty()) {
			table.selectFirstRow();
		}
		
		searchPurchaseOrdersDialog.updateDisplay();
	}

	public void displayPurchaseOrderDetails(PurchaseOrder purchaseOrder) {
		getMagicFrame().switchToPurchaseOrderPanel(purchaseOrder);
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets.top = 5;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = c.gridy = 0;
		
		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, c);
	}
	
	@Override
	protected void registerKeyBindings() {
		table.onEnterKeyAndDoubleClick(new CustomAction() {
			
			@Override
			public void doAction() {
				selectPurchaseOrder();
			}
		});
		
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}

	protected void selectPurchaseOrder() {
		getMagicFrame().switchToPurchaseOrderBySpecialCodesPanel(getSelectedItem());
	}
	
	private PurchaseOrder getSelectedItem() {
        return tableModel.getItem(table.getSelectedRow());
    }
	
	private void switchToNewPurchaseOrderPanel() {
		getMagicFrame().switchToPurchaseOrderBySpecialCodesPanel(purchaseOrderService.newPurchaseOrder());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPurchasesMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton addButton = new MagicToolBarButton("plus", "New");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewPurchaseOrderPanel();
			}
		});
		toolBar.add(addButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchPurchaseOrders();
			}
		});
		
		toolBar.add(searchButton);
	}

	private void searchPurchaseOrders() {
		searchPurchaseOrdersDialog.setVisible(true);
		
		PurchaseOrderSearchCriteria criteria = searchPurchaseOrdersDialog.getSearchCriteria();
		if (criteria != null) {
			List<PurchaseOrder> purchaseOrders = purchaseOrderService.search(criteria);
			tableModel.setItems(purchaseOrders);
			if (!purchaseOrders.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}

	private static final int PURCHASE_ORDER_NUMBER_COLUMN_INDEX = 0;
	private static final int SUPPLIER_COLUMN_INDEX = 1;
	private static final int STATUS_COLUMN_INDEX = 2;
	
	private static final String[] COLUMN_NAMES = {"PO No.", "Supplier", "Status"};
	
	private class PurchaseOrdersTableModel extends ListBackedTableModel<PurchaseOrder> {

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PurchaseOrder purchaseOrder = getItem(rowIndex);
			switch (columnIndex) {
			case PURCHASE_ORDER_NUMBER_COLUMN_INDEX:
				return String.valueOf(purchaseOrder.getPurchaseOrderNumber());
			case SUPPLIER_COLUMN_INDEX:
				return purchaseOrder.getSupplier().getName();
			case STATUS_COLUMN_INDEX:
				return purchaseOrder.getStatus();
			default:
				throw new RuntimeException("Fetch invalid column index: " + columnIndex);
			}
		}

		@Override
		protected String[] getColumnNames() {
			return COLUMN_NAMES;
		}

	}
	
}
