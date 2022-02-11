package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.pj.magic.gui.component.MagicFileChooser;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.TrisysSales;
import com.pj.magic.model.TrisysSalesImport;
import com.pj.magic.model.TrisysSalesItem;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.SystemService;
import com.pj.magic.service.TrisysSalesService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.DbfFileFilter;
import com.pj.magic.util.FormatterUtil;

import lombok.extern.slf4j.Slf4j;
import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;

@Component
@Slf4j
public class TrisysSalesImportListPanel extends StandardMagicPanel {

    private static final int FILE_COLUMN_INDEX = 0;
    private static final int IMPORT_DATE_COLUMN_INDEX = 1;
    private static final int IMPORT_BY_COLUMN_INDEX = 2;
    
	@Autowired private TrisysSalesService trisysSalesService;
	@Autowired private SystemService systemService;
	@Autowired private LoginService loginService;
	
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
        
        String filename = FilenameUtils.getBaseName(fileChooser.getSelectedFile().getName());
        if (trisysSalesService.findByFile(filename) != null) {
        	showErrorMessage("File already imported");
        	return;
        }
        
        String csvString = null;
        try {
            csvString = convertDbfToCsv(fileChooser.getSelectedFile());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            showMessageForUnexpectedError();
            return;
        }
        
        TrisysSalesImport salesImport = null;
        TrisysSales sales = null;
        try (
            CSVReader reader = new CSVReaderBuilder(new StringReader(csvString)).withSkipLines(1).build();
        ) {
            String[] nextLine = null;
            while ((nextLine = reader.readNext()) != null) {
            	for (String part : nextLine) {
            		System.out.print(part);
            		System.out.print(" - ");
            	}
            	System.out.println();
            	
        		Date salesDate = new SimpleDateFormat("yyyyMMdd").parse(nextLine[1]);
            	String terminal = nextLine[11];
            	if (salesImport == null) {
                    salesImport = new TrisysSalesImport();
                    salesImport.setFile(filename);
                    salesImport.setImportDate(systemService.getCurrentDateTime());
                    salesImport.setImportBy(loginService.getLoggedInUser());
                    trisysSalesService.saveTrisysSalesImport(salesImport);
            	}
            	
            	String saleNumber = nextLine[0];
            	
            	if (sales != null && sales.getSaleNumber().equals(saleNumber) && !sales.getTerminal().equals(terminal)) {
            		showErrorMessage("Multiple terminals in sale number");
            		return;
            	}
        		
            	if (sales == null || !sales.getSaleNumber().equals(saleNumber)) {
            		sales = new TrisysSales();
            		sales.setSalesImport(salesImport);
            		sales.setSaleNumber(saleNumber);
            		sales.setTerminal(terminal);
                    sales.setSalesDate(salesDate);
            		trisysSalesService.saveTrisysSales(sales);
            	}
            	
            	String productCode = nextLine[2];
            	BigDecimal unitCost = new BigDecimal(nextLine[7]);
            	BigDecimal sellPrice = new BigDecimal(nextLine[8]);
            	BigDecimal total = new BigDecimal(nextLine[10]);
            	
            	TrisysSalesItem item = new TrisysSalesItem();
            	item.setSales(sales);
            	item.setProductCode(productCode);
            	item.setQuantity(total.divide(sellPrice, 2, RoundingMode.HALF_EVEN).intValue());
            	item.setUnitCost(unitCost);
            	item.setSellPrice(sellPrice);
            	trisysSalesService.saveSalesItem(item);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            showMessageForUnexpectedError();
            return;
        }
        
        updateDisplay();
    }
	
	private static String convertDbfToCsv(File file) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    
        DbfRecord rec;
        try (
            DbfReader reader = new DbfReader(file);
        ) {
            DbfMetadata meta = reader.getMetadata();
            List<String> fields = meta.getFields().stream().map(f -> f.getName()).collect(Collectors.toList());
            sb.append(fields.stream().collect(Collectors.joining(",")));
            sb.append("\n");

            while ((rec = reader.read()) != null) {
                rec.setStringCharset(StandardCharsets.UTF_8);

                StringJoiner sj = new StringJoiner(",");
                for (String s : fields) {
                    String value = rec.getString(s);
                    
                    if (value != null && value.contains("\"")) {
                        value = value.replaceAll("\"", "\"\"");
                        value = "\"" + value + "\"";
                    }
                    if (value != null && value.contains(",")) {
                        value = "\"" + value + "\"";
                    }
                    sj.add(value);
                }

                sb.append(sj.toString());
                sb.append("\n");
            }
            return sb.toString();
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
