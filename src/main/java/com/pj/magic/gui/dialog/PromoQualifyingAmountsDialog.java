package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.search.PromoSearchCriteria;
import com.pj.magic.service.impl.PromoService;
import com.pj.magic.util.FormatterUtil;

@Component
public class PromoQualifyingAmountsDialog extends MagicDialog {

	private static final int NAME_COLUMN_INDEX = 0;
	private static final int QUALIFYING_AMOUNT_COLUMN_INDEX = 1;
	
	@Autowired private PromoService promoService;
	
	private MagicListTable table;
	private PromosTableModel tableModel;
	private SalesRequisition salesRequisition;
	private SalesInvoice salesInvoice;
	
	public PromoQualifyingAmountsDialog() {
		setSize(600, 300);
		setLocationRelativeTo(null);
		setTitle("Qualifying Amounts for Promos");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		tableModel = new PromosTableModel();
		table = new MagicListTable(tableModel);
	}

	private void registerKeyBindings() {
		// none
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH;
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(400, 100));
		add(scrollPane, c);
	}
	
	public void updateDisplay(SalesRequisition salesRequisition) {
		this.salesRequisition = salesRequisition;
		salesInvoice = null;
		
		List<Promo> promos = getAllActivePromosUsingQualifyingAmounts();
		tableModel.setPromos(promos);
		if (!promos.isEmpty()) {
			table.selectFirstRow();
		}
	}

	public void updateDisplay(SalesInvoice salesInvoice) {
		this.salesInvoice = salesInvoice;
		salesRequisition = null;
		
		List<Promo> promos = getAllActivePromosUsingQualifyingAmounts();
		tableModel.setPromos(promos);
		if (!promos.isEmpty()) {
			table.selectFirstRow();
		}
	}

	private List<Promo> getAllActivePromosUsingQualifyingAmounts() {
		PromoSearchCriteria criteria = new PromoSearchCriteria();
		criteria.setActive(true);
		criteria.setPromoTypes(Arrays.asList(PromoType.PROMO_TYPE_1, PromoType.PROMO_TYPE_3));
		
		return promoService.search(criteria);
	}

	private class PromosTableModel extends AbstractTableModel {
		
		private final String[] columnNames = {"Promo", "Qualifying Amount"};
		
		private List<Promo> promos = new ArrayList<>();
		
		public void setPromos(List<Promo> promos) {
			this.promos = promos;
			fireTableDataChanged();
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
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == QUALIFYING_AMOUNT_COLUMN_INDEX) {
				return Number.class;
			} else {
				return String.class;
			}
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Promo promo = promos.get(rowIndex);
			switch (columnIndex) {
			case NAME_COLUMN_INDEX:
				return promo.getName();
			case QUALIFYING_AMOUNT_COLUMN_INDEX:
				if (promo.isPromoType1()) {
					if (salesRequisition != null) {
						return FormatterUtil.formatAmount(promo.getPromoType1Rule().getQualifyingAmount(salesRequisition));
					} else if (salesInvoice != null) {
						return FormatterUtil.formatAmount(promo.getPromoType1Rule().getQualifyingAmount(salesInvoice));
					}
				} else if (promo.isPromoType3()) {
					if (salesRequisition != null) {
						return FormatterUtil.formatAmount(promo.getPromoType3Rule().getQualifyingAmount(salesRequisition));
					} else {
						return FormatterUtil.formatAmount(promo.getPromoType3Rule().getQualifyingAmount(salesInvoice));
					}
				} else {
					return null;
				}
			default:
				throw new RuntimeException("Error fetching invalid column index: " + columnIndex);
			}
		}
		
	}

}