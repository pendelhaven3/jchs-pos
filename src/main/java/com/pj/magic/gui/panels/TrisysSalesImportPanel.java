package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.MagicFrame;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.TrisysSales;
import com.pj.magic.model.TrisysSalesImport;
import com.pj.magic.service.TrisysSalesService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class TrisysSalesImportPanel extends StandardMagicPanel {

	private static final int SALE_NUMBER_COLUMN_INDEX = 0;
	private static final int TERMINAL_COLUMN_INDEX = 1;
	
	@Autowired
	private TrisysSalesService trisysSalesService;
	
	private MagicListTable table;
	private TrisysSalesTableModel tableModel = new TrisysSalesTableModel();
	
	private JLabel fileLabel;
	private JLabel importDateLabel;
	private JLabel importByLabel;
	private JLabel totalItemsLabel = new JLabel();
	
	@Override
	public String getTitle() {
		return "Trisys Sales Import";
	}
	
	@Override
	protected void initializeComponents() {
		table = new MagicListTable(tableModel);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay(TrisysSalesImport salesImport) {
		salesImport = trisysSalesService.getTrisysSalesImport(salesImport.getId());
		tableModel.setItems(salesImport.getSales());
		if (!salesImport.getSales().isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
		
		fileLabel.setText(salesImport.getFile());
		importDateLabel.setText(FormatterUtil.formatDateTime(salesImport.getImportDate()));
		importByLabel.setText(salesImport.getImportBy().getUsername());
		totalItemsLabel.setText(String.valueOf(salesImport.getSales().size()));
	}

	@Override
	protected void registerKeyBindings() {
	    table.setKeyEvent(KeyEvent.VK_ENTER, new AbstractAction() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                selectSales();
            }
        });
	    
	    table.addDoubleClickMouseListener(() -> selectSales());
	}
	
	protected void selectSales() {
		if (table.getSelectedRow() != -1) {
			TrisysSales sales = tableModel.getItem(table.getSelectedRow());
			getMagicFrame().switchToTrisysSalesPanel(sales);
		}
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().back(MagicFrame.TRISYS_SALES_IMPORT_LIST_PANEL);
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets.left = 50;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createLabel(100, "File:"), c);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		c.gridy = currentRow;
		fileLabel = ComponentUtil.createLabel(200);
		mainPanel.add(fileLabel, c);

		c = new GridBagConstraints();
		c.insets.left = 50;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 2;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createLabel(100, "Import Date:"), c);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 3;
		c.gridy = currentRow;
		importDateLabel = ComponentUtil.createLabel(200);
		mainPanel.add(importDateLabel, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.left = 50;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 2;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createLabel(100, "Import By:"), c);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 3;
		c.gridy = currentRow;
		importByLabel = ComponentUtil.createLabel(200);
		mainPanel.add(importByLabel, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 5;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
		mainPanel.add(new JScrollPane(table), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.CENTER;
		mainPanel.add(createTotalsPanel(), c);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsLabel = ComponentUtil.createLabel(60, "");
		panel.add(totalItemsLabel, c);
		
		return panel;
	}
	
    private class TrisysSalesTableModel extends ListBackedTableModel<TrisysSales> {

        private final String[] columnNames = {"Sale No.", "Terminal"};
	    
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
        	TrisysSales sales = getItem(rowIndex);
            switch (columnIndex) {
            case SALE_NUMBER_COLUMN_INDEX:
                return sales.getSaleNumber();
            case TERMINAL_COLUMN_INDEX:
                return sales.getTerminal();
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