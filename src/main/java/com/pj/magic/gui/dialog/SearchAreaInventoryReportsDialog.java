package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.search.AreaInventoryReportSearchCriteria;
import com.pj.magic.service.InventoryCheckService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.ListUtil;

@Component
public class SearchAreaInventoryReportsDialog extends MagicDialog {

	private static final int STATUS_NEW = 1;
	private static final int STATUS_REVIEWED = 2;
	
	@Autowired private InventoryCheckService inventoryCheckService;
	
	private JComboBox<InventoryCheck> inventoryCheckComboBox;
	private JComboBox<String> statusComboBox;
	private JButton searchButton;
	private AreaInventoryReportSearchCriteria searchCriteria;
	
	public SearchAreaInventoryReportsDialog() {
		setSize(400, 180);
		setLocationRelativeTo(null);
		setTitle("Search Area Inventory Reportss");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		inventoryCheckComboBox = new JComboBox<>();
		List<InventoryCheck> inventoryChecks = inventoryCheckService.getAllInventoryChecks();
		inventoryCheckComboBox.setModel(ListUtil.toDefaultComboBoxModel(inventoryChecks));
		
		statusComboBox = new JComboBox<>();
		statusComboBox.setModel(new DefaultComboBoxModel<>(new String[] {"All", "New", "Reviewed"}));
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAreaInventoryReportCriteria();
			}
		});
	}

	private void saveAreaInventoryReportCriteria() {
		searchCriteria = new AreaInventoryReportSearchCriteria();
		searchCriteria.setInventoryCheck((InventoryCheck)inventoryCheckComboBox.getSelectedItem());
		
		switch (statusComboBox.getSelectedIndex()) {
		case STATUS_NEW:
			searchCriteria.setReviewed(false);
			break;
		case STATUS_REVIEWED:
			searchCriteria.setReviewed(true);
			break;
		}
		
		setVisible(false);
	}

	private void registerKeyBindings() {
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		// nothing
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "Inventory Check:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		inventoryCheckComboBox.setPreferredSize(new Dimension(100, 25));
		add(inventoryCheckComboBox, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "Status:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusComboBox.setPreferredSize(new Dimension(150, 25));
		add(statusComboBox, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		add(Box.createVerticalStrut(5), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		searchButton.setPreferredSize(new Dimension(100, 25));
		add(searchButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createGlue(), c);
	}
	
	public AreaInventoryReportSearchCriteria getSearchCriteria() {
		AreaInventoryReportSearchCriteria returnCriteria = searchCriteria;
		searchCriteria = null;
		return returnCriteria;
	}
	
	public void updateDisplay() {
		searchCriteria = null;
		inventoryCheckComboBox.setSelectedIndex(0);
		statusComboBox.setSelectedIndex(0);
	}
	
}