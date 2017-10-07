package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
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
import com.pj.magic.gui.tables.models.ManufacturersTableModel;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.service.ManufacturerService;
import com.pj.magic.util.ComponentUtil;

@Component
public class ManufacturerListPanel extends StandardMagicPanel {

	private static final String EDIT_MANUFACTURER_ACTION_NAME = "editManufacturer";
	
	@Autowired private ManufacturerService manufacturerService;
	
	private JTable table;
	private ManufacturersTableModel tableModel = new ManufacturersTableModel();
	
	public void updateDisplay() {
		List<Manufacturer> manufacturers = manufacturerService.getAllManufacturers();
		tableModel.setManufacturers(manufacturers);
		if (!manufacturers.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
	}

	@Override
	protected void initializeComponents() {
		table = new MagicListTable(tableModel);
		focusOnComponentWhenThisPanelIsDisplayed(table);
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

	@Override
	protected void registerKeyBindings() {
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), EDIT_MANUFACTURER_ACTION_NAME);
		table.getActionMap().put(EDIT_MANUFACTURER_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectManufacturer();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectManufacturer();
			}
		});
	}

	protected void selectManufacturer() {
		Manufacturer manufacturer = tableModel.getManufacturer(table.getSelectedRow());
		getMagicFrame().switchToEditManufacturerPanel(manufacturer);
	}

	private void switchToNewManufacturerPanel() {
		getMagicFrame().switchToAddNewManufacturerPanel();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToRecordsMaintenanceMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton postButton = new MagicToolBarButton("plus", "New");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewManufacturerPanel();
			}
		});
		toolBar.add(postButton);
	}
	
}
