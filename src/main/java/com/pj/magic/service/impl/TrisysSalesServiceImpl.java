package com.pj.magic.service.impl;

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

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.model.TrisysSalesImport;
import com.pj.magic.model.TrisysSalesItem;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.pj.magic.exception.FileAlreadyImportedException;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.model.Product;
import com.pj.magic.model.TrisysSales;
import com.pj.magic.repository.TrisysSalesImportRepository;
import com.pj.magic.repository.TrisysSalesItemRepository;
import com.pj.magic.repository.TrisysSalesRepository;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.SystemService;
import com.pj.magic.service.TrisysSalesService;

import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;

@Service
public class TrisysSalesServiceImpl implements TrisysSalesService {

	@Autowired private TrisysSalesImportRepository trisysSalesImportRepository;
	@Autowired private TrisysSalesRepository trisysSalesRepository;
	@Autowired private TrisysSalesItemRepository trisysSalesItemRepository;
	@Autowired private LoginService loginService;
	@Autowired private SystemService systemService;
	@Autowired private ProductService productService;
	
	@Override
	public List<TrisysSalesImport> getAllTrisysSalesImports() {
		return trisysSalesImportRepository.getAll();
	}

	@Override
	public TrisysSalesImport getTrisysSalesImport(Long id) {
		TrisysSalesImport salesImport = trisysSalesImportRepository.get(id);
		salesImport.setSales(trisysSalesRepository.findAllBySalesImport(salesImport));
		return salesImport;
	}

	@Transactional
	@Override
	public void saveTrisysSalesImport(TrisysSalesImport salesImport) {
		trisysSalesImportRepository.save(salesImport);
	}

	@Transactional
	@Override
	public void saveTrisysSales(TrisysSales sales) {
		trisysSalesRepository.save(sales);
	}

	@Override
	public TrisysSalesImport findByFile(String file) {
		return trisysSalesImportRepository.findByFile(file);
	}

	@Override
	public TrisysSales getTrisysSales(Long id) {
		TrisysSales sales = trisysSalesRepository.get(id);
		sales.setItems(trisysSalesItemRepository.findAllByTrisysSales(sales));
		return sales;
	}

	@Transactional
	@Override
	public void saveSalesItem(TrisysSalesItem item) {
		trisysSalesItemRepository.save(item);
	}

	@Transactional()
	@Override
	public void importTrisysSales(File file) throws Exception {
        String filename = FilenameUtils.getBaseName(file.getName());
        if (findByFile(filename) != null) {
        	throw new FileAlreadyImportedException();
        }
        
        String csvString = convertDbfToCsv(file);
		
        TrisysSalesImport salesImport = null;
        TrisysSales sales = null;
        try (
            CSVReader reader = new CSVReaderBuilder(new StringReader(csvString)).withSkipLines(1).build();
        ) {
            String[] nextLine = null;
            while ((nextLine = reader.readNext()) != null) {
        		Date salesDate = new SimpleDateFormat("yyyyMMdd").parse(nextLine[1]);
            	String terminal = nextLine[11];
            	if (salesImport == null) {
                    salesImport = new TrisysSalesImport();
                    salesImport.setFile(filename);
                    salesImport.setImportDate(systemService.getCurrentDateTime());
                    salesImport.setImportBy(loginService.getLoggedInUser());
                    saveTrisysSalesImport(salesImport);
            	}
            	
            	String saleNumber = nextLine[0];
            	
            	if (sales != null && sales.getSaleNumber().equals(saleNumber) && !sales.getTerminal().equals(terminal)) {
            		throw new RuntimeException("Multiple terminals in sale number");
            	}
        		
            	if (sales == null || !sales.getSaleNumber().equals(saleNumber)) {
            		sales = new TrisysSales();
            		sales.setSalesImport(salesImport);
            		sales.setSaleNumber(saleNumber);
            		sales.setTerminal(terminal);
                    sales.setSalesDate(salesDate);
            		saveTrisysSales(sales);
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
            	saveSalesItem(item);
            	
            	Product product = productService.findProductByCode(productCode);
            	if (product != null) {
            		try {
                    	productService.subtractAvailableQuantity(product, item.getQuantity(), !product.isWholesale());
            		} catch (NotEnoughStocksException e) {
        				Product wholesaleProduct = productService.findProductByCode(product.getCode() + "01");
        				if (wholesaleProduct != null && wholesaleProduct.getAvailableQuantity() > 0) {
        					Product fromDb = productService.findProductByCode(product.getCode());
        					int quantityToConvert = 1;
        					while (item.getQuantity() > (fromDb.getAvailableQuantity() + product.getUnitConversions().get(1).getQuantity() * quantityToConvert)
        							&& wholesaleProduct.getAvailableQuantity() > quantityToConvert) {
        						quantityToConvert++;
        					}
        					productService.subtractAvailableQuantity(wholesaleProduct, quantityToConvert, false);
        					productService.addAvailableQuantity(product, product.getUnitConversions().get(1).getQuantity() * quantityToConvert);
        					productService.subtractAvailableQuantity(product, item.getQuantity(), false);
        				} else {
                        	productService.subtractAvailableQuantity(product, item.getQuantity(), false);
        				}
            		}
            	}
            }
        }
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
	
}
