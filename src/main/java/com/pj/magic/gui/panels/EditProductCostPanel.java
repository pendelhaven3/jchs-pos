package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.model.Product2;
import com.pj.magic.model.Unit;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.Product2Service;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EditProductCostPanel extends StandardMagicPanel {

	@Autowired
	private Product2Service product2Service;
	
	@Autowired
	private LoginService loginService;
	
	private Product2 product;
	
	private JLabel idLabel = new JLabel();
	private JLabel descriptionLabel = new JLabel();
	private JCheckBox caseUnitIndicatorCheckBox = new JCheckBox("CASE");
	private JCheckBox tiesUnitIndicatorCheckBox = new JCheckBox("TIES");
	private JCheckBox packUnitIndicatorCheckBox = new JCheckBox("PACK");
	private JCheckBox hdznUnitIndicatorCheckBox = new JCheckBox("HDZN");
	private JCheckBox pcsUnitIndicatorCheckBox = new JCheckBox("PCS");
	private MagicTextField grossCostCaseTextField = new MagicTextField();
	private MagicTextField grossCostTiesTextField = new MagicTextField();
	private MagicTextField grossCostPackTextField = new MagicTextField();
	private MagicTextField grossCostHdznTextField = new MagicTextField();
	private MagicTextField grossCostPcsTextField = new MagicTextField();
	private MagicTextField finalCostCaseTextField = new MagicTextField();
	private MagicTextField finalCostTiesTextField = new MagicTextField();
	private MagicTextField finalCostPackTextField = new MagicTextField();
	private MagicTextField finalCostHdznTextField = new MagicTextField();
	private MagicTextField finalCostPcsTextField = new MagicTextField();
	private JButton saveButton;
	
	@Override
	public String getTitle() {
		return "Edit Product Cost";
	}
	
	@Override
	protected void initializeComponents() {
		saveButton = new JButton("Save");
		saveButton.addActionListener(e -> saveProductCosts());
		
		caseUnitIndicatorCheckBox.addItemListener(e -> caseUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.CASE)));
		tiesUnitIndicatorCheckBox.addItemListener(e -> tiesUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.TIES)));
		packUnitIndicatorCheckBox.addItemListener(e -> packUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.PACK)));
		hdznUnitIndicatorCheckBox.addItemListener(e -> hdznUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.HDZN)));
		pcsUnitIndicatorCheckBox.addItemListener(e -> pcsUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.PIECES)));
		
		focusOnComponentWhenThisPanelIsDisplayed(grossCostCaseTextField);
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(grossCostCaseTextField);
		focusOrder.add(grossCostTiesTextField);
		focusOrder.add(grossCostPackTextField);
		focusOrder.add(grossCostHdznTextField);
		focusOrder.add(grossCostPcsTextField);
		focusOrder.add(finalCostCaseTextField);
		focusOrder.add(finalCostTiesTextField);
		focusOrder.add(finalCostPackTextField);
		focusOrder.add(finalCostHdznTextField);
		focusOrder.add(finalCostPcsTextField);
		focusOrder.add(saveButton);
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets.left = 50;
		c.gridx = 0;
		c.gridy = currentRow;
		c.weightx = c.weighty = 0.0;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(125, "Product ID:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		idLabel.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(idLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.left = 50;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(125, "Description:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		descriptionLabel.setPreferredSize(new Dimension(400, 25));
		mainPanel.add(descriptionLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 20;
		c.gridx = 1;
		c.gridy = currentRow;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.VERTICAL;
		mainPanel.add(createCostsPanel(), c);		
	}

	private JPanel createCostsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createCenterLabel(90, "Units"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createCenterLabel(150, "Gross Cost"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createCenterLabel(130, "Final Cost"), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(caseUnitIndicatorCheckBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		grossCostCaseTextField.setPreferredSize(new Dimension(100,  25));
		panel.add(grossCostCaseTextField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		finalCostCaseTextField.setPreferredSize(new Dimension(100,  25));
		panel.add(finalCostCaseTextField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(tiesUnitIndicatorCheckBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		grossCostTiesTextField.setPreferredSize(new Dimension(100,  25));
		panel.add(grossCostTiesTextField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		finalCostTiesTextField.setPreferredSize(new Dimension(100,  25));
		panel.add(finalCostTiesTextField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(packUnitIndicatorCheckBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		grossCostPackTextField.setPreferredSize(new Dimension(100,  25));
		panel.add(grossCostPackTextField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		finalCostPackTextField.setPreferredSize(new Dimension(100,  25));
		panel.add(finalCostPackTextField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(hdznUnitIndicatorCheckBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		grossCostHdznTextField.setPreferredSize(new Dimension(100,  25));
		panel.add(grossCostHdznTextField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		finalCostHdznTextField.setPreferredSize(new Dimension(100,  25));
		panel.add(finalCostHdznTextField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(pcsUnitIndicatorCheckBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		grossCostPcsTextField.setPreferredSize(new Dimension(100,  25));
		panel.add(grossCostPcsTextField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		finalCostPcsTextField.setPreferredSize(new Dimension(100,  25));
		panel.add(finalCostPcsTextField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 15;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		saveButton.setPreferredSize(new Dimension(100, 25));
		panel.add(saveButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.VERTICAL;
		panel.add(Box.createGlue(), c);
		
		return panel;
	}

	public void updateDisplay(Product2 product) {
		this.product = product = product2Service.getProduct(product.getId());
		
		idLabel.setText(String.valueOf(product.getId()));
		descriptionLabel.setText(product.getDescription());
		
		caseUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.CASE));
		tiesUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.TIES));
		packUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.PACK));
		hdznUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.HDZN));
		pcsUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.PIECES));
		
		grossCostCaseTextField.setText(FormatterUtil.formatAmount(product.getGrossCost(Unit.CASE)));
		grossCostTiesTextField.setText(FormatterUtil.formatAmount(product.getGrossCost(Unit.TIES)));
		grossCostPackTextField.setText(FormatterUtil.formatAmount(product.getGrossCost(Unit.PACK)));
		grossCostHdznTextField.setText(FormatterUtil.formatAmount(product.getGrossCost(Unit.HDZN)));
		grossCostPcsTextField.setText(FormatterUtil.formatAmount(product.getGrossCost(Unit.PIECES)));
		finalCostCaseTextField.setText(FormatterUtil.formatAmount(product.getFinalCost(Unit.CASE)));
		finalCostTiesTextField.setText(FormatterUtil.formatAmount(product.getFinalCost(Unit.TIES)));
		finalCostPackTextField.setText(FormatterUtil.formatAmount(product.getFinalCost(Unit.PACK)));
		finalCostHdznTextField.setText(FormatterUtil.formatAmount(product.getFinalCost(Unit.HDZN)));
		finalCostPcsTextField.setText(FormatterUtil.formatAmount(product.getFinalCost(Unit.PIECES)));
		
		saveButton.setEnabled(product.isActive() 
				&& Arrays.asList("ADMIN", "JOY", "IRENE").contains(loginService.getLoggedInUser().getUsername()));
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToProductCostListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

	@Override
	protected void registerKeyBindings() {
	}
	
	private void saveProductCosts() {
		if (!validateFields()) {
			return;
		}
		
		try {
			product.setGrossCost(Unit.CASE, NumberUtil.toBigDecimal(grossCostCaseTextField.getText()));
			product.setGrossCost(Unit.TIES, NumberUtil.toBigDecimal(grossCostTiesTextField.getText()));
			product.setGrossCost(Unit.PACK, NumberUtil.toBigDecimal(grossCostPackTextField.getText()));
			product.setGrossCost(Unit.HDZN, NumberUtil.toBigDecimal(grossCostHdznTextField.getText()));
			product.setGrossCost(Unit.PIECES, NumberUtil.toBigDecimal(grossCostPcsTextField.getText()));
			product.setFinalCost(Unit.CASE, NumberUtil.toBigDecimal(finalCostCaseTextField.getText()));
			product.setFinalCost(Unit.TIES, NumberUtil.toBigDecimal(finalCostTiesTextField.getText()));
			product.setFinalCost(Unit.PACK, NumberUtil.toBigDecimal(finalCostPackTextField.getText()));
			product.setFinalCost(Unit.HDZN, NumberUtil.toBigDecimal(finalCostHdznTextField.getText()));
			product.setFinalCost(Unit.PIECES, NumberUtil.toBigDecimal(finalCostPcsTextField.getText()));
			product2Service.updateCosts(product);
			showMessage("Saved");
			updateDisplay(product);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			showErrorMessage("Error occurred during saving: " + e.getMessage());
		}
	}

	private boolean validateFields() {		
		if (!NumberUtil.isAmount(grossCostCaseTextField.getText())) {
			showErrorMessage("Gross Cost CASE must be a valid amount");
			return false;
		}
		if (!NumberUtil.isAmount(grossCostTiesTextField.getText())) {
			showErrorMessage("Gross Cost TIES must be a valid amount");
			return false;
		}
		if (!NumberUtil.isAmount(grossCostPackTextField.getText())) {
			showErrorMessage("Gross Cost PACK must be a valid amount");
			return false;
		}
		if (!NumberUtil.isAmount(grossCostHdznTextField.getText())) {
			showErrorMessage("Gross Cost HDZN must be a valid amount");
			return false;
		}
		if (!NumberUtil.isAmount(grossCostPcsTextField.getText())) {
			showErrorMessage("Gross Cost PCS must be a valid amount");
			return false;
		}

		if (!NumberUtil.isAmount(finalCostCaseTextField.getText())) {
			showErrorMessage("Final Cost CASE must be a valid amount");
			return false;
		}
		if (!NumberUtil.isAmount(finalCostTiesTextField.getText())) {
			showErrorMessage("Final Cost TIES must be a valid amount");
			return false;
		}
		if (!NumberUtil.isAmount(finalCostPackTextField.getText())) {
			showErrorMessage("Final Cost PACK must be a valid amount");
			return false;
		}
		if (!NumberUtil.isAmount(finalCostHdznTextField.getText())) {
			showErrorMessage("Final Cost HDZN must be a valid amount");
			return false;
		}
		if (!NumberUtil.isAmount(finalCostPcsTextField.getText())) {
			showErrorMessage("Final Cost PCS must be a valid amount");
			return false;
		}
		
		return true;
	}

}
