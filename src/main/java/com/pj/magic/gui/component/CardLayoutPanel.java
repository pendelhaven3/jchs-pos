package com.pj.magic.gui.component;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import com.pj.magic.gui.panels.StandardMagicPanel;

public class CardLayoutPanel extends JPanel {

	private Map<String, JPanel> panels = new HashMap<>();
	
	public CardLayoutPanel() {
		super(new CardLayout());
	}
	
	@Override
	public void add(Component comp, Object constraints) {
		super.add(comp, constraints);
		panels.put((String)constraints, (JPanel)comp);
	}
	
	public StandardMagicPanel getCardPanel(String panelName) {
		return (StandardMagicPanel)panels.get(panelName);
	}
	
}
