package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.PaymentTerminalAssignmentsTableModel;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.service.PaymentTerminalService;
import com.pj.magic.util.ComponentUtil;

@Component
public class PaymentTerminalAssignmentListPanel extends StandardMagicPanel {

	@Autowired private PaymentTerminalService paymentTerminalService;
	
	private MagicListTable table;
	private PaymentTerminalAssignmentsTableModel tableModel = new PaymentTerminalAssignmentsTableModel();
	
	public void updateDisplay() {
		List<PaymentTerminalAssignment> assignments = paymentTerminalService.getAllPaymentTerminalAssignments();
		tableModel.setPaymentTerminalAssignments(assignments);
		if (!assignments.isEmpty()) {
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
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectPaymentTerminal();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectPaymentTerminal();
			}
			
		});
	}

	protected void selectPaymentTerminal() {
		PaymentTerminalAssignment assignment = tableModel.getPaymentTerminalAssignment(table.getSelectedRow());
		getMagicFrame().switchToEditPaymentTerminalAssignmentPanel(assignment);
	}

	private void switchToNewPaymentTerminalAssignmentPanel() {
		getMagicFrame().switchToAddNewPaymentTerminalAssignmentPanel();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToAdminMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton postButton = new MagicToolBarButton("plus", "New");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewPaymentTerminalAssignmentPanel();
			}
		});
		toolBar.add(postButton);
	}
	
}
