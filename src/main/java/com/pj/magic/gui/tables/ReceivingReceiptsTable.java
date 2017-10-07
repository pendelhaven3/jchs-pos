package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.panels.ReceivingReceiptListPanel;
import com.pj.magic.gui.tables.models.ReceivingReceiptsTableModel;
import com.pj.magic.model.ReceivingReceipt;

@Component
public class ReceivingReceiptsTable extends MagicListTable {

	private static final String SELECT_RECEIVING_RECEIPT_ACTION_NAME = "selectReceivingReceipt";
	
	@Autowired private ReceivingReceiptsTableModel tableModel;
	
	@Autowired
	public ReceivingReceiptsTable(ReceivingReceiptsTableModel tableModel) {
		super(tableModel);
	}

	@PostConstruct
	public void initialize() {
		initializeColumns();
		registerKeyBindings();
	}
	
	private void initializeColumns() {
		columnModel.getColumn(ReceivingReceiptsTableModel.RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX)
			.setPreferredWidth(60);
		columnModel.getColumn(ReceivingReceiptsTableModel.RECEIVED_DATE_COLUMN_INDEX)
			.setPreferredWidth(100);
		columnModel.getColumn(ReceivingReceiptsTableModel.SUPPLIER_COLUMN_INDEX)
			.setPreferredWidth(200);
		columnModel.getColumn(ReceivingReceiptsTableModel.REFERENCE_NUMBER_COLUMN_INDEX)
			.setPreferredWidth(120);
		columnModel.getColumn(ReceivingReceiptsTableModel.NET_AMOUNT_COLUMN_INDEX)
			.setPreferredWidth(100);
		columnModel.getColumn(ReceivingReceiptsTableModel.STATUS_COLUMN_INDEX)
			.setPreferredWidth(60);
	}

	private void registerKeyBindings() {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECT_RECEIVING_RECEIPT_ACTION_NAME);
		getActionMap().put(SELECT_RECEIVING_RECEIPT_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectReceivingReceipt();
			}
		});
		
		addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectReceivingReceipt();
			}
		});
	}

	protected void selectReceivingReceipt() {
		ReceivingReceiptListPanel panel = (ReceivingReceiptListPanel)
				SwingUtilities.getAncestorOfClass(ReceivingReceiptListPanel.class, this);
		panel.displayReceivingReceiptDetails(getCurrentlySelectedReceivingReceipt());
	}

	public void setReceivingReceipts(List<ReceivingReceipt> receivingReceipts) {
		tableModel.setReceivingReceipts(receivingReceipts);
		if (!receivingReceipts.isEmpty()) {
			changeSelection(0, 0, false, false);
		}
	}

	public ReceivingReceipt getCurrentlySelectedReceivingReceipt() {
		return tableModel.getReceivingReceipt(getSelectedRow());
	}

}
