package com.pj.magic.exception;

import com.pj.magic.model.AdjustmentOutItem;
import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.model.StockQuantityConversionItem;

import lombok.Getter;

@Getter
public class NotEnoughStocksException extends RuntimeException {

	private SalesRequisitionItem salesRequisitionItem;
	private StockQuantityConversionItem stockQuantityConversionItem;
	private AdjustmentOutItem adjustmentOutItem;
	private String productCode;

	public NotEnoughStocksException() {
		
	}
	
	public NotEnoughStocksException(String productCode) {
		this.productCode = productCode;
	}
	
	public NotEnoughStocksException(SalesRequisitionItem item) {
		this.salesRequisitionItem = item;
	}

	public NotEnoughStocksException(StockQuantityConversionItem item) {
		this.stockQuantityConversionItem = item;
	}
	
	public NotEnoughStocksException(AdjustmentOutItem adjustmentOutItem) {
		this.adjustmentOutItem = adjustmentOutItem;
	}

}
