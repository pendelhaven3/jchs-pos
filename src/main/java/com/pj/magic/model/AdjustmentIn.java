package com.pj.magic.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class AdjustmentIn {

	private Long id;
	private Long adjustmentInNumber;
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private List<AdjustmentInItem> items = new ArrayList<>();
	private String remarks;
	private boolean pilferageFlag = true;

	public AdjustmentIn() {
	}
	
	public AdjustmentIn(long id) {
		this.id = id;
	}
	
	public BigDecimal getTotalAmount() {
		BigDecimal total = BigDecimal.ZERO;
		for (AdjustmentInItem item : items) {
			total = total.add(item.getAmount());
		}
		return total.setScale(2, RoundingMode.HALF_UP);
	}
	
	public boolean hasItems() {
		return !items.isEmpty();
	}
	
	public int getTotalItems() {
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
        if (!(obj instanceof AdjustmentIn)) {
            return false;
        }
        AdjustmentIn other = (AdjustmentIn)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getAdjustmentInNumber() {
		return adjustmentInNumber;
	}

	public void setAdjustmentInNumber(Long adjustmentInNumber) {
		this.adjustmentInNumber = adjustmentInNumber;
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

	public List<AdjustmentInItem> getItems() {
		return items;
	}

	public void setItems(List<AdjustmentInItem> items) {
		this.items = items;
	}

	public String getStatus() {
		return posted ? "Posted" : "New";
	}

	public boolean getPilferageFlag() {
		return pilferageFlag;
	}

	public void setPilferageFlag(boolean pilferageFlag) {
		this.pilferageFlag = pilferageFlag;
	}
	
}
