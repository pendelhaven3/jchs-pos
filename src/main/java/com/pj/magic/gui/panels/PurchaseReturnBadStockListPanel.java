package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchPurchaseReturnBadStocksDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.PurchaseReturnBadStock;
import com.pj.magic.model.search.PurchaseReturnBadStockSearchCriteria;
import com.pj.magic.service.PurchaseReturnBadStockService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class PurchaseReturnBadStockListPanel extends StandardMagicPanel {
	
	private static final int PURCHASE_RETURN_BAD_STOCK_NUMBER_COLUMN_INDEX = 0;
	private static final int SUPPLIER_COLUMN_INDEX = 1;
	private static final int TOTAL_AMOUNT_COLUMN_INDEX = 2;
	private static final int STATUS_COLUMN_INDEX = 3;
	private static final int POST_DATE_COLUMN_INDEX = 4;
	
	@Autowired private PurchaseReturnBadStockService purchaseReturnBadStockService;
	@Autowired private SearchPurchaseReturnBadStocksDialog searchPurchaseReturnBadStocksDialog;
	
	private MagicListTable table;
	private PurchaseReturnBadStocksTableModel tableModel;
	
	@Override
	public void initializeComponents() {
		initializeTable();
		
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void initializeTable() {
		tableModel = new PurchaseReturnBadStocksTableModel();
		table = new MagicListTable(tableModel);
	}

	public void updateDisplay() {
		List<PurchaseReturnBadStock> purchaseReturnBadStocks = 
				purchaseReturnBadStockService.getAllNewPurchaseReturnBadStocks();
		tableModel.setPurchaseReturnBadStocks(purchaseReturnBadStocks);
		if (!purchaseReturnBadStocks.isEmpty()) {
			table.changeSelection(0, 0);
		}
		
		searchPurchaseReturnBadStocksDialog.updateDisplay();
	}

	public void displayPurchaseReturnBadStockDetails(PurchaseReturnBadStock purchaseReturnBadStock) {
		getMagicFrame().switchToPurchaseReturnBadStockPanel(purchaseReturnBadStock);
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
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectPurchaseReturnBadStock();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectPurchaseReturnBadStock();
			}
		});
		
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}
	
	private void selectPurchaseReturnBadStock() {
		displayPurchaseReturnBadStockDetails(getCurrentlySelectedPurchaseReturnBadStock());
	}

	public PurchaseReturnBadStock getCurrentlySelectedPurchaseReturnBadStock() {
		return tableModel.getPurchaseReturnBadStock(table.getSelectedRow());
	}
	
	protected void switchToNewPurchaseReturnBadStockPanel() {
		getMagicFrame().switchToPurchaseReturnBadStockPanel(new PurchaseReturnBadStock());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPurchasesMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton addButton = new MagicToolBarButton("plus", "New");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewPurchaseReturnBadStockPanel();
			}
		});
		toolBar.add(addButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchPurchaseReturnBadStocks();
			}
		});
		
		toolBar.add(searchButton);
	}

	private void searchPurchaseReturnBadStocks() {
		searchPurchaseReturnBadStocksDialog.setVisible(true);
		
		PurchaseReturnBadStockSearchCriteria criteria = searchPurchaseReturnBadStocksDialog.getSearchCriteria();
		if (criteria != null) {
			List<PurchaseReturnBadStock> purchaseReturnBadStocks = purchaseReturnBadStockService.search(criteria);
			tableModel.setPurchaseReturnBadStocks(purchaseReturnBadStocks);
			if (!purchaseReturnBadStocks.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}

	private class PurchaseReturnBadStocksTableModel extends AbstractTableModel {

		private final String[] columnNames = 
			{"PRBS No.", "Supplier", "Total Amount", "Status", "Post Date"};
		
		private List<PurchaseReturnBadStock> purchaseReturnBadStocks = new ArrayList<>();
		
		public void setPurchaseReturnBadStocks(List<PurchaseReturnBadStock> purchaseReturnBadStocks) {
			this.purchaseReturnBadStocks = purchaseReturnBadStocks;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return purchaseReturnBadStocks.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PurchaseReturnBadStock purchaseReturnBadStock = purchaseReturnBadStocks.get(rowIndex);
			switch (columnIndex) {
			case PURCHASE_RETURN_BAD_STOCK_NUMBER_COLUMN_INDEX:
				return purchaseReturnBadStock.getPurchaseReturnBadStockNumber();
			case SUPPLIER_COLUMN_INDEX:
				return purchaseReturnBadStock.getSupplier().getName();
			case TOTAL_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(purchaseReturnBadStock.getTotalAmount());
			case STATUS_COLUMN_INDEX:
				return purchaseReturnBadStock.getStatus();
			case POST_DATE_COLUMN_INDEX:
				return purchaseReturnBadStock.isPosted() ? 
						FormatterUtil.formatDate(purchaseReturnBadStock.getPostDate()) : null;
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
		public PurchaseReturnBadStock getPurchaseReturnBadStock(int index) {
			return purchaseReturnBadStocks.get(index);
		}
		
	}
	
}