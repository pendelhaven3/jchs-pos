package com.pj.magic.model.search;

import java.util.Date;


public class AdjustmentOutSearchCriteria {

	private Long adjustmentOutNumber;
	private Boolean posted;
	private Date postDate;

	public Boolean getPosted() {
		return posted;
	}

	public void setPosted(Boolean posted) {
		this.posted = posted;
	}

	public Long getAdjustmentOutNumber() {
		return adjustmentOutNumber;
	}

	public void setAdjustmentOutNumber(Long adjustmentOutNumber) {
		this.adjustmentOutNumber = adjustmentOutNumber;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

}