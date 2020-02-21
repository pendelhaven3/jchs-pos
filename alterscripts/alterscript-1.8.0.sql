create table INVENTORY_CHECK (
  ID integer auto_increment,
  INVENTORY_DT date not null,
  POST_IND char(1) default 'N' not null,
  POST_DT datetime null,
  constraint INVENTORY_CHECK$PK primary key (ID),
  constraint INVENTORY_CHECK$UK unique (INVENTORY_DT)
);

create table AREA_INV_REPORT (
  ID integer auto_increment,
  INVENTORY_CHECK_ID integer not null,
  REPORT_NO integer not null,
  AREA varchar(100) not null,
  CHECKER varchar(50) null,
  DOUBLE_CHECKER varchar(50) null,
  CREATE_BY integer null,
  REVIEW_IND char(1) default 'N' not null,
  REVIEWER varchar(50) null,
  constraint AREA_INV_REPORT$PK primary key (ID),
  constraint AREA_INV_REPORT$UK unique (INVENTORY_CHECK_ID, REPORT_NO),
  constraint AREA_INV_REPORT$FK foreign key (INVENTORY_CHECK_ID) references INVENTORY_CHECK (ID)
);

create table AREA_INV_REPORT_ITEM (
  ID integer auto_increment,
  AREA_INV_REPORT_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(4) not null,
  QUANTITY integer not null,
  constraint AREA_INV_REPORT_ITEM$PK primary key (ID),
  constraint AREA_INV_REPORT_ITEM$FK foreign key (AREA_INV_REPORT_ID) references AREA_INV_REPORT (ID),
  constraint AREA_INV_REPORT_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table INVENTORY_CHECK_SUMMARY_ITEM (
  ID integer auto_increment,
  INVENTORY_CHECK_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(4) not null,
  QUANTITY integer(6) not null,
  COST numeric(10, 2) not null,
  constraint INVENTORY_CHECK_SUMMARY_ITEM$PK primary key (ID),
  constraint INVENTORY_CHECK_SUMMARY_ITEM$FK foreign key (INVENTORY_CHECK_ID) references INVENTORY_CHECK (ID),
  constraint INVENTORY_CHECK_SUMMARY_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);
