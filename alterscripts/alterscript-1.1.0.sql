insert into SEQUENCE (NAME) values ('PURCHASE_RETURN_BAD_STOCK_NO_SEQ');

alter table PRODUCT add GROSS_COST numeric(9,2) not null;
alter table PRODUCT add GROSS_COST1 numeric(9,2) null;
alter table PRODUCT add FINAL_COST numeric(9,2) not null;
alter table PRODUCT add FINAL_COST1 numeric(9,2) null;

create table PURCHASE_RETURN_BAD_STOCK (
  ID integer auto_increment,
  PURCHASE_RETURN_BAD_STOCK_NO integer not null,
  SUPPLIER_ID integer not null,
  POST_IND char(1) default 'N' not null,
  POST_DT date null,
  POST_BY integer null,
  REMARKS varchar(100) null,
  constraint PURCHASE_RETURN_BAD_STOCK$PK primary key (ID),
  constraint PURCHASE_RETURN_BAD_STOCK$UK unique (PURCHASE_RETURN_BAD_STOCK_NO),
  constraint PURCHASE_RETURN_BAD_STOCK$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID)
);

create table PURCHASE_RETURN_BAD_STOCK_ITEM (
  ID integer auto_increment,
  PURCHASE_RETURN_BAD_STOCK_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer not null,
  UNIT_COST numeric(10, 2) not null,
  constraint PURCHASE_RETURN_BAD_STOCK_ITEM$PK primary key (ID),
  constraint PURCHASE_RETURN_BAD_STOCK_ITEM$FK foreign key (PURCHASE_RETURN_BAD_STOCK_ID) references PURCHASE_RETURN_BAD_STOCK (ID),
  constraint PURCHASE_RETURN_BAD_STOCK_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);
