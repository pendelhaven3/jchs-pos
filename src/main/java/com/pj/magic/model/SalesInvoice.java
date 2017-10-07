package com.pj.magic.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;

import com.pj.magic.Constants;
import com.pj.magic.util.ListUtil;

public class SalesInvoice implements Serializable {

    private static final long serialVersionUID = -6766882154444060022L;
    
    private Long id;
	private Long salesInvoiceNumber;
	private Customer customer;
	private Date createDate;
	private Date transactionDate;
	private User encoder;
	private Date postDate;
	private User postedBy;
	private List<SalesInvoiceItem> items = new ArrayList<>();
	private String mode;
	private String remarks;
	private PricingScheme pricingScheme;
	private Long relatedSalesRequisitionNumber;
	private PaymentTerm paymentTerm;
	private Date cancelDate;
	private User cancelledBy;
	private boolean cancelled;
	private boolean marked;
	private Date markDate;
	private User markedBy;
	private BigDecimal vatAmount;
	private boolean printed;
	
	private List<SalesReturn> salesReturns = new ArrayList<>(); // transient
	private List<NoMoreStockAdjustment> noMoreStockAdjustments = new ArrayList<>(); // transient

	public SalesInvoice() {
		// default constructor
	}
	
	public SalesInvoice(long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSalesInvoiceNumber() {
		return salesInvoiceNumber;
	}

	public void setSalesInvoiceNumber(Long salesInvoiceNumber) {
		this.salesInvoiceNumber = salesInvoiceNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public User getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(User postedBy) {
		this.postedBy = postedBy;
	}

	public List<SalesInvoiceItem> getItems() {
		return items;
	}

	public void setItems(List<SalesInvoiceItem> items) {
		this.items = items;
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = BigDecimal.ZERO;
		for (SalesInvoiceItem item : items) {
			total = total.add(item.getAmount());
		}
		return total;
	}

	public int getTotalNumberOfItems() {
		return items.size();
	}
	
	public boolean hasItems() {
		return !items.isEmpty();
	}

	public Long getRelatedSalesRequisitionNumber() {
		return relatedSalesRequisitionNumber;
	}

	public void setRelatedSalesRequisitionNumber(Long relatedSalesRequisitionNumber) {
		this.relatedSalesRequisitionNumber = relatedSalesRequisitionNumber;
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

	public PricingScheme getPricingScheme() {
		return pricingScheme;
	}

	public void setPricingScheme(PricingScheme pricingScheme) {
		this.pricingScheme = pricingScheme;
	}

	public int getTotalQuantity() {
		int total = 0;
		for (SalesInvoiceItem item : items) {
			total += item.getQuantity();
		}
		return total;
	}
	
	public BigDecimal getTotalDiscounts() {
		BigDecimal totalDiscountedAmount = Constants.ZERO;
		for (SalesInvoiceItem item : items) {
			totalDiscountedAmount = totalDiscountedAmount.add(item.getDiscountedAmount());
		}
		return totalDiscountedAmount.setScale(2, RoundingMode.HALF_UP);
	}

	public BigDecimal getTotalNetAmount() {
		BigDecimal totalNetAmount = BigDecimal.ZERO;
		for (SalesInvoiceItem item : items) {
			totalNetAmount = totalNetAmount.add(item.getNetAmount());
		}
		return totalNetAmount.setScale(2, RoundingMode.HALF_UP);
	}

	public PaymentTerm getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(PaymentTerm paymentTerm) {
		this.paymentTerm = paymentTerm;
	}
	
	public SalesRequisition createSalesRequisition() {
		SalesRequisition salesRequisition = new SalesRequisition();
		salesRequisition.setCustomer(customer);
		salesRequisition.setPaymentTerm(paymentTerm);
		salesRequisition.setPricingScheme(pricingScheme);
		salesRequisition.setMode(mode);
		salesRequisition.setRemarks(remarks);
		
		for (SalesInvoiceItem invoiceItem : items) {
			SalesRequisitionItem item = new SalesRequisitionItem();
			item.setParent(salesRequisition);
			item.setProduct(invoiceItem.getProduct());
			item.setUnit(invoiceItem.getUnit());
			item.setQuantity(invoiceItem.getQuantity());
			salesRequisition.getItems().add(item);
		}
		
		return salesRequisition;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public User getEncoder() {
		return encoder;
	}

	public void setEncoder(User encoder) {
		this.encoder = encoder;
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public User getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(User cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public String getStatus() {
		if (marked) {
			return "Marked";
		} else if (cancelled) {
			return "Cancelled";
		} else {
			return "New";
		}
	}

	public Date getMarkDate() {
		return markDate;
	}

	public void setMarkDate(Date markDate) {
		this.markDate = markDate;
	}

	public User getMarkedBy() {
		return markedBy;
	}

	public void setMarkedBy(User markedBy) {
		this.markedBy = markedBy;
	}

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public boolean isNew() {
		return !(marked || cancelled);
	}

	public BigDecimal getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(BigDecimal vatAmount) {
		this.vatAmount = vatAmount;
	}
	
	public BigDecimal getVatableSales() {
		return getTotalNetAmount().subtract(vatAmount);
	}
	
	public Date getDueDate() {
		return DateUtils.addDays(transactionDate, paymentTerm.getNumberOfDays());
	}
	
	public BigDecimal getTotalNetCost() {
		BigDecimal total = Constants.ZERO;
		for (SalesInvoiceItem item : items) {
			total = total.add(item.getNetCost());
		}
		return total;
	}
	
	public BigDecimal getTotalNetProfit() {
		return getTotalNetAmount().subtract(getTotalNetCost());
	}

	public boolean hasProduct(Product product) {
		for (SalesInvoiceItem item : items) {
			if (product.equals(item.getProduct())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasProductAndUnit(Product product, String unit) {
		return findItemByProductAndUnit(product, unit) != null;
	}
	
	public SalesInvoiceItem findItemByProductAndUnit(Product product, String unit) {
		for (SalesInvoiceItem item : items) {
			if (product.equals(item.getProduct()) && unit.equals(item.getUnit())) {
				return item;
			}
		}
		return null;
	}

	public BigDecimal getSalesByManufacturer(Manufacturer manufacturer) {
		BigDecimal total = Constants.ZERO;
		for (SalesInvoiceItem item : items) {
			if (manufacturer.equals(item.getProduct().getManufacturer())) {
				total = total.add(item.getNetAmount());
			}
		}
		return total;
	}

	public List<Manufacturer> getAllItemProductManufacturers() {
		Set<Manufacturer> manufacturers = new HashSet<>();
		for (SalesInvoiceItem item : items) {
			if (item.getProduct().getManufacturer() != null) {
				manufacturers.add(item.getProduct().getManufacturer());
			}
		}
		return ListUtil.asSortedList(new ArrayList<>(manufacturers));
	}

	public SalesInvoiceItem findItemByProduct(Product product) {
		for (SalesInvoiceItem item : items) {
			if (product.equals(item.getProduct())) {
				return item;
			}
		}
		return null;
	}

	public List<SalesReturn> getSalesReturns() {
		return salesReturns;
	}

	public void setSalesReturns(List<SalesReturn> salesReturns) {
		this.salesReturns = salesReturns;
	}

	public List<NoMoreStockAdjustment> getNoMoreStockAdjustments() {
		return noMoreStockAdjustments;
	}

	public void setNoMoreStockAdjustments(List<NoMoreStockAdjustment> noMoreStockAdjustments) {
		this.noMoreStockAdjustments = noMoreStockAdjustments;
	}

	public boolean isPrinted() {
		return printed;
	}

	public void setPrinted(boolean printed) {
		this.printed = printed;
	}
	
}