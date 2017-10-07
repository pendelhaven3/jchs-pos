package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.StockQuantityConversionItem;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.StockQuantityConversionService;
import com.pj.magic.util.ComponentUtil;

@Component
public class StockQuantityConversionDialog extends MagicDialog {

	private static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	
	@Autowired private StockQuantityConversionService stockQuantityConversionService;
	@Autowired private PrintService printService;
	
	private MagicListTable table;
	private StockQuantityConversionItemsTableModel tableModel;
	private StockQuantityConversion conversion;
	private JLabel stockQuantityConversionNumberLabel;
	private MagicTextField remarksField;
	private JButton printButton;
	private JButton assignPageNumberButton;
	
	public StockQuantityConversionDialog() {
		setSize(600, 350);
		setLocationRelativeTo(null);
		setTitle("Stock Quantity Conversion");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		stockQuantityConversionNumberLabel = new JLabel();
		remarksField = new MagicTextField();
		
		printButton = new JButton("Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printService.print(conversion);
			}
		});
		
		assignPageNumberButton = new JButton("Assign Page Number");
		assignPageNumberButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				assignPageNumber();
			}
		});
		
		initializeTable();
	}

	private void initializeTable() {
		tableModel = new StockQuantityConversionItemsTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(400);
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
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "SQC No."), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(stockQuantityConversionNumberLabel, c);
		
		currentRow++;

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Remarks"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(200, 25));
		add(ComponentUtil.createGenericPanel(
				remarksField, Box.createHorizontalStrut(5), assignPageNumberButton), c);
		
		currentRow++;

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createVerticalStrut(10), c);

		currentRow++;

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		add(printButton, c);
		
		currentRow++;

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH;
		add(ComponentUtil.createScrollPane(table, 400, 100), c);
	}
	
	public void updateDisplay(SalesRequisition salesRequisition) {
		conversion = stockQuantityConversionService.getStockQuantityConversion(
				salesRequisition.getStockQuantityConversion().getId());
		
		stockQuantityConversionNumberLabel.setText(conversion.getStockQuantityConversionNumber().toString());
		remarksField.setText(conversion.getRemarks());
		
		List<StockQuantityConversionItem> items = conversion.getItems();
		tableModel.setItems(items);
		if (!items.isEmpty()) {
			table.selectFirstRow();
		}
	}

	private void assignPageNumber() {
		String pageNumber = "P" + stockQuantityConversionService.getNextPageNumber();
		remarksField.setText(pageNumber);
		conversion.setRemarks(pageNumber);
		stockQuantityConversionService.save(conversion);
		showMessage("Saved");
	}
	
	private class StockQuantityConversionItemsTableModel extends ListBackedTableModel<StockQuantityConversionItem> {
		
		private final String[] columnNames = {"Product Code", "Product Description"};

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			StockQuantityConversionItem item = getItem(rowIndex);
			switch (columnIndex) {
			case PRODUCT_CODE_COLUMN_INDEX:
				return item.getProduct().getCode();
			case PRODUCT_DESCRIPTION_COLUMN_INDEX:
				return item.getProduct().getDescription();
			default:
				throw new RuntimeException("Error fetching invalid column index: " + columnIndex);
			}
		}

		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}
		
	}

}