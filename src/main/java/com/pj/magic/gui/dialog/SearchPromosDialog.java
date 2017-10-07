package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.model.search.PromoSearchCriteria;
import com.pj.magic.util.ComponentUtil;

import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class SearchPromosDialog extends MagicDialog {

	private static final int STATUS_ALL = 0;
	private static final int STATUS_ACTIVE = 1;
	private static final int STATUS_INACTIVE = 2;
	
	private MagicComboBox<String> statusComboBox;
	private UtilCalendarModel startDateModel;
	private UtilCalendarModel endDateModel;
	private JButton searchButton;
	private PromoSearchCriteria searchCriteria;
	
	public SearchPromosDialog() {
		setSize(500, 200);
		setLocationRelativeTo(null);
		setTitle("Search Promos");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		layoutComponents();
	}

	private void initializeComponents() {
		statusComboBox = new MagicComboBox<>();
		statusComboBox.setModel(new DefaultComboBoxModel<>(
				new String[] {"All", "Yes", "No"}));
		
		startDateModel = new UtilCalendarModel();
		endDateModel = new UtilCalendarModel();
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(e -> savePromoCriteria());
		
		focusOnComponentWhenThisPanelIsDisplayed(statusComboBox);
	}

	private void savePromoCriteria() {
		searchCriteria = new PromoSearchCriteria();
		
		switch (statusComboBox.getSelectedIndex()) {
		case STATUS_ALL:
			searchCriteria.setActive(null);
			break;
		case STATUS_ACTIVE:
			searchCriteria.setActive(Boolean.TRUE);
			break;
		case STATUS_INACTIVE:
			searchCriteria.setActive(Boolean.FALSE);
			break;
		}

		if (startDateModel.getValue() != null) {
			searchCriteria.setStartDate(startDateModel.getValue().getTime());
		}
		
		if (endDateModel.getValue() != null) {
			searchCriteria.setEndDate(endDateModel.getValue().getTime());
		}
		
		setVisible(false);
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
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(140, "From Date:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		add(ComponentUtil.createDatePicker(startDateModel), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(140, "To Date:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		add(ComponentUtil.createDatePicker(endDateModel), c);

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
	
	public PromoSearchCriteria getSearchCriteria() {
		PromoSearchCriteria returnCriteria = searchCriteria;
		searchCriteria = null;
		return returnCriteria;
	}
	
	public void updateDisplay() {
		searchCriteria = null;
		statusComboBox.setSelectedIndex(STATUS_ACTIVE);
		startDateModel.setValue(Calendar.getInstance());
		endDateModel.setValue(null);
	}
	
}