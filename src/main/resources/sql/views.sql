create view DAILY_PRODUCT_MOVEMENT as
select b.PRODUCT_ID, b.UNIT, b.QUANTITY
from SALES_INVOICE a
join SALES_INVOICE_ITEM b
	on b.SALES_INVOICE_ID = a.ID
where a.POST_DT >= date_add(current_date(), interval -1 day)
and a.POST_DT < current_date()
union all
select b.PRODUCT_ID, b.UNIT, -1 * QUANTITY as QUANTITY
from SALES_INVOICE a
join SALES_INVOICE_ITEM b
	on b.SALES_INVOICE_ID = a.ID
where a.CANCEL_IND = 'Y'
and a.POST_DT >= date_add(current_date(), interval -1 day)
and a.POST_DT < current_date()
union all
select b.PRODUCT_ID, b.UNIT, -1 * b.QUANTITY as QUANTITY
from RECEIVING_RECEIPT a
join RECEIVING_RECEIPT_ITEM b
	on b.RECEIVING_RECEIPT_ID = a.ID
where a.POST_IND = 'Y'
and a.POST_DT >= date_add(current_date(), interval -1 day)
and a.POST_DT < current_date()
union all
select b.PRODUCT_ID, b.UNIT, b.QUANTITY
from ADJUSTMENT_OUT a
join ADJUSTMENT_OUT_ITEM b
	on b.ADJUSTMENT_OUT_ID = a.ID
where a.POST_IND = 'Y'
and a.POST_DT >= date_add(current_date(), interval -1 day)
and a.POST_DT < current_date()
union all
select b.PRODUCT_ID, b.UNIT, -1 * b.QUANTITY as QUANTITY
from ADJUSTMENT_IN a
join ADJUSTMENT_IN_ITEM b
	on b.ADJUSTMENT_IN_ID = a.ID
where a.POST_IND = 'Y'
and a.POST_DT >= date_add(current_date(), interval -1 day)
and a.POST_DT < current_date()
union all
select b.PRODUCT_ID, b.FROM_UNIT as UNIT, b.QUANTITY
from STOCK_QTY_CONVERSION a
join STOCK_QTY_CONVERSION_ITEM b
	on b.STOCK_QTY_CONVERSION_ID = a.ID
where a.POST_IND = 'Y'
and a.POST_DT >= date_add(current_date(), interval -1 day)
and a.POST_DT < current_date()
union all
select b.PRODUCT_ID, b.TO_UNIT as UNIT, -1 * b.QUANTITY as QUANTITY
from STOCK_QTY_CONVERSION a
join STOCK_QTY_CONVERSION_ITEM b
	on b.STOCK_QTY_CONVERSION_ID = a.ID
where a.POST_IND = 'Y'
and a.POST_DT >= date_add(current_date(), interval -1 day)
and a.POST_DT < current_date()
union all
select c.PRODUCT_ID, c.UNIT, -1 * b.QUANTITY as QUANTITY
from SALES_RETURN a
join SALES_RETURN_ITEM b
	on b.SALES_RETURN_ID = a.ID
join SALES_INVOICE_ITEM c
	on c.ID = b.SALES_INVOICE_ITEM_ID
where a.POST_IND = 'Y'
and a.POST_DT >= date_add(current_date(), interval -1 day)
and a.POST_DT < current_date()
union all
select c.PRODUCT_ID, c.UNIT, b.QUANTITY
from SALES_RETURN a
join SALES_RETURN_ITEM b
	on b.SALES_RETURN_ID = a.ID
join SALES_INVOICE_ITEM c
	on c.ID = b.SALES_INVOICE_ITEM_ID
where a.POST_IND = 'Y'
and a.CANCEL_IND = 'Y'
and a.POST_DT >= date_add(current_date(), interval -1 day)
and a.POST_DT < current_date()
union all
select a.PRODUCT_ID, a.UNIT, a.QUANTITY
from PROMO_REDEMPTION_REWARD a
join PROMO_REDEMPTION b
	on b.ID = a.PROMO_REDEMPTION_ID
where b.POST_IND = 'Y'
and b.POST_DT >= date_add(current_date(), interval -1 day)
and b.POST_DT < current_date()
union all
select c.PRODUCT_ID, c.UNIT, b.QUANTITY
from PURCHASE_RETURN a
join PURCHASE_RETURN_ITEM b
	on b.PURCHASE_RETURN_ID = a.ID
join RECEIVING_RECEIPT_ITEM c
	on c.ID = b.RECEIVING_RECEIPT_ITEM_ID
where a.POST_IND = 'Y'
and a.POST_DT >= date_add(current_date(), interval -1 day)
and a.POST_DT < current_date();
