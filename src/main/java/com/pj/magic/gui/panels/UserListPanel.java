package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.CustomAction;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.User;
import com.pj.magic.service.UserService;

@Component
public class UserListPanel extends StandardMagicPanel {

	private static final int USERNAME_COLUMN_INDEX = 0;
	private static final int SUPERVISOR_COLUMN_INDEX = 1;
	private static final int MODIFY_PRICING_COLUMN_INDEX = 2;
	
	private static final ImageIcon checkIcon = createImageIcon("/images/small_check.png");
	
	@Autowired private UserService userService;
	
	private MagicListTable table;
	private UsersTableModel tableModel;
	
	@Override
	protected void initializeComponents() {
		tableModel = new UsersTableModel();
		table = new MagicListTable(tableModel);
		
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay() {
		List<User> users = userService.getAllUsers();
		tableModel.setItems(users);
		if (!users.isEmpty()) {
			table.changeSelection(0, 0, false, false);
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
		mainPanel.add(new JScrollPane(table), c);
	}

	@Override
	protected void registerKeyBindings() {
		table.onEnterKeyAndDoubleClick(new CustomAction() {
			
			@Override
			public void doAction() {
				selectUser();
			}
		});
	}

	protected void selectUser() {
		User user = tableModel.getItem(table.getSelectedRow());
		getMagicFrame().switchToEditUserPanel(user);
	}

	private void switchToNewUserPanel() {
		getMagicFrame().switchToAddNewUserPanel();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToAdminMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton postButton = new MagicToolBarButton("plus", "Add New User");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewUserPanel();
			}
		});
		
		toolBar.add(postButton);
	}
	
	private class UsersTableModel extends ListBackedTableModel<User> {

		private final String[] columnNames = {"Username", "Supervisor", "Modify Pricing?"};
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			User user = getItem(rowIndex);
			switch (columnIndex) {
			case USERNAME_COLUMN_INDEX:
				return user.getUsername();
			case SUPERVISOR_COLUMN_INDEX:
				return user.isSupervisor() ? checkIcon : null;
			case MODIFY_PRICING_COLUMN_INDEX:
				return user.isSupervisor() || user.isModifyPricing() ? checkIcon : null;
			default:
				return null;
			}
		}

		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == SUPERVISOR_COLUMN_INDEX || columnIndex == MODIFY_PRICING_COLUMN_INDEX) {
				return ImageIcon.class;
			} else {
				return Object.class;
			}
		}
		
	}
	
	private static ImageIcon createImageIcon(String path) {
		URL imgURL = UserListPanel.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
	
}
