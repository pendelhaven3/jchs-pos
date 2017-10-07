package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.SelectProductEllipsisButton;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.model.report.PilferageReport;
import com.pj.magic.model.report.PilferageReportItem;
import com.pj.magic.model.search.PilferageReportCriteria;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.ReportService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class PilferageReportPanel extends StandardMagicPanel {

	private static final int DATE_COLUMN_INDEX = 0;
	private static final int TRANSACTION_TYPE_COLUMN_INDEX = 1;
	private static final int TRANSACTION_NUMBER_COLUMN_INDEX = 2;
	private static final int PRODUCT_CODE_COLUMN_INDEX = 3;
	private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 4;
	private static final int UNIT_COLUMN_INDEX = 5;
	private static final int QUANTITY_COLUMN_INDEX = 6;
	private static final int COST_COLUMN_INDEX = 7;
	private static final int AMOUNT_COLUMN_INDEX = 8;
	
	@Autowired private ProductService productService;
	@Autowired private ReportService reportService;
	@Autowired private SelectProductDialog selectProductDialog;
	
	private UtilCalendarModel fromDateModel;
	private UtilCalendarModel toDateModel;
	private MagicTextField productCodeField;
	private JLabel productDescriptionLabel;
	private SelectProductEllipsisButton selectProductButton;
	private JButton generateButton;
	private MagicListTable table;
	private PilferageReportItemsTableModel tableModel;
	private JLabel totalAmountLabel;
	
	@Override
	protected void initializeComponents() {
		fromDateModel = new UtilCalendarModel();
		toDateModel = new UtilCalendarModel();
		
		productCodeField = new MagicTextField();
		productCodeField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);

		productDescriptionLabel = new JLabel();
		
		selectProductButton = new SelectProductEllipsisButton(selectProductDialog, productCodeField, productDescriptionLabel);
		
		generateButton = new JButton("Generate");
		generateButton.addActionListener(e -> generateReport());
		
		initializeTable();
	}

	private void initializeTable() {
		tableModel = new PilferageReportItemsTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(DATE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(TRANSACTION_TYPE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(TRANSACTION_NUMBER_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(60);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(COST_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setPreferredWidth(100);
	}

	private void generateReport() {
		if (fromDateModel.getValue() == null) {
			showErrorMessage("From Date must be specified");
			return;
		}
		
		PilferageReportCriteria criteria = new PilferageReportCriteria();
		criteria.setFrom(fromDateModel.getValue().getTime());
		if (toDateModel.getValue() != null) {
			criteria.setTo(toDateModel.getValue().getTime());
		}
		if (!productCodeField.isEmpty()) {
			Product product = productService.findProductByCode(productCodeField.getText());
			if (product == null) {
				productDescriptionLabel.setText(null);
			} else {
				productDescriptionLabel.setText(product.getDescription());
				criteria.setProduct(product);
			}
		}
		
		PilferageReport report = reportService.getPilferageReport(criteria);
		tableModel.setItems(report.getItems());
		if (report.getItems().isEmpty()) {
			showErrorMessage("No records found");
		}
		totalAmountLabel.setText(FormatterUtil.formatAmount(report.getTotalAmount()));
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
		mainPanel.add(ComponentUtil.createLabel(100, "Date From: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createDatePicker(fromDateModel), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Date To: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createDatePicker(toDateModel), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0; // right space filler
		c.gridx = 6;
		c.gridy = currentRow;
		mainPanel.add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Product:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 5;
		mainPanel.add(selectProductButton.getFieldsPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
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
		mainPanel.add(Box.createVerticalStrut(30), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 7;
		mainPanel.add(ComponentUtil.createScrollPane(table, 600, 100), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		c.gridwidth = 7;
		mainPanel.add(createTotalsPanel(), c);
	}

	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountLabel = ComponentUtil.createRightLabel(100, "");
		panel.add(totalAmountLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(Box.createHorizontalStrut(10), c);
		
		return panel;
	}

	@Override
	protected void registerKeyBindings() {
		// none
	}

	public void updateDisplay() {
		fromDateModel.setValue(null);
		toDateModel.setValue(null);
		productCodeField.setText(null);
		productDescriptionLabel.setText(null);
		tableModel.clear();
		totalAmountLabel.setText(null);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToReportsMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

	private class PilferageReportItemsTableModel extends ListBackedTableModel<PilferageReportItem> {

		private final String[] columnNames = 
			{"Date", "Tran Type", "Tran No", "Product Code", "Product Description", "Unit", "Quantity", "Cost", "Amount"};
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PilferageReportItem item = getItem(rowIndex);
			switch (columnIndex) {
			case DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(item.getDate());
			case TRANSACTION_TYPE_COLUMN_INDEX:
				return item.getTransactionType();
			case TRANSACTION_NUMBER_COLUMN_INDEX:
				return item.getTransactionNumber();
			case PRODUCT_CODE_COLUMN_INDEX:
				return item.getProduct().getCode();
			case PRODUCT_DESCRIPTION_COLUMN_INDEX:
				return item.getProduct().getDescription();
			case UNIT_COLUMN_INDEX:
				return item.getUnit();
			case QUANTITY_COLUMN_INDEX:
				return item.getQuantity();
			case COST_COLUMN_INDEX:
				return FormatterUtil.formatAmount(item.getCost());
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(item.getAmount());
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case COST_COLUMN_INDEX:
			case AMOUNT_COLUMN_INDEX:
			case QUANTITY_COLUMN_INDEX:
				return Number.class;
			default:
				return String.class;
			}
		}

		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}
		
	}
	
}