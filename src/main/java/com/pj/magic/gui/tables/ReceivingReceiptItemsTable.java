package com.pj.magic.gui.tables;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.models.ReceivingReceiptItemsTableModel;
import com.pj.magic.gui.tables.rowitems.ReceivingReceiptItemRowItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.util.NumberUtil;

@Component
public class ReceivingReceiptItemsTable extends MagicTable {
	
	public static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	public static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	public static final int UNIT_COLUMN_INDEX = 2;
	public static final int QUANTITY_COLUMN_INDEX = 3;
	public static final int COST_COLUMN_INDEX = 4;
	public static final int AMOUNT_COLUMN_INDEX = 5;
	public static final int DISCOUNT_1_COLUMN_INDEX = 6;
	public static final int DISCOUNT_2_COLUMN_INDEX = 7;
	public static final int DISCOUNT_3_COLUMN_INDEX = 8;
	public static final int FLAT_RATE_COLUMN_INDEX = 9;
	public static final int DISCOUNTED_AMOUNT_COLUMN_INDEX = 10;
	public static final int NET_AMOUNT_COLUMN_INDEX = 11;
	
	@Autowired private ReceivingReceiptItemsTableModel tableModel;
	
	private ReceivingReceipt receivingReceipt;
	
	@Autowired
	public ReceivingReceiptItemsTable(ReceivingReceiptItemsTableModel tableModel) {
		super(tableModel);
		setSurrendersFocusOnKeystroke(true);
		initializeColumns();
		initializeRowItemValidationBehavior();
		registerKeyBindings();
	}
	
	private void initializeColumns() {
		TableColumnModel columnModel = getColumnModel();
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(80);
		columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(60);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(60);
		
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		getColumnModel().getColumn(COST_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(AMOUNT_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(DISCOUNT_1_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(DISCOUNT_2_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(DISCOUNT_3_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(FLAT_RATE_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(DISCOUNTED_AMOUNT_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(NET_AMOUNT_COLUMN_INDEX).setCellRenderer(rightRenderer);
	}
	
	public boolean isQuantityFieldSelected() {
		return getSelectedColumn() == QUANTITY_COLUMN_INDEX;
	}
	
	public boolean isProductCodeFieldSelected() {
		return getSelectedColumn() == PRODUCT_CODE_COLUMN_INDEX;
	}

	public boolean isUnitFieldSelected() {
		return getSelectedColumn() == UNIT_COLUMN_INDEX;
	}
	
	public boolean isLastRowSelected() {
		return getSelectedRow() + 1 == tableModel.getRowCount();
	}

	public ReceivingReceiptItemRowItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	public void setReceivingReceipt(ReceivingReceipt receivingReceipt) {
		clearSelection();
		this.receivingReceipt = receivingReceipt;
		tableModel.setReceivingReceipt(receivingReceipt);
	}
	
	protected void registerKeyBindings() {
		// none
	}
	
	public int getTotalNumberOfItems() {
		return receivingReceipt.getTotalNumberOfItems();
	}

	public void highlight() {
		changeSelection(0, DISCOUNT_1_COLUMN_INDEX, false, false);
		if (!receivingReceipt.isNew()) {
			requestFocusInWindow();
		} else {
			editCellAt(0, DISCOUNT_1_COLUMN_INDEX);
			getEditorComponent().requestFocusInWindow();
		}
	}
	
	private void initializeRowItemValidationBehavior() {
		getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				final int column = e.getColumn();
				
				if (column == DISCOUNT_1_COLUMN_INDEX) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							validateDiscount1(getCurrentlySelectedRowItem());
						}
					});
				} else if (column == DISCOUNT_2_COLUMN_INDEX) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							validateDiscount2(getCurrentlySelectedRowItem());
						}
					});
				} else if (column == DISCOUNT_3_COLUMN_INDEX) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							validateDiscount3(getCurrentlySelectedRowItem());
						}
					});
				} else if (column == FLAT_RATE_COLUMN_INDEX) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							validateFlatRate(getCurrentlySelectedRowItem());
						}
					});
				}
			}
		});
	}
	
	private void validateDiscount1(ReceivingReceiptItemRowItem rowItem) {
		if (!StringUtils.isEmpty(rowItem.getDiscount1()) && !NumberUtil.isAmount(rowItem.getDiscount1())) {
			showErrorMessage("Discount 1 must be a valid amount");
			editCellAtCurrentRow(DISCOUNT_1_COLUMN_INDEX);
		}
	}
	
	private void validateDiscount2(ReceivingReceiptItemRowItem rowItem) {
		if (!StringUtils.isEmpty(rowItem.getDiscount2()) && !NumberUtil.isAmount(rowItem.getDiscount2())) {
			showErrorMessage("Discount 2 must be a valid amount");
			editCellAtCurrentRow(DISCOUNT_2_COLUMN_INDEX);
		}
	}
	
	private void validateDiscount3(ReceivingReceiptItemRowItem rowItem) {
		if (!StringUtils.isEmpty(rowItem.getDiscount3()) && !NumberUtil.isAmount(rowItem.getDiscount3())) {
			showErrorMessage("Discount 3 must be a valid amount");
			editCellAtCurrentRow(DISCOUNT_3_COLUMN_INDEX);
		}
	}
	
	private void validateFlatRate(ReceivingReceiptItemRowItem rowItem) {
		if (!StringUtils.isEmpty(rowItem.getFlatRateDiscount()) 
				&& !NumberUtil.isAmount(rowItem.getFlatRateDiscount())) {
			showErrorMessage("Flat Rate Discount must be a valid amount");
			editCellAtCurrentRow(FLAT_RATE_COLUMN_INDEX);
		}
	}
	
}