package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.gui.component.MagicCheckBox;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.tables.AdjustmentOutItemsTable;
import com.pj.magic.gui.tables.ProductInfoTable;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.Product;
import com.pj.magic.service.AdjustmentOutService;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class AdjustmentOutPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(AdjustmentOutPanel.class);
	
	@Autowired private AdjustmentOutItemsTable itemsTable;
	@Autowired private ProductService productService;
	@Autowired private AdjustmentOutService adjustmentOutService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	@Autowired private LoginService loginService;
	
	private AdjustmentOut adjustmentOut;
	private JLabel adjustmentOutNumberLabel;
	private JLabel statusLabel;
	private MagicTextField remarksField;
	private MagicCheckBox pilferageCheckBox;
	private JLabel postDateField;
	private JLabel postedByField;
	private JLabel totalItemsField;
	private JLabel totalAmountField;
	private JButton postButton;
	private JButton addItemButton;
	private JButton deleteItemButton;
	private JButton printPreviewButton;
	private JButton printButton;
	private ProductInfoTable productInfoTable;
	
	@Override
	protected void initializeComponents() {
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(100);
		remarksField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveRemarks();
			}
		});
		
		pilferageCheckBox = new MagicCheckBox();
		pilferageCheckBox.addOnClickListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				savePilferageFlag();
			}
		});
		
		postDateField = new JLabel();
		
		focusOnComponentWhenThisPanelIsDisplayed(remarksField);
		updateTotalAmountFieldWhenItemsTableChanges();
		initializeUnitPricesAndQuantitiesTable();
	}

	@Override
	protected void registerKeyBindings() {
		remarksField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.highlight();
			}
		});
	}

	protected void saveRemarks() {
		if (adjustmentOut.getId() != null || !remarksField.getText().equals(adjustmentOut.getRemarks())) {
			adjustmentOut.setRemarks(remarksField.getText());
			try {
				adjustmentOutService.save(adjustmentOut);
				updateDisplay(adjustmentOut);
				itemsTable.highlight();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error on saving");
			}
		}
	}

	private void savePilferageFlag() {
		if (adjustmentOut.getPilferageFlag() == pilferageCheckBox.isSelected()) {
			return;
		}
		
		adjustmentOut.setPilferageFlag(pilferageCheckBox.isSelected());
		try {
			adjustmentOutService.save(adjustmentOut);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			showErrorMessage("Unexpected error on saving");
		}
	}
	
	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToAdjustmentOutListPanel();
	}
	
	private void updateTotalAmountFieldWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalItemsField.setText(String.valueOf(adjustmentOut.getTotalItems()));
				totalAmountField.setText(FormatterUtil.formatAmount(adjustmentOut.getTotalAmount()));
			}
		});
	}

	public void updateDisplay(AdjustmentOut adjustmentOut) {
		if (adjustmentOut.getId() == null) {
			this.adjustmentOut = adjustmentOut;
			clearDisplay();
			return;
		}
		
		this.adjustmentOut = adjustmentOut = adjustmentOutService.getAdjustmentOut(adjustmentOut.getId());
		
		adjustmentOutNumberLabel.setText(adjustmentOut.getAdjustmentOutNumber().toString());
		statusLabel.setText(adjustmentOut.getStatus());
		remarksField.setEnabled(!adjustmentOut.isPosted());
		remarksField.setText(adjustmentOut.getRemarks());
		pilferageCheckBox.setSelected(adjustmentOut.getPilferageFlag(), false);
		pilferageCheckBox.setEnabled(loginService.getLoggedInUser().isSupervisor());
		if (adjustmentOut.getPostDate() != null) {
			postDateField.setText(FormatterUtil.formatDateTime(adjustmentOut.getPostDate()));
		} else {
			postDateField.setText(null);
		}
		if (adjustmentOut.getPostedBy() != null) {
			postedByField.setText(adjustmentOut.getPostedBy().getUsername());
		} else {
			postedByField.setText(null);
		}
		totalItemsField.setText(String.valueOf(adjustmentOut.getTotalItems()));
		totalAmountField.setText(adjustmentOut.getTotalAmount().toString());
		itemsTable.setAdjustmentOut(adjustmentOut);
		
		postButton.setEnabled(!adjustmentOut.isPosted());
		addItemButton.setEnabled(!adjustmentOut.isPosted());
		deleteItemButton.setEnabled(!adjustmentOut.isPosted());
	}

	private void clearDisplay() {
		adjustmentOutNumberLabel.setText(null);
		statusLabel.setText(null);
		remarksField.setEnabled(true);
		remarksField.setText(null);
		pilferageCheckBox.setEnabled(loginService.getLoggedInUser().isSupervisor());
		pilferageCheckBox.setSelected(true, false);
		postDateField.setText(null);
		postedByField.setText(null);
		totalItemsField.setText(null);
		totalAmountField.setText(null);
		itemsTable.setAdjustmentOut(adjustmentOut);
		
		postButton.setEnabled(false);
		addItemButton.setEnabled(false);
		deleteItemButton.setEnabled(false);
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
		mainPanel.add(ComponentUtil.createLabel(100, "Adj. Out No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		adjustmentOutNumberLabel = ComponentUtil.createLabel(200, "");
		mainPanel.add(adjustmentOutNumberLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Status:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusLabel = ComponentUtil.createLabel(100);
		mainPanel.add(statusLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Remarks:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(remarksField, c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Post Date:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(postDateField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Pilferage:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(pilferageCheckBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Posted By:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postedByField = ComponentUtil.createLabel(100);
		mainPanel.add(postedByField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.insets.top = 10;
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
		mainPanel.add(ComponentUtil.createScrollPane(itemsTable, 600, 100), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		mainPanel.add(ComponentUtil.createScrollPane(productInfoTable, 500, 65), c);
		
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
		panel.add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsField = ComponentUtil.createLabel(60);
		panel.add(totalItemsField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(Box.createHorizontalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = ComponentUtil.createLabel(100);
		panel.add(totalAmountField, c);
		
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
				itemsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
	}

	private void initializeUnitPricesAndQuantitiesTable() {
		productInfoTable = new ProductInfoTable();
		
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getColumn() == AdjustmentOutItemsTable.PRODUCT_CODE_COLUMN_INDEX ||
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
	
	private void postAdjustmentOut() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}
		
		int confirm = showConfirmMessage("Do you want to post this Adjustment Out?");
		if (confirm == JOptionPane.OK_OPTION) {
			if (!adjustmentOut.hasItems()) {
				showErrorMessage("Cannot post a Adjustment Out with no items");
				itemsTable.requestFocusInWindow();
				return;
			}
			try {
				adjustmentOutService.post(adjustmentOut);
				JOptionPane.showMessageDialog(this, "Post successful!");
				updateDisplay(adjustmentOut);
			} catch (NotEnoughStocksException e) {	
				showErrorMessage("Not enough available stocks!");
				updateDisplay(adjustmentOut);
				itemsTable.highlightColumn(e.getAdjustmentOutItem(), 
						AdjustmentOutItemsTable.QUANTITY_COLUMN_INDEX);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postAdjustmentOut();
			}
		});
		toolBar.add(postButton);
		
		printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreviewDialog.updateDisplay(printService.generateReportAsString(adjustmentOut));
				printPreviewDialog.setVisible(true);
			}
		});
		toolBar.add(printPreviewButton);
		
		printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printService.print(adjustmentOut);
			}
		});
		toolBar.add(printButton);
	}

}