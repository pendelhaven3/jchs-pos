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
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchAdjustmentOutsDialog;
import com.pj.magic.gui.tables.AdjustmentOutsTable;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.search.AdjustmentOutSearchCriteria;
import com.pj.magic.service.AdjustmentOutService;
import com.pj.magic.util.ComponentUtil;

@Component
public class AdjustmentOutListPanel extends StandardMagicPanel {
	
	private static final String NEW_ADJUSTMENT_OUT_ACTION_NAME = "newAdjustmentOut";
	
	@Autowired private AdjustmentOutsTable table;
	@Autowired private AdjustmentOutService adjustmentOutService;
	@Autowired private SearchAdjustmentOutsDialog searchAdjustmentOutsDialog;
	
	@Override
	public void initializeComponents() {
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay() {
		table.setAdjustmentOuts(adjustmentOutService.getAllNonPostedAdjustmentOuts());
		searchAdjustmentOutsDialog.updateDisplay();
	}

	public void displayAdjustmentOutDetails(AdjustmentOut AdjustmentOut) {
		getMagicFrame().switchToAdjustmentOutPanel(AdjustmentOut);
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
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), NEW_ADJUSTMENT_OUT_ACTION_NAME);
		getActionMap().put(NEW_ADJUSTMENT_OUT_ACTION_NAME, new AbstractAction() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewAdjustmentOutPanel();
			}
		});		
	}
	
	protected void switchToNewAdjustmentOutPanel() {
		getMagicFrame().switchToAdjustmentOutPanel(new AdjustmentOut());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToStockMovementMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton addButton = new MagicToolBarButton("plus", "New");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewAdjustmentOutPanel();
			}
		});
		toolBar.add(addButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchAdjustmentOuts();
			}
		});
		
		toolBar.add(searchButton);
	}

	private void searchAdjustmentOuts() {
		searchAdjustmentOutsDialog.setVisible(true);
		
		AdjustmentOutSearchCriteria criteria = searchAdjustmentOutsDialog.getSearchCriteria();
		if (criteria != null) {
			List<AdjustmentOut> adjustmentIns = adjustmentOutService.search(criteria);
			table.setAdjustmentOuts(adjustmentIns);
			if (!adjustmentIns.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}

}
