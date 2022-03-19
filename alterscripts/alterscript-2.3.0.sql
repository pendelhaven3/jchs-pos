alter table PURCHASE_RETURN add POST_BY integer null;
alter table PURCHASE_RETURN add PAID_BY integer null;

alter table PURCHASE_RETURN add constraint PURCHASE_RETURN$FK2 foreign key (POST_BY) references USER (ID);
alter table PURCHASE_RETURN add constraint PURCHASE_RETURN$FK3 foreign key (PAID_BY) references USER (ID);

alter table PURCHASE_RETURN_BAD_STOCK_ITEM drop foreign key PURCHASE_RETURN_BAD_STOCK_ITEM$FK2;
alter table PURCHASE_RETURN_BAD_STOCK_ITEM add constraint PURCHASE_RETURN_BAD_STOCK_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT2 (ID);

alter table PURCHASE_RETURN_BAD_STOCK add PICKUP_DT date null;
