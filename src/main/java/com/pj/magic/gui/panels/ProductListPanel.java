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
import java.util.Arrays;
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
import com.pj.magic.model.Unit;
import com.pj.magic.model.UnitConversion;
import com.pj.magic.model.search.ProductSearchCriteria;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;

import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;

@Component
public class ProductListPanel extends StandardMagicPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductListPanel.class);
    
    private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 0;
    
	@Autowired private ProductService productService;
	@Autowired private SearchProductsDialog searchProductsDialog;
	
	private MagicListTable table;
	private ProductsTableModel tableModel = new ProductsTableModel();
	private MagicFileChooser fileChooser = new MagicFileChooser();
	
	public void updateDisplay() {
		List<Product> products = productService.getAllActiveProducts();
		tableModel.setItems(products);
		if (!products.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
		searchProductsDialog.updateDisplay();
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
		tableModel.setItems(productService.getAllActiveProducts());
		table.changeSelection(0, 0, false, false);
		table.requestFocusInWindow();
		searchProductsDialog.updateDisplay();
	}

	private List<String> VALID_UNITS = Arrays.asList(Unit.values());
	
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
        
        List<String> activeProductCodes = productService.getAllActiveProductCodes();
        
        try (
            CSVReader reader = new CSVReaderBuilder(new StringReader(csvString)).withSkipLines(1).build();
        ) {
            String[] nextLine = null;
            while ((nextLine = reader.readNext()) != null) {
            	LOGGER.info("updating product code " + nextLine[0]);
            	
                String unit1 = nextLine[8];
                String unit2 = nextLine[9];
                if ("null".equals(unit2)) {
                    unit2 = null;
                }

                if (!VALID_UNITS.contains(unit1)) { // TODO: Remove
                	System.out.println("rejected1: " + nextLine[0]);
                	continue;
                }
                
                if (!StringUtils.isEmpty(unit2) && !VALID_UNITS.contains(unit2)) {  // TODO: Remove
                	System.out.println("rejected2: " + nextLine[0]);
                	continue;
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
                if (!StringUtils.isEmpty(nextLine[9]) && !"null".equals(nextLine[9])) {
                    product.getUnitConversions().add(new UnitConversion(unit2, unitConversion2));
                }
                
                productService.updateProduct(product);
                activeProductCodes.remove(product.getCode());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            showErrorMessage("Unexpected error occurred");
            return;
        }
        
        LOGGER.info("Updating {} products as inactive", activeProductCodes.size());
        productService.updateProductsAsInactive(activeProductCodes);
        
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

        private final String[] columnNames = {"Description"};
	    
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Product product = getItem(rowIndex);
            switch (columnIndex) {
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
