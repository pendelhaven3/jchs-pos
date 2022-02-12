package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.dao.SystemDao;
import com.pj.magic.gui.component.MagicToolBar;

@Component
public class AdminMiscellaneousPanel extends StandardMagicPanel {

	@Autowired
	private SystemDao systemDao;
	
	private JButton synchSuppliersButton;
	
	@Override
	protected void initializeComponents() {
		synchSuppliersButton = new JButton("Synch suppliers between retail and wholesale codes of same product");
		synchSuppliersButton.addActionListener(e -> synchSuppliers());
	}

	private void synchSuppliers() {
		systemDao.synchSuppliers();
		showMessage("Synch suppliers done");
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = c.weighty = 1.0;
		mainPanel.add(synchSuppliersButton, c);
	}

	@Override
	protected void registerKeyBindings() {
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToAdminMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}
