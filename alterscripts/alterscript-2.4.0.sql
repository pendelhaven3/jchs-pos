drop table PRODUCT_CUSTOM_CODE;

create table PRODUCT_CUSTOM_CODE (
  ID integer auto_increment,
  PRODUCT_ID integer not null,
  CODE varchar(30) not null,
  SUPPLIER_ID integer not null,
  constraint PRODUCT_CUSTOM_CODE$PK primary key (ID),
  constraint PRODUCT_CUSTOM_CODE$UK unique (PRODUCT_ID, SUPPLIER_ID),
  constraint PRODUCT_CUSTOM_CODE$FK foreign key (PRODUCT_ID) references PRODUCT2 (ID),
  constraint PRODUCT_CUSTOM_CODE$FK2 foreign key (SUPPLIER_ID) references SUPPLIER (ID)
);
