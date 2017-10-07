package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchStockQuantityConversionsDialog;
import com.pj.magic.gui.tables.StockQuantityConversionsTable;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.search.StockQuantityConversionSearchCriteria;
import com.pj.magic.service.StockQuantityConversionService;

@Component
public class StockQuantityConversionListPanel extends StandardMagicPanel {
	
	@Autowired private StockQuantityConversionsTable table;
	@Autowired private StockQuantityConversionService stockQuantityConversionService;
	@Autowired private SearchStockQuantityConversionsDialog searchStockQuantityConversionsDialog;
	
	@Override
	public void initializeComponents() {
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay() {
		table.setStockQuantityConversions(
				stockQuantityConversionService.getAllPendingStockQuantityConversions());
		searchStockQuantityConversionsDialog.updateDisplay();
	}

	public void displayStockQuantityConversionDetails(StockQuantityConversion stockQuantityConversion) {
		getMagicFrame().switchToStockQuantityConversionPanel(stockQuantityConversion);
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
		mainPanel.add(Box.createVerticalStrut(5), c);
		
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
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}
	
	protected void switchToNewStockQuantityConversionPanel() {
		StockQuantityConversion stockQuantityConversion = new StockQuantityConversion();
		stockQuantityConversionService.save(stockQuantityConversion);
		
		getMagicFrame().switchToStockQuantityConversionPanel(stockQuantityConversion);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToSalesMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton addButton = new MagicToolBarButton("plus", "New");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewStockQuantityConversionPanel();
			}
		});
		toolBar.add(addButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchStockQuantityConversions();
			}
		});
		
		toolBar.add(searchButton);
	}

	private void searchStockQuantityConversions() {
		searchStockQuantityConversionsDialog.setVisible(true);
		
		StockQuantityConversionSearchCriteria criteria = 
				searchStockQuantityConversionsDialog.getSearchCriteria();
		if (criteria != null) {
			List<StockQuantityConversion> stockQuantityConversions = 
					stockQuantityConversionService.search(criteria);
			table.setStockQuantityConversions(stockQuantityConversions);
			if (!stockQuantityConversions.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}

}
