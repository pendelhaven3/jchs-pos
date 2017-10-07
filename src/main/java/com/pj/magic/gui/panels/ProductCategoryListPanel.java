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
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ProductCategoriesTableModel;
import com.pj.magic.model.ProductCategory;
import com.pj.magic.service.ProductCategoryService;
import com.pj.magic.util.ComponentUtil;

@Component
public class ProductCategoryListPanel extends StandardMagicPanel {

	private static final String EDIT_PRODUCT_CATEGORY_ACTION_NAME = "editProductCategory";
	
	@Autowired private ProductCategoryService productCategoryService;
	
	private JTable table;
	private ProductCategoriesTableModel tableModel = new ProductCategoriesTableModel();
	
	public void updateDisplay() {
		List<ProductCategory> categories = productCategoryService.getAllProductCategories();
		tableModel.setProductCategories(categories);
		if (!categories.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
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
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), EDIT_PRODUCT_CATEGORY_ACTION_NAME);
		table.getActionMap().put(EDIT_PRODUCT_CATEGORY_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectProductCategory();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectProductCategory();
			}
		});
	}

	protected void selectProductCategory() {
		ProductCategory category = tableModel.getProductCategory(table.getSelectedRow());
		getMagicFrame().switchToEditProductCategoryPanel(category);
	}

	private void switchToNewProductCategoryPanel() {
		getMagicFrame().switchToAddNewProductCategoryPanel();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToRecordsMaintenanceMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton postButton = new MagicToolBarButton("plus", "New");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewProductCategoryPanel();
			}
		});
		toolBar.add(postButton);
	}
	
}
