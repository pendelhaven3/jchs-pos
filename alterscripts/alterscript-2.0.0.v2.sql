create table PRODUCT2 (
  ID integer auto_increment,
  DESCRIPTION varchar(50) not null,
  MAX_STOCK_LEVEL integer(4) default 0,
  MIN_STOCK_LEVEL integer(4) default 0,
  ACTIVE_IND char(1) default 'Y',
  UNIT_IND_CASE char(1) null,
  UNIT_IND_TIES char(1) null,
  UNIT_IND_PACK char(1) null,
  UNIT_IND_HDZN char(1) null,
  UNIT_IND_PCS char(1) null,
  ACTIVE_UNIT_IND_CASE char(1) default 'N' not null,
  ACTIVE_UNIT_IND_TIES char(1) default 'N' not null,
  ACTIVE_UNIT_IND_PACK char(1) default 'N' not null,
  ACTIVE_UNIT_IND_HDZN char(1) default 'N' not null,
  ACTIVE_UNIT_IND_PCS char(1) default 'N' not null,
  AVAIL_QTY_CASE integer(4) default 0 not null,
  AVAIL_QTY_TIES integer(4) default 0 not null,
  AVAIL_QTY_PACK integer(4) default 0 not null,
  AVAIL_QTY_HDZN integer(4) default 0 not null,
  AVAIL_QTY_PCS integer(4) default 0 not null,
  UNIT_CONV_CASE integer(5) null,
  UNIT_CONV_TIES integer(5) null,
  UNIT_CONV_PACK integer(5) null,
  UNIT_CONV_HDZN integer(5) null,
  UNIT_CONV_PCS integer(5) null,
  GROSS_COST_CASE numeric(10, 2) default 0 not null,
  GROSS_COST_TIES numeric(10, 2) default 0 not null,
  GROSS_COST_PACK numeric(10, 2) default 0 not null,
  GROSS_COST_HDZN numeric(10, 2) default 0 not null,
  GROSS_COST_PCS numeric(10, 2) default 0 not null,
  FINAL_COST_CASE numeric(10, 2) default 0 not null,
  FINAL_COST_TIES numeric(10, 2) default 0 not null,
  FINAL_COST_PACK numeric(10, 2) default 0 not null,
  FINAL_COST_HDZN numeric(10, 2) default 0 not null,
  FINAL_COST_PCS numeric(10, 2) default 0 not null,
  constraint PRODUCT2$PK primary key (ID)
);

alter table PRODUCT add PRODUCT2_ID integer null;
alter table PRODUCT add constraint PRODUCT$FK foreign key (PRODUCT2_ID) references PRODUCT2 (ID);

alter table SUPPLIER_PRODUCT drop foreign key SUPPLIER_PRODUCT$FK2;

update SUPPLIER_PRODUCT a
set a.PRODUCT_ID = (select b.PRODUCT2_ID from PRODUCT b where b.ID = a.PRODUCT_ID) 

alter table SUPPLIER_PRODUCT add constraint SUPPLIER_PRODUCT$FK2 foreign key (PRODUCT_ID) references PRODUCT2 (ID);

-- Purchase Order migration
-- Receiving Receipt migration

alter table PURCHASE_ORDER_ITEM drop foreign key PURCHASE_ORDER_ITEM$FK2;
alter table RECEIVING_RECEIPT_ITEM drop foreign key RECEIVING_RECEIPT_ITEM$FK2;

alter table PURCHASE_ORDER_ITEM add constraint PURCHASE_ORDER_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT2 (ID);
