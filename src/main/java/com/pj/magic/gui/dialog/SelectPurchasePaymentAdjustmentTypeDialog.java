package com.pj.magic.gui.dialog;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.PurchasePaymentAdjustmentType;
import com.pj.magic.service.PurchasePaymentAdjustmentTypeService;

@Component
public class SelectPurchasePaymentAdjustmentTypeDialog extends MagicDialog {

	private static final int CODE_COLUMN_INDEX = 0;
	private static final int DESCRIPTION_COLUMN_INDEX = 1;
	
	@Autowired private PurchasePaymentAdjustmentTypeService purchasePaymentAdjustmentTypeService;
	
	private PurchasePaymentAdjustmentType selectedAdjustmentType;
	private MagicListTable table;
	private PurchasePaymentAdjustmentTypesTableModel tableModel;
	
	public SelectPurchasePaymentAdjustmentTypeDialog() {
		setSize(500, 400);
		setLocationRelativeTo(null);
		setTitle("Select Adjustment Type");

		initializeTable();
		layoutMainPanel();
	}

	private void initializeTable() {
		tableModel = new PurchasePaymentAdjustmentTypesTableModel();
		table = new MagicListTable(tableModel);
		
		table.getColumnModel().getColumn(CODE_COLUMN_INDEX).setPreferredWidth(100);
		table.getColumnModel().getColumn(DESCRIPTION_COLUMN_INDEX).setPreferredWidth(200);
		
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAdjustmentType();
			}
		});
		
		table.onF9Key(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAdjustmentType();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectAdjustmentType();
			}
		});
	}

	private void layoutMainPanel() {
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);	
	}
	
	private void selectAdjustmentType() {
		selectedAdjustmentType = tableModel.getAdjustmentType(table.getSelectedRow());
		setVisible(false);
	}

	public PurchasePaymentAdjustmentType getSelectedAdjustmentType() {
		return selectedAdjustmentType;
	}
	
	@Override
	protected void doWhenEscapeKeyPressed() {
		selectedAdjustmentType = null;
	}
	
	public void updateDisplay() {
		tableModel.setAdjustmentTypes(purchasePaymentAdjustmentTypeService.getAllAdjustmentTypes());
		table.changeSelection(0, 0);
	}
	
	private class PurchasePaymentAdjustmentTypesTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Code", "Description"};
		
		private List<PurchasePaymentAdjustmentType> adjustmentTypes = new ArrayList<>();
		
		public void setAdjustmentTypes(List<PurchasePaymentAdjustmentType> adjustmentTypes) {
			this.adjustmentTypes = adjustmentTypes;
			fireTableDataChanged();
		}
		
		public PurchasePaymentAdjustmentType getAdjustmentType(int rowIndex) {
			return adjustmentTypes.get(rowIndex);
		}
		
		@Override
		public int getRowCount() {
			return adjustmentTypes.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PurchasePaymentAdjustmentType adjustmentType = adjustmentTypes.get(rowIndex);
			switch (columnIndex) {
			case CODE_COLUMN_INDEX:
				return adjustmentType.getCode();
			case DESCRIPTION_COLUMN_INDEX:
				return adjustmentType.getDescription();
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
	}
	
}