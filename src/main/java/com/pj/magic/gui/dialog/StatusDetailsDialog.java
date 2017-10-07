package com.pj.magic.gui.dialog;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.Payment;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class StatusDetailsDialog extends MagicDialog {

	private JLabel createDateLabel = new JLabel();
	private JLabel createdByLabel = new JLabel();
	private JLabel postDateLabel = new JLabel();
	private JLabel postedByLabel = new JLabel();
	private JLabel cancelDateLabel = new JLabel();
	private JLabel cancelledByLabel = new JLabel();
	private JLabel paidDateLabel = new JLabel();
	private JLabel paidByLabel = new JLabel();
	private boolean showCreateFields;
	private boolean showPaidFields;
	private boolean showCancelFields;
	
	public StatusDetailsDialog() {
		setLocationRelativeTo(null);
		getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		initializeComponents();
	}

	private void initializeComponents() {
		// always get focus within the dialog so escape key binding can be used
		focusOnComponentWhenThisPanelIsDisplayed(getRootPane()); 
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
	}
	
	private void layoutMainPanel() {
		Container mainPanel = getContentPane();
		mainPanel.removeAll();
		
		setLayout(new GridBagLayout());
		int currentRow = 0;
		
		if (showCreateFields) {
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(ComponentUtil.createLabel(120, "Create Date:"), c);

			c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(createDateLabel, c);
			
			currentRow++;
			
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(ComponentUtil.createLabel(120, "Created By:"), c);

			c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(createdByLabel, c);
			
			currentRow++;
		}

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Post Date:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postDateLabel.setPreferredSize(new Dimension(150, 20));
		add(postDateLabel, c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Posted By:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postedByLabel.setPreferredSize(new Dimension(150, 20));
		add(postedByLabel, c);
		
		if (showPaidFields) {
			currentRow++;
			
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(ComponentUtil.createLabel(120, "Paid Date:"), c);

			c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(paidDateLabel, c);
			
			currentRow++;
			
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(ComponentUtil.createLabel(120, "Paid By:"), c);

			c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(paidByLabel, c);
		}
		
		if (showCancelFields) {
			currentRow++;
			
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(ComponentUtil.createLabel(120, "Cancel Date:"), c);

			c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			cancelDateLabel.setPreferredSize(new Dimension(150, 20));
			add(cancelDateLabel, c);
			
			currentRow++;
			
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(ComponentUtil.createLabel(120, "Cancelled By:"), c);

			c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			cancelledByLabel.setPreferredSize(new Dimension(150, 20));
			add(cancelledByLabel, c);
		}
		
		mainPanel.repaint();
		mainPanel.revalidate();
		
		showCreateFields = false;
		showPaidFields = false;
		showCancelFields = false;
	}
	
	public void updateDisplay(ReceivingReceipt receivingReceipt) {
		setSize(320, 180);
		setTitle("Receiving Receipt Status");
		showPaidFields = false;
		showCancelFields = true;
		layoutMainPanel();
		
		postDateLabel.setText(receivingReceipt.isPosted() ? 
				FormatterUtil.formatDateTime(receivingReceipt.getPostDate()) : "-");
		postedByLabel.setText(receivingReceipt.isPosted() ? 
				receivingReceipt.getPostedBy().getUsername() : "-");
		cancelDateLabel.setText(receivingReceipt.isCancelled() ? 
				FormatterUtil.formatDate(receivingReceipt.getCancelDate()) : "-");
		cancelledByLabel.setText(receivingReceipt.isCancelled() ? 
				receivingReceipt.getCancelledBy().getUsername() : "-");
	}

	public void updateDisplay(SalesReturn salesReturn) {
		setSize(320, 240);
		setTitle("Sales Return Status");
		showPaidFields = true;
		showCancelFields = true;
		layoutMainPanel();
		
		postDateLabel.setText(salesReturn.isPosted() ? 
				FormatterUtil.formatDateTime(salesReturn.getPostDate()) : "-");
		postedByLabel.setText(salesReturn.isPosted() ? 
				salesReturn.getPostedBy().getUsername() : "-");
		paidDateLabel.setText(salesReturn.isPaid() ? 
				FormatterUtil.formatDateTime(salesReturn.getPaidDate()) : "-");
		paidByLabel.setText(salesReturn.isPaid() ? 
				salesReturn.getPaidBy().getUsername() : "-");
		cancelDateLabel.setText(salesReturn.isCancelled() ? 
				FormatterUtil.formatDateTime(salesReturn.getCancelDate()) : "-");
		cancelledByLabel.setText(salesReturn.isCancelled() ? 
				salesReturn.getCancelledBy().getUsername() : "-");
	}

	public void updateDisplay(BadStockReturn badStockReturn) {
		setSize(320, 240);
		setLocationRelativeTo(null);
		setTitle("Bad Stock Return Status");
		showPaidFields = true;
		showCancelFields = true;
		layoutMainPanel();
		
		postDateLabel.setText(badStockReturn.isPosted() ? 
				FormatterUtil.formatDateTime(badStockReturn.getPostDate()) : "-");
		postedByLabel.setText(badStockReturn.isPosted() ? 
				badStockReturn.getPostedBy().getUsername() : "-");
		paidDateLabel.setText(badStockReturn.isPaid() ? 
				FormatterUtil.formatDateTime(badStockReturn.getPaidDate()) : "-");
		paidByLabel.setText(badStockReturn.isPaid() ? 
				badStockReturn.getPaidBy().getUsername() : "-");
		cancelDateLabel.setText(badStockReturn.isCancelled() ? 
				FormatterUtil.formatDateTime(badStockReturn.getCancelDate()) : "-");
		cancelledByLabel.setText(badStockReturn.isCancelled() ? 
				badStockReturn.getCancelledBy().getUsername() : "-");
	}

	public void updateDisplay(NoMoreStockAdjustment noMoreStockAdjustment) {
		setSize(320, 180);
		setTitle("No More Stock Adjustment Status");
		showPaidFields = true;
		showCancelFields = false;
		layoutMainPanel();
		
		postDateLabel.setText(noMoreStockAdjustment.isPosted() ? 
				FormatterUtil.formatDateTime(noMoreStockAdjustment.getPostDate()) : "-");
		postedByLabel.setText(noMoreStockAdjustment.isPosted() ? 
				noMoreStockAdjustment.getPostedBy().getUsername() : "-");
		paidDateLabel.setText(noMoreStockAdjustment.isPaid() ? 
				FormatterUtil.formatDateTime(noMoreStockAdjustment.getPaidDate()) : "-");
		paidByLabel.setText(noMoreStockAdjustment.isPaid() ? 
				noMoreStockAdjustment.getPaidBy().getUsername() : "-");
	}

	public void updateDisplay(Payment payment) {
		setSize(320, 240);
		setLocationRelativeTo(null);
		setTitle("Payment Status");
		showCreateFields = true;
		showCancelFields = true;
		layoutMainPanel();
		
		createDateLabel.setText(FormatterUtil.formatDateTime(payment.getCreateDate()));
		createdByLabel.setText(payment.getEncoder().getUsername());
		postDateLabel.setText(payment.isPosted() ? 
				FormatterUtil.formatDateTime(payment.getPostDate()) : "-");
		postedByLabel.setText(payment.isPosted() ? 
				payment.getPostedBy().getUsername() : "-");
		cancelDateLabel.setText(payment.isCancelled() ? 
				FormatterUtil.formatDateTime(payment.getCancelDate()) : "-");
		cancelledByLabel.setText(payment.isCancelled() ? 
				payment.getCancelledBy().getUsername() : "-");
	}

}