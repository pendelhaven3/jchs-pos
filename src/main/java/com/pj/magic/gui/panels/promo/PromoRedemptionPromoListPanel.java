package com.pj.magic.gui.panels.promo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.panels.StandardMagicPanel;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.Promo;
import com.pj.magic.service.impl.PromoService;
import com.pj.magic.util.ComponentUtil;

@Component
public class PromoRedemptionPromoListPanel extends StandardMagicPanel {

	private static final int PROMO_NUMBER_COLUMN_INDEX = 0;
	private static final int NAME_COLUMN_INDEX = 1;
	
	@Autowired private PromoService promoService;
	
	private MagicListTable table;
	private PromosTableModel tableModel;
	
	public void updateDisplay() {
		List<Promo> promos = promoService.getAllActivePromos();
		tableModel.setPromos(promos);
		if (!promos.isEmpty()) {
			table.changeSelection(0, 0);
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
		int currentRow = 0;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
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

	private void selectPromo() {
		Promo promo = tableModel.getPromo(table.getSelectedRow());
		if (promo.isPromoType4()) {
			getMagicFrame().switchToPromoPointsPanel(promo); 
		} else {
			getMagicFrame().switchToPromoRedemptionListPanel(promo); 
		}
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

	private class PromosTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Promo No.", "Promo Name"};
		
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
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Promo promo = promos.get(rowIndex);
			switch (columnIndex) {
			case PROMO_NUMBER_COLUMN_INDEX:
				return promo.getPromoNumber().toString();
			case NAME_COLUMN_INDEX:
				return promo.getName();
			default:
				return null;
			}
		}
		
	}
	
}