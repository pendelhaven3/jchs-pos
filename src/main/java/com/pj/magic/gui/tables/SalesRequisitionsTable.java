package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.panels.SalesRequisitionListPanel;
import com.pj.magic.gui.tables.models.SalesRequisitionsTableModel;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.service.SalesRequisitionService;

@Component
public class SalesRequisitionsTable extends MagicListTable {

	public static final int SALES_REQUISITION_NUMBER_COLUMN_INDEX = 0;
	public static final int CUSTOMER_NAME_COLUMN_INDEX = 1;
	public static final int CREATE_DATE_COLUMN_INDEX = 2;
	public static final int TRANSACTION_DATE_COLUMN_INDEX = 3;
	public static final int ENCODER_COLUMN_INDEX = 4;
	public static final int TOTAL_AMOUNT_COLUMN_INDEX = 5;
	private static final String GO_TO_SALES_REQUISITION_ACTION_NAME = "goToSalesRequisition";
	private static final String DELETE_SALES_REQUISITION_ACTION_NAME = "deleteSalesRequisition";

	@Autowired private SalesRequisitionService salesRequisitionService;
	@Autowired private SalesRequisitionsTableModel tableModel;
	
	@Autowired
	public SalesRequisitionsTable(SalesRequisitionsTableModel tableModel) {
		super(tableModel);
	}
	
	@PostConstruct
	public void initialize() {
		registerKeyBindings();
    }
	
	public void update() {
		List<SalesRequisition> salesRequisitions = salesRequisitionService.getAllNonPostedSalesRequisitions();
		tableModel.setSalesRequisitions(salesRequisitions);
		if (!salesRequisitions.isEmpty()) {
			changeSelection(0, 0, false, false);
		}
	}
	
	public SalesRequisitionsTableModel getSalesRequisitionsTableModel() {
		return (SalesRequisitionsTableModel)super.getModel();
	}
	
	public SalesRequisition getCurrentlySelectedSalesRequisition() {
		return getSalesRequisitionsTableModel().getSalesRequisition(getSelectedRow());
	}
	
	public void displaySalesRequisitionDetails(SalesRequisition salesRequisition) {
		SalesRequisitionListPanel panel = (SalesRequisitionListPanel)
				SwingUtilities.getAncestorOfClass(SalesRequisitionListPanel.class, this);
		panel.displaySalesRequisitionDetails(salesRequisition);
	}
	
	public void removeCurrentlySelectedRow() {
		
		int selectedRowIndex = getSelectedRow();
		SalesRequisition salesRequisition = getCurrentlySelectedSalesRequisition();
		salesRequisitionService.delete(salesRequisition);
		tableModel.remove(salesRequisition);
		
		if (tableModel.getRowCount() > 0) {
			if (selectedRowIndex == tableModel.getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
		
		// TODO: update table as well if any new SR has been created
	}
	
	public void registerKeyBindings() {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), GO_TO_SALES_REQUISITION_ACTION_NAME);
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), DELETE_SALES_REQUISITION_ACTION_NAME);
		
		getActionMap().put(GO_TO_SALES_REQUISITION_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (getSelectedRow() != -1) {
					selectSalesRequisition();
				}
			}
		});
		getActionMap().put(DELETE_SALES_REQUISITION_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getSelectedRow() != -1) {
					int confirm = JOptionPane.showConfirmDialog(getParent(), "Delete selected sales requisition?");
					if (confirm == JOptionPane.YES_OPTION) {
						removeCurrentlySelectedRow();
					}
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					selectSalesRequisition();
				}
			}
		});
	}

	protected void selectSalesRequisition() {
		displaySalesRequisitionDetails(getCurrentlySelectedSalesRequisition());
	}
	
}
