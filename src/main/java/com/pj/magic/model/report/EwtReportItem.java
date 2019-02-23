package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.ReceivingReceipt;

import lombok.Getter;
import lombok.Setter;

@Setter
public class EwtReportItem {

    @Getter
    private ReceivingReceipt receivingReceipt;
    
    @Getter
    private PurchasePayment purchasePayment;
    
    @Getter
    private BigDecimal badStockAndDiscountTotalAmount;
    
    @Getter
    private BigDecimal cancelledItemsTotalAmount;
    
    private BigDecimal invoiceAmount;
    
    public BigDecimal getInvoiceAmount() {
        if (invoiceAmount == null) {
            invoiceAmount = receivingReceipt.getTotalNetAmountWithVat().setScale(2, RoundingMode.HALF_UP);
        }
        return invoiceAmount;
    }
    
    public BigDecimal getGrossAmount() {
        return getInvoiceAmount().subtract(badStockAndDiscountTotalAmount).subtract(cancelledItemsTotalAmount);
    }
    
}
