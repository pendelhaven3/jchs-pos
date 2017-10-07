package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.ColumnGroup;
import com.pj.magic.gui.component.GroupableTableHeader;
import com.pj.magic.gui.component.MagicCellEditor;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.dialog.SelectUnitDialog;
import com.pj.magic.gui.tables.models.PromoType2RulesTableModel;
import com.pj.magic.gui.tables.rowitems.PromoType2RuleRowItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType2Rule;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.KeyUtil;

@Component
public class PromoType2RulesTable extends MagicTable {

	public static final int PROMO_PRODUCT_CODE_COLUMN_INDEX = 0;
	public static final int PROMO_PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	public static final int PROMO_UNIT_COLUMN_INDEX = 2;
	public static final int PROMO_QUANTITY_COLUMN_INDEX = 3;
	public static final int FREE_PRODUCT_CODE_COLUMN_INDEX = 4;
	public static final int FREE_PRODUCT_DESCRIPTION_COLUMN_INDEX = 5;
	public static final int FREE_UNIT_COLUMN_INDEX = 6;
	public static final int FREE_QUANTITY_COLUMN_INDEX = 7;
	
	@Autowired private PromoType2RulesTableModel tableModel;
	@Autowired private ProductService productService;
	@Autowired private SelectProductDialog selectProductDialog;
	@Autowired private SelectUnitDialog selectUnitDialog;
	
	private Promo promo;
	
	@Autowired
	public PromoType2RulesTable(PromoType2RulesTableModel tableModel) {
		super(tableModel);
		initializeHeader();
		initializeColumns();
		initializeModelListener();
		registerKeyBindings();
	}

	private void registerKeyBindings() {
		onF5Key(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (isProductCodeFieldSelected()) {
					openSelectProductDialog();
				} else if (isUnitFieldSelected()) {
					openSelectUnitDialog();
				}
			}
			
		});
		
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelEditing();
			}
		});
	}

	private void cancelEditing() {
		if (isEditing()) {
			getCellEditor().cancelCellEditing();
			if (getCurrentlySelectedRowItem().isUpdating()) {
				tableModel.reset(getSelectedRow());
			}
		} else {
			clearSelection();
			tableModel.setPromo(promo);
			if (!promo.getPromoType2Rules().isEmpty()) {
				selectFirstRow();
			}
		}
	}

	private void openSelectUnitDialog() {
		int column = getSelectedColumn();
		if (!isEditing()) {
			editCellAt(getSelectedRow(), column);
		}
		
		if (column == PROMO_UNIT_COLUMN_INDEX) {
			selectUnitDialog.setUnits(getCurrentlySelectedRowItem().getPromoProduct().getUnits());
		} else {
			selectUnitDialog.setUnits(getCurrentlySelectedRowItem().getFreeProduct().getUnits());
		}
		
		selectUnitDialog.searchUnits((String)getCellEditor().getCellEditorValue());
		selectUnitDialog.setVisible(true);
		
		String unit = selectUnitDialog.getSelectedUnit();
		if (unit != null) {
			((JTextField)getEditorComponent()).setText(unit);
			getCellEditor().stopCellEditing();
		}
	}

	private boolean isUnitFieldSelected() {
		int column = getSelectedColumn();
		return column == PROMO_UNIT_COLUMN_INDEX || column == FREE_UNIT_COLUMN_INDEX;
	}

	private void openSelectProductDialog() {
		if (!isEditing()) {
			editCellAt(getSelectedRow(), getSelectedColumn());
		}
		
		String codeOrDescription = (String)getCellEditor().getCellEditorValue();
		selectProductDialog.searchProducts(codeOrDescription, null);
		selectProductDialog.setVisible(true);
		
		Product product = selectProductDialog.getSelectedProduct();
		if (product != null) {
			((JTextField)getEditorComponent()).setText(product.getCode());
			getCellEditor().stopCellEditing();
		}
	}
	
	private boolean isProductCodeFieldSelected() {
		int column = getSelectedColumn();
		return column == PROMO_PRODUCT_CODE_COLUMN_INDEX || column == FREE_PRODUCT_CODE_COLUMN_INDEX;
	}

	private void initializeColumns() {
		MagicTextField promoProductCodeField = new MagicTextField()	;		
		promoProductCodeField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		promoProductCodeField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent event) {
				if (KeyUtil.isAlphaNumericKeyCode(event.getKeyCode())) {
					JTextField textField = (JTextField)event.getComponent();
					if (textField.getText().length() == Constants.PRODUCT_CODE_MAXIMUM_LENGTH) {
						getCellEditor().stopCellEditing();
					};
				}
			}
		});
		columnModel.getColumn(PROMO_PRODUCT_CODE_COLUMN_INDEX)
			.setCellEditor(new ProductCodeCellEditor(promoProductCodeField, true));
		
		MagicTextField promoUnitTextField = new MagicTextField();
		promoUnitTextField.setMaximumLength(Constants.UNIT_MAXIMUM_LENGTH);
		promoUnitTextField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent event) {
				if (KeyUtil.isAlphaNumericKeyCode(event.getKeyCode())) {
					JTextField textField = (JTextField)event.getComponent();
					if (textField.getText().length() == Constants.UNIT_MAXIMUM_LENGTH) {
						getCellEditor().stopCellEditing();
					}
				}
			}
		});
		columnModel.getColumn(PROMO_UNIT_COLUMN_INDEX).setCellEditor(
				new UnitCellEditor(promoUnitTextField, true));
		
		MagicTextField promoQuantityTextField = new MagicTextField();
		promoQuantityTextField.setMaximumLength(Constants.QUANTITY_MAXIMUM_LENGTH);
		promoQuantityTextField.setNumbersOnly(true);
		columnModel.getColumn(PROMO_QUANTITY_COLUMN_INDEX)
			.setCellEditor(new QuantityCellEditor(promoQuantityTextField, true));
		
		MagicTextField freeProductCodeField = new MagicTextField()	;		
		freeProductCodeField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		freeProductCodeField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent event) {
				if (KeyUtil.isAlphaNumericKeyCode(event.getKeyCode())) {
					JTextField textField = (JTextField)event.getComponent();
					if (textField.getText().length() == Constants.PRODUCT_CODE_MAXIMUM_LENGTH) {
						getCellEditor().stopCellEditing();
					};
				}
			}
		});
		columnModel.getColumn(FREE_PRODUCT_CODE_COLUMN_INDEX)
			.setCellEditor(new ProductCodeCellEditor(freeProductCodeField, false));
		
		MagicTextField freeUnitTextField = new MagicTextField();
		freeUnitTextField.setMaximumLength(Constants.UNIT_MAXIMUM_LENGTH);
		freeUnitTextField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent event) {
				if (KeyUtil.isAlphaNumericKeyCode(event.getKeyCode())) {
					JTextField textField = (JTextField)event.getComponent();
					if (textField.getText().length() == Constants.UNIT_MAXIMUM_LENGTH) {
						getCellEditor().stopCellEditing();
					}
				}
			}
		});
		columnModel.getColumn(FREE_UNIT_COLUMN_INDEX).setCellEditor(
				new UnitCellEditor(freeUnitTextField, false));
		
		MagicTextField freeQuantityTextField = new MagicTextField();
		freeQuantityTextField.setMaximumLength(Constants.QUANTITY_MAXIMUM_LENGTH);
		freeQuantityTextField.setNumbersOnly(true);
		columnModel.getColumn(FREE_QUANTITY_COLUMN_INDEX)
			.setCellEditor(new QuantityCellEditor(freeQuantityTextField, false));
	}

	private void initializeHeader() {
		TableColumnModel columnModel = getColumnModel();
		GroupableTableHeader header = new GroupableTableHeader(columnModel);
		setTableHeader(header);
		
		ColumnGroup group = new ColumnGroup("Buy");
		group.add(columnModel.getColumn(PROMO_PRODUCT_CODE_COLUMN_INDEX));
		group.add(columnModel.getColumn(PROMO_PRODUCT_DESCRIPTION_COLUMN_INDEX));
		group.add(columnModel.getColumn(PROMO_UNIT_COLUMN_INDEX));
		group.add(columnModel.getColumn(PROMO_QUANTITY_COLUMN_INDEX));
		header.addColumnGroup(group);
		
		group = new ColumnGroup("Get Free");
		group.add(columnModel.getColumn(FREE_PRODUCT_CODE_COLUMN_INDEX));
		group.add(columnModel.getColumn(FREE_PRODUCT_DESCRIPTION_COLUMN_INDEX));
		group.add(columnModel.getColumn(FREE_UNIT_COLUMN_INDEX));
		group.add(columnModel.getColumn(FREE_QUANTITY_COLUMN_INDEX));
		header.addColumnGroup(group);
	}
	
	public void addNewRow() {
		if (getRowCount() > 0 && !tableModel.getRowItem(getRowCount() - 1).isUpdating()) {
			return;
		}
		
		PromoType2Rule rule = new PromoType2Rule();
		rule.setParent(promo);
		tableModel.addItem(rule);
		selectAndEditCellAt(getRowCount() - 1, 0);
	}

	public void setPromo(Promo promo) {
		this.promo = promo;
		tableModel.setPromo(promo);
	}
	
	private void initializeModelListener() {
		getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				final int row = e.getFirstRow();
				final int column = e.getColumn();
				
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						switch (column) {
						case PROMO_PRODUCT_CODE_COLUMN_INDEX:
							selectAndEditCellAt(row, PROMO_UNIT_COLUMN_INDEX);
							break;
						case PROMO_UNIT_COLUMN_INDEX:
							selectAndEditCellAt(row, PROMO_QUANTITY_COLUMN_INDEX);
							break;
						case PROMO_QUANTITY_COLUMN_INDEX:
							selectAndEditCellAt(row, FREE_PRODUCT_CODE_COLUMN_INDEX);
							break;
						case FREE_PRODUCT_CODE_COLUMN_INDEX:
							selectAndEditCellAt(row, FREE_UNIT_COLUMN_INDEX);
							break;
						case FREE_UNIT_COLUMN_INDEX:
							selectAndEditCellAt(row, FREE_QUANTITY_COLUMN_INDEX);
							break;
						case FREE_QUANTITY_COLUMN_INDEX:
							if (isLastRowSelected() && getCurrentlySelectedRowItem().isValid()) {
								addNewRow();
							}
							break;
						}
					}

				});
			}
		});
	}
	
	private boolean isLastRowSelected() {
		return getSelectedRow() + 1 == tableModel.getRowCount();
	}
	
	public PromoType2RuleRowItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	private class ProductCodeCellEditor extends MagicCellEditor {

		private String fieldName;
		
		public ProductCodeCellEditor(JTextField textField, boolean promo) {
			super(textField);
			fieldName = (promo) ? "Promo" : "Free";
		}
		
		@Override
		public boolean stopCellEditing() {
			String code = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(code)) {
				showErrorMessage(fieldName + " Product Code must be specified");
			} else {
				Product product = productService.findProductByCode(code);
				if (product == null) {
					showErrorMessage("No product matching code specified");
				} else {
					valid = true;
				}
			}
			return (valid) ? super.stopCellEditing() : false;
		}

	}
	
	private class UnitCellEditor extends MagicCellEditor {
		
		private boolean promo;
		private String fieldName;
		
		public UnitCellEditor(JTextField textField, boolean promo) {
			super(textField);
			this.promo = promo;
			fieldName = (promo) ? "Promo" : "Free";
		}
		
		@Override
		public boolean stopCellEditing() {
			String unit = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(unit)) {
				showErrorMessage(fieldName + " Unit must be specified");
			} else {
				PromoType2RuleRowItem rowItem = getCurrentlySelectedRowItem();
				if (promo && !rowItem.getPromoProduct().hasUnit(unit)) {
					showErrorMessage("Product does not have unit specified");
				} else if (!promo && !rowItem.getFreeProduct().hasUnit(unit)) {
					showErrorMessage("Product does not have unit specified");
				} else {
					valid = true;
				}
			}
			return (valid) ? super.stopCellEditing() : false;
		}
		
	}
	
	private class QuantityCellEditor extends MagicCellEditor {
		
		private String fieldName;
		
		public QuantityCellEditor(JTextField textField, boolean promo) {
			super(textField);
			fieldName = (promo) ? "Promo" : "Free";
		}
		
		@Override
		public boolean stopCellEditing() {
			String quantity = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(quantity)) {
				showErrorMessage(fieldName + " Quantity must be specified");
			} else if (Integer.parseInt(quantity) == 0) {
				showErrorMessage("Quantity must be greater than 0");
			} else {
				valid = true;
			}
			return (valid) ? super.stopCellEditing() : false;
		}
		
	}

	public void removeCurrentlySelectedRule() {
		if (getSelectedRow() != -1) {
			if (getCurrentlySelectedRowItem().isValid()) { // check valid row to prevent deleting the blank row
				if (confirm("Do you wish to delete the selected item?")) {
					doDeleteCurrentlySelectedRule();
				}
			}
		}
	}

	private void doDeleteCurrentlySelectedRule() {
		int selectedRowIndex = getSelectedRow();
		PromoType2Rule rule = getCurrentlySelectedRowItem().getRule();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		promo.getPromoType2Rules().remove(rule);
		tableModel.removeRule(selectedRowIndex);
		
		if (tableModel.hasItems()) {
			if (selectedRowIndex == getModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}
	
}