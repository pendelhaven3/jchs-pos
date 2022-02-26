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

-- SUPPLIER_PRODUCT

alter table SUPPLIER_PRODUCT drop foreign key SUPPLIER_PRODUCT$FK2;

update SUPPLIER_PRODUCT a
set a.PRODUCT_ID = (select b.PRODUCT2_ID from PRODUCT b where b.ID = a.PRODUCT_ID) 

alter table SUPPLIER_PRODUCT add constraint SUPPLIER_PRODUCT$FK2 foreign key (PRODUCT_ID) references PRODUCT2 (ID);

-- Purchase Order migration
-- Receiving Receipt migration

alter table PURCHASE_ORDER_ITEM drop foreign key PURCHASE_ORDER_ITEM$FK2;
alter table RECEIVING_RECEIPT_ITEM drop foreign key RECEIVING_RECEIPT_ITEM$FK2;

-- TODO: update scripts

alter table PURCHASE_ORDER_ITEM add constraint PURCHASE_ORDER_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT2 (ID);
alter table RECEIVING_RECEIPT_ITEM add constraint RECEIVING_RECEIPT_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT2 (ID);

--

alter table PRODUCT drop column AVAIL_QTY;

--

insert into SEQUENCE (NAME) values ('ADJUSTMENT_OUT_NO_SEQ');
insert into SEQUENCE (NAME) values ('ADJUSTMENT_IN_NO_SEQ');

create table ADJUSTMENT_IN (
  ID integer auto_increment,
  ADJUSTMENT_IN_NO integer not null,
  POST_IND char(1) default 'N' not null,
  REMARKS varchar(100) null,
  POST_DT datetime null,
  POSTED_BY integer null,
  PILFERAGE_IND char(1) default 'N' not null,
  constraint ADJUSTMENT_IN$PK primary key (ID),
  constraint ADJUSTMENT_IN$UK unique (ADJUSTMENT_IN_NO),
  constraint ADJUSTMENT_IN$FK foreign key (POSTED_BY) references USER (ID)
);

create table ADJUSTMENT_IN_ITEM (
  ID integer auto_increment,
  ADJUSTMENT_IN_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(4) not null,
  QUANTITY integer not null,
  COST numeric(10, 2) null,
  constraint ADJUSTMENT_IN_ITEM$PK primary key (ID),
  constraint ADJUSTMENT_IN_ITEM$FK foreign key (ADJUSTMENT_IN_ID) references ADJUSTMENT_IN (ID),
  constraint ADJUSTMENT_IN_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT2 (ID)
);

create table ADJUSTMENT_OUT (
  ID integer auto_increment,
  ADJUSTMENT_OUT_NO integer not null,
  POST_IND char(1) default 'N' not null,
  REMARKS varchar(100) null,
  POST_DT datetime null,
  POSTED_BY integer null,
  PILFERAGE_IND char(1) default 'N' not null,
  constraint ADJUSTMENT_OUT$PK primary key (ID),
  constraint ADJUSTMENT_OUT$UK unique (ADJUSTMENT_OUT_NO),
  constraint ADJUSTMENT_OUT$FK foreign key (POSTED_BY) references USER (ID)
);

create table ADJUSTMENT_OUT_ITEM (
  ID integer auto_increment,
  ADJUSTMENT_OUT_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(4) not null,
  QUANTITY integer not null,
  UNIT_PRICE numeric(10, 2) null,
  constraint ADJUSTMENT_OUT_ITEM$PK primary key (ID),
  constraint ADJUSTMENT_OUT_ITEM$FK foreign key (ADJUSTMENT_OUT_ID) references ADJUSTMENT_OUT (ID),
  constraint ADJUSTMENT_OUT_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT2 (ID)
);
