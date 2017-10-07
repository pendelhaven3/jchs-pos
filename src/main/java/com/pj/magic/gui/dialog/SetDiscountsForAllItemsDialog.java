package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pj.magic.exception.SellingPriceLessThanCostException;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.service.ReceivingReceiptService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class SetDiscountsForAllItemsDialog extends MagicDialog {

	private static final Logger logger = LoggerFactory.getLogger(SetDiscountsForAllItemsDialog.class);
	
	@Autowired private ReceivingReceiptService receivingReceiptService;
	@Autowired private SalesInvoiceService salesInvoiceService;
	
	private MagicTextField discount1Field;
	private MagicTextField discount2Field;
	private MagicTextField discount3Field;
	private JButton saveButton;
	
	private ReceivingReceipt receivingReceipt;
	private SalesInvoice salesInvoice;
	
	public SetDiscountsForAllItemsDialog() {
		setSize(400, 220);
		setLocationRelativeTo(null);
		setTitle("Set Discounts For All Items");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		
		initializeComponents();
		layoutComponents();
	}

	private void initializeComponents() {
		discount1Field = new MagicTextField();
		discount2Field = new MagicTextField();
		discount3Field = new MagicTextField();
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setDiscountsForAllItems();
			}
		});
	}

	protected void setDiscountsForAllItems() {
		if (!validateFields()) {
			return;
		}
		
		if (confirm("Set discounts for all items?")) {
			try {
				if (receivingReceipt != null) {
					receivingReceiptService.setAllItemDiscounts(receivingReceipt, 
							getDiscount1(), getDiscount2(), getDiscount3());
				} else if (salesInvoice != null) {
					salesInvoiceService.setAllItemDiscounts(salesInvoice,
							getDiscount1(), getDiscount2(), getDiscount3());
				}
			} catch (SellingPriceLessThanCostException e) {
				showErrorMessage("Selling price less than cost for: " + e.getItem().getProduct().getDescription());
				return;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showUnexpectedErrorMessage();
				return;
			}
			
			showMessage("Discounts saved");
			setVisible(false);
		}
	}

	private BigDecimal getDiscount1() {
		if (!StringUtils.isEmpty(discount1Field.getText())) {
			return NumberUtil.toBigDecimal(discount1Field.getText());
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	private BigDecimal getDiscount2() {
		if (!StringUtils.isEmpty(discount2Field.getText())) {
			return NumberUtil.toBigDecimal(discount2Field.getText());
		} else {
			return BigDecimal.ZERO;
		}
	}

	private BigDecimal getDiscount3() {
		if (!StringUtils.isEmpty(discount3Field.getText())) {
			return NumberUtil.toBigDecimal(discount3Field.getText());
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	private boolean validateFields() {
		if (isDiscount1Specified() && isDiscount1NotAValidDiscount()) {
			showErrorMessage("Discount 1 must be a valid discount");
			discount1Field.requestFocus();
			return false;
		}
		
		if (isDiscount2Specified() && isDiscount2NotAValidDiscount()) {
			showErrorMessage("Discount 2 must be a valid discount");
			discount2Field.requestFocus();
			return false;
		}
		
		if (isDiscount3Specified() && isDiscount3NotAValidDiscount()) {
			showErrorMessage("Discount 3 must be a valid discount");
			discount3Field.requestFocus();
			return false;
		}
		
		return true;
	}

	private boolean isDiscount1Specified() {
		return !StringUtils.isEmpty(discount1Field.getText());
	}

	private boolean isDiscount1NotAValidDiscount() {
		return !NumberUtil.isAmount(discount1Field.getText());
	}

	private boolean isDiscount2Specified() {
		return !StringUtils.isEmpty(discount2Field.getText());
	}

	private boolean isDiscount2NotAValidDiscount() {
		return !NumberUtil.isAmount(discount2Field.getText());
	}

	private boolean isDiscount3Specified() {
		return !StringUtils.isEmpty(discount3Field.getText());
	}

	private boolean isDiscount3NotAValidDiscount() {
		return !NumberUtil.isAmount(discount3Field.getText());
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(130, "Discount 1:"), c);

		c = new GridBagConstraints();
		c.weightx = 0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		discount1Field.setPreferredSize(new Dimension(100, 25));
		add(discount1Field, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(130, "Discount 2:"), c);

		c = new GridBagConstraints();
		c.weightx = 0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		discount2Field.setPreferredSize(new Dimension(100, 25));
		add(discount2Field, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(130, "Discount 3:"), c);

		c = new GridBagConstraints();
		c.weightx = 0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		discount3Field.setPreferredSize(new Dimension(100, 25));
		add(discount3Field, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 2;
		saveButton.setPreferredSize(new Dimension(100, 25));
		add(saveButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createGlue(), c);
	}

	public void updateDisplay(ReceivingReceipt receivingReceipt) {
		this.receivingReceipt = receivingReceipt;
		salesInvoice = null;
		updateDisplay();
	}

	public void updateDisplay(SalesInvoice salesInvoice) {
		this.salesInvoice = salesInvoice;
		receivingReceipt = null;
		updateDisplay();
	}
	
	private void updateDisplay() {
		discount1Field.setText(null);
		discount2Field.setText(null);
		discount3Field.setText(null);
		discount1Field.requestFocus();
	}

}
