package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.tables.ProductCanvassTable;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductCanvassItem;
import com.pj.magic.model.search.ProductCanvassSearchCriteria;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.ReceivingReceiptService;
import com.pj.magic.util.ComponentUtil;

@Component
public class ProductCanvassPanel extends StandardMagicPanel {

	@Autowired private ProductService productService;
	@Autowired private SelectProductDialog selectProductDialog;
	@Autowired private ProductCanvassTable table;
	@Autowired private ReceivingReceiptService receivingReceiptService;
	
	private MagicTextField productCodeField;
	private UtilCalendarModel dateFromModel;
	private UtilCalendarModel dateToModel;
	private JLabel productDescriptionLabel;
	private JButton generateButton;
	private EllipsisButton selectProductButton;
	
	@Override
	protected void initializeComponents() {
		productCodeField = new MagicTextField();
		productCodeField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		
		dateFromModel = new UtilCalendarModel();
		dateToModel = new UtilCalendarModel();
		
		selectProductButton = new EllipsisButton();
		selectProductButton.setToolTipText("Select Product");
		selectProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectProductDialog();
			}
		});
		
		productDescriptionLabel = new JLabel();
		
		generateButton = new JButton("Generate");
		generateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateProductCanvass();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(productCodeField);
	}

	protected void openSelectProductDialog() {
		selectProductDialog.searchProducts(productCodeField.getText());
		selectProductDialog.setVisible(true);
		
		Product product = selectProductDialog.getSelectedProduct();
		if (product != null) {
			productCodeField.setText(product.getCode());
			productDescriptionLabel.setText(product.getDescription());
		}
	}

	protected void generateProductCanvass() {
		String productCode = productCodeField.getText();
		if (StringUtils.isEmpty(productCode)) {
			showErrorMessage("Product Code must be specified");
			return;
		}
		
		Product product = productService.findProductByCode(productCode);
		if (product == null) {
			showErrorMessage("No product matching code specified");
			return;
		}
		
		ProductCanvassSearchCriteria criteria = new ProductCanvassSearchCriteria();
		criteria.setProduct(product);

		Calendar dateFrom = dateFromModel.getValue();
		if (dateFrom != null) {
			criteria.setDateFrom(dateFrom.getTime());
		}
		
		Calendar dateTo = dateToModel.getValue();
		if (dateTo != null) {
			criteria.setDateTo(dateTo.getTime());
		}
		
		List<ProductCanvassItem> items = receivingReceiptService.getProductCanvass(criteria);
		table.setItems(items);
		if (items.isEmpty()) {
			showErrorMessage("No records found");
		}
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
		mainPanel.add(ComponentUtil.createLabel(120, "Product Code: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createProductPanel(), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0; // right space filler
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Date From: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePickerImpl dateFromPicker = new JDatePickerImpl(new JDatePanelImpl(dateFromModel), new DatePickerFormatter());
		mainPanel.add(dateFromPicker, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Date To: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePickerImpl dateToPicker = new JDatePickerImpl(new JDatePanelImpl(dateToModel), new DatePickerFormatter());
		mainPanel.add(dateToPicker, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
		generateButton.setPreferredSize(new Dimension(160, 25));
		mainPanel.add(generateButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 30), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(table);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);
	}

	private JPanel createProductPanel() {
		productCodeField.setPreferredSize(new Dimension(150, 25));
		productDescriptionLabel.setPreferredSize(new Dimension(300, 20));
		
		JPanel panel = new JPanel();
		panel.add(productCodeField);
		panel.add(selectProductButton);
		panel.add(productDescriptionLabel);
		return panel;
	}

	@Override
	protected void registerKeyBindings() {
		productCodeField.onF5Key(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectProductDialog();
			}
			
		});
	}

	public void updateDisplay() {
		productCodeField.setText(null);
		productDescriptionLabel.setText(null);
		dateFromModel.setValue(null);
		dateToModel.setValue(null);
		table.setItems(new ArrayList<ProductCanvassItem>());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPurchasesMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}
