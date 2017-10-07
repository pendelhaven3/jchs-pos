package com.pj.magic.gui.component;

import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.pj.magic.Constants;

public class MagicPasswordField extends JPasswordField {

	private int maximumLength;
	
	public MagicPasswordField() {
		setDocument(new PlainDocument() {
			
			@Override
			public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
				if (str == null) {
					return;
				}
				
				if (maximumLength > 0) {
					if ((getLength() + str.length()) <= maximumLength) {
						super.insertString(offset, str, attr);
					}
				} else {
					super.insertString(offset, str, attr);
				}
			}
		});
	}
	
	public void onEnterKey(Action action) {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), Constants.ENTER_KEY_ACTION_NAME);
		getActionMap().put(Constants.ENTER_KEY_ACTION_NAME, action);
	}
	
	public void setMaximumLength(int maximumLength) {
		this.maximumLength = maximumLength;
	}
	
}
