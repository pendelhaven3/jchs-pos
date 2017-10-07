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

import com.pj.magic.gui.component.CenterTableCellRenderer;
import com.pj.magic.gui.panels.AdjustmentOutListPanel;
import com.pj.magic.gui.tables.models.AdjustmentOutsTableModel;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.service.AdjustmentOutService;

@Component
public class AdjustmentOutsTable extends MagicListTable {

	public static final int ADJUSTMENT_OUT_NUMBER_COLUMN_INDEX = 0;
	public static final int REMARKS_COLUMN_INDEX = 1;
	public static final int POSTED_COLUMN_INDEX = 2;
	public static final int POST_DATE_COLUMN_INDEX = 3;
	public static final int PILFERAGE_FLAG_COLUMN_INDEX = 4;
	
	private static final String GO_TO_ADJUSTMENT_OUT_ACTION_NAME = "goToAdjustmentOut";
	private static final String DELETE_ADJUSTMENT_OUT_ACTION_NAME = "deleteAdjustmentOut";

	@Autowired private AdjustmentOutService adjustmentOutService;
	@Autowired private AdjustmentOutsTableModel tableModel;
	
	@Autowired
	public AdjustmentOutsTable(AdjustmentOutsTableModel tableModel) {
		super(tableModel);
		initializeTable();
	}
	
	@PostConstruct
	public void initialize() {
		registerKeyBindings();
    }
	
	private void initializeTable() {
		columnModel.getColumn(ADJUSTMENT_OUT_NUMBER_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(REMARKS_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(POSTED_COLUMN_INDEX).setPreferredWidth(80);
		columnModel.getColumn(POST_DATE_COLUMN_INDEX).setPreferredWidth(120);
		columnModel.getColumn(PILFERAGE_FLAG_COLUMN_INDEX).setPreferredWidth(80);
		
		CenterTableCellRenderer centerRenderer = new CenterTableCellRenderer();
		columnModel.getColumn(POSTED_COLUMN_INDEX).setCellRenderer(centerRenderer);
		columnModel.getColumn(PILFERAGE_FLAG_COLUMN_INDEX).setCellRenderer(centerRenderer);
	}

	public void update() {
		List<AdjustmentOut> salesRequisitions = adjustmentOutService.getAllNonPostedAdjustmentOuts();
		tableModel.setAdjustmentOuts(salesRequisitions);
		if (!salesRequisitions.isEmpty()) {
			changeSelection(0, 0, false, false);
		}
	}
	
	public AdjustmentOutsTableModel getAdjustmentOutsTableModel() {
		return (AdjustmentOutsTableModel)super.getModel();
	}
	
	public AdjustmentOut getCurrentlySelectedAdjustmentOut() {
		return getAdjustmentOutsTableModel().getAdjustmentOut(getSelectedRow());
	}
	
	public void displayAdjustmentOutDetails(AdjustmentOut salesRequisition) {
		AdjustmentOutListPanel panel = (AdjustmentOutListPanel)
				SwingUtilities.getAncestorOfClass(AdjustmentOutListPanel.class, this);
		panel.displayAdjustmentOutDetails(salesRequisition);
	}
	
	public void removeCurrentlySelectedRow() {
		
		int selectedRowIndex = getSelectedRow();
		AdjustmentOut salesRequisition = getCurrentlySelectedAdjustmentOut();
		adjustmentOutService.delete(salesRequisition);
		tableModel.remove(salesRequisition);
		
		if (tableModel.getRowCount() > 0) {
			if (selectedRowIndex == tableModel.getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}
	
	public void registerKeyBindings() {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), GO_TO_ADJUSTMENT_OUT_ACTION_NAME);
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), DELETE_ADJUSTMENT_OUT_ACTION_NAME);
		
		getActionMap().put(GO_TO_ADJUSTMENT_OUT_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (getSelectedRow() != -1) {
					selectAdjustmentOut();
				}
			}
		});
		getActionMap().put(DELETE_ADJUSTMENT_OUT_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getSelectedRow() != -1) {
					int confirm = JOptionPane.showConfirmDialog(getParent(), "Delete selected adjustment out?");
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
					selectAdjustmentOut();
				}
			}
		});
	}

	protected void selectAdjustmentOut() {
		displayAdjustmentOutDetails(getCurrentlySelectedAdjustmentOut());
	}
	
	public void setAdjustmentOuts(List<AdjustmentOut> adjustmentOuts) {
		tableModel.setAdjustmentOuts(adjustmentOuts);
		if (!adjustmentOuts.isEmpty()) {
			changeSelection(0, 0, false, false);
		}
	}
	
}
