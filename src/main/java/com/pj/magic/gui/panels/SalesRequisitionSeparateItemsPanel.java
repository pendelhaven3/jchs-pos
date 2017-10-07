package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.SalesRequisitionSeparateItemsTable;
import com.pj.magic.gui.tables.models.SalesRequisitionSeparateItemsTableModel;
import com.pj.magic.service.SalesRequisitionService;
import com.pj.magic.util.ComponentUtil;

@Component
public class SalesRequisitionSeparateItemsPanel extends StandardMagicPanel {
	
	@Autowired private SalesRequisitionService salesRequisitionService;
	@Autowired private SalesRequisitionSeparateItemsTable table;
	@Autowired private SalesRequisitionSeparateItemsTableModel tableModel;
	
	@Override
	public void initializeComponents() {
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay() {
		tableModel.setProducts(salesRequisitionService.getSalesRequisitionSeparateItemsList().getProducts());
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createItemsTableToolBar(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createScrollPane(table), c);
	}
	
	@Override
	protected void registerKeyBindings() {
		table.onDeleteKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeCurrentlySelectedItem();
			}
		});
	}
	
	@Override
	protected void doOnBack() {
		getMagicFrame().switchToSalesMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

	private JPanel createItemsTableToolBar() {
		JPanel panel = new JPanel();
		
		MagicToolBarButton addProductButton = new MagicToolBarButton("plus_small", "Add Product", true);
		addProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				table.addProduct();
			}
		});
		panel.add(addProductButton, BorderLayout.WEST);
		
		MagicToolBarButton removeProductButton = new MagicToolBarButton("minus_small", "Remove Product", true);
		removeProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeCurrentlySelectedItem();
			}
		});
		panel.add(removeProductButton, BorderLayout.WEST);
		
		return panel;
	}

	protected void removeCurrentlySelectedItem() {
		if (table.hasNoSelectedRow() || table.isCurrentlySelectedProductBlank()) {
			return;
		}
		
		if (!confirm("Remove currently selected product?")) {
			return;
		}
		
		table.removeCurrentlySelectedItem();
	}
	
}