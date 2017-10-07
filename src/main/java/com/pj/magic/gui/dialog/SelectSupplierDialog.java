package com.pj.magic.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.SuppliersTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.model.Supplier;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.SupplierService;

@Component
public class SelectSupplierDialog extends MagicDialog {

	private static final String SELECT_SUPPLIER_ACTION = "selectSupplier";
	
	@Autowired private ProductService productService;
	@Autowired private SupplierService supplierService;
	
	private Supplier selectedSupplier;
	private JTable table;
	private SuppliersTableModel tableModel = new SuppliersTableModel();
	
	public SelectSupplierDialog() {
		setSize(500, 400);
		setLocationRelativeTo(null);
		setTitle("Select Supplier");
		addContents();
		registerKeyBindings();
	}

	private void addContents() {
		table = new MagicListTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);	
	}

	private void registerKeyBindings() {
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), SELECT_SUPPLIER_ACTION);
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECT_SUPPLIER_ACTION);
		table.getActionMap().put(SELECT_SUPPLIER_ACTION, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectSupplier();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectSupplier();
			}
		});
	}

	protected void selectSupplier() {
		if (table.getSelectedRow() != -1) {
			selectedSupplier = tableModel.getSupplier(table.getSelectedRow());
			setVisible(false);
		}
	}

	public Supplier getSelectedSupplier() {
		return selectedSupplier;
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		selectedSupplier = null; // TODO: necessary?
	}
	
	public void searchAvailableSuppliers(Product product) {
		selectedSupplier = null;
		List<Supplier> suppliers = productService.getAvailableSuppliers(product);
		tableModel.setSuppliers(suppliers);
		if (!suppliers.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
	}
	
	public void searchSuppliers(String supplierCode) {
		selectedSupplier = null;
		List<Supplier> suppliers = supplierService.getAllSuppliers();
		tableModel.setSuppliers(suppliers);
		
		if (suppliers.isEmpty()) {
			return;
		}
		
		int selectedRow = 0;
		if (!StringUtils.isEmpty(supplierCode)) {
			for (int i = 0; i < suppliers.size(); i++) {
				if (suppliers.get(i).getCode().startsWith(supplierCode)) {
					selectedRow = i;
					break;
				}
			}
		}
		table.changeSelection(selectedRow, 0, false, false);
	}
	
}
