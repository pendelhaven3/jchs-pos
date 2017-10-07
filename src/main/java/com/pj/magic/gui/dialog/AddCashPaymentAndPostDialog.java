package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.AlreadyCancelledException;
import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.AmountGivenLessThanRemainingAmountDueException;
import com.pj.magic.exception.UserNotAssignedToPaymentTerminalException;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.Payment;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PaymentService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class AddCashPaymentAndPostDialog extends MagicDialog {

	private static final Logger logger = LoggerFactory.getLogger(AddCashPaymentAndPostDialog.class);
	
	@Autowired private PaymentService paymentService;
	@Autowired private LoginService loginService;
	
	private JLabel remainingAmountDueLabel;
	private MagicTextField amountGivenField;
	private JButton postButton;
	private JButton cancelButton;
	
	private Payment payment;
	
	public AddCashPaymentAndPostDialog() {
		setSize(500, 200);
		setLocationRelativeTo(null);
		setTitle("Add Cash Payment And Post");
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		layoutComponents();
	}

	private void initializeComponents() {
		amountGivenField = new MagicTextField();
		
		postButton = new JButton("Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addCashPaymentAndPost();
			}
		});
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
	}

	private void addCashPaymentAndPost() {
		if (!validateFields()) {
			return;
		}
		
		if (confirm("Add cash payment and post?")) {
			if (!amountGivenField.getText().isEmpty()) {
				payment.setCashAmountGiven(NumberUtil.toBigDecimal(amountGivenField.getText()));
			} else {
				payment.setCashAmountGiven(null);
			}
			try {
				paymentService.addCashPaymentAndPost(payment);
			} catch (UserNotAssignedToPaymentTerminalException e) {
				showErrorMessage("User " + loginService.getLoggedInUser().getUsername()
						+ " is not assigned to payment terminal");
				return;
			} catch (AlreadyPostedException e) {
				showErrorMessage("Payment is already posted");
				return;
			} catch (AlreadyCancelledException e) {
				showErrorMessage("Payment is already cancelled");
				return;
			} catch (AmountGivenLessThanRemainingAmountDueException e) {
				showErrorMessage("Amount Given is less than remaining amount due");
				return;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showUnexpectedErrorMessage();
				return;
			}
			showMessage("Payment posted!");
			setVisible(false);
		}
	}

	private boolean validateFields() {
		if (amountGivenField.getText().isEmpty()) {
			return true;
		}
		
		if (!NumberUtil.isAmount(amountGivenField.getText())) {
			showErrorMessage("Amount Given must be a valid amount");
			amountGivenField.requestFocus();
			return false;
		}
		
		if (isAmountGivenLessThanRemainingAmountDue()) {
			showErrorMessage("Amount Given must be greater than or equal to Remaining Amount Due");
			amountGivenField.requestFocus();
			return false;
		}
		
		return true;
	}

	private boolean isAmountGivenLessThanRemainingAmountDue() {
		return NumberUtil.toBigDecimal(amountGivenField.getText())
				.compareTo(payment.getTotalAmountDueMinusNonCashPaymentsAndAdjustments()) < 0;
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
		add(Box.createVerticalStrut(5), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createHorizontalStrut(30), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(200, "Remaining Amount Due:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remainingAmountDueLabel = ComponentUtil.createLabel(100);
		add(remainingAmountDueLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "Amount Given:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		amountGivenField.setPreferredSize(new Dimension(100, 25));
		add(amountGivenField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(250, "Leave blank for exact amount"), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createVerticalStrut(10), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		add(ComponentUtil.createGenericPanel(
				postButton,
				Box.createHorizontalStrut(5),
				cancelButton), c);
	}
	
	public void updateDisplay(Payment payment) {
		this.payment = payment;
		remainingAmountDueLabel.setText(FormatterUtil.formatAmount(
				payment.getTotalAmountDueMinusNonCashPaymentsAndAdjustments()));
		amountGivenField.setText(null);
		amountGivenField.requestFocusInWindow();
	}

}
