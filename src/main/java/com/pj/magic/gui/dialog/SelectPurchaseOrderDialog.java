package com.pj.magic.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.PurchaseOrdersTableModel;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.search.PurchaseOrderSearchCriteria;
import com.pj.magic.service.PurchaseOrderService;

@Component
public class SelectPurchaseOrderDialog extends MagicDialog {

	private static final String SELECT_CUSTOMER_ACTION_NAME = "selectCustomer";
	
	@Autowired
	private PurchaseOrderService purchaseOrderService;
	
	private PurchaseOrder selectedPurchaseOrder;
	private MagicListTable table;
	private PurchaseOrdersTableModel tableModel = new PurchaseOrdersTableModel();
	
	public SelectPurchaseOrderDialog() {
		setSize(500, 400);
		setLocationRelativeTo(null);
		setTitle("Select Purchase Order");
		addContents();
	}

	private void addContents() {
		table = new MagicListTable(tableModel);
		
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), SELECT_CUSTOMER_ACTION_NAME);
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECT_CUSTOMER_ACTION_NAME);
		table.getActionMap().put(SELECT_CUSTOMER_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectPurchaseOrder();
			}
		});

		table.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					selectPurchaseOrder();
				}
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);	
	}

	protected void selectPurchaseOrder() {
		selectedPurchaseOrder = tableModel.getPurchaseOrder(table.getSelectedRow());
		setVisible(false);
	}

	public PurchaseOrder getSelectedPurchaseOrder() {
		return selectedPurchaseOrder;
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		selectedPurchaseOrder = null;
	}
	
	public void searchUnpostedPurchaseOrders() {
		PurchaseOrderSearchCriteria criteria = new PurchaseOrderSearchCriteria();
		criteria.setPosted(false);
		
		List<PurchaseOrder> purchaseOrders = purchaseOrderService.search(criteria);
		tableModel.setPurchaseOrders(purchaseOrders);
		
		if (!purchaseOrders.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
	}
	
}