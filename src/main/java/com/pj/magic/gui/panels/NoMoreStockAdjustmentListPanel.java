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
import com.pj.magic.gui.dialog.SearchNoMoreStockAdjustmentsDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.search.NoMoreStockAdjustmentSearchCriteria;
import com.pj.magic.service.NoMoreStockAdjustmentService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class NoMoreStockAdjustmentListPanel extends StandardMagicPanel {

	@Autowired private NoMoreStockAdjustmentService noMoreStockAdjustmentService;
	@Autowired private SearchNoMoreStockAdjustmentsDialog searchNoMoreStockAdjustmentsDialog;
	
	private MagicListTable table;
	private NoMoreStockAdjustmentsTableModel tableModel = new NoMoreStockAdjustmentsTableModel();
	
	public void updateDisplay() {
		List<NoMoreStockAdjustment> noMoreStockAdjustments = noMoreStockAdjustmentService.getUnpaidNoMoreStockAdjustments();
		tableModel.setNoMoreStockAdjustments(noMoreStockAdjustments);
		if (!noMoreStockAdjustments.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
		searchNoMoreStockAdjustmentsDialog.updateDisplay();
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
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectNoMoreStockAdjustment();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectNoMoreStockAdjustment();
			}
			
		});
		
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}

	protected void selectNoMoreStockAdjustment() {
		NoMoreStockAdjustment noMoreStockAdjustment = tableModel.getNoMoreStockAdjustment(table.getSelectedRow());
		getMagicFrame().switchToNoMoreStockAdjustmentPanel(noMoreStockAdjustment);
	}

	private void switchToNewNoMoreStockAdjustmentPanel() {
		getMagicFrame().switchToNoMoreStockAdjustmentPanel(new NoMoreStockAdjustment());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToSalesMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton postButton = new MagicToolBarButton("plus", "New");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewNoMoreStockAdjustmentPanel();
			}
		});
		toolBar.add(postButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchNoMoreStockAdjustments();
			}
		});
		
		toolBar.add(searchButton);
	}
	
	private void searchNoMoreStockAdjustments() {
		searchNoMoreStockAdjustmentsDialog.setVisible(true);
		
		NoMoreStockAdjustmentSearchCriteria criteria = searchNoMoreStockAdjustmentsDialog.getSearchCriteria();
		if (criteria != null) {
			List<NoMoreStockAdjustment> noMoreStockAdjustments = noMoreStockAdjustmentService.search(criteria);
			tableModel.setNoMoreStockAdjustments(noMoreStockAdjustments);
			if (!noMoreStockAdjustments.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}
	
	private class NoMoreStockAdjustmentsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"NMS No.", "SI No.", "Customer", "Total Amount",
			"Status", "Paid Date"};
		
		private static final int NO_MORE_STOCK_ADJUSTMENT_NUMBER_COLUMN_INDEX = 0;
		private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 1;
		private static final int CUSTOMER_COLUMN_INDEX = 2;
		private static final int TOTAL_AMOUNT_COLUMN_INDEX = 3;
		private static final int STATUS_COLUMN_INDEX = 4;
		private static final int PAID_DATE_COLUMN_INDEX = 5;
		
		private List<NoMoreStockAdjustment> noMoreStockAdjustments = new ArrayList<>();
		
		@Override
		public int getRowCount() {
			return noMoreStockAdjustments.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			NoMoreStockAdjustment noMoreStockAdjustment = noMoreStockAdjustments.get(rowIndex);
			switch (columnIndex) {
			case NO_MORE_STOCK_ADJUSTMENT_NUMBER_COLUMN_INDEX:
				return noMoreStockAdjustment.getNoMoreStockAdjustmentNumber();
			case SALES_INVOICE_NUMBER_COLUMN_INDEX:
				return noMoreStockAdjustment.getSalesInvoice().getSalesInvoiceNumber();
			case CUSTOMER_COLUMN_INDEX:
				return noMoreStockAdjustment.getSalesInvoice().getCustomer().getName();
			case TOTAL_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(noMoreStockAdjustment.getTotalAmount());
			case STATUS_COLUMN_INDEX:
				return noMoreStockAdjustment.getStatus();
			case PAID_DATE_COLUMN_INDEX:
				return noMoreStockAdjustment.isPaid() ? FormatterUtil.formatDateTime(noMoreStockAdjustment.getPaidDate()) : null;
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == TOTAL_AMOUNT_COLUMN_INDEX) {
				return Number.class;
			} else {
				return Object.class;
			}
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		public void setNoMoreStockAdjustments(List<NoMoreStockAdjustment> noMoreStockAdjustments) {
			this.noMoreStockAdjustments = noMoreStockAdjustments;
			fireTableDataChanged();
		}
		
		public NoMoreStockAdjustment getNoMoreStockAdjustment(int rowIndex) {
			return noMoreStockAdjustments.get(rowIndex);
		}
	
	}
	
}