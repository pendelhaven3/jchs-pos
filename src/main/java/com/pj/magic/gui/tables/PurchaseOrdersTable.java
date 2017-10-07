package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.panels.PurchaseOrderListPanel;
import com.pj.magic.gui.tables.models.PurchaseOrdersTableModel;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.service.PurchaseOrderService;

@Component
public class PurchaseOrdersTable extends MagicListTable {

	private static final String SELECT_PURCHASE_ORDER_ACTION = "selectPurchaseOrder";
	
	@Autowired private PurchaseOrderService purchaseOrderService;
	@Autowired private PurchaseOrdersTableModel tableModel;
	
	@Autowired
	public PurchaseOrdersTable(PurchaseOrdersTableModel tableModel) {
		super(tableModel);
	}

	@PostConstruct
	public void initialize() {
		registerKeyBindings();
	}
	
	private void registerKeyBindings() {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECT_PURCHASE_ORDER_ACTION);
		getActionMap().put(SELECT_PURCHASE_ORDER_ACTION, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectPurchaseOrder();
			}
		});
		
		addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectPurchaseOrder();
			}
		});
	}

	protected void selectPurchaseOrder() {
		PurchaseOrderListPanel panel = (PurchaseOrderListPanel)
				SwingUtilities.getAncestorOfClass(PurchaseOrderListPanel.class, this);
		panel.displayPurchaseOrderDetails(getCurrentlySelectedPurchaseOrder());
	}

	public void setPurchaseOrders(List<PurchaseOrder> purchaseOrders) {
		tableModel.setPurchaseOrders(purchaseOrders);
		if (!purchaseOrders.isEmpty()) {
			changeSelection(0, 0, false, false);
		}
	}

	public PurchaseOrder getCurrentlySelectedPurchaseOrder() {
		return tableModel.getPurchaseOrder(getSelectedRow());
	}

	public void removeCurrentlySelectedRow() {
		PurchaseOrder purchaseOrder = getCurrentlySelectedPurchaseOrder();
		tableModel.remove(purchaseOrder);
		purchaseOrderService.delete(purchaseOrder);
	}
	
}
