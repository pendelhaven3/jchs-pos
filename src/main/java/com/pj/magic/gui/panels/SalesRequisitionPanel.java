package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.NotEnoughPromoStocksException;
import com.pj.magic.exception.SalesRequisitionPostException;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.AvailedPromoRewardsDialog;
import com.pj.magic.gui.dialog.PromoQualifyingAmountsDialog;
import com.pj.magic.gui.dialog.SalesRequisitionPostExceptionsDialog;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.dialog.StockQuantityConversionDialog;
import com.pj.magic.gui.tables.ProductInfoTable;
import com.pj.magic.gui.tables.SalesRequisitionItemsTable;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.service.CustomerService;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PaymentTermService;
import com.pj.magic.service.PricingSchemeService;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.SalesRequisitionService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.KeyUtil;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class SalesRequisitionPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(SalesRequisitionPanel.class);
	
	private static final String SAVE_CUSTOMER_ACTION_NAME = "saveCustomer";
	private static final String OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME = "openSelectCustomerDialog";
	
	@Autowired private SalesRequisitionItemsTable itemsTable;
	@Autowired private ProductService productService;
	@Autowired private SalesRequisitionService salesRequisitionService;
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	@Autowired private CustomerService customerService;
	@Autowired private PricingSchemeService pricingSchemeService;
	@Autowired private PaymentTermService paymentTermService;
	@Autowired private SalesRequisitionPostExceptionsDialog postExceptionsDialog;
	@Autowired private AvailedPromoRewardsDialog availedPromoRewardsDialog;
	@Autowired private PromoQualifyingAmountsDialog promoQualifyingAmountsDialog;
	@Autowired private StockQuantityConversionDialog stockQuantityConversionDialog;
	@Autowired private SalesInvoiceListPanel salesInvoiceListPanel;
	@Autowired private LoginService loginService;
	
	private SalesRequisition salesRequisition;
	private JLabel salesRequisitionNumberField;
	private JLabel createDateField;
	private UtilCalendarModel transactionDateModel;
	private JDatePickerImpl transactionDatePicker;
	private JTextField customerCodeField;
	private JLabel customerNameField;
	private MagicComboBox<PaymentTerm> paymentTermComboBox;
	private MagicComboBox<PricingScheme> pricingSchemeComboBox;
	private MagicComboBox<String> modeComboBox;
	private JLabel encoderField;
	private MagicTextField remarksField;
	private JLabel totalItemsField;
	private JLabel totalAmountField;
	private JButton selectCustomerButton;
	private MagicToolBarButton addItemButton;
	private MagicToolBarButton deleteItemButton;
	private MagicToolBarButton postButton;
	private MagicToolBarButton stockQuantityConversionButton;
	private MagicToolBarButton showAvailedPromoRewardsButton;
	private MagicToolBarButton showPromoQualifyingAmountsButton;
	private MagicToolBarButton separatePerCaseItemsButton;
	private ProductInfoTable productInfoTable;
	
	@Override
	protected void initializeComponents() {
		transactionDateModel = new UtilCalendarModel();
		transactionDateModel.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("value".equals(evt.getPropertyName()) && evt.getOldValue() != null 
						&& evt.getNewValue() != null) {
					salesRequisition.setTransactionDate(((Calendar)evt.getNewValue()).getTime());
					salesRequisitionService.save(salesRequisition);
				}
			}
		});
		
		customerCodeField = new MagicTextField();
		paymentTermComboBox = new MagicComboBox<>();
		paymentTermComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (paymentTermComboBox.shouldTriggerCustomListeners()) {
					if (paymentTermComboBox.getSelectedItem() == null) {
						showErrorMessage("Payment Term must be specified");
						return;
					}
					savePaymentTerm();
				}
			}
		});
		
		pricingSchemeComboBox = new MagicComboBox<>();
		pricingSchemeComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (pricingSchemeComboBox.shouldTriggerCustomListeners()) {
					if (pricingSchemeComboBox.getSelectedItem() == null) {
						showErrorMessage("Pricing Scheme must be specified");
						return;
					}
					savePricingScheme();
				}
			}
		});
		
		modeComboBox = new MagicComboBox<>();
		modeComboBox.setModel(new DefaultComboBoxModel<>(new String[] {"DELIVERY", "PICK-UP"}));
		modeComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (modeComboBox.shouldTriggerCustomListeners()) {
					if (modeComboBox.getSelectedItem() == null) {
						showErrorMessage("Mode must be specified");
						return;
					}
					saveMode();
				}
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
		
		selectCustomerButton = new EllipsisButton();
		selectCustomerButton.setToolTipText("Select Customer (F5)");
		selectCustomerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectCustomer();
			}
		});;
		
		focusOnComponentWhenThisPanelIsDisplayed(customerCodeField);
		
		updateTotalAmountFieldWhenItemsTableChanges();
		initializeUnitPricesAndQuantitiesTable();
	}

	protected void saveMode() {
		if (salesRequisition.getId() != null) {
			if (!salesRequisition.getMode().equals(modeComboBox.getSelectedItem())) {
				salesRequisition.setMode((String)modeComboBox.getSelectedItem());
				salesRequisitionService.save(salesRequisition);
			}
		} else {
			salesRequisition.setMode((String)modeComboBox.getSelectedItem());
			salesRequisitionService.save(salesRequisition);
			updateDisplay(salesRequisition);
		}
		remarksField.requestFocusInWindow();
	}

	protected void savePricingScheme() {
		if (salesRequisition.getId() != null) {
			if (!salesRequisition.getPricingScheme().equals(pricingSchemeComboBox.getSelectedItem())) {
				salesRequisition.setPricingScheme((PricingScheme)pricingSchemeComboBox.getSelectedItem());
				salesRequisitionService.save(salesRequisition);
				updateDisplay(salesRequisition);
			}
		} else {
			salesRequisition.setPricingScheme((PricingScheme)pricingSchemeComboBox.getSelectedItem());
			modeComboBox.setEnabled(true);
		}
		modeComboBox.requestFocusInWindow();
	}

	protected void savePaymentTerm() {
		if (salesRequisition.getId() != null) {
			if (!salesRequisition.getPaymentTerm().equals(paymentTermComboBox.getSelectedItem())) {
				salesRequisition.setPaymentTerm((PaymentTerm)paymentTermComboBox.getSelectedItem());
				salesRequisitionService.save(salesRequisition);
			}
		} else {
			salesRequisition.setPaymentTerm((PaymentTerm)paymentTermComboBox.getSelectedItem());
			pricingSchemeComboBox.setEnabled(true);
		}
		pricingSchemeComboBox.requestFocusInWindow();
	}

	protected void saveRemarks() {
		if (!remarksField.getText().equals(salesRequisition.getRemarks())) {
			salesRequisition.setRemarks(remarksField.getText());
			salesRequisitionService.save(salesRequisition);
		}
	}

	@Override
	protected void registerKeyBindings() {
		customerCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_CUSTOMER_ACTION_NAME);
		customerCodeField.getActionMap().put(SAVE_CUSTOMER_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveCustomer();
			}
		});
		
		customerCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME);
		customerCodeField.getActionMap().put(OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectCustomer();
			}
		});
		
		remarksField.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		remarksField.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.highlight();
			}
		});
	}

	// TODO: Review this again
	// TODO: Can combine selectCustomer() and saveCustomer()?
	protected void selectCustomer() {
		selectCustomerDialog.searchActiveCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			if (salesRequisition.getCustomer() != null && salesRequisition.getCustomer().equals(customer)) {
				// skip saving sales requisition since there is no change
				focusNextField();
				return;
			}
			
			salesRequisition.setCustomer(customer);
			customerCodeField.setText(customer.getCode());
			customerNameField.setText(customer.getName());
			paymentTermComboBox.setEnabled(true);
			
			if (customer.getPaymentTerm() != null) {
				if (salesRequisition.getId() != null) {
					if (customer.getPaymentTerm().equals((PaymentTerm)paymentTermComboBox.getSelectedItem())) {
						salesRequisitionService.save(salesRequisition);
					}
				}
				paymentTermComboBox.setSelectedItem(customer.getPaymentTerm());
			} else {
				paymentTermComboBox.setSelectedItem(null, false);
				if (salesRequisition.getId() != null) {
					salesRequisitionService.save(salesRequisition);
				}
				paymentTermComboBox.requestFocusInWindow();
			}
		}
	}

	protected void saveCustomer() {
		if (salesRequisition.getCustomer() != null) {
			if (salesRequisition.getCustomer().getCode().equals(customerCodeField.getText())) {
				// skip saving sales requisition since there is no change in customer
				focusNextField();
				return;
			}
		}
		
		if (StringUtils.isEmpty(customerCodeField.getText())) {
			showErrorMessage("Customer must be specified");
			return;
		}
		
		Customer customer = customerService.findCustomerByCode(customerCodeField.getText());
		if (customer == null || !customer.isActive()) {
			showErrorMessage("No customer matching code specified");
			return;
		} else {
			salesRequisition.setCustomer(customer);
			if (salesRequisition.getId() != null) {
				salesRequisitionService.save(salesRequisition);
			}
			customerNameField.setText(customer.getName());
			paymentTermComboBox.setEnabled(true);
			paymentTermComboBox.setSelectedItem(customer.getPaymentTerm(), false);
			paymentTermComboBox.requestFocusInWindow();
		}
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(customerCodeField);
		focusOrder.add(paymentTermComboBox);
		focusOrder.add(pricingSchemeComboBox);
		focusOrder.add(modeComboBox);
		focusOrder.add(remarksField);
	}
	
	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToSalesRequisitionsListPanel();
	}
	
	private void updateTotalAmountFieldWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalItemsField.setText(String.valueOf(salesRequisition.getTotalNumberOfItems()));
				totalAmountField.setText(FormatterUtil.formatAmount(salesRequisition.getTotalAmount()));
			}
		});
	}

	public void updateDisplay(SalesRequisition salesRequisition) {
		List<PricingScheme> pricingSchemes = pricingSchemeService.getAllPricingSchemes();
		pricingSchemeComboBox.setModel(
				new DefaultComboBoxModel<>(pricingSchemes.toArray(new PricingScheme[pricingSchemes.size()])));
		List<PaymentTerm> paymentTerms = paymentTermService.getAllPaymentTerms();
		paymentTermComboBox.setModel(
				new DefaultComboBoxModel<>(paymentTerms.toArray(new PaymentTerm[paymentTerms.size()])));
		
		if (salesRequisition.getId() == null) {
			this.salesRequisition = salesRequisition;
			clearDisplay();
			return;
		}
		
		this.salesRequisition = salesRequisitionService.getSalesRequisition(salesRequisition.getId());
		salesRequisition = this.salesRequisition;
		
		salesRequisitionNumberField.setText(salesRequisition.getSalesRequisitionNumber().toString());
		if (salesRequisition.getCustomer() == null) {
			customerCodeField.setText(null);
			customerNameField.setText(null);
		} else {
			customerCodeField.setText(salesRequisition.getCustomer().getCode());
			customerNameField.setText(salesRequisition.getCustomer().getName());
		}
		createDateField.setText(FormatterUtil.formatDate(salesRequisition.getCreateDate()));
		updateTransactionDateField();
		encoderField.setText(salesRequisition.getEncoder().getUsername());
		pricingSchemeComboBox.setEnabled(true);
		pricingSchemeComboBox.setSelectedItem(salesRequisition.getPricingScheme(), false);
		paymentTermComboBox.setEnabled(true);
		paymentTermComboBox.setSelectedItem(salesRequisition.getPaymentTerm(), false);
		modeComboBox.setEnabled(true);
		modeComboBox.setSelectedItem(salesRequisition.getMode(), false);
		remarksField.setEnabled(true);
		remarksField.setText(salesRequisition.getRemarks());
		totalItemsField.setText(String.valueOf(salesRequisition.getTotalNumberOfItems()));
		totalAmountField.setText(salesRequisition.getTotalAmount().toString());
		itemsTable.setSalesRequisition(salesRequisition);
		
		postButton.setEnabled(!salesRequisition.isPosted());
		addItemButton.setEnabled(!salesRequisition.isPosted());
		deleteItemButton.setEnabled(!salesRequisition.isPosted());
		showPromoQualifyingAmountsButton.setEnabled(true);
		showAvailedPromoRewardsButton.setEnabled(true);
		stockQuantityConversionButton.setEnabled(true);
		separatePerCaseItemsButton.setEnabled(true);
	}

	private void updateTransactionDateField() {
		transactionDatePicker.getComponents()[1].setVisible(!salesRequisition.isPosted());
		transactionDateModel.setValue(null); // set to null first to prevent property change listener from triggering
		transactionDateModel.setValue(DateUtils.toCalendar(salesRequisition.getTransactionDate()));
	}
	
	private void clearDisplay() {
		salesRequisitionNumberField.setText(null);
		createDateField.setText(null);
		transactionDateModel.setValue(null);
		transactionDatePicker.getComponents()[1].setVisible(false);
		customerCodeField.setText(null);
		customerNameField.setText(null);
		encoderField.setText(null);
		pricingSchemeComboBox.setEnabled(false);
		pricingSchemeComboBox.setSelectedItem(null, false);
		paymentTermComboBox.setEnabled(false);
		paymentTermComboBox.setSelectedItem(null, false);
		modeComboBox.setEnabled(false);
		modeComboBox.setSelectedItem(null, false);
		remarksField.setEnabled(false);
		remarksField.setText(null);
		totalItemsField.setText(null);
		totalAmountField.setText(null);
		itemsTable.setSalesRequisition(salesRequisition);
		
		postButton.setEnabled(false);
		addItemButton.setEnabled(false);
		deleteItemButton.setEnabled(false);
		stockQuantityConversionButton.setEnabled(false);
		showPromoQualifyingAmountsButton.setEnabled(false);
		showAvailedPromoRewardsButton.setEnabled(false);
		separatePerCaseItemsButton.setEnabled(false);		
	}

	private java.awt.Component createCustomerNamePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(customerCodeField, c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectCustomerButton.setPreferredSize(new Dimension(30, 24));
		panel.add(selectCustomerButton, c);
		
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
		panel.add(customerNameField, c);
		
		return panel;
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
			product = productService.findProductByCodeAndPricingScheme(
					product.getCode(), salesRequisition.getPricingScheme());
			productInfoTable.setProduct(product);
		} else {
			productInfoTable.setProduct(null);
		}
	}
	
	private void postSalesRequisition() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}
		
		if (confirm("Do you want to post this sales requisition?")) {
			if (!salesRequisition.hasItems()) {
				showErrorMessage("Cannot post a sales requisition with no items");
				itemsTable.requestFocusInWindow();
				return;
			}
			try {
//				SalesInvoice salesInvoice = salesRequisitionServiceClient.post(salesRequisition, loginService.getLoggedInUser());
                SalesInvoice salesInvoice = null;
				JOptionPane.showMessageDialog(this, "Post successful!");
				salesInvoiceListPanel.clearTableSelection();
				getMagicFrame().switchToSalesInvoicePanel(salesInvoice);
			} catch (SalesRequisitionPostException e) {
				postExceptionsDialog.updateDisplay(e);
				postExceptionsDialog.setVisible(true);
				
				if (postExceptionsDialog.getStockQuantityConversion() != null) {
					getMagicFrame().switchToStockQuantityConversionPanel(
							postExceptionsDialog.getStockQuantityConversion());
				}
			} catch (NotEnoughPromoStocksException e) {
				showErrorMessage("Not enough promo stocks");
			} catch (AlreadyPostedException e) {
				showErrorMessage("Sales Requisition is already posted");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
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
		mainPanel.add(ComponentUtil.createLabel(130, "SR No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		salesRequisitionNumberField = ComponentUtil.createLabel(200, "");
		mainPanel.add(salesRequisitionNumberField, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Create Date:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		createDateField = ComponentUtil.createLabel(100);
		mainPanel.add(createDateField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Customer Name:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		customerCodeField.setPreferredSize(new Dimension(150, 25));
		customerNameField = ComponentUtil.createLabel(190, "");
		
		mainPanel.add(createCustomerNamePanel(), c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Encoder:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		encoderField = ComponentUtil.createLabel(180, "");
		mainPanel.add(encoderField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Payment Term:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentTermComboBox.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(paymentTermComboBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(140, "Transaction Date:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		JDatePanelImpl datePanel = new JDatePanelImpl(transactionDateModel);
		transactionDatePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(transactionDatePicker, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Pricing Scheme:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		pricingSchemeComboBox.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(pricingSchemeComboBox, c);

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Mode:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		modeComboBox.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(modeComboBox, c);
		
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
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(300, 25));
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
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);

		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane infoTableScrollPane = new JScrollPane(productInfoTable);
		infoTableScrollPane.setPreferredSize(new Dimension(500, 65));
		mainPanel.add(infoTableScrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postSalesRequisition();
			}
		});
		
		toolBar.add(postButton);

		stockQuantityConversionButton = new MagicToolBarButton("convert", "Stock Quantity Conversion");
		stockQuantityConversionButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showStockQuantityConversionDialog();
			}
		});
		toolBar.add(stockQuantityConversionButton);
		
		showPromoQualifyingAmountsButton = new MagicToolBarButton("qualify", "Show Qualifying Amounts for Promos");
		showPromoQualifyingAmountsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showPromoQualifyingAmountsDialog();
			}
		});
		toolBar.add(showPromoQualifyingAmountsButton);
		
		showAvailedPromoRewardsButton = new MagicToolBarButton("present", "Show Availed Promos");
		showAvailedPromoRewardsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showAvailedPromoRewardsDialog();
			}
		});
		toolBar.add(showAvailedPromoRewardsButton);
		
		separatePerCaseItemsButton = new MagicToolBarButton("split", "Separate Per-Case Items");
		separatePerCaseItemsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				separatePerCaseItems();
			}
		});
		toolBar.add(separatePerCaseItemsButton);
	}

	private void separatePerCaseItems() {
		if (!confirm("Separate per-case items?")) {
			return;
		}
		
		SalesRequisition newSalesRequisition = salesRequisitionService.separatePerCaseItems(salesRequisition);
		updateDisplay(newSalesRequisition);
	}

	private void showPromoQualifyingAmountsDialog() {
		promoQualifyingAmountsDialog.updateDisplay(salesRequisition);
		promoQualifyingAmountsDialog.setVisible(true);
	}

	private void showAvailedPromoRewardsDialog() {
		availedPromoRewardsDialog.updateDisplay(salesRequisition);
		availedPromoRewardsDialog.setVisible(true);
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
		totalAmountField = ComponentUtil.createLabel(120, "");
		panel.add(totalAmountField, c);
		
		return panel;
	}
	
	private void showStockQuantityConversionDialog() {
		if (salesRequisition.getStockQuantityConversion() == null) {
			showErrorMessage("No stock quantity conversion");
			return;
		}
		
		stockQuantityConversionDialog.updateDisplay(salesRequisition);
		stockQuantityConversionDialog.setVisible(true);
	}
	
}
