package com.pj.magic.gui.dialog;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.stereotype.Component;

@Component
public class WaitDialog extends JDialog {

	public WaitDialog() {
		setModal(true);
		setSize(200, 150);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		layoutComponents();
	}

	private void layoutComponents() {
		int currentRow = 0;
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = currentRow;
		c.insets = new Insets(10, 10, 10, 10);
		centerPanel.add(new JLabel("Restoring data..."), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridy = currentRow;
		c.insets = new Insets(10, 10, 10, 10);
		centerPanel.add(new JLabel(new ImageIcon(getClass().getResource("/images/loading.gif"))), c);
		
		// ===
		
		setLayout(new BorderLayout());
		add(centerPanel, BorderLayout.CENTER);
	}
	
}