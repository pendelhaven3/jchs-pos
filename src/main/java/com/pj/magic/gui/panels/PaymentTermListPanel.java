package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.PaymentTermsTableModel;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.service.PaymentTermService;
import com.pj.magic.util.ComponentUtil;

@Component
public class PaymentTermListPanel extends StandardMagicPanel {

    private static final long serialVersionUID = -7922947577520088853L;

    private static final String EDIT_PAYMENT_TERM_ACTION_NAME = "editPaymentTerm";
	
	@Autowired private PaymentTermService paymentTermService;
	
	private JTable table;
	private PaymentTermsTableModel tableModel = new PaymentTermsTableModel();
	
	public void updateDisplay() {
		List<PaymentTerm> paymentTerms = paymentTermService.getAllPaymentTerms();
		tableModel.setPaymentTerms(paymentTerms);
		if (!paymentTerms.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
	}

	@Override
	protected void initializeComponents() {
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
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), EDIT_PAYMENT_TERM_ACTION_NAME);
		table.getActionMap().put(EDIT_PAYMENT_TERM_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectPaymentTerm();
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					selectPaymentTerm();
				}
			}
		});
	}

	protected void selectPaymentTerm() {
		PaymentTerm paymentTerm = tableModel.getPaymentTerm(table.getSelectedRow());
		getMagicFrame().switchToEditPaymentTermPanel(paymentTerm);
	}

	private void switchToNewPaymentTermPanel() {
		getMagicFrame().switchToAddNewPaymentTermPanel();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToRecordsMaintenanceMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton postButton = new MagicToolBarButton("plus", "New");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewPaymentTermPanel();
			}
		});
		toolBar.add(postButton);
	}
	
}
