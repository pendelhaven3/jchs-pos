package com.pj.magic.gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DatePickerFormatter;

@Component
public class SelectDateDialog extends MagicDialog {

	private UtilCalendarModel model;
	private JDatePickerImpl datePicker;
	private JButton selectButton;
	
	public SelectDateDialog() {
		setSize(260, 300);
		setLocationRelativeTo(null);
		setTitle("Select Date");
		
		initializeComponents();
		layoutComponents();
	}

	private void initializeComponents() {
		model = new UtilCalendarModel();
		
		selectButton = new JButton("Select");
		selectButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		showDatePickerPanelWhenDialogIsShown();
	}

	private void showDatePickerPanelWhenDialogIsShown() {
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				clickDatePickerButton();
			}
		});
	}

	private void clickDatePickerButton() {
		((JButton)datePicker.getComponents()[1]).doClick();
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(10, 0, 0, 0);
		
		JDatePanelImpl datePanel = new JDatePanelImpl(model);
		datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		add(datePicker, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.insets = new Insets(10, 0, 10, 0);
		add(selectButton, c);
	}
	
	public Date getSelectedDate() {
		return (model.getValue() != null) ? model.getValue().getTime() : null;
	}

	public void updateDisplay() {
		model.setValue(Calendar.getInstance());
	}
	
}