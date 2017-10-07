package com.pj.magic.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

public class MagicComboBox<E> extends JComboBox<E> {

	private boolean triggerCustomListeners = true;
	
	public MagicComboBox() {
		init();
	}
	
	public MagicComboBox(E[] values) {
		super(values);
		init();
	}
	
	private void init() {
		putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
	}
	
	public void setSelectedItem(Object anObject, boolean triggerCustomListeners) {
		this.triggerCustomListeners = triggerCustomListeners;
		super.setSelectedItem(anObject);
		this.triggerCustomListeners = true;
	}
	
	public boolean shouldTriggerCustomListeners() {
		return triggerCustomListeners;
	}

	// TODO: migrate references to this
	public void addOnSelectListener(final ActionListener actionListener) {
		addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (shouldTriggerCustomListeners()) {
					actionListener.actionPerformed(e);
				}
			}
		});
	}
	
	// TODO: Not working!
	public void resetScrollPosition() {
		setSelectedIndex(1);
		setSelectedIndex(-1);
	}
	
}
