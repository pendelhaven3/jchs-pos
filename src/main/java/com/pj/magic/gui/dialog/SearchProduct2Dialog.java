package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.search.Product2SearchCriteria;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.KeyUtil;

@Component
public class SearchProduct2Dialog extends MagicDialog {

	private MagicTextField descriptionField;
	private MagicComboBox<String> activeIndicatorComboBox;
	private JButton searchButton;
	private Product2SearchCriteria searchCriteria;
	
	public SearchProduct2Dialog() {
		setSize(600, 400);
		setLocationRelativeTo(null);
		setTitle("Search Products");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		
        initializeComponents();
        registerKeyBindings();
        layoutComponents();
	}

	private void initializeComponents() {
		descriptionField = new MagicTextField();
		
		activeIndicatorComboBox = new MagicComboBox<>();
		activeIndicatorComboBox.setModel(new DefaultComboBoxModel<>(new String[] {"All", "Active", "Not Active"}));
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSearchCriteria();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(descriptionField);
	}

	private void saveSearchCriteria() {
		searchCriteria = new Product2SearchCriteria();
		searchCriteria.setDescriptionLike(descriptionField.getText());
		
		if (activeIndicatorComboBox.getSelectedIndex() != 0) {
			searchCriteria.setActive(activeIndicatorComboBox.getSelectedIndex() == 1);
		}
		
		setVisible(false);
	}

	private void registerKeyBindings() {
		descriptionField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
                saveSearchCriteria();
			}
		});
		
		searchButton.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		searchButton.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSearchCriteria();
			}
		});
		
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
		add(ComponentUtil.createLabel(140, "Description:"), c);

		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		descriptionField.setPreferredSize(new Dimension(150, 25));
		add(descriptionField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(140, "Active?"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		activeIndicatorComboBox.setPreferredSize(new Dimension(150, 25));
		add(activeIndicatorComboBox, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.insets.top = 15;
		c.insets.bottom = 20;
		c.anchor = GridBagConstraints.WEST;
		searchButton.setPreferredSize(new Dimension(100, 25));
		add(searchButton, c);
		
		pack();
        setLocationRelativeTo(null);
	}
	
	public Product2SearchCriteria getSearchCriteria() {
		Product2SearchCriteria returnCriteria = searchCriteria;
		searchCriteria = null;
		return returnCriteria;
	}
	
	public void updateDisplay() {
		searchCriteria = null;
		descriptionField.setText(null);
		activeIndicatorComboBox.setSelectedIndex(1);
	}
	
}
