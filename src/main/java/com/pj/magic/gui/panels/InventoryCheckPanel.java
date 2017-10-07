package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.ActualCountDetailsDialog;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.dialog.SearchInventoryChecksDialog;
import com.pj.magic.gui.tables.InventoryCheckSummaryTable;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.InventoryCheckSummaryItem;
import com.pj.magic.model.search.AreaInventoryReportSearchCriteria;
import com.pj.magic.model.search.InventoryCheckSearchCriteria;
import com.pj.magic.model.util.InventoryCheckReportType;
import com.pj.magic.service.AreaInventoryReportService;
import com.pj.magic.service.InventoryCheckService;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.PrintServiceImpl;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class InventoryCheckPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(InventoryCheckPanel.class);
	
	@Autowired private InventoryCheckService inventoryCheckService;
	@Autowired private InventoryCheckSummaryTable summaryTable;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	@Autowired private ActualCountDetailsDialog actualCountDetailsDialog;
	@Autowired private SearchInventoryChecksDialog searchInventoryChecksDialog;
	@Autowired private AreaInventoryReportService areaInventoryReportService;
	@Autowired private LoginService loginService;
	
	private InventoryCheck inventoryCheck;
	private UtilCalendarModel inventoryDateModel;
	private JButton saveButton;
	private JDatePickerImpl datePicker;
	private JButton postButton;
	private JLabel totalBeginningValueField;
	private JLabel totalActualValueField;
	private JButton printPreviewButton;
	private JButton printButton;
	private JButton showAllButton;
	private JButton searchButton;
	
	@Override
	protected void initializeComponents() {
		inventoryDateModel = new UtilCalendarModel();
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveInventoryCheck();
			}
		});
	}

	protected void saveInventoryCheck() {
		if (inventoryDateModel.getValue() == null) {
			showErrorMessage("Inventory Date must be specified");
			return;
		}
		
		if (confirm("Save?")) {
			inventoryCheck.setInventoryDate(inventoryDateModel.getValue().getTime());
			
			try {
				inventoryCheckService.save(inventoryCheck);
				showMessage("Saved!");
				updateDisplay(inventoryCheck);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	@Override
	protected void registerKeyBindings() {
		summaryTable.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openActualCountDetailsDialog();
			}
		});
		
		summaryTable.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				openActualCountDetailsDialog();
			}
		});
	}

	private void openActualCountDetailsDialog() {
		InventoryCheckSummaryItem item = summaryTable.getSelectedItem();
		actualCountDetailsDialog.updateDisplay(inventoryCheck, item);
		actualCountDetailsDialog.setVisible(true);
	}

	public void updateDisplay(InventoryCheck inventoryCheck) {
		this.inventoryCheck = inventoryCheck;
		if (inventoryCheck.getId() == null) {
			clearDisplay();
			return;
		}

		this.inventoryCheck = inventoryCheckService.getInventoryCheck(inventoryCheck.getId());
		inventoryCheck = this.inventoryCheck;
		
		updateInventoryDateField(inventoryCheck.getInventoryDate());
		datePicker.getComponents()[1].setVisible(false);
		totalBeginningValueField.setText(FormatterUtil.formatAmount(inventoryCheck.getTotalBeginningValue()));
		totalActualValueField.setText(FormatterUtil.formatAmount(inventoryCheck.getTotalActualValue()));
		
		postButton.setEnabled(!inventoryCheck.isPosted() && loginService.getLoggedInUser().isSupervisor());
		printButton.setEnabled(true);
		printPreviewButton.setEnabled(true);
		saveButton.setVisible(false);
		showAllButton.setEnabled(true);
		searchButton.setEnabled(true);
		
		summaryTable.setItems(inventoryCheck.getSummaryItems());
		searchInventoryChecksDialog.updateDisplay();
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				summaryTable.highlight();
			}
		});
	}

	private void updateInventoryDateField(Date inventoryDate) {
		inventoryDateModel.setValue(null); // set to null first to prevent property change listener from triggering
		inventoryDateModel.setValue(DateUtils.toCalendar(inventoryDate));
	}

	private void clearDisplay() {
		updateInventoryDateField(new Date());
		datePicker.getComponents()[1].setVisible(true);
		totalBeginningValueField.setText(null);
		totalActualValueField.setText(null);
		summaryTable.setItems(new ArrayList<InventoryCheckSummaryItem>());
		postButton.setEnabled(false);
		printButton.setEnabled(false);
		printPreviewButton.setEnabled(false);
		saveButton.setVisible(true);
		showAllButton.setEnabled(false);
		searchButton.setEnabled(false);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToInventoryCheckListPanel();
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Inventory Date: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl datePanel = new JDatePanelImpl(inventoryDateModel);
		datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 20), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		saveButton.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(saveButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 30), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		JScrollPane summaryTableScrollPane = new JScrollPane(summaryTable);
		summaryTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(summaryTableScrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}

	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(160, "Total Beginning Value:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalBeginningValueField = ComponentUtil.createRightLabel(120, "");
		panel.add(totalBeginningValueField, c);
		
		c = new GridBagConstraints(); // right side space
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createFiller(10, 1), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Actual Value:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalActualValueField = ComponentUtil.createRightLabel(120, "");
		panel.add(totalActualValueField, c);
		
		return panel;
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		showAllButton = new MagicToolBarButton("all", "Show All");
		showAllButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showAllSummaryItems();
			}
		});
		toolBar.add(showAllButton);
		
		searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchSummaryItems();
			}
		});
		toolBar.add(searchButton);
		
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postInventoryCheck();
			}
		});
		
		toolBar.add(postButton);
		
		printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreviewInventoryCheck();
			}
		});
		toolBar.add(printPreviewButton);
		
		printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print();
			}
		});
		toolBar.add(printButton);
	}

	private void searchSummaryItems() {
		searchInventoryChecksDialog.setVisible(true);
		InventoryCheckSearchCriteria criteria = searchInventoryChecksDialog.getSearchCriteria();
		refreshInventoryCheck();
		summaryTable.setItems(inventoryCheck.searchSummaryItems(criteria));
	}

	private void refreshInventoryCheck() {
		inventoryCheck = inventoryCheckService.getInventoryCheck(inventoryCheck.getId());
	}

	private void showAllSummaryItems() {
		refreshInventoryCheck();
		summaryTable.setItems(inventoryCheck.getSummaryItems());
		searchInventoryChecksDialog.updateDisplay();
	}

	private void print() {
		printService.print(inventoryCheck, chooseReportType("Print Inventory Check"));
	}

	private void printPreviewInventoryCheck() {
		InventoryCheckReportType reportType = chooseReportType("Print Preview");
		
		printPreviewDialog.updateDisplay(
				printService.generateReportAsString(inventoryCheck, reportType));
		if (reportType == InventoryCheckReportType.COMPLETE) {
			printPreviewDialog.setColumnsPerLine(PrintServiceImpl.INVENTORY_REPORT_COMPLETE_COLUMNS_PER_LINE);
		} else {
			printPreviewDialog.setColumnsPerLine(PrintServiceImpl.INVENTORY_REPORT_COLUMNS_PER_LINE);
		}
		printPreviewDialog.setUseCondensedFontForPrinting(true);
		printPreviewDialog.setVisible(true);
	}
	
	private InventoryCheckReportType chooseReportType(String dialogTitle) {
		Object[] buttons = {"Beginning Inventory", "Actual Count", "Complete"};
		int choice = JOptionPane.showOptionDialog(this, "Beginning inventory or actual count?", 
				dialogTitle, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, 
				null, buttons, buttons[0]);
		
		switch (choice) {
		case JOptionPane.YES_OPTION:
			return InventoryCheckReportType.BEGINNING_INVENTORY;
		case JOptionPane.NO_OPTION:
			return InventoryCheckReportType.ACTUAL_COUNT;
		case JOptionPane.CANCEL_OPTION:
			return InventoryCheckReportType.COMPLETE;
		default:
			return null;
		}
	}

	private void postInventoryCheck() {
		if (!areAllAreaInventoryReportsReviewed()) {
			showErrorMessage(
					"All Area Inventory Reports must be marked as reviewed before posting Inventory Check");
			return;
		}
		
		if (confirm("Post Inventory Check?")) {
			try {
				inventoryCheckService.post(inventoryCheck);
				showMessage("Inventory Check posted!");
				updateDisplay(inventoryCheck);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error!");
			}
		}
	}

	private boolean areAllAreaInventoryReportsReviewed() {
		AreaInventoryReportSearchCriteria criteria = new AreaInventoryReportSearchCriteria();
		criteria.setInventoryCheck(inventoryCheck);
		criteria.setReviewed(false);
		return areaInventoryReportService.search(criteria).isEmpty();
	}

}