package com.pj.magic.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.service.ManufacturerService;

@Component
public class SelectManufacturerDialog extends MagicDialog {

	private static final String SELECT_MANUFACTURER_ACTION = "selectManufacturer";
	
	@Autowired 
	private ManufacturerService manufacturerService;
	
	private Manufacturer selectedManufacturer;
	private JTable table;
	private ManufacturersTableModel tableModel = new ManufacturersTableModel();
	
	public SelectManufacturerDialog() {
		setSize(500, 400);
		setLocationRelativeTo(null);
		setTitle("Select Manufacturer");
		addContents();
		registerKeyBindings();
	}

	private void addContents() {
		table = new MagicListTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);	
	}

	private void registerKeyBindings() {
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECT_MANUFACTURER_ACTION);
		
		table.getActionMap().put(SELECT_MANUFACTURER_ACTION, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectManufacturer();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectManufacturer();
			}
		});
	}

	protected void selectManufacturer() {
		if (table.getSelectedRow() != -1) {
			selectedManufacturer = tableModel.getItem(table.getSelectedRow());
			setVisible(false);
		}
	}

	public Manufacturer getSelectedManufacturer() {
		return selectedManufacturer;
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		selectedManufacturer = null;
	}
	
	public void searchManufacturers() {
		selectedManufacturer = null;
		List<Manufacturer> manufacturers = manufacturerService.getAllManufacturers();
		tableModel.setItems(manufacturers);
		table.changeSelection(0, 0, false, false);
	}
	
	private class ManufacturersTableModel extends ListBackedTableModel<Manufacturer> {

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Manufacturer manufacturer = getItem(rowIndex);
			return manufacturer.getName();
		}

		@Override
		protected String[] getColumnNames() {
			return new String[] {"Manufacturer"};
		}

	}
	
}
