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
import com.pj.magic.gui.dialog.SearchPaymentsDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.PaymentsTableModel;
import com.pj.magic.model.Payment;
import com.pj.magic.model.search.PaymentSearchCriteria;
import com.pj.magic.service.PaymentService;
import com.pj.magic.util.ComponentUtil;

@Component
public class PaymentListPanel extends StandardMagicPanel {

	private static final String VIEW_PAYMENT_ACTION_NAME = "VIEW_PAYMENT_ACTION_NAME";
	
	@Autowired private PaymentService paymentService;
	@Autowired private SearchPaymentsDialog searchPaymentsDialog;
	
	private JTable table;
	private PaymentsTableModel tableModel = new PaymentsTableModel();
	
	public void updateDisplay() {
		List<Payment> payments = paymentService.getAllNewPayments();
		tableModel.setPayments(payments);
		if (!payments.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
		searchPaymentsDialog.updateDisplay();
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
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(new JScrollPane(table), c);
	}

	@Override
	protected void registerKeyBindings() {
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), VIEW_PAYMENT_ACTION_NAME);
		table.getActionMap().put(VIEW_PAYMENT_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectPayment();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectPayment();
			}
			
		});
		
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}

	protected void selectPayment() {
		Payment payment = tableModel.getPayment(table.getSelectedRow());
		getMagicFrame().switchToPaymentPanel(payment);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToSalesPaymentsMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton addButton = new MagicToolBarButton("plus", "New");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewPaymentPanel();
			}
		});
		toolBar.add(addButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchPayments();
			}
		});
		
		toolBar.add(searchButton);
	}

	private void searchPayments() {
		searchPaymentsDialog.setVisible(true);
		
		PaymentSearchCriteria criteria = searchPaymentsDialog.getSearchCriteria();
		if (criteria != null) {
			List<Payment> payments = paymentService.searchPayments(criteria);
			tableModel.setPayments(payments);
			if (!payments.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}

	private void switchToNewPaymentPanel() {
		getMagicFrame().switchToPaymentPanel(new Payment());
	}
	
}
