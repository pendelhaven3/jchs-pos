package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.SalesInvoice;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class SalesInvoiceStatusDialog extends MagicDialog {

	private JLabel postDateLabel = new JLabel();
	private JLabel postedByLabel = new JLabel();
	private JLabel markDateLabel = new JLabel();
	private JLabel markedByLabel = new JLabel();
	private JLabel cancelDateLabel = new JLabel();
	private JLabel cancelledByLabel = new JLabel();
	
	public SalesInvoiceStatusDialog() {
		setSize(320, 240);
		setLocationRelativeTo(null);
		setTitle("Sales Invoice Status");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
		layoutComponents();
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

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Mark Date:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		markDateLabel.setPreferredSize(new Dimension(150, 20));
		add(markDateLabel, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Marked By:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		markedByLabel.setPreferredSize(new Dimension(150, 20));
		add(markedByLabel, c);
		
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
	
	public void updateDisplay(SalesInvoice salesInvoice) {
		postDateLabel.setText(FormatterUtil.formatDateTime(salesInvoice.getPostDate()));
		postedByLabel.setText(salesInvoice.getPostedBy().getUsername());
		markDateLabel.setText(salesInvoice.isMarked() ? FormatterUtil.formatDate(salesInvoice.getMarkDate()) : "-");
		markedByLabel.setText(salesInvoice.isMarked() ? salesInvoice.getMarkedBy().getUsername() : "-");
		cancelDateLabel.setText(salesInvoice.isCancelled() ? FormatterUtil.formatDateTime(salesInvoice.getCancelDate()) : "-");
		cancelledByLabel.setText(salesInvoice.isCancelled() ? salesInvoice.getCancelledBy().getUsername() : "-");
	}

}