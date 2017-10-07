package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.SuppliersTableModel;
import com.pj.magic.model.Supplier;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;

@Component
public class PaymentSupplierListPanel extends StandardMagicPanel {

	private static final String EDIT_SUPPLIER_ACTION_NAME = "editSupplier";
	
	@Autowired private SupplierService supplierService;
	
	private JTable table;
	private SuppliersTableModel tableModel = new SuppliersTableModel();
	
	public void updateDisplay() {
		List<Supplier> suppliers = supplierService.getAllSuppliers();
		tableModel.setSuppliers(suppliers);
		if (!suppliers.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
	}

	@Override
	protected void initializeComponents() {
		table = new MagicListTable(tableModel);
		
		table.getColumnModel().getColumn(SuppliersTableModel.CODE_COLUMN_INDEX).setPreferredWidth(100);
		table.getColumnModel().getColumn(SuppliersTableModel.NAME_COLUMN_INDEX).setPreferredWidth(500);
		
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++; // first row
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(new JScrollPane(table), c);
	}

	@Override
	protected void registerKeyBindings() {
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), EDIT_SUPPLIER_ACTION_NAME);
		table.getActionMap().put(EDIT_SUPPLIER_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectSupplier();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectSupplier();
			}
		});
		
	}

	protected void selectSupplier() {
		Supplier supplier = tableModel.getSupplier(table.getSelectedRow());
		getMagicFrame().switchToEditSupplierPanel(supplier);
	}

	private void switchToNewSupplierPanel() {
		getMagicFrame().switchToAddNewSupplierPanel();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToRecordsMaintenanceMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton addButton = new MagicToolBarButton("plus", "New");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewSupplierPanel();
			}
		});
		toolBar.add(addButton);
	}
	
}
