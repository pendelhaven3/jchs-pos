package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.tables.AreaInventoryReportItemsTable;
import com.pj.magic.gui.tables.ProductInfoTable;
import com.pj.magic.gui.tables.SalesRequisitionItemsTable;
import com.pj.magic.model.Area;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.Product;
import com.pj.magic.service.AreaInventoryReportService;
import com.pj.magic.service.AreaService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class AreaInventoryReportPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(AreaInventoryReportPanel.class);
	
	@Autowired private AreaInventoryReportItemsTable itemsTable;
	@Autowired private AreaInventoryReportService areaInventoryReportService;
	@Autowired private AreaService areaService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	@Autowired private ProductService productService;
	
	private AreaInventoryReport areaInventoryReport;
	private JLabel inventoryDateField;
	private JLabel encoderLabel;
	private JLabel statusLabel;
	private MagicTextField reportNumberField;
	private JComboBox<Area> areaComboBox;
	private MagicTextField checkerField;
	private MagicTextField doubleCheckerField;
	private MagicTextField reviewerField;
	private JLabel totalItemsField;
	private JButton addItemButton;
	private JButton deleteItemButton;
	private JButton markAsReviewedButton;
	private ProductInfoTable productInfoTable;
	
	@Override
	protected void initializeComponents() {
		reportNumberField = new MagicTextField();
		reportNumberField.setMaximumLength(2);
		reportNumberField.setNumbersOnly(true);
		reportNumberField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveReportNumberField();
			}
		});
		
		areaComboBox = new JComboBox<>();
		areaComboBox.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveArea();
			}
		});
		
		checkerField = new MagicTextField();
		checkerField.setMaximumLength(50);
		checkerField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveCheckerField();
			}
		});
		
		doubleCheckerField = new MagicTextField();
		doubleCheckerField.setMaximumLength(50);
		doubleCheckerField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveDoubleChecker();
			}
		});
		
		reviewerField = new MagicTextField();
		reviewerField.setMaximumLength(50);
		reviewerField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveReviewer();
			}
		});
		
		updateTotalAmountFieldWhenItemsTableChanges();
		initializeUnitPricesAndQuantitiesTable();
		focusOnComponentWhenThisPanelIsDisplayed(reportNumberField);
	}

	protected void saveArea() {
		Area selectedArea = (Area)areaComboBox.getSelectedItem();
		if ((selectedArea != null && !selectedArea.equals(areaInventoryReport.getArea()))
				|| (selectedArea == null && areaInventoryReport.getArea() != null))  {
			areaInventoryReport.setArea(selectedArea);
			areaInventoryReportService.save(areaInventoryReport);
		}
	}

	private void updateTotalAmountFieldWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalItemsField.setText(String.valueOf(areaInventoryReport.getTotalNumberOfItems()));
			}
		});
	}

	private void saveReportNumberField() {
		if (StringUtils.isEmpty(reportNumberField.getText())) {
			showErrorMessage("Report No. must be specified");
			reportNumberField.requestFocusInWindow();
			return;
		}
		
		int reportNumber = Integer.parseInt(reportNumberField.getText());
		AreaInventoryReport existing = areaInventoryReportService
				.findByInventoryCheckAndReportNumber(areaInventoryReport.getParent(), reportNumber);
		if (existing != null && 
				(areaInventoryReport.getId() == null || !areaInventoryReport.getId().equals(existing.getId()))) {
			showErrorMessage("Report No. is already used by another record");
			reportNumberField.requestFocusInWindow();
			return;
		}
		
		if (areaInventoryReport.getId() == null || reportNumber != areaInventoryReport.getReportNumber()) {
			areaInventoryReport.setReportNumber(reportNumber);
			areaInventoryReportService.save(areaInventoryReport);
			updateDisplay(areaInventoryReport);
		}
	}

	private void saveReviewer() {
		if (!reviewerField.getText().equals(areaInventoryReport.getReviewer())) {
			areaInventoryReport.setReviewer(reviewerField.getText());
			areaInventoryReportService.save(areaInventoryReport);
		}
	}

	private void saveDoubleChecker() {
		if (!doubleCheckerField.getText().equals(areaInventoryReport.getDoubleChecker())) {
			areaInventoryReport.setDoubleChecker(doubleCheckerField.getText());
			areaInventoryReportService.save(areaInventoryReport);
		}
	}

	private void saveCheckerField() {
		if (!checkerField.getText().equals(areaInventoryReport.getChecker())) {
			areaInventoryReport.setChecker(checkerField.getText());
			areaInventoryReportService.save(areaInventoryReport);
		}
	}

	@Override
	protected void registerKeyBindings() {
		setFocusOnNextFieldOnEnterKey(reportNumberField);
		setFocusOnNextFieldOnEnterKey(areaComboBox);
		setFocusOnNextFieldOnEnterKey(checkerField);
		setFocusOnNextFieldOnEnterKey(doubleCheckerField);
		
		reviewerField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.highlight();
			}
		});
	}

	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToAreaInventoryReportListPanel();
	}
	
	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(reportNumberField);
		focusOrder.add(areaComboBox);
		focusOrder.add(checkerField);
		focusOrder.add(doubleCheckerField);
		focusOrder.add(reviewerField);
	}
	
	public void updateDisplay(AreaInventoryReport areaInventoryReport) {
		inventoryDateField.setText(FormatterUtil.formatDate(areaInventoryReport.getParent().getInventoryDate()));
		statusLabel.setText(areaInventoryReport.getStatus());
		
		if (areaInventoryReport.getId() == null) {
			this.areaInventoryReport = areaInventoryReport;
			clearDisplay();
			return;
		}
		
		this.areaInventoryReport = areaInventoryReport 
				= areaInventoryReportService.getAreaInventoryReport(areaInventoryReport.getId());
		
		updateComboBoxes();
		
		boolean editable = !(areaInventoryReport.getParent().isPosted() || areaInventoryReport.isReviewed());
		
		encoderLabel.setText(areaInventoryReport.getCreatedBy().getUsername());
		reportNumberField.setText(areaInventoryReport.getReportNumber().toString());
		reportNumberField.setEnabled(editable);
		areaComboBox.setEnabled(editable);
		areaComboBox.setSelectedItem(areaInventoryReport.getArea());
		checkerField.setEnabled(editable);
		checkerField.setText(areaInventoryReport.getChecker());
		doubleCheckerField.setEnabled(editable);
		doubleCheckerField.setText(areaInventoryReport.getDoubleChecker());
		reviewerField.setEnabled(editable);
		reviewerField.setText(areaInventoryReport.getReviewer());
		totalItemsField.setText(String.valueOf(areaInventoryReport.getTotalNumberOfItems()));
		
		itemsTable.setAreaInventoryReport(areaInventoryReport);
		
		addItemButton.setEnabled(editable);
		deleteItemButton.setEnabled(editable);
		markAsReviewedButton.setEnabled(editable);
	}

	private void clearDisplay() {
		encoderLabel.setText(null);
		reportNumberField.setText(null);
		reportNumberField.setEnabled(true);
		areaComboBox.setEnabled(false);
		areaComboBox.setSelectedItem(null);
		checkerField.setEnabled(false);
		checkerField.setText(null);
		doubleCheckerField.setEnabled(false);
		doubleCheckerField.setText(null);
		reviewerField.setEnabled(false);
		reviewerField.setText(null);
		totalItemsField.setText(null);
		itemsTable.setAreaInventoryReport(areaInventoryReport);
		addItemButton.setEnabled(false);
		deleteItemButton.setEnabled(false);
		markAsReviewedButton.setEnabled(false);
	}

	private void updateComboBoxes() {
		List<Area> areas = areaService.getAllAreas();
		areaComboBox.setModel(
				new DefaultComboBoxModel<>(areas.toArray(new Area[areas.size()])));
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 30), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Inventory Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		inventoryDateField = ComponentUtil.createLabel(100);
		mainPanel.add(inventoryDateField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Encoder:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		encoderLabel = ComponentUtil.createLabel(100);
		mainPanel.add(encoderLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Status:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusLabel = ComponentUtil.createLabel(100);
		mainPanel.add(statusLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Report No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		reportNumberField.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(reportNumberField, c);

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Area:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		areaComboBox.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(areaComboBox, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Checker:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		checkerField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(checkerField, c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Double Checker:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		doubleCheckerField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(doubleCheckerField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Reviewer:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		reviewerField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(reviewerField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createItemsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);

		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane infoTableScrollPane = new JScrollPane(productInfoTable);
		infoTableScrollPane.setPreferredSize(new Dimension(500, 65));
		mainPanel.add(infoTableScrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.EAST;
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
		panel.add(ComponentUtil.createLabel(120, "Total Items:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsField = ComponentUtil.createLabel(120, "");
		panel.add(totalItemsField, c);
		
		return panel;
	}

	private JPanel createItemsTableToolBar() {
		JPanel panel = new JPanel();
		
		addItemButton = new MagicToolBarButton("plus_small", "Add Item", true);
		addItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.switchToAddMode();
			}
		});
		panel.add(addItemButton, BorderLayout.WEST);
		
		deleteItemButton = new MagicToolBarButton("minus_small", "Delete Item", true);
		deleteItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.delete();
			}
		});
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		markAsReviewedButton = new MagicToolBarButton("post", "Mark as Reviewed");
		markAsReviewedButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				markAreaInventoryReportAsReviewed();
			}
		});
		toolBar.add(markAsReviewedButton);
		
		JButton printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreviewDialog.updateDisplay(printService.generateReportAsString(areaInventoryReport));
				printPreviewDialog.setVisible(true);
			}
		});
		toolBar.add(printPreviewButton);
		
		JButton printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printService.print(areaInventoryReport);
			}
		});
		toolBar.add(printButton);
	}

	private void markAreaInventoryReportAsReviewed() {
		if (confirm("Mark Area Inventory Report as reviewed?")) {
			try {
				areaInventoryReportService.markAsReviewed(areaInventoryReport);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during saving");
				return;
			}
			showMessage("Area Inventory Report marked as reviewed");
			updateDisplay(areaInventoryReport);
		}
	}

	private void initializeUnitPricesAndQuantitiesTable() {
		productInfoTable = new ProductInfoTable();
		
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getColumn() == SalesRequisitionItemsTable.PRODUCT_CODE_COLUMN_INDEX ||
						e.getColumn() == TableModelEvent.ALL_COLUMNS) {
					updateUnitPricesAndQuantitiesTable();
				}
			}

		});
		
		itemsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateUnitPricesAndQuantitiesTable();
			}
		});
	}

	private void updateUnitPricesAndQuantitiesTable() {
		if (itemsTable.getSelectedRow() == -1) {
			productInfoTable.setProduct(null);
			return;
		}
		
		Product product = itemsTable.getCurrentlySelectedRowItem().getProduct();
		if (product != null) {
			product = productService.findProductByCode(product.getCode());
			productInfoTable.setProduct(product);
		} else {
			productInfoTable.setProduct(null);
		}
	}
	
}