package com.pj.magic.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StockQuantityConversion implements Serializable {

    private static final long serialVersionUID = -1696900768471273923L;
    
    private Long id;
	private Long stockQuantityConversionNumber;
	private String remarks;
	private List<StockQuantityConversionItem> items = new ArrayList<>();
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private boolean printed;

	public StockQuantityConversion() {
		// default constructor
	}
	
	public StockQuantityConversion(long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStockQuantityConversionNumber() {
		return stockQuantityConversionNumber;
	}

	public void setStockQuantityConversionNumber(Long stockQuantityConversionNumber) {
		this.stockQuantityConversionNumber = stockQuantityConversionNumber;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<StockQuantityConversionItem> getItems() {
		return items;
	}

	public void setItems(List<StockQuantityConversionItem> items) {
		this.items = items;
	}

	public int getTotalNumberOfItems() {
		return items.size();
	}

	public boolean hasItems() {
		return !items.isEmpty();
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public String getStatus() {
		return posted ? "Yes" : "No";
	}

	public User getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(User postedBy) {
		this.postedBy = postedBy;
	}

	public boolean isPrinted() {
		return printed;
	}

	public void setPrinted(boolean printed) {
		this.printed = printed;
	}

}