package com.pj.magic.gui.tables;

import java.util.List;

import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.models.ProductCanvassTableModel;
import com.pj.magic.model.ProductCanvassItem;

@Component
public class ProductCanvassTable extends MagicListTable {

	public static final int RECEIVED_DATE_COLUMN_INDEX = 0;
	public static final int RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX = 1;
	public static final int SUPPLIER_COLUMN_INDEX = 2;
	public static final int UNIT_COLUMN_INDEX = 3;
	public static final int FINAL_COST_COLUMN_INDEX = 4;
	public static final int GROSS_COST_COLUMN_INDEX = 5;
	public static final int REFERENCE_NUMBER_COLUMN_INDEX = 6;
	
	@Autowired private ProductCanvassTableModel tableModel;
	
	@Autowired
	public ProductCanvassTable(ProductCanvassTableModel tableModel) {
		super(tableModel);
		initializeColumns();
	}

	private void initializeColumns() {
		TableColumnModel columnModel = getColumnModel();
		columnModel.getColumn(RECEIVED_DATE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(SUPPLIER_COLUMN_INDEX).setPreferredWidth(200);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(FINAL_COST_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(GROSS_COST_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(REFERENCE_NUMBER_COLUMN_INDEX).setPreferredWidth(100);
	}

	public void setItems(List<ProductCanvassItem> items) {
		tableModel.setItems(items);
	}
	
}
