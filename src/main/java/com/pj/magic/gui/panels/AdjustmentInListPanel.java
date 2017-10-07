package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchAdjustmentInsDialog;
import com.pj.magic.gui.tables.AdjustmentInsTable;
import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.search.AdjustmentInSearchCriteria;
import com.pj.magic.service.AdjustmentInService;

@Component
public class AdjustmentInListPanel extends StandardMagicPanel {
	
	@Autowired private AdjustmentInsTable table;
	@Autowired private AdjustmentInService adjustmentInService;
	@Autowired private SearchAdjustmentInsDialog searchAdjustmentInsDialog;
	
	@Override
	public void initializeComponents() {
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay() {
		table.setAdjustmentIns(adjustmentInService.getAllNonPostedAdjustmentIns());
		searchAdjustmentInsDialog.updateDisplay();
	}

	public void displayAdjustmentInDetails(AdjustmentIn adjustmentIn) {
		getMagicFrame().switchToAdjustmentInPanel(adjustmentIn);
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
		
		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, c);
	}
	
	@Override
	protected void registerKeyBindings() {
	}
	
	protected void switchToNewAdjustmentInPanel() {
		getMagicFrame().switchToAdjustmentInPanel(new AdjustmentIn());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToStockMovementMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton addButton = new MagicToolBarButton("plus", "New");
		addButton.addActionListener(e -> switchToNewAdjustmentInPanel());
		toolBar.add(addButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(e -> searchAdjustmentIns());
		toolBar.add(searchButton);
	}

	private void searchAdjustmentIns() {
		searchAdjustmentInsDialog.setVisible(true);
		
		AdjustmentInSearchCriteria criteria = searchAdjustmentInsDialog.getSearchCriteria();
		if (criteria != null) {
			List<AdjustmentIn> adjustmentIns = adjustmentInService.search(criteria);
			table.setAdjustmentIns(adjustmentIns);
			if (!adjustmentIns.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}

}
