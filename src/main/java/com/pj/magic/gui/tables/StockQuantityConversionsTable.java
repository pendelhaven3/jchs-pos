package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.panels.StockQuantityConversionListPanel;
import com.pj.magic.gui.tables.models.StockQuantityConversionsTableModel;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.service.StockQuantityConversionService;

@Component
public class StockQuantityConversionsTable extends MagicListTable {

	private static final String GO_TO_STOCK_QUANTITY_CONVERSION_ACTION_NAME = "goToStockQuantityConversion";
	private static final String DELETE_STOCK_QUANTITY_CONVERSION_ACTION_NAME = "deleteStockQuantityConversion";

	@Autowired private StockQuantityConversionService stockQuantityConversionService;
	@Autowired private StockQuantityConversionsTableModel tableModel;
	
	@Autowired
	public StockQuantityConversionsTable(StockQuantityConversionsTableModel tableModel) {
		super(tableModel);
	}
	
	@PostConstruct
	public void initialize() {
		registerKeyBindings();
    }
	
	public StockQuantityConversion getCurrentlySelectedStockQuantityConversion() {
		return tableModel.getStockQuantityConversion(getSelectedRow());
	}
	
	public void displayStockQuantityConversionDetails(StockQuantityConversion stockQuantityConversion) {
		StockQuantityConversionListPanel panel = (StockQuantityConversionListPanel)
				SwingUtilities.getAncestorOfClass(StockQuantityConversionListPanel.class, this);
		panel.displayStockQuantityConversionDetails(stockQuantityConversion);
	}
	
	public void removeCurrentlySelectedRow() {
		int selectedRowIndex = getSelectedRow();
		StockQuantityConversion stockQuantityConversion = getCurrentlySelectedStockQuantityConversion();
		stockQuantityConversionService.delete(stockQuantityConversion);
		tableModel.remove(stockQuantityConversion);
		
		if (tableModel.getRowCount() > 0) {
			if (selectedRowIndex == tableModel.getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
		
		// TODO: update table as well if any new SQC has been created
	}
	
	public void registerKeyBindings() {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), GO_TO_STOCK_QUANTITY_CONVERSION_ACTION_NAME);
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), DELETE_STOCK_QUANTITY_CONVERSION_ACTION_NAME);
		
		getActionMap().put(GO_TO_STOCK_QUANTITY_CONVERSION_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (getSelectedRow() != -1) {
					selectStockQuantityConversion();
				}
			}
		});
		getActionMap().put(DELETE_STOCK_QUANTITY_CONVERSION_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getSelectedRow() != -1) {
					int confirm = JOptionPane.showConfirmDialog(getParent(), "Delete selected stock quantity conversion?");
					if (confirm == JOptionPane.YES_OPTION) {
						removeCurrentlySelectedRow();
					}
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					selectStockQuantityConversion();
				}
			}
		});
	}

	protected void selectStockQuantityConversion() {
		displayStockQuantityConversionDetails(getCurrentlySelectedStockQuantityConversion());
	}

	public void setStockQuantityConversions(List<StockQuantityConversion> stockQuantityConversions) {
		tableModel.setStockQuantityConversions(stockQuantityConversions);
		if (!stockQuantityConversions.isEmpty()) {
			changeSelection(0, 0, false, false);
		}
	}
	
}
