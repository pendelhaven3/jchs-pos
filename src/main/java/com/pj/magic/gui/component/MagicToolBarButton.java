package com.pj.magic.gui.component;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class MagicToolBarButton extends JButton {

	public MagicToolBarButton(String imageName, String toolTipText) {
		this(imageName, toolTipText, false);
	}
	
	public MagicToolBarButton(String imageName, String toolTipText, boolean smallSize) {
		if (smallSize) {
			setPreferredSize(new Dimension(28, 28));
		} else {
			setPreferredSize(new Dimension(36, 36));
		}
		setIcon(new ImageIcon(getClass().getResource("/images/" + imageName + ".png")));
		setToolTipText(toolTipText);
	}
	
	public void useSmallerSize() {
		setPreferredSize(new Dimension(32, 32));
	}
	
}
