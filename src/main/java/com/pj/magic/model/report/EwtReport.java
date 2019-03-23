package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import com.pj.magic.model.BirForm2307Report;
import com.pj.magic.model.Supplier;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EwtReport {

    private Supplier supplier;
    private List<EwtReportItem> items;
    private Date fromDate;
    private Date toDate;

    public BirForm2307Report toForm2307Report() {
        BirForm2307Report report = new BirForm2307Report();
        report.setSupplier(supplier);
        report.setFromDate(fromDate);
        report.setToDate(toDate);
        
        BigDecimal month1Total = BigDecimal.ZERO;
        BigDecimal month2Total = BigDecimal.ZERO;
        BigDecimal month3Total = BigDecimal.ZERO;
        for (EwtReportItem item : items) {
            switch (getQuarterPosition(item.getReceivingReceipt().getReceivedDate())) {
            case 1:
                month1Total = month1Total.add(item.getInvoiceAmount());
                break;
            case 2:
                month2Total = month2Total.add(item.getInvoiceAmount());
                break;
            case 3:
                month3Total = month3Total.add(item.getInvoiceAmount());
                break;
            }
        }
        
        report.setMonth1NetAmount(month1Total);
        report.setMonth2NetAmount(month2Total);
        report.setMonth3NetAmount(month3Total);
        
        return report;
    }
    
    private int getQuarterPosition(Date date) {
        Calendar calendar = DateUtils.toCalendar(date);
        switch (calendar.get(Calendar.MONTH)) {
        case 0:
        case 3:
        case 6:
        case 9:
            return 1;
        case 1:
        case 4:
        case 7:
        case 10:
            return 2;
        default:
            return 3;
        }
    }
    
}
