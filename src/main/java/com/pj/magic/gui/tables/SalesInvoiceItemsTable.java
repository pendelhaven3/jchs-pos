package com.pj.magic.gui.tables;

import java.math.BigDecimal;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicCellEditor;
import com.pj.magic.gui.tables.models.SalesInvoiceItemsTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.NumberUtil;

@Component
public class SalesInvoiceItemsTable extends MagicTable {
	
	public static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	public static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	public static final int UNIT_COLUMN_INDEX = 2;
	public static final int QUANTITY_COLUMN_INDEX = 3;
	public static final int UNIT_PRICE_COLUMN_INDEX = 4;
	public static final int AMOUNT_COLUMN_INDEX = 5;
	public static final int DISCOUNT_1_COLUMN_INDEX = 6;
	public static final int DISCOUNT_2_COLUMN_INDEX = 7;
	public static final int DISCOUNT_3_COLUMN_INDEX = 8;
	public static final int FLAT_RATE_DISCOUNT_COLUMN_INDEX = 9;
	public static final int DISCOUNTED_AMOUNT_COLUMN_INDEX = 10;
	public static final int NET_AMOUNT_COLUMN_INDEX = 11;

	@Autowired private ProductService productService;
	@Autowired private SalesInvoiceItemsTableModel tableModel;
	
	private SalesInvoice salesInvoice;
	
	@Autowired
	public SalesInvoiceItemsTable(SalesInvoiceItemsTableModel tableModel) {
		super(tableModel);
		initializeModelListener();
	}
	
	private void initializeModelListener() {
		getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				final AbstractTableModel model = (AbstractTableModel)e.getSource();
				final int row = e.getFirstRow();
				final int column = e.getColumn();
				
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						switch (column) {
						case DISCOUNT_1_COLUMN_INDEX:
						case DISCOUNT_2_COLUMN_INDEX:
						case DISCOUNT_3_COLUMN_INDEX:
						case FLAT_RATE_DISCOUNT_COLUMN_INDEX:
							model.fireTableCellUpdated(row, DISCOUNTED_AMOUNT_COLUMN_INDEX);
							model.fireTableCellUpdated(row, NET_AMOUNT_COLUMN_INDEX);
							break;
						}
					}
				});
			}
		});
	}

	private void initializeColumns(boolean showDiscountDetails) {
		TableColumnModel columnModel = getColumnModel();
		
		if (showDiscountDetails) {
			columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(120);
			columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(240);
			columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(60);
			columnModel.getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(60);
		} else {
			columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(120);
			columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
			columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(70);
			columnModel.getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(70);
			columnModel.getColumn(UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(100);
			columnModel.getColumn(AMOUNT_COLUMN_INDEX).setPreferredWidth(100);
		}
		
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		columnModel.getColumn(UNIT_PRICE_COLUMN_INDEX).setCellRenderer(rightRenderer);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setCellRenderer(rightRenderer);
		if (showDiscountDetails) {
			columnModel.getColumn(DISCOUNT_1_COLUMN_INDEX).setCellRenderer(rightRenderer);
			columnModel.getColumn(DISCOUNT_2_COLUMN_INDEX).setCellRenderer(rightRenderer);
			columnModel.getColumn(DISCOUNT_3_COLUMN_INDEX).setCellRenderer(rightRenderer);
			columnModel.getColumn(FLAT_RATE_DISCOUNT_COLUMN_INDEX).setCellRenderer(rightRenderer);
			columnModel.getColumn(DISCOUNTED_AMOUNT_COLUMN_INDEX).setCellRenderer(rightRenderer);
			columnModel.getColumn(NET_AMOUNT_COLUMN_INDEX).setCellRenderer(rightRenderer);
			
			columnModel.getColumn(DISCOUNT_1_COLUMN_INDEX).setCellEditor(
					new DiscountCellEditor(new JTextField(), "Discount 1"));
			columnModel.getColumn(DISCOUNT_2_COLUMN_INDEX).setCellEditor(
					new DiscountCellEditor(new JTextField(), "Discount 2"));
			columnModel.getColumn(DISCOUNT_3_COLUMN_INDEX).setCellEditor(
					new DiscountCellEditor(new JTextField(), "Discount 3"));
			columnModel.getColumn(FLAT_RATE_DISCOUNT_COLUMN_INDEX).setCellEditor(
					new DiscountCellEditor(new JTextField(), "Flat Rate Discount"));
		}
	}
	
	public SalesInvoiceItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	public void setSalesInvoice(SalesInvoice salesInvoice, boolean showDiscountDetails) {
		this.salesInvoice = salesInvoice;
		tableModel.setSalesInvoice(salesInvoice, showDiscountDetails);
		initializeColumns(showDiscountDetails);
	}
	
	public boolean validateItemProfitNotLessThanZero(BigDecimal value, String fieldName) {
		SalesInvoiceItem item = new SalesInvoiceItem(getCurrentlySelectedRowItem());
		Product product = productService.getProduct(item.getProduct().getId(), salesInvoice.getPricingScheme());
		switch (fieldName) {
		case "Discount 1":
			item.setDiscount1(value);
			break;
		case "Discount 2":
			item.setDiscount2(value);
			break;
		case "Discount 3":
			item.setDiscount3(value);
			break;
		case "Flat Rate Discount":
			item.setFlatRateDiscount(value);
			break;
		}
		return item.getDiscountedUnitPrice().compareTo(product.getFinalCost(item.getUnit())) >= 0;
	}
	
	private class DiscountCellEditor extends MagicCellEditor {
		
		private String fieldName;
		
		public DiscountCellEditor(JTextField textField, String fieldName) {
			super(textField);
			this.fieldName = fieldName;
		}
		
		@Override
		public boolean stopCellEditing() {
			String discount = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(discount)) {
				((JTextField)getComponent()).setText("0.00");
			} else if (!NumberUtil.isAmount(discount)){
				showErrorMessage(fieldName + " must be a valid amount");
			} else if (!validateItemProfitNotLessThanZero(NumberUtil.toBigDecimal(discount), fieldName)) {
				showErrorMessage("Resulting net price less than cost");
			} else {
				valid = true;
			}
			return (valid) ? super.stopCellEditing() : false;
		}
		
	}
	
}