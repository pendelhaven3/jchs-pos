package com.pj.magic.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import com.pj.magic.Constants;

public class MagicButton extends JButton {

	public MagicButton(String text) {
		super(text);
	}

	public void onEnterKey(Action action) {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), Constants.ENTER_KEY_ACTION_NAME);
		getActionMap().put(Constants.ENTER_KEY_ACTION_NAME, action);
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