package com.pj.magic.model.search;

import java.util.Date;


public class AdjustmentInSearchCriteria {

	private Long adjustmentInNumber;
	private Boolean posted;
	private Date postDate;

	public Boolean getPosted() {
		return posted;
	}

	public void setPosted(Boolean posted) {
		this.posted = posted;
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

}