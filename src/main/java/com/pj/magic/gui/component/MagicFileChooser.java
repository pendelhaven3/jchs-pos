package com.pj.magic.gui.component;

import java.awt.Component;

import javax.swing.JFileChooser;

public class MagicFileChooser extends JFileChooser {

	/**
	 * Pops up a "Save File" file chooser dialog.
	 * 
	 * @see JFileChooser#showSaveDialog
	 * 
	 * @param parent
	 * @return true if save file is selected; false otherwise
	 */
	public boolean selectSaveFile(Component parent) {
		return showSaveDialog(parent) == JFileChooser.APPROVE_OPTION;
	}

	/**
	 * Pops up an "Open File" file chooser dialog.
	 * 
	 * @see JFileChooser#showOpenDialog
	 * 
	 * @param parent
	 * @return true if file to open is selected; false otherwise
	 */
	public boolean selectFileToOpen(Component parent) {
		return showOpenDialog(parent) == JFileChooser.APPROVE_OPTION;
	}
	
}
