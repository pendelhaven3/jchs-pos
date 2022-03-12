package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchProduct2Dialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.Product2;
import com.pj.magic.model.Unit;
import com.pj.magic.model.search.Product2SearchCriteria;
import com.pj.magic.service.Product2Service;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class ProductCostListPanel extends StandardMagicPanel {

    private static final int ID_COLUMN_INDEX = 0;
    private static final int DESCRIPTION_COLUMN_INDEX = 1;
    private static final int GROSS_COST_CASE_COLUMN_INDEX = 2;
    private static final int GROSS_COST_TIES_COLUMN_INDEX = 3;
    private static final int GROSS_COST_PACK_COLUMN_INDEX = 4;
    private static final int GROSS_COST_HDZN_COLUMN_INDEX = 5;
    private static final int GROSS_COST_PCS_COLUMN_INDEX = 6;
    private static final int FINAL_COST_CASE_COLUMN_INDEX = 7;
    private static final int FINAL_COST_TIES_COLUMN_INDEX = 8;
    private static final int FINAL_COST_PACK_COLUMN_INDEX = 9;
    private static final int FINAL_COST_HDZN_COLUMN_INDEX = 10;
    private static final int FINAL_COST_PCS_COLUMN_INDEX = 11;
    
	@Autowired private Product2Service product2Service;
	@Autowired private SearchProduct2Dialog searchProduct2Dialog;
	
	private MagicListTable table;
	private ProductsTableModel tableModel = new ProductsTableModel();
	
	@Override
	public String getTitle() {
		return "Product Cost List";
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
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(ID_COLUMN_INDEX).setPreferredWidth(80);
		columnModel.getColumn(DESCRIPTION_COLUMN_INDEX).setPreferredWidth(200);
		columnModel.getColumn(GROSS_COST_CASE_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(GROSS_COST_TIES_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(GROSS_COST_PACK_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(GROSS_COST_HDZN_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(GROSS_COST_PCS_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(FINAL_COST_CASE_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(FINAL_COST_TIES_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(FINAL_COST_PACK_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(FINAL_COST_HDZN_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(FINAL_COST_PCS_COLUMN_INDEX).setPreferredWidth(50);
		
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.RIGHT);
		renderer.getInsets().set(0, 0, 0, 10);
		
		columnModel.getColumn(GROSS_COST_CASE_COLUMN_INDEX).setCellRenderer(renderer);
		columnModel.getColumn(GROSS_COST_TIES_COLUMN_INDEX).setCellRenderer(renderer);
		columnModel.getColumn(GROSS_COST_PACK_COLUMN_INDEX).setCellRenderer(renderer);
		columnModel.getColumn(GROSS_COST_HDZN_COLUMN_INDEX).setCellRenderer(renderer);
		columnModel.getColumn(GROSS_COST_PCS_COLUMN_INDEX).setCellRenderer(renderer);
		columnModel.getColumn(FINAL_COST_CASE_COLUMN_INDEX).setCellRenderer(renderer);
		columnModel.getColumn(FINAL_COST_TIES_COLUMN_INDEX).setCellRenderer(renderer);
		columnModel.getColumn(FINAL_COST_PACK_COLUMN_INDEX).setCellRenderer(renderer);
		columnModel.getColumn(FINAL_COST_HDZN_COLUMN_INDEX).setCellRenderer(renderer);
		columnModel.getColumn(FINAL_COST_PCS_COLUMN_INDEX).setCellRenderer(renderer);
		
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
                editProduct();
            }
        });
	    
	    table.addDoubleClickMouseListener(() -> editProduct());
	}

	protected void editProduct() {
		Product2 product = tableModel.getItem(table.getSelectedRow());
		getMagicFrame().switchToEditProductCostPanel(product);
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

    private class ProductsTableModel extends ListBackedTableModel<Product2>{

        private final String[] columnNames = {"Product ID", "Description", 
        		"Gross Cost CASE", "Gross Cost TIES", "Gross Cost PACK", "Gross Cost HDZN", "Gross Cost PCS",
        		"Final Cost CASE", "Final Cost TIES", "Final Cost PACK", "Final Cost HDZN", "Final Cost PCS"};
	    
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Product2 product = getItem(rowIndex);
            switch (columnIndex) {
            case ID_COLUMN_INDEX:
                return String.valueOf(product.getId());
            case DESCRIPTION_COLUMN_INDEX:
                return product.getDescription();
            case GROSS_COST_CASE_COLUMN_INDEX:
                return FormatterUtil.formatAmount(product.getGrossCost(Unit.CASE));
            case GROSS_COST_TIES_COLUMN_INDEX:
                return FormatterUtil.formatAmount(product.getGrossCost(Unit.TIES));
            case GROSS_COST_PACK_COLUMN_INDEX:
                return FormatterUtil.formatAmount(product.getGrossCost(Unit.PACK));
            case GROSS_COST_HDZN_COLUMN_INDEX:
                return FormatterUtil.formatAmount(product.getGrossCost(Unit.HDZN));
            case GROSS_COST_PCS_COLUMN_INDEX:
                return FormatterUtil.formatAmount(product.getGrossCost(Unit.PIECES));
            case FINAL_COST_CASE_COLUMN_INDEX:
                return FormatterUtil.formatAmount(product.getGrossCost(Unit.CASE));
            case FINAL_COST_TIES_COLUMN_INDEX:
                return FormatterUtil.formatAmount(product.getGrossCost(Unit.TIES));
            case FINAL_COST_PACK_COLUMN_INDEX:
                return FormatterUtil.formatAmount(product.getGrossCost(Unit.PACK));
            case FINAL_COST_HDZN_COLUMN_INDEX:
                return FormatterUtil.formatAmount(product.getGrossCost(Unit.HDZN));
            case FINAL_COST_PCS_COLUMN_INDEX:
                return FormatterUtil.formatAmount(product.getGrossCost(Unit.PIECES));
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
