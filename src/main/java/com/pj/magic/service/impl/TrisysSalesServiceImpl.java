package com.pj.magic.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.model.TrisysSalesImport;
import com.pj.magic.model.TrisysSalesItem;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.pj.magic.exception.FileAlreadyImportedException;
import com.pj.magic.exception.TrisysSalesImportException;
import com.pj.magic.model.Product;
import com.pj.magic.model.Product2;
import com.pj.magic.model.TrisysSales;
import com.pj.magic.repository.TrisysSalesImportRepository;
import com.pj.magic.repository.TrisysSalesItemRepository;
import com.pj.magic.repository.TrisysSalesRepository;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.Product2Service;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.SystemService;
import com.pj.magic.service.TrisysSalesService;

import lombok.extern.slf4j.Slf4j;
import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;

@Service
@Slf4j
public class TrisysSalesServiceImpl implements TrisysSalesService {

	@Autowired private TrisysSalesImportRepository trisysSalesImportRepository;
	@Autowired private TrisysSalesRepository trisysSalesRepository;
	@Autowired private TrisysSalesItemRepository trisysSalesItemRepository;
	@Autowired private LoginService loginService;
	@Autowired private SystemService systemService;
	@Autowired private ProductService productService;
	@Autowired private Product2Service product2Service;
	@Autowired private TrisysSalesService trisysSalesService;
	
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

	@Transactional
	@Override
	public void importTrisysSales(File file) throws Exception {
        String filename = FilenameUtils.getBaseName(file.getName());
        
        TrisysSalesImport existingSalesImport = findByFile(filename);
        if (existingSalesImport != null) {
        	String status = existingSalesImport.getStatus();
        	if ("SUCCESS".equals(status)) {
               	throw new FileAlreadyImportedException();
           	} else {
           		trisysSalesService.deleteTrisysSalesImport(existingSalesImport.getId());
           	}
        }
        
        String csvString = convertDbfToCsv(file);
		
        TrisysSalesImport salesImport = null;
        TrisysSales sales = null;
        String[] nextLine = null;
        try (
            CSVReader reader = new CSVReaderBuilder(new StringReader(csvString)).withSkipLines(1).build();
        ) {
            while ((nextLine = reader.readNext()) != null) {
        		Date salesDate = new SimpleDateFormat("yyyyMMdd").parse(nextLine[1]);
            	String terminal = nextLine[11];
            	if (salesImport == null) {
                    salesImport = new TrisysSalesImport();
                    salesImport.setFile(filename);
                    salesImport.setImportDate(systemService.getCurrentDateTime());
                    salesImport.setImportBy(loginService.getLoggedInUser());
                    salesImport.setStatus("SUCCESS");
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
            	String unit = nextLine[6];
            	BigDecimal unitCost = new BigDecimal(nextLine[7]);
            	BigDecimal sellPrice = new BigDecimal(nextLine[8]);
            	BigDecimal total = new BigDecimal(nextLine[10]);
            	
            	List<String> allowedUnits = Arrays.asList("CASE", "TIES", "PACK", "HDZN", "PCS");
            	if (!allowedUnits.contains(unit)) {
            		continue;
            	}
            	
            	Product product = productService.findProductByCode(productCode);
            	if (product != null) {
                	if (product.getUnits().size() > 1 && !allowedUnits.contains(product.getUnits().get(1))) { // debug line
                		continue;
                	}
            		
                	TrisysSalesItem item = new TrisysSalesItem();
                	item.setSales(sales);
                	item.setProductCode(productCode);
                	item.setQuantity(total.divide(sellPrice, 2, RoundingMode.HALF_EVEN).intValue());
                	item.setUnit(unit);
                	item.setUnitCost(unitCost);
                	item.setSellPrice(sellPrice);
                	saveSalesItem(item);
            		
                	product2Service.subtractAvailableQuantity(product.getProduct2Id(), item.getUnit(), item.getQuantity());
                	
                	// auto conversion
    				Product2 product2 = product2Service.getProduct(product.getProduct2Id());
    				int availableQuantity = product2.getUnitQuantity(item.getUnit());
    				if (availableQuantity < 0 && !product.isWholesale()) {
    	            	Product wholesaleProduct = productService.findProductByCode(productCode + "01");
    	            	if (wholesaleProduct != null) {
    	            		String wholesaleUnit = wholesaleProduct.getUnits().get(0);
    	            		int availableWholesaleQuantity = product2.getUnitQuantity(wholesaleUnit);
    	            		if (availableWholesaleQuantity > 0) {
    	            			int unitConversion = product2.getUnitConversion(wholesaleUnit);
            					int quantityToConvert = 1;
            					while (quantityToConvert >= availableWholesaleQuantity &&
            							availableQuantity + (unitConversion * quantityToConvert) < 0) {
            						quantityToConvert++;
            					}
            					product2Service.subtractAvailableQuantity(product.getProduct2Id(), wholesaleUnit, quantityToConvert);
            					product2Service.addAvailableQuantity(product.getProduct2Id(), item.getUnit(), quantityToConvert * unitConversion);
    	            		}
    	            	}
    				}
            	} else {
            		log.warn("Product code not found: " + productCode);
            	}
            }
        } catch (Exception e) {
        	if (nextLine != null) {
        		throw new TrisysSalesImportException(StringUtils.join(Arrays.asList(nextLine[2], nextLine[6]), ", "), e);
        	} else {
        		throw e;
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

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public void deleteTrisysSalesImport(Long id) {
		trisysSalesImportRepository.delete(id);
	}
	
}
