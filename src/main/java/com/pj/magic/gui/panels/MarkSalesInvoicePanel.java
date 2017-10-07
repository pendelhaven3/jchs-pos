package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MarkSalesInvoicesTable;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.util.ComponentUtil;

@Component
public class MarkSalesInvoicePanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(MarkSalesInvoicePanel.class);
	
	@Autowired private MarkSalesInvoicesTable table;
	@Autowired private SalesInvoiceService salesInvoiceService;
	
	private JButton markButton;
	private JButton markToolBarButton;
	
	@Override
	protected void initializeComponents() {
		markButton = new JButton("Mark Sales Invoices");
		markButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				markSalesInvoices();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToSalesMenuPanel();
	}
	
	public void updateDisplay() {
		table.update();
	}

	@Override
	protected void registerKeyBindings() {
		// none
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(new JScrollPane(table), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		markButton.setPreferredSize(new Dimension(230, 30));
		mainPanel.add(markButton, c);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		markToolBarButton = new MagicToolBarButton("post", "Mark Sales Invoices");
		markToolBarButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				markSalesInvoices();
			}
		});
		
		toolBar.add(markToolBarButton);
	}

	private void markSalesInvoices() {
		if (confirm("Mark Sales Invoices?")) {
			try {
				salesInvoiceService.markSalesInvoices(table.getSalesInvoices());
				showMessage("Sales Invoices Marked");
				updateDisplay();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessage("Error on updating records");
			}
		}
	}

}