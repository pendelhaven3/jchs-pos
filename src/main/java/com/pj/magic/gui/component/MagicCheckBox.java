package com.pj.magic.gui.component;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

public class MagicCheckBox extends JCheckBox {

	private boolean triggerCustomListeners = true;
	
	public void setSelected(boolean value, boolean triggerCustomListeners) {
		this.triggerCustomListeners = triggerCustomListeners;
		super.setSelected(value);
		this.triggerCustomListeners = true;
	}
	
	public boolean shouldTriggerCustomListeners() {
		return triggerCustomListeners;
	}

	public void addOnClickListener(final ItemListener itemListener) {
		addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (shouldTriggerCustomListeners()) {
					itemListener.itemStateChanged(e);
				}
			}
		});
	}
	
}
