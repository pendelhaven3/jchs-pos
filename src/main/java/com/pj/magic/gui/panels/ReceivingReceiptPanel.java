package com.pj.magic.gui.panels;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.AlreadyCancelledException;
import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.dialog.SetDiscountsForAllItemsDialog;
import com.pj.magic.gui.dialog.StatusDetailsDialog;
import com.pj.magic.gui.tables.ReceivingReceiptItemsTable;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.service.ExcelService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.ReceivingReceiptService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FileUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.HtmlUtil;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class ReceivingReceiptPanel extends StandardMagicPanel {

    private static final long serialVersionUID = 6012875814973521782L;

    private static final Logger logger = LoggerFactory.getLogger(ReceivingReceiptPanel.class);
	
	@Autowired private ReceivingReceiptItemsTable itemsTable;
	@Autowired private ReceivingReceiptService receivingReceiptService;
	@Autowired private PrintService printService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private StatusDetailsDialog statusDialog;
	@Autowired private SetDiscountsForAllItemsDialog setDiscountsForAllItemsDialog;
    @Autowired private ExcelService excelService;
	
	private ReceivingReceipt receivingReceipt;
	private JLabel receivingReceiptNumberField;
	private JLabel relatedPurchaseOrderNumberField;
	private JLabel supplierField;
	private JLabel statusField;
	private JLabel paymentTermField;
	private UtilCalendarModel receivedDateModel;
	private JLabel referenceNumberField;
	private JCheckBox vatInclusiveCheckBox;
	private JLabel subTotalAmountField;
	private JLabel totalDiscountedAmountField;
	private JLabel totalNetAmountField;
	private JLabel vatAmountField;
	private JLabel totalAmountField;
	private MagicToolBarButton postButton;
	private MagicToolBarButton cancelButton;
	private MagicToolBarButton setDiscountsForAllButton;
	private JDatePickerImpl datePicker;
    private JFileChooser excelFileChooser;
	
	@Override
	protected void initializeComponents() {
		supplierField = new JLabel();
		statusField = new JLabel();
		paymentTermField = new JLabel();
		referenceNumberField = new JLabel();
		
		vatInclusiveCheckBox = new JCheckBox();
		vatInclusiveCheckBox.setEnabled(false);
		
		receivedDateModel = new UtilCalendarModel();
		receivedDateModel.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("value".equals(evt.getPropertyName()) && evt.getOldValue() != null 
						&& evt.getNewValue() != null) {
					receivingReceipt.setReceivedDate(receivedDateModel.getValue().getTime());
					receivingReceiptService.save(receivingReceipt);
				}
			}
		});
		
        excelFileChooser = new JFileChooser();
        excelFileChooser.setCurrentDirectory(new File(FileUtil.getDesktopFolderPath()));
        excelFileChooser.setFileFilter(new FileFilter() {
            
            @Override
            public String getDescription() {
                return "Excel workbook (*.xlsx)";
            }
            
            @Override
            public boolean accept(File f) {
                return FilenameUtils.getExtension(f.getName()).equals("xlsx");
            }
        });
		
		focusOnItemsTableWhenThisPanelIsDisplayed();
		updateTotalAmountFieldWhenItemsTableChanges();
	}

	private void focusOnItemsTableWhenThisPanelIsDisplayed() {
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				itemsTable.highlight();
			}
		});
	}

	@Override
	protected void registerKeyBindings() {
		statusField.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				statusDialog.updateDisplay(receivingReceipt);
				statusDialog.setVisible(true);
			}
			
		});
		statusField.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToReceivingReceiptListPanel();
	}
	
	private void updateTotalAmountFieldWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				subTotalAmountField.setText(FormatterUtil.formatAmount(receivingReceipt.getSubTotalAmount()));
				totalDiscountedAmountField.setText(
						FormatterUtil.formatAmount(receivingReceipt.getTotalDiscountedAmount()));
				totalNetAmountField.setText(FormatterUtil.formatAmount(receivingReceipt.getTotalNetAmount()));
				vatAmountField.setText(FormatterUtil.formatAmount(receivingReceipt.getVatAmount()));
				totalAmountField.setText(FormatterUtil.formatAmount(receivingReceipt.getTotalNetAmountWithVat()));
			}
		});
	}

	public void updateDisplay(ReceivingReceipt receivingReceipt) {
		this.receivingReceipt = receivingReceiptService.getReceivingReceipt(receivingReceipt.getId());
		receivingReceipt = this.receivingReceipt;
		
		receivingReceiptNumberField.setText(receivingReceipt.getReceivingReceiptNumber().toString());
		relatedPurchaseOrderNumberField.setText(receivingReceipt.getRelatedPurchaseOrderNumber().toString());
		supplierField.setText(receivingReceipt.getSupplier().getName());
		statusField.setText(HtmlUtil.blueUnderline(receivingReceipt.getStatus()));
		paymentTermField.setText(receivingReceipt.getPaymentTerm().getName());
		updateReceivedDateField();
		referenceNumberField.setText(receivingReceipt.getReferenceNumber());
		vatInclusiveCheckBox.setSelected(receivingReceipt.isVatInclusive());
		subTotalAmountField.setText(FormatterUtil.formatAmount(receivingReceipt.getSubTotalAmount()));
		totalDiscountedAmountField.setText(
				FormatterUtil.formatAmount(receivingReceipt.getTotalDiscountedAmount()));
		totalNetAmountField.setText(FormatterUtil.formatAmount(receivingReceipt.getTotalNetAmount()));
		itemsTable.setReceivingReceipt(receivingReceipt);
		
		postButton.setEnabled(receivingReceipt.isNew());
		cancelButton.setEnabled(receivingReceipt.isNew());
		setDiscountsForAllButton.setEnabled(receivingReceipt.isNew());
		datePicker.getComponents()[1].setVisible(receivingReceipt.isNew());
	}

	private void updateReceivedDateField() {
		receivedDateModel.setValue(null); // set to null first to prevent property change listener from triggering
		receivedDateModel.setValue(DateUtils.toCalendar(receivingReceipt.getReceivedDate()));
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 30), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "RR No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		receivingReceiptNumberField = ComponentUtil.createLabel(200, "");
		mainPanel.add(receivingReceiptNumberField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 1), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Related PO No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		relatedPurchaseOrderNumberField = ComponentUtil.createLabel(100, "");
		mainPanel.add(relatedPurchaseOrderNumberField, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 6;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Supplier:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(supplierField, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Status:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusField.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(statusField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Payment Term:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(paymentTermField, c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Received Date:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;

		JDatePanelImpl datePanel = new JDatePanelImpl(receivedDateModel);
		datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Reference No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		referenceNumberField = ComponentUtil.createLabel(150, "");
		mainPanel.add(referenceNumberField, c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "VAT Inclusive:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(vatInclusiveCheckBox, c);

		currentRow++;

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weightx = c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 7;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 7;
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
		panel.add(ComponentUtil.createLabel(120, "Sub Total:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		subTotalAmountField = ComponentUtil.createRightLabel(120, "");
		panel.add(subTotalAmountField, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createFiller(50, 1), c);
			
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Disc. Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalDiscountedAmountField = ComponentUtil.createRightLabel(120, "");
		panel.add(totalDiscountedAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Net Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalNetAmountField = ComponentUtil.createRightLabel(120, "");
		panel.add(totalNetAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "VAT Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		vatAmountField = ComponentUtil.createRightLabel(120, "");
		panel.add(vatAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = ComponentUtil.createRightLabel(120, "");
		panel.add(totalAmountField, c);
		
		return panel;
	}

	private void postReceivingReceipt() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		
		if (confirm("Do you want to post this Receiving Receipt?")) {
			try {
				receivingReceiptService.post(receivingReceipt);
				showMessage("Post successful!");
				updateDisplay(receivingReceipt);
			} catch (AlreadyPostedException e) {
				showErrorMessage("Receiving Receipt already posted");
				updateDisplay(receivingReceipt);
			} catch (AlreadyCancelledException e) {
				showErrorMessage("Receiving Receipt already cancelled");
				updateDisplay(receivingReceipt);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
				updateDisplay(receivingReceipt);
			}
		}
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		setDiscountsForAllButton = new MagicToolBarButton("discount_all", "Set Discounts For All Items");
		setDiscountsForAllButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSetDiscountsForAllItemsDialog();
			}
		});
		toolBar.add(setDiscountsForAllButton);
		
		cancelButton = new MagicToolBarButton("cancel", "Cancel");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelReceivingReceipt();
			}
		});
		toolBar.add(cancelButton);
		
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postReceivingReceipt();
			}
		});
//		toolBar.add(postButton);
		
		JButton printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreview();
			}
		});
//		toolBar.add(printPreviewButton);
		
		MagicToolBarButton printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printReceivingReceipt();
			}
		});
//		toolBar.add(printButton);
		
        MagicToolBarButton toExcelButton = new MagicToolBarButton("excel", "Generate Excel spreadsheet");
        toExcelButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                generateExcelSpreadsheet();
            }
        }); 
        toolBar.add(toExcelButton);
	}

    protected void openSetDiscountsForAllItemsDialog() {
		setDiscountsForAllItemsDialog.updateDisplay(receivingReceipt);
		setDiscountsForAllItemsDialog.setVisible(true);
		
		updateDisplay(receivingReceipt);
	}

	private void cancelReceivingReceipt() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		
		if (confirm("Do you want to cancel this Receiving Receipt?")) {
			try {
				receivingReceiptService.cancel(receivingReceipt);
				showMessage("Receiving Receipt cancelled");
				updateDisplay(receivingReceipt);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
			}
		}
	}

	protected void printPreview() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		
		int confirm = JOptionPane.showConfirmDialog(this, "Include discount details?", "Print Receiving Receipt", JOptionPane.YES_NO_OPTION);
		printPreviewDialog.updateDisplay(
				printService.generateReportAsString(receivingReceipt, confirm == JOptionPane.YES_OPTION));
		printPreviewDialog.setVisible(true);
	}

	protected void printReceivingReceipt() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		
		int confirm = JOptionPane.showConfirmDialog(this, "Include discount details?", "Print Receiving Receipt", JOptionPane.YES_NO_CANCEL_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			printService.print(receivingReceipt, true);
		} else if (confirm == JOptionPane.NO_OPTION) {
			printService.print(receivingReceipt, false);
		}
	}

    private void generateExcelSpreadsheet() {
        excelFileChooser.setSelectedFile(new File(generateDefaultSpreadsheetName() + ".xlsx"));
        
        int returnVal = excelFileChooser.showSaveDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        try (
            Workbook workbook = excelService.generateSpreadsheet(receivingReceipt);
            FileOutputStream out = new FileOutputStream(excelFileChooser.getSelectedFile());
        ) {
            workbook.write(out);
            showMessage("Excel spreadsheet generated successfully");
        } catch (IOException e) {
            showErrorMessage("Unexpected error during excel generation");
        }
    }
	
    private String generateDefaultSpreadsheetName() {
        return new StringBuilder()
            .append(receivingReceipt.getSupplier().getName())
            .append(" - ")
            .append(new SimpleDateFormat("MMM-dd-yyyy").format(new Date()))
            .append(" - RR ")
            .append(receivingReceipt.getReceivingReceiptNumber())
            .toString();
    }
    
}
