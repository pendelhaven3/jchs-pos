package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.CustomAction;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.service.CreditCardService;
import com.pj.magic.util.FormatterUtil;

@Component
public class CreditCardStatementListPanel extends StandardMagicPanel {
	
	private static final int CUSTOMER_NUMBER_COLUMN_INDEX = 0;
	private static final int STATEMENT_DATE_COLUMN_INDEX = 1;
	private static final int AMOUNT_COLUMN_INDEX = 2;
	private static final int STATUS_COLUMN_INDEX = 3;
	
	@Autowired private CreditCardService creditCardService;
	
	private MagicListTable table;
	private CreditCardStatementsTableModel tableModel;
	
	@Override
	public void initializeComponents() {
		tableModel = new CreditCardStatementsTableModel();
		table = new MagicListTable(tableModel);
		
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay() {
		List<CreditCardStatement> statements = creditCardService.getAllCreditCardStatements();
		tableModel.setItems(statements);
		if (!statements.isEmpty()) {
			table.selectFirstRow();
		}
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(5), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		
		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, c);
	}
	
	@Override
	protected void registerKeyBindings() {
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
		
		table.onEnterKeyAndDoubleClick(new CustomAction() {
			
			@Override
			public void doAction() {
				selectStatement();
			}
		});
	}
	
	protected void selectStatement() {
		getMagicFrame().switchToCreditCardStatementPanel(tableModel.getItem(table.getSelectedRow()));
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPurchasePaymentsMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

	private class CreditCardStatementsTableModel extends ListBackedTableModel<CreditCardStatement> {

		private final String[] columnNames = {"Customer Number", "Statement Date", "Amount", "Status"};
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			CreditCardStatement statement = getItem(rowIndex);
			switch (columnIndex) {
			case CUSTOMER_NUMBER_COLUMN_INDEX:
				return statement.getCustomerNumber();
			case STATEMENT_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(statement.getStatementDate());
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(statement.getTotalAmount());
			case STATUS_COLUMN_INDEX:
				return statement.getStatus();
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}

		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case AMOUNT_COLUMN_INDEX:
				return Number.class;
			default:
				return Object.class;
			}
		}
		
	}
	
}
