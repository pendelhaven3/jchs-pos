package com.pj.magic.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.Constants;
import com.pj.magic.util.ListUtil;

public class SalesRequisition implements Serializable {

    private static final long serialVersionUID = -4706581594898787110L;
    
    private Long id;
	private Long salesRequisitionNumber;
	private Customer customer;
	private Date createDate;
	private Date transactionDate;
	private User encoder;
	private boolean posted;
	private List<SalesRequisitionItem> items = new ArrayList<>();
	private PricingScheme pricingScheme;
	private String mode;
	private String remarks;
	private PaymentTerm paymentTerm;
	private StockQuantityConversion stockQuantityConversion;

	public SalesRequisition() {
	}
	
	public SalesRequisition(long id) {
		this.id = id;
	}
	
	public BigDecimal getTotalAmount() {
		BigDecimal total = BigDecimal.ZERO;
		for (SalesRequisitionItem item : items) {
			total = total.add(item.getAmount());
		}
		return total.setScale(2, RoundingMode.HALF_UP);
	}
	
	public boolean hasItems() {
		return !items.isEmpty();
	}
	
	public int getTotalNumberOfItems() {
		return items.size();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof SalesRequisition)) {
            return false;
        }
        SalesRequisition other = (SalesRequisition)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}

	public Long getSalesRequisitionNumber() {
		return salesRequisitionNumber;
	}

	public void setSalesRequisitionNumber(Long salesRequisitionNumber) {
		this.salesRequisitionNumber = salesRequisitionNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<SalesRequisitionItem> getItems() {
		return items;
	}

	public void setItems(List<SalesRequisitionItem> items) {
		this.items = items;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SalesInvoice createSalesInvoice() {
		SalesInvoice salesInvoice = new SalesInvoice();
		salesInvoice.setCustomer(customer);
		salesInvoice.setPricingScheme(pricingScheme);
		salesInvoice.setMode(mode);
		salesInvoice.setRemarks(remarks);
		salesInvoice.setCreateDate(createDate);
		salesInvoice.setTransactionDate(transactionDate);
		salesInvoice.setEncoder(encoder);
		salesInvoice.setRelatedSalesRequisitionNumber(salesRequisitionNumber);
		salesInvoice.setPaymentTerm(paymentTerm);
		
		for (SalesRequisitionItem item : items) {
			SalesInvoiceItem invoiceItem = new SalesInvoiceItem();
			invoiceItem.setParent(salesInvoice);
			invoiceItem.setProduct(item.getProduct());
			invoiceItem.setUnit(item.getUnit());
			invoiceItem.setQuantity(item.getQuantity());
			invoiceItem.setUnitPrice(item.getUnitPrice());
			invoiceItem.setCost(item.getProduct().getFinalCost(item.getUnit()));
			salesInvoice.getItems().add(invoiceItem);
		}
		
		return salesInvoice;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public User getEncoder() {
		return encoder;
	}

	public void setEncoder(User encoder) {
		this.encoder = encoder;
	}

	public PricingScheme getPricingScheme() {
		return pricingScheme;
	}

	public void setPricingScheme(PricingScheme pricingScheme) {
		this.pricingScheme = pricingScheme;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public PaymentTerm getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(PaymentTerm paymentTerm) {
		this.paymentTerm = paymentTerm;
	}
	
	public Date getTransactionDate() {
		return transactionDate;
	}
	
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	
	public List<Manufacturer> getAllItemProductManufacturers() {
		Set<Manufacturer> manufacturers = new HashSet<>();
		for (SalesRequisitionItem item : items) {
			if (item.getProduct().getManufacturer() != null) {
				manufacturers.add(item.getProduct().getManufacturer());
			}
		}
		return ListUtil.asSortedList(new ArrayList<>(manufacturers));
	}

	public BigDecimal getSalesByManufacturer(Manufacturer manufacturer) {
		BigDecimal total = Constants.ZERO;
		for (SalesRequisitionItem item : items) {
			if (manufacturer.equals(item.getProduct().getManufacturer())) {
				total = total.add(item.getAmount());
			}
		}
		return total;
	}

	public SalesRequisitionItem findItemByProductAndUnit(Product product, String unit) {
		for (SalesRequisitionItem item : items) {
			if (product.getId().equals(item.getProduct().getId()) && unit.equals(item.getUnit())) {
				return item;
			}
		}
		return null;
	}

	public StockQuantityConversion getStockQuantityConversion() {
		return stockQuantityConversion;
	}

	public void setStockQuantityConversion(StockQuantityConversion stockQuantityConversion) {
		this.stockQuantityConversion = stockQuantityConversion;
	}
	
	public SalesRequisition extractToNewSalesRequisition(SalesRequisitionSeparateItemsList whitelist) {
		SalesRequisition newSalesRequisition = new SalesRequisition();
		newSalesRequisition.setCustomer(customer);
		newSalesRequisition.setTransactionDate(transactionDate);
		newSalesRequisition.setPricingScheme(pricingScheme);
		newSalesRequisition.setPaymentTerm(paymentTerm);
		newSalesRequisition.setMode(mode);
		newSalesRequisition.setRemarks(remarks);
		newSalesRequisition.setCreateDate(createDate);
		newSalesRequisition.setEncoder(encoder);
		
		Iterator<SalesRequisitionItem> iterator = items.iterator();
		while (iterator.hasNext()) {
			SalesRequisitionItem item = iterator.next();
			if (whitelist.isIncluded(item)) {
				newSalesRequisition.getItems().add(item);
				iterator.remove();
			}
		}
		
		return newSalesRequisition;
	}
	
}