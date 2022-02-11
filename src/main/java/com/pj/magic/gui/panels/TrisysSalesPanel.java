package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

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
import com.pj.magic.model.TrisysSalesItem;
import com.pj.magic.service.TrisysSalesService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class TrisysSalesPanel extends StandardMagicPanel {

	private static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	private static final int UNIT_COLUMN_INDEX = 2;
	private static final int QUANTITY_COLUMN_INDEX = 3;
	private static final int UNIT_COST_COLUMN_INDEX = 4;
	private static final int SELL_PRICE_COLUMN_INDEX = 5;
	private static final int TOTAL_COLUMN_INDEX = 6;
	
	@Autowired
	private TrisysSalesService trisysSalesService;
	
	private MagicListTable table;
	private TrisysSalesItemsTableModel tableModel = new TrisysSalesItemsTableModel();
	
	private JLabel saleNumberLabel;
	private JLabel terminalLabel;
	private JLabel saleDateLabel;
	private JLabel totalItemsLabel = new JLabel();
	private JLabel totalAmountLabel = new JLabel();
	
	@Override
	public String getTitle() {
		return "Trisys Sales";
	}
	
	@Override
	protected void initializeComponents() {
		table = new MagicListTable(tableModel);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay(TrisysSales sales) {
		sales = trisysSalesService.getTrisysSales(sales.getId());
		tableModel.setItems(sales.getItems());
		
		saleNumberLabel.setText(sales.getSaleNumber());
		terminalLabel.setText(sales.getTerminal());
		saleDateLabel.setText(FormatterUtil.formatDate(sales.getSalesDate()));
		totalItemsLabel.setText(String.valueOf(sales.getItems().size()));
		totalAmountLabel.setText(FormatterUtil.formatAmount(sales.getTotalAmount()));
	}

	@Override
	protected void registerKeyBindings() {
	}
	
	@Override
	protected void doOnBack() {
		getMagicFrame().back(MagicFrame.TRISYS_SALES_IMPORT_PANEL);
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
		mainPanel.add(ComponentUtil.createLabel(150, "Sale Number:"), c);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		c.gridy = currentRow;
		saleNumberLabel = ComponentUtil.createLabel(180);
		mainPanel.add(saleNumberLabel, c);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 2;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createLabel(100, "Terminal:"), c);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 3;
		c.gridy = currentRow;
		terminalLabel = ComponentUtil.createLabel(200);
		mainPanel.add(terminalLabel, c);
		
		currentRow++;

		c = new GridBagConstraints();
		c.insets.left = 50;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createLabel(150, "Sale Date:"), c);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		c.gridy = currentRow;
		saleDateLabel = ComponentUtil.createLabel(180);
		mainPanel.add(saleDateLabel, c);

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
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountLabel = ComponentUtil.createLabel(60, "");
		panel.add(totalAmountLabel, c);
		
		return panel;
	}
	
    private class TrisysSalesItemsTableModel extends ListBackedTableModel<TrisysSalesItem> {

        private final String[] columnNames = {"Product Code", "Description", "Unit", "Quantity", "Unit Cost", "Sell Price", "Total"};
	    
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
        	TrisysSalesItem item = getItem(rowIndex);
            switch (columnIndex) {
            case PRODUCT_CODE_COLUMN_INDEX:
                return item.getProductCode();
            case PRODUCT_DESCRIPTION_COLUMN_INDEX:
                return item.getProductDescription();
            case UNIT_COLUMN_INDEX:
                return item.getUnit();
            case QUANTITY_COLUMN_INDEX:
                return item.getQuantity();
            case UNIT_COST_COLUMN_INDEX:
                return FormatterUtil.formatAmount(item.getUnitCost());
            case SELL_PRICE_COLUMN_INDEX:
                return FormatterUtil.formatAmount(item.getSellPrice());
            case TOTAL_COLUMN_INDEX:
                return FormatterUtil.formatAmount(item.getTotal());
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