package com.pj.magic.gui.panels.promo;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.panels.StandardMagicPanel;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoType;
import com.pj.magic.service.PromoRedemptionService;
import com.pj.magic.service.impl.PromoService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.HtmlUtil;

@Component
public class PromoRedemptionListPanel extends StandardMagicPanel {

	private static final int PROMO_REDEMPTION_NUMBER_COLUMN_INDEX = 0;
	private static final int CUSTOMER_COLUMN_INDEX = 1;
	private static final int STATUS_COLUMN_INDEX = 2;
	private static final int POST_DATE_COLUMN_INDEX = 3;
	
	@Autowired private PromoRedemptionService promoRedemptionService;
	@Autowired private PromoService promoService;
	
	private MagicListTable table;
	private PromoRedemptionsTableModel tableModel;
	private Promo promo;
	private JLabel promoNameLabel;
	private JLabel promoMechanicsLabel;
	
	public void updateDisplay(Promo promo) {
		this.promo = promo = promoService.getPromo(promo.getId());
		List<PromoRedemption> promoRedemptions = promoRedemptionService.getPromoRedemptionsByPromo(promo);
		tableModel.setPromoRedemptions(promoRedemptions);
		if (!promoRedemptions.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
		promoNameLabel.setText(promo.getName());
		promoMechanicsLabel.setText(HtmlUtil.html(promo.getMechanicsDescription()));
	}

	@Override
	protected void initializeComponents() {
		promoMechanicsLabel = new JLabel();
		
		tableModel = new PromoRedemptionsTableModel();
		table = new MagicListTable(tableModel);
		
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Promo:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		promoNameLabel = ComponentUtil.createLabel(300);
		mainPanel.add(promoNameLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Mechanics:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		promoMechanicsLabel.setPreferredSize(new Dimension(600, 50));
		mainPanel.add(promoMechanicsLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(600, 200));
		mainPanel.add(scrollPane, c);
	}

	@Override
	protected void registerKeyBindings() {
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectPromoRedemption();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectPromoRedemption();
			}
			
		});
		
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}

	protected void selectPromoRedemption() {
		PromoRedemption promoRedemption = tableModel.getPromoRedemption(table.getSelectedRow());
		PromoType promoType = promoRedemption.getPromo().getPromoType();
		
		if (promoType.isType5()) {
			getMagicFrame().switchToPromoRedemptionRebatesPanel(promoRedemption);
		} else {
			getMagicFrame().switchToPromoRedemptionPanel(promoRedemption);
		}
	}

	private void switchToNewPromoRedemptionPanel() {
		PromoRedemption promoRedemption = new PromoRedemption();
		promoRedemption.setPromo(promo);
		
		if (promo.isPromoType5()) {
			getMagicFrame().switchToPromoRedemptionRebatesPanel(promoRedemption);
		} else {
			getMagicFrame().switchToPromoRedemptionPanel(promoRedemption);
		}
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPromoRedemptionPromoListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton postButton = new MagicToolBarButton("plus", "New");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewPromoRedemptionPanel();
			}
		});
		toolBar.add(postButton);
	}
	
	private class PromoRedemptionsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Promo Redemption No.", "Customer", "Status", "Post Date"};

		private List<PromoRedemption> promoRedemptions = new ArrayList<>();
		
		public void setPromoRedemptions(List<PromoRedemption> promoRedemptions) {
			this.promoRedemptions = promoRedemptions;
			fireTableDataChanged();
		}
		
		public PromoRedemption getPromoRedemption(int rowIndex) {
			return promoRedemptions.get(rowIndex);
		}

		@Override
		public int getRowCount() {
			return promoRedemptions.size();
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
			PromoRedemption promoRedemption = promoRedemptions.get(rowIndex);
			switch (columnIndex) {
			case PROMO_REDEMPTION_NUMBER_COLUMN_INDEX:
				return promoRedemption.getPromoRedemptionNumber().toString();
			case CUSTOMER_COLUMN_INDEX:
				return promoRedemption.getCustomer().getName();
			case STATUS_COLUMN_INDEX:
				return promoRedemption.getStatus();
			case POST_DATE_COLUMN_INDEX:
				return promoRedemption.isPosted() ? FormatterUtil.formatDate(promoRedemption.getPostDate()) : null;
			default:
				return null;
			}
		}
		
	}
	
}