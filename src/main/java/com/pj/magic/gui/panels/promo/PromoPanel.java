package com.pj.magic.gui.panels.promo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SelectManufacturerDialog;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.panels.StandardMagicPanel;
import com.pj.magic.gui.tables.PromoType2RulesTable;
import com.pj.magic.gui.tables.PromoType3RulePromoProductsTable;
import com.pj.magic.gui.tables.PromoType4RulePromoProductsTable;
import com.pj.magic.gui.tables.PromoType5RulePromoProductsTable;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType;
import com.pj.magic.model.PromoType1Rule;
import com.pj.magic.model.PromoType3Rule;
import com.pj.magic.model.PromoType4Rule;
import com.pj.magic.model.PromoType5Rule;
import com.pj.magic.model.Unit;
import com.pj.magic.service.ManufacturerService;
import com.pj.magic.service.PricingSchemeService;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.impl.PromoService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.ListUtil;
import com.pj.magic.util.NumberUtil;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class PromoPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(PromoPanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	
	@Autowired private PromoService promoService;
	@Autowired private ProductService productService;
	@Autowired private ManufacturerService manufacturerService;
	@Autowired private PricingSchemeService pricingSchemeService;
	@Autowired private SelectProductDialog selectProductDialog;
	@Autowired private SelectManufacturerDialog selectManufacturerDialog;
	@Autowired private PromoType2RulesTable promoType2RulesTable;
	@Autowired private PromoType3RulePromoProductsTable promoType3RulePromoProductsTable;
	@Autowired private PromoType4RulePromoProductsTable promoType4RulePromoProductsTable;
	@Autowired private PromoType5RulePromoProductsTable promoType5RulePromoProductsTable;
	
	private Promo promo;
	private JLabel promoNumberLabel;
	private MagicTextField nameField;
	private JComboBox<PromoType> promoTypeComboBox;
	private UtilCalendarModel startDateModel;
	private UtilCalendarModel endDateModel;
	private JComboBox<PricingScheme> pricingSchemeComboBox;
	private JCheckBox activeCheckBox;
	private JPanel promoDetailsPanel;
	private JComboBox<Manufacturer> manufacturerComboBox;
	private MagicTextField targetAmountField;
	private MagicTextField freeProductCodeField;
	private JLabel freeProductDescriptionLabel;
	private EllipsisButton selectProductButton;
	private JComboBox<String> freeUnitComboBox;
	private MagicTextField freeQuantityField;
	private MagicTextField dailyRedeemLimitPerCustomerField;
	private MagicTextField rebateField;
	private MagicButton saveButton;
	private JButton addRuleButton;
	private JButton removeRuleButton;
	private JButton addType3PromoProductButton;
	private JButton removeType3PromoProductButton;
	private JButton addAllType3PromoProductButton;
	private JButton removeAllType3PromoProductButton;
	private JButton addType4PromoProductButton;
	private JButton removeType4PromoProductButton;
	private JButton addAllType4PromoProductButton;
	private JButton removeAllType4PromoProductButton;
	private JButton addAllType4PromoProductByManufacturerButton;
	private JButton addType5PromoProductButton;
	private JButton removeType5PromoProductButton;
	private JButton addAllType5PromoProductButton;
	private JButton removeAllType5PromoProductButton;
	
	@Override
	protected void initializeComponents() {
		promoNumberLabel = new JLabel();
		
		nameField = new MagicTextField();
		nameField.setMaximumLength(50);
		
		promoTypeComboBox = new JComboBox<>();
		promoTypeComboBox.setModel(ListUtil.toDefaultComboBoxModel(PromoType.getPromoTypes(), true));
		
		startDateModel = new UtilCalendarModel();
		endDateModel = new UtilCalendarModel();
		
		activeCheckBox = new JCheckBox();
		
		manufacturerComboBox = new JComboBox<>();
		
		targetAmountField = new MagicTextField();
		
		freeProductCodeField = new MagicTextField();
		freeProductCodeField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		
		freeProductDescriptionLabel = new JLabel();
		
		selectProductButton = new EllipsisButton("Select Product (F5)");
		selectProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectProductDialog();
			}
		});
		
		freeUnitComboBox = new JComboBox<>();
		freeUnitComboBox.setModel(ListUtil.toDefaultComboBoxModel(
				Arrays.asList(Unit.CASE, Unit.TIE, Unit.CARTON, Unit.DOZEN, Unit.PIECES)));
		
		freeQuantityField = new MagicTextField();
		freeQuantityField.setNumbersOnly(true);
		
		dailyRedeemLimitPerCustomerField = new MagicTextField();
		dailyRedeemLimitPerCustomerField.setNumbersOnly(true);
		
		pricingSchemeComboBox = new JComboBox<>();
		
		rebateField = new MagicTextField();
		
		saveButton = new MagicButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePromo();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(nameField);
	}

	private void openSelectProductDialog() {
		selectProductDialog.searchProducts(freeProductCodeField.getText());
		selectProductDialog.setVisible(true);
		
		Product product = selectProductDialog.getSelectedProduct();
		if (product != null) {
			freeProductCodeField.setText(product.getCode());
			freeProductDescriptionLabel.setText(product.getDescription());
		}
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(nameField);
		focusOrder.add(promoTypeComboBox);
		focusOrder.add(activeCheckBox);
		focusOrder.add(manufacturerComboBox);
		focusOrder.add(targetAmountField);
		focusOrder.add(freeProductCodeField);
		focusOrder.add(freeUnitComboBox);
		focusOrder.add(freeQuantityField);
		focusOrder.add(saveButton);
	}
	
	private void savePromo() {
		if (promo.getId() == null) {
			saveNewPromo();
		} else {
			switch (promo.getPromoType()) {
			case PROMO_TYPE_1:
				savePromoType1();
				break;
			case PROMO_TYPE_2:
				savePromoType2();
				break;
			case PROMO_TYPE_3:
				savePromoType3();
				break;
			case PROMO_TYPE_4:
				savePromoType4();
				break;
			case PROMO_TYPE_5:
				savePromoType5();
				break;
			}
		}
	}

	private void savePromoType4() {
		if (!validatePromoType4()) {
			return;
		}
		
		if (confirm("Save Promo?")) {
			setCommonFieldsForSaving();
			if (promo.getPromoType4Rule() == null) {
				promo.setPromoType4Rule(new PromoType4Rule());
			}
			PromoType4Rule rule = promo.getPromoType4Rule();
			rule.setParent(promo);
			rule.setTargetAmount(NumberUtil.toBigDecimal(targetAmountField.getText()));
			
			try {
				promoService.save(promo);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
				return;
			}
			
			showMessage("Saved!");
			updateDisplay(promo);
		}
	}

	private void savePromoType5() {
		if (!validatePromoType5()) {
			return;
		}
		
		if (confirm("Save Promo?")) {
			setCommonFieldsForSaving();
			if (promo.getPromoType5Rule() == null) {
				promo.setPromoType5Rule(new PromoType5Rule());
			}
			PromoType5Rule rule = promo.getPromoType5Rule();
			rule.setParent(promo);
			rule.setTargetAmount(NumberUtil.toBigDecimal(targetAmountField.getText()));
			rule.setRebate(NumberUtil.toBigDecimal(rebateField.getText()));
			
			try {
				promoService.save(promo);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
				return;
			}
			
			showMessage("Saved!");
			updateDisplay(promo);
		}
	}

	private boolean validatePromoType4() {
		try {
			validateCommonFields();
			validateMandatoryField(targetAmountField, "Target Amount");
		} catch (ValidationException e) {
			return false;
		}
		
		if (!NumberUtil.isAmount(targetAmountField.getText())) {
			showErrorMessage("Target Amount must be a valid amount");
			targetAmountField.requestFocusInWindow();
			return false;
		}
		
		return true;
	}

	private boolean validatePromoType5() {
		try {
			validateCommonFields();
			validateMandatoryField(targetAmountField, "Target Amount");
			validateMandatoryField(rebateField, "Rebate");
		} catch (ValidationException e) {
			return false;
		}
		
		if (!NumberUtil.isAmount(targetAmountField.getText())) {
			showErrorMessage("Target Amount must be a valid amount");
			targetAmountField.requestFocusInWindow();
			return false;
		}
		
		if (!NumberUtil.isAmount(rebateField.getText())) {
			showErrorMessage("Rebate must be a valid amount");
			rebateField.requestFocusInWindow();
			return false;
		}
		
		return true;
	}

	private void savePromoType3() {
		if (!validatePromoType3()) {
			return;
		}
		
		if (confirm("Save Promo?")) {
			setCommonFieldsForSaving();
			if (promo.getPromoType3Rule() == null) {
				promo.setPromoType3Rule(new PromoType3Rule());
			}
			PromoType3Rule rule = promo.getPromoType3Rule();
			rule.setParent(promo);
			rule.setTargetAmount(NumberUtil.toBigDecimal(targetAmountField.getText()));
			rule.setFreeProduct(productService.findProductByCode(freeProductCodeField.getText()));
			rule.setFreeUnit((String)freeUnitComboBox.getSelectedItem());
			rule.setFreeQuantity(Integer.valueOf(freeQuantityField.getText()));
			if (!StringUtils.isEmpty(dailyRedeemLimitPerCustomerField.getText())) {
				rule.setDailyRedeemLimitPerCustomer(Integer.valueOf(dailyRedeemLimitPerCustomerField.getText()));
			}
			
			try {
				promoService.save(promo);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
				return;
			}
			
			showMessage("Saved!");
			updateDisplay(promo);
		}
	}

	private boolean validatePromoType3() {
		try {
			validateCommonFields();
			validateMandatoryField(targetAmountField, "Target Amount");
			validateMandatoryField(freeProductCodeField, "Product Code");
			validateMandatoryField(freeUnitComboBox, "Unit");
			validateMandatoryField(freeQuantityField, "Quantity");
		} catch (ValidationException e) {
			return false;
		}
		
		if (!NumberUtil.isAmount(targetAmountField.getText())) {
			showErrorMessage("Target Amount must be a valid amount");
			targetAmountField.requestFocusInWindow();
			return false;
		}
		
		if (productService.findProductByCode(freeProductCodeField.getText()) == null) {
			showErrorMessage("No product matching code specified");
			freeProductCodeField.requestFocusInWindow();
			return false;
		}
		
		return true;
	}

	private void savePromoType2() {
		if (!validatePromoType2()) {
			return;
		}
		
		if (confirm("Save Promo?")) {
			setCommonFieldsForSaving();
			try {
				promoService.save(promo);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
				return;
			}
			
			showMessage("Saved!");
			updateDisplay(promo);
		}
	}

	private void setCommonFieldsForSaving() {
		promo.setName(nameField.getText());
		if (isStartDateSpecified()) {
			promo.setStartDate(startDateModel.getValue().getTime());
		} else {
			promo.setStartDate(null);
		}
		if (isEndDateSpecified()) {
			promo.setEndDate(endDateModel.getValue().getTime());
		} else {
			promo.setEndDate(null);
		}
		promo.setActive(activeCheckBox.isSelected());
		if (pricingSchemeComboBox.getSelectedIndex() > 0) {
			promo.setPricingScheme((PricingScheme)pricingSchemeComboBox.getSelectedItem());
		} else {
			promo.setPricingScheme(null);
		}
	}

	private boolean isStartDateSpecified() {
		return startDateModel.getValue() != null;
	}

	private boolean isEndDateSpecified() {
		return endDateModel.getValue() != null;
	}

	private boolean validatePromoType2() {
		try {
			validateCommonFields();
		} catch (ValidationException e) {
			return false;
		}
		return true;
	}

	private void validateCommonFields() throws ValidationException {
		validateMandatoryField(nameField, "Name");
		validateMandatoryField(startDateModel, "Start Date");
		validateStartAndEndDates();
	}
	
	private void saveNewPromo() {
		if (!validateNewPromo()) {
			return;
		}
		
		if (confirm("Save Promo?")) {
			setCommonFieldsForSaving();
			promo.setPromoType((PromoType)promoTypeComboBox.getSelectedItem());
			try {
				promoService.save(promo);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
				return;
			}
			
			showMessage("Saved!");
			updateDisplay(promo);
		}
	}

	private void savePromoType1() {
		if (!validatePromoType1()) {
			return;
		}
		
		if (confirm("Save Promo?")) {
			setCommonFieldsForSaving();
			if (promo.getPromoType1Rule() == null) {
				promo.setPromoType1Rule(new PromoType1Rule());
			}
			PromoType1Rule rule = promo.getPromoType1Rule();
			rule.setParent(promo);
			rule.setTargetAmount(NumberUtil.toBigDecimal(targetAmountField.getText()));
			rule.setManufacturer((Manufacturer)manufacturerComboBox.getSelectedItem());
			rule.setProduct(productService.findProductByCode(freeProductCodeField.getText()));
			rule.setUnit((String)freeUnitComboBox.getSelectedItem());
			rule.setQuantity(Integer.valueOf(freeQuantityField.getText()));
			if (!StringUtils.isEmpty(dailyRedeemLimitPerCustomerField.getText())) {
				rule.setDailyRedeemLimitPerCustomer(Integer.valueOf(dailyRedeemLimitPerCustomerField.getText()));
			}
			
			try {
				promoService.save(promo);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
				return;
			}
			
			showMessage("Saved!");
			updateDisplay(promo);
		}
	}
	
	private boolean validateNewPromo() {
		try {
			validateMandatoryField(nameField, "Name");
			validateMandatoryField(promoTypeComboBox, "Promo Type");
			validateStartAndEndDates();
		} catch (ValidationException e) {
			return false;
		}
		return true;
	}

	private void validateStartAndEndDates() throws ValidationException {
		if (isStartDateAndEndDateSpecified()) {
			if (!isStartDateLessThanEndDate()) {
				showErrorMessage("Start Date must be less than End Date");
				throw new ValidationException();
			}
		}
	}

	private boolean isStartDateAndEndDateSpecified() {
		return startDateModel.getValue() != null && endDateModel.getValue() != null;	
	}
		
	private boolean isStartDateLessThanEndDate() {
		return DateUtils.truncatedCompareTo(startDateModel.getValue(), endDateModel.getValue(), Calendar.DATE) < 0;
	}

	private boolean validatePromoType1() {
		try {
			validateCommonFields();
			validateMandatoryField(manufacturerComboBox, "Manufacturer");
			validateMandatoryField(targetAmountField, "Target Amount");
			validateMandatoryField(freeProductCodeField, "Product Code");
			validateMandatoryField(freeUnitComboBox, "Unit");
			validateMandatoryField(freeQuantityField, "Quantity");
		} catch (ValidationException e) {
			return false;
		}
		
		if (!NumberUtil.isAmount(targetAmountField.getText())) {
			showErrorMessage("Target Amount must be a valid amount");
			targetAmountField.requestFocusInWindow();
			return false;
		}
		
		if (productService.findProductByCode(freeProductCodeField.getText()) == null) {
			showErrorMessage("No product matching code specified");
			freeProductCodeField.requestFocusInWindow();
			return false;
		}
		
		return true;
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50));
			
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Promo No.: "), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		promoNumberLabel.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(promoNumberLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Name: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		nameField.setPreferredSize(new Dimension(300, 25));
		mainPanel.add(nameField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Promo Type: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		promoTypeComboBox.setPreferredSize(new Dimension(500, 25));
		mainPanel.add(promoTypeComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(new JLabel("Start Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createStartAndEndDatePanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Pricing Scheme:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		pricingSchemeComboBox.getPreferredSize().height = 25;
		mainPanel.add(pricingSchemeComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Active?"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(activeCheckBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.WEST;
		
		promoDetailsPanel = new JPanel(new GridBagLayout());
		mainPanel.add(promoDetailsPanel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		saveButton.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(saveButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createGlue(), c);
	}

	private JPanel createStartAndEndDatePanel() {
		JDatePanelImpl startDatePanel = new JDatePanelImpl(startDateModel);
		JDatePickerImpl startDatePicker = new JDatePickerImpl(startDatePanel, new DatePickerFormatter());
		
		JDatePanelImpl endDatePanel = new JDatePanelImpl(endDateModel);
		JDatePickerImpl endDatePicker = new JDatePickerImpl(endDatePanel, new DatePickerFormatter());
		
		return ComponentUtil.createGenericPanel(
				startDatePicker,
				Box.createHorizontalStrut(50),
				new JLabel("End Date:"),
				Box.createHorizontalStrut(30),
				endDatePicker
		);
	}

	private void updateToPromoType2Panel(JPanel panel) {
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createPromoType2RulesTableToolBar(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		
		JScrollPane scrollPane = new JScrollPane(promoType2RulesTable);
		scrollPane.setPreferredSize(new Dimension(900, 200));
		panel.add(scrollPane, c);
	}

	private void updateToPromoType3Panel(JPanel panel) {
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Target Amount: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		targetAmountField.setPreferredSize(new Dimension(100, 25));
		panel.add(targetAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Free Product: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createProductPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Free Unit:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		freeUnitComboBox.setPreferredSize(new Dimension(60, 25));
		panel.add(freeUnitComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Free Quantity:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		freeQuantityField.setPreferredSize(new Dimension(50, 25));
		dailyRedeemLimitPerCustomerField.setPreferredSize(new Dimension(50, 25));
		panel.add(ComponentUtil.createGenericPanel(
				freeQuantityField,
				Box.createHorizontalStrut(50),
				new JLabel("Daily Redeem Limit Per Customer:"),
				Box.createHorizontalStrut(30),
				dailyRedeemLimitPerCustomerField,
				Box.createHorizontalStrut(5),
				new JLabel("(0 = No Limit)")
		), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 2;
		panel.add(createPromoType3RulesTableToolBar(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 2;
		
		JScrollPane scrollPane = new JScrollPane(promoType3RulePromoProductsTable);
		scrollPane.setPreferredSize(new Dimension(600, 100));
		panel.add(scrollPane, c);
	}

	private JPanel createPromoType3RulesTableToolBar() {
		JPanel panel = new JPanel();
		
		addType3PromoProductButton = new MagicToolBarButton("plus_small", "Add Promo Product", true);
		addType3PromoProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addType3PromoProduct();
			}
		});
		panel.add(addType3PromoProductButton, BorderLayout.WEST);
		
		removeType3PromoProductButton = new MagicToolBarButton("minus_small", "Remove Promo Product", true);
		removeType3PromoProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeType3PromoProduct();
			}
		});
		panel.add(removeType3PromoProductButton, BorderLayout.WEST);
		
		addAllType3PromoProductButton = new MagicToolBarButton("add_all_small", "Add All Promo Product", true);
		addAllType3PromoProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addAllType3PromoProduct();
			}
		});
		panel.add(addAllType3PromoProductButton, BorderLayout.WEST);
		
		removeAllType3PromoProductButton = new MagicToolBarButton("delete_all_small", "Remove All Promo Products", true);
		removeAllType3PromoProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeAllType3PromoProduct();
			}
		});
		panel.add(removeAllType3PromoProductButton, BorderLayout.WEST);
		
		return panel;
	}

	private void removeAllType3PromoProduct() {
		if (confirm("Remove all promo products?")) {
			try {
				promoService.removeAllPromoProducts(promo.getPromoType3Rule());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
			}
			updateDisplay(promo);
		}
	}

	private void removeType3PromoProduct() {
		promoType3RulePromoProductsTable.removeCurrentlySelectedPromoProduct();
	}

	private void addType3PromoProduct() {
		promoType3RulePromoProductsTable.addNewRow();
	}

	private void addAllType3PromoProduct() {
		if (confirm("Add all products to promo?")) {
			try {
				promoService.addAllPromoProducts(promo.getPromoType3Rule());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
				return;
			}
			updateDisplay(promo);
		}
	}

	private JPanel createPromoType2RulesTableToolBar() {
		JPanel panel = new JPanel();
		
		addRuleButton = new MagicToolBarButton("plus_small", "Add Rule", true);
		addRuleButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addPromoType2Rule();
			}
		});
		panel.add(addRuleButton, BorderLayout.WEST);
		
		removeRuleButton = new MagicToolBarButton("minus_small", "Delete", true);
		removeRuleButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removePromoType2Rule();
			}
		});
		panel.add(removeRuleButton, BorderLayout.WEST);
		
		return panel;
	}

	private void removePromoType2Rule() {
		promoType2RulesTable.removeCurrentlySelectedRule();
	}

	private void addPromoType2Rule() {
		promoType2RulesTable.addNewRow();
	}

	private void updateToPromoType1Panel(JPanel panel) {
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Manufacturer: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		manufacturerComboBox.setPreferredSize(new Dimension(300, 25));
		panel.add(manufacturerComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Target Amount: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		targetAmountField.setPreferredSize(new Dimension(100, 25));
		panel.add(targetAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Product: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createProductPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(60, "Unit: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		freeUnitComboBox.setPreferredSize(new Dimension(60, 25));
		panel.add(freeUnitComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Quantity: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		freeQuantityField.setPreferredSize(new Dimension(50, 25));
		dailyRedeemLimitPerCustomerField.setPreferredSize(new Dimension(50, 25));
		panel.add(ComponentUtil.createGenericPanel(
				freeQuantityField,
				Box.createHorizontalStrut(50),
				new JLabel("Daily Redeem Limit Per Customer:"),
				Box.createHorizontalStrut(30),
				dailyRedeemLimitPerCustomerField,
				Box.createHorizontalStrut(5),
				new JLabel("(0 = No Limit)")
		), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(Box.createGlue(), c);
	}

	private JPanel createProductPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		
		freeProductCodeField.setPreferredSize(new Dimension(100, 25));
		panel.add(freeProductCodeField, c);
		
		panel.add(selectProductButton, c);
		panel.add(Box.createHorizontalStrut(10), c);
		
		freeProductDescriptionLabel.setPreferredSize(new Dimension(400, 20));
		panel.add(freeProductDescriptionLabel, c);
		
		return panel;
	}

	@Override
	protected void registerKeyBindings() {
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), NEXT_FIELD_ACTION_NAME);
		getActionMap().put(NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				focusNextField();
			}
		});
		
		freeProductCodeField.onF5Key(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectProductDialog();
			}
		});
		
		saveButton.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePromo();
			}
		});
	}

	public void updateDisplay(Promo promo) {
		updatePricingSchemeComboBox();
		
		this.promo = promo;
		
		if (promo.getId() == null) {
			clearDisplay();
			return;
		}
		
		this.promo = promo = promoService.getPromo(promo.getId());
		
		promoNumberLabel.setText(String.valueOf(promo.getPromoNumber()));
		nameField.setText(promo.getName());
		promoTypeComboBox.setEnabled(false);
		promoTypeComboBox.setSelectedItem(promo.getPromoType());
		if (promo.hasStartDate()) {
			startDateModel.setValue(DateUtils.toCalendar(promo.getStartDate()));
		} else {
			startDateModel.setValue(null);
		}
		if (promo.hasEndDate()) {
			endDateModel.setValue(DateUtils.toCalendar(promo.getEndDate()));
		} else {
			endDateModel.setValue(null);
		}
		if (promo.getPricingScheme() != null) {
			pricingSchemeComboBox.setSelectedItem(promo.getPricingScheme());
		} else {
			pricingSchemeComboBox.setSelectedIndex(0);
		}
		activeCheckBox.setSelected(promo.isActive());
		
		updatePromoDetailsPanel();
	}

	private void updatePromoDetailsPanel() {
		promoDetailsPanel.removeAll();
		switch (promo.getPromoType()) {
		case PROMO_TYPE_1:
			updateDisplayForPromoType1();
			break;
		case PROMO_TYPE_2:
			updateDisplayForPromoType2();
			break;
		case PROMO_TYPE_3:
			updateDisplayForPromoType3();
			break;
		case PROMO_TYPE_4:
			updateDisplayForPromoType4();
			break;
		case PROMO_TYPE_5:
			updateDisplayForPromoType5();
			break;
		}
		promoDetailsPanel.revalidate();
		promoDetailsPanel.repaint();
		promoDetailsPanel.setVisible(true);
	}

	private void updateDisplayForPromoType4() {
		updateToPromoType4Panel(promoDetailsPanel);
		
		PromoType4Rule rule = promo.getPromoType4Rule();
		if (rule != null) {
			targetAmountField.setText(FormatterUtil.formatAmount(rule.getTargetAmount()));
			promoType4RulePromoProductsTable.setRule(rule);
			addType4PromoProductButton.setEnabled(true);
			removeType4PromoProductButton.setEnabled(true);
			addAllType4PromoProductButton.setEnabled(true);
			removeType4PromoProductButton.setEnabled(true);
			addAllType4PromoProductByManufacturerButton.setEnabled(true);
		} else {
			targetAmountField.setText(null);
			promoType4RulePromoProductsTable.clear();
			addType4PromoProductButton.setEnabled(false);
			removeType4PromoProductButton.setEnabled(false);
			addAllType4PromoProductButton.setEnabled(false);
			removeAllType4PromoProductButton.setEnabled(false);
			addAllType4PromoProductByManufacturerButton.setEnabled(false);
		}
	}

	private void updateToPromoType4Panel(JPanel panel) {
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Target Amount: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		targetAmountField.setPreferredSize(new Dimension(100, 25));
		panel.add(targetAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 2;
		panel.add(createPromoType4RulesTableToolBar(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 2;
		
		JScrollPane scrollPane = new JScrollPane(promoType4RulePromoProductsTable);
		scrollPane.setPreferredSize(new Dimension(600, 100));
		panel.add(scrollPane, c);
	}

	private JPanel createPromoType4RulesTableToolBar() {
		JPanel panel = new JPanel();
		
		addType4PromoProductButton = new MagicToolBarButton("plus_small", "Add Promo Product", true);
		addType4PromoProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addType4PromoProduct();
			}
		});
		panel.add(addType4PromoProductButton, BorderLayout.WEST);
		
		removeType4PromoProductButton = new MagicToolBarButton("minus_small", "Remove Promo Product", true);
		removeType4PromoProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeType4PromoProduct();
			}
		});
		panel.add(removeType4PromoProductButton, BorderLayout.WEST);
		
		addAllType4PromoProductButton = new MagicToolBarButton("add_all_small", "Add All Promo Product", true);
		addAllType4PromoProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addAllType4PromoProduct();
			}
		});
		panel.add(addAllType4PromoProductButton, BorderLayout.WEST);
		
		removeAllType4PromoProductButton = new MagicToolBarButton("delete_all_small", "Remove All Promo Products", true);
		removeAllType4PromoProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeAllType4PromoProduct();
			}
		});
		panel.add(removeAllType4PromoProductButton, BorderLayout.WEST);
		
		addAllType4PromoProductByManufacturerButton = new MagicToolBarButton("add_all_by_manufacturer", "Add All Promo Product By Manufacturer", true);
		addAllType4PromoProductByManufacturerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addAllType4PromoProductByManufacturer();
			}
		});
		panel.add(addAllType4PromoProductByManufacturerButton, BorderLayout.WEST);
		
		return panel;
	}

	private void removeAllType4PromoProduct() {
		if (confirm("Remove all promo products?")) {
			try {
				promoService.removeAllPromoProducts(promo.getPromoType4Rule());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
			}
			updateDisplay(promo);
		}
	}

	private void addAllType4PromoProduct() {
		if (confirm("Add all products to promo?")) {
			try {
				promoService.addAllPromoProducts(promo.getPromoType4Rule());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
				return;
			}
			updateDisplay(promo);
		}
	}

	private void removeType4PromoProduct() {
		promoType4RulePromoProductsTable.removeCurrentlySelectedPromoProduct();
	}

	private void addAllType4PromoProductByManufacturer() {
		selectManufacturerDialog.searchManufacturers();
		selectManufacturerDialog.setVisible(true);
		
		Manufacturer manufacturer = selectManufacturerDialog.getSelectedManufacturer();
		if (manufacturer != null) {
			if (confirm("Add all products by " + manufacturer.getName() + " to promo?")) {
				try {
					promoService.addAllPromoProductsByManufacturer(promo.getPromoType4Rule(), manufacturer);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					showMessageForUnexpectedError();
					return;
				}
				updateDisplay(promo);
			}
		}
	}
	
	private void addType4PromoProduct() {
		promoType4RulePromoProductsTable.addNewRow();
	}

	private void updateDisplayForPromoType5() {
		updateToPromoType5Panel(promoDetailsPanel);
		
		PromoType5Rule rule = promo.getPromoType5Rule();
		if (rule != null) {
			targetAmountField.setText(FormatterUtil.formatAmount(rule.getTargetAmount()));
			rebateField.setText(FormatterUtil.formatAmount(rule.getRebate()));
			promoType5RulePromoProductsTable.setRule(rule);
			addType5PromoProductButton.setEnabled(true);
			removeType5PromoProductButton.setEnabled(true);
			addAllType5PromoProductButton.setEnabled(true);
			removeType5PromoProductButton.setEnabled(true);
		} else {
			targetAmountField.setText(null);
			rebateField.setText(null);
			promoType5RulePromoProductsTable.clear();
			addType5PromoProductButton.setEnabled(false);
			removeType5PromoProductButton.setEnabled(false);
			addAllType5PromoProductButton.setEnabled(false);
			removeAllType5PromoProductButton.setEnabled(false);
		}
	}

	private void updateToPromoType5Panel(JPanel panel) {
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Target Amount: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		targetAmountField.setPreferredSize(new Dimension(100, 25));
		panel.add(targetAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Rebate: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		rebateField.setPreferredSize(new Dimension(100, 25));
		panel.add(rebateField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 2;
		panel.add(createPromoType5RulePromoProductsTableToolBar(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 2;
		panel.add(ComponentUtil.createScrollPane(promoType5RulePromoProductsTable, 600, 100), c);
	}

	private JPanel createPromoType5RulePromoProductsTableToolBar() {
		JPanel panel = new JPanel();
		
		addType5PromoProductButton = new MagicToolBarButton("plus_small", "Add Promo Product", true);
		addType5PromoProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addType5PromoProduct();
			}
		});
		panel.add(addType5PromoProductButton, BorderLayout.WEST);
		
		removeType5PromoProductButton = new MagicToolBarButton("minus_small", "Remove Promo Product", true);
		removeType5PromoProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeType5PromoProduct();
			}
		});
		panel.add(removeType5PromoProductButton, BorderLayout.WEST);
		
		addAllType5PromoProductButton = new MagicToolBarButton("add_all_small", "Add All Promo Product", true);
		addAllType5PromoProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addAllType5PromoProduct();
			}
		});
		panel.add(addAllType5PromoProductButton, BorderLayout.WEST);
		
		removeAllType5PromoProductButton = new MagicToolBarButton("delete_all_small", "Remove All Promo Products", true);
		removeAllType5PromoProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeAllType5PromoProduct();
			}
		});
		panel.add(removeAllType5PromoProductButton, BorderLayout.WEST);
		
		return panel;
	}

	private void removeAllType5PromoProduct() {
		if (confirm("Remove all promo products?")) {
			try {
				promoService.removeAllPromoProducts(promo.getPromoType5Rule());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
			}
			updateDisplay(promo);
		}
	}

	private void addAllType5PromoProduct() {
		if (confirm("Add all products to promo?")) {
			try {
				promoService.addAllPromoProducts(promo.getPromoType5Rule());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
				return;
			}
			updateDisplay(promo);
		}
	}

	private void removeType5PromoProduct() {
		promoType5RulePromoProductsTable.removeCurrentlySelectedPromoProduct();
	}

	private void addType5PromoProduct() {
		promoType5RulePromoProductsTable.addNewRow();
	}

	private void updateDisplayForPromoType2() {
		updateToPromoType2Panel(promoDetailsPanel);
		promoType2RulesTable.setPromo(promo);
	}

	private void updateDisplayForPromoType3() {
		updateToPromoType3Panel(promoDetailsPanel);
		
		PromoType3Rule rule = promo.getPromoType3Rule();
		if (rule != null) {
			targetAmountField.setText(FormatterUtil.formatAmount(rule.getTargetAmount()));
			freeProductCodeField.setText(rule.getFreeProduct().getCode());
			freeProductDescriptionLabel.setText(rule.getFreeProduct().getDescription());
			freeUnitComboBox.setSelectedItem(rule.getFreeUnit());
			freeQuantityField.setText(rule.getFreeQuantity().toString());
			dailyRedeemLimitPerCustomerField.setText(String.valueOf(rule.getDailyRedeemLimitPerCustomer()));
			promoType3RulePromoProductsTable.setRule(rule);
			addType3PromoProductButton.setEnabled(true);
			removeType3PromoProductButton.setEnabled(true);
			addAllType3PromoProductButton.setEnabled(true);
			removeType3PromoProductButton.setEnabled(true);
		} else {
			targetAmountField.setText(null);
			freeProductCodeField.setText(null);
			freeProductDescriptionLabel.setText(null);
			freeUnitComboBox.setSelectedItem(null);
			freeQuantityField.setText(null);
			dailyRedeemLimitPerCustomerField.setText(null);
			promoType3RulePromoProductsTable.clear();
			addType3PromoProductButton.setEnabled(false);
			removeType3PromoProductButton.setEnabled(false);
			addAllType3PromoProductButton.setEnabled(false);
			removeAllType3PromoProductButton.setEnabled(false);
		}
	}

	private void updatePricingSchemeComboBox() {
		DefaultComboBoxModel<PricingScheme> model = ListUtil.toDefaultComboBoxModel(pricingSchemeService.getAllPricingSchemes());
		model.insertElementAt(new PricingScheme() {
			
			@Override
			public String toString() {
				return "ANY";
			}
			
		}, 0);
		pricingSchemeComboBox.setModel(model);
	}

	private void updateDisplayForPromoType1() {
		List<Manufacturer> manufacturers = manufacturerService.getAllManufacturers();
		manufacturerComboBox.setModel(ListUtil.toDefaultComboBoxModel(manufacturers, true));
		
		updateToPromoType1Panel(promoDetailsPanel);
		
		PromoType1Rule rule = promo.getPromoType1Rule();
		if (rule != null) {
			manufacturerComboBox.setSelectedItem(rule.getManufacturer());
			targetAmountField.setText(FormatterUtil.formatAmount(rule.getTargetAmount()));
			freeProductCodeField.setText(rule.getProduct().getCode());
			freeProductDescriptionLabel.setText(rule.getProduct().getDescription());
			freeUnitComboBox.setSelectedItem(rule.getUnit());
			freeQuantityField.setText(rule.getQuantity().toString());
			dailyRedeemLimitPerCustomerField.setText(String.valueOf(rule.getDailyRedeemLimitPerCustomer()));
		} else {
			manufacturerComboBox.setSelectedIndex(0);
			targetAmountField.setText(null);
			freeProductCodeField.setText(null);
			freeProductDescriptionLabel.setText(null);
			freeUnitComboBox.setSelectedItem(null);
			freeQuantityField.setText(null);
			dailyRedeemLimitPerCustomerField.setText(null);
		}
	}

	private void clearDisplay() {
		promoNumberLabel.setText(null);
		nameField.setText(null);
		promoTypeComboBox.setEnabled(true);
		promoTypeComboBox.setSelectedIndex(0);
		startDateModel.setValue(Calendar.getInstance());
		pricingSchemeComboBox.setSelectedIndex(0);
		activeCheckBox.setSelected(true);
		
		promoDetailsPanel.setVisible(false);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPromoListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

}