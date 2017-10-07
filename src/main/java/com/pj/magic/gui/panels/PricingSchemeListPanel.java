package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.PricingSchemesTableModel;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PricingSchemeService;
import com.pj.magic.util.ComponentUtil;

@Component
public class PricingSchemeListPanel extends StandardMagicPanel {

	private static final String EDIT_PRICING_SCHEME_ACTION_NAME = "editPricingScheme";
	
	@Autowired private PricingSchemeService pricingSchemeService;
	@Autowired private LoginService loginService;
	@Autowired private PricingSchemesTableModel tableModel;
	
	private JTable table;
	private MagicToolBarButton addButton;
	
	public void updateDisplay() {
		List<PricingScheme> pricingSchemes = pricingSchemeService.getAllPricingSchemes();
		tableModel.setPricingSchemes(pricingSchemes);
		if (!pricingSchemes.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
		addButton.setEnabled(loginService.getLoggedInUser().isSupervisor());
	}

	@Override
	protected void initializeComponents() {
		table = new MagicListTable(tableModel);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void registerKeyBindings() {
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), EDIT_PRICING_SCHEME_ACTION_NAME);
		table.getActionMap().put(EDIT_PRICING_SCHEME_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectPricingScheme();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectPricingScheme();
			}
		});
		
	}

	protected void selectPricingScheme() {
		PricingScheme pricingScheme = tableModel.getPricingScheme(table.getSelectedRow());
		getMagicFrame().switchToEditPricingSchemePanel(pricingScheme);
	}

	private void switchToNewPricingSchemePanel() {
		getMagicFrame().switchToAddNewPricingSchemePanel();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToInventoryMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		addButton = new MagicToolBarButton("plus", "New");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewPricingSchemePanel();
			}
		});
		
		toolBar.add(addButton);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++; // first row
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(new JScrollPane(table), c);
	}
	
}
