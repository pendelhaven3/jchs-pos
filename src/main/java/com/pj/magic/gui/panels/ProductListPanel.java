package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.pj.magic.gui.component.MagicFileChooser;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchProductsDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.model.UnitConversion;
import com.pj.magic.model.search.ProductSearchCriteria;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;

import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;

@Component
@SuppressWarnings("serial")
public class ProductListPanel extends StandardMagicPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductListPanel.class);
    
    private static final int PRODUCT_CODE_COLUMN_INDEX = 0;
    private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
    private static final int UOM_1_COLUMN_INDEX = 2;
    private static final int UOM_2_COLUMN_INDEX = 3;
    private static final int UOM_1_QUANTITY_COLUMN_INDEX = 4;
    private static final int UOM_2_QUANTITY_COLUMN_INDEX = 5;
    
	@Autowired private ProductService productService;
	@Autowired private SearchProductsDialog searchProductsDialog;
	
	private MagicListTable table;
	private ProductsTableModel tableModel = new ProductsTableModel();
	private MagicFileChooser fileChooser = new MagicFileChooser();
	
	public void updateDisplay() {
		List<Product> products = productService.getAllProducts();
		tableModel.setItems(products);
		if (!products.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
//		searchProductsDialog.updateDisplay();
	}

	@Override
	protected void initializeComponents() {
		table = new MagicListTable(tableModel);
        table.getColumnModel().getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(100);
        table.getColumnModel().getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
        table.getColumnModel().getColumn(UOM_1_COLUMN_INDEX).setPreferredWidth(50);
        table.getColumnModel().getColumn(UOM_2_COLUMN_INDEX).setPreferredWidth(50);
        table.getColumnModel().getColumn(UOM_1_QUANTITY_COLUMN_INDEX).setPreferredWidth(50);
        table.getColumnModel().getColumn(UOM_2_QUANTITY_COLUMN_INDEX).setPreferredWidth(50);
        
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
		searchProductsDialog.setVisible(true);
		
		ProductSearchCriteria criteria = searchProductsDialog.getSearchCriteria();
		if (criteria != null) {
			List<Product> products = productService.searchProducts(criteria);
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
	}

	protected void selectProduct() {
		Product product = tableModel.getItem(table.getSelectedRow());
		getMagicFrame().switchToEditProductPanel(product);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
        JButton addButton = new MagicToolBarButton("up", "Update Products from DBF");
        addButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProductsFromDbf();
            }

        });
        toolBar.add(addButton);
        
        JButton showAllButton = new MagicToolBarButton("all", "Show All");
        showAllButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllProducts();
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

	private void showAllProducts() {
		tableModel.setItems(productService.getAllProducts());
		table.changeSelection(0, 0, false, false);
		table.requestFocusInWindow();
		searchProductsDialog.updateDisplay();
	}
	
    private void updateProductsFromDbf() {
        fileChooser.setFileFilter(new FileFilter() {
            
            @Override
            public String getDescription() {
                return "Product DBF file (Product.DBF)";
            }
            
            @Override
            public boolean accept(File f) {
                return f.getName().equalsIgnoreCase("Product.DBF");
            }
        });
        
        if (!fileChooser.selectFileToOpen(this)) {
            return;
        }
        
        String csvString = null;
        try {
            csvString = convertDbfToCsv(fileChooser.getSelectedFile());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            showErrorMessage("Unexpected error occurred");
            return;
        }
        
        try (
            CSVReader reader = new CSVReaderBuilder(new StringReader(csvString)).withSkipLines(1).build();
        ) {
            String[] nextLine = null;
            while ((nextLine = reader.readNext()) != null) {
                String unit1 = nextLine[8];
                String unit2 = nextLine[9];
                if ("null".equals(unit2)) {
                    unit2 = null;
                }
                
                int unitConversion1 = (int)Float.parseFloat(nextLine[13]);
                int unitConversion2 = 0;
                if (!StringUtils.isEmpty(nextLine[14]) && !"null".equals(nextLine[14])) {
                    unitConversion2 = (int)Float.parseFloat(nextLine[14]);
                }
                
                Product product = new Product();
                product.setCode(nextLine[0]);
                product.setDescription(nextLine[3]);
                product.getUnits().add(unit1);
                if (!StringUtils.isEmpty(unit2)) {
                    product.getUnits().add(unit2);
                }
                product.getUnitConversions().add(new UnitConversion(unit1, unitConversion1));
                if (!StringUtils.isEmpty(nextLine[14])) {
                    product.getUnitConversions().add(new UnitConversion(unit2, unitConversion2));
                }
                
                productService.updateProduct(product);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            showErrorMessage("Unexpected error occurred");
            return;
        }
        
        showMessage("Product list updated");
        updateDisplay();
    }
	
	private static String convertDbfToCsv(File file) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    
        DbfRecord rec;
        try (
            DbfReader reader = new DbfReader(file);
        ) {
            DbfMetadata meta = reader.getMetadata();
            List<String> fields = meta.getFields().stream().map(f -> f.getName()).collect(Collectors.toList());
            sb.append(fields.stream().collect(Collectors.joining(",")));
            sb.append("\n");

            while ((rec = reader.read()) != null) {
                rec.setStringCharset(StandardCharsets.UTF_8);

                StringJoiner sj = new StringJoiner(",");
                for (String s : fields) {
                    String value = rec.getString(s);
                    
                    if (value != null && value.contains("\"")) {
                        value = value.replaceAll("\"", "\"\"");
                        value = "\"" + value + "\"";
                    }
                    if (value != null && value.contains(",")) {
                        value = "\"" + value + "\"";
                    }
                    sj.add(value);
                }

                sb.append(sj.toString());
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    private class ProductsTableModel extends ListBackedTableModel<Product>{

        private static final long serialVersionUID = -5447604347973015681L;
        
        private final String[] columnNames = {"Code", "Description", "UOM 1", "UOM 2", "UOM Qty 1", "UOM Qty 2"};
	    
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Product product = getItem(rowIndex);
            switch (columnIndex) {
            case PRODUCT_CODE_COLUMN_INDEX:
                return product.getCode();
            case PRODUCT_DESCRIPTION_COLUMN_INDEX:
                return product.getDescription();
            case UOM_1_COLUMN_INDEX:
                return product.getUnits().get(0);
            case UOM_2_COLUMN_INDEX:
                return product.getUnits().size() > 1 ? product.getUnits().get(1) : null;
            case UOM_1_QUANTITY_COLUMN_INDEX:
                return product.getUnitConversions().get(0).getQuantity();
            case UOM_2_QUANTITY_COLUMN_INDEX:
                return product.getUnitConversions().size() > 1 ? product.getUnitConversions().get(1).getQuantity() : null;
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
