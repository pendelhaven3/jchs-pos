package com.pj.magic.gui.component;

import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class MagicTextArea extends JTextArea {

	private int maximumLength;
	private boolean allowLowerCase;
	
	public MagicTextArea() {
		setDocument(new PlainDocument() {
			
			@Override
			public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
				if (str == null) {
					return;
				}
				
				if (maximumLength > 0) {
					if ((getLength() + str.length()) <= maximumLength) {
						super.insertString(offset, allowLowerCase ? str : str.toUpperCase(), attr);
					}
				} else {
					super.insertString(offset, allowLowerCase ? str : str.toUpperCase(), attr);
				}
			}
		});
	}
	
	public void setMaximumLength(int maximumLength) {
		this.maximumLength = maximumLength;
	}

}
