package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchProduct2Dialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.Product2;
import com.pj.magic.model.search.Product2SearchCriteria;
import com.pj.magic.service.Product2Service;
import com.pj.magic.util.ComponentUtil;

@Component
public class ProductListPanel extends StandardMagicPanel {

    private static final int PRODUCT_ID_COLUMN_INDEX = 0;
    private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
    
	@Autowired private Product2Service product2Service;
	@Autowired private SearchProduct2Dialog searchProduct2Dialog;
	
	private MagicListTable table;
	private Products2TableModel tableModel = new Products2TableModel();
	
	@Override
	public String getTitle() {
		return "Product List";
	}
	
	public void updateDisplay() {
		List<Product2> products = product2Service.getAllActiveProducts();
		tableModel.setItems(products);
		if (!products.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
		searchProduct2Dialog.updateDisplay();
	}

	@Override
	protected void initializeComponents() {
		table = new MagicListTable(tableModel);
        table.getColumnModel().getColumn(PRODUCT_ID_COLUMN_INDEX).setPreferredWidth(100);
        table.getColumnModel().getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(400);
        
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
		
		currentRow++; // first row
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(new JScrollPane(table), c);
	}

	private void searchProducts() {
		searchProduct2Dialog.setVisible(true);
		
		Product2SearchCriteria criteria = searchProduct2Dialog.getSearchCriteria();
		if (criteria != null) {
			List<Product2> products = product2Service.searchProducts(criteria);
			tableModel.setItems(products);
			if (!products.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}

    @Override
	protected void registerKeyBindings() {
	    table.setKeyEvent(KeyEvent.VK_ENTER, new AbstractAction() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                selectProduct();
            }
        });
	    
	    table.addDoubleClickMouseListener(() -> selectProduct());
	    
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}

	protected void selectProduct() {
		Product2 product2 = tableModel.getItem(table.getSelectedRow());
		getMagicFrame().setBackPanel("PRODUCT_LIST_PANEL");
		getMagicFrame().switchToEditProductPanel(product2);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToInventoryMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
        JButton showAllButton = new MagicToolBarButton("all", "Show All");
        showAllButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                showActiveProducts();
            }
        });
        toolBar.add(showAllButton);
        
        JButton searchButton = new MagicToolBarButton("search", "Search");
        searchButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                searchProducts();
            }
        });
        
        toolBar.add(searchButton);
	}

	private void showActiveProducts() {
		tableModel.setItems(product2Service.getAllActiveProducts());
		table.changeSelection(0, 0, false, false);
		table.requestFocusInWindow();
		searchProduct2Dialog.updateDisplay();
	}

    private class Products2TableModel extends ListBackedTableModel<Product2>{

        private final String[] columnNames = {"Product ID", "Description"};
	    
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Product2 product = getItem(rowIndex);
            switch (columnIndex) {
            case PRODUCT_ID_COLUMN_INDEX:
                return product.getId();
            case PRODUCT_DESCRIPTION_COLUMN_INDEX:
                return product.getDescription();
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
