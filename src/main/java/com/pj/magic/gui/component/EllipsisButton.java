package com.pj.magic.gui.component;

import java.awt.Dimension;

import javax.swing.JButton;

public class EllipsisButton extends JButton {

	public EllipsisButton() {
		setText("...");
		setPreferredSize(new Dimension(30, 24));
	}
	
	public EllipsisButton(String toolTipText) {
		this();
		setToolTipText(toolTipText);
	}
	
}