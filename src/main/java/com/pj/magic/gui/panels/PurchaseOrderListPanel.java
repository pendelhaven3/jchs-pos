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

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchPurchaseOrdersDialog;
import com.pj.magic.gui.tables.PurchaseOrdersTable;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.search.PurchaseOrderSearchCriteria;
import com.pj.magic.service.PurchaseOrderService;
import com.pj.magic.util.ComponentUtil;

@Component
public class PurchaseOrderListPanel extends StandardMagicPanel {
	
	@Autowired private PurchaseOrdersTable table;
	@Autowired private SearchPurchaseOrdersDialog searchPurchaseOrdersDialog;
	@Autowired private PurchaseOrderService purchaseOrderService;
	
	@Override
	public void initializeComponents() {
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay() {
		List<PurchaseOrder> purchaseOrders = purchaseOrderService.getAllNonPostedPurchaseOrders();
		table.setPurchaseOrders(purchaseOrders);
		searchPurchaseOrdersDialog.updateDisplay();
	}

	public void displayPurchaseOrderDetails(PurchaseOrder stockQuantityConversion) {
		getMagicFrame().switchToPurchaseOrderPanel(stockQuantityConversion);
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		
		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, c);
	}
	
	@Override
	protected void registerKeyBindings() {
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}
	
	private void switchToNewPurchaseOrderPanel() {
		getMagicFrame().switchToPurchaseOrderPanel(purchaseOrderService.newPurchaseOrder());
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
			table.setPurchaseOrders(purchaseOrders);
			if (!purchaseOrders.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}

}
