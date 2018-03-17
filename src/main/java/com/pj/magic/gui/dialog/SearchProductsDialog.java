package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;

import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.search.ProductSearchCriteria;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.KeyUtil;

@Component
@SuppressWarnings("serial")
public class SearchProductsDialog extends MagicDialog {

	private MagicTextField codeOrDescriptionField;
	private JButton searchButton;
	private ProductSearchCriteria searchCriteria;
	
	public SearchProductsDialog() {
		setTitle("Search Products");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		
        initializeComponents();
        registerKeyBindings();
        layoutComponents();
	}

	private void initializeComponents() {
		codeOrDescriptionField = new MagicTextField();
		codeOrDescriptionField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveProductCodeCriteria();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(codeOrDescriptionField);
	}

	private void saveProductCodeCriteria() {
		searchCriteria = new ProductSearchCriteria();
		searchCriteria.setCodeOrDescriptionLike(codeOrDescriptionField.getText());
		
		setVisible(false);
	}

	private void registerKeyBindings() {
		codeOrDescriptionField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
                saveProductCodeCriteria();
			}
		});
		
		searchButton.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		searchButton.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveProductCodeCriteria();
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
		add(ComponentUtil.createLabel(140, "Code/Description:"), c);

		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		codeOrDescriptionField.setPreferredSize(new Dimension(150, 25));
		add(codeOrDescriptionField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createVerticalStrut(5), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		searchButton.setPreferredSize(new Dimension(100, 25));
		add(searchButton, c);
		
//		currentRow++;
//		
//		c.fill = GridBagConstraints.BOTH;
//		c.weightx = 0.0;
//		c.weighty = 1.0; // bottom space filler
//		c.gridx = 0;
//		c.gridy = currentRow;
//		add(Box.createGlue(), c);
		
		pack();
        setLocationRelativeTo(null);
	}
	
	public ProductSearchCriteria getSearchCriteria() {
		ProductSearchCriteria returnCriteria = searchCriteria;
		searchCriteria = null;
		return returnCriteria;
	}
	
	public void updateDisplay() {
		searchCriteria = null;
		codeOrDescriptionField.setText(null);
	}
	
}
