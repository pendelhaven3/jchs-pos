package com.pj.magic.gui.panels.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.pj.magic.Constants;
import com.pj.magic.gui.panels.StandardMagicPanel;

public abstract class MenuPanel extends StandardMagicPanel {

	@Override
	@PostConstruct
	public final void registerBackKeyBinding() {
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), Constants.ESCAPE_KEY_ACTION_NAME);
		getActionMap().put(Constants.ESCAPE_KEY_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}
	
}