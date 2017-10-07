package com.pj.magic.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.pj.magic.Constants;

public class MagicTextField extends JTextField {

	private int maximumLength;
	private boolean numbersOnly;
	private boolean allowLowerCase;
	
	public MagicTextField() {
		setDocument(new PlainDocument() {
			
			@Override
			public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
				if (str == null) {
					return;
				}
				if (numbersOnly && !StringUtils.isNumeric(str)) {
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

	public void setNumbersOnly(boolean numbersOnly) {
		this.numbersOnly = numbersOnly;
	}
	
	// TODO: Migrate references here
	public void onEnterKey(Action action) {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), Constants.ENTER_KEY_ACTION_NAME);
		getActionMap().put(Constants.ENTER_KEY_ACTION_NAME, action);
	}

	public void onF5Key(Action action) {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), Constants.F5_KEY_ACTION_NAME);
		getActionMap().put(Constants.F5_KEY_ACTION_NAME, action);
	}

	public void onF5Key(CustomAction action) {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), Constants.F5_KEY_ACTION_NAME);
		getActionMap().put(Constants.F5_KEY_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				action.doAction();
			}
		});
	}

	public void setAllowLowerCase(boolean allowLowerCase) {
		this.allowLowerCase = allowLowerCase;
	}

	public Integer getTextAsInteger() {
		String value = getText();
		if (NumberUtils.isNumber(value)) {
			return Integer.valueOf(value);
		} else {
			return null;
		}
	}

	public boolean isEmpty() {
		return StringUtils.isEmpty(getText());
	}

	public Date getTextAsDateTime() {
		String text = getText();
		
		if (StringUtils.isEmpty(text)) {
			return null;
		}
		
		try {
			return new SimpleDateFormat(Constants.DATETIME_FORMAT).parse(text);
		} catch (ParseException e) {
			return null;
		}
	}
	
}