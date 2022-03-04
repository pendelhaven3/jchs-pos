create table AREA (
  ID integer auto_increment,
  NAME varchar(50) not null,
  constraint AREA$PK primary key (ID),
  constraint AREA$UK unique (NAME)
);

alter table AREA_INV_REPORT drop column AREA;
alter table AREA_INV_REPORT add AREA_ID integer null;
alter table AREA_INV_REPORT add constraint AREA_INV_REPORT$FK2 foreign key (AREA_ID) references AREA (ID);
alter table AREA_INV_REPORT add constraint AREA_INV_REPORT$FK3 foreign key (CREATE_BY) references USER (ID);

drop table INVENTORY_CHECK_SUMMARY_ITEM;

create table INVENTORY_CHECK_SUMMARY_ITEM (
  ID integer auto_increment,
  INVENTORY_CHECK_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(4) not null,
  BEGINNING_INV integer(6) not null,
  ACTUAL_COUNT integer(6) not null,
  COST numeric(10, 2) not null,
  constraint INVENTORY_CHECK_SUMMARY_ITEM$PK primary key (ID),
  constraint INVENTORY_CHECK_SUMMARY_ITEM$FK foreign key (INVENTORY_CHECK_ID) references INVENTORY_CHECK (ID),
  constraint INVENTORY_CHECK_SUMMARY_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT2 (ID)
);
