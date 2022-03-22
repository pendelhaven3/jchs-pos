package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.Product2;
import com.pj.magic.model.ProductCustomCode;
import com.pj.magic.service.Product2Service;
import com.pj.magic.util.ComponentUtil;

@Component
public class ProductCustomCodesListPanel extends StandardMagicPanel {

	private static final int CODE_COLUMN_INDEX = 0;
	private static final int REMARKS_COLUMN_INDEX = 1;
	
	@Autowired
	private Product2Service productService;
	
	private Product2 product;
	private List<ProductCustomCode> customCodes;
	
	private MagicListTable table;
	private ProductCustomCodesTableModel tableModel = new ProductCustomCodesTableModel();
	
	private JLabel idLabel;
	private JLabel descriptionLabel;
	
	@Override
	public String getTitle() {
		return "Product Custom Codes List";
	}
	
	@Override
	protected void initializeComponents() {
		table = new MagicListTable(tableModel);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay(Product2 product) {
		this.product = product = productService.getProduct(product.getId());
		customCodes = productService.getCustomCodes(product.getId());
		tableModel.setItems(customCodes);
		if (!customCodes.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
		
		idLabel.setText(String.valueOf(product.getId()));
		descriptionLabel.setText(product.getDescription());
	}

	@Override
	protected void registerKeyBindings() {
	    table.setKeyEvent(KeyEvent.VK_ENTER, new AbstractAction() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
            	selectCustomCode();
            }
        });
	    
	    table.addDoubleClickMouseListener(() -> selectCustomCode());
	}
	
	protected void selectCustomCode() {
		if (table.getSelectedRow() != -1) {
			ProductCustomCode customCode = tableModel.getItem(table.getSelectedRow());
			getMagicFrame().switchToEditCustomCodePanel(customCode);
		}
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToEditProductPanel(product);
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
		mainPanel.add(ComponentUtil.createLabel(100, "Product ID:"), c);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		c.gridy = currentRow;
		idLabel = ComponentUtil.createLabel(100);
		mainPanel.add(idLabel, c);

		c = new GridBagConstraints();
		c.insets.left = 50;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 2;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createLabel(100, "Description:"), c);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 3;
		c.gridy = currentRow;
		descriptionLabel = ComponentUtil.createLabel(300);
		mainPanel.add(descriptionLabel, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 5;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
		mainPanel.add(new JScrollPane(table), c);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton newCustomCodeButton = new MagicToolBarButton("plus", "New", e -> switchToNewCustomCodePanel(product));
		toolBar.add(newCustomCodeButton);
	}

    private void switchToNewCustomCodePanel(Product2 product) {
    	ProductCustomCode customCode = new ProductCustomCode();
    	customCode.setProduct(product);
    	
    	getMagicFrame().switchToAddNewCustomCodePanel(customCode);
	}

	private class ProductCustomCodesTableModel extends ListBackedTableModel<ProductCustomCode> {

        private final String[] columnNames = {"Code", "Remarks"};
	    
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
        	ProductCustomCode customCode = getItem(rowIndex);
            switch (columnIndex) {
            case CODE_COLUMN_INDEX:
                return customCode.getCode();
            case REMARKS_COLUMN_INDEX:
                return customCode.getRemarks();
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