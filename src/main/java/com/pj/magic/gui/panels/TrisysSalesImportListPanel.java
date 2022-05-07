package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumnModel;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.FileAlreadyImportedException;
import com.pj.magic.exception.TrisysSalesImportException;
import com.pj.magic.gui.component.MagicFileChooser;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.TrisysSalesImport;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.SystemService;
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
    private static final int STATUS_COLUMN_INDEX = 3;
    private static final int FAILED_LINE_COLUMN_INDEX = 4;
    
	@Autowired private TrisysSalesService trisysSalesService;
	@Autowired private LoginService loginService;
	@Autowired private SystemService systemService;
	
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
		initializeTable();
		
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void initializeTable() {
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(FILE_COLUMN_INDEX).setPreferredWidth(200);
		columnModel.getColumn(IMPORT_DATE_COLUMN_INDEX).setPreferredWidth(200);
		columnModel.getColumn(IMPORT_BY_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(STATUS_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(FAILED_LINE_COLUMN_INDEX).setPreferredWidth(200);
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
	    
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}

	protected void selectSalesImport() {
		if (table.getSelectedRow() != -1) {
			TrisysSalesImport salesImport = tableModel.getItem(table.getSelectedRow());
			if (!salesImport.isError()) {
				getMagicFrame().switchToTrisysSalesImportPanel(salesImport);
			}
		}
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
//        toolBar.add(new MagicToolBarButton("up", "Import Trisys Sales", e -> importTrisysSales()));
        toolBar.add(new MagicToolBarButton("up_all", "Import All Trisys Sales", e -> importAllTrisysSales()));
	}

    private void importTrisysSales() {
        fileChooser.setFileFilter(new DbfFileFilter());
        if (!fileChooser.selectFileToOpen(this)) {
            return;
        }
        
        try {
            trisysSalesService.importTrisysSales(fileChooser.getSelectedFile());
            showMessage("Trisys sales file imported");
            updateDisplay();
        } catch (FileAlreadyImportedException e) {
        	showErrorMessage("File already imported");
        } catch (TrisysSalesImportException e) {
            log.error(e.getMessage(), e);
            showMessageForUnexpectedError();
            saveFailedImport(fileChooser.getSelectedFile().getName(), e);
            updateDisplay();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            showMessageForUnexpectedError(e);
        }
    }
	
    private void saveFailedImport(String filename, TrisysSalesImportException e) {
        TrisysSalesImport salesImport = new TrisysSalesImport();
        salesImport.setFile(FilenameUtils.getBaseName(filename));
        salesImport.setImportDate(systemService.getCurrentDateTime());
        salesImport.setImportBy(loginService.getLoggedInUser());
        salesImport.setStatus("ERROR");
        salesImport.setFailedLine(e.getLine());
        trisysSalesService.saveTrisysSalesImport(salesImport);
	}

    private void importAllTrisysSales() {
    	File location = new File("C:\\Trisys\\Magic");
    	for (final File fileEntry : location.listFiles()) {
    		if (fileEntry.isDirectory()) {
    			continue;
    		}
    		
            try {
        		log.info("Importing {}", fileEntry.getName());
                trisysSalesService.importTrisysSales(fileEntry);
        		log.info("Importing {} success", fileEntry.getName());
        		moveFileToProcessedFolder(location, fileEntry);
            } catch (FileAlreadyImportedException e) {
        		log.info("{} already imported", fileEntry.getName());
        		moveFileToProcessedFolder(location, fileEntry);
            	continue;
            } catch (TrisysSalesImportException e) {
                log.error(e.getMessage(), e);
                saveFailedImport(fileEntry.getName(), e);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
    	}
    	showMessage("Done importing all available Trisys Sales Files");
    	updateDisplay();
    }
    
	private void moveFileToProcessedFolder(File directory, File fileEntry) {
		File newFile = new File(directory.getAbsolutePath() + "\\processed\\" + fileEntry.getName());
		if (!newFile.exists()) {
			fileEntry.renameTo(newFile);
		} else {
			fileEntry.delete();
		}
	}

	private class TrisysSalesImportTableModel extends ListBackedTableModel<TrisysSalesImport> {

        private final String[] columnNames = {"File", "Import Date", "Import By", "Status", "Failed Line"};
	    
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
            case STATUS_COLUMN_INDEX:
            	return salesImport.getStatus();
            case FAILED_LINE_COLUMN_INDEX:
            	return salesImport.getFailedLine();
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
