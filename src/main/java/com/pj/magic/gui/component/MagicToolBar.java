package com.pj.magic.gui.component;

import javax.swing.JToolBar;

public class MagicToolBar extends JToolBar {

	private boolean rightSideContent;
	
	public MagicToolBar() {
		setFloatable(false);
	}

	public boolean hasRightSideContent() {
		return rightSideContent;
	}

	public void setRightSideContent(boolean rightSideContent) {
		this.rightSideContent = rightSideContent;
	}
	
}
