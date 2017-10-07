package com.pj.magic.gui.panels.menu;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.MagicSubmenuTable;

@Component
public class SalesMenuPanel extends MenuPanel {

	private static final String SALES_REQUISITION_SEPARATE_ITEMS_LIST = 
			"Sales Requisition Separate Items List";
	private static final String NO_MORE_STOCK_ADJUSTMENT = "No More Stock Adjustment";
	private static final String BAD_STOCK_RETURN = "Bad Stock Return";
	private static final String SALES_RETURN = "Sales Return";
	private static final String MARK_SALES_INVOICE = "Mark Sales Invoice";
	private static final String SALES_INVOICE = "Sales Invoice";
	private static final String STOCK_QUANTITY_CONVERSION = "Stock Quantity Conversion";
	private static final String SALES_REQUISITION = "Sales Requisition";
	
	private MagicListTable table;
	private MainMenuTableModel tableModel;
	
	@Override
	protected void initializeComponents() {
		tableModel = new MainMenuTableModel();
		table = new MagicSubmenuTable(tableModel);
		
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(Object.class, renderer);
		
		table.changeSelection(0, 0, false, false);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		
		table.setTableHeader(null);
		table.setShowGrid(false);
		
		mainPanel.add(new JScrollPane(table), c);
	}

	@Override
	protected void registerKeyBindings() {
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectMenuItem();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectMenuItem();
			}
		});
	}

	public void updateDisplay() {
		table.changeSelection(0, 0, false, false);
	}
	
	private void selectMenuItem() {
		switch ((String)table.getValueAt(table.getSelectedRow(), 0)) {
		case SALES_REQUISITION:
			getMagicFrame().switchToSalesRequisitionsListPanel();
			break;
		case STOCK_QUANTITY_CONVERSION:
			getMagicFrame().switchToStockQuantityConversionListPanel();
			break;
		case SALES_INVOICE:
			getMagicFrame().switchToSalesInvoicesListPanel();
			break;
		case MARK_SALES_INVOICE:
			getMagicFrame().switchToMarkSalesInvoicesPanel();
			break;
		case SALES_RETURN:
			getMagicFrame().switchToSalesReturnListPanel();
			break;
		case BAD_STOCK_RETURN:
			getMagicFrame().switchToBadStockReturnListPanel();
			break;
		case NO_MORE_STOCK_ADJUSTMENT:
			getMagicFrame().switchToNoMoreStockAdjustmentListPanel();
			break;
		case SALES_REQUISITION_SEPARATE_ITEMS_LIST:
			getMagicFrame().switchToSalesRequisitionSeparateItemsPanel();
			break;
		}
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	private class MainMenuTableModel extends AbstractTableModel {

		private final List<String> menuItems = Arrays.asList(
				SALES_REQUISITION,
				STOCK_QUANTITY_CONVERSION,
				SALES_INVOICE,
				MARK_SALES_INVOICE,
				SALES_RETURN,
				BAD_STOCK_RETURN,
				NO_MORE_STOCK_ADJUSTMENT,
				SALES_REQUISITION_SEPARATE_ITEMS_LIST
		);
		
		@Override
		public int getRowCount() {
			return menuItems.size();
		}

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return menuItems.get(rowIndex);
		}
		
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}