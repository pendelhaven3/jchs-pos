package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.SalesRequisitionItemNotEnoughStocksException;
import com.pj.magic.exception.SalesRequisitionItemPostException;
import com.pj.magic.exception.SalesRequisitionPostException;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.StockQuantityConversionItem;
import com.pj.magic.service.StockQuantityConversionService;

@Component
public class SalesRequisitionPostExceptionsDialog extends MagicDialog {

	private static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	private static final int UNIT_COLUMN_INDEX = 2;
	private static final int QUANTITY_COLUMN_INDEX = 3;
	private static final int ERROR_MESSAGE_COLUMN_INDEX = 4;
	
	@Autowired private StockQuantityConversionService stockQuantityConversionService;
	
	private ExceptionsTableModel tableModel;
	private MagicListTable table;
	private JButton createConversionButton;
	private SalesRequisitionPostException exception;
	private StockQuantityConversion stockQuantityConversion;
	
	public SalesRequisitionPostExceptionsDialog() {
		setSize(800, 300);
		setLocationRelativeTo(null);
		setTitle("Post Validation Errors");
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		layoutComponents();
		registerKeyBindings();
	}

	private void initializeComponents() {
		tableModel = new ExceptionsTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(200);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(ERROR_MESSAGE_COLUMN_INDEX).setPreferredWidth(200);
		
		createConversionButton = new JButton("Create Stock Quantity Conversion");
		createConversionButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				createStockQuantityConversion();
			}
		});
	}

	private void createStockQuantityConversion() {
		StockQuantityConversion stockQuantityConversion = new StockQuantityConversion();
		for (SalesRequisitionItemPostException e : exception.getExceptions()) {
			if (e instanceof SalesRequisitionItemNotEnoughStocksException) {
				SalesRequisitionItem item = e.getItem();
				if (!item.getUnit().equals(item.getProduct().getMaxUnit())) {
					StockQuantityConversionItem conversionItem = new StockQuantityConversionItem();
					conversionItem.setParent(stockQuantityConversion);
					conversionItem.setProduct(item.getProduct());
					conversionItem.setFromUnit(item.getProduct().getMaxUnit());
					conversionItem.setQuantity(1);
					conversionItem.setToUnit(item.getUnit());
					stockQuantityConversion.getItems().add(conversionItem);
				}
			}
		}
		if (stockQuantityConversion.getItems().isEmpty()) {
			showErrorMessage("No conversion requirement detected");
		} else {
			stockQuantityConversionService.save(stockQuantityConversion);
			for (StockQuantityConversionItem item : stockQuantityConversion.getItems()) {
				stockQuantityConversionService.save(item);
			}
			this.stockQuantityConversion = stockQuantityConversion;
		}
		setVisible(false);
	}

	private void registerKeyBindings() {
		// none
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		
		JScrollPane productsScrollPane = new JScrollPane(table);
		productsScrollPane.setPreferredSize(new Dimension(400, 100));
		add(productsScrollPane, c);
		
		currentRow++;

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(createConversionButton, c);
		
		currentRow++;

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(Box.createVerticalStrut(10), c);
	}
	
	public void updateDisplay(SalesRequisitionPostException exception) {
		this.exception = exception;
		tableModel.setExceptions(exception.getExceptions());
		stockQuantityConversion = null;
	}
	
	private class ExceptionsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Code", "Description", "Unit", "Quantity", "Error"};
		
		private List<SalesRequisitionItemPostException> exceptions = new ArrayList<>();
		
		public void setExceptions(List<SalesRequisitionItemPostException> exceptions) {
			this.exceptions = exceptions;
		}
		
		@Override
		public int getRowCount() {
			return exceptions.size();
		}

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
			SalesRequisitionItemPostException exception = exceptions.get(rowIndex);
			switch (columnIndex) {
			case PRODUCT_CODE_COLUMN_INDEX:
				return exception.getItem().getProduct().getCode();
			case PRODUCT_DESCRIPTION_COLUMN_INDEX:
				return exception.getItem().getProduct().getDescription();
			case UNIT_COLUMN_INDEX:
				return exception.getItem().getUnit();
			case QUANTITY_COLUMN_INDEX:
				return exception.getItem().getQuantity();
			case ERROR_MESSAGE_COLUMN_INDEX:
				return exception.getErrorMessage();
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}

	}
	
	public StockQuantityConversion getStockQuantityConversion() {
		return stockQuantityConversion;
	}
	
}