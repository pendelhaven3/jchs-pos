package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.FileAlreadyImportedException;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.gui.component.MagicFileChooser;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.TrisysSalesImport;
import com.pj.magic.service.TrisysSalesService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.DbfFileFilter;
import com.pj.magic.util.FormatterUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TrisysSalesImportListPanel extends StandardMagicPanel {

    private static final int FILE_COLUMN_INDEX = 0;
    private static final int IMPORT_DATE_COLUMN_INDEX = 1;
    private static final int IMPORT_BY_COLUMN_INDEX = 2;
    
	@Autowired private TrisysSalesService trisysSalesService;
	
	private MagicListTable table;
	private TrisysSalesImportTableModel tableModel = new TrisysSalesImportTableModel();
	private MagicFileChooser fileChooser = new MagicFileChooser();
	
	@Override
	public String getTitle() {
		return "Trisys Sales Import List";
	}
	
	public void updateDisplay() {
		List<TrisysSalesImport> salesImports = trisysSalesService.getAllTrisysSalesImports();
		tableModel.setItems(salesImports);
		if (!salesImports.isEmpty()) {
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
		
		currentRow++; // first row
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(new JScrollPane(table), c);
	}

    @Override
	protected void registerKeyBindings() {
	    table.setKeyEvent(KeyEvent.VK_ENTER, new AbstractAction() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                selectSalesImport();
            }
        });
	    
	    table.addDoubleClickMouseListener(() -> selectSalesImport());
	}

	protected void selectSalesImport() {
		if (table.getSelectedRow() != -1) {
			TrisysSalesImport salesImport = tableModel.getItem(table.getSelectedRow());
			getMagicFrame().switchToTrisysSalesImportPanel(salesImport);
		}
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
        JButton addButton = new MagicToolBarButton("up", "Import Trisys Sales");
        addButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                importTrisysSales();
            }

        });
        toolBar.add(addButton);
	}

    private void importTrisysSales() {
        fileChooser.setFileFilter(new DbfFileFilter());
        if (!fileChooser.selectFileToOpen(this)) {
            return;
        }
        
        /*
        try (
            CSVReader reader = new CSVReaderBuilder(new StringReader(csvString)).withSkipLines(1).build();
        ) {
            String[] nextLine = null;
            while ((nextLine = reader.readNext()) != null) {
            	String productCode = nextLine[2];
            	if (productService.findProductByCode(productCode) == null) {
            		showErrorMessage("Product code not defined: " + productCode);
            		return;
            	}
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            showMessageForUnexpectedError();
            return;
        }
        */
        
        try {
            trisysSalesService.importTrisysSales(fileChooser.getSelectedFile());
            showMessage("Trisys sales file imported");
            updateDisplay();
        } catch (FileAlreadyImportedException e) {
        	showErrorMessage("File already imported");
        } catch (NotEnoughStocksException e) {
        	showErrorMessage("Not enough stocks for product " + e.getProductCode());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            showMessageForUnexpectedError();
        }
    }
	
    private class TrisysSalesImportTableModel extends ListBackedTableModel<TrisysSalesImport> {

        private final String[] columnNames = {"File", "Import Date", "Import By"};
	    
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
        	TrisysSalesImport salesImport = getItem(rowIndex);
            switch (columnIndex) {
            case FILE_COLUMN_INDEX:
                return salesImport.getFile();
            case IMPORT_DATE_COLUMN_INDEX:
                return FormatterUtil.formatDateTime(salesImport.getImportDate());
            case IMPORT_BY_COLUMN_INDEX:
            	return salesImport.getImportBy().getUsername();
            default:
                throw new RuntimeException("Fetching invalid column index: " + columnIndex);
            }
        }

        @Override
        protected String[] getColumnNames() {
            return columnNames;
        }
	    
	}
	
}
