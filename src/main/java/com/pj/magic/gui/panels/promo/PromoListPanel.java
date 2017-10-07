package com.pj.magic.gui.panels.promo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchPromosDialog;
import com.pj.magic.gui.panels.StandardMagicPanel;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.Promo;
import com.pj.magic.model.search.PromoSearchCriteria;
import com.pj.magic.service.impl.PromoService;
import com.pj.magic.util.FormatterUtil;

@Component
public class PromoListPanel extends StandardMagicPanel {

	private static final int PROMO_NUMBER_COLUMN_INDEX = 0;
	private static final int NAME_COLUMN_INDEX = 1;
	private static final int ACTIVE_COLUMN_INDEX = 2;
	private static final int START_DATE_COLUMN_INDEX = 3;
	private static final int END_DATE_COLUMN_INDEX = 4;
	
	@Autowired private PromoService promoService;
	@Autowired private SearchPromosDialog searchPromosDialog;
	
	private MagicListTable table;
	private PromosTableModel tableModel;
	
	public void updateDisplay() {
		List<Promo> promos = promoService.getAllActivePromos();
		tableModel.setPromos(promos);
		if (!promos.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
	}

	@Override
	protected void initializeComponents() {
		tableModel = new PromosTableModel();
		table = new MagicListTable(tableModel);
		
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		mainPanel.add(new JScrollPane(table), c);
	}

	@Override
	protected void registerKeyBindings() {
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectPromo();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectPromo();
			}
			
		});
		
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}

	protected void selectPromo() {
		Promo promo = tableModel.getPromo(table.getSelectedRow());
		getMagicFrame().switchToPromoPanel(promo);
	}

	private void switchToNewPromoPanel() {
		getMagicFrame().switchToPromoPanel(new Promo());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton postButton = new MagicToolBarButton("plus", "New Promo");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewPromoPanel();
			}
		});
		toolBar.add(postButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(e -> searchPromos());
		toolBar.add(searchButton);
	}
	
	private void searchPromos() {
		searchPromosDialog.setVisible(true);
		
		PromoSearchCriteria criteria = searchPromosDialog.getSearchCriteria();
		if (criteria != null) {
			List<Promo> promos = promoService.search(criteria);
			tableModel.setPromos(promos);
			if (!promos.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}
	
	private class PromosTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Promo No.", "Name", "Active?", "Start Date", "End Date"};

		private List<Promo> promos = new ArrayList<>();
		
		public void setPromos(List<Promo> promos) {
			this.promos = promos;
			fireTableDataChanged();
		}
		
		public Promo getPromo(int rowIndex) {
			return promos.get(rowIndex);
		}

		@Override
		public int getRowCount() {
			return promos.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Promo promo = promos.get(rowIndex);
			switch (columnIndex) {
			case PROMO_NUMBER_COLUMN_INDEX:
				return promo.getPromoNumber().toString();
			case NAME_COLUMN_INDEX:
				return promo.getName();
			case ACTIVE_COLUMN_INDEX:
				return promo.isActive() ? "Yes" : "No";
			case START_DATE_COLUMN_INDEX:
				return promo.getStartDate() != null ? FormatterUtil.formatDate(promo.getStartDate()) : null;
			case END_DATE_COLUMN_INDEX:
				return promo.getEndDate() != null ? FormatterUtil.formatDate(promo.getEndDate()) : null;
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
	}
	
}