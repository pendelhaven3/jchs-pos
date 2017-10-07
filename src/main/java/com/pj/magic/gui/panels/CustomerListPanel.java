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
import com.pj.magic.gui.dialog.SearchCustomersDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.CustomersTableModel;
import com.pj.magic.model.Customer;
import com.pj.magic.model.search.CustomerSearchCriteria;
import com.pj.magic.service.CustomerService;
import com.pj.magic.util.ComponentUtil;

@Component
public class CustomerListPanel extends StandardMagicPanel {

	private static final String EDIT_CUSTOMER_ACTION_NAME = "editCustomer";
	
	@Autowired private CustomerService customerService;
	@Autowired private CustomersTableModel tableModel;
	@Autowired private SearchCustomersDialog searchCustomersDialog;
	
	private JTable table;
	
	public void updateDisplay() {
		List<Customer> customers = customerService.getAllCustomers();
		tableModel.setCustomers(customers);
		if (!customers.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
	}

	@Override
	protected void initializeComponents() {
		table = new MagicListTable(tableModel);
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
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), EDIT_CUSTOMER_ACTION_NAME);
		table.getActionMap().put(EDIT_CUSTOMER_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectCustomer();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectCustomer();
			}
		});
	}

	protected void selectCustomer() {
		Customer customer = tableModel.getCustomer(table.getSelectedRow());
		getMagicFrame().switchToEditCustomerPanel(customer);
	}

	private void switchToNewCustomerPanel() {
		getMagicFrame().switchToAddNewCustomerPanel();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToRecordsMaintenanceMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton postButton = new MagicToolBarButton("plus", "New");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewCustomerPanel();
			}
		});
		
		toolBar.add(postButton);
		
		JButton showAllButton = new MagicToolBarButton("all", "Show All");
		showAllButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showAllCustomers();
			}
		});
		toolBar.add(showAllButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchCustomers();
			}
		});
		
		toolBar.add(searchButton);
	}

	private void showAllCustomers() {
		List<Customer> customers = customerService.getAllCustomers();
		tableModel.setCustomers(customers);
		if (!customers.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
		table.requestFocusInWindow();
		searchCustomersDialog.updateDisplay();
	}

	private void searchCustomers() {
		searchCustomersDialog.setVisible(true);
		
		CustomerSearchCriteria criteria = searchCustomersDialog.getSearchCriteria();
		if (criteria != null) {
			List<Customer> customers = customerService.searchCustomers(criteria);
			tableModel.setCustomers(customers);
			if (!customers.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}
	
}
