package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Supplier;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EwtReportCriteria {

	private Supplier supplier;
	private Date fromDate;
	private Date toDate;

}
