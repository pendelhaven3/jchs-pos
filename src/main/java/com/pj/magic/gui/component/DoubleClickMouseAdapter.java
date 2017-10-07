package com.pj.magic.gui.component;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// TODO: Replace other instances
public abstract class DoubleClickMouseAdapter extends MouseAdapter {

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			onDoubleClick();
		}
	}

	protected abstract void onDoubleClick();
	
}
