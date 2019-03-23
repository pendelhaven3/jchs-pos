package com.pj.magic.model;

import static java.util.Calendar.APRIL;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DECEMBER;
import static java.util.Calendar.JANUARY;
import static java.util.Calendar.JULY;
import static java.util.Calendar.JUNE;
import static java.util.Calendar.MARCH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.OCTOBER;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.apache.commons.lang.time.DateUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BirForm2307Report {

    @Id
    @GeneratedValue
    private Long id;
    
    @Column(name = "REPORT_NO")
    private Long reportNumber;
    
    @OneToOne
    private Supplier supplier;
    
    private Date fromDate;
    private Date toDate;
    private BigDecimal month1NetAmount;
    private BigDecimal month2NetAmount;
    private BigDecimal month3NetAmount;
    private Date createDate;
    private User createdBy;
    
    public boolean isNew() {
        return id == null;
    }
    
    public Date getForm2307FromDate() {
        Calendar cal = DateUtils.toCalendar(fromDate);
        int month = cal.get(MONTH);
        
        if (month < APRIL) {
            cal.set(MONTH, JANUARY);
        } else if (month < JULY) {
            cal.set(MONTH, APRIL);
        } else if (month < OCTOBER) {
            cal.set(MONTH, JULY);
        } else {
            cal.set(MONTH, OCTOBER);
        }
        
        cal.set(DATE, 1);
        
        return cal.getTime();
    }
    
    public Date getForm2307ToDate() {
        Calendar cal = DateUtils.toCalendar(fromDate);
        int month = cal.get(MONTH);
        
        if (month < APRIL) {
            cal.set(MONTH, MARCH);
            cal.set(DATE, 31);
        } else if (month < JULY) {
            cal.set(MONTH, JUNE);
            cal.set(DATE, 30);
        } else if (month < OCTOBER) {
            cal.set(MONTH, JULY);
            cal.set(DATE, 31);
        } else {
            cal.set(MONTH, DECEMBER);
            cal.set(DATE, 31);
        }
        
        return cal.getTime();
    }

    public BigDecimal getTotalEwtAmount() {
        return month1NetAmount.add(month2NetAmount).add(month3NetAmount).multiply(new BigDecimal("0.01")).setScale(2, RoundingMode.HALF_UP);
    }
    
}
