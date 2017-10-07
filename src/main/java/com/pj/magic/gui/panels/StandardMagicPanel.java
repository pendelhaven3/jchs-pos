package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.util.ComponentUtil;

public abstract class StandardMagicPanel extends AbstractMagicPanel {

	private String title;
	
	@PostConstruct
	@Override
	public void initialize() {
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		initializeComponents();
		registerKeyBindings();
		registerBackKeyBinding();
		createStandardLayout();
	}
	
	private void createStandardLayout() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		add(createToolBar(), c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		add(ComponentUtil.createFiller(1, 5), c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		add(createMainPanel(), c);
	}

	private JPanel createMainPanel() {
		JPanel panel = new JPanel();
		layoutMainPanel(panel);
		return panel;
	}

	protected abstract void layoutMainPanel(JPanel mainPanel);

	private MagicToolBar createToolBar() {
		MagicToolBar toolBar = new MagicToolBar();
		addBackButton(toolBar);
		addToolBarButtons(toolBar);
		addUsernameFieldAndLogoutButton(toolBar);
		return toolBar;
	}

	/**
	 * Add panel-specific toolbar content here
	 * 
	 * @param toolBar
	 */
	protected abstract void addToolBarButtons(MagicToolBar toolBar);

	// TODO: Remove this
	@Override
	protected final void layoutComponents() {
		// use layoutMainPanel instead
	}
	
	protected void onEscapeKey(Action action) {
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), Constants.ESCAPE_KEY_ACTION_NAME);
		getActionMap().put(Constants.ESCAPE_KEY_ACTION_NAME, action);
	}
	
	public void updateDisplayOnBack() {
		// for overriding
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
}