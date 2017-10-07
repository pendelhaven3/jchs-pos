package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.PurchaseReturnItemsTable;
import com.pj.magic.model.PurchaseReturn;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.Supplier;
import com.pj.magic.service.PurchaseReturnService;
import com.pj.magic.service.ReceivingReceiptService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class PurchaseReturnPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(PurchaseReturnPanel.class);
	
	@Autowired private PurchaseReturnItemsTable itemsTable;
	@Autowired private ReceivingReceiptService receivingReceiptService;
	@Autowired private PurchaseReturnService purchaseReturnService;
	
	private PurchaseReturn purchaseReturn;
	private JLabel purchaseReturnNumberField;
	private MagicTextField receivingReceiptNumberField;
	private JLabel supplierField;
	private JLabel statusField;
	private JLabel postDateField;
	private JLabel postedByField;
	private MagicTextField remarksField;
	private JLabel totalItemsField;
	private JLabel totalAmountField;
	private JButton postButton;
	private JButton addItemButton;
	private JButton deleteItemButton;
	private JButton printButton;
	private JButton printPreviewButton;
	
	@Override
	protected void initializeComponents() {
		purchaseReturnNumberField = new JLabel();
		
		receivingReceiptNumberField = new MagicTextField();
		receivingReceiptNumberField.setNumbersOnly(true);
		
		supplierField = new JLabel();
		statusField = new JLabel();
		postDateField = new JLabel();
		postedByField = new JLabel();
		
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(100);
		remarksField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveRemarks();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(receivingReceiptNumberField);
		
		updateTotalFieldsWhenItemsTableChanges();
	}

	private void saveRemarks() {
		if (!remarksField.getText().equals(purchaseReturn.getRemarks())) {
			purchaseReturn.setRemarks(remarksField.getText());
			purchaseReturnService.save(purchaseReturn);
		}
	}

	@Override
	protected void registerKeyBindings() {
		receivingReceiptNumberField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveReceivingReceiptNumber();
			}
		});
		
		remarksField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.highlight();
			}
		});		
	}

	private void saveReceivingReceiptNumber() {
		if (StringUtils.isEmpty(receivingReceiptNumberField.getText())) {
			showErrorMessage("Receiving Receipt No. must be specified");
			receivingReceiptNumberField.requestFocusInWindow();
			return;
		}
		
		Long receivingReceiptNumber = Long.valueOf(receivingReceiptNumberField.getText());
		if (purchaseReturn.getId() != null 
				&& purchaseReturn.getReceivingReceipt().getReceivingReceiptNumber().equals(receivingReceiptNumber)) {
			// no changes
			remarksField.requestFocusInWindow();
			return;
		}
		
		ReceivingReceipt receivingReceipt = receivingReceiptService
				.findReceivingReceiptByReceivingReceiptNumber(receivingReceiptNumber);
		if (!validateReceivingReceipt(receivingReceipt)) {
			receivingReceiptNumberField.requestFocusInWindow();
			return;
		}
		
		purchaseReturn.setReceivingReceipt(receivingReceipt);
		try {
			purchaseReturnService.save(purchaseReturn);
			updateDisplay(purchaseReturn);
			remarksField.requestFocusInWindow();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			showErrorMessage(Constants.UNEXPECTED_ERROR_MESSAGE);
			return;
		}
	}

	private boolean validateReceivingReceipt(ReceivingReceipt receivingReceipt) {
		boolean valid = false;
		if (receivingReceipt == null) {
			showErrorMessage("No record matching Receiving Receipt No. specified");
		} else if (receivingReceipt.isCancelled()) {
			showErrorMessage("Receiving Receipt is already cancelled");
		} else {
			valid = true;
		}
		return valid;
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPurchaseReturnListPanel();
	}
	
	private void updateTotalFieldsWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalItemsField.setText(String.valueOf(purchaseReturn.getTotalItems()));
				totalAmountField.setText(FormatterUtil.formatAmount(purchaseReturn.getTotalAmount()));
			}
		});
	}

	public void updateDisplay(PurchaseReturn purchaseReturn) {
		if (purchaseReturn.getId() == null) {
			this.purchaseReturn = purchaseReturn;
			clearDisplay();
			return;
		}
		
		this.purchaseReturn = purchaseReturn = purchaseReturnService.getPurchaseReturn(purchaseReturn.getId());
		
		purchaseReturnNumberField.setText(purchaseReturn.getPurchaseReturnNumber().toString());
		receivingReceiptNumberField.setEnabled(!purchaseReturn.isPosted());
		receivingReceiptNumberField.setText(purchaseReturn.getReceivingReceipt().getReceivingReceiptNumber().toString());
		statusField.setText(purchaseReturn.getStatus());
		
		Supplier supplier = purchaseReturn.getReceivingReceipt().getSupplier();
		supplierField.setText(supplier.getCode() + " - " + supplier.getName());
		
		postDateField.setText(purchaseReturn.isPosted() ? FormatterUtil.formatDate(purchaseReturn.getPostDate()) : null);
		postedByField.setText(purchaseReturn.isPosted() ? purchaseReturn.getPostedBy().getUsername() : null);
		remarksField.setText(purchaseReturn.getRemarks());
		remarksField.setEnabled(!purchaseReturn.isPosted());
		
		itemsTable.setPurchaseReturn(purchaseReturn);
		
		postButton.setEnabled(!purchaseReturn.isPosted());
		addItemButton.setEnabled(!purchaseReturn.isPosted());
		deleteItemButton.setEnabled(!purchaseReturn.isPosted());
		printButton.setEnabled(true);
		printPreviewButton.setEnabled(true);
	}

	private void clearDisplay() {
		purchaseReturnNumberField.setText(null);
		receivingReceiptNumberField.setEnabled(true);
		receivingReceiptNumberField.setText(null);
		statusField.setText(null);
		supplierField.setText(null);
		postDateField.setText(null);
		postedByField.setText(null);
		remarksField.setText(null);
		remarksField.setEnabled(false);
		itemsTable.setPurchaseReturn(purchaseReturn);
		postButton.setEnabled(false);
		addItemButton.setEnabled(false);
		deleteItemButton.setEnabled(false);
		printButton.setEnabled(false);
		printPreviewButton.setEnabled(false);
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
		mainPanel.add(ComponentUtil.createLabel(180, "Purchase Return No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		purchaseReturnNumberField.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(purchaseReturnNumberField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Status:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusField = ComponentUtil.createLabel(150);
		mainPanel.add(statusField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(180, "Receiving Receipt No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		receivingReceiptNumberField.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(receivingReceiptNumberField, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Post Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postDateField = ComponentUtil.createLabel(100);
		mainPanel.add(postDateField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Supplier:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		supplierField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(supplierField, c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Posted By:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postedByField = ComponentUtil.createLabel(100);
		mainPanel.add(postedByField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Remarks:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(remarksField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
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
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postPurchaseReturn();
			}
		});
		toolBar.add(postButton);
		
		printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				printPreviewDialog.updateDisplay(printService.generateReportAsString(purchaseReturn));
//				printPreviewDialog.setVisible(true);
			}
		});
//		toolBar.add(printPreviewButton);
		
		printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				printService.print(purchaseReturn);
			}
		});
//		toolBar.add(printButton);
	}

	private void postPurchaseReturn() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}
		
		if (!purchaseReturn.hasItems()) {
			showErrorMessage("Cannot post a Purchase Return with no items");
			itemsTable.requestFocusInWindow();
			return;
		}
		
		if (confirm("Do you want to post this Purchase Return?")) {
			try {
				purchaseReturnService.post(purchaseReturn);
				JOptionPane.showMessageDialog(this, "Purchase Return posted");
				updateDisplay(purchaseReturn);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
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
				itemsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
	}
	
}
