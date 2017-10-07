package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
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
import com.pj.magic.gui.dialog.SearchBadStockReturnsDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.search.BadStockReturnSearchCriteria;
import com.pj.magic.service.BadStockReturnService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class BadStockReturnListPanel extends StandardMagicPanel {
	
	private static final int BAD_STOCK_RETURN_NUMBER_COLUMN_INDEX = 0;
	private static final int CUSTOMER_COLUMN_INDEX = 1;
	private static final int TOTAL_AMOUNT_COLUMN_INDEX = 2;
	private static final int STATUS_COLUMN_INDEX = 3;
	private static final int PAID_DATE_COLUMN_INDEX = 4;
	
	@Autowired private BadStockReturnService badStockReturnService;
	@Autowired private SearchBadStockReturnsDialog searchBadStockReturnsDialog;
	
	private MagicListTable table;
	private BadStockReturnsTableModel tableModel;
	
	private BadStockReturnSearchCriteria searchCriteria;
    private int selectedRow;
    private Rectangle visibleRect;
	
    public BadStockReturnListPanel() {
        setTitle("Bad Stock Return List");
    }
    
	@Override
	public void initializeComponents() {
		initializeTable();
		
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void initializeTable() {
		tableModel = new BadStockReturnsTableModel();
		table = new MagicListTable(tableModel);
	}

	public void updateDisplay() {
	    List<BadStockReturn> badStockReturns = badStockReturnService.getUnpaidBadStockReturns();
		tableModel.setBadStockReturns(badStockReturns);
		if (!badStockReturns.isEmpty()) {
            table.changeSelection(0, 0, false, false);
		}
		searchBadStockReturnsDialog.updateDisplay();
		searchCriteria = null;
        selectedRow = 0;
        visibleRect = null;
	}

	public void displayBadStockReturnDetails(BadStockReturn badStockReturn) {
		getMagicFrame().switchToBadStockReturnPanel(badStockReturn);
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
				selectBadStockReturn();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectBadStockReturn();
			}
		});
		
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}
	
	private void selectBadStockReturn() {
        selectedRow = table.getSelectedRow();
        visibleRect = table.getVisibleRect();
        
		displayBadStockReturnDetails(getCurrentlySelectedBadStockReturn());
	}

	public BadStockReturn getCurrentlySelectedBadStockReturn() {
		return tableModel.getBadStockReturn(table.getSelectedRow());
	}
	
	protected void switchToNewBadStockReturnPanel() {
	    searchCriteria = null;
	    selectedRow = 0;
	    visibleRect = null;
		getMagicFrame().switchToBadStockReturnPanel(new BadStockReturn());
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
				switchToNewBadStockReturnPanel();
			}
		});
		toolBar.add(addButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchBadStockReturns();
			}
		});
		
		toolBar.add(searchButton);
	}

	private void searchBadStockReturns() {
		searchBadStockReturnsDialog.setVisible(true);
		
		BadStockReturnSearchCriteria criteria = searchBadStockReturnsDialog.getSearchCriteria();
		if (criteria != null) {
			searchCriteria = criteria;
            selectedRow = 0;
            visibleRect = null;
			List<BadStockReturn> badStockReturns = badStockReturnService.search(criteria);
			tableModel.setBadStockReturns(badStockReturns);
			if (!badStockReturns.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}

	@Override
	public void updateDisplayOnBack() {
	    List<BadStockReturn> badStockReturns = null;
	    
		if (searchCriteria != null) {
			badStockReturns = badStockReturnService.search(searchCriteria);
		} else {
	        badStockReturns = badStockReturnService.getUnpaidBadStockReturns();
	        searchBadStockReturnsDialog.updateDisplay();
		}
		
        tableModel.setBadStockReturns(badStockReturns);
        if (badStockReturns.size() - 1 < selectedRow) {
            selectedRow = badStockReturns.size() - 1;
        }
        table.changeSelection(selectedRow, 0, false, false);
        if (visibleRect != null) {
            table.scrollRectToVisible(visibleRect);
        }
	}
	
	private class BadStockReturnsTableModel extends AbstractTableModel {

		private final String[] columnNames = 
			{"Bad Stock Return No.", "Customer", "Total Amount", "Status", "Paid Date"};
		
		private List<BadStockReturn> badStockReturns = new ArrayList<>();
		
		public void setBadStockReturns(List<BadStockReturn> badStockReturns) {
			this.badStockReturns = badStockReturns;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return badStockReturns.size();
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
			BadStockReturn badStockReturn = badStockReturns.get(rowIndex);
			switch (columnIndex) {
			case BAD_STOCK_RETURN_NUMBER_COLUMN_INDEX:
				return badStockReturn.getBadStockReturnNumber();
			case CUSTOMER_COLUMN_INDEX:
				return badStockReturn.getCustomer().getName();
			case TOTAL_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(badStockReturn.getTotalAmount());
			case STATUS_COLUMN_INDEX:
				return badStockReturn.getStatus();
			case PAID_DATE_COLUMN_INDEX:
				return badStockReturn.isPaid() ? FormatterUtil.formatDateTime(badStockReturn.getPaidDate()) : null;
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
		public BadStockReturn getBadStockReturn(int index) {
			return badStockReturns.get(index);
		}
		
	}
	
}