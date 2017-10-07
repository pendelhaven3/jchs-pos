package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.MagicCellEditor;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.tables.models.PromoType3RulePromoProductsTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.model.PromoType3Rule;
import com.pj.magic.model.PromoType3RulePromoProduct;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.KeyUtil;

@Component
public class PromoType3RulePromoProductsTable extends MagicTable {

	public static final int CODE_COLUMN_INDEX = 0;
	public static final int DESCRIPTION_COLUMN_INDEX = 1;
	
	@Autowired private PromoType3RulePromoProductsTableModel tableModel;
	@Autowired private ProductService productService;
	@Autowired private SelectProductDialog selectProductDialog;
	
	private PromoType3Rule rule;
	
	@Autowired
	public PromoType3RulePromoProductsTable(PromoType3RulePromoProductsTableModel tableModel) {
		super(tableModel);
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
		}
		
		clearSelection();
		tableModel.setRule(rule);
		if (!rule.getPromoProducts().isEmpty()) {
			selectFirstRow();
		}
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
		return getSelectedColumn() == CODE_COLUMN_INDEX;
	}

	private void initializeColumns() {
		MagicTextField codeField = new MagicTextField()	;		
		codeField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		codeField.addKeyListener(new KeyAdapter() {
			
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
		columnModel.getColumn(CODE_COLUMN_INDEX).setCellEditor(new ProductCodeCellEditor(codeField));
		
		columnModel.getColumn(CODE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(DESCRIPTION_COLUMN_INDEX).setPreferredWidth(500);
	}

	public void addNewRow() {
		if (tableModel.hasNewRowNotYetSaved()) {
			return;
		}
		
		PromoType3RulePromoProduct promoProduct = new PromoType3RulePromoProduct();
		promoProduct.setParent(rule);
		tableModel.addItem(promoProduct);
		selectAndEditCellAt(getRowCount() - 1, 0);
	}

	public void setRule(PromoType3Rule rule) {
		this.rule = rule;
		tableModel.setRule(rule);
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
						case CODE_COLUMN_INDEX:
							model.fireTableCellUpdated(row, DESCRIPTION_COLUMN_INDEX);
							
							if (isLastRowSelected() && !getCurrentlySelectedPromoProduct().isNew()) {
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
	
	public PromoType3RulePromoProduct getCurrentlySelectedPromoProduct() {
		return tableModel.getPromoProduct(getSelectedRow());
	}
	
	private class ProductCodeCellEditor extends MagicCellEditor {

		public ProductCodeCellEditor(JTextField textField) {
			super(textField);
		}
		
		@Override
		public boolean stopCellEditing() {
			String code = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(code)) {
				showErrorMessage("Product Code must be specified");
			} else {
				Product product = productService.findProductByCode(code);
				if (product == null) {
					showErrorMessage("No product matching code specified");
				} else if (rule.hasPromoProduct(product)) {
					showErrorMessage("Product is already included in promo");
				} else {
					valid = true;
				}
			}
			return (valid) ? super.stopCellEditing() : false;
		}

	}
	
	public void removeCurrentlySelectedPromoProduct() {
		if (getSelectedRow() != -1) {
			if (!getCurrentlySelectedPromoProduct().isNew()) { // check valid row to prevent deleting the blank row
				if (confirm("Do you wish to delete the selected item?")) {
					doDeleteCurrentlySelectedRule();
				}
			}
		}
	}

	// TODO: See if this can be promoted to MagicTable class
	@Override
	protected boolean confirm(String message) {
		int confirm = JOptionPane.showConfirmDialog(getRootPane(), message, "Confirmation", JOptionPane.YES_NO_OPTION);
		return confirm == JOptionPane.YES_OPTION;
	}
	
	private void doDeleteCurrentlySelectedRule() {
		int selectedRowIndex = getSelectedRow();
		PromoType3RulePromoProduct promoProduct = getCurrentlySelectedPromoProduct();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		rule.getPromoProducts().remove(promoProduct);
		tableModel.removePromoProduct(selectedRowIndex);
		
		if (tableModel.hasItems()) {
			if (selectedRowIndex == getModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}

	public void clear() {
		rule = null;
		tableModel.clear();
	}
	
}