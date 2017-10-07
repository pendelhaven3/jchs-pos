package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.table.TableColumnModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.ExcelFileFilter;
import com.pj.magic.gui.component.MagicFileChooser;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FileUtil;

@Component
public class UploadMaximumStockLevelChangesPanel extends StandardMagicPanel {

	private static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	private static final int UNIT_COLUMN_INDEX = 2;
	private static final int NEW_MAXIMUM_STOCK_LEVEL_COLUMN_INDEX = 3;
	
	private static final Logger logger = LoggerFactory.getLogger(UploadMaximumStockLevelChangesPanel.class);
	
	@Autowired private ProductService productService;
	
	private MagicListTable table;
	private ProductsTableModel tableModel;
	private JButton uploadButton;
	private JButton saveChangesButton;

	@Override
	protected void initializeComponents() {
		initializeTable();

		uploadButton = new JButton("Upload File");
		uploadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				uploadFile();
			}
		});
		
		saveChangesButton = new JButton("Save Changes");
		saveChangesButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveChanges();
			}
		});
	}

	private void initializeTable() {
		tableModel = new ProductsTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(NEW_MAXIMUM_STOCK_LEVEL_COLUMN_INDEX).setPreferredWidth(100);
	}

	private void uploadFile() {
		MagicFileChooser fileChooser = createFileChooser();
		if (!fileChooser.selectFileToOpen(this)) {
			return;
		}
		
		List<ProductEntry> entries = null;
		try {
			entries = new ExcelFileReader().read(fileChooser.getSelectedFile());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			showMessageForUnexpectedError();
			return;
		}
		
		tableModel.setItems(entries);
		if (!entries.isEmpty()) {
			table.selectFirstRow();
			saveChangesButton.setEnabled(true);
		}
	}

	private MagicFileChooser createFileChooser() {
		MagicFileChooser fileChooser = new MagicFileChooser();
		fileChooser.setFileFilter(ExcelFileFilter.getInstance());
		fileChooser.setCurrentDirectory(FileUtil.getDesktopFolderPathAsFile());
		return fileChooser;
	}

	private void saveChanges() {
		if (tableModel.getItems().isEmpty()) {
			showErrorMessage("Nothing to save");
			return;
		}
		
		if (!confirm("Save changes?")) {
			return;
		}
		
		List<Product> products = new ArrayList<>();
		for (ProductEntry entry : tableModel.getItems()) {
			Product product = new Product();
			product.setId(entry.getProduct().getId());
			product.setMaximumStockLevel(entry.getNewMaximumStockLevel());
			products.add(product);
		}
		
		try {
			productService.updateMaximumStockLevel(products);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			showMessageForUnexpectedError();
			return;
		}
		
		showMessage("Changes saved");
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());

		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.insets.top = 10;
		c.insets.bottom = 10;
		mainPanel.add(ComponentUtil.createGenericPanel(
				uploadButton, Box.createHorizontalStrut(5), saveChangesButton), c);

		currentRow++;

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createScrollPane(table), c);
	}

	@Override
	protected void registerKeyBindings() {
	}

	public void updateDisplay() {
		tableModel.clear();
		saveChangesButton.setEnabled(false);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToInventoryMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

	private class ExcelFileReader {
		
		public List<ProductEntry> read(File file) throws IOException {
			try (
				Workbook workbook = new XSSFWorkbook(new FileInputStream(file));
			) {
				int codeColumn = getCodeColumn(workbook);
				if (codeColumn == -1) {
					showErrorMessage("Column CODE not found");
					return null;
				}
				
				int unitColumn = getUnitColumn(workbook);
				if (unitColumn == -1) {
					showErrorMessage("Column UNIT not found");
					return null;
				}
				
				int newMaximumStockLevelColumn = getNewMaximumStockLevelColumn(workbook);
				if (newMaximumStockLevelColumn == -1) {
					showErrorMessage("Column NEW MAXIMUM STOCK LEVEL not found");
					return null;
				}
				
				List<ProductEntry> entries = new ArrayList<>();
				
				Iterator<Row> rows = workbook.getSheetAt(0).iterator();
				Row row = rows.next();
				
				while (rows.hasNext()) {
					row = rows.next();
					entries.add(new ProductEntry(
							getProduct(row.getCell(codeColumn)),
							row.getCell(unitColumn).getStringCellValue(),
							(int)row.getCell(newMaximumStockLevelColumn).getNumericCellValue()));
				}
				
				return entries;
			}
		}

		private int getCodeColumn(Workbook workbook) {
			Row row = workbook.getSheetAt(0).getRow(0);
			for (int i = 0; i < 10; i++) {
				if ("CODE".equals(row.getCell(i).getStringCellValue())) {
					return i;
				}
			}
			return -1;
		}

		private int getUnitColumn(Workbook workbook) {
			Row row = workbook.getSheetAt(0).getRow(0);
			for (int i = 0; i < 10; i++) {
				if (row.getCell(i) != null && "UNIT".equals(row.getCell(i).getStringCellValue())) {
					return i;
				}
			}
			return -1;
		}

		private int getNewMaximumStockLevelColumn(Workbook workbook) {
			Row row = workbook.getSheetAt(0).getRow(0);
			for (int i = 0; i < 10; i++) {
				if (row.getCell(i) != null && "NEW MAXIMUM STOCK LEVEL".equals(row.getCell(i).getStringCellValue())) {
					return i;
				}
			}
			return -1;
		}
		
		private Product getProduct(Cell cell) {
			return productService.findProductByCode(cell.getStringCellValue());
		}
		
	}
	
	private class ProductsTableModel extends ListBackedTableModel<ProductEntry> {

		private final String[] columnNames = {"Code", "Description", "Unit", "New Maximum Stock Level"};

		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ProductEntry item = getItem(rowIndex);
			switch (columnIndex) {
			case PRODUCT_CODE_COLUMN_INDEX:
				return item.getProduct().getCode();
			case PRODUCT_DESCRIPTION_COLUMN_INDEX:
				return item.getProduct().getDescription();
			case UNIT_COLUMN_INDEX:
				return item.getUnit();
			case NEW_MAXIMUM_STOCK_LEVEL_COLUMN_INDEX:
				return item.getNewMaximumStockLevel();
			default:
				return null;
			}
		}

		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == NEW_MAXIMUM_STOCK_LEVEL_COLUMN_INDEX) {
				return Number.class;
			} else {
				return Object.class;
			}
		}

	}

	private class ProductEntry {

		private Product product;
		private String unit;
		private int newMaximumStockLevel;

		public ProductEntry(Product product, String unit, int newMaximumStockLevel) {
			this.product = product;
			this.unit = unit;
			this.newMaximumStockLevel = newMaximumStockLevel;
		}

		public Product getProduct() {
			return product;
		}

		public String getUnit() {
			return unit;
		}

		public int getNewMaximumStockLevel() {
			return newMaximumStockLevel;
		}

	}
}