package com.pj.magic.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.pj.magic.gui.component.DatePickerFormatter;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

public class ComponentUtil {

	public static final JLabel createFiller() {
		return createFiller(1, 1);
	}

	public static final JLabel createFiller(int width, int height) {
		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(width, height));
		return label;
	}
	
	public static final JLabel createVerticalFiller(int height) {
		return createFiller(1, height);
	}
	
	public static final JLabel createLabel(int width) {
		return createLabel(width, "");
	}
	
	public static final JLabel createLabel(int width, String text) {
		JLabel label = new JLabel(text);
		label.setPreferredSize(new Dimension(width, 30));
		return label;
	}
	
	public static final JLabel createRightLabel(int width) {
		return createRightLabel(width, null);
	}
	
	public static final JLabel createRightLabel(int width, String text) {
		JLabel label = new JLabel(text);
		label.setPreferredSize(new Dimension(width, 30));
		label.setHorizontalAlignment(JLabel.RIGHT);
		return label;
	}

	public static final JLabel createCenterLabel(int width) {
		return createCenterLabel(width, "");
	}
	
	public static final JLabel createCenterLabel(int width, String text) {
		JLabel label = new JLabel(text);
		label.setPreferredSize(new Dimension(width, 30));
		label.setHorizontalAlignment(JLabel.CENTER);
		return label;
	}
	
	public static final JPanel createGenericPanel(Component... components) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		for (Component c : components) {
			panel.add(c);
		}
		return panel;
	}
	
	public static final JDatePickerImpl createDatePicker(UtilCalendarModel model) {
		return new JDatePickerImpl(new JDatePanelImpl(model), new DatePickerFormatter());
	}
	
	public static final JScrollPane createScrollPane(JTable table) {
		return createScrollPane(table, 600, 200);
	}

	public static final JScrollPane createScrollPane(JTable table, int width, int height) {
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(width, height));
		return scrollPane;
	}

	public static void clearLabels(JLabel... labels) {
		for (JLabel label : labels) {
			label.setText(null);
		}
	}
	
}
