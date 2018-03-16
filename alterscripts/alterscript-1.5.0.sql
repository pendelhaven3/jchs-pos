alter table PRODUCT add MAX_STOCK_LEVEL integer(4) default 0;
alter table PRODUCT add MIN_STOCK_LEVEL integer(4) default 0;

create table SUPPLIER_PRODUCT (
  SUPPLIER_ID integer not null,
  PRODUCT_ID integer not null,
  constraint SUPPLIER_PRODUCT$PK primary key (SUPPLIER_ID, PRODUCT_ID),
  constraint SUPPLIER_PRODUCT$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID),
  constraint SUPPLIER_PRODUCT$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);
 