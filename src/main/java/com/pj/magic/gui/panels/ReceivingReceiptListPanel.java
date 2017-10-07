package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchReceivingReceiptsDialog;
import com.pj.magic.gui.tables.ReceivingReceiptsTable;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.search.ReceivingReceiptSearchCriteria;
import com.pj.magic.service.ReceivingReceiptService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class ReceivingReceiptListPanel extends StandardMagicPanel {
	
	@Autowired private ReceivingReceiptsTable table;
	@Autowired private ReceivingReceiptService receivingReceiptService;
	@Autowired private SearchReceivingReceiptsDialog searchReceivingReceiptsDialog;
	
	private JLabel totalItemsLabel;
	private JLabel totalAmountLabel;
	
	@Override
	public void initializeComponents() {
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay() {
		List<ReceivingReceipt> receivingReceipts = receivingReceiptService.getNewReceivingReceipts();
		updateFields(receivingReceipts);
		searchReceivingReceiptsDialog.updateDisplay();
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
		c.insets.top = 5;
		c.insets.bottom = 5;
		
		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
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
	
	protected void switchToNewReceivingReceiptPanel() {
		getMagicFrame().switchToReceivingReceiptPanel(new ReceivingReceipt());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPurchasesMenuPanel();
	}
	
	public void displayReceivingReceiptDetails(ReceivingReceipt receivingReceipt) {
		getMagicFrame().switchToReceivingReceiptPanel(receivingReceipt);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchReceivingReceipts();
			}
		});
		
		toolBar.add(searchButton);
	}

	private void searchReceivingReceipts() {
		searchReceivingReceiptsDialog.setVisible(true);
		
		ReceivingReceiptSearchCriteria criteria = searchReceivingReceiptsDialog.getSearchCriteria();
		if (criteria != null) {
			List<ReceivingReceipt> receivingReceipts = receivingReceiptService.search(criteria);
			updateFields(receivingReceipts);
			if (!receivingReceipts.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}

	private void updateFields(List<ReceivingReceipt> receivingReceipts) {
		table.setReceivingReceipts(receivingReceipts);
		totalItemsLabel.setText(String.valueOf(receivingReceipts.size()));
		totalAmountLabel.setText(FormatterUtil.formatAmount(getTotalAmount(receivingReceipts)));
	}

	private static BigDecimal getTotalAmount(List<ReceivingReceipt> receivingReceipts) {
		BigDecimal total = Constants.ZERO;
		for (ReceivingReceipt receivingReceipt : receivingReceipts) {
			total = total.add(receivingReceipt.getTotalNetAmountWithVat());
		}
		return total;
	}

	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsLabel = ComponentUtil.createRightLabel(100);
		panel.add(totalItemsLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(Box.createHorizontalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountLabel = ComponentUtil.createRightLabel(100);
		panel.add(totalAmountLabel, c);
		
		return panel;
	}
	
}