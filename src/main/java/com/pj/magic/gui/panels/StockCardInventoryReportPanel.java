package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.Product;
import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.Unit;
import com.pj.magic.model.search.ProductSearchCriteria;
import com.pj.magic.model.search.StockCardInventoryReportCriteria;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.ReportService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class StockCardInventoryReportPanel extends StandardMagicPanel {

	private static final int POST_DATE_COLUMN_INDEX = 0;
	private static final int TRANSACTION_NUMBER_COLUMN_INDEX = 1;
	private static final int SUPPLIER_OR_CUSTOMER_NAME_COLUMN_INDEX = 2;
	private static final int TRANSACTION_TYPE_COLUMN_INDEX = 3;
	private static final int UNIT_COLUMN_INDEX = 4;
	private static final int ADD_QUANTITY_COLUMN_INDEX = 5;
	private static final int LESS_QUANTITY_COLUMN_INDEX = 6;
	private static final int CURRENT_COST_COLUMN_INDEX = 7;
	private static final int AMOUNT_COLUMN_INDEX = 8;
	private static final int REFERENCE_NUMBER_COLUMN_INDEX = 9;
	
	@Autowired private ProductService productService;
	@Autowired private ReportService reportService;
	@Autowired private SelectProductDialog selectProductDialog;
	
	private MagicTextField productCodeField;
	private JLabel productDescriptionLabel;
	private UtilCalendarModel fromDateModel;
	private UtilCalendarModel toDateModel;
	private JButton generateButton;
	private EllipsisButton selectProductButton;
	private JComboBox<String> unitComboBox;
	private JCheckBox fromLastInventoryCheckCheckBox;
	private JCheckBox salesInvoiceTransactionTypeCheckBox;
	private JCheckBox stockQuantityConversionTransactionTypeCheckBox;
	private JCheckBox salesReturnTransactionTypeCheckBox;
	private JCheckBox adjustmentInTransactionTypeCheckBox;
	private JCheckBox adjustmentOutTransactionTypeCheckBox;
	private JCheckBox receivingReceiptTransactionTypeCheckBox;
	private JCheckBox inventoryCheckTransactionTypeCheckBox;
	private JCheckBox promoRedemptionTransactionTypeCheckBox;
	private JCheckBox purchaseReturnTransactionTypeCheckBox;
	private JLabel currentQuantityLabel;
	private JLabel totalLessQuantityLabel;
	private JLabel totalAddQuantityLabel;
	private JLabel quantityDifferenceLabel;
	private MagicListTable table;
	private StockCardInventoryReportTableModel tableModel;
	
	@Override
	protected void initializeComponents() {
		productCodeField = new MagicTextField();
		productCodeField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		
		productDescriptionLabel = new JLabel();
		
		selectProductButton = new EllipsisButton();
		selectProductButton.setToolTipText("Select Product");
		selectProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectProductDialog();
			}
		});
		
		fromDateModel = new UtilCalendarModel();
		toDateModel = new UtilCalendarModel();
		
		unitComboBox = new JComboBox<>(
				new String[] {null, Unit.PIECES, Unit.DOZEN, Unit.CARTON, Unit.TIE, Unit.CASE});
		fromLastInventoryCheckCheckBox = new JCheckBox();
		
		salesInvoiceTransactionTypeCheckBox = new JCheckBox();
		receivingReceiptTransactionTypeCheckBox = new JCheckBox();
		stockQuantityConversionTransactionTypeCheckBox = new JCheckBox();
		adjustmentOutTransactionTypeCheckBox = new JCheckBox();
		adjustmentInTransactionTypeCheckBox = new JCheckBox();
		salesReturnTransactionTypeCheckBox = new JCheckBox();
		inventoryCheckTransactionTypeCheckBox = new JCheckBox();
		promoRedemptionTransactionTypeCheckBox = new JCheckBox();
		purchaseReturnTransactionTypeCheckBox = new JCheckBox();
		
		generateButton = new JButton("Generate");
		generateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateStockCardInventoryReport();
			}
		});
		
		totalLessQuantityLabel = new JLabel();
		totalAddQuantityLabel = new JLabel();
		
		focusOnComponentWhenThisPanelIsDisplayed(productCodeField);
		
		initializeTable();
	}

	private void initializeTable() {
		tableModel = new StockCardInventoryReportTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(POST_DATE_COLUMN_INDEX).setPreferredWidth(150);
		columnModel.getColumn(SUPPLIER_OR_CUSTOMER_NAME_COLUMN_INDEX).setPreferredWidth(200);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(ADD_QUANTITY_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(LESS_QUANTITY_COLUMN_INDEX).setPreferredWidth(50);
	}

	private void openSelectProductDialog() {
		ProductSearchCriteria criteria = new ProductSearchCriteria();
		criteria.setCodeOrDescriptionLike(productCodeField.getText());
		criteria.setActive(true);
		
		selectProductDialog.searchProducts(criteria);
		selectProductDialog.setVisible(true);
		
		Product product = selectProductDialog.getSelectedProduct();
		if (product != null) {
			productCodeField.setText(product.getCode());
			productDescriptionLabel.setText(product.getDescription());
		}
	}

	private void generateStockCardInventoryReport() {
		String productCode = productCodeField.getText();
		if (StringUtils.isEmpty(productCode)) {
			showErrorMessage("Product Code must be specified");
			return;
		}
		
		Product product = productService.findProductByCode(productCode);
		if (product == null) {
			showErrorMessage("No product matching code specified");
			return;
		} else {
			productCodeField.setText(product.getCode());
			productDescriptionLabel.setText(product.getDescription());
		}
		
		StockCardInventoryReportCriteria criteria = new StockCardInventoryReportCriteria();
		criteria.setProduct(product);
		if (fromDateModel.getValue() != null) {
			criteria.setFromDate(fromDateModel.getValue().getTime());
		}
		if (toDateModel.getValue() != null) {
			criteria.setToDate(toDateModel.getValue().getTime());
		}
		if (unitComboBox.getSelectedItem() != null) {
			criteria.setUnit((String)unitComboBox.getSelectedItem());
		}
		criteria.setFromLastInventoryCheck(fromLastInventoryCheckCheckBox.isSelected());
		setTransactionTypeCriteria(criteria);
		
		List<StockCardInventoryReportItem> items = reportService.getStockCardInventoryReport(criteria);
		tableModel.setItems(items);
		if (items.isEmpty()) {
			showErrorMessage("No records found");
		} else {
			table.selectFirstRow();
		}
		
		if (unitComboBox.getSelectedIndex() > 0) {
			int totalLessQuantity = getTotalLessQuantity(items);
			int totalAddQuantity = getTotalAddQuantity(items);
			currentQuantityLabel.setText(FormatterUtil.formatInteger(product.getUnitQuantity((String)unitComboBox.getSelectedItem())));
			totalLessQuantityLabel.setText(FormatterUtil.formatInteger(totalLessQuantity));
			totalAddQuantityLabel.setText(FormatterUtil.formatInteger(totalAddQuantity));
			quantityDifferenceLabel.setText(FormatterUtil.formatInteger(totalAddQuantity - totalLessQuantity));
		} else {
			ComponentUtil.clearLabels(currentQuantityLabel, totalLessQuantityLabel, totalAddQuantityLabel,
					quantityDifferenceLabel);
		}
	}

	private void setTransactionTypeCriteria(StockCardInventoryReportCriteria criteria) {
		List<String> transactionTypes = criteria.getTransactionTypes();
		if (salesInvoiceTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("SALES INVOICE");
		}
		if (stockQuantityConversionTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("STOCK QTY CONVERSION");
		}
		if (adjustmentInTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("ADJUSTMENT IN");
		}
		if (adjustmentOutTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("ADJUSTMENT OUT");
		}
		if (receivingReceiptTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("RECEIVING RECEIPT");
		}
		if (salesReturnTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("SALES RETURN");
		}
		if (inventoryCheckTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("INVENTORY CHECK");
		}
		if (promoRedemptionTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("PROMO REDEMPTION");
		}
		if (purchaseReturnTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("PURCHASE RETURN");
		}
	}

	private static int getTotalLessQuantity(List<StockCardInventoryReportItem> items) {
		int total = 0;
		for (StockCardInventoryReportItem item : items) {
			if (item.getLessQuantity() != null) {
				total += item.getLessQuantity();
			}
		}
		return total;
	}

	private static int getTotalAddQuantity(List<StockCardInventoryReportItem> items) {
		int total = 0;
		for (StockCardInventoryReportItem item : items) {
			if (item.getAddQuantity() != null) {
				total += item.getAddQuantity();
			}
		}
		return total;
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Product Code: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 5;
		mainPanel.add(createProductPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "From Date: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl datePanel = new JDatePanelImpl(fromDateModel);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "To Date: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		datePanel = new JDatePanelImpl(toDateModel);
		datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0; // right space filler
		c.gridx = 6;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Unit: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		unitComboBox.setPreferredSize(new Dimension(60, 25));
		mainPanel.add(unitComboBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(
				ComponentUtil.createGenericPanel(
						ComponentUtil.createLabel(280, "From Last Inventory Check: "),
						fromLastInventoryCheckCheckBox), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Transaction Type: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createTransactionTypesPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createVerticalFiller(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.gridwidth = 4;
		generateButton.setPreferredSize(new Dimension(160, 25));
		mainPanel.add(generateButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 30), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 7;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(table);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 7;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}

	private JPanel createTransactionTypesPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.insets.right = 5;
		
		c.gridy = currentRow;
		panel.add(salesInvoiceTransactionTypeCheckBox, c);
		panel.add(new JLabel("Sales Invoice"), c);
		panel.add(receivingReceiptTransactionTypeCheckBox, c);
		panel.add(new JLabel("Receiving Receipt"), c);
		
		currentRow++;
		
		c.gridy = currentRow;
		panel.add(stockQuantityConversionTransactionTypeCheckBox, c);
		panel.add(new JLabel("Stock Quantity Conversion"), c);
		panel.add(salesReturnTransactionTypeCheckBox, c);
		panel.add(new JLabel("Sales Return"), c);
		
		currentRow++;
		
		c.gridy = currentRow;
		panel.add(adjustmentInTransactionTypeCheckBox, c);
		panel.add(new JLabel("Adjustment In"), c);
		panel.add(inventoryCheckTransactionTypeCheckBox, c);
		panel.add(new JLabel("Inventory Check"), c);
		
		currentRow++;
		
		c.gridy = currentRow;
		panel.add(adjustmentOutTransactionTypeCheckBox, c);
		panel.add(new JLabel("Adjustment Out"), c);
		panel.add(promoRedemptionTransactionTypeCheckBox, c);
		panel.add(new JLabel("Promo Redemption"), c);
		
		currentRow++;
		
		c.gridy = currentRow;
		panel.add(purchaseReturnTransactionTypeCheckBox, c);
		panel.add(new JLabel("Purchase Return"), c);
		
		return panel;
	}

	private JPanel createProductPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		productCodeField.setPreferredSize(new Dimension(150, 25));
		panel.add(productCodeField, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectProductButton.setPreferredSize(new Dimension(30, 25));
		panel.add(selectProductButton, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(Box.createHorizontalStrut(10), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		productDescriptionLabel.setPreferredSize(new Dimension(300, 20));
		panel.add(productDescriptionLabel, c);
		
		return panel;
	}

	@Override
	protected void registerKeyBindings() {
		productCodeField.onF5Key(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectProductDialog();
			}
		});
	}

	public void updateDisplay() {
		productCodeField.setText(null);
		productDescriptionLabel.setText(null);
		fromDateModel.setValue(null);
		toDateModel.setValue(null);
		unitComboBox.setSelectedItem(null);
		fromLastInventoryCheckCheckBox.setSelected(false);
		ComponentUtil.clearLabels(currentQuantityLabel, totalLessQuantityLabel, totalAddQuantityLabel,
				quantityDifferenceLabel);
		tableModel.clear();
		
		salesInvoiceTransactionTypeCheckBox.setSelected(false);
		receivingReceiptTransactionTypeCheckBox.setSelected(false);
		stockQuantityConversionTransactionTypeCheckBox.setSelected(false);
		adjustmentInTransactionTypeCheckBox.setSelected(false);
		adjustmentOutTransactionTypeCheckBox.setSelected(false);
		salesReturnTransactionTypeCheckBox.setSelected(false);
		inventoryCheckTransactionTypeCheckBox.setSelected(false);
		promoRedemptionTransactionTypeCheckBox.setSelected(false);
		purchaseReturnTransactionTypeCheckBox.setSelected(false);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToStockMovementMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Current Quantity:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.insets.right = 20;
		currentQuantityLabel = ComponentUtil.createRightLabel(50);
		panel.add(currentQuantityLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Add Quantity:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAddQuantityLabel = ComponentUtil.createRightLabel(50);
		panel.add(totalAddQuantityLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Less Quantity:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.insets.right = 10;
		totalLessQuantityLabel = ComponentUtil.createRightLabel(50);
		panel.add(totalLessQuantityLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Difference:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		quantityDifferenceLabel = ComponentUtil.createRightLabel(50);
		panel.add(quantityDifferenceLabel, c);
		
		return panel;
	}
	
	private class StockCardInventoryReportTableModel extends AbstractTableModel {

		private final String[] columnNames = 
			{"Post Date", "Trans. No.", "Supplier/Customer", "Trans. Type", "Unit", "Add Qty", "Less Qty", 
				"Cost / Price", "Amount", "Ref. No."};
		
		private List<StockCardInventoryReportItem> items = new ArrayList<>();
		
		@Override
		public int getRowCount() {
			return items.size();
		}

		public void clear() {
			items.clear();
			fireTableDataChanged();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			StockCardInventoryReportItem item = items.get(rowIndex);
			switch (columnIndex) {
			case POST_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDateTime(item.getPostDate());
			case TRANSACTION_NUMBER_COLUMN_INDEX:
				return item.getTransactionNumber();
			case SUPPLIER_OR_CUSTOMER_NAME_COLUMN_INDEX:
				return item.getSupplierOrCustomerName();
			case TRANSACTION_TYPE_COLUMN_INDEX:
				return item.getTransactionType();
			case UNIT_COLUMN_INDEX:
				return item.getUnit();
			case ADD_QUANTITY_COLUMN_INDEX:
				return item.getAddQuantity();
			case LESS_QUANTITY_COLUMN_INDEX:
				return item.getLessQuantity();
			case CURRENT_COST_COLUMN_INDEX:
				BigDecimal costOrPrice = item.getCurrentCostOrSellingPrice();
				return (costOrPrice != null) ? FormatterUtil.formatAmount(costOrPrice) : null;
			case AMOUNT_COLUMN_INDEX:
				BigDecimal amount = item.getAmount();
				return (amount != null) ? FormatterUtil.formatAmount(amount) : null;
			case REFERENCE_NUMBER_COLUMN_INDEX:
				return item.getReferenceNumber();
			default:
				throw new RuntimeException("Fetch invalid column index: " + columnIndex);
			}
		}

		public void setItems(List<StockCardInventoryReportItem> items) {
			this.items = items;
			fireTableDataChanged();
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case CURRENT_COST_COLUMN_INDEX:
			case AMOUNT_COLUMN_INDEX:
				return Number.class;
			default:
				return Object.class;
			}
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
	}
	
}