package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SelectPurchaseOrderDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchaseOrderItem;
import com.pj.magic.service.Product2Service;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.PurchaseOrderService;
import com.pj.magic.util.ComponentUtil;

@Component
public class ReceiveDeliveryPanel extends StandardMagicPanel {

    private static final long serialVersionUID = -6676395278966161803L;
    
    private static final int PRODUCT_CODE_COLUMN_INDEX = 0;
    private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
    private static final int UNIT_COLUMN_INDEX = 2;
    private static final int QUANTITY_COLUMN_INDEX = 3;
    
	@Autowired private ProductService productService;
	@Autowired private Product2Service product2Service;
	@Autowired private PurchaseOrderService purchaseOrderService;
	@Autowired private SelectPurchaseOrderDialog selectPurchaseOrderDialog;
	
	private Product product = null;
	private MagicListTable table;
	private ReceiveDeliveryTableModel tableModel;
	
	private MagicTextField codeField;
	private JLabel productDescriptionLabel = new JLabel();
	private JLabel unitLabel = new JLabel(); 
	private MagicTextField quantityField;
	private JButton toCaseButton = new JButton("Toggle CASE");
	private JButton addButton = new JButton("Add");
	private JButton clearButton = new JButton("Clear");
	private JLabel totalItemsField;
	private MagicToolBarButton postButton;
	private MagicToolBarButton deleteItemButton;
	
	public ReceiveDeliveryPanel() {
		setTitle("Receive Delivery");
	}
	
	@Override
	protected void initializeComponents() {
		tableModel = new ReceiveDeliveryTableModel();
		table = new MagicListTable(tableModel);
		
		codeField = new MagicTextField();
		codeField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				String value = codeField.getText();
				if (value.length() == 12) {
					product = productService.findProductByCode(value);
					if (product != null) {
						productDescriptionLabel.setText(product.getDescription());
						unitLabel.setText(product.getUnits().get(0));
						quantityField.setText(null);
						quantityField.requestFocusInWindow();
					} else {
						showErrorMessage("Invalid product code: " + value);
						codeField.setText(null);
						codeField.requestFocusInWindow();
					}
				}
			}
			
		});
		codeField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusGained(FocusEvent e) {
				codeField.setText(null);
				codeField.setText(null);
				productDescriptionLabel.setText(null);
				unitLabel.setText(null);
				quantityField.setText(null);
				product = null;
			}
			
		});
		
		quantityField = new MagicTextField();
		quantityField.setNumbersOnly(true);
		
		toCaseButton.addActionListener(e -> convertToCaseUnit());
		addButton.addActionListener(e -> addItem());
		clearButton.addActionListener(e -> codeField.requestFocusInWindow());
		
		focusOnComponentWhenThisPanelIsDisplayed(codeField);
		updateTotalsPanelWhenItemsTableChanges();		
	}

	private void convertToCaseUnit() {
		String code = codeField.getText();
		if (!StringUtils.isEmpty(code)) {
			if (code.length() == 12) {
				code = code + "01";
			} else if (code.length() == 14){
				code = code.substring(0, 12);
			} else {
				codeField.setText(null);
			}
		} else {
			return;
		}
		
		product = productService.findProductByCode(code);
		if (product != null) {
			codeField.setText(code);
			productDescriptionLabel.setText(product.getDescription());
			unitLabel.setText(product.getUnits().get(0));
			quantityField.requestFocusInWindow();
		} else {
			showErrorMessage("No product defined for CASE unit");
		}
	}
	
	private void addItem() {
		if (StringUtils.isEmpty(codeField.getText()) || product == null) {
			codeField.requestFocusInWindow();
			return;
		}
		
		if (StringUtils.isEmpty(quantityField.getText())) {
			showErrorMessage("Quantity is empty");
			quantityField.requestFocusInWindow();
			return;
		}
		
		PurchaseOrderItem item = new PurchaseOrderItem();
		item.setCode(product.getCode());
		item.setProduct(product2Service.getProduct(product.getProduct2Id()));
		item.setUnit(product.getUnits().get(0));
		item.setQuantity(quantityField.getTextAsInteger());
		
		if (tableModel.getItems().contains(item)) {
			if (confirm("Item already exists. Overwrite?")) {
				tableModel.getItems().remove(item);
			} else {
				codeField.setText(null);
				productDescriptionLabel.setText(null);
				unitLabel.setText(null);
				quantityField.setText(null);
				codeField.requestFocusInWindow();
				return;
			}
		}
		
		tableModel.addItem(item);
		
		codeField.setText(null);
		productDescriptionLabel.setText(null);
		unitLabel.setText(null);
		quantityField.setText(null);
		codeField.requestFocusInWindow();
	}
	
	private void updateTotalsPanelWhenItemsTableChanges() {
		table.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalItemsField.setText(String.valueOf(tableModel.getItems().size()));
			}
		});
	}

	@Override
	protected void registerKeyBindings() {
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPurchasesMenuPanel();
	}
	
	public void updateDisplay() {
		clearDisplay();
	}

	private void clearDisplay() {
		codeField.setText(null);
		productDescriptionLabel.setText(null);
		unitLabel.setText(null);
		quantityField.setText(null);
		totalItemsField.setText("0");
		tableModel.clear();
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 30), c);

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Scan barcode:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		codeField.setPreferredSize(new Dimension(150, 25));
//		mainPanel.add(codeField, c);
		mainPanel.add(ComponentUtil.createGenericPanel(
				codeField,
				Box.createHorizontalStrut(5),
				toCaseButton), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createGlue(), c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(180, "Product description:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		productDescriptionLabel.setPreferredSize(new Dimension(300, 25));
		mainPanel.add(productDescriptionLabel, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Unit:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		unitLabel.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(unitLabel, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Quantity:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		quantityField.setPreferredSize(new Dimension(50, 25));
		addButton.setPreferredSize(new Dimension(80, 25));
		clearButton.setPreferredSize(new Dimension(80, 25));
		mainPanel.add(ComponentUtil.createGenericPanel(
				quantityField,
				Box.createHorizontalStrut(5),
				addButton,
				Box.createHorizontalStrut(5),
				clearButton), c);
		
		currentRow++;

		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createItemsTableToolBar(), c);

		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(table);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}
	
	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsField = ComponentUtil.createLabel(100, "");
		panel.add(totalItemsField, c);
		
		return panel;
	}

	private JPanel createItemsTableToolBar() {
		JPanel panel = new JPanel();
		
		deleteItemButton = new MagicToolBarButton("minus_small", "Delete Item", true);
		deleteItemButton.addActionListener(e -> removeCurrentlySelectedItem());
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
	}
	
	private void removeCurrentlySelectedItem() {
		if (table.getSelectedRow() != -1) {
			tableModel.removeItem(table.getSelectedRow());
		}
	}

	private void postReceiveDelivery() {
		selectPurchaseOrderDialog.searchUnpostedPurchaseOrders();
		selectPurchaseOrderDialog.setVisible(true);
		PurchaseOrder purchaseOrder = selectPurchaseOrderDialog.getSelectedPurchaseOrder();
		if (purchaseOrder != null) {
			try {
				purchaseOrderService.receiveDelivery(purchaseOrder, tableModel.getItems());
				getMagicFrame().switchToPurchaseOrderPanel(purchaseOrder);
			} catch (Exception e) {
				showErrorMessage("Unexpected error occurred: " + e.getMessage());
			}
		}
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postReceiveDelivery();
			}
		});
		toolBar.add(postButton);
		
	}
	
	private static final String[] columnNames = {"Code", "Description", "Unit", "Quantity"};
	
	private class ReceiveDeliveryTableModel extends ListBackedTableModel<PurchaseOrderItem> {

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
			PurchaseOrderItem item = getItems().get(rowIndex);
			switch (columnIndex) {
			case PRODUCT_CODE_COLUMN_INDEX:
				return item.getCode();
			case PRODUCT_DESCRIPTION_COLUMN_INDEX:
				return item.getProduct().getDescription();
			case UNIT_COLUMN_INDEX:
				return item.getUnit();
			case QUANTITY_COLUMN_INDEX:
				return item.getQuantity();
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