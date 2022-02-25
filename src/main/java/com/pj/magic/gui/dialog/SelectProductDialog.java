package com.pj.magic.gui.dialog;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ProductsTableModel;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.Unit;
import com.pj.magic.model.search.ProductSearchCriteria;
import com.pj.magic.service.Product2Service;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class SelectProductDialog extends MagicDialog {

    private static final long serialVersionUID = -6683629003061338285L;
    
    private static final String SELECT_PRODUCT_ACTION_NAME = "selectProduct";
	private static final String UNIT_PRICE_INFO_TABLE = "unitPriceInfoTable";
	private static final String UNIT_COST_INFO_TABLE = "unitCostInfoTable";

	@Autowired private ProductService productService;

	private ProductsTableModel productsTableModel = new ProductsTableModel();
	private UnitPricesAndQuantitiesTableModel unitPricesAndQuantitiesTableModel;
	private UnitCostsAndQuantitiesTableModel unitCostsAndQuantitiesTableModel;
	private MagicListTable productsTable;
	private JTable unitPricesAndQuantitiesTable;
	private JTable unitCostsAndQuantitiesTable;
	private JPanel infoTablePanel;
	private Product selectedProduct;
	
	public SelectProductDialog() {
		setSize(600, 450);
		setLocationRelativeTo(null);
		setTitle("Select Product");
		
		initialize();
	}

	public void initialize() {
		productsTable = new MagicListTable(productsTableModel);
		productsTable.getColumnModel().getColumn(ProductsTableModel.CODE_COLUMN_INDEX)
			.setPreferredWidth(150);
		productsTable.getColumnModel().getColumn(ProductsTableModel.DESCRIPTION_COLUMN_INDEX)
			.setPreferredWidth(300);
		productsTable.getColumnModel().getColumn(ProductsTableModel.UNIT_COLUMN_INDEX)
			.setPreferredWidth(100);
		
		unitPricesAndQuantitiesTableModel = new UnitPricesAndQuantitiesTableModel();
		unitPricesAndQuantitiesTable = new MagicListTable(unitPricesAndQuantitiesTableModel);
		
		unitCostsAndQuantitiesTableModel = new UnitCostsAndQuantitiesTableModel();
		unitCostsAndQuantitiesTable = new MagicListTable(unitCostsAndQuantitiesTableModel);
		
		layoutComponents();
		registerKeyBindings();
		
		productsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedRow = productsTable.getSelectedRow();
				if (selectedRow != -1) {
					Product product = productsTableModel.getProduct(selectedRow);
					unitPricesAndQuantitiesTableModel.setProduct(product);
					unitCostsAndQuantitiesTableModel.setProduct(product);
				}
			}
		});
	}

	private void registerKeyBindings() {
		productsTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), SELECT_PRODUCT_ACTION_NAME);
		productsTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECT_PRODUCT_ACTION_NAME);
		productsTable.getActionMap().put(SELECT_PRODUCT_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectProduct();
			}
		});
		
		productsTable.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectProduct();
			}
		});
		
	}

	protected void selectProduct() {
		selectedProduct = productsTableModel.getProduct(productsTable.getSelectedRow());
		setVisible(false);
	}

	public Product getSelectedProduct() {
		return selectedProduct;
	}
	
	@Override
	protected void doWhenEscapeKeyPressed() {
		selectedProduct = null;
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;

		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		
		JScrollPane productsScrollPane = new JScrollPane(productsTable);
		productsScrollPane.setPreferredSize(new Dimension(400, 100));
		add(productsScrollPane, c);

		currentRow++;
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		
		add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++;
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		add(createInfoTablePanel(), c);
	}
	
	private JPanel createInfoTablePanel() {
		infoTablePanel = new JPanel(new CardLayout());
		infoTablePanel.setPreferredSize(new Dimension(400, 149));
		
		JScrollPane unitPricesAndQuantitiesScrollPane = new JScrollPane(unitPricesAndQuantitiesTable);
		infoTablePanel.add(unitPricesAndQuantitiesScrollPane, UNIT_PRICE_INFO_TABLE);
		
		JScrollPane unitCostsAndQuantitiesScrollPane = new JScrollPane(unitCostsAndQuantitiesTable);
		infoTablePanel.add(unitCostsAndQuantitiesScrollPane, UNIT_COST_INFO_TABLE);
		
		return infoTablePanel;
	}

	public void searchProducts(String productCode) {
		searchProducts(productCode, null, new PricingScheme(Constants.CANVASSER_PRICING_SCHEME_ID));
	}

	public void searchProducts(String productCode, String currentlySelectedCode) {
		searchProducts(productCode, currentlySelectedCode, new PricingScheme(Constants.CANVASSER_PRICING_SCHEME_ID));
	}

	public void searchProducts(String codeOrDescription, String currentlySelectedCode, PricingScheme pricingScheme) {
		ProductSearchCriteria criteria = new ProductSearchCriteria();
		criteria.setActive(true);
		criteria.setCodeOrDescriptionLike(codeOrDescription);
		criteria.setPricingScheme(pricingScheme);
		
		List<Product> products = productService.searchProducts(criteria);
		productsTableModel.setProducts(products);
		
		if (!products.isEmpty()) {
			int selectedRow = 0;
			if (!StringUtils.isEmpty(currentlySelectedCode)) {
				int i = 0;
				for (Product product : products) {
					if (product.getCode().equals(currentlySelectedCode)) {
						selectedRow = i;
						break;
					}
					i++;
				}
			}
			productsTable.changeSelection(selectedRow, 0, false, false);
		}
		
		((CardLayout)infoTablePanel.getLayout()).show(infoTablePanel, UNIT_PRICE_INFO_TABLE);
	}

	public void searchProducts(String codeOrDescription, String currentlySelectedCode, Supplier supplier) {
		ProductSearchCriteria criteria = new ProductSearchCriteria();
		criteria.setActive(true);
		criteria.setCodeOrDescriptionLike(codeOrDescription);
		criteria.setSupplier(supplier);
		
		List<Product> products = productService.searchProducts(criteria);
		productsTableModel.setProducts(products);
		
		if (!products.isEmpty()) {
			int selectedRow = 0;
			if (!StringUtils.isEmpty(currentlySelectedCode)) {
				int i = 0;
				for (Product product : products) {
					if (product.getCode().equals(currentlySelectedCode)) {
						selectedRow = i;
						break;
					}
					i++;
				}
			}
			productsTable.changeSelection(selectedRow, 0, false, false);
		}
		
		((CardLayout)infoTablePanel.getLayout()).show(infoTablePanel, UNIT_COST_INFO_TABLE);
	}

	public void searchProducts(ProductSearchCriteria criteria) {
		List<Product> products = productService.searchProducts(criteria);
		productsTableModel.setProducts(products);
		if (!products.isEmpty()) {
			productsTable.selectFirstRow();
		}
		((CardLayout)infoTablePanel.getLayout()).show(infoTablePanel, UNIT_PRICE_INFO_TABLE);
	}
	
	private class UnitPricesAndQuantitiesTableModel extends AbstractTableModel {

		private static final int UNIT_COLUMN_INDEX = 0;
		private static final int QUANTITY_COLUMN_INDEX = 1;
		private static final int UNIT_PRICE_COLUMN_INDEX = 2;
		
		private final String[] columnNames = {"Unit", "Quantity", "Price"};
		private final String[] units = {Unit.CASE, Unit.TIES, Unit.PACK, Unit.HDZN, Unit.PIECES};
		
		private Product product;
		
		public void setProduct(Product product) {
			this.product = product;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return 5;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			String unit = units[rowIndex];

			switch (columnIndex) {
			case UNIT_COLUMN_INDEX:
				return unit;
			case QUANTITY_COLUMN_INDEX:
				return (product != null) ? product.getUnitQuantity(unit) : 0;
			case UNIT_PRICE_COLUMN_INDEX:
				if (product != null && product.hasUnit(unit)) {
					return FormatterUtil.formatAmount(product.getUnitPrice(unit));
				} else {
					return "0.00";
				}
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == UNIT_PRICE_COLUMN_INDEX) {
				return Number.class;
			} else {
				return Object.class;
			}
		}
	
	}
	
	private class UnitCostsAndQuantitiesTableModel extends AbstractTableModel {

		private static final int UNIT_COLUMN_INDEX = 0;
		private static final int QUANTITY_COLUMN_INDEX = 1;
		private static final int UNIT_COST_COLUMN_INDEX = 2;
		private final String[] columnNames = {"Unit", "Quantity", "Cost"};
		private final String[] units = {Unit.CASE, Unit.TIES, Unit.PACK, Unit.HDZN, Unit.PIECES};
		
		private Product product;
		
		public void setProduct(Product product) {
			this.product = product;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return 5;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			String unit = units[rowIndex];
			
			switch (columnIndex) {
			case UNIT_COLUMN_INDEX:
				return unit;
			case QUANTITY_COLUMN_INDEX:
				return (product != null) ? product.getUnitQuantity(unit) : "0";
			case UNIT_COST_COLUMN_INDEX:
				if (product != null && product.hasUnit(unit)) {
					return FormatterUtil.formatAmount(product.getGrossCost(unit));
				} else {
					return "0.00";
				}
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == UNIT_COST_COLUMN_INDEX) {
				return Number.class;
			} else {
				return Object.class;
			}
		}
		
	}
	
}