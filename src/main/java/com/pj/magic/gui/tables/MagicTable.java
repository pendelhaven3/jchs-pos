package com.pj.magic.gui.tables;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.CustomAction;
import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.util.KeyUtil;

// TODO: This is not really ItemsTable
/*
 * [PJ 8/27/2014] 
 * ItemsTable has 2 modes: edit (default) and add (allows adding blank rows after the last row).
 * 
 */
public class MagicTable extends JTable {

	private static final String SCROLL_TO_TOP_ACTION_NAME = "scrollToTop";
	private static final String SCROLL_TO_BOTTOM_ACTION_NAME = "scrollToBottom";
	
	protected boolean addMode; // TODO: Return back to PO panel / items table class
	
	public MagicTable() {
		super();
	}
	
	public MagicTable(TableModel tableModel) {
		super(tableModel);
		setSurrendersFocusOnKeystroke(true); // TODO: search other references
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setRowHeight(25);
		registerScrollKeys();
	}
	
	private void registerScrollKeys() {
		InputMap inputMap = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap actionMap = getActionMap();
		
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), SCROLL_TO_TOP_ACTION_NAME);
		actionMap.put(SCROLL_TO_TOP_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				scrollToTop();
			}
		});
		
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), SCROLL_TO_BOTTOM_ACTION_NAME);
		actionMap.put(SCROLL_TO_BOTTOM_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				scrollToBottom();
			}
		});
	}

	protected void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(this, message, "Error Message", JOptionPane.ERROR_MESSAGE);
	}
	
	protected boolean confirm(String message) {
		int confirm = JOptionPane.showConfirmDialog(this, message, "Confirmation", JOptionPane.YES_NO_OPTION);
		return confirm == JOptionPane.YES_OPTION;
	}
	
	protected void editCellAtCurrentRow(int columnIndex) {
		editCellAt(getSelectedRow(), columnIndex);
		getEditorComponent().requestFocusInWindow();
	}
	
	protected void editCellAtCurrentLocation() {
		editCellAt(getSelectedRow(), getSelectedColumn());
		getEditorComponent().requestFocusInWindow();
	}
	
	public boolean isAdding() {
		return addMode;
	}
	
	@Override
	public boolean editCellAt(int row, int column, EventObject e) {
		boolean result = super.editCellAt(row, column, e);
		if (e instanceof KeyEvent) {
			int keyCode = ((KeyEvent) e).getKeyCode();
			if (KeyUtil.isAlphaNumericKeyCode(keyCode) || KeyUtil.isNumericKeyCodeFromNumPad(keyCode)) {
				Component c = getEditorComponent();
				if (c instanceof JTextField) {
					((JTextField)c).setText(null);
				}
			}
		}
		return result;
	}
	
	public void selectAndEditCellAt(int row, int column) {
		changeSelection(row, column, false, false);
		editCellAt(row, column);
		getEditorComponent().requestFocusInWindow();
	}
	
	public void onEnterKey(Action action) {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), Constants.ENTER_KEY_ACTION_NAME);
		getActionMap().put(Constants.ENTER_KEY_ACTION_NAME, action);
	}
	
	public void onF9Key(Action action) {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), Constants.F9_KEY_ACTION_NAME);
		getActionMap().put(Constants.F9_KEY_ACTION_NAME, action);
	}
	
	public void onDeleteKey(Action action) {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), Constants.DELETE_KEY_ACTION_NAME);
		getActionMap().put(Constants.DELETE_KEY_ACTION_NAME, action);
	}
	
	protected void scrollToBottom() {
		changeSelection(getRowCount() - 1, 0, false, false);
	}

	protected void scrollToTop() {
		changeSelection(0, 0, false, false);
	}

	protected String getValueAtAsString(int row, int column) {
		Object value = getValueAt(row, column);
		if (value instanceof String) {
			return (String)value;
		} else if (value instanceof Integer) {
			return ((Integer)value).toString();
		} else if (value instanceof BigDecimal) {
			return ((BigDecimal)value).toString();
		} else {
			return (String)value;
		}
	}

	// TODO: Remove this
	public void changeSelection(int rowIndex, int columnIndex) {
		super.changeSelection(rowIndex, columnIndex, false, false);
	}
	
	public void selectFirstRow() {
		changeSelection(0, 0, false, false);
	}
	
	protected void onF4Key(Action action) {
		InputMap inputMap = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyUtil.getF4Key(), Constants.F4_KEY_ACTION_NAME);
		getActionMap().put(Constants.F4_KEY_ACTION_NAME, action);
	}
	
	protected void onF5Key(Action action) {
		InputMap inputMap = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyUtil.getF5Key(), Constants.F5_KEY_ACTION_NAME);
		getActionMap().put(Constants.F5_KEY_ACTION_NAME, action);
	}
	
	protected void onEscapeKey(Action action) {
		InputMap inputMap = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyUtil.getEscapeKey(), Constants.ESCAPE_KEY_ACTION_NAME);
		getActionMap().put(Constants.ESCAPE_KEY_ACTION_NAME, action);
	}

	public void onDoubleClick(final CustomAction action) {
		addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				action.doAction();
			}
		});
	}
	
	public void onEnterKeyAndDoubleClick(final CustomAction action) {
		onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				action.doAction();
			}
		});
		
		onDoubleClick(action);
	}
	
	public boolean hasNoSelectedRow() {
		return getSelectedRow() == -1;
	}
	
}