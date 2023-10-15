package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.ReceiveDelivery;
import com.pj.magic.service.ReceiveDeliveryService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class ReceiveDeliveryListPanel extends StandardMagicPanel {

    private static final int ID_COLUMN_INDEX = 0;
    private static final int SUPPLIER_COLUMN_INDEX = 1;
    private static final int RECEIVE_DATE_COLUMN_INDEX = 2;
    private static final int RECEIVED_BY_COLUMN_INDEX = 3;
    
	@Autowired private ReceiveDeliveryService receiveDeliveryService;
	
	private MagicListTable table;
	private ReceiveDeliveryTableModel tableModel = new ReceiveDeliveryTableModel();
	
	@Override
	public String getTitle() {
		return "Receive Delivery List";
	}
	
	public void updateDisplay() {
		List<ReceiveDelivery> salesImports = receiveDeliveryService.getAllUnpostedReceiveDeliveries();
		tableModel.setItems(salesImports);
		if (!salesImports.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
	}

	@Override
	protected void initializeComponents() {
		table = new MagicListTable(tableModel);
		initializeTable();
		
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void initializeTable() {
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(ID_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(SUPPLIER_COLUMN_INDEX).setPreferredWidth(200);
		columnModel.getColumn(RECEIVE_DATE_COLUMN_INDEX).setPreferredWidth(200);
		columnModel.getColumn(RECEIVED_BY_COLUMN_INDEX).setPreferredWidth(100);
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
		
		currentRow++; // first row
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(new JScrollPane(table), c);
	}

    @Override
	protected void registerKeyBindings() {
	    table.setKeyEvent(KeyEvent.VK_ENTER, new AbstractAction() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                selectReceiveDelivery();
            }
        });
	    
	    table.addDoubleClickMouseListener(() -> selectReceiveDelivery());
	    
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}

	protected void selectReceiveDelivery() {
		if (table.getSelectedRow() != -1) {
			ReceiveDelivery receiveDelivery = tableModel.getItem(table.getSelectedRow());
			getMagicFrame().switchToReceiveDeliveryPanel(receiveDelivery);
		}
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPurchasesMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		toolBar.add(new MagicToolBarButton("plus", "New", e -> getMagicFrame().switchToReceiveDeliveryPanel()));
	}

	private class ReceiveDeliveryTableModel extends ListBackedTableModel<ReceiveDelivery> {

        private final String[] columnNames = {"ID", "Supplier", "Receive Date", "Received By"};
	    
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
        	ReceiveDelivery salesImport = getItem(rowIndex);
            switch (columnIndex) {
            case ID_COLUMN_INDEX:
                return String.valueOf(salesImport.getId());
            case SUPPLIER_COLUMN_INDEX:
                return salesImport.getSupplier().getName();
            case RECEIVE_DATE_COLUMN_INDEX:
            	return FormatterUtil.formatDateTime(salesImport.getReceiveDate());
            case RECEIVED_BY_COLUMN_INDEX:
            	return salesImport.getReceivedBy().getUsername();
            default:
                throw new RuntimeException("Fetching invalid column index: " + columnIndex);
            }
        }

        @Override
        protected String[] getColumnNames() {
            return columnNames;
        }
	    
	}
	
}
