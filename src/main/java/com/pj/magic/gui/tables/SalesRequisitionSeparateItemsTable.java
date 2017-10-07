package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.MagicCellEditor;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.tables.models.SalesRequisitionSeparateItemsTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.SalesRequisitionService;
import com.pj.magic.util.KeyUtil;

@Component
public class SalesRequisitionSeparateItemsTable extends MagicListTable {

	public static final int CODE_COLUMN_INDEX = 0;
	public static final int DESCRIPTION_COLUMN_INDEX = 1;
	
	@Autowired private ProductService productService;
	@Autowired private SalesRequisitionService salesRequisitionService;
	@Autowired private SalesRequisitionSeparateItemsTableModel tableModel;
	@Autowired private SelectProductDialog selectProductDialog;
	
	private String previousSelectProductCriteria;
	
	@Autowired
	public SalesRequisitionSeparateItemsTable(SalesRequisitionSeparateItemsTableModel tableModel) {
		super(tableModel);
		initializeColumns();
		initializeModelListener();
		registerKeyBindings();
	}

	private void initializeColumns() {
		columnModel.getColumn(CODE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
		
		MagicTextField textField = new MagicTextField();
		textField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		textField.addKeyListener(new KeyAdapter() {
			
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
		columnModel.getColumn(CODE_COLUMN_INDEX).setCellEditor(new ProductCodeCellEditor(textField));
	}

	public void addProduct() {
		if (!tableModel.isLastProductBlank()) {
			int newRowIndex = getRowCount();
			tableModel.addProduct(new Product());
			
			changeSelection(newRowIndex, 0, false, false);
			editCellAt(newRowIndex, 0);
			getEditorComponent().requestFocusInWindow();
		}
	}

	public void removeCurrentlySelectedItem() {
		int selectedRow = getSelectedRow();
		tableModel.removeProduct(selectedRow);
		if (tableModel.hasItems()) {
			if (selectedRow == getModel().getRowCount()) {
				changeSelection(selectedRow - 1, 0, false, false);
			} else {
				changeSelection(selectedRow, 0, false, false);
			}
		}
	}
	
	public boolean isCurrentlySelectedProductBlank() {
		return isLastRowSelected() && tableModel.isLastProductBlank();
	}

	private boolean isLastRowSelected() {
		return getSelectedRow() == getRowCount() - 1;
	}

	private void initializeModelListener() {
		getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				final int column = e.getColumn();
				
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						switch (column) {
						case CODE_COLUMN_INDEX:
							addProduct();
						}
					}
				});
			}
		});
	}
	
	private void registerKeyBindings() {
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isEditing()) {
					getCellEditor().cancelCellEditing();
					if (tableModel.isLastProductBlank()) {
						tableModel.removeLastProduct();
					}
				}
			}
		});
		
		onF5Key(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isCodeFieldSelected() && isCurrentCellEditable()) {
					if (!isEditing()) {
						editCellAt(getSelectedRow(), CODE_COLUMN_INDEX);
					}
					String criteria = (String)getCellEditor().getCellEditorValue();
					openSelectProductDialog(criteria, criteria);
				}
			}
		});
		
		onF4Key(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isCodeFieldSelected() && isCurrentCellEditable()) {
					openSelectProductDialogUsingPreviousCriteria();
				}
			}
		});
	}
	
	private boolean isCurrentCellEditable() {
		return tableModel.isCellEditable(getSelectedRow(), getSelectedColumn());
	}

	private void openSelectProductDialog(String criteria, String currentlySelectedCode) {
		previousSelectProductCriteria = criteria;
		
		selectProductDialog.searchProducts(criteria, currentlySelectedCode);
		selectProductDialog.setVisible(true);
		
		Product product = selectProductDialog.getSelectedProduct();
		if (product != null) {
			((JTextField)getEditorComponent()).setText(product.getCode());
			getCellEditor().stopCellEditing();
		}
	}
	
	protected void openSelectProductDialogUsingPreviousCriteria() {
		if (!isLastRowSelected()) {
			return;
		}
		
		if (!isEditing()) {
			editCellAt(getSelectedRow(), getSelectedColumn());
		}

		openSelectProductDialog(previousSelectProductCriteria,
				(String)getValueAt(getSelectedRow() - 1, CODE_COLUMN_INDEX));
	}

	private boolean isCodeFieldSelected() {
		return getSelectedColumn() == CODE_COLUMN_INDEX;
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
				showErrorMessage("Product code must be specified");
			} else {
				Product product = productService.findProductByCode(code);
				if (product == null) {
					showErrorMessage("No product matching code specified");
				} else if (isAlreadyAdded(product)) {
					showErrorMessage("Duplicate item");
				} else {
					valid = true;
				}
			}
			return (valid) ? super.stopCellEditing() : false;
		}

	}

	private boolean isAlreadyAdded(Product product) {
		return salesRequisitionService.getSalesRequisitionSeparateItemsList().getProducts().contains(product);
	}
	
}
