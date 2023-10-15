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
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.InvalidProductCodeException;
import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SelectSupplierDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.ReceiveDelivery;
import com.pj.magic.model.ReceiveDeliveryItem;
import com.pj.magic.model.Supplier;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.ReceiveDeliveryService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ReceiveDeliveryPanel extends StandardMagicPanel {

    private static final long serialVersionUID = -6676395278966161803L;
    
    public static final int PRODUCT_CODE_COLUMN_INDEX = 0;
    public static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
    public static final int UNIT_COLUMN_INDEX = 2;
    public static final int QUANTITY_COLUMN_INDEX = 3;
    public static final int EXISTING_PRODUCT_COLUMN_INDEX = 4;
    
	@Autowired private SelectSupplierDialog selectSupplierDialog;
	@Autowired private ReceiveDeliveryService receiveDeliveryService;
	@Autowired private ProductService productService;
	
	private Product product = null;
	private MagicListTable table;
	private ReceiveDeliveryItemsTableModel tableModel = new ReceiveDeliveryItemsTableModel();
	
	private MagicTextField supplierCodeField;
	private JLabel supplierNameField;
	private JButton selectSupplierButton;
	private JLabel idLabel = new JLabel();
	private JLabel receiveDateLabel = new JLabel();
	private JLabel receivedByLabel = new JLabel();
	private MagicTextField codeField;
	private JTextField productDescriptionField = new JTextField();
	private MagicTextField unitField = new MagicTextField(); 
	private MagicTextField quantityField;
	private JButton toCaseButton = new JButton("Toggle CASE");
	private JButton addButton = new JButton("Add");
	private JButton clearButton = new JButton("Clear");
	private JLabel totalItemsField;
	private MagicToolBarButton deleteButton;
	private MagicToolBarButton postButton;
	private MagicToolBarButton deleteItemButton;
	
	private ReceiveDelivery receiveDelivery;
	
	public ReceiveDeliveryPanel() {
		setTitle("Receive Delivery");
	}
	
	@Override
	protected void initializeComponents() {
		table = new MagicListTable(tableModel);
		initializeTable();
		
		supplierCodeField = new MagicTextField();
		supplierCodeField.setMaximumLength(15);
		
		productDescriptionField.setEditable(false);
		
		codeField = new MagicTextField();
		codeField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				String value = codeField.getText();
				if (value.length() == 12) {
					product = productService.findProductByCode(value);
					if (product != null) {
						productDescriptionField.setText(product.getDescription());
						unitField.setText(product.getUnits().get(0));
						quantityField.setText(null);
						quantityField.requestFocusInWindow();
					} else {
						showErrorMessage("Invalid product code: " + value);
						unitField.requestFocusInWindow();
						codeField.setText(value);
					}
				}
			}
			
		});
//		codeField.addFocusListener(new FocusAdapter() {
//			
//			@Override
//			public void focusGained(FocusEvent e) {
//				codeField.setText(null);
//				productDescriptionField.setText(null);
//				unitField.setText(null);
//				quantityField.setText(null);
//				product = null;
//			}
//			
//		});
		
		quantityField = new MagicTextField();
		quantityField.setNumbersOnly(true);
		
		toCaseButton.addActionListener(e -> convertToCaseUnit());
		addButton.addActionListener(e -> addItem());
		clearButton.addActionListener(e -> {
			codeField.setText(null);
			productDescriptionField.setText(null);
			unitField.setText(null);
			quantityField.setText(null);
			codeField.requestFocusInWindow();
		});
		
		updateTotalsPanelWhenItemsTableChanges();
		
		selectSupplierButton = new EllipsisButton();
		selectSupplierButton.setToolTipText("Select Supplier (F5)");
		selectSupplierButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectSupplier();
			}
			
		});
	}

	private void initializeTable() {
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(200);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(100);
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				int selectedRow = table.getSelectedRow();
				if (selectedRow != -1) {
					ReceiveDeliveryItem item = tableModel.getItem(table.getSelectedRow());
					codeField.setText(item.getCode());
					unitField.setText(item.getUnit());
					quantityField.setText(String.valueOf(item.getQuantity()));
					Product product = productService.findProductByCode(item.getCode());
					if (product != null) {
						productDescriptionField.setText(product.getDescription());
					}
					quantityField.requestFocusInWindow();
				}
			}
		});
		
	}
	
	private void selectSupplier() {
		selectSupplierDialog.searchSuppliers(supplierCodeField.getText());
		selectSupplierDialog.setVisible(true);
		
		Supplier selectedSupplier = selectSupplierDialog.getSelectedSupplier();
		if (selectedSupplier != null) {
			receiveDelivery.setSupplier(selectedSupplier);
			supplierCodeField.setText(selectedSupplier.getCode());
			supplierNameField.setText(selectedSupplier.getName());
			
			try {
				receiveDeliveryService.save(receiveDelivery);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving! " + e.getMessage());
				return;
			}
			
			updateDisplay(receiveDelivery);
		}
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
			productDescriptionField.setText(product.getDescription());
			unitField.setText(product.getUnits().get(0));
			quantityField.requestFocusInWindow();
		} else {
			showErrorMessage("No product defined for CASE unit");
		}
	}
	
	private void addItem() {
		String code = codeField.getText();
		if (StringUtils.isEmpty(code)) {
			codeField.requestFocusInWindow();
			return;
		}
		
		String unit = unitField.getText();
		if (StringUtils.isEmpty(unit)) {
			showErrorMessage("Unit is empty");
			unitField.requestFocusInWindow();
			return;
		}
		
		if (StringUtils.isEmpty(quantityField.getText())) {
			showErrorMessage("Quantity is empty");
			quantityField.requestFocusInWindow();
			return;
		}
		
		product = productService.findProductByCode(code);
		if (product != null) {
			if (!unit.equals(product.getUnits().get(0))) {
				showErrorMessage("Unit does not match existing code");
				unitField.requestFocusInWindow();
				return;
			}
		}
		
		ReceiveDeliveryItem item = new ReceiveDeliveryItem();
		item.setParent(receiveDelivery);
		item.setCode(code);
		item.setProduct(product);
		item.setUnit(unit);
		item.setQuantity(quantityField.getTextAsInteger());
		
		if (tableModel.getItems().contains(item)) {
			if (confirm("Item already exists. Overwrite?")) {
				ReceiveDeliveryItem selectedItem = null;
				for (ReceiveDeliveryItem tableItem : tableModel.getItems()) {
					if (tableItem.equals(item)) {
						selectedItem = tableItem;
					}
				}
				item.setId(selectedItem.getId());

				try {
					receiveDeliveryService.save(item);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					showErrorMessage("Error occurred during saving! " + e.getMessage());
					return;
				}
				
				selectedItem.setQuantity(item.getQuantity());
				tableModel.fireTableDataChanged();
			} else {
				return;
			}
		} else {
			tableModel.addItem(item);
		}
		
		codeField.setText(null);
		productDescriptionField.setText(null);
		unitField.setText(null);
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
		getMagicFrame().switchToReceiveDeliveryListPanel();
	}
	
	public void updateDisplay(ReceiveDelivery receiveDelivery) {
		if (receiveDelivery.getId() == null) {
			this.receiveDelivery = receiveDelivery;
			clearDisplay();
			focusOnComponentWhenThisPanelIsDisplayed(supplierCodeField);
		} else {
			this.receiveDelivery = receiveDelivery = receiveDeliveryService.getDeliveryService(receiveDelivery.getId());
			tableModel.setItems(receiveDelivery.getItems());
			idLabel.setText(String.valueOf(receiveDelivery.getId()));
			supplierCodeField.setText(receiveDelivery.getSupplier().getCode());
			supplierNameField.setText(receiveDelivery.getSupplier().getName());
			receiveDateLabel.setText(FormatterUtil.formatDateTime(receiveDelivery.getReceiveDate()));
			receivedByLabel.setText(receiveDelivery.getReceivedBy().getUsername());
			codeField.setEnabled(true);
			quantityField.setEnabled(true);
			toCaseButton.setEnabled(true);
			addButton.setEnabled(true);
			clearButton.setEnabled(true);
			deleteButton.setEnabled(true);
			postButton.setEnabled(true);
			focusOnComponentWhenThisPanelIsDisplayed(codeField);
		}
	}

	private void clearDisplay() {
		idLabel.setText(null);
		supplierCodeField.setText(null);
		supplierCodeField.setEnabled(true);
		supplierNameField.setText(null);
		receiveDateLabel.setText(null);
		receivedByLabel.setText(null);
		codeField.setText(null);
		codeField.setEnabled(false);
		productDescriptionField.setText(null);
		unitField.setText(null);
		quantityField.setText(null);
		quantityField.setEnabled(false);
		totalItemsField.setText("0");
		tableModel.clear();
		toCaseButton.setEnabled(false);
		addButton.setEnabled(false);
		clearButton.setEnabled(false);
		deleteButton.setEnabled(false);
		postButton.setEnabled(false);
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
		mainPanel.add(ComponentUtil.createLabel(100, "Supplier:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createSupplierPanel(), c);
		
		c.weightx = c.weighty = 0.0;
		c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "ID:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		idLabel.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(idLabel, c);
		
		currentRow++;
		
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
		mainPanel.add(ComponentUtil.createGenericPanel(
				codeField,
				Box.createHorizontalStrut(5),
				toCaseButton), c);
		
		c.weightx = c.weighty = 0.0;
		c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Receive Date:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(receiveDateLabel, c);
		
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
		productDescriptionField.setPreferredSize(new Dimension(300, 25));
		mainPanel.add(productDescriptionField, c);
		
		c.weightx = c.weighty = 0.0;
		c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createLabel(100, "Received By:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		receivedByLabel.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(receivedByLabel, c);
		
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
		unitField.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(unitField, c);
		
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
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(table);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}
	
	private JPanel createSupplierPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierCodeField.setPreferredSize(new Dimension(120, 25));
		panel.add(supplierCodeField, c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(selectSupplierButton, c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createFiller(10, 20), c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierNameField = ComponentUtil.createLabel(300, "");
		panel.add(supplierNameField, c);
		
		return panel;
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
		try {
			PurchaseOrder purchaseOrder = receiveDeliveryService.post(receiveDelivery);
			showMessage("Receive Delivery posted!");
			getMagicFrame().switchToPurchaseOrderPanel(purchaseOrder);
		} catch (InvalidProductCodeException e) {
			showErrorMessage("Invalid product code: " + e.getCode());
			return;
		}
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		deleteButton = new MagicToolBarButton("trash", "Delete");
		deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteReceiveDelivery();
			}
		});
		toolBar.add(deleteButton);
		
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postReceiveDelivery();
			}
		});
		toolBar.add(postButton);
		
	}
	
	private void deleteReceiveDelivery() {
		if (confirm("Do you really want to delete this Receive Delivery?")) {
			try {
				receiveDeliveryService.delete(receiveDelivery);
				showMessage("Receive Delivery deleted");
				getMagicFrame().switchToReceiveDeliveryListPanel();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred when deleting: " + e.getMessage());
			}
		}
	}

	private static final String[] columnNames = {"Code", "Description", "Unit", "Quantity", "Existing Code?"};
	
	private class ReceiveDeliveryItemsTableModel extends ListBackedTableModel<ReceiveDeliveryItem> {

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
			ReceiveDeliveryItem item = getItems().get(rowIndex);
			switch (columnIndex) {
			case PRODUCT_CODE_COLUMN_INDEX:
				return item.getCode();
			case PRODUCT_DESCRIPTION_COLUMN_INDEX:
				return item.getProduct() != null ? item.getProduct().getDescription() : null;
			case UNIT_COLUMN_INDEX:
				return item.getUnit();
			case QUANTITY_COLUMN_INDEX:
				return item.getQuantity();
			case EXISTING_PRODUCT_COLUMN_INDEX:
				return item.getProduct() != null ? "Yes" : "No";
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}

		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}
		
		@Override
		public void addItem(ReceiveDeliveryItem item) {
			receiveDeliveryService.save(item);
			super.addItem(item);
		}
		
		@Override
		public void removeItem(int index) {
			receiveDeliveryService.delete(getItem(index));
			super.removeItem(index);
		}
		
	}

}