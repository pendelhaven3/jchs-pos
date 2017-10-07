package com.pj.magic.model.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import com.pj.magic.model.ProductPriceHistory;

public class PriceChangesReport {

	private static Comparator<ProductPriceHistory> itemsComparator = new Comparator<ProductPriceHistory>() {

		@Override
		public int compare(ProductPriceHistory o1, ProductPriceHistory o2) {
			return o1.getProduct().getDescription().compareTo(o2.getProduct().getDescription());
		}
	};
	
	private Date fromDate;
	private Date toDate;
	private List<ProductPriceHistory> items;

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public List<ProductPriceHistory> getItems() {
		return items;
	}

	public void setItems(List<ProductPriceHistory> items) {
		this.items = items;
	}

	public List<PriceChangesReportItemGroup> getItemsGroupedByDate() {
		Map<Date, PriceChangesReportItemGroup> groupsMap = new HashMap<>();
		for (ProductPriceHistory item : items) {
			Date key = DateUtils.truncate(item.getUpdateDate(), Calendar.DATE);
			PriceChangesReportItemGroup group = groupsMap.get(key);
			if (group == null) {
				group = new PriceChangesReportItemGroup();
				group.setDate(key);
				groupsMap.put(key, group);
			}
			group.getItems().add(item);
		}
		
		List<PriceChangesReportItemGroup> groups = new ArrayList<PriceChangesReportItemGroup>(groupsMap.values());
		Collections.sort(groups);
		
		for (PriceChangesReportItemGroup group : groups) {
			Collections.sort(group.getItems(), itemsComparator);
		}
		
		return groups;
	}
	
}