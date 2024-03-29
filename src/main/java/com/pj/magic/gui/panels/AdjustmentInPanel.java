package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicCheckBox;
import com.pj.magic.gui.component.MagicFileChooser;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.AdjustmentInItemsTable;
import com.pj.magic.gui.tables.ProductInfoTable;
import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.Product2;
import com.pj.magic.report.pdf.AdjustmentInPdfGenerator;
import com.pj.magic.service.AdjustmentInService;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.Product2Service;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FileUtil;
import com.pj.magic.util.FormatterUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AdjustmentInPanel extends StandardMagicPanel {

	@Autowired private AdjustmentInItemsTable itemsTable;
	@Autowired private AdjustmentInService adjustmentInService;
	@Autowired private LoginService loginService;
	@Autowired private Product2Service product2Service;
	
	private AdjustmentIn adjustmentIn;
	private JLabel adjustmentInNumberLabel;
	private JLabel statusLabel;
	private MagicTextField remarksField;
	private JLabel postDateField;
	private JLabel postedByField;
	private MagicCheckBox pilferageCheckBox;
	private JLabel totalItemsField;
	private JLabel totalAmountField;
	private JButton postButton;
	private JButton addItemButton;
	private JButton deleteItemButton;
	private ProductInfoTable productInfoTable;
    private MagicFileChooser pdfFileChooser;
	
	@Override
	protected void initializeComponents() {
		postDateField = new JLabel();
		
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(100);
		remarksField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveRemarks();
			}
		});
		
		pilferageCheckBox = new MagicCheckBox();
		pilferageCheckBox.addOnClickListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				savePilferageFlag();
			}
		});
		
        pdfFileChooser = new MagicFileChooser();
        pdfFileChooser.setCurrentDirectory(new File(FileUtil.getDesktopFolderPath()));
        pdfFileChooser.setFileFilter(new FileFilter() {
            
            @Override
            public String getDescription() {
                return "PDF (*.pdf)";
            }
            
            @Override
            public boolean accept(File f) {
                return FilenameUtils.getExtension(f.getName()).equals("pdf");
            }
        });
		
		focusOnComponentWhenThisPanelIsDisplayed(remarksField);
		updateTotalAmountFieldWhenItemsTableChanges();
		initializeUnitPricesAndQuantitiesTable();
	}

	@Override
	protected void registerKeyBindings() {
		remarksField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.highlight();
			}
		});
	}

	protected void saveRemarks() {
		if (adjustmentIn.getId() != null || !remarksField.getText().equals(adjustmentIn.getRemarks())) {
			adjustmentIn.setRemarks(remarksField.getText());
			try {
				adjustmentInService.save(adjustmentIn);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				showMessageForUnexpectedError(e);
				return;
			}
			
			updateDisplay(adjustmentIn);
			itemsTable.highlight();
		}
	}

	private void savePilferageFlag() {
		if (adjustmentIn.getPilferageFlag() == pilferageCheckBox.isSelected()) {
			return;
		}
		
		adjustmentIn.setPilferageFlag(pilferageCheckBox.isSelected());
		try {
			adjustmentInService.save(adjustmentIn);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			showMessageForUnexpectedError(e);
		}
	}
	
	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToAdjustmentInListPanel();
	}
	
	private void updateTotalAmountFieldWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalItemsField.setText(String.valueOf(adjustmentIn.getTotalItems()));
				totalAmountField.setText(FormatterUtil.formatAmount(adjustmentIn.getTotalAmount()));
			}
		});
	}

	public void updateDisplay(AdjustmentIn adjustmentIn) {
		if (adjustmentIn.getId() == null) {
			this.adjustmentIn = adjustmentIn;
			clearDisplay();
			return;
		}
		
		this.adjustmentIn = adjustmentIn = adjustmentInService.getAdjustmentIn(adjustmentIn.getId());
		
		adjustmentInNumberLabel.setText(adjustmentIn.getAdjustmentInNumber().toString());
		statusLabel.setText(adjustmentIn.getStatus());
		if (adjustmentIn.getPostDate() != null) {
			postDateField.setText(FormatterUtil.formatDateTime(adjustmentIn.getPostDate()));
		} else {
			postDateField.setText(null);
		}
		if (adjustmentIn.getPostedBy() != null) {
			postedByField.setText(adjustmentIn.getPostedBy().getUsername());
		} else {
			postedByField.setText(null);
		}
		remarksField.setEnabled(!adjustmentIn.isPosted());
		remarksField.setText(adjustmentIn.getRemarks());
		pilferageCheckBox.setEnabled(loginService.getLoggedInUser().isSupervisor() && !adjustmentIn.isPosted());
		pilferageCheckBox.setSelected(adjustmentIn.getPilferageFlag(), false);
		totalItemsField.setText(String.valueOf(adjustmentIn.getTotalItems()));
		totalAmountField.setText(adjustmentIn.getTotalAmount().toString());
		postButton.setEnabled(!adjustmentIn.isPosted());
		addItemButton.setEnabled(!adjustmentIn.isPosted());
		deleteItemButton.setEnabled(!adjustmentIn.isPosted());
		
		itemsTable.setAdjustmentIn(adjustmentIn);
	}

	private void clearDisplay() {
		adjustmentInNumberLabel.setText(null);
		statusLabel.setText(null);
		postDateField.setText(null);
		postedByField.setText(null);
		remarksField.setEnabled(true);
		remarksField.setText(null);
		pilferageCheckBox.setEnabled(loginService.getLoggedInUser().isSupervisor());
		pilferageCheckBox.setSelected(true, false);
		totalItemsField.setText(null);
		totalAmountField.setText(null);
		itemsTable.setAdjustmentIn(adjustmentIn);
		postButton.setEnabled(false);
		addItemButton.setEnabled(false);
		deleteItemButton.setEnabled(false);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50));

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Adj. In No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		adjustmentInNumberLabel = ComponentUtil.createLabel(200, "");
		mainPanel.add(adjustmentInNumberLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Status:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusLabel = ComponentUtil.createLabel(100, "");
		mainPanel.add(statusLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Remarks:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(remarksField, c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Post Date:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(postDateField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Pilferage:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(pilferageCheckBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Posted By:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postedByField = ComponentUtil.createLabel(100, "");
		mainPanel.add(postedByField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.insets.top = 10;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createItemsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		mainPanel.add(ComponentUtil.createScrollPane(itemsTable, 600, 100), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.CENTER;
		mainPanel.add(ComponentUtil.createScrollPane(productInfoTable, 500, 65), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}
	
	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.insets.right = 10;
		c.anchor = GridBagConstraints.WEST;
		totalItemsField = ComponentUtil.createLabel(60);
		panel.add(totalItemsField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.insets.right = 10;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = ComponentUtil.createLabel(100);
		panel.add(totalAmountField, c);
		
		return panel;
	}
	
	private JPanel createItemsTableToolBar() {
		JPanel panel = new JPanel();
		
		addItemButton = new MagicToolBarButton("plus_small", "Add Item", true);
		addItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.switchToAddMode();
			}
		});
		panel.add(addItemButton, BorderLayout.WEST);
		
		deleteItemButton = new MagicToolBarButton("minus_small", "Delete Item", true);
		deleteItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
	}

	private void initializeUnitPricesAndQuantitiesTable() {
		productInfoTable = new ProductInfoTable();
		
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getColumn() == AdjustmentInItemsTable.PRODUCT_CODE_COLUMN_INDEX ||
						e.getColumn() == TableModelEvent.ALL_COLUMNS) {
					updateUnitPricesAndQuantitiesTable();
				}
			}

		});
		
		itemsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateUnitPricesAndQuantitiesTable();
			}
		});
	}
	
	private void updateUnitPricesAndQuantitiesTable() {
		if (itemsTable.getSelectedRow() == -1) {
			productInfoTable.setProduct(null);
			return;
		}
		
		Product2 product = itemsTable.getCurrentlySelectedRowItem().getProduct();
		if (product != null) {
			productInfoTable.setProduct(product2Service.getProduct(product.getId()));
		} else {
			productInfoTable.setProduct(null);
		}
	}
	
	private void postAdjustmentIn() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}
		
		int confirm = showConfirmMessage("Do you want to post this Adjustment In?");
		if (confirm == JOptionPane.OK_OPTION) {
			if (!adjustmentIn.hasItems()) {
				showErrorMessage("Cannot post a Adjustment In with no items");
				itemsTable.requestFocusInWindow();
				return;
			}
			try {
				adjustmentInService.post(adjustmentIn);
				JOptionPane.showMessageDialog(this, "Post successful!");
				updateDisplay(adjustmentIn);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				showMessageForUnexpectedError(e);
			}
		}
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postAdjustmentIn();
			}
		});
		toolBar.add(postButton);
		
        toolBar.add(new MagicToolBarButton("pdf", "Generate PDF report", e -> generatePdfReport()));
	}

	private void generatePdfReport() {
		String filename = new StringBuilder()
	            .append("ADJUSTMENT IN - ")
	            .append(String.valueOf(adjustmentIn.getAdjustmentInNumber()))
	            .append(".pdf")
	            .toString();

		
    	pdfFileChooser.setSelectedFile(new File(filename));
        if (!pdfFileChooser.selectSaveFile(this)) {
        	return;
        }
        File file = pdfFileChooser.getSelectedFile();
        
    	try {
    		new AdjustmentInPdfGenerator(file).generate(adjustmentIn);
	        Desktop.getDesktop().open(file);
    	} catch (Exception e) {
    		log.error(e.getMessage(), e);
        	showMessageForUnexpectedError(e);
    	}
	}

}