package com.pj.magic.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.KeyStroke;

import com.pj.magic.Constants;

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
	
    public void onEnterKey(CustomAction action) {
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), Constants.ENTER_KEY_ACTION_NAME);
        getActionMap().put(Constants.ENTER_KEY_ACTION_NAME, new AbstractAction() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                action.doAction();
            }
        });
    }
	
}
