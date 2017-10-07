package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.tables.ProductInfoTable;
import com.pj.magic.gui.tables.SalesRequisitionItemsTable;
import com.pj.magic.gui.tables.StockQuantityConversionItemsTable;
import com.pj.magic.model.Product;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.StockQuantityConversionService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class StockQuantityConversionPanel extends StandardMagicPanel {

	private static final String SAVE_REMARKS_ACTION_NAME = "saveRemarks";
	
	@Autowired private StockQuantityConversionItemsTable itemsTable;
	@Autowired private ProductService productService;
	@Autowired private StockQuantityConversionService stockQuantityConversionService;
	@Autowired private PrintService printService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	
	private StockQuantityConversion stockQuantityConversion;
	private JLabel stockQuantityConversionNumberField;
	private JTextField remarksField;
	private JLabel postedField;
	private JLabel postDateField;
	private JLabel postedByField;
	private JLabel totalItemsField;
	private JButton postButton;
	private JButton addItemButton;
	private JButton deleteItemButton;
	private JButton assignPageNumberButton;
	private JButton markAsPrintedButton;
	private ProductInfoTable productInfoTable;
	
	@Override
	protected void initializeComponents() {
		stockQuantityConversionNumberField = new JLabel();
		postedField = new JLabel();
		postDateField = new JLabel();
		postedByField = new JLabel();
		remarksField = new MagicTextField();
		
		assignPageNumberButton = new JButton("Assign Page Number");
		assignPageNumberButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				assignPageNumber();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(remarksField);
		updateTotalItemsFieldWhenItemsTableChanges();
		initializeRemarksFieldBehavior();
		initializeUnitPricesAndQuantitiesTable();
	}

	@Override
	protected void registerKeyBindings() {
	}

	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToStockQuantityConversionListPanel();
	}
	
	private void updateTotalItemsFieldWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalItemsField.setText(String.valueOf(stockQuantityConversion.getTotalNumberOfItems()));
			}
		});
	}

	private void initializeRemarksFieldBehavior() {
		remarksField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_REMARKS_ACTION_NAME);
		remarksField.getActionMap().put(SAVE_REMARKS_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveRemarks();
			}
		});
	}
	
	protected void saveRemarks() {
		stockQuantityConversion.setRemarks(remarksField.getText());
		stockQuantityConversionService.save(stockQuantityConversion);
		itemsTable.highlight();
	}

	public void updateDisplay(StockQuantityConversion stockQuantityConversion) {
		this.stockQuantityConversion = stockQuantityConversion
				= stockQuantityConversionService.getStockQuantityConversion(stockQuantityConversion.getId());
		
		stockQuantityConversionNumberField.setText(stockQuantityConversion.getStockQuantityConversionNumber().toString());
		postedField.setText(stockQuantityConversion.getStatus());
		postDateField.setText(stockQuantityConversion.isPosted() ?
				FormatterUtil.formatDateTime(stockQuantityConversion.getPostDate()) : null);
		postedByField.setText(stockQuantityConversion.isPosted() ?
				stockQuantityConversion.getPostedBy().getUsername() : null);
		remarksField.setText(stockQuantityConversion.getRemarks());
		remarksField.setEnabled(!stockQuantityConversion.isPosted());
		totalItemsField.setText(String.valueOf(stockQuantityConversion.getTotalNumberOfItems()));
		itemsTable.setStockQuantityConversion(stockQuantityConversion);
		
		postButton.setEnabled(!stockQuantityConversion.isPosted());
		addItemButton.setEnabled(!stockQuantityConversion.isPosted());
		deleteItemButton.setEnabled(!stockQuantityConversion.isPosted());
		markAsPrintedButton.setEnabled(!stockQuantityConversion.isPrinted());
		assignPageNumberButton.setEnabled(!stockQuantityConversion.isPosted());
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
		mainPanel.add(ComponentUtil.createLabel(120, "SQC No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		stockQuantityConversionNumberField = ComponentUtil.createLabel(200, "");
		mainPanel.add(stockQuantityConversionNumberField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Posted:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postedField.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(postedField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Remarks:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(300, 25));
		mainPanel.add(remarksField, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Post Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(postDateField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		mainPanel.add(assignPageNumberButton, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Posted By:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postedByField.setPreferredSize(new Dimension(120, 20));
		mainPanel.add(postedByField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createItemsTableToolBar(), c);

		currentRow++;
		
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
			productInfoTable.setProduct(productService.getProduct(product.getId()));
		} else {
			productInfoTable.setProduct(null);
		}
	}
	
	private void postStockQuantityConversion() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}
		
		updateDisplay(stockQuantityConversion);
		
		if (!stockQuantityConversion.hasItems()) {
			showErrorMessage("Cannot post a stock quantity conversion with no items");
			itemsTable.requestFocusInWindow();
			return;
		}
		
		int confirm = showConfirmMessage("Do you want to post this stock quantity conversion?");
		if (confirm == JOptionPane.OK_OPTION) {
			try {
				stockQuantityConversionService.post(stockQuantityConversion);
				showMessage("Post successful!");
				updateDisplay(stockQuantityConversion);
			} catch (AlreadyPostedException e) {
				showErrorMessage("Stock Quantity Conversion is already posted");
			} catch (NotEnoughStocksException e) {	
				showErrorMessage("Not enough available stocks!");
				updateDisplay(stockQuantityConversion);
				itemsTable.highlightQuantityColumn(e.getStockQuantityConversionItem());
			}
		}
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postStockQuantityConversion();
			}
		});
		toolBar.add(postButton);
		
		JButton printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreview();
			}
		});
		toolBar.add(printPreviewButton);
		
		JButton printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printStockQuantityConversion();
			}
		});
		toolBar.add(printButton);
		
		markAsPrintedButton = new MagicToolBarButton("mark_print", "Mark As Printed");
		markAsPrintedButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				markStockQuantityConversionAsPrinted();
			}
		});
		toolBar.add(markAsPrintedButton);
	}

	private void printStockQuantityConversion() {
		stockQuantityConversion = 
				stockQuantityConversionService.getStockQuantityConversion(stockQuantityConversion.getId());
		printService.print(stockQuantityConversion);
		updateDisplay(stockQuantityConversion);
	}

	protected void printPreview() {
		updateDisplay(stockQuantityConversion);
		printPreviewDialog.updateDisplay(printService.generateReportAsString(stockQuantityConversion));
		printPreviewDialog.setVisible(true);
	}

	private JPanel createItemsTableToolBar() {
		JPanel panel = new JPanel();
		
		addItemButton = new MagicToolBarButton("plus_small", "Add Item (F10)", true);
		addItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.switchToAddMode();
			}
		});
		panel.add(addItemButton, BorderLayout.WEST);
		
		deleteItemButton = new MagicToolBarButton("minus_small", "Delete Item (Delete)", true);
		deleteItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.removeCurrentlySelectedRow();
			}
		});
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
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
	
	private void assignPageNumber() {
		stockQuantityConversion = stockQuantityConversionService.getStockQuantityConversion(
				stockQuantityConversion.getId());
		
		if (stockQuantityConversion.isPosted()) {
			showErrorMessage("Stock Quantity Conversion is already posted");
			updateDisplay(stockQuantityConversion);
			return;
		}
		
		String pageNumber = "P" + stockQuantityConversionService.getNextPageNumber();
		remarksField.setText(pageNumber);
		stockQuantityConversion.setRemarks(pageNumber);
		stockQuantityConversionService.save(stockQuantityConversion);
		showMessage("Saved");
		updateDisplay(stockQuantityConversion);
	}
	
	private void markStockQuantityConversionAsPrinted() {
		stockQuantityConversion.setPrinted(true);
		stockQuantityConversionService.save(stockQuantityConversion);
		showMessage("Saved");
		updateDisplay(stockQuantityConversion);
	}
	
}