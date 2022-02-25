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
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.NoActualQuantityException;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicCheckBox;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.dialog.SelectSupplierDialog;
import com.pj.magic.gui.tables.ProductInfoTable;
import com.pj.magic.gui.tables.PurchaseOrderItemsTable;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.Product2;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.Supplier;
import com.pj.magic.service.ExcelService;
import com.pj.magic.service.PaymentTermService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.Product2Service;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.PurchaseOrderService;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FileUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class PurchaseOrderPanel extends StandardMagicPanel {

    private static final long serialVersionUID = -6676395278966161803L;
    
    private static final String OPEN_SELECT_SUPPLIER_DIALOG_ACTION_NAME = "openSelectSupplierDialog";
	private static final String FOCUS_NEXT_FIELD_ACTION_NAME = "focusNextField";
	
	private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderPanel.class);
	
	@Autowired private PurchaseOrderItemsTable itemsTable;
	@Autowired private Product2Service product2Service;
	@Autowired private PurchaseOrderService purchaseOrderService;
	@Autowired private SupplierService supplierService;
	@Autowired private PaymentTermService paymentTermService;
	@Autowired private PrintService printService;
	@Autowired private SelectSupplierDialog selectSupplierDialog;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private ExcelService excelService;
	
	private PurchaseOrder purchaseOrder;
	private JLabel purchaseOrderNumberField;
	private JLabel statusField;
	private MagicTextField supplierCodeField;
	private JLabel supplierNameField;
	private JComboBox<PaymentTerm> paymentTermComboBox;
	private MagicTextField remarksField;
	private MagicTextField referenceNumberField;
	private MagicCheckBox vatInclusiveCheckBox;
	private JLabel totalItemsField;
	private JLabel subTotalAmountField;
	private JLabel vatAmountField;
	private JLabel totalAmountField;
	private MagicToolBarButton markAsDeliveredButton;
	private MagicToolBarButton postButton;
	private MagicToolBarButton addItemButton;
	private MagicToolBarButton deleteItemButton;
	private MagicToolBarButton printPreviewButton;
	private MagicToolBarButton printButton;
	private MagicToolBarButton toExcelButton;
	private JButton selectSupplierButton;
	private JButton deleteButton;
	private JFileChooser excelFileChooser;
	private ProductInfoTable productInfoTable;
	
	@Override
	protected void initializeComponents() {
		supplierCodeField = new MagicTextField();
		supplierCodeField.setMaximumLength(15);
		
		paymentTermComboBox = new JComboBox<>();
		paymentTermComboBox.addFocusListener(new FocusAdapter() {
			
			public void focusLost(FocusEvent e) {
				savePaymentTerm();
			}
		});
			
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(100);
		remarksField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveRemarks();
			}
		});
		
		referenceNumberField = new MagicTextField();
		referenceNumberField.setMaximumLength(30);
		referenceNumberField.addFocusListener(new FocusAdapter() {
			
			public void focusLost(FocusEvent e) {
				saveReferenceNumber();
			}
		});
		
		vatInclusiveCheckBox = new MagicCheckBox();
		vatInclusiveCheckBox.addOnClickListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				saveVatInclusive();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(supplierCodeField);
		
		updateTotalsPanelWhenItemsTableChanges();
		initializeUnitCostsAndQuantitiesTable();
		
		selectSupplierButton = new EllipsisButton();
		selectSupplierButton.setToolTipText("Select Supplier (F5)");
		selectSupplierButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectSupplier();
			}
		});
		
		excelFileChooser = new JFileChooser();
		excelFileChooser.setCurrentDirectory(new File(FileUtil.getDesktopFolderPath()));
		excelFileChooser.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return "Excel workbook (*.xlsx)";
			}
			
			@Override
			public boolean accept(File f) {
				return FilenameUtils.getExtension(f.getName()).equals("xlsx");
			}
		});
		
	}

	private void saveVatInclusive() {
		purchaseOrder.setVatInclusive(vatInclusiveCheckBox.isSelected());
		purchaseOrderService.save(purchaseOrder);
		updateTotalsPanel();
	}

	protected void selectSupplier() {
		selectSupplierDialog.searchSuppliers(supplierCodeField.getText());
		selectSupplierDialog.setVisible(true);
		
		Supplier selectedSupplier = selectSupplierDialog.getSelectedSupplier();
		if (selectedSupplier != null) {
			saveSupplier(selectedSupplier);
		}
	}

	private void saveSupplier(Supplier supplier) {
		if (!supplier.equals(purchaseOrder.getSupplier())) {
			purchaseOrder.setSupplier(supplier);
			supplierCodeField.setText(supplier.getCode());
			supplierNameField.setText(supplier.getName());
			
			purchaseOrder.setVatInclusive(supplier.isVatInclusive());
			
			purchaseOrder.setPaymentTerm(supplier.getPaymentTerm());
			paymentTermComboBox.setEnabled(true);
			paymentTermComboBox.setSelectedItem(supplier.getPaymentTerm());
			
			try {
				purchaseOrderService.save(purchaseOrder);
				updateDisplay(purchaseOrder);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
				return;
			}
		}
		
		if (purchaseOrder.isDelivered()) {
			referenceNumberField.requestFocusInWindow();
		} else {
	 		paymentTermComboBox.requestFocusInWindow();
		}
	}
	
	@Override
	protected void registerKeyBindings() {
		supplierCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
				OPEN_SELECT_SUPPLIER_DIALOG_ACTION_NAME);
		supplierCodeField.getActionMap().put(OPEN_SELECT_SUPPLIER_DIALOG_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectSupplier();
			}
		});
		
		supplierCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				FOCUS_NEXT_FIELD_ACTION_NAME);
		supplierCodeField.getActionMap().put(FOCUS_NEXT_FIELD_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Supplier supplier = supplierService.findSupplierByCode(supplierCodeField.getText());
				if (supplier != null) {
					saveSupplier(supplier);
				} else {
					showErrorMessage("No supplier matching code specified");
				}
			}
		});
		
		paymentTermComboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				FOCUS_NEXT_FIELD_ACTION_NAME);
		paymentTermComboBox.getActionMap().put(FOCUS_NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				remarksField.requestFocusInWindow();
			}
		});
		
		remarksField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), FOCUS_NEXT_FIELD_ACTION_NAME);
		remarksField.getActionMap().put(FOCUS_NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.highlight();
			}
		});
		
		referenceNumberField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				FOCUS_NEXT_FIELD_ACTION_NAME);
		referenceNumberField.getActionMap().put(FOCUS_NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				paymentTermComboBox.requestFocusInWindow();
			}
		});
	}

	protected void saveReferenceNumber() {
		if (!referenceNumberField.getText().equals(purchaseOrder.getReferenceNumber())) {
			purchaseOrder.setReferenceNumber(referenceNumberField.getText());
			purchaseOrderService.save(purchaseOrder);
		}
	}

	protected void saveRemarks() {
		if (!remarksField.getText().equals(purchaseOrder.getRemarks())) {
			purchaseOrder.setRemarks(remarksField.getText());
			purchaseOrderService.save(purchaseOrder);
		}
	}

	protected void savePaymentTerm() {
		PaymentTerm paymentTerm = (PaymentTerm)paymentTermComboBox.getSelectedItem();
		if ((paymentTerm == null && purchaseOrder.getPaymentTerm() != null)
				|| (paymentTerm != null && !paymentTerm.equals(purchaseOrder.getPaymentTerm()))) {
			purchaseOrder.setPaymentTerm(paymentTerm);
			purchaseOrderService.save(purchaseOrder);
		}
	}

	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToPurchaseOrderListPanel();
	}
	
	private void updateTotalsPanelWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				updateTotalsPanel();
			}
		});
	}

	private void updateTotalsPanel() {
		totalItemsField.setText(String.valueOf(purchaseOrder.getTotalNumberOfItems()));
		subTotalAmountField.setText(FormatterUtil.formatAmount(purchaseOrder.getSubTotalAmount()));
		vatAmountField.setText(FormatterUtil.formatAmount(purchaseOrder.getVatAmount()));
		totalAmountField.setText(FormatterUtil.formatAmount(purchaseOrder.getTotalAmount()));
	}

	public void updateDisplay(PurchaseOrder purchaseOrder) {
		List<PaymentTerm> paymentTerms = paymentTermService.getAllPaymentTerms();
		paymentTermComboBox.setModel(
				new DefaultComboBoxModel<>(paymentTerms.toArray(new PaymentTerm[paymentTerms.size()])));
		
		if (purchaseOrder.getId() == null) {
			this.purchaseOrder = purchaseOrder;
			clearDisplay();
			return;
		}
		
		this.purchaseOrder = purchaseOrderService.getPurchaseOrder(purchaseOrder.getId());
		purchaseOrder = this.purchaseOrder;
		
		purchaseOrderNumberField.setText(purchaseOrder.getPurchaseOrderNumber().toString());
		statusField.setText(purchaseOrder.getStatus());
		supplierCodeField.setText(purchaseOrder.getSupplier().getCode());
		supplierCodeField.setEnabled(!purchaseOrder.isPosted());
		supplierNameField.setText(purchaseOrder.getSupplier().getName());
		paymentTermComboBox.setEnabled(!purchaseOrder.isPosted());
		paymentTermComboBox.setSelectedItem(purchaseOrder.getPaymentTerm());
		remarksField.setEnabled(!purchaseOrder.isPosted());
		remarksField.setText(purchaseOrder.getRemarks());
		referenceNumberField.setEnabled(purchaseOrder.isDelivered() && !purchaseOrder.isPosted());
		referenceNumberField.setText(purchaseOrder.getReferenceNumber());
		vatInclusiveCheckBox.setEnabled(!purchaseOrder.isPosted());
		vatInclusiveCheckBox.setSelected(purchaseOrder.isVatInclusive(), false);
		itemsTable.setPurchaseOrder(purchaseOrder);
		
		markAsDeliveredButton.setEnabled(!purchaseOrder.isDelivered());
		postButton.setEnabled(purchaseOrder.isDelivered() && !purchaseOrder.isPosted());
		addItemButton.setEnabled(!purchaseOrder.isPosted());
		deleteItemButton.setEnabled(!purchaseOrder.isPosted());
		printButton.setEnabled(true);
		printPreviewButton.setEnabled(true);
		deleteButton.setEnabled(!purchaseOrder.isPosted());
		selectSupplierButton.setEnabled(!purchaseOrder.isPosted());
	}

	private void clearDisplay() {
		purchaseOrderNumberField.setText(null);
		statusField.setText(null);
		supplierCodeField.setText(null);
		supplierNameField.setText(null);
		paymentTermComboBox.setEnabled(false);
		paymentTermComboBox.setSelectedItem(null);
		remarksField.setEnabled(false);
		remarksField.setText(null);
		referenceNumberField.setEnabled(false);
		referenceNumberField.setText(null);
		vatInclusiveCheckBox.setEnabled(false);
		vatInclusiveCheckBox.setSelected(true, false);
		totalItemsField.setText(null);
		subTotalAmountField.setText(null);
		vatAmountField.setText(null);
		totalAmountField.setText(null);
		itemsTable.setPurchaseOrder(purchaseOrder);
		
		markAsDeliveredButton.setEnabled(false);
		postButton.setEnabled(false);
		addItemButton.setEnabled(false);
		deleteItemButton.setEnabled(false);
		printButton.setEnabled(false);
		printPreviewButton.setEnabled(false);
		selectSupplierButton.setEnabled(true);
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
		mainPanel.add(ComponentUtil.createLabel(120, "PO No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		purchaseOrderNumberField = ComponentUtil.createLabel(200, "");
		mainPanel.add(purchaseOrderNumberField, c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 1), c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Status:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusField = ComponentUtil.createLabel(100, "");
		mainPanel.add(statusField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Supplier:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createSupplierPanel(), c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Reference No.:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		referenceNumberField.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(referenceNumberField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Payment Term:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentTermComboBox.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(paymentTermComboBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "VAT Inclusive:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(vatInclusiveCheckBox, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Remarks:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(300, 25));
		mainPanel.add(remarksField, c);

		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 10), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
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
	
	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsField = ComponentUtil.createLabel(100, "");
		panel.add(totalItemsField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Sub Total:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		subTotalAmountField = ComponentUtil.createRightLabel(100, "");
		panel.add(subTotalAmountField, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 4;
		c.gridy = currentRow;
		panel.add(Box.createHorizontalStrut(30), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "VAT Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		vatAmountField = ComponentUtil.createRightLabel(100, "");
		panel.add(vatAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = ComponentUtil.createRightLabel(100, "");
		panel.add(totalAmountField, c);
		
		return panel;
	}

	private JPanel createSupplierPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierCodeField.setPreferredSize(new Dimension(120, 25));
		panel.add(supplierCodeField, c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(selectSupplierButton, c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createFiller(10, 20), c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierNameField = ComponentUtil.createLabel(200, "");
		panel.add(supplierNameField, c);
		
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
				itemsTable.removeCurrentlySelectedRow();
			}
		});
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
	}

	private void initializeUnitCostsAndQuantitiesTable() {
		productInfoTable = new ProductInfoTable();
		productInfoTable.setShowCost(true);
		
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getColumn() == PurchaseOrderItemsTable.PRODUCT_CODE_COLUMN_INDEX ||
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
		
		Product2 product = itemsTable.getCurrentlySelectedRowItem().getProduct();
		if (product != null) {
			productInfoTable.setProduct(product2Service.getProduct(product.getId()));
		} else {
			productInfoTable.setProduct(null);
		}
	}
	
	private void markPurchaseOrderAsDelivered() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}
		
		if (!purchaseOrder.hasItems()) {
			showErrorMessage("Cannot mark purchase order with no items");
			return;
		}
		
		if (confirm("Mark PO as delivered?")) {
			purchaseOrderService.markAsDelivered(purchaseOrder);
			updateDisplay(purchaseOrder);
			referenceNumberField.requestFocusInWindow();
		}
	}

	private void postPurchaseOrder() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}
		
		if (StringUtils.isEmpty(referenceNumberField.getText())) {
			showErrorMessage("Reference No. must be specified");
			referenceNumberField.requestFocusInWindow();
			return;
		}
		
		if (paymentTermComboBox.getSelectedItem() == null) {
			showErrorMessage("Payment Term must be specified");
			paymentTermComboBox.requestFocusInWindow();
			return;
		}
		
		if (confirm("Do you want to post this Purchase Order?")) {
			try {
				ReceivingReceipt receivingReceipt = purchaseOrderService.post(purchaseOrder);
				showMessage("Post successful!");
				getMagicFrame().switchToReceivingReceiptPanel(receivingReceipt);
			} catch (NoActualQuantityException e) {
				showErrorMessage("Actual Quantity must be specified");
				updateDisplay(purchaseOrder);
				itemsTable.highlightColumn(e.getItem(), itemsTable.getActualQuantityColumnIndex());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		deleteButton = new MagicToolBarButton("trash", "Delete");
		deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deletePurchaseOrder();
			}
		});
		toolBar.add(deleteButton);
		
		markAsDeliveredButton = new MagicToolBarButton("truck", "Mark as Delivered");
		markAsDeliveredButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				markPurchaseOrderAsDelivered();
			}
		});
		toolBar.add(markAsDeliveredButton);
		
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postPurchaseOrder();
			}
		});
		toolBar.add(postButton);
		
		printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreview();
			}
		});
//		toolBar.add(printPreviewButton);
		
		printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPurchaseOrder();
			}
		});	
//		toolBar.add(printButton);
		
		toExcelButton = new MagicToolBarButton("excel", "Generate Excel spreadsheet from Purchase Order");
		toExcelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateExcelSpreadsheetFromPurchaseOrder();
			}
		});	
		toolBar.add(toExcelButton);
	}

	private void generateExcelSpreadsheetFromPurchaseOrder() {
		excelFileChooser.setSelectedFile(new File(generateDefaultSpreadsheetName() + ".xlsx"));
		
		int returnVal = excelFileChooser.showSaveDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		try (
			Workbook workbook = excelService.generateSpreadsheet(purchaseOrder);
			FileOutputStream out = new FileOutputStream(excelFileChooser.getSelectedFile());
		) {
			workbook.write(out);
			showMessage("Excel spreadsheet generated successfully");
		} catch (IOException e) {
			showErrorMessage("Unexpected error during excel generation");
		}
	}

	private String generateDefaultSpreadsheetName() {
		return new StringBuilder()
			.append(purchaseOrder.getSupplier().getName())
			.append(" - ")
			.append(new SimpleDateFormat("MMM-dd-yyyy").format(new Date()))
			.append(" - PO ")
			.append(purchaseOrder.getPurchaseOrderNumber())
			.toString();
	}
	
	private void deletePurchaseOrder() {
		if (confirm("Do you really want to delete this Purchase Order?")) {
			try {
				purchaseOrderService.delete(purchaseOrder);
				showMessage("Purchase Order deleted");
				getMagicFrame().switchToPurchaseOrderListPanel();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred when deleting");
			}
		}
	}

	private void printPurchaseOrder() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		
		int confirm = JOptionPane.showConfirmDialog(this, "Include cost?", "Print Purchase Order", JOptionPane.YES_NO_CANCEL_OPTION);
		if (confirm == JOptionPane.YES_OPTION || confirm == JOptionPane.NO_OPTION) {
			printService.print(purchaseOrder, confirm == JOptionPane.YES_OPTION);
		}
	}

	protected void printPreview() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		
		int confirm = JOptionPane.showConfirmDialog(this, "Include cost?", "Print Preview Purchase Order", 
				JOptionPane.YES_NO_CANCEL_OPTION);
		if (confirm == JOptionPane.YES_OPTION || confirm == JOptionPane.NO_OPTION) {
			printPreviewDialog.updateDisplay(
					printService.generateReportAsString(purchaseOrder, confirm == JOptionPane.YES_OPTION));
			printPreviewDialog.setVisible(true);
		}
		
	}

}