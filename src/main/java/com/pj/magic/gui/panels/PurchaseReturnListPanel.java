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
import com.pj.magic.gui.dialog.SearchPurchaseReturnsDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.PurchaseReturn;
import com.pj.magic.model.search.PurchaseReturnSearchCriteria;
import com.pj.magic.service.PurchaseReturnService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class PurchaseReturnListPanel extends StandardMagicPanel {

	private static final int PURCHASE_RETURN_COLUMN_INDEX = 0;
	private static final int RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX = 1;
	private static final int SUPPLIER_COLUMN_INDEX = 2;
	private static final int TOTAL_AMOUNT_COLUMN_INDEX = 3;
	private static final int STATUS_COLUMN_INDEX = 4;
	private static final int POST_DATE_COLUMN_INDEX = 5;
	
	@Autowired private PurchaseReturnService purchaseReturnService;
	@Autowired private SearchPurchaseReturnsDialog searchPurchaseReturnsDialog;
	
	private MagicListTable table;
	private PurchaseReturnsTableModel tableModel;
	
	public void updateDisplay() {
		List<PurchaseReturn> purchaseReturns = purchaseReturnService.getUnpaidPurchaseReturns();
		tableModel.setPurchaseReturns(purchaseReturns);
		if (!purchaseReturns.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
		searchPurchaseReturnsDialog.updateDisplay();
	}

	@Override
	protected void initializeComponents() {
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void initializeTable() {
		tableModel = new PurchaseReturnsTableModel();
		table = new MagicListTable(tableModel);
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
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectPurchaseReturn();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectPurchaseReturn();
			}
			
		});
		
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}

	protected void selectPurchaseReturn() {
		PurchaseReturn purchaseReturn = tableModel.getPurchaseReturn(table.getSelectedRow());
		getMagicFrame().switchToPurchaseReturnPanel(purchaseReturn);
	}

	private void switchToNewPurchaseReturnPanel() {
		getMagicFrame().switchToPurchaseReturnPanel(new PurchaseReturn());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPurchasesMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton postButton = new MagicToolBarButton("plus", "New");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewPurchaseReturnPanel();
			}
		});
		toolBar.add(postButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchPurchaseReturns();
			}
		});
		
		toolBar.add(searchButton);
	}
	
	private void searchPurchaseReturns() {
		searchPurchaseReturnsDialog.setVisible(true);
		
		PurchaseReturnSearchCriteria criteria = searchPurchaseReturnsDialog.getSearchCriteria();
		if (criteria != null) {
			List<PurchaseReturn> purchaseReturns = purchaseReturnService.search(criteria);
			tableModel.setPurchaseReturns(purchaseReturns);
			if (!purchaseReturns.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}
	
	private class PurchaseReturnsTableModel extends AbstractTableModel {

		private final String[] columnNames = 
			{"Purchase Return No.", "RR No.", "Supplier", "Total Amount", "Status", "Post Date"};
		
		private List<PurchaseReturn> purchaseReturns = new ArrayList<>();
		
		public void setPurchaseReturns(List<PurchaseReturn> purchaseReturns) {
			this.purchaseReturns = purchaseReturns;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return purchaseReturns.size();
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
			PurchaseReturn purchaseReturn = getPurchaseReturn(rowIndex);
			switch (columnIndex) {
			case PURCHASE_RETURN_COLUMN_INDEX:
				return purchaseReturn.getPurchaseReturnNumber();
			case RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX:
				return purchaseReturn.getReceivingReceipt().getReceivingReceiptNumber();
			case SUPPLIER_COLUMN_INDEX:
				return purchaseReturn.getReceivingReceipt().getSupplier().getName();
			case TOTAL_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(purchaseReturn.getTotalAmount());
			case STATUS_COLUMN_INDEX:
				return purchaseReturn.getStatus();
			case POST_DATE_COLUMN_INDEX:
				return (purchaseReturn.isPosted()) ? FormatterUtil.formatDate(purchaseReturn.getPostDate()) : null;
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
		public PurchaseReturn getPurchaseReturn(int rowIndex) {
			return purchaseReturns.get(rowIndex);
		}
		
	}

}